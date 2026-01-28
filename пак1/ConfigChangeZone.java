package services;

import java.util.HashMap;
import java.util.Map;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigChangeZone {
   private static Logger _log = LoggerFactory.getLogger(ConfigChangeZone.class);
   public static final String CONFIG_FILE = "config/custom/change_zone.properties";
   public static boolean ENABLE_CHANGE_ZONES;
   public static int INIT_TIME_CHANGE_ZONE;
   public static int TIME_TO_CHANGE_ZONE;
   public static boolean BROADCAST_TIMER_IN_ACTIVE_CHANGE_ZONE;
   public static boolean SEND_REQUEST_TELEPORT_CHANGE_ZONE;
   public static int SECONDS_REQUEST_TELEPORT_CHANGE_ZONE;
   public static boolean SEND_MESSAGE_TIME_WHEN_ENTER_ZONE;
   public static boolean FIXED_REVIVE_WINDOW_ENABLE;
   public static boolean ACTIVE_CHANGE_ZONE_HIDE_NAME_ENABLE;
   public static String ACTIVE_CHANGE_ZONE_HIDE_NAME;
   public static boolean ACTIVE_CHANGE_ZONE_HIDE_TITLE_ENABLE;
   public static String ACTIVE_CHANGE_ZONE_HIDE_TITLE;
   public static boolean ACTIVE_CHANGE_ZONE_HIDE_NAME_COLOR_ENABLE;
   public static int ACTIVE_CHANGE_ZONE_HIDE_NAME_COLOR;
   public static boolean ACTIVE_CHANGE_ZONE_HIDE_TITLE_COLOR_ENABLE;
   public static int ACTIVE_CHANGE_ZONE_HIDE_TITLE_COLOR;
   public static boolean ACTIVE_CHANGE_ZONE_HIDE_CLAN_ALY_INFO_ENABLE;
   public static boolean ACTIVE_CHANGE_ZONE_HIDE_RACE_ENABLE;
   public static boolean ALLOW_CHANGE_ZONE_PVP_REWARD;
   public static int[] CHANGE_ZONE_PVP_REWARD;
   public static int CHANGE_ZONE_PVP_REWARD_LVL_DIFF;
   public static long CHANGE_ZONE_PVP_REWARD_TIME;
   public static boolean CHANGE_ZONE_PVP_REWARD_IP;
   public static boolean CHANGE_ZONE_PVP_REWARD_HWID;
   public static boolean ALLOW_CHANGE_ZONE_TOP_PVP;
   public static boolean ALLOW_CHANGE_ZONE_TOP_PVP_ANNOUNCE;
   public static Map<Integer, int[]> CHANGE_ZONE_PVP_WINNERS = new HashMap();

   public static void load() {
      ExProperties settingProperties = Config.load("config/custom/change_zone.properties");
      ENABLE_CHANGE_ZONES = settingProperties.getProperty("EnableChangeZones", false);
      INIT_TIME_CHANGE_ZONE = settingProperties.getProperty("InitTimeChangeZone", 1);
      TIME_TO_CHANGE_ZONE = settingProperties.getProperty("TimeToChangeZone", 120);
      BROADCAST_TIMER_IN_ACTIVE_CHANGE_ZONE = settingProperties.getProperty("BroadcastTimerInActiveChangeZone", true);
      SEND_REQUEST_TELEPORT_CHANGE_ZONE = settingProperties.getProperty("SendRequestTeleportChangeZone", true);
      SECONDS_REQUEST_TELEPORT_CHANGE_ZONE = settingProperties.getProperty("SecondsRequestTeleportChangeZone", 30);
      SEND_MESSAGE_TIME_WHEN_ENTER_ZONE = settingProperties.getProperty("SendMessageTimeWhenEnterZone", false);
      FIXED_REVIVE_WINDOW_ENABLE = settingProperties.getProperty("FixedReviveWindowEnable", false);
      ACTIVE_CHANGE_ZONE_HIDE_NAME_ENABLE = settingProperties.getProperty("ActiveChangeZoneHideNameEnable", false);
      ACTIVE_CHANGE_ZONE_HIDE_NAME = settingProperties.getProperty("ActiveChangeZoneHideName", "Pvp Zone");
      ACTIVE_CHANGE_ZONE_HIDE_TITLE_ENABLE = settingProperties.getProperty("ActiveChangeZoneHideTitleEnable", false);
      ACTIVE_CHANGE_ZONE_HIDE_TITLE = settingProperties.getProperty("ActiveChangeZoneHideTitle", "Pvp");
      ACTIVE_CHANGE_ZONE_HIDE_NAME_COLOR_ENABLE = settingProperties.getProperty("ActiveChangeZoneHideNameColorEnable", false);
      ACTIVE_CHANGE_ZONE_HIDE_NAME_COLOR = Integer.decode("0x" + settingProperties.getProperty("ActiveChangeZoneHideNameColor", "FFFFFF"));
      ACTIVE_CHANGE_ZONE_HIDE_TITLE_COLOR_ENABLE = settingProperties.getProperty("ActiveChangeZoneHideTitleColorEnable", false);
      ACTIVE_CHANGE_ZONE_HIDE_TITLE_COLOR = Integer.decode("0x" + settingProperties.getProperty("ActiveChangeZoneHideTitleColor", "FFFF77"));
      ACTIVE_CHANGE_ZONE_HIDE_CLAN_ALY_INFO_ENABLE = settingProperties.getProperty("ActiveChangeZoneHideClanAlyInfoEnable", false);
      ACTIVE_CHANGE_ZONE_HIDE_RACE_ENABLE = settingProperties.getProperty("ActiveChangeZoneHideRaceEnable", false);
      ALLOW_CHANGE_ZONE_TOP_PVP = settingProperties.getProperty("AllowChangeZoneTopPvp", false);
      ALLOW_CHANGE_ZONE_TOP_PVP_ANNOUNCE = settingProperties.getProperty("AllowChangeZoneTopPvpAnnounce", false);
      String[] rewardsByPlace = settingProperties.getProperty("ChangeZoneTopPvpRewards", "").split(";");
      String[] var2 = rewardsByPlace;
      int var3 = rewardsByPlace.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String r = var2[var4];
         if (!r.trim().isEmpty()) {
            String[] rewardSplit = r.split(":");
            int place = Integer.parseInt(rewardSplit[0]);
            String[] rewards = rewardSplit[1].split(",");
            int[] items = new int[rewards.length];

            for(int i = 0; i < rewards.length; ++i) {
               items[i] = Integer.parseInt(rewards[i]);
            }

            CHANGE_ZONE_PVP_WINNERS.put(place, items);
         }
      }

      ALLOW_CHANGE_ZONE_PVP_REWARD = settingProperties.getProperty("AllowChangeZonePvPReward", false);
      CHANGE_ZONE_PVP_REWARD = settingProperties.getProperty("ChangeZonePvPReward", new int[]{57, 100});
      CHANGE_ZONE_PVP_REWARD_LVL_DIFF = settingProperties.getProperty("ChangeZonePvPRewardLvlDiff", 5);
      CHANGE_ZONE_PVP_REWARD_TIME = Long.parseLong(settingProperties.getProperty("ChangeZonePvPRewardTime", "45")) * 1000L;
      CHANGE_ZONE_PVP_REWARD_IP = settingProperties.getProperty("ChangeZonePvPRewardIP", false);
      CHANGE_ZONE_PVP_REWARD_HWID = settingProperties.getProperty("ChangeZonePvPRewardHWID", false);
   }
}
