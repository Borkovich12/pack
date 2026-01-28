package services;

import java.util.HashMap;
import java.util.Map;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigAgathion {
   private static Logger _log = LoggerFactory.getLogger(ConfigAgathion.class);
   public static final String CONFIG_FILE = "config/custom/agathions.properties";
   public static Map<Integer, Integer> AGATHION_DATAS;
   public static int AGATHION_SUMMON_SKILL_ID;
   public static int AGATHION_RANGE;
   public static boolean AGATHION_COPY_OWNER_RUN_SPD;

   public static void load() {
      ExProperties properties = Config.load("config/custom/agathions.properties");
      AGATHION_SUMMON_SKILL_ID = properties.getProperty("AgathionSummonSkillId", 2046);
      AGATHION_RANGE = properties.getProperty("AgathionRange", 50);
      AGATHION_COPY_OWNER_RUN_SPD = properties.getProperty("AgathionCopyOwnerRunSpd", true);
      AGATHION_DATAS = new HashMap();
      String[] agathionDatas = properties.getProperty("AgathionDatas", "").split(";");
      String[] var2 = agathionDatas;
      int var3 = agathionDatas.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String data = var2[var4];
         if (!data.isEmpty()) {
            int itemId = Integer.parseInt(data.split(",")[0]);
            int npcId = Integer.parseInt(data.split(",")[1]);
            AGATHION_DATAS.put(itemId, npcId);
         }
      }

   }
}
