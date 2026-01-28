package services.community.custom.roulette;

import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.scripts.ScriptFile;
import services.community.custom.CommunityTools;
import services.community.custom.roulette.data.RouletteRewardParser;

public class CommunityRoulette implements ScriptFile, ICommunityBoardHandler {
   public void onLoad() {
      ConfigRoulette.load();
      if (ConfigRoulette.ENABLE_CUSTOM_ROULETTE) {
         RouletteRewardParser.getInstance().load();
         RouletteManager.getInstance().restore();
         CommunityBoardManager.getInstance().registerHandler(this);
      }

   }

   public void onReload() {
      if (ConfigRoulette.ENABLE_CUSTOM_ROULETTE) {
         CommunityBoardManager.getInstance().removeHandler(this);
      }

   }

   public void onShutdown() {
   }

   public String[] getBypassCommands() {
      return new String[]{"_cbroulette", "_cbroulette_start", "_cbroulette_fast", "_cbroulette_stop"};
   }

   public void onBypassCommand(Player player, String bypass) {
      if (ConfigRoulette.ENABLE_CUSTOM_ROULETTE) {
         if (!CommunityTools.checkConditions(player)) {
            String html = HtmCache.getInstance().getNotNull("scripts/services/community/pages/locked.htm", player);
            html = html.replace("%name%", player.getName());
            ShowBoard.separateAndSend(html, player);
         } else {
            if (bypass.equalsIgnoreCase("_cbroulette")) {
               player.getRoulette().showRouletteBoard();
            } else if (bypass.equalsIgnoreCase("_cbroulette_start")) {
               player.getRoulette().startRoulette();
            } else if (bypass.equalsIgnoreCase("_cbroulette_fast")) {
               player.getRoulette().fastRoulette();
            } else if (bypass.equalsIgnoreCase("_cbroulette_stop")) {
               player.getRoulette().stopRoulette();
            }

         }
      }
   }

   public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
   }
}
