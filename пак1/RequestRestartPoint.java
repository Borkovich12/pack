package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zones;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.Die;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.TeleportUtils;
import org.apache.commons.lang3.tuple.Pair;
import services.ConfigChangeZone;

public class RequestRestartPoint extends L2GameClientPacket {
   private RestartType _restartType;

   protected void readImpl() {
      this._restartType = (RestartType)ArrayUtils.valid(RestartType.VALUES, this.readD());
   }

   protected void runImpl() {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (this._restartType != null && activeChar != null) {
         if (activeChar.isFakeDeath()) {
            activeChar.breakFakeDeath();
         } else if (!activeChar.isDead() && !activeChar.isGM()) {
            activeChar.sendActionFailed();
         } else if (activeChar.isFestivalParticipant()) {
            activeChar.doRevive();
         } else if (activeChar.isResurectProhibited()) {
            activeChar.sendActionFailed();
         } else {
            Location loc;
            switch(this._restartType) {
            case FIXED:
               if (ConfigChangeZone.FIXED_REVIVE_WINDOW_ENABLE && isInFixedZone(activeChar)) {
                  loc = null;
                  Iterator var3 = activeChar.getZones().iterator();

                  while(var3.hasNext()) {
                     Zone zone = (Zone)var3.next();
                     String pointStr = zone.getParams().getString("ChangeZonePoints", (String)null);
                     if (pointStr != null && !pointStr.isEmpty()) {
                        String[] points = pointStr.split(";");
                        List<Location> changePoints = new ArrayList(points.length);
                        String[] var8 = points;
                        int var9 = points.length;

                        for(int var10 = 0; var10 < var9; ++var10) {
                           String point = var8[var10];
                           changePoints.add(Location.parseLoc(point));
                        }

                        loc = (Location)Rnd.get(changePoints);
                        break;
                     }
                  }

                  int reviveDelay = Rnd.get(3, 5);
                  ThreadPoolManager.getInstance().schedule(new RequestRestartPoint.FixedReviveTask(activeChar.getObjectId(), loc), (long)reviveDelay * 1000L);
                  if (activeChar.isLangRus()) {
                     activeChar.sendMessage("Вы будете воскрешены через " + reviveDelay + " секунд(ы).");
                  } else {
                     activeChar.sendMessage("You will be revived after " + reviveDelay + " seconds.");
                  }
               } else if (activeChar.getPlayerAccess().ResurectFixed) {
                  activeChar.doRevive(100.0D);
               } else if (Config.SERVICE_FEATHER_REVIVE_ENABLE && (!Config.SERVICE_DISABLE_FEATHER_ON_SIEGES_AND_EPIC || !activeChar.isOnSiegeField() && !activeChar.isInZone(ZoneType.epic)) && !activeChar.getInventory().isLockedItem(Config.SERVICE_FEATHER_ITEM_ID) && ItemFunctions.removeItem(activeChar, Config.SERVICE_FEATHER_ITEM_ID, 1L, true) == 1L) {
                  activeChar.sendMessage(new CustomMessage("YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT", activeChar, new Object[0]));
                  activeChar.doRevive(100.0D);
               } else if (Config.ALT_REVIVE_WINDOW_TO_TOWN) {
                  Pair<Integer, OnAnswerListener> ask = activeChar.getAskListener(false);
                  if (ask != null && ask.getValue() instanceof ReviveAnswerListener && !((ReviveAnswerListener)ask.getValue()).isForPet()) {
                     activeChar.getAskListener(true);
                  }

                  activeChar.setPendingRevive(true);
                  activeChar.teleToLocation(Config.ALT_REVIVE_WINDOW_TO_TOWN_LOCATION, ReflectionManager.DEFAULT);
               } else {
                  activeChar.sendPacket(new IStaticPacket[]{ActionFail.STATIC, new Die(activeChar)});
               }
               break;
            default:
               loc = null;
               Reflection ref = activeChar.getReflection();
               GlobalEvent e;
               if (ref == ReflectionManager.DEFAULT) {
                  for(Iterator var15 = activeChar.getEvents().iterator(); var15.hasNext(); loc = e.getRestartLoc(activeChar, this._restartType)) {
                     e = (GlobalEvent)var15.next();
                  }
               }

               if (loc == null) {
                  loc = defaultLoc(this._restartType, activeChar);
               }

               if (loc != null) {
                  Pair<Integer, OnAnswerListener> ask = activeChar.getAskListener(false);
                  if (ask != null && ((OnAnswerListener)ask.getValue()).isRevive() && !((OnAnswerListener)ask.getValue()).asRevive().isForPet()) {
                     activeChar.getAskListener(true);
                  }

                  activeChar.setPendingRevive(true);
                  activeChar.teleToLocation(loc, ReflectionManager.DEFAULT);
               } else {
                  activeChar.sendPacket(new IStaticPacket[]{ActionFail.STATIC, new Die(activeChar)});
               }
            }

         }
      }
   }

   public static boolean isInFixedZone(Player player) {
      Zones zones = player.getZones();
      if (zones != null) {
         Iterator var2 = zones.iterator();

         while(var2.hasNext()) {
            Zone zone = (Zone)var2.next();
            if (zone != null) {
               boolean isChangeZone = zone.getParams().getBool("IsChangeZone", false);
               String pointStr = zone.getParams().getString("ChangeZonePoints", (String)null);
               if (isChangeZone && pointStr != null && !pointStr.isEmpty()) {
                  String activeChangeZone = ServerVariables.getString("active_change_zone", "");
                  if (zone.getName().equalsIgnoreCase(activeChangeZone)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static Location defaultLoc(RestartType restartType, Player activeChar) {
      Location loc = null;
      Clan clan = activeChar.getClan();
      switch(restartType) {
      case TO_CLANHALL:
         if (clan != null && clan.getHasHideout() != 0) {
            ClanHall clanHall = activeChar.getClanHall();
            loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_CLANHALL);
            if (clanHall.getFunction(5) != null) {
               activeChar.restoreExp((double)clanHall.getFunction(5).getLevel());
            }
         }
         break;
      case TO_CASTLE:
         if (clan != null && clan.getCastle() != 0) {
            Castle castle = activeChar.getCastle();
            loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_CASTLE);
            if (castle.getFunction(5) != null) {
               activeChar.restoreExp((double)castle.getFunction(5).getLevel());
            }
         }
         break;
      default:
         loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_VILLAGE);
      }

      return loc;
   }

   private static class FixedReviveTask extends RunnableImpl {
      private int _playerObjId;
      private Location _changePoint;

      FixedReviveTask(int playerObjId, Location changePoint) {
         this._playerObjId = playerObjId;
         this._changePoint = changePoint;
      }

      public void runImpl() throws Exception {
         Player activeChar = GameObjectsStorage.getPlayer(this._playerObjId);
         if (activeChar != null && activeChar.isDead()) {
            Pair<Integer, OnAnswerListener> ask = activeChar.getAskListener(false);
            if (ask != null && ask.getValue() instanceof ReviveAnswerListener && !((ReviveAnswerListener)ask.getValue()).isForPet()) {
               activeChar.getAskListener(true);
            }

            activeChar.setPendingRevive(true);
            if (this._changePoint != null) {
               activeChar.teleToLocation(this._changePoint, ReflectionManager.DEFAULT);
            }

         }
      }
   }
}
