package events.BossHunting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigBossHunting {
   private static Logger _log = LoggerFactory.getLogger(ConfigBossHunting.class);
   public static final String CONFIG_FILE = "config/events/boss_hunting.properties";
   public static int[] BOSS_HUNTING_KILL_REWARD;
   public static int[] BOSS_HUNTING_REWARD_FINAL;
   public static int[] BOSS_HUNTING_REWARD_FINAL_PREMIUM;
   public static int[] BOSS_HUNTING_REWARD_DRAW;
   public static int[] BOSS_HUNTING_REWARD_DRAW_PREMIUM;
   public static boolean BOSS_HUNTING_TOP_KILLER_ENABLE;
   public static Map<Integer, int[]> BOSS_HUNTING_TOP_KILLER_REWARD = new HashMap();
   public static Map<Integer, int[]> BOSS_HUNTING_TOP_KILLER_REWARD_PREMIUM = new HashMap();
   public static boolean BOSS_HUNTING_TOP_KILLER_ANNOUNCE;
   public static boolean BOSS_HUNTING_TOP_DAMAGER_ENABLE;
   public static Map<Integer, int[]> BOSS_HUNTING_TOP_DAMAGER_REWARD = new HashMap();
   public static Map<Integer, int[]> BOSS_HUNTING_TOP_DAMAGER_REWARD_PREMIUM = new HashMap();
   public static boolean BOSS_HUNTING_TOP_DAMAGER_ANNOUNCE;
   public static int BOSS_HUNTING_WINNER_MIN_KILLS;
   public static int BOSS_HUNTING_WINNER_MIN_KILLS_PREMIUM;
   public static int[] BOSS_HUNTING_REWARD_LOSERS;
   public static int[] BOSS_HUNTING_REWARD_LOSERS_PREMIUM;
   public static int BOSS_HUNTING_LOSERS_MIN_KILLS;
   public static int BOSS_HUNTING_LOSERS_MIN_KILLS_PREMIUM;
   public static boolean BOSS_HUNTING_IP_RESTRICTION;
   public static boolean BOSS_HUNTING_HWID_RESTRICTION;
   public static boolean BOSS_HUNTING_IN_INSTANCE;
   public static int BOSS_HUNTING_MIN_PLAYERS;
   public static int BOSS_HUNTING_MAX_PLAYERS;
   public static boolean BOSS_HUNTING_CANCEL_ALL_BUFF;
   public static boolean BOSS_HUNTING_MULT_REWARD_FOR_KILL_BY_LVL;
   public static int BOSS_HUNTING_TIME_TO_START;
   public static int BOSS_HUNTING_RESURRECT_DELAY;
   public static int BOSS_HUNTING_TIME_PARALYZE;
   public static int BOSS_HUNTING_BATTLE_DURATION;
   public static int[] BOSS_HUNTING_RETURN_POINT;
   public static boolean BOSS_HUNTING_SHOW_KILLS;
   public static boolean BOSS_HUNTING_ENABLE_CUSTOM_ITEMS;
   public static int BOSS_HUNTING_CUSTOM_ITEMS_ENCHANT;
   public static Map<Integer, List<Integer>> BOSS_HUNTING_CUSTOM_ITEMS;
   public static boolean BOSS_HUNTING_ALLOW_CALENDAR_DAY;
   public static int[] BOSS_HUNTING_START_TIME;
   public static String BOSS_HUNTING_ZONE_NAME;
   public static List<Location> BOSS_HUNTING_BLUE_TEAM_LOCS = new ArrayList();
   public static List<Location> BOSS_HUNTING_RED_TEAM_LOCS = new ArrayList();
   public static List<Location> BOSS_HUNTING_BLUE_TEAM_RES_LOCS = new ArrayList();
   public static List<Location> BOSS_HUNTING_RED_TEAM_RES_LOCS = new ArrayList();
   public static String BOSS_HUNTING_CLEAR_LOC;
   public static int BOSS_HUNTING_TIME_BACK;
   public static boolean BOSS_HUNTING_STATUS_INFO_ON_DEATH;
   public static int[] BOSS_HUNTING_RESTRICTED_ITEMS;
   public static int[] BOSS_HUNTING_RESTRICTED_SKILLS;
   public static int[] BOSS_HUNTING_RESTRICTED_CLASS_IDS;
   public static boolean BOSS_HUNTING_DISABLE_HERO_SKILLS;
   public static int[] BOSS_HUNTING_RESTRICTED_SUMMONS;
   public static boolean BOSS_HUNTING_ENCHANT_LIMIT;
   public static int BOSS_HUNTING_ENCHANT_LIMIT_WEAPON;
   public static int BOSS_HUNTING_ENCHANT_LIMIT_ARMOR;
   public static int[] BOSS_HUNTING_BUFFS_FIGHTER;
   public static int[] BOSS_HUNTING_BUFFS_MAGE;
   public static long BOSS_HUNTING_ALT_BUFFS_DURATION;
   public static int BOSS_HUNTING_RAID_BOSS_ID;
   public static String BOSS_HUNTING_RAID_BOSS_SPAWN_POINT;
   public static boolean BOSS_HUNTING_LAST_HIT_WIN;
   public static boolean BOSS_HUNTING_CALCULATE_DAMAGE_END;
   public static boolean BOSS_HUNTING_BROADCAST_TIMER;
   public static boolean BOSS_HUNTING_SHOW_DAMAGE_IN_TIMER;
   public static boolean BOSS_HUNTING_SPAWN_REG_MANAGER;
   public static int BOSS_HUNTING_REG_MANAGER_ID;
   public static String BOSS_HUNTING_REG_MANAGER_LOC;
   public static boolean BOSS_HUNTING_ENABLE_COMMAND;
   public static String BOSS_HUNTING_VOICE_COMMAND;
   public static boolean BOSS_HUNTING_SEND_REG_WINDOW;
   public static boolean BOSS_HUNTING_ALLOW_OBSERVER;
   public static List<String> BOSS_HUNTING_OBSERVER_COORDS = new ArrayList();
   public static boolean BOSS_HUNTING_HIDE_NAME;
   public static boolean BOSS_HUNTING_HIDE_TITLE;
   public static boolean BOSS_HUNTING_HIDE_CLAN_ALY_INFO;
   public static boolean BOSS_HUNTING_HIDE_NAME_COLOR;
   public static boolean BOSS_HUNTING_HIDE_TITLE_COLOR;

   public static void load() {
      ExProperties eventSettings = Config.load("config/events/boss_hunting.properties");
      BOSS_HUNTING_KILL_REWARD = eventSettings.getProperty("BossHuntingKillReward", new int[]{57, 10000});
      BOSS_HUNTING_MULT_REWARD_FOR_KILL_BY_LVL = eventSettings.getProperty("BossHuntingMultRewardForKillByLvl", false);
      BOSS_HUNTING_REWARD_FINAL = eventSettings.getProperty("BossHuntingRewardFinal", new int[]{57, 10000});
      BOSS_HUNTING_REWARD_FINAL_PREMIUM = eventSettings.getProperty("BossHuntingRewardFinalPremium", new int[]{57, 20000});
      BOSS_HUNTING_REWARD_DRAW = eventSettings.getProperty("BossHuntingRewardDraw", new int[0]);
      BOSS_HUNTING_REWARD_DRAW_PREMIUM = eventSettings.getProperty("BossHuntingRewardDrawPremium", new int[0]);
      BOSS_HUNTING_TOP_KILLER_ENABLE = eventSettings.getProperty("BossHuntingTopKillerEnable", false);
      String[] rewardsByPlace = eventSettings.getProperty("BossHuntingTopKillerReward", "").split(";");
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

         BOSS_HUNTING_TOP_KILLER_REWARD.put(place, items);
      }

      rewardsByPlacePremium = eventSettings.getProperty("BossHuntingTopKillerRewardPremium", "").split(";");
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

         BOSS_HUNTING_TOP_KILLER_REWARD_PREMIUM.put(place, items);
      }

      BOSS_HUNTING_TOP_KILLER_ANNOUNCE = eventSettings.getProperty("BossHuntingTopKillerAnnounce", false);
      BOSS_HUNTING_TOP_DAMAGER_ENABLE = eventSettings.getProperty("BossHuntingTopOpenerEnable", false);
      rewardsByPlace = eventSettings.getProperty("BossHuntingTopOpenerReward", "").split(";");
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

         BOSS_HUNTING_TOP_DAMAGER_REWARD.put(place, items);
      }

      rewardsByPlacePremium = eventSettings.getProperty("BossHuntingTopOpenerRewardPremium", "").split(";");
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

         BOSS_HUNTING_TOP_DAMAGER_REWARD_PREMIUM.put(place, items);
      }

      BOSS_HUNTING_TOP_DAMAGER_ANNOUNCE = eventSettings.getProperty("BossHuntingTopOpenerAnnounce", false);
      BOSS_HUNTING_WINNER_MIN_KILLS = eventSettings.getProperty("BossHuntingWinnerMinKills", 0);
      BOSS_HUNTING_WINNER_MIN_KILLS_PREMIUM = eventSettings.getProperty("BossHuntingWinnerMinKillsPremium", 0);
      BOSS_HUNTING_REWARD_LOSERS = eventSettings.getProperty("BossHuntingRewardLosers", new int[0]);
      BOSS_HUNTING_REWARD_LOSERS_PREMIUM = eventSettings.getProperty("BossHuntingRewardLosersPremium", new int[0]);
      BOSS_HUNTING_LOSERS_MIN_KILLS = eventSettings.getProperty("BossHuntingLosersMinKills", 0);
      BOSS_HUNTING_LOSERS_MIN_KILLS_PREMIUM = eventSettings.getProperty("BossHuntingLosersMinKillsPremium", 0);
      BOSS_HUNTING_IP_RESTRICTION = eventSettings.getProperty("BossHuntingIpRestriction", false);
      BOSS_HUNTING_HWID_RESTRICTION = eventSettings.getProperty("BossHuntingHwidRestriction", false);
      BOSS_HUNTING_IN_INSTANCE = eventSettings.getProperty("BossHuntingInInstance", true);
      BOSS_HUNTING_MIN_PLAYERS = eventSettings.getProperty("BossHuntingMinPlayers", 4);
      BOSS_HUNTING_MAX_PLAYERS = eventSettings.getProperty("BossHuntingMaxPlayers", 54);
      BOSS_HUNTING_CANCEL_ALL_BUFF = eventSettings.getProperty("BossHuntingCancelAllBuff", false);
      BOSS_HUNTING_TIME_TO_START = eventSettings.getProperty("BossHuntingTimeToStart", 3);
      BOSS_HUNTING_RESURRECT_DELAY = eventSettings.getProperty("BossHuntingResurrectDelay", 20);
      BOSS_HUNTING_TIME_PARALYZE = eventSettings.getProperty("BossHuntingTimeParalyze", 60);
      BOSS_HUNTING_BATTLE_DURATION = eventSettings.getProperty("BossHuntingBattleDuration", 10);
      BOSS_HUNTING_RETURN_POINT = eventSettings.getProperty("BossHuntingReturnPoint", new int[0]);
      BOSS_HUNTING_ALLOW_CALENDAR_DAY = eventSettings.getProperty("BossHuntingAllowCalendarDay", false);
      BOSS_HUNTING_START_TIME = eventSettings.getProperty("BossHuntingStartTime", new int[]{18, 30, 6});
      BOSS_HUNTING_ZONE_NAME = eventSettings.getProperty("BossHuntingZoneName", "[colosseum_battle]");
      blueTeamLocs = eventSettings.getProperty("BossHuntingBlueTeamLocs", "150545,46734,-3410").split(";");
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
         BOSS_HUNTING_BLUE_TEAM_LOCS.add(new Location(x, x, x));
      }

      redTeamLocs = eventSettings.getProperty("BossHuntingRedTeamLocs", "148386,46747,-3410").split(";");
      String[] blueTeamResLocs = redTeamLocs;
      var20 = redTeamLocs.length;

      int x;
      for(place = 0; place < var20; ++place) {
         String loc = blueTeamResLocs[place];
         s = loc.split(",");
         x = Integer.parseInt(s[0]);
         x = Integer.parseInt(s[1]);
         x = Integer.parseInt(s[2]);
         BOSS_HUNTING_RED_TEAM_LOCS.add(new Location(x, x, x));
      }

      blueTeamResLocs = eventSettings.getProperty("BossHuntingBlueTeamResLocs", "150545,46734,-3410").split(";");
      redTeamResLocs = blueTeamResLocs;
      place = blueTeamResLocs.length;

      int y;
      for(place = 0; place < place; ++place) {
         String loc = redTeamResLocs[place];
         String[] s = loc.split(",");
         x = Integer.parseInt(s[0]);
         x = Integer.parseInt(s[1]);
         y = Integer.parseInt(s[2]);
         BOSS_HUNTING_BLUE_TEAM_RES_LOCS.add(new Location(x, x, y));
      }

      redTeamResLocs = eventSettings.getProperty("BossHuntingRedTeamResLocs", "148386,46747,-3410").split(";");
      coords = redTeamResLocs;
      place = redTeamResLocs.length;

      for(x = 0; x < place; ++x) {
         String loc = coords[x];
         String[] s = loc.split(",");
         x = Integer.parseInt(s[0]);
         y = Integer.parseInt(s[1]);
         int z = Integer.parseInt(s[2]);
         BOSS_HUNTING_RED_TEAM_RES_LOCS.add(new Location(x, y, z));
      }

      BOSS_HUNTING_CLEAR_LOC = eventSettings.getProperty("BossHuntingClearLoc", "147451,46728,-3410");
      BOSS_HUNTING_SHOW_KILLS = eventSettings.getProperty("BossHuntingShowKills", false);
      BOSS_HUNTING_ENABLE_CUSTOM_ITEMS = eventSettings.getProperty("BossHuntingEnableCustomItems", false);
      BOSS_HUNTING_CUSTOM_ITEMS_ENCHANT = eventSettings.getProperty("BossHuntingCustomItemsEnchant", 0);
      BOSS_HUNTING_TIME_BACK = eventSettings.getProperty("BossHuntingTimeBack", 30);
      BOSS_HUNTING_STATUS_INFO_ON_DEATH = eventSettings.getProperty("BossHuntingStatusInfoOnDeath", true);
      BOSS_HUNTING_RESTRICTED_ITEMS = eventSettings.getProperty("BossHuntingRestrictedItems", new int[0]);
      BOSS_HUNTING_RESTRICTED_SKILLS = eventSettings.getProperty("BossHuntingRestrictedSkills", new int[0]);
      BOSS_HUNTING_RESTRICTED_CLASS_IDS = eventSettings.getProperty("BossHuntingRestrictedClassIds", new int[0]);
      BOSS_HUNTING_DISABLE_HERO_SKILLS = eventSettings.getProperty("BossHuntingDisableHeroSkills", true);
      BOSS_HUNTING_RESTRICTED_SUMMONS = eventSettings.getProperty("BossHuntingRestrictedSummons", new int[0]);
      BOSS_HUNTING_ENCHANT_LIMIT = eventSettings.getProperty("BossHuntingEnchantLimit", false);
      BOSS_HUNTING_ENCHANT_LIMIT_WEAPON = eventSettings.getProperty("BossHuntingEnchantLimitWeapon", 6);
      BOSS_HUNTING_ENCHANT_LIMIT_ARMOR = eventSettings.getProperty("BossHuntingEnchantLimitArmor", 6);
      BOSS_HUNTING_BUFFS_FIGHTER = eventSettings.getProperty("BossHuntingBuffsFighter", new int[0]);
      BOSS_HUNTING_BUFFS_MAGE = eventSettings.getProperty("BossHuntingBuffsMage", new int[0]);
      BOSS_HUNTING_ALT_BUFFS_DURATION = eventSettings.getProperty("BossHuntingAltBuffsDuration", 0L) * 1000L;
      BOSS_HUNTING_RAID_BOSS_ID = eventSettings.getProperty("BossHuntingRaidBossId", 50031);
      BOSS_HUNTING_RAID_BOSS_SPAWN_POINT = eventSettings.getProperty("BossHuntingRaidBossSpawnPoint", "150545,46734,-3410");
      BOSS_HUNTING_LAST_HIT_WIN = eventSettings.getProperty("BossHuntingLastHitWin", false);
      BOSS_HUNTING_CALCULATE_DAMAGE_END = eventSettings.getProperty("BossHuntingCalculateDamageEnd", false);
      BOSS_HUNTING_BROADCAST_TIMER = eventSettings.getProperty("BossHuntingBroadcastTimer", true);
      BOSS_HUNTING_SHOW_DAMAGE_IN_TIMER = eventSettings.getProperty("BossHuntingShowDamageInTimer", true);
      BOSS_HUNTING_SPAWN_REG_MANAGER = eventSettings.getProperty("BossHuntingSpawnRegManager", false);
      BOSS_HUNTING_REG_MANAGER_ID = eventSettings.getProperty("BossHuntingRegManagerId", 31225);
      BOSS_HUNTING_REG_MANAGER_LOC = eventSettings.getProperty("BossHuntingRegManagerLoc", "83448,148375,-3425,47670");
      BOSS_HUNTING_ENABLE_COMMAND = eventSettings.getProperty("BossHuntingEnableCommand", true);
      BOSS_HUNTING_VOICE_COMMAND = eventSettings.getProperty("BossHuntingVoiceCommand", "bosshunt");
      BOSS_HUNTING_SEND_REG_WINDOW = eventSettings.getProperty("BossHuntingSendRegWindow", true);
      BOSS_HUNTING_ALLOW_OBSERVER = eventSettings.getProperty("BossHuntingAllowObserver", false);
      coords = eventSettings.getProperty("BossHuntingObserverCoords", "").split(";");
      s = coords;
      x = coords.length;

      for(x = 0; x < x; ++x) {
         String coord = s[x];
         BOSS_HUNTING_OBSERVER_COORDS.add(coord);
      }

      BOSS_HUNTING_HIDE_NAME = eventSettings.getProperty("BossHuntingHideName", false);
      BOSS_HUNTING_HIDE_TITLE = eventSettings.getProperty("BossHuntingHideTitle", false);
      BOSS_HUNTING_HIDE_CLAN_ALY_INFO = eventSettings.getProperty("BossHuntingClanAlyInfo", false);
      BOSS_HUNTING_HIDE_NAME_COLOR = eventSettings.getProperty("BossHuntingHideNameColor", false);
      BOSS_HUNTING_HIDE_TITLE_COLOR = eventSettings.getProperty("BossHuntingHideTitleColor", false);
   }
}
