package services;

import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigCustomServices implements ScriptFile {
   private static Logger _log = LoggerFactory.getLogger(ConfigCustomServices.class);
   public static final String CONFIG_FILE = "config/custom/custom_services.properties";
   public static boolean SERVICES_CHANGE_NICK_COLOR_ENABLED;
   public static int[] SERVICES_CHANGE_NICK_COLOR_PRICE;
   public static int[] SERVICES_CHANGE_NICK_COLOR_ITEM;
   public static String[] SERVICES_CHANGE_NICK_COLOR_LIST;
   public static boolean SERVICES_CHANGE_TITLE_COLOR_ENABLED;
   public static int[] SERVICES_CHANGE_TITLE_COLOR_PRICE;
   public static int[] SERVICES_CHANGE_TITLE_COLOR_ITEM;
   public static String[] SERVICES_CHANGE_TITLE_COLOR_LIST;

   public static void load() {
      ExProperties servicesSettings = Config.load("config/custom/custom_services.properties");
      SERVICES_CHANGE_NICK_COLOR_ENABLED = servicesSettings.getProperty("CustomNickColorChangeEnabled", false);
      SERVICES_CHANGE_NICK_COLOR_PRICE = servicesSettings.getProperty("CustomNickColorChangePrice", new int[]{100});
      SERVICES_CHANGE_NICK_COLOR_ITEM = servicesSettings.getProperty("CustomNickColorChangeItem", new int[]{100});
      SERVICES_CHANGE_NICK_COLOR_LIST = servicesSettings.getProperty("CustomNickColorChangeList", new String[]{"00FF00"});
      SERVICES_CHANGE_TITLE_COLOR_ENABLED = servicesSettings.getProperty("CustomTitleColorChangeEnabled", false);
      SERVICES_CHANGE_TITLE_COLOR_LIST = servicesSettings.getProperty("CustomTitleColorChangeList", new String[]{"00FF00"});
      SERVICES_CHANGE_TITLE_COLOR_ITEM = servicesSettings.getProperty("CustomTitleColorChangeItem", new int[]{4037});
      SERVICES_CHANGE_TITLE_COLOR_PRICE = servicesSettings.getProperty("CustomTitleColorChangePrice", new int[]{100});
   }

   public void onLoad() {
      load();
   }

   public void onReload() {
   }

   public void onShutdown() {
   }
}
