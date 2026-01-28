package services.community.custom.roulette;

import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigRoulette {
   private static Logger _log = LoggerFactory.getLogger(ConfigRoulette.class);
   public static final String CONFIG_FILE = "config/custom/roulette.properties";
   public static boolean ENABLE_CUSTOM_ROULETTE;
   public static int CUSTOM_ROULETTE_ITEM_ID;
   public static int CUSTOM_ROULETTE_ITEM_COUNT;
   public static int CUSTOM_ROULETTE_DELAY;
   public static int CUSTOM_ROULETTE_VISIBLE_ITEMS_COUNT;
   public static int CUSTOM_ROULETTE_VISIBLE_EXAMPLES_ITEMS_COUNT;
   public static int CUSTOM_ROULETTE_MAX_LENGTH_NAME;
   public static boolean CUSTOM_ROULETTE_STORE_STATS_IN_DB;

   public static void load() {
      ExProperties servicesSettings = Config.load("config/custom/roulette.properties");
      ENABLE_CUSTOM_ROULETTE = Boolean.parseBoolean(servicesSettings.getProperty("EnableCustomRoulette", "False"));
      CUSTOM_ROULETTE_ITEM_ID = Integer.parseInt(servicesSettings.getProperty("CustomRouletteItemId", "57"));
      CUSTOM_ROULETTE_ITEM_COUNT = Integer.parseInt(servicesSettings.getProperty("CustomRouletteItemCount", "1000"));
      CUSTOM_ROULETTE_DELAY = Integer.parseInt(servicesSettings.getProperty("CustomRouletteDelay", "200"));
      CUSTOM_ROULETTE_VISIBLE_ITEMS_COUNT = Integer.parseInt(servicesSettings.getProperty("CustomRouletteVisibleItemsCount", "5"));
      CUSTOM_ROULETTE_VISIBLE_EXAMPLES_ITEMS_COUNT = Integer.parseInt(servicesSettings.getProperty("CustomRouletteVisibleExamplesItemsCount", "5"));
      CUSTOM_ROULETTE_MAX_LENGTH_NAME = Integer.parseInt(servicesSettings.getProperty("CustomRouletteMaxLengthName", "16"));
      CUSTOM_ROULETTE_STORE_STATS_IN_DB = Boolean.parseBoolean(servicesSettings.getProperty("CustomRouletteStoreStatsInDb", "False"));
   }
}
