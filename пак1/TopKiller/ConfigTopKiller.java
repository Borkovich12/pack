package events.TopKiller;

import java.util.HashMap;
import java.util.Map;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigTopKiller {
   private static Logger _log = LoggerFactory.getLogger(ConfigTopKiller.class);
   public static final String CONFIG_FILE = "config/events/top_killer.properties";
   public static boolean TOP_KILLER_ENABLE;
   public static int TOP_KILLER_MANAGER_NPC_ID;
   public static int[] TOP_KILLER_MANAGER_SPAWN;
   public static int[] TOP_KILLER_DINO_MOBS;
   public static boolean TOP_KILLER_DINO_MSG_GET;
   public static boolean TOP_KILLER_PVP_MSG_GET;
   public static boolean TOP_KILLER_PVP_REWARD_IP;
   public static boolean TOP_KILLER_PVP_REWARD_HWID;
   public static boolean TOP_KILLER_PVP_DISABLE_IN_EPIC_SIEGE_ZONES;
   public static String[] TOP_KILLER_PVP_DISABLE_ZONES;
   public static boolean TOP_KILLER_PK_MSG_GET;
   public static boolean TOP_KILLER_PK_REWARD_IP;
   public static boolean TOP_KILLER_PK_REWARD_HWID;
   public static boolean TOP_KILLER_PK_DISABLE_IN_EPIC_SIEGE_ZONES;
   public static String[] TOP_KILLER_PK_DISABLE_ZONES;
   public static Map<Integer, int[]> TOP_KILLER_DINO_REWARD = new HashMap();
   public static Map<Integer, int[]> TOP_KILLER_PVP_REWARD = new HashMap();
   public static Map<Integer, int[]> TOP_KILLER_PK_REWARD = new HashMap();
   public static int TOP_KILLER_EVENT_DAYS;
   public static int TOP_KILLER_EVENT_HOUR_OF_DAY;
   public static int TOP_KILLER_EVENT_MINUTE;
   public static int TOP_KILLER_EVENT_DELETE_DELAY;
   public static int TOP_KILLER_STAT_SIZE;
   public static boolean TOP_KILLER_VOICE_COMMAND_ENABLE;
   public static String TOP_KILLER_VOICE_COMMAND;

   public static void load() {
      ExProperties eventSettings = Config.load("config/events/top_killer.properties");
      TOP_KILLER_ENABLE = eventSettings.getProperty("TopKillerEnable", false);
      TOP_KILLER_MANAGER_NPC_ID = eventSettings.getProperty("TopKillerManagerNpcId", 0);
      TOP_KILLER_MANAGER_SPAWN = eventSettings.getProperty("TopKillerManagerSpawn", new int[0]);
      TOP_KILLER_DINO_MOBS = eventSettings.getProperty("TopKillerDinoMobs", new int[0]);
      TOP_KILLER_DINO_MSG_GET = eventSettings.getProperty("TopKillerDinoMsgGet", false);
      TOP_KILLER_PVP_MSG_GET = eventSettings.getProperty("TopKillerPvpMsgGet", false);
      TOP_KILLER_PVP_REWARD_IP = eventSettings.getProperty("TopKillerPvpRewardIp", false);
      TOP_KILLER_PVP_REWARD_HWID = eventSettings.getProperty("TopKillerPvpRewardHwid", false);
      TOP_KILLER_PVP_DISABLE_IN_EPIC_SIEGE_ZONES = eventSettings.getProperty("TopKillerPvpDisableInEpicSiegeZones", false);
      TOP_KILLER_PVP_DISABLE_ZONES = eventSettings.getProperty("TopKillerPvpDisableInEpicSiegeZones", "").split(";");
      TOP_KILLER_PK_MSG_GET = eventSettings.getProperty("TopKillerPkMsgGet", false);
      TOP_KILLER_PK_REWARD_IP = eventSettings.getProperty("TopKillerPkRewardIp", false);
      TOP_KILLER_PK_REWARD_HWID = eventSettings.getProperty("TopKillerPkRewardHwid", false);
      TOP_KILLER_PK_DISABLE_IN_EPIC_SIEGE_ZONES = eventSettings.getProperty("TopKillerPkDisableInEpicSiegeZones", false);
      TOP_KILLER_PK_DISABLE_ZONES = eventSettings.getProperty("TopKillerPkDisableZones", "").split(";");
      String[] rewardsByPlace = eventSettings.getProperty("TopKillerDinoReward", "").split(";");
      String[] var2 = rewardsByPlace;
      int var3 = rewardsByPlace.length;

      int var4;
      String r;
      String[] rewardSplit;
      int place;
      String[] rewards;
      int[] items;
      int i;
      for(var4 = 0; var4 < var3; ++var4) {
         r = var2[var4];
         rewardSplit = r.split(":");
         place = Integer.parseInt(rewardSplit[0]);
         rewards = rewardSplit[1].split(",");
         items = new int[rewards.length];

         for(i = 0; i < rewards.length; ++i) {
            items[i] = Integer.parseInt(rewards[i]);
         }

         TOP_KILLER_DINO_REWARD.put(place, items);
      }

      rewardsByPlace = eventSettings.getProperty("TopKillerPvpReward", "").split(";");
      var2 = rewardsByPlace;
      var3 = rewardsByPlace.length;

      for(var4 = 0; var4 < var3; ++var4) {
         r = var2[var4];
         rewardSplit = r.split(":");
         place = Integer.parseInt(rewardSplit[0]);
         rewards = rewardSplit[1].split(",");
         items = new int[rewards.length];

         for(i = 0; i < rewards.length; ++i) {
            items[i] = Integer.parseInt(rewards[i]);
         }

         TOP_KILLER_PVP_REWARD.put(place, items);
      }

      rewardsByPlace = eventSettings.getProperty("TopKillerPkReward", "").split(";");
      var2 = rewardsByPlace;
      var3 = rewardsByPlace.length;

      for(var4 = 0; var4 < var3; ++var4) {
         r = var2[var4];
         rewardSplit = r.split(":");
         place = Integer.parseInt(rewardSplit[0]);
         rewards = rewardSplit[1].split(",");
         items = new int[rewards.length];

         for(i = 0; i < rewards.length; ++i) {
            items[i] = Integer.parseInt(rewards[i]);
         }

         TOP_KILLER_PK_REWARD.put(place, items);
      }

      TOP_KILLER_EVENT_DAYS = eventSettings.getProperty("TopKillerEventDays", 2);
      TOP_KILLER_EVENT_HOUR_OF_DAY = eventSettings.getProperty("TopKillerEventHourOfDay", 23);
      TOP_KILLER_EVENT_MINUTE = eventSettings.getProperty("TopKillerEventMinute", 58);
      TOP_KILLER_EVENT_DELETE_DELAY = eventSettings.getProperty("TopKillerEventDeleteDelay", 0);
      TOP_KILLER_STAT_SIZE = eventSettings.getProperty("TopKillerStatSize", 10);
      TOP_KILLER_VOICE_COMMAND_ENABLE = eventSettings.getProperty("TopKillerVoiceCommandEnable", false);
      TOP_KILLER_VOICE_COMMAND = eventSettings.getProperty("TopKillerVoiceCommand", "topkiller");
   }
}
