package events.RoomOfPower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigRoomOfPower {
   private static Logger _log = LoggerFactory.getLogger(ConfigRoomOfPower.class);
   public static final String CONFIG_FILE = "config/events/room_of_power.properties";
   public static int[] ROP_REWARD_FINAL;
   public static int[] ROP_REWARD_FINAL_PREMIUM;
   public static boolean ROP_TOP_KILLER_ENABLE;
   public static boolean ROP_TOP_KILLER_ANNOUNCE;
   public static Map<Integer, int[]> ROP_TOP_KILLER_REWARD = new HashMap();
   public static Map<Integer, int[]> ROP_TOP_KILLER_REWARD_PREMIUM = new HashMap();
   public static int ROP_WINNER_MIN_KILLS;
   public static int ROP_WINNER_MIN_KILLS_PREMIUM;
   public static int[] ROP_CLASS_REWARD_NO_KILLS;
   public static boolean ROP_IP_RESTRICTION;
   public static boolean ROP_HWID_RESTRICTION;
   public static int ROP_MIN_PLAYERS;
   public static int ROP_MAX_PLAYERS;
   public static int ROP_MIN_LEVEL;
   public static int ROP_MAX_LEVEL;
   public static boolean ROP_CANCEL_ALL_BUFF;
   public static int ROP_TIME_TO_START;
   public static int ROP_RESURRECT_DELAY;
   public static int ROP_TIME_PARALYZE;
   public static int ROP_BATTLE_DURATION;
   public static int[] ROP_RETURN_POINT;
   public static boolean ROP_SHOW_KILLS;
   public static boolean ROP_ENABLE_CUSTOM_ITEMS;
   public static int ROP_CUSTOM_ITEMS_ENCHANT;
   public static Map<Integer, List<Integer>> ROP_CUSTOM_ITEMS;
   public static boolean ROP_ALLOW_CALENDAR_DAY;
   public static boolean ROP_ALLOW_INFO_LOG_SCHEDULE;
   public static int[] ROP_START_TIME;
   public static String ROP_ZONE_NAME;
   public static List<Location> ROP_TELE_ZONE_LOCS = new ArrayList();
   public static String ROP_CLEAR_LOC;
   public static int ROP_TIME_BACK;
   public static int[] ROP_RESTRICTED_ITEMS;
   public static int[] ROP_RESTRICTED_SKILLS;
   public static int[] ROP_RESTRICTED_CLASS_IDS;
   public static boolean ROP_DISABLE_HERO_SKILLS;
   public static int[] ROP_RESTRICTED_SUMMONS;
   public static int[] ROP_BUFFS_FIGHTER;
   public static int[] ROP_BUFFS_MAGE;
   public static long ROP_ALT_BUFFS_DURATION;
   public static boolean ROP_BROADCAST_TIMER;
   public static boolean ROP_PARALYZE_PLAYERS;
   public static int ROP_DEATH_COUNT;
   public static boolean ROP_ENABLE_COMMAND;
   public static String ROP_VOICE_COMMAND;
   public static boolean ROP_SPAWN_REG_MANAGER;
   public static int ROP_REG_MANAGER_ID;
   public static String ROP_REG_MANAGER_LOC;
   public static boolean ROP_SEND_REG_WINDOW;
   public static boolean ROP_ENCHANT_LIMIT;
   public static int ROP_ENCHANT_LIMIT_WEAPON;
   public static int ROP_ENCHANT_LIMIT_ARMOR;
   public static boolean ROP_HIDE_NAME;
   public static boolean ROP_HIDE_TITLE;
   public static boolean ROP_HIDE_CLAN_ALY_INFO;
   public static boolean ROP_HIDE_NAME_COLOR;
   public static boolean ROP_HIDE_TITLE_COLOR;

   public static void load() {
      ExProperties eventSettings = Config.load("config/events/room_of_power.properties");
      ROP_REWARD_FINAL = eventSettings.getProperty("RopRewardFinal", new int[]{57, 10000});
      ROP_REWARD_FINAL_PREMIUM = eventSettings.getProperty("RopRewardFinalPremium", new int[]{57, 20000});
      ROP_TOP_KILLER_ENABLE = eventSettings.getProperty("RopTopKillerEnable", false);
      ROP_TOP_KILLER_ANNOUNCE = eventSettings.getProperty("RopTopKillerAnnounce", false);
      String[] rewardsByPlace = eventSettings.getProperty("RopTopKillerReward", "").split(";");
      String[] rewardsByPlacePremium = rewardsByPlace;
      int var3 = rewardsByPlace.length;

      int var4;
      String[] s;
      int y;
      for(var4 = 0; var4 < var3; ++var4) {
         String r = rewardsByPlacePremium[var4];
         String[] rewardSplit = r.split(":");
         int place = Integer.parseInt(rewardSplit[0]);
         s = rewardSplit[1].split(",");
         int[] items = new int[s.length];

         for(y = 0; y < s.length; ++y) {
            items[y] = Integer.parseInt(s[y]);
         }

         ROP_TOP_KILLER_REWARD.put(place, items);
      }

      rewardsByPlacePremium = eventSettings.getProperty("RopTopKillerRewardPremium", "").split(";");
      String[] teleLocs = rewardsByPlacePremium;
      var4 = rewardsByPlacePremium.length;

      int z;
      int var14;
      for(var14 = 0; var14 < var4; ++var14) {
         String r = teleLocs[var14];
         String[] rewardSplit = r.split(":");
         int place = Integer.parseInt(rewardSplit[0]);
         String[] rewards = rewardSplit[1].split(",");
         int[] items = new int[rewards.length];

         for(z = 0; z < rewards.length; ++z) {
            items[z] = Integer.parseInt(rewards[z]);
         }

         ROP_TOP_KILLER_REWARD_PREMIUM.put(place, items);
      }

      ROP_WINNER_MIN_KILLS = eventSettings.getProperty("RopWinnerMinKills", 0);
      ROP_WINNER_MIN_KILLS_PREMIUM = eventSettings.getProperty("RopWinnerMinKillsPremium", 0);
      ROP_CLASS_REWARD_NO_KILLS = eventSettings.getProperty("RopClassRewardNoKills", new int[0]);
      ROP_IP_RESTRICTION = eventSettings.getProperty("RopIpRestriction", false);
      ROP_HWID_RESTRICTION = eventSettings.getProperty("RopHwidRestriction", false);
      ROP_MIN_PLAYERS = eventSettings.getProperty("RopMinPlayers", 4);
      ROP_MAX_PLAYERS = eventSettings.getProperty("RopMaxPlayers", 54);
      ROP_MIN_LEVEL = eventSettings.getProperty("RopMinLevel", 1);
      ROP_MAX_LEVEL = eventSettings.getProperty("RopMaxLevel", 80);
      ROP_CANCEL_ALL_BUFF = eventSettings.getProperty("RopCancelAllBuff", false);
      ROP_TIME_TO_START = eventSettings.getProperty("RopTimeToStart", 3);
      ROP_RESURRECT_DELAY = eventSettings.getProperty("RopResurrectDelay", 20);
      ROP_TIME_PARALYZE = eventSettings.getProperty("RopTimeParalyze", 60);
      ROP_BATTLE_DURATION = eventSettings.getProperty("RopBattleDuration", 10);
      ROP_RETURN_POINT = eventSettings.getProperty("RopReturnPoint", new int[0]);
      ROP_ALLOW_INFO_LOG_SCHEDULE = eventSettings.getProperty("RopAllowInfoLogSchedule", false);
      ROP_ALLOW_CALENDAR_DAY = eventSettings.getProperty("RopAllowCalendarDay", false);
      ROP_START_TIME = eventSettings.getProperty("RopStartTime", new int[]{18, 30});
      ROP_ZONE_NAME = eventSettings.getProperty("RopZoneName", "[colosseum_pvp_event]");
      teleLocs = eventSettings.getProperty("RopTeleZoneLocs", "174232,-89016,-5138").split(";");
      String[] var13 = teleLocs;
      var14 = teleLocs.length;

      for(int var16 = 0; var16 < var14; ++var16) {
         String loc = var13[var16];
         s = loc.split(",");
         int x = Integer.parseInt(s[0]);
         y = Integer.parseInt(s[1]);
         z = Integer.parseInt(s[2]);
         ROP_TELE_ZONE_LOCS.add(new Location(x, y, z));
      }

      ROP_CLEAR_LOC = eventSettings.getProperty("RopClearLoc", "147451,46728,-3410");
      ROP_SHOW_KILLS = eventSettings.getProperty("RopShowKills", false);
      ROP_ENABLE_CUSTOM_ITEMS = eventSettings.getProperty("RopEnableCustomItems", false);
      ROP_CUSTOM_ITEMS_ENCHANT = eventSettings.getProperty("RopCustomItemsEnchant", 0);
      ROP_TIME_BACK = eventSettings.getProperty("RopTimeBack", 30);
      ROP_RESTRICTED_ITEMS = eventSettings.getProperty("RopRestrictedItems", new int[0]);
      ROP_RESTRICTED_SKILLS = eventSettings.getProperty("RopRestrictedSkills", new int[0]);
      ROP_RESTRICTED_CLASS_IDS = eventSettings.getProperty("RopRestrictedClassIds", new int[0]);
      ROP_DISABLE_HERO_SKILLS = eventSettings.getProperty("RopDisableHeroSkills", true);
      ROP_RESTRICTED_SUMMONS = eventSettings.getProperty("RopRestrictedSummons", new int[0]);
      ROP_BUFFS_FIGHTER = eventSettings.getProperty("RopBuffsFighter", new int[0]);
      ROP_BUFFS_MAGE = eventSettings.getProperty("RopBuffsMage", new int[0]);
      ROP_ALT_BUFFS_DURATION = eventSettings.getProperty("RopAltBuffsDuration", 0L) * 1000L;
      ROP_BROADCAST_TIMER = eventSettings.getProperty("RopBroadcastTime", false);
      ROP_PARALYZE_PLAYERS = eventSettings.getProperty("RopParalyzePlayers", false);
      ROP_DEATH_COUNT = eventSettings.getProperty("RopDeathCount", 0);
      ROP_ENABLE_COMMAND = eventSettings.getProperty("RopEnableCommand", true);
      ROP_VOICE_COMMAND = eventSettings.getProperty("RopVoiceCommand", "rop");
      ROP_SPAWN_REG_MANAGER = eventSettings.getProperty("RopSpawnRegManager", false);
      ROP_REG_MANAGER_ID = eventSettings.getProperty("RopRegManagerId", 31225);
      ROP_REG_MANAGER_LOC = eventSettings.getProperty("RopRegManagerLoc", "83448,148375,-3425,47670");
      ROP_SEND_REG_WINDOW = eventSettings.getProperty("RopSendRegWindow", true);
      ROP_ENCHANT_LIMIT = eventSettings.getProperty("RopEnchantLimit", false);
      ROP_ENCHANT_LIMIT_WEAPON = eventSettings.getProperty("RopEnchantLimitWeapon", 6);
      ROP_ENCHANT_LIMIT_ARMOR = eventSettings.getProperty("RopEnchantLimitArmor", 6);
      ROP_HIDE_NAME = eventSettings.getProperty("RopHideName", false);
      ROP_HIDE_TITLE = eventSettings.getProperty("RopHideTitle", false);
      ROP_HIDE_CLAN_ALY_INFO = eventSettings.getProperty("RopClanAlyInfo", false);
      ROP_HIDE_NAME_COLOR = eventSettings.getProperty("RopHideNameColor", false);
      ROP_HIDE_TITLE_COLOR = eventSettings.getProperty("RopHideTitleColor", false);
   }
}
