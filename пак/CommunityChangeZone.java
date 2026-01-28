package services.community.custom;

import java.util.ArrayList;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ConfigChangeZone;

public class CommunityChangeZone implements ScriptFile, ICommunityBoardHandler {
   private static final Logger _log = LoggerFactory.getLogger(CommunityChangeZone.class);
   private static CommunityChangeZone _Instance = null;

   public void onLoad() {
      if (ConfigChangeZone.ENABLE_CHANGE_ZONES) {
         CommunityBoardManager.getInstance().registerHandler(this);
      }

   }

   public void onReload() {
      if (ConfigChangeZone.ENABLE_CHANGE_ZONES) {
         CommunityBoardManager.getInstance().removeHandler(this);
      }

   }

   public void onShutdown() {
   }

   public static CommunityChangeZone getInstance() {
      if (_Instance == null) {
         _Instance = new CommunityChangeZone();
      }

      return _Instance;
   }

   public String[] getBypassCommands() {
      return new String[]{"_bbschangezone"};
   }

   public void onBypassCommand(Player player, String bypass) {
      if (ConfigChangeZone.ENABLE_CHANGE_ZONES) {
         String activeChangeZone;
         if (!CommunityTools.checkConditions(player)) {
            activeChangeZone = HtmCache.getInstance().getNotNull("scripts/services/community/pages/locked.htm", player);
            activeChangeZone = activeChangeZone.replace("%name%", player.getName());
            ShowBoard.separateAndSend(activeChangeZone, player);
         } else {
            if (bypass.equals("_bbschangezone")) {
               activeChangeZone = ServerVariables.getString("active_change_zone", (String)null);
               if (activeChangeZone == null || activeChangeZone.isEmpty()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Зона не установлена.");
                  } else {
                     player.sendMessage("The zone is not set.");
                  }

                  return;
               }

               Zone activeZone = ReflectionUtils.getZone(activeChangeZone);
               if (activeZone != null) {
                  String pointStr = activeZone.getParams().getString("ChangeZonePoints", (String)null);
                  if (pointStr != null && !pointStr.isEmpty()) {
                     String[] points = pointStr.split(";");
                     List<Location> changePoints = new ArrayList(points.length);
                     String[] var8 = points;
                     int var9 = points.length;

                     for(int var10 = 0; var10 < var9; ++var10) {
                        String point = var8[var10];
                        changePoints.add(Location.parseLoc(point));
                     }

                     Location changePoint = (Location)Rnd.get(changePoints);
                     player.teleToLocation(changePoint);
                  }
               }
            }

         }
      }
   }

   public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
   }
}
