package l2.gameserver.network.l2.c2s;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.EnchantItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.EnchantResult;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.item.support.EnchantScroll;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;

public class RequestEnchantItem extends L2GameClientPacket {
   private int _objectId;

   protected void readImpl() {
      this._objectId = this.readD();
   }

   protected void runImpl() {
      GameClient client = (GameClient)this.getClient();
      if (client != null) {
         Player player = client.getActiveChar();
         if (player != null) {
            if (player.isActionsDisabled()) {
               player.setEnchantScroll((ItemInstance)null);
               player.sendActionFailed();
            } else if (player.isInTrade()) {
               player.setEnchantScroll((ItemInstance)null);
               player.sendActionFailed();
            } else if (player.isInStoreMode()) {
               player.setEnchantScroll((ItemInstance)null);
               player.sendPacket(EnchantResult.CANCEL);
               player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
               player.sendActionFailed();
            } else {
               PcInventory inventory = player.getInventory();
               inventory.writeLock();

               try {
                  ItemInstance item = inventory.getItemByObjectId(this._objectId);
                  ItemInstance scroll = player.getEnchantScroll();
                  if (item == null || scroll == null) {
                     player.sendActionFailed();
                     return;
                  }

                  EnchantScroll enchantScroll = EnchantItemHolder.getInstance().getEnchantScroll(scroll.getItemId());
                  if (enchantScroll == null) {
                     player.sendActionFailed();
                     return;
                  }

                  if (!item.canBeEnchanted(false)) {
                     player.sendPacket(EnchantResult.CANCEL);
                     player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                     player.sendActionFailed();
                     return;
                  }

                  if (enchantScroll.isUsableWith(item)) {
                     double chanceMod = 1.0D + enchantScroll.getChanceMod();
                     int toLvl = item.getEnchantLevel() + enchantScroll.getIncrement();
                     chanceMod *= player.getEnchantBonusMul();
                     if (!inventory.destroyItem(scroll, 1L)) {
                        player.sendPacket(EnchantResult.CANCEL);
                        player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                        player.sendActionFailed();
                        return;
                     }

                     double chance = enchantScroll.getEnchantChance(item);
                     if (!enchantScroll.isInfallible() && !Rnd.chance(chance * chanceMod)) {
                        switch(enchantScroll.getOnFailAction()) {
                        case CRYSTALIZE:
                           this.onCrystallizeItem(player, item);
                           return;
                        case RESET:
                           this.onResetItem(player, item, enchantScroll.getFailResultLevel());
                           return;
                        case NONE:
                           this.onEnchantNone(player, item);
                           return;
                        default:
                           return;
                        }
                     }

                     if (enchantScroll.getIncrement() > 1 && toLvl > enchantScroll.getMaxLvl()) {
                        this.onEnchantSuccess(player, item, enchantScroll.getMaxLvl());
                        return;
                     }

                     this.onEnchantSuccess(player, item, toLvl);
                     return;
                  }

                  player.sendPacket(EnchantResult.CANCEL);
                  player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
                  player.sendActionFailed();
               } finally {
                  inventory.writeUnlock();
                  player.setEnchantScroll((ItemInstance)null);
                  player.updateStats();
               }

            }
         }
      }
   }

   private void onEnchantSuccess(Player player, ItemInstance item, int toLvl) {
      PcInventory inventory = player.getInventory();
      if (toLvl >= 65535) {
         player.sendPacket(new IStaticPacket[]{EnchantResult.CANCEL, SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL});
         player.sendActionFailed();
      } else {
         boolean equipped = item.isEquipped();
         int itemSlot = item.getBodyPart();
         if (equipped) {
            item.setEquipped(false);
            inventory.getListeners().onUnequip(itemSlot, item);
         }

         int enchantLevel = item.getEnchantLevel();

         try {
            item.setEnchantLevel(toLvl);
            Log.LogItem(player, ItemLog.EnchantSuccess, item);
         } finally {
            if (equipped) {
               inventory.getListeners().onEquip(itemSlot, item);
               item.setEquipped(true);
            }

            item.save();
         }

         player.sendPacket(new IStaticPacket[]{(new InventoryUpdate()).addModifiedItem(item), enchantLevel > 0 ? ((SystemMessage)(new SystemMessage(SystemMsg._S1_S2_HAS_BEEN_SUCCESSFULLY_ENCHANTED)).addNumber(enchantLevel)).addItemName(item.getItemId()) : (new SystemMessage(SystemMsg.S1_HAS_BEEN_SUCCESSFULLY_ENCHANTED)).addItemName(item.getItemId()), EnchantResult.SUCESS});
         player.getListeners().onEnchantItem(item, true);
         if (Config.SHOW_ENCHANT_EFFECT_RESULT) {
            broadcastResult(player, item);
         }

      }
   }

   private void onCrystallizeItem(Player player, ItemInstance item) {
      PcInventory inventory = player.getInventory();
      boolean equipped = item.isEquipped();
      int itemId = item.getItemId();
      int itemEnchantLevel = item.getEnchantLevel();
      int itemCrystalId = item.getCrystalItemId();
      int itemCrystalCount = item.getTemplate().getCrystalCount();
      ItemInstance enchantScroll = player.getEnchantScroll();
      if (equipped) {
         player.sendDisarmMessage(item);
         inventory.unEquipItem(item);
      }

      Log.LogItem(player, ItemLog.EnchantCrystallize, item, 1L, (long)item.getReferencePrice(), enchantScroll.getItemId());
      if (!inventory.destroyItem(item, 1L)) {
         player.sendActionFailed();
      } else {
         if (itemCrystalId > 0 && itemCrystalCount > 0) {
            int crystalAmount = (int)((double)itemCrystalCount * 0.87D);
            if (itemEnchantLevel > 3 && Config.CRYSTALLIZE_BONUS_AT_ENCHANT) {
               crystalAmount = (int)((double)crystalAmount + (double)itemCrystalCount * 0.25D * (double)(itemEnchantLevel - 3));
            }

            if (crystalAmount < 1) {
               crystalAmount = 1;
            }

            player.sendPacket(new IStaticPacket[]{new EnchantResult(1, itemCrystalId, (long)crystalAmount), ((SystemMessage)(new SystemMessage(SystemMsg.THE_ENCHANTMENT_HAS_FAILED__YOUR_S1_S2_HAS_BEEN_CRYSTALLIZED)).addNumber(itemEnchantLevel)).addItemName(itemId)});
            ItemFunctions.addItem(player, itemCrystalId, (long)crystalAmount, true);
         } else {
            player.sendPacket(new IStaticPacket[]{EnchantResult.FAILED_NO_CRYSTALS, (new SystemMessage(SystemMsg.THE_ENCHANTMENT_HAS_FAILED_YOUR_S1_HAS_BEEN_CRYSTALLIZED)).addItemName(item.getItemId())});
         }

         player.getListeners().onEnchantItem(item, false);
      }
   }

   private void onResetItem(Player player, ItemInstance item, int scrollResetLevel) {
      PcInventory inventory = player.getInventory();
      boolean equipped = item.isEquipped();
      int itemSlot = item.getBodyPart();
      int resetLvl = Math.min(item.getEnchantLevel(), scrollResetLevel);
      ItemInstance enchantScroll = player.getEnchantScroll();
      if (equipped) {
         item.setEquipped(false);
         inventory.getListeners().onUnequip(itemSlot, item);
      }

      try {
         item.setEnchantLevel(resetLvl);
         Log.LogItem(player, ItemLog.EnchantReset, item, 1L, (long)item.getReferencePrice(), enchantScroll.getItemId());
      } finally {
         if (equipped) {
            inventory.getListeners().onEquip(itemSlot, item);
            item.setEquipped(true);
         }

         item.save();
      }

      player.sendPacket(new IStaticPacket[]{(new InventoryUpdate()).addModifiedItem(item), EnchantResult.BLESSED_FAILED, SystemMsg.THE_BLESSED_ENCHANT_FAILED});
      player.getListeners().onEnchantItem(item, false);
   }

   private void onEnchantNone(Player player, ItemInstance item) {
      Log.LogItem(player, ItemLog.EnchantFail, item, 1L, (long)item.getReferencePrice(), player.getEnchantScroll().getItemId());
      player.sendPacket(new IStaticPacket[]{EnchantResult.CANCEL, SystemMsg.NOTHING_HAPPENED});
      player.getListeners().onEnchantItem(item, false);
   }

   private static final void broadcastResult(Player enchanter, ItemInstance item) {
      if (item.getTemplate().getType2() == 0) {
         if (Config.SHOW_ENCHANT_EFFECT_RESULT_EVERY_NEXT_SUCCESS) {
            if (item.getEnchantLevel() != Config.WEAPON_FIRST_ENCHANT_EFFECT_LEVEL && item.getEnchantLevel() < Config.WEAPON_SECOND_ENCHANT_EFFECT_LEVEL) {
               return;
            }
         } else if (item.getEnchantLevel() != Config.WEAPON_FIRST_ENCHANT_EFFECT_LEVEL && item.getEnchantLevel() != Config.WEAPON_SECOND_ENCHANT_EFFECT_LEVEL) {
            return;
         }

         if (Config.WEAPON_ENCHANT_EFFECT_SKILL_ID > 0) {
            enchanter.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(enchanter, enchanter, Config.WEAPON_ENCHANT_EFFECT_SKILL_ID, 1, 500, 1500L)});
         }

         enchanter.broadCastCustomMessage("_C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3", enchanter, new Object[]{enchanter, item, item.getEnchantLevel()});
      } else {
         if (Config.SHOW_ENCHANT_EFFECT_RESULT_EVERY_NEXT_SUCCESS) {
            if (item.getEnchantLevel() < Config.ARMOR_ENCHANT_EFFECT_LEVEL) {
               return;
            }
         } else if (item.getEnchantLevel() != Config.ARMOR_ENCHANT_EFFECT_LEVEL) {
            return;
         }

         if (Config.ARMOR_ENCHANT_EFFECT_SKILL_ID > 0) {
            enchanter.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(enchanter, enchanter, Config.ARMOR_ENCHANT_EFFECT_SKILL_ID, 1, 500, 1500L)});
         }

         enchanter.broadCastCustomMessage("_C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3", enchanter, new Object[]{enchanter, item, item.getEnchantLevel()});
      }

   }
}
