package events.CaptureCastle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigCaptureCastle {
   private static Logger _log = LoggerFactory.getLogger(ConfigCaptureCastle.class);
   public static final String CONFIG_FILE = "config/events/capture_castle.properties";
   public static int CAPTURE_CASTLE_Time_Battle;
   public static int CAPTURE_CASTLE_Time_TO_START;
   public static int CAPTURE_CASTLE_Allow_Calendar_Day;
   public static int[] CAPTURE_CASTLE_Time_Start;
   public static int CAPTURE_CASTLE_Time_Paralyze;
   public static boolean CAPTURE_CASTLE_AllowClanSkill;
   public static boolean CAPTURE_CASTLE_AllowHeroSkill;
   public static boolean CAPTURE_CASTLE_DispelTransformation;
   public static boolean CAPTURE_CASTLE_AllowSummons;
   public static boolean CAPTURE_CASTLE_Categories;
   public static boolean CAPTURE_CASTLE_AllowBuffs;
   public static boolean CAPTURE_CASTLE_AllowHwidCheck;
   public static boolean CAPTURE_CASTLE_AllowIpCheck;
   public static String[] CAPTURE_CASTLE_FighterBuffs;
   public static String[] CAPTURE_CASTLE_MageBuffs;
   public static boolean CAPTURE_CASTLE_BuffPlayers;
   public static int[] CAPTURE_CASTLE_Rewards;
   public static int[] CAPTURE_CASTLE_Rewards_Loose;
   public static int[] CAPTURE_CASTLE_Rewards_Tie;
   public static boolean CAPTURE_CASTLE_EnableTopKiller;
   public static Map<Integer, int[]> CAPTURE_CASTLE_TopKillerReward;
   public static boolean CAPTURE_CASTLE_EnableKillsInTitle;
   public static int[] CAPTURE_CASTLE_INCLUDE_ITEMS;
   public static int[] CAPTURE_CASTLE_RESTRICTED_SKILL_IDS;
   public static boolean CAPTURE_CASTLE_ENCHANT_LIMIT;
   public static int CAPTURE_CASTLE_ENCHANT_LIMIT_WEAPON;
   public static int CAPTURE_CASTLE_ENCHANT_LIMIT_ARMOR;
   public static int CAPTURE_CASTLE_BUFF_TIME_MAGE;
   public static int CAPTURE_CASTLE_BUFF_TIME_FIGHTER;
   public static boolean CAPTURE_CASTLE_CAN_PARTY_INVITE;
   public static int CAPTURE_CASTLE_MaxPlayerInTeam;
   public static int CAPTURE_CASTLE_MinPlayerInTeam;
   public static int CAPTURE_CASTLE_Instance;
   public static int CAPTURE_CASTLE_FLAG_ID;
   public static Location CAPTURE_CASTLE_FLAG_LOC;
   public static int[] CAPTURE_CASTLE_DOORS;
   public static String CAPTURE_CASTLE_ZONE;
   public static Location CAPTURE_CASTLE_FIRST_TEAM_SPAWN_LOC;
   public static Location CAPTURE_CASTLE_SECOND_TEAM_SPAWN_LOC;
   public static Location CAPTURE_CASTLE_SPAWN_LOC_OWNER_TEAM;
   public static Location CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_RED_TEAM;
   public static Location CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_BLUE_TEAM;
   public static int CAPTURE_CASTLE_TIME_BACK;
   public static boolean CAPTURE_CASTLE_SPAWN_REG_MANAGER;
   public static int CAPTURE_CASTLE_REG_MANAGER_ID;
   public static String CAPTURE_CASTLE_REG_MANAGER_LOC;
   public static boolean CAPTURE_CASTLE_ENABLE_COMMAND;
   public static String CAPTURE_CASTLE_VOICE_COMMAND;
   public static boolean CAPTURE_CASTLE_SEND_REG_WINDOW;
   public static boolean CAPTURE_CASTLE_RETURN_POINT_ENABLE;
   public static Location CAPTURE_CASTLE_RETURN_POINT;
   public static boolean CAPTURE_CASTLE_RESTORE_DESTROYED_DOORS;
   public static int CAPTURE_CASTLE_RES_DELAY;
   public static boolean CAPTURE_CASTLE_BROADCAST_TIMER;
   public static boolean CAPTURE_CASTLE_HIDE_NAME;
   public static boolean CAPTURE_CASTLE_HIDE_TITLE;
   public static boolean CAPTURE_CASTLE_HIDE_CLAN_ALY_INFO;
   public static boolean CAPTURE_CASTLE_HIDE_NAME_COLOR;
   public static boolean CAPTURE_CASTLE_HIDE_TITLE_COLOR;
   public static boolean CAPTURE_CASTLE_ALLOW_OBSERVER;
   public static List<String> CAPTURE_CASTLE_OBSERVER_COORDS = new ArrayList();

   public static void load() {
      ExProperties eventSettings = Config.load("config/events/capture_castle.properties");
      CAPTURE_CASTLE_Time_Battle = eventSettings.getProperty("CaptureCastleTimeBattle", 5);
      CAPTURE_CASTLE_Time_TO_START = eventSettings.getProperty("CaptureCastleTimeToStart", 5);
      CAPTURE_CASTLE_Allow_Calendar_Day = eventSettings.getProperty("CaptureCastleAllow_Calendar_Day", 0);
      CAPTURE_CASTLE_Time_Start = eventSettings.getProperty("CaptureCastleTime_Start", new int[]{18, 30, 6});
      CAPTURE_CASTLE_Time_Paralyze = eventSettings.getProperty("CaptureCastleTime_Paralyze", 60);
      CAPTURE_CASTLE_AllowClanSkill = eventSettings.getProperty("CaptureCastleAllowClanSkill", false);
      CAPTURE_CASTLE_AllowHeroSkill = eventSettings.getProperty("CaptureCastleAllowHeroSkill", false);
      CAPTURE_CASTLE_AllowSummons = eventSettings.getProperty("CaptureCastleAllowSummons", false);
      CAPTURE_CASTLE_DispelTransformation = eventSettings.getProperty("CaptureCastleDispelTransformation", false);
      CAPTURE_CASTLE_Rewards = eventSettings.getProperty("CaptureCastleRewards", new int[]{57, 10000});
      CAPTURE_CASTLE_Rewards_Loose = eventSettings.getProperty("CaptureCastleRewardsLoose", new int[]{57, 10000});
      CAPTURE_CASTLE_Rewards_Tie = eventSettings.getProperty("CaptureCastleRewardsTie", new int[]{57, 10000});
      CAPTURE_CASTLE_EnableTopKiller = eventSettings.getProperty("CaptureCastleEnableTopKiller", true);
      CAPTURE_CASTLE_TopKillerReward = new HashMap();
      String[] topKillerRewards = eventSettings.getProperty("CaptureCastleTopKillerRewards", "1:4037,100;2:4037,50;3:4037,20").split(";");
      String[] coords = topKillerRewards;
      int var3 = topKillerRewards.length;

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         String reward = coords[var4];
         if (!reward.trim().isEmpty()) {
            String[] rewardParts = reward.split(":");
            int place = Integer.parseInt(rewardParts[0]);
            String[] itemStrs = rewardParts[1].split(",");
            int[] items = new int[itemStrs.length];

            for(int i = 0; i < itemStrs.length; ++i) {
               items[i] = Integer.parseInt(itemStrs[i]);
            }

            CAPTURE_CASTLE_TopKillerReward.put(place, items);
         }
      }

      CAPTURE_CASTLE_EnableKillsInTitle = eventSettings.getProperty("CaptureCastleKillsInTitle", false);
      CAPTURE_CASTLE_Categories = eventSettings.getProperty("CaptureCastleCategories", false);
      CAPTURE_CASTLE_AllowSummons = eventSettings.getProperty("CaptureCastleAllowSummons", false);
      CAPTURE_CASTLE_AllowBuffs = eventSettings.getProperty("CaptureCastleAllowBuffs", false);
      CAPTURE_CASTLE_AllowHwidCheck = eventSettings.getProperty("CaptureCastleAllowHwidCheck", false);
      CAPTURE_CASTLE_AllowIpCheck = eventSettings.getProperty("CaptureCastleAllowIpCheck", false);
      CAPTURE_CASTLE_FighterBuffs = eventSettings.getProperty("CaptureCastleFighterBuffs", "").trim().replaceAll(" ", "").split(";");
      CAPTURE_CASTLE_MageBuffs = eventSettings.getProperty("CaptureCastleMageBuffs", "").trim().replaceAll(" ", "").split(";");
      CAPTURE_CASTLE_BuffPlayers = eventSettings.getProperty("CaptureCastleBuffPlayers", false);
      CAPTURE_CASTLE_INCLUDE_ITEMS = eventSettings.getProperty("CaptureCastleIncludeItems", new int[0]);
      CAPTURE_CASTLE_RESTRICTED_SKILL_IDS = eventSettings.getProperty("CaptureCastleRestrictedSkillIds", new int[0]);
      CAPTURE_CASTLE_ENCHANT_LIMIT = eventSettings.getProperty("CaptureCastleEnchantLimit", false);
      CAPTURE_CASTLE_ENCHANT_LIMIT_WEAPON = eventSettings.getProperty("CaptureCastleEnchantLimitWeapon", 6);
      CAPTURE_CASTLE_ENCHANT_LIMIT_ARMOR = eventSettings.getProperty("CaptureCastleEnchantLimitArmor", 6);
      CAPTURE_CASTLE_BUFF_TIME_FIGHTER = eventSettings.getProperty("CaptureCastleBuffTimeFighter", 20);
      CAPTURE_CASTLE_BUFF_TIME_MAGE = eventSettings.getProperty("CaptureCastleBuffTimeMage", 20);
      CAPTURE_CASTLE_CAN_PARTY_INVITE = eventSettings.getProperty("CaptureCastleCanPartyInvite", false);
      CAPTURE_CASTLE_MaxPlayerInTeam = eventSettings.getProperty("CaptureCastleMaxPlayerInTeam", 200);
      CAPTURE_CASTLE_MinPlayerInTeam = eventSettings.getProperty("CaptureCastleMinPlayerInTeam", 1);
      CAPTURE_CASTLE_Instance = eventSettings.getProperty("CaptureCastleInstance", 611);
      CAPTURE_CASTLE_FLAG_ID = eventSettings.getProperty("CaptureCastleFlagId", 50025);
      CAPTURE_CASTLE_FLAG_LOC = Location.parseLoc(eventSettings.getProperty("CaptureCastleFlagLoc", "22073,161778,-2674,49152"));
      CAPTURE_CASTLE_DOORS = eventSettings.getProperty("CaptureCastleDoors", new int[]{20220001, 20220002, 20220005, 20220006});
      CAPTURE_CASTLE_ZONE = eventSettings.getProperty("CaptureCastleZone", "[capture_castle_dion]");
      CAPTURE_CASTLE_FIRST_TEAM_SPAWN_LOC = Location.parseLoc(eventSettings.getProperty("CaptureCastleFirstTeamSpawnLoc", "24328,153144,-3010"));
      CAPTURE_CASTLE_SECOND_TEAM_SPAWN_LOC = Location.parseLoc(eventSettings.getProperty("CaptureCastleSecondTeamSpawnLoc", "21320,155976,-3042"));
      CAPTURE_CASTLE_SPAWN_LOC_OWNER_TEAM = Location.parseLoc(eventSettings.getProperty("CaptureCastleSpawnLocOwnerTeam", "22264,160712,-2754"));
      CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_BLUE_TEAM = Location.parseLoc(eventSettings.getProperty("CaptureCastleSpawnLocAttackingBlueTeam", "23112,154040,-3010"));
      CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_RED_TEAM = Location.parseLoc(eventSettings.getProperty("CaptureCastleSpawnLocAttackingRedTeam", "23112,154040,-3010"));
      CAPTURE_CASTLE_TIME_BACK = eventSettings.getProperty("CaptureCastleTimeBack", 30);
      CAPTURE_CASTLE_SPAWN_REG_MANAGER = eventSettings.getProperty("CaptureCastleSpawnRegManager", false);
      CAPTURE_CASTLE_REG_MANAGER_ID = eventSettings.getProperty("CaptureCastleRegManagerId", 31225);
      CAPTURE_CASTLE_REG_MANAGER_LOC = eventSettings.getProperty("CaptureCastleRegManagerLoc", "83448,148375,-3425,47670");
      CAPTURE_CASTLE_ENABLE_COMMAND = eventSettings.getProperty("CaptureCastleEnableCommand", true);
      CAPTURE_CASTLE_VOICE_COMMAND = eventSettings.getProperty("CaptureCastleVoiceCommand", "capture");
      CAPTURE_CASTLE_SEND_REG_WINDOW = eventSettings.getProperty("CaptureCastleSendRegWindow", true);
      CAPTURE_CASTLE_RETURN_POINT_ENABLE = eventSettings.getProperty("CaptureCastleReturnPointEnable", true);
      CAPTURE_CASTLE_RETURN_POINT = Location.parseLoc(eventSettings.getProperty("CaptureCastleReturnPoint", "81043,148618,-3472"));
      CAPTURE_CASTLE_RESTORE_DESTROYED_DOORS = eventSettings.getProperty("CaptureCastleRestoreDestroyedDoors", true);
      CAPTURE_CASTLE_RES_DELAY = eventSettings.getProperty("CaptureCastleResDelay", 10);
      CAPTURE_CASTLE_BROADCAST_TIMER = eventSettings.getProperty("CaptureCastleBroadcastTimer", true);
      CAPTURE_CASTLE_HIDE_NAME = eventSettings.getProperty("CaptureCastleHideName", true);
      CAPTURE_CASTLE_HIDE_TITLE = eventSettings.getProperty("CaptureCastleHideTitle", false);
      CAPTURE_CASTLE_HIDE_CLAN_ALY_INFO = eventSettings.getProperty("CaptureCastleClanAlyInfo", false);
      CAPTURE_CASTLE_HIDE_NAME_COLOR = eventSettings.getProperty("CaptureCastleHideNameColor", false);
      CAPTURE_CASTLE_HIDE_TITLE_COLOR = eventSettings.getProperty("CaptureCastleHideTitleColor", false);
      CAPTURE_CASTLE_ALLOW_OBSERVER = eventSettings.getProperty("CaptureCastleAllowObserver", false);
      coords = eventSettings.getProperty("CaptureCastleObserverCoords", "").split(";");
      String[] var11 = coords;
      var4 = coords.length;

      for(int var12 = 0; var12 < var4; ++var12) {
         String coord = var11[var12];
         CAPTURE_CASTLE_OBSERVER_COORDS.add(coord);
      }

   }
}
