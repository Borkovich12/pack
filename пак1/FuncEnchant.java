package l2.gameserver.stats.funcs;

import events.BossHunting.ConfigBossHunting;
import events.CaptureCastle.ConfigCaptureCastle;
import events.RoomOfPower.ConfigRoomOfPower;
import events.TreasureHunting.ConfigTreasureHunting;
import events.battle.enums.BattleType;
import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.item.ItemType;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;

public class FuncEnchant extends Func {
   public FuncEnchant(Stats stat, int order, Object owner, double value) {
      super(stat, order, owner);
   }

   private int getItemImpliedEnchantLevel(ItemInstance item) {
      if (item == null) {
         return 0;
      } else {
         Player player = GameObjectsStorage.getPlayer(item.getOwnerId());
         int enchant = item.getEnchantLevel();
         int enchLvlLim = -1;
         if (player != null) {
            if (player.isOlyParticipant()) {
               if (item.isArmor()) {
                  enchLvlLim = Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ARMOR;
               } else if (item.isWeapon()) {
                  enchLvlLim = item.getTemplate().isMageItem() ? Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_MAGE : Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_WEAPON_PHYS;
               } else if (item.isAccessory()) {
                  enchLvlLim = Config.OLY_LIMIT_ENCHANT_STAT_LEVEL_ACCESSORY;
               }

               if (enchLvlLim < 0) {
                  return enchant;
               }

               return Math.min(enchant, enchLvlLim);
            }

            if (player.isInGvG() && haveGvgEnchantLimit(player)) {
               if (item.isArmor()) {
                  enchLvlLim = getGvgEnchantArmorLimit(player);
               } else if (item.isWeapon()) {
                  enchLvlLim = getGvgEnchantWeaponLimit(player);
               } else if (item.isAccessory()) {
                  enchLvlLim = getGvgEnchantArmorLimit(player);
               }

               if (enchLvlLim < 0) {
                  return enchant;
               }

               return Math.min(enchant, enchLvlLim);
            }

            if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_ENCHANT_LIMIT) {
               if (item.isArmor()) {
                  enchLvlLim = ConfigTreasureHunting.TREASURE_HUNTING_ENCHANT_LIMIT_ARMOR;
               } else if (item.isWeapon()) {
                  enchLvlLim = ConfigTreasureHunting.TREASURE_HUNTING_ENCHANT_LIMIT_WEAPON;
               } else if (item.isAccessory()) {
                  enchLvlLim = ConfigTreasureHunting.TREASURE_HUNTING_ENCHANT_LIMIT_ARMOR;
               }

               if (enchLvlLim < 0) {
                  return enchant;
               }

               return Math.min(enchant, enchLvlLim);
            }

            if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_ENCHANT_LIMIT) {
               if (item.isArmor()) {
                  enchLvlLim = ConfigBossHunting.BOSS_HUNTING_ENCHANT_LIMIT_ARMOR;
               } else if (item.isWeapon()) {
                  enchLvlLim = ConfigBossHunting.BOSS_HUNTING_ENCHANT_LIMIT_WEAPON;
               } else if (item.isAccessory()) {
                  enchLvlLim = ConfigBossHunting.BOSS_HUNTING_ENCHANT_LIMIT_ARMOR;
               }

               if (enchLvlLim < 0) {
                  return enchant;
               }

               return Math.min(enchant, enchLvlLim);
            }

            if (player.isInRop() && ConfigRoomOfPower.ROP_ENCHANT_LIMIT) {
               if (item.isArmor()) {
                  enchLvlLim = ConfigRoomOfPower.ROP_ENCHANT_LIMIT_ARMOR;
               } else if (item.isWeapon()) {
                  enchLvlLim = ConfigRoomOfPower.ROP_ENCHANT_LIMIT_WEAPON;
               } else if (item.isAccessory()) {
                  enchLvlLim = ConfigRoomOfPower.ROP_ENCHANT_LIMIT_ARMOR;
               }

               if (enchLvlLim < 0) {
                  return enchant;
               }

               return Math.min(enchant, enchLvlLim);
            }

            if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_ENCHANT_LIMIT) {
               if (item.isArmor()) {
                  enchLvlLim = ConfigCaptureCastle.CAPTURE_CASTLE_ENCHANT_LIMIT_ARMOR;
               } else if (item.isWeapon()) {
                  enchLvlLim = ConfigCaptureCastle.CAPTURE_CASTLE_ENCHANT_LIMIT_WEAPON;
               } else if (item.isAccessory()) {
                  enchLvlLim = ConfigCaptureCastle.CAPTURE_CASTLE_ENCHANT_LIMIT_ARMOR;
               }

               if (enchLvlLim < 0) {
                  return enchant;
               }

               return Math.min(enchant, enchLvlLim);
            }

            if (Config.PVP_EVENTS_RESTRICT_ENCHANT && player.getTeam() != TeamType.NONE) {
               if (item.isArmor()) {
                  enchLvlLim = Config.PVP_EVENTS_RESTRICT_ENCHANT_ARMOR_LEVEL;
               } else if (item.isWeapon()) {
                  enchLvlLim = item.getTemplate().isMageItem() ? Config.PVP_EVENTS_RESTRICT_ENCHANT_WEAPON_MAGE : Config.PVP_EVENTS_RESTRICT_ENCHANT_WEAPON_PHYS;
               } else if (item.isAccessory()) {
                  enchLvlLim = Config.PVP_EVENTS_RESTRICT_ENCHANT_ACCESSORY;
               }

               if (enchLvlLim < 0) {
                  return enchant;
               }

               return Math.min(enchant, enchLvlLim);
            }

            if (!Config.ENCHANT_LIMIT_ZONE_NAMES.isEmpty()) {
               Iterator var5 = player.getZones().iterator();

               while(var5.hasNext()) {
                  Zone zone = (Zone)var5.next();
                  if (Config.ENCHANT_LIMIT_ZONE_NAMES.contains(zone.getName())) {
                     if (item.isArmor()) {
                        enchLvlLim = Config.ENCHANT_LIMIT_ZONE_ARMOR_LEVEL;
                     } else if (item.isWeapon()) {
                        enchLvlLim = item.getTemplate().isMageItem() ? Config.ENCHANT_LIMIT_ZONE_WEAPON_MAGE : Config.ENCHANT_LIMIT_ZONE_WEAPON_PHYS;
                     } else if (item.isAccessory()) {
                        enchLvlLim = Config.ENCHANT_LIMIT_ZONE_ACCESSORY;
                     }

                     if (enchLvlLim < 0) {
                        return enchant;
                     }

                     return Math.min(enchant, enchLvlLim);
                  }
               }
            }
         }

         return enchant;
      }
   }

   public static boolean haveGvgEnchantLimit(Player player) {
      if (!player.isInGvG()) {
         return false;
      } else {
         int gvgId = player.getGvGId();
         if (gvgId == 0) {
            return false;
         } else {
            BattleType[] var2 = BattleType.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               BattleType type = var2[var4];
               if (type.getMembers() == gvgId && type.isEnchantLimit()) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public static int getGvgEnchantWeaponLimit(Player player) {
      if (!player.isInGvG()) {
         return -1;
      } else {
         int gvgId = player.getGvGId();
         if (gvgId == 0) {
            return -1;
         } else {
            BattleType[] var2 = BattleType.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               BattleType type = var2[var4];
               if (type.getMembers() == gvgId && type.isEnchantLimit()) {
                  return type.getEnchantLimitWeapon();
               }
            }

            return -1;
         }
      }
   }

   public static int getGvgEnchantArmorLimit(Player player) {
      if (!player.isInGvG()) {
         return -1;
      } else {
         int gvgId = player.getGvGId();
         if (gvgId == 0) {
            return -1;
         } else {
            BattleType[] var2 = BattleType.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               BattleType type = var2[var4];
               if (type.getMembers() == gvgId && type.isEnchantLimit()) {
                  return type.getEnchantLimitArmor();
               }
            }

            return -1;
         }
      }
   }

   public void calc(Env env) {
      ItemInstance item = (ItemInstance)this.owner;
      int enchant = this.getItemImpliedEnchantLevel(item);
      int overenchant = Math.max(0, enchant - 3);
      switch(this.stat) {
      case SHIELD_DEFENCE:
      case MAGIC_DEFENCE:
      case POWER_DEFENCE:
         env.value += (double)(enchant + overenchant * 2);
         return;
      case MAGIC_ATTACK:
         switch(item.getTemplate().getCrystalType()) {
         case S:
            env.value += (double)(4 * (enchant + overenchant));
            break;
         case A:
            env.value += (double)(3 * (enchant + overenchant));
            break;
         case B:
            env.value += (double)(3 * (enchant + overenchant));
            break;
         case C:
            env.value += (double)(3 * (enchant + overenchant));
            break;
         case D:
         case NONE:
            env.value += (double)(2 * (enchant + overenchant));
         }

         return;
      case POWER_ATTACK:
         ItemType itemType = item.getItemType();
         boolean isBow = itemType == WeaponType.BOW;
         boolean isDSword = (itemType == WeaponType.DUALFIST || itemType == WeaponType.DUAL || itemType == WeaponType.BIGSWORD || itemType == WeaponType.SWORD) && item.getTemplate().getBodyPart() == 16384;
         switch(item.getTemplate().getCrystalType()) {
         case S:
            if (isBow) {
               env.value += (double)(10 * (enchant + overenchant));
            } else if (isDSword) {
               env.value += (double)(6 * (enchant + overenchant));
            } else {
               env.value += (double)(5 * (enchant + overenchant));
            }
            break;
         case A:
            if (isBow) {
               env.value += (double)(8 * (enchant + overenchant));
            } else if (isDSword) {
               env.value += (double)(5 * (enchant + overenchant));
            } else {
               env.value += (double)(4 * (enchant + overenchant));
            }
            break;
         case B:
         case C:
            if (isBow) {
               env.value += (double)(6 * (enchant + overenchant));
            } else if (isDSword) {
               env.value += (double)(4 * (enchant + overenchant));
            } else {
               env.value += (double)(3 * (enchant + overenchant));
            }
            break;
         case D:
         case NONE:
            if (isBow) {
               env.value += (double)(4 * (enchant + overenchant));
            } else {
               env.value += (double)(2 * (enchant + overenchant));
            }
         }

         return;
      case POWER_ATTACK_SPEED:
      case MAGIC_ATTACK_SPEED:
      case MAX_HP:
      case MAX_MP:
      case MAX_CP:
         env.value += 1.5D * (double)(enchant + overenchant);
         return;
      default:
      }
   }
}
