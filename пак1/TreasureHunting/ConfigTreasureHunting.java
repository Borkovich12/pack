package events.TreasureHunting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigTreasureHunting {
   private static Logger _log = LoggerFactory.getLogger(ConfigTreasureHunting.class);
   public static final String CONFIG_FILE = "config/events/treasure_hunting.properties";
   public static int[] TREASURE_HUNTING_KILL_REWARD;
   public static int[] TREASURE_HUNTING_REWARD_FINAL;
   public static int[] TREASURE_HUNTING_REWARD_FINAL_PREMIUM;
   public static int[] TREASURE_HUNTING_REWARD_DRAW;
   public static int[] TREASURE_HUNTING_REWARD_DRAW_PREMIUM;
   public static boolean TREASURE_HUNTING_TOP_KILLER_ENABLE;
   public static Map<Integer, int[]> TREASURE_HUNTING_TOP_KILLER_REWARD = new HashMap();
   public static Map<Integer, int[]> TREASURE_HUNTING_TOP_KILLER_REWARD_PREMIUM = new HashMap();
   public static boolean TREASURE_HUNTING_TOP_KILLER_ANNOUNCE;
   public static boolean TREASURE_HUNTING_TOP_OPENER_ENABLE;
   public static Map<Integer, int[]> TREASURE_HUNTING_TOP_OPENER_REWARD = new HashMap();
   public static Map<Integer, int[]> TREASURE_HUNTING_TOP_OPENER_REWARD_PREMIUM = new HashMap();
   public static boolean TREASURE_HUNTING_TOP_OPENER_ANNOUNCE;
   public static int TREASURE_HUNTING_WINNER_MIN_KILLS;
   public static int TREASURE_HUNTING_WINNER_MIN_KILLS_PREMIUM;
   public static int[] TREASURE_HUNTING_REWARD_LOSERS;
   public static int[] TREASURE_HUNTING_REWARD_LOSERS_PREMIUM;
   public static int TREASURE_HUNTING_LOSERS_MIN_KILLS;
   public static int TREASURE_HUNTING_LOSERS_MIN_KILLS_PREMIUM;
   public static boolean TREASURE_HUNTING_IP_RESTRICTION;
   public static boolean TREASURE_HUNTING_HWID_RESTRICTION;
   public static boolean TREASURE_HUNTING_IN_INSTANCE;
   public static int TREASURE_HUNTING_MIN_PLAYERS;
   public static int TREASURE_HUNTING_MAX_PLAYERS;
   public static boolean TREASURE_HUNTING_CANCEL_ALL_BUFF;
   public static boolean TREASURE_HUNTING_MULT_REWARD_FOR_KILL_BY_LVL;
   public static int TREASURE_HUNTING_TIME_TO_START;
   public static int TREASURE_HUNTING_RESURRECT_DELAY;
   public static int TREASURE_HUNTING_TIME_PARALYZE;
   public static int TREASURE_HUNTING_BATTLE_DURATION;
   public static int[] TREASURE_HUNTING_RETURN_POINT;
   public static boolean TREASURE_HUNTING_SHOW_KILLS;
   public static boolean TREASURE_HUNTING_SHOW_OPEN_CHEST;
   public static boolean TREASURE_HUNTING_ENABLE_CUSTOM_ITEMS;
   public static int TREASURE_HUNTING_CUSTOM_ITEMS_ENCHANT;
   public static Map<Integer, List<Integer>> TREASURE_HUNTING_CUSTOM_ITEMS;
   public static boolean TREASURE_HUNTING_ALLOW_CALENDAR_DAY;
   public static int[] TREASURE_HUNTING_START_TIME;
   public static String TREASURE_HUNTING_ZONE_NAME;
   public static List<Location> TREASURE_HUNTING_BLUE_TEAM_LOCS = new ArrayList();
   public static List<Location> TREASURE_HUNTING_RED_TEAM_LOCS = new ArrayList();
   public static List<Location> TREASURE_HUNTING_BLUE_TEAM_RES_LOCS = new ArrayList();
   public static List<Location> TREASURE_HUNTING_RED_TEAM_RES_LOCS = new ArrayList();
   public static String TREASURE_HUNTING_CLEAR_LOC;
   public static int TREASURE_HUNTING_TIME_BACK;
   public static boolean TREASURE_HUNTING_STATUS_INFO_ON_DEATH;
   public static int[] TREASURE_HUNTING_RESTRICTED_ITEMS;
   public static int[] TREASURE_HUNTING_RESTRICTED_SKILLS;
   public static int[] TREASURE_HUNTING_RESTRICTED_CLASS_IDS;
   public static boolean TREASURE_HUNTING_DISABLE_HERO_SKILLS;
   public static int[] TREASURE_HUNTING_RESTRICTED_SUMMONS;
   public static boolean TREASURE_HUNTING_ENCHANT_LIMIT;
   public static int TREASURE_HUNTING_ENCHANT_LIMIT_WEAPON;
   public static int TREASURE_HUNTING_ENCHANT_LIMIT_ARMOR;
   public static int[] TREASURE_HUNTING_BUFFS_FIGHTER;
   public static int[] TREASURE_HUNTING_BUFFS_MAGE;
   public static long TREASURE_HUNTING_ALT_BUFFS_DURATION;
   public static String TREASURE_HUNTING_CHEST_SPAWN_GROUP;
   public static double TREASURE_HUNTING_CHEST_OPENED_CHANCE;
   public static int TREASURE_HUNTING_CHEST_KEY_ID;
   public static boolean TREASURE_HUNTING_REMOVE_KEYS;
   public static boolean TREASURE_HUNTING_BROADCAST_TIMER;
   public static boolean TREASURE_HUNTING_SPAWN_REG_MANAGER;
   public static int TREASURE_HUNTING_REG_MANAGER_ID;
   public static String TREASURE_HUNTING_REG_MANAGER_LOC;
   public static boolean TREASURE_HUNTING_ENABLE_COMMAND;
   public static String TREASURE_HUNTING_VOICE_COMMAND;
   public static boolean TREASURE_HUNTING_SEND_REG_WINDOW;
   public static boolean TREASURE_HUNTING_ALLOW_OBSERVER;
   public static List<String> TREASURE_HUNTING_OBSERVER_COORDS = new ArrayList();
   public static boolean TREASURE_HUNTING_HIDE_NAME;
   public static boolean TREASURE_HUNTING_HIDE_TITLE;
   public static boolean TREASURE_HUNTING_HIDE_CLAN_ALY_INFO;
   public static boolean TREASURE_HUNTING_HIDE_NAME_COLOR;
   public static boolean TREASURE_HUNTING_HIDE_TITLE_COLOR;

   public static void load() {
      ExProperties eventSettings = Config.load("config/events/treasure_hunting.properties");
      TREASURE_HUNTING_KILL_REWARD = eventSettings.getProperty("TreasureHuntingKillReward", new int[]{57, 10000});
      TREASURE_HUNTING_MULT_REWARD_FOR_KILL_BY_LVL = eventSettings.getProperty("TreasureHuntingMultRewardForKillByLvl", false);
      TREASURE_HUNTING_REWARD_FINAL = eventSettings.getProperty("TreasureHuntingRewardFinal", new int[]{57, 10000});
      TREASURE_HUNTING_REWARD_FINAL_PREMIUM = eventSettings.getProperty("TreasureHuntingRewardFinalPremium", new int[]{57, 20000});
      TREASURE_HUNTING_REWARD_DRAW = eventSettings.getProperty("TreasureHuntingRewardDraw", new int[0]);
      TREASURE_HUNTING_REWARD_DRAW_PREMIUM = eventSettings.getProperty("TreasureHuntingRewardDrawPremium", new int[0]);
      TREASURE_HUNTING_TOP_KILLER_ENABLE = eventSettings.getProperty("TreasureHuntingTopKillerEnable", false);
      String[] rewardsByPlace = eventSettings.getProperty("TreasureHuntingTopKillerReward", "").split(";");
      String[] rewardsByPlacePremium = rewardsByPlace;
      int var3 = rewardsByPlace.length;

      int var4;
      String[] redTeamResLocs;
      int place;
      String[] s;
      int x;
      for(var4 = 0; var4 < var3; ++var4) {
         String r = rewardsByPlacePremium[var4];
         redTeamResLocs = r.split(":");
         place = Integer.parseInt(redTeamResLocs[0]);
         s = redTeamResLocs[1].split(",");
         int[] items = new int[s.length];

         for(x = 0; x < s.length; ++x) {
            items[x] = Integer.parseInt(s[x]);
         }

         TREASURE_HUNTING_TOP_KILLER_REWARD.put(place, items);
      }

      rewardsByPlacePremium = eventSettings.getProperty("TreasureHuntingTopKillerRewardPremium", "").split(";");
      String[] blueTeamLocs = rewardsByPlacePremium;
      var4 = rewardsByPlacePremium.length;

      int x;
      int var16;
      String r;
      String[] coords;
      int place;
      String[] s;
      int[] items;
      for(var16 = 0; var16 < var4; ++var16) {
         r = blueTeamLocs[var16];
         coords = r.split(":");
         place = Integer.parseInt(coords[0]);
         s = coords[1].split(",");
         items = new int[s.length];

         for(x = 0; x < s.length; ++x) {
            items[x] = Integer.parseInt(s[x]);
         }

         TREASURE_HUNTING_TOP_KILLER_REWARD_PREMIUM.put(place, items);
      }

      TREASURE_HUNTING_TOP_KILLER_ANNOUNCE = eventSettings.getProperty("TreasureHuntingTopKillerAnnounce", false);
      TREASURE_HUNTING_TOP_OPENER_ENABLE = eventSettings.getProperty("TreasureHuntingTopOpenerEnable", false);
      rewardsByPlace = eventSettings.getProperty("TreasureHuntingTopOpenerReward", "").split(";");
      blueTeamLocs = rewardsByPlace;
      var4 = rewardsByPlace.length;

      for(var16 = 0; var16 < var4; ++var16) {
         r = blueTeamLocs[var16];
         coords = r.split(":");
         place = Integer.parseInt(coords[0]);
         s = coords[1].split(",");
         items = new int[s.length];

         for(x = 0; x < s.length; ++x) {
            items[x] = Integer.parseInt(s[x]);
         }

         TREASURE_HUNTING_TOP_OPENER_REWARD.put(place, items);
      }

      rewardsByPlacePremium = eventSettings.getProperty("TreasureHuntingTopOpenerRewardPremium", "").split(";");
      blueTeamLocs = rewardsByPlacePremium;
      var4 = rewardsByPlacePremium.length;

      for(var16 = 0; var16 < var4; ++var16) {
         r = blueTeamLocs[var16];
         coords = r.split(":");
         place = Integer.parseInt(coords[0]);
         s = coords[1].split(",");
         items = new int[s.length];

         for(x = 0; x < s.length; ++x) {
            items[x] = Integer.parseInt(s[x]);
         }

         TREASURE_HUNTING_TOP_OPENER_REWARD_PREMIUM.put(place, items);
      }

      TREASURE_HUNTING_TOP_OPENER_ANNOUNCE = eventSettings.getProperty("TreasureHuntingTopOpenerAnnounce", false);
      TREASURE_HUNTING_WINNER_MIN_KILLS = eventSettings.getProperty("TreasureHuntingWinnerMinKills", 0);
      TREASURE_HUNTING_WINNER_MIN_KILLS_PREMIUM = eventSettings.getProperty("TreasureHuntingWinnerMinKillsPremium", 0);
      TREASURE_HUNTING_REWARD_LOSERS = eventSettings.getProperty("TreasureHuntingRewardLosers", new int[0]);
      TREASURE_HUNTING_REWARD_LOSERS_PREMIUM = eventSettings.getProperty("TreasureHuntingRewardLosersPremium", new int[0]);
      TREASURE_HUNTING_LOSERS_MIN_KILLS = eventSettings.getProperty("TreasureHuntingLosersMinKills", 0);
      TREASURE_HUNTING_LOSERS_MIN_KILLS_PREMIUM = eventSettings.getProperty("TreasureHuntingLosersMinKillsPremium", 0);
      TREASURE_HUNTING_IP_RESTRICTION = eventSettings.getProperty("TreasureHuntingIpRestriction", false);
      TREASURE_HUNTING_HWID_RESTRICTION = eventSettings.getProperty("TreasureHuntingHwidRestriction", false);
      TREASURE_HUNTING_IN_INSTANCE = eventSettings.getProperty("TreasureHuntingInInstance", true);
      TREASURE_HUNTING_MIN_PLAYERS = eventSettings.getProperty("TreasureHuntingMinPlayers", 4);
      TREASURE_HUNTING_MAX_PLAYERS = eventSettings.getProperty("TreasureHuntingMaxPlayers", 54);
      TREASURE_HUNTING_CANCEL_ALL_BUFF = eventSettings.getProperty("TreasureHuntingCancelAllBuff", false);
      TREASURE_HUNTING_TIME_TO_START = eventSettings.getProperty("TreasureHuntingTimeToStart", 3);
      TREASURE_HUNTING_RESURRECT_DELAY = eventSettings.getProperty("TreasureHuntingResurrectDelay", 20);
      TREASURE_HUNTING_TIME_PARALYZE = eventSettings.getProperty("TreasureHuntingTimeParalyze", 60);
      TREASURE_HUNTING_BATTLE_DURATION = eventSettings.getProperty("TreasureHuntingBattleDuration", 10);
      TREASURE_HUNTING_RETURN_POINT = eventSettings.getProperty("TreasureHuntingReturnPoint", new int[0]);
      TREASURE_HUNTING_ALLOW_CALENDAR_DAY = eventSettings.getProperty("TreasureHuntingAllowCalendarDay", false);
      TREASURE_HUNTING_START_TIME = eventSettings.getProperty("TreasureHuntingStartTime", new int[]{18, 30, 6});
      TREASURE_HUNTING_ZONE_NAME = eventSettings.getProperty("TreasureHuntingZoneName", "[colosseum_battle]");
      blueTeamLocs = eventSettings.getProperty("TreasureHuntingBlueTeamLocs", "150545,46734,-3410").split(";");
      String[] redTeamLocs = blueTeamLocs;
      var16 = blueTeamLocs.length;

      int var20;
      int x;
      for(var20 = 0; var20 < var16; ++var20) {
         String loc = redTeamLocs[var20];
         s = loc.split(",");
         x = Integer.parseInt(s[0]);
         x = Integer.parseInt(s[1]);
         x = Integer.parseInt(s[2]);
         TREASURE_HUNTING_BLUE_TEAM_LOCS.add(new Location(x, x, x));
      }

      redTeamLocs = eventSettings.getProperty("TreasureHuntingRedTeamLocs", "148386,46747,-3410").split(";");
      String[] blueTeamResLocs = redTeamLocs;
      var20 = redTeamLocs.length;

      int x;
      for(place = 0; place < var20; ++place) {
         String loc = blueTeamResLocs[place];
         s = loc.split(",");
         x = Integer.parseInt(s[0]);
         x = Integer.parseInt(s[1]);
         x = Integer.parseInt(s[2]);
         TREASURE_HUNTING_RED_TEAM_LOCS.add(new Location(x, x, x));
      }

      blueTeamResLocs = eventSettings.getProperty("TreasureHuntingBlueTeamResLocs", "150545,46734,-3410").split(";");
      redTeamResLocs = blueTeamResLocs;
      place = blueTeamResLocs.length;

      int y;
      for(place = 0; place < place; ++place) {
         String loc = redTeamResLocs[place];
         String[] s = loc.split(",");
         x = Integer.parseInt(s[0]);
         x = Integer.parseInt(s[1]);
         y = Integer.parseInt(s[2]);
         TREASURE_HUNTING_BLUE_TEAM_RES_LOCS.add(new Location(x, x, y));
      }

      redTeamResLocs = eventSettings.getProperty("TreasureHuntingRedTeamResLocs", "148386,46747,-3410").split(";");
      coords = redTeamResLocs;
      place = redTeamResLocs.length;

      for(x = 0; x < place; ++x) {
         String loc = coords[x];
         String[] s = loc.split(",");
         x = Integer.parseInt(s[0]);
         y = Integer.parseInt(s[1]);
         int z = Integer.parseInt(s[2]);
         TREASURE_HUNTING_RED_TEAM_RES_LOCS.add(new Location(x, y, z));
      }

      TREASURE_HUNTING_CLEAR_LOC = eventSettings.getProperty("TreasureHuntingClearLoc", "147451,46728,-3410");
      TREASURE_HUNTING_SHOW_KILLS = eventSettings.getProperty("TreasureHuntingShowKills", false);
      TREASURE_HUNTING_SHOW_OPEN_CHEST = eventSettings.getProperty("TreasureHuntingShowOpenChest", false);
      TREASURE_HUNTING_ENABLE_CUSTOM_ITEMS = eventSettings.getProperty("TreasureHuntingEnableCustomItems", false);
      TREASURE_HUNTING_CUSTOM_ITEMS_ENCHANT = eventSettings.getProperty("TreasureHuntingCustomItemsEnchant", 0);
      TREASURE_HUNTING_TIME_BACK = eventSettings.getProperty("TreasureHuntingTimeBack", 30);
      TREASURE_HUNTING_STATUS_INFO_ON_DEATH = eventSettings.getProperty("TreasureHuntingStatusInfoOnDeath", true);
      TREASURE_HUNTING_RESTRICTED_ITEMS = eventSettings.getProperty("TreasureHuntingRestrictedItems", new int[0]);
      TREASURE_HUNTING_RESTRICTED_SKILLS = eventSettings.getProperty("TreasureHuntingRestrictedSkills", new int[0]);
      TREASURE_HUNTING_RESTRICTED_CLASS_IDS = eventSettings.getProperty("TreasureHuntingRestrictedClassIds", new int[0]);
      TREASURE_HUNTING_DISABLE_HERO_SKILLS = eventSettings.getProperty("TreasureHuntingDisableHeroSkills", true);
      TREASURE_HUNTING_RESTRICTED_SUMMONS = eventSettings.getProperty("TreasureHuntingRestrictedSummons", new int[0]);
      TREASURE_HUNTING_ENCHANT_LIMIT = eventSettings.getProperty("TreasureHuntingEnchantLimit", false);
      TREASURE_HUNTING_ENCHANT_LIMIT_WEAPON = eventSettings.getProperty("TreasureHuntingEnchantLimitWeapon", 6);
      TREASURE_HUNTING_ENCHANT_LIMIT_ARMOR = eventSettings.getProperty("TreasureHuntingEnchantLimitArmor", 6);
      TREASURE_HUNTING_BUFFS_FIGHTER = eventSettings.getProperty("TreasureHuntingBuffsFighter", new int[0]);
      TREASURE_HUNTING_BUFFS_MAGE = eventSettings.getProperty("TreasureHuntingBuffsMage", new int[0]);
      TREASURE_HUNTING_ALT_BUFFS_DURATION = eventSettings.getProperty("TreasureHuntingAltBuffsDuration", 0L) * 1000L;
      TREASURE_HUNTING_CHEST_SPAWN_GROUP = eventSettings.getProperty("TreasureHuntingChestSpawnGroup", "[treasure_hunting_chest]");
      TREASURE_HUNTING_CHEST_OPENED_CHANCE = eventSettings.getProperty("TreasureHuntingChestOpenedChance", 100.0D);
      TREASURE_HUNTING_CHEST_KEY_ID = eventSettings.getProperty("TreasureHuntingChestKeyId", 6672);
      TREASURE_HUNTING_REMOVE_KEYS = eventSettings.getProperty("TreasureHuntingRemoveKeys", true);
      TREASURE_HUNTING_BROADCAST_TIMER = eventSettings.getProperty("TreasureHuntingBroadcastTimer", true);
      TREASURE_HUNTING_SPAWN_REG_MANAGER = eventSettings.getProperty("TreasureHuntingSpawnRegManager", false);
      TREASURE_HUNTING_REG_MANAGER_ID = eventSettings.getProperty("TreasureHuntingRegManagerId", 31225);
      TREASURE_HUNTING_REG_MANAGER_LOC = eventSettings.getProperty("TreasureHuntingRegManagerLoc", "83448,148375,-3425,47670");
      TREASURE_HUNTING_ENABLE_COMMAND = eventSettings.getProperty("TreasureHuntingEnableCommand", true);
      TREASURE_HUNTING_VOICE_COMMAND = eventSettings.getProperty("TreasureHuntingVoiceCommand", "treasure");
      TREASURE_HUNTING_SEND_REG_WINDOW = eventSettings.getProperty("TreasureHuntingSendRegWindow", true);
      TREASURE_HUNTING_ALLOW_OBSERVER = eventSettings.getProperty("TreasureHuntingAllowObserver", false);
      coords = eventSettings.getProperty("TreasureHuntingObserverCoords", "").split(";");
      s = coords;
      x = coords.length;

      for(x = 0; x < x; ++x) {
         String coord = s[x];
         TREASURE_HUNTING_OBSERVER_COORDS.add(coord);
      }

      TREASURE_HUNTING_HIDE_NAME = eventSettings.getProperty("TreasureHuntingHideName", false);
      TREASURE_HUNTING_HIDE_TITLE = eventSettings.getProperty("TreasureHuntingHideTitle", false);
      TREASURE_HUNTING_HIDE_CLAN_ALY_INFO = eventSettings.getProperty("TreasureHuntingClanAlyInfo", false);
      TREASURE_HUNTING_HIDE_NAME_COLOR = eventSettings.getProperty("TreasureHuntingHideNameColor", false);
      TREASURE_HUNTING_HIDE_TITLE_COLOR = eventSettings.getProperty("TreasureHuntingHideTitleColor", false);
   }
}
