package events.battle.enums;

import events.battle.BattleConfig;
import java.util.List;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Pair;

public enum BattleType {
   B1X1("1x1", 1) {
      public String getStartTime() {
         return BattleConfig.GVG_1X1_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_1X1_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_1X1_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_1X1_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_1X1_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_1X1_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_1X1_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_1X1_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_1X1_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_1X1_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_1X1_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_1X1_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_1X1_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_1X1_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_1X1_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_1X1_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_1X1_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_1X1_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_1X1_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_1X1_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_1X1_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_1X1_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_1X1_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_1X1_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_1X1_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_1X1_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_1X1_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_1X1_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_1X1_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_1X1_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_1X1_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_1X1_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_1X1_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_1X1_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_1X1_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_1X1_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_1X1_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_1X1_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_1X1_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_1X1_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_1X1_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_1X1_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_1X1_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_1X1_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_1X1_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_1X1_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_1X1_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_1X1_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_1X1_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_1X1_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_1X1_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_1X1_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_1X1_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_1X1_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_1X1_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_1X1_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_1X1_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_1X1_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_1X1_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_1X1_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_1X1_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_1X1_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_1X1_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_1X1_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_1X1_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_1X1_DOOMCRYER_ALLOWED;
      }
   },
   B2X2("2x2", 2) {
      public String getStartTime() {
         return BattleConfig.GVG_2X2_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_2X2_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_2X2_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_2X2_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_2X2_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_2X2_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_2X2_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_2X2_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_2X2_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_2X2_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_2X2_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_2X2_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_2X2_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_2X2_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_2X2_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_2X2_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_2X2_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_2X2_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_2X2_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_2X2_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_2X2_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_2X2_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_2X2_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_2X2_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_2X2_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_2X2_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_2X2_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_2X2_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_2X2_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_2X2_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_2X2_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_2X2_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_2X2_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_2X2_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_2X2_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_2X2_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_2X2_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_2X2_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_2X2_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_2X2_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_2X2_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_2X2_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_2X2_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_2X2_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_2X2_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_2X2_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_2X2_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_2X2_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_2X2_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_2X2_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_2X2_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_2X2_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_2X2_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_2X2_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_2X2_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_2X2_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_2X2_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_2X2_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_2X2_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_2X2_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_2X2_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_2X2_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_2X2_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_2X2_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_2X2_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_2X2_DOOMCRYER_ALLOWED;
      }
   },
   B3X3("3x3", 3) {
      public String getStartTime() {
         return BattleConfig.GVG_3X3_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_3X3_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_3X3_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_3X3_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_3X3_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_3X3_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_3X3_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_3X3_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_3X3_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_3X3_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_3X3_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_3X3_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_3X3_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_3X3_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_3X3_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_3X3_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_3X3_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_3X3_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_3X3_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_3X3_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_3X3_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_3X3_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_3X3_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_3X3_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_3X3_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_3X3_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_3X3_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_3X3_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_3X3_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_3X3_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_3X3_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_3X3_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_3X3_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_3X3_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_3X3_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_3X3_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_3X3_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_3X3_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_3X3_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_3X3_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_3X3_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_3X3_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_3X3_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_3X3_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_3X3_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_3X3_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_3X3_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_3X3_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_3X3_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_3X3_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_3X3_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_3X3_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_3X3_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_3X3_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_3X3_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_3X3_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_3X3_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_3X3_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_3X3_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_3X3_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_3X3_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_3X3_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_3X3_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_3X3_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_3X3_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_3X3_DOOMCRYER_ALLOWED;
      }
   },
   B4X4("4x4", 4) {
      public String getStartTime() {
         return BattleConfig.GVG_4X4_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_4X4_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_4X4_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_4X4_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_4X4_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_4X4_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_4X4_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_4X4_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_4X4_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_4X4_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_4X4_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_4X4_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_4X4_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_4X4_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_4X4_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_4X4_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_4X4_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_4X4_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_4X4_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_4X4_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_4X4_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_4X4_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_4X4_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_4X4_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_4X4_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_4X4_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_4X4_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_4X4_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_4X4_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_4X4_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_4X4_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_4X4_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_4X4_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_4X4_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_4X4_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_4X4_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_4X4_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_4X4_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_4X4_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_4X4_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_4X4_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_4X4_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_4X4_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_4X4_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_4X4_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_4X4_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_4X4_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_4X4_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_4X4_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_4X4_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_4X4_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_4X4_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_4X4_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_4X4_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_4X4_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_4X4_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_4X4_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_4X4_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_4X4_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_4X4_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_4X4_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_4X4_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_4X4_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_4X4_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_4X4_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_4X4_DOOMCRYER_ALLOWED;
      }
   },
   B5X5("5x5", 5) {
      public String getStartTime() {
         return BattleConfig.GVG_5X5_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_5X5_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_5X5_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_5X5_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_5X5_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_5X5_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_5X5_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_5X5_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_5X5_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_5X5_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_5X5_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_5X5_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_5X5_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_5X5_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_5X5_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_5X5_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_5X5_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_5X5_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_5X5_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_5X5_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_5X5_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_5X5_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_5X5_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_5X5_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_5X5_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_5X5_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_5X5_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_5X5_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_5X5_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_5X5_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_5X5_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_5X5_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_5X5_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_5X5_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_5X5_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_5X5_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_5X5_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_5X5_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_5X5_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_5X5_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_5X5_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_5X5_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_5X5_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_5X5_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_5X5_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_5X5_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_5X5_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_5X5_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_5X5_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_5X5_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_5X5_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_5X5_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_5X5_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_5X5_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_5X5_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_5X5_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_5X5_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_5X5_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_5X5_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_5X5_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_5X5_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_5X5_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_5X5_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_5X5_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_5X5_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_5X5_DOOMCRYER_ALLOWED;
      }
   },
   B6X6("6x6", 6) {
      public String getStartTime() {
         return BattleConfig.GVG_6X6_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_6X6_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_6X6_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_6X6_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_6X6_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_6X6_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_6X6_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_6X6_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_6X6_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_6X6_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_6X6_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_6X6_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_6X6_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_6X6_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_6X6_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_6X6_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_6X6_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_6X6_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_6X6_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_6X6_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_6X6_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_6X6_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_6X6_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_6X6_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_6X6_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_6X6_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_6X6_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_6X6_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_6X6_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_6X6_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_6X6_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_6X6_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_6X6_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_6X6_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_6X6_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_6X6_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_6X6_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_6X6_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_6X6_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_6X6_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_6X6_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_6X6_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_6X6_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_6X6_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_6X6_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_6X6_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_6X6_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_6X6_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_6X6_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_6X6_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_6X6_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_6X6_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_6X6_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_6X6_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_6X6_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_6X6_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_6X6_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_6X6_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_6X6_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_6X6_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_6X6_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_6X6_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_6X6_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_6X6_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_6X6_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_6X6_DOOMCRYER_ALLOWED;
      }
   },
   B7X7("7x7", 7) {
      public String getStartTime() {
         return BattleConfig.GVG_7X7_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_7X7_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_7X7_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_7X7_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_7X7_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_7X7_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_7X7_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_7X7_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_7X7_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_7X7_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_7X7_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_7X7_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_7X7_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_7X7_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_7X7_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_7X7_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_7X7_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_7X7_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_7X7_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_7X7_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_7X7_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_7X7_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_7X7_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_7X7_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_7X7_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_7X7_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_7X7_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_7X7_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_7X7_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_7X7_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_7X7_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_7X7_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_7X7_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_7X7_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_7X7_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_7X7_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_7X7_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_7X7_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_7X7_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_7X7_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_7X7_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_7X7_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_7X7_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_7X7_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_7X7_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_7X7_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_7X7_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_7X7_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_7X7_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_7X7_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_7X7_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_7X7_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_7X7_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_7X7_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_7X7_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_7X7_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_7X7_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_7X7_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_7X7_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_7X7_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_7X7_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_7X7_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_7X7_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_7X7_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_7X7_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_7X7_DOOMCRYER_ALLOWED;
      }
   },
   B8X8("8x8", 8) {
      public String getStartTime() {
         return BattleConfig.GVG_8X8_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_8X8_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_8X8_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_8X8_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_8X8_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_8X8_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_8X8_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_8X8_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_8X8_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_8X8_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_8X8_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_8X8_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_8X8_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_8X8_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_8X8_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_8X8_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_8X8_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_8X8_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_8X8_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_8X8_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_8X8_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_8X8_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_8X8_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_8X8_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_8X8_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_8X8_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_8X8_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_8X8_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_8X8_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_8X8_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_8X8_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_8X8_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_8X8_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_8X8_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_8X8_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_8X8_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_8X8_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_8X8_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_8X8_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_8X8_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_8X8_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_8X8_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_8X8_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_8X8_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_8X8_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_8X8_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_8X8_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_8X8_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_8X8_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_8X8_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_8X8_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_8X8_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_8X8_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_8X8_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_8X8_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_8X8_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_8X8_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_8X8_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_8X8_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_8X8_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_8X8_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_8X8_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_8X8_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_8X8_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_8X8_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_8X8_DOOMCRYER_ALLOWED;
      }
   },
   B9X9("9x9", 9) {
      public String getStartTime() {
         return BattleConfig.GVG_9X9_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_9X9_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_9X9_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_9X9_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_9X9_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_9X9_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_9X9_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_9X9_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_9X9_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_9X9_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_9X9_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_9X9_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_9X9_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_9X9_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_9X9_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_9X9_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_9X9_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_9X9_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_9X9_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_9X9_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_9X9_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_9X9_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_9X9_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_9X9_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_9X9_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_9X9_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_9X9_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_9X9_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_9X9_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_9X9_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_9X9_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_9X9_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_9X9_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_9X9_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_9X9_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_9X9_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_9X9_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_9X9_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_9X9_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_9X9_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_9X9_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_9X9_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_9X9_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_9X9_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_9X9_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_9X9_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_9X9_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_9X9_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_9X9_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_9X9_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_9X9_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_9X9_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_9X9_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_9X9_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_9X9_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_9X9_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_9X9_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_9X9_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_9X9_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_9X9_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_9X9_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_9X9_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_9X9_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_9X9_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_9X9_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_9X9_DOOMCRYER_ALLOWED;
      }
   },
   BCXC("CxC", 18) {
      public String getStartTime() {
         return BattleConfig.GVG_CXC_START_TIME;
      }

      public int getInstanceId() {
         return BattleConfig.GVG_CXC_INSTANCE_ID;
      }

      public int getMinLevel() {
         return BattleConfig.GVG_CXC_MIN_LVL;
      }

      public int getMaxLevel() {
         return BattleConfig.GVG_CXC_MAX_LVL;
      }

      public int getCommandsMin() {
         return BattleConfig.GVG_CXC_COMMANDS_MIN;
      }

      public int getCommandsMax() {
         return BattleConfig.GVG_CXC_COMMANDS_MAX;
      }

      public boolean isRestrictIp() {
         return BattleConfig.GVG_CXC_RESTRICT_IP;
      }

      public boolean isRestrictHwid() {
         return BattleConfig.GVG_CXC_RESTRICT_HWID;
      }

      public boolean isToArena() {
         return BattleConfig.GVG_CXC_TO_ARENA;
      }

      public String[] getArenaPoints() {
         return BattleConfig.GVG_CXC_ARENA_POINTS;
      }

      public boolean isInPeace() {
         return BattleConfig.GVG_CXC_IN_PEACE;
      }

      public boolean isPreCheck() {
         return BattleConfig.GVG_CXC_PRE_CHECK;
      }

      public boolean isBlockMove() {
         return BattleConfig.GVG_CXC_BLOCK_MOVE;
      }

      public boolean isShowStatus() {
         return BattleConfig.GVG_CXC_SHOW_STATUS;
      }

      public int getRoundsWin() {
         return BattleConfig.GVG_CXC_ROUNDS_WIN;
      }

      public int getRegTime() {
         return BattleConfig.GVG_CXC_REG_TIME;
      }

      public int[] getAnnounceRegTimes() {
         return BattleConfig.GVG_CXC_ANNOUNCE_REG_TIMES;
      }

      public int getTimeBattle() {
         return BattleConfig.GVG_CXC_TIME_BATTLE;
      }

      public int getHealTpBackTime() {
         return BattleConfig.GVG_CXC_HEAL_TP_BACK_TIME;
      }

      public boolean isAllowBattleTimer() {
         return BattleConfig.GVG_CXC_ALLOW_BATTLE_TIMER;
      }

      public int[] getReturnPoint() {
         return BattleConfig.GVG_CXC_RETURN_POINT;
      }

      public int getManagerId() {
         return BattleConfig.GVG_CXC_MANAGER_ID;
      }

      public byte getManagerSpawnType() {
         return BattleConfig.GVG_CXC_MANAGER_SPAWN_TYPE;
      }

      public int[] getManagerCoords() {
         return BattleConfig.GVG_CXC_MANAGER_COORDS;
      }

      public int[] getRestrictedSkills() {
         return BattleConfig.GVG_CXC_RESTRICTED_SKILLS;
      }

      public int[] getRestrictedItems() {
         return BattleConfig.GVG_CXC_RESTRICTED_ITEMS;
      }

      public int[] getRestrictedSummons() {
         return BattleConfig.GVG_CXC_RESTRICTED_SUMMONS;
      }

      public int getBufferId() {
         return BattleConfig.GVG_CXC_BUFFER_ID;
      }

      public int getTpRange() {
         return BattleConfig.GVG_CXC_TP_RANGE;
      }

      public String[] getBuffer1Coords() {
         return BattleConfig.GVG_CXC_BUFFER1_COORDS;
      }

      public String[] getBuffer2Coords() {
         return BattleConfig.GVG_CXC_BUFFER2_COORDS;
      }

      public boolean isEnableBuffs() {
         return BattleConfig.GVG_CXC_ENABLE_BUFFS;
      }

      public List<Pair<Integer, Integer>> getWarriorBuff() {
         return BattleConfig.GVG_CXC_WARRIOR_BUFF;
      }

      public List<Pair<Integer, Integer>> getMageBuff() {
         return BattleConfig.GVG_CXC_MAGE_BUFF;
      }

      public boolean isCustomItemsEnable() {
         return BattleConfig.GVG_CXC_CUSTOM_ITEMS_ENABLE;
      }

      public String getCustomItemsPath() {
         return BattleConfig.GVG_CXC_CUSTOM_ITEMS_PATH;
      }

      public int getCustomItemsEnchantWeapon() {
         return BattleConfig.GVG_CXC_CUSTOM_ITEMS_ENCHANT_WEAPON;
      }

      public int getCustomItemsEnchantArmor() {
         return BattleConfig.GVG_CXC_CUSTOM_ITEMS_ENCHANT_ARMOR;
      }

      public boolean isEnchantLimit() {
         return BattleConfig.GVG_CXC_ENCHANT_LIMIT;
      }

      public int getEnchantLimitWeapon() {
         return BattleConfig.GVG_CXC_ENCHANT_LIMIT_WEAPON;
      }

      public int getEnchantLimitArmor() {
         return BattleConfig.GVG_CXC_ENCHANT_LIMIT_ARMOR;
      }

      public String getCNameTemplate() {
         return BattleConfig.GVG_CXC_CNAME_TEMPLATE;
      }

      public boolean isAllowSpec() {
         return BattleConfig.GVG_CXC_ALLOW_SPEC;
      }

      public boolean isProhibitParticipantsSpec() {
         return BattleConfig.GVG_CXC_PROHIBIT_PARTICIPANTS_SPEC;
      }

      public int[][] getReward() {
         return BattleConfig.GVG_CXC_REWARD;
      }

      public int[][] getRewardPerKill() {
         return BattleConfig.GVG_CXC_REWARD_PER_KILL;
      }

      public List<Location> getBlueTeamLoc() {
         return BattleConfig.GVG_CXC_BLUE_TEAM_LOC;
      }

      public List<Location> getRedTeamLoc() {
         return BattleConfig.GVG_CXC_RED_TEAM_LOC;
      }

      public String getClearLoc() {
         return BattleConfig.GVG_CXC_CLEAR_LOC;
      }

      public int[] getCoordsSpectators() {
         return BattleConfig.GVG_CXC_COORDS_SPECTATORS;
      }

      public boolean isNoAn() {
         return BattleConfig.GVG_CXC_NO_AN;
      }

      public boolean isNoEnchantSkills() {
         return BattleConfig.GVG_CXC_NO_ENCHANT_SKILLS;
      }

      public int[] getProhibitedClassIds() {
         return BattleConfig.GVG_CXC_PROHIBITED_CLASS_IDS;
      }

      public int getDuelistAllowed() {
         return BattleConfig.GVG_CXC_DUELIST_ALLOWED;
      }

      public int getDreadnoughtAllowed() {
         return BattleConfig.GVG_CXC_DREADNOUGHT_ALLOWED;
      }

      public int getTankerAllowed() {
         return BattleConfig.GVG_CXC_TANKER_ALLOWED;
      }

      public int getDaggerAllowed() {
         return BattleConfig.GVG_CXC_DAGGER_ALLOWED;
      }

      public int getArcherAllowed() {
         return BattleConfig.GVG_CXC_ARCHER_ALLOWED;
      }

      public int getHealerAllowed() {
         return BattleConfig.GVG_CXC_HEALER_ALLOWED;
      }

      public int getArchmageAllowed() {
         return BattleConfig.GVG_CXC_ARCHMAGE_ALLOWED;
      }

      public int getSoultakerAllowed() {
         return BattleConfig.GVG_CXC_SOULTAKER_ALLOWED;
      }

      public int getMysticMouseAllowed() {
         return BattleConfig.GVG_CXC_MYSTICMOUSE_ALLOWED;
      }

      public int getStormScreamerAllowed() {
         return BattleConfig.GVG_CXC_STORMSCREAMER_ALLOWED;
      }

      public int getTitanAllowed() {
         return BattleConfig.GVG_CXC_TITAN_ALLOWED;
      }

      public int getDominatorAllowed() {
         return BattleConfig.GVG_CXC_DOMINATOR_ALLOWED;
      }

      public int getDoomcryerAllowed() {
         return BattleConfig.GVG_CXC_DOOMCRYER_ALLOWED;
      }
   };

   private final String nameType;
   private final int members;

   private BattleType(String nameType, int members) {
      this.nameType = nameType;
      this.members = members;
   }

   public abstract String getStartTime();

   public abstract int getInstanceId();

   public abstract int getMinLevel();

   public abstract int getMaxLevel();

   public abstract int getCommandsMin();

   public abstract int getCommandsMax();

   public abstract boolean isRestrictIp();

   public abstract boolean isRestrictHwid();

   public abstract boolean isToArena();

   public abstract String[] getArenaPoints();

   public abstract boolean isInPeace();

   public abstract boolean isPreCheck();

   public abstract boolean isBlockMove();

   public abstract boolean isShowStatus();

   public abstract int getRoundsWin();

   public abstract int getRegTime();

   public abstract int[] getAnnounceRegTimes();

   public abstract int getTimeBattle();

   public abstract int getHealTpBackTime();

   public abstract boolean isAllowBattleTimer();

   public abstract int[] getReturnPoint();

   public abstract int getManagerId();

   public abstract byte getManagerSpawnType();

   public abstract int[] getManagerCoords();

   public abstract int[] getRestrictedSkills();

   public abstract int[] getRestrictedItems();

   public abstract int[] getRestrictedSummons();

   public abstract int getBufferId();

   public abstract int getTpRange();

   public abstract String[] getBuffer1Coords();

   public abstract String[] getBuffer2Coords();

   public abstract boolean isEnableBuffs();

   public abstract List<Pair<Integer, Integer>> getWarriorBuff();

   public abstract List<Pair<Integer, Integer>> getMageBuff();

   public abstract boolean isCustomItemsEnable();

   public abstract String getCustomItemsPath();

   public abstract int getCustomItemsEnchantWeapon();

   public abstract int getCustomItemsEnchantArmor();

   public abstract boolean isEnchantLimit();

   public abstract int getEnchantLimitWeapon();

   public abstract int getEnchantLimitArmor();

   public abstract String getCNameTemplate();

   public abstract boolean isAllowSpec();

   public abstract boolean isProhibitParticipantsSpec();

   public abstract int[][] getReward();

   public abstract int[][] getRewardPerKill();

   public abstract List<Location> getBlueTeamLoc();

   public abstract List<Location> getRedTeamLoc();

   public abstract String getClearLoc();

   public abstract int[] getCoordsSpectators();

   public abstract boolean isNoAn();

   public abstract boolean isNoEnchantSkills();

   public abstract int[] getProhibitedClassIds();

   public abstract int getDuelistAllowed();

   public abstract int getDreadnoughtAllowed();

   public abstract int getTankerAllowed();

   public abstract int getDaggerAllowed();

   public abstract int getArcherAllowed();

   public abstract int getHealerAllowed();

   public abstract int getArchmageAllowed();

   public abstract int getSoultakerAllowed();

   public abstract int getMysticMouseAllowed();

   public abstract int getStormScreamerAllowed();

   public abstract int getTitanAllowed();

   public abstract int getDominatorAllowed();

   public abstract int getDoomcryerAllowed();

   public String getNameType() {
      return this.nameType;
   }

   public int getMembers() {
      return this.members;
   }

   public static BattleType getTypeByName(String name) {
      BattleType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         BattleType type = var1[var3];
         if (type.getNameType().equalsIgnoreCase(name)) {
            return type;
         }
      }

      return null;
   }

   // $FF: synthetic method
   BattleType(String x2, int x3, Object x4) {
      this(x2, x3);
   }
}
