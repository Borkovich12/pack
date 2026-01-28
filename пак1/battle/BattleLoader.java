package events.battle;

import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleLoader implements ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(BattleLoader.class);

   public void onLoad() {
      _log.info("=================================================================");
      _log.info("Load GvG Tournament event.");
      _log.info("Telegram: MerdoxOne");
      _log.info("Skype: MerdoxOne");
      BattleConfig.load();
      if (BattleConfig.GVG_1X1_ENABLE) {
         BattleGvG1x1.getInstance().load();
      }

      if (BattleConfig.GVG_2X2_ENABLE) {
         BattleGvG2x2.getInstance().load();
      }

      if (BattleConfig.GVG_3X3_ENABLE) {
         BattleGvG3x3.getInstance().load();
      }

      if (BattleConfig.GVG_4X4_ENABLE) {
         BattleGvG4x4.getInstance().load();
      }

      if (BattleConfig.GVG_5X5_ENABLE) {
         BattleGvG5x5.getInstance().load();
      }

      if (BattleConfig.GVG_6X6_ENABLE) {
         BattleGvG6x6.getInstance().load();
      }

      if (BattleConfig.GVG_7X7_ENABLE) {
         BattleGvG7x7.getInstance().load();
      }

      if (BattleConfig.GVG_8X8_ENABLE) {
         BattleGvG8x8.getInstance().load();
      }

      if (BattleConfig.GVG_9X9_ENABLE) {
         BattleGvG9x9.getInstance().load();
      }

      if (BattleConfig.GVG_CXC_ENABLE) {
         BattleGvGCxC.getInstance().load();
      }

      _log.info("Battle GvG system loaded.");
      _log.info("=================================================================");
   }

   public void onReload() {
   }

   public void onShutdown() {
   }
}
