package services.community.custom.progress;

import java.util.Iterator;
import l2.gameserver.model.Player;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate.Grade;

public enum ProgressMetricType {
   LOGIN,
   NPC_KILL,
   NPC_CHAMP_KILL,
   NPC_KILL_PARTY,
   PVP_KILL {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         final int pvpKills = player.getPvpKills();
         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return pvpKills;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return pvpKills;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   PK_KILL {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         final int pkKills = player.getPkKills();
         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return pkKills;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return pkKills;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   ENCHANT_ITEM {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         final int enchantLevel = 0;
         ItemInstance[] var5 = player.getInventory().getItems();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ItemInstance item = var5[var7];
            if (item.isEquipable() && enchantLevel < item.getEnchantLevel()) {
               enchantLevel = item.getEnchantLevel();
            }
         }

         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return enchantLevel;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return enchantLevel;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   ENCHANT_WEAPON {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         final int enchantLevel = 0;
         ItemInstance[] var5 = player.getInventory().getItems();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ItemInstance item = var5[var7];
            if (item.isWeapon() && enchantLevel < item.getEnchantLevel()) {
               enchantLevel = item.getEnchantLevel();
            }
         }

         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return enchantLevel;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return enchantLevel;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   ENCHANT_A_S_ARMOR {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         final int enchantLevel = 0;
         ItemInstance[] var5 = player.getInventory().getItems();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ItemInstance item = var5[var7];
            if (item.isArmor() && (item.getCrystalType() == Grade.A || item.getCrystalType() == Grade.S) && enchantLevel < item.getEnchantLevel()) {
               enchantLevel = item.getEnchantLevel();
            }
         }

         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return enchantLevel;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return enchantLevel;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   ENCHANT_ARMOR_AND_JEWELRY {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         int enchantArmorLevel = 0;
         ItemInstance[] var5 = player.getInventory().getItems();
         final int finalLevel = var5.length;

         int var7;
         for(var7 = 0; var7 < finalLevel; ++var7) {
            ItemInstance item = var5[var7];
            if (item.isArmor() && enchantArmorLevel < item.getEnchantLevel()) {
               enchantArmorLevel = item.getEnchantLevel();
            }
         }

         int enchantJewelsLevel = 0;
         ItemInstance[] var11 = player.getInventory().getItems();
         var7 = var11.length;

         for(int var12 = 0; var12 < var7; ++var12) {
            ItemInstance itemx = var11[var12];
            if (itemx.isArmor() && enchantJewelsLevel < itemx.getEnchantLevel()) {
               enchantJewelsLevel = itemx.getEnchantLevel();
            }
         }

         finalLevel = Math.min(enchantArmorLevel, enchantJewelsLevel);
         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return finalLevel;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return finalLevel;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   ADD_ITEM,
   CRAFT_WEAPON_S,
   CASTLE_SIEGE,
   TVT_EVENT,
   TVT_WIN_EVENT,
   CTF_EVENT,
   CTF_WIN_EVENT,
   DEATH_MATCH_EVENT,
   DEATH_MATCH_TEN_PLACE_EVENT,
   DEATH_MATCH_WIN_EVENT,
   ROOM_OF_POWER_EVENT,
   ROOM_OF_POWER_WIN_EVENT,
   BOSS_HUNTING_EVENT,
   BOSS_HUNTING_WIN_EVENT,
   TREASURE_HUNTING_EVENT,
   TREASURE_HUNTING_WIN_EVENT,
   GVG_EVENT,
   GVG_WIN_EVENT,
   CAPTURE_CASTLE_EVENT,
   CAPTURE_CASTLE_WIN_EVENT,
   TOP_KILLER_EVENT,
   TOP_KILLER_WIN_EVENT,
   TOP_KILLER_DINO_EVENT,
   TOP_KILLER_DINO_WIN_EVENT,
   TOP_KILLER_PVP_EVENT,
   TOP_KILLER_PVP_WIN_EVENT,
   TOP_KILLER_PK_EVENT,
   TOP_KILLER_PK_WIN_EVENT,
   DEATH,
   SUB_LEVEL {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         final int level = 0;
         Iterator var5 = player.getSubClasses().values().iterator();

         while(var5.hasNext()) {
            SubClass subClass = (SubClass)var5.next();
            if (subClass.getLevel() > level) {
               level = subClass.getLevel();
            }
         }

         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return level;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return level;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   NOBLE {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         final int count = player.isNoble() ? 1 : 0;
         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return count;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return count;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   LEVEL {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         final int playerLevel = player.getLevel();
         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return playerLevel;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return playerLevel;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   REC_COUNT {
      public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
         final int playerRec = player.getReceivedRec();
         return new ProgressCounter(player.getObjectId(), activeStageId, achInfo.getId()) {
            public int getVal() {
               return playerRec;
            }

            public void setVal(int val) {
            }

            public int incrementAndGetValue() {
               return playerRec;
            }

            public void store() {
            }

            public boolean isStorable() {
               return false;
            }
         };
      }
   },
   OLYMPIAD,
   RAID_PARTICIPATION,
   QUEST_STATE;

   private ProgressMetricType() {
   }

   public ProgressCounter getCounter(Player player, int activeStageId, ProgressInfo achInfo) {
      return ProgressCounter.makeDBStorableCounter(player.getObjectId(), activeStageId, achInfo.getId());
   }

   // $FF: synthetic method
   ProgressMetricType(Object x2) {
      this();
   }
}
