package events.PiratesTreasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigPiratesTreasure {
   private static Logger _log = LoggerFactory.getLogger(ConfigPiratesTreasure.class);
   public static final String CONFIG_FILE = "config/events/pirates_treasure.properties";
   public static int PiratesTreasureNpcId;
   public static int PiratesTreasureTimeEvent;
   public static boolean PiratesTreasureAltSearch;
   public static Map<Integer, Integer> PiratesTreasureRewards;
   public static List<EventInterval> PiratesTreasureInterval;

   public static void load() {
      ExProperties EventSettings = Config.load("config/events/pirates_treasure.properties");
      PiratesTreasureNpcId = Integer.parseInt(EventSettings.getProperty("PiratesTreasureNpcId", "13099"));
      PiratesTreasureTimeEvent = Integer.parseInt(EventSettings.getProperty("PiratesTreasureTimeEvent", "30"));
      PiratesTreasureAltSearch = Boolean.parseBoolean(EventSettings.getProperty("PiratesTreasureAltSearch", "True"));
      String[] propertySplit = EventSettings.getProperty("PiratesTreasureRewards", "57,10000;4037,100").split(";");
      PiratesTreasureRewards = new HashMap();
      String[] var2 = propertySplit;
      int var3 = propertySplit.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String param = var2[var4];
         String[] prices = param.split(",");
         if (prices.length == 2) {
            try {
               PiratesTreasureRewards.put(Integer.parseInt(prices[0]), Integer.parseInt(prices[1]));
            } catch (NumberFormatException var8) {
            }
         }
      }

      PiratesTreasureInterval = new ArrayList();
      StringTokenizer stringTokenizer = new StringTokenizer(EventSettings.getProperty("PiratesTreasureInterval", ""), "[]");

      while(stringTokenizer.hasMoreTokens()) {
         String interval = stringTokenizer.nextToken();
         String[] t2 = interval.split(":");
         int h = Integer.parseInt(t2[0]);
         int m = Integer.parseInt(t2[1]);
         PiratesTreasureInterval.add(new EventInterval(h, m, -1));
      }

   }
}
