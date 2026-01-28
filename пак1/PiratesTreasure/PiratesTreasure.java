package events.PiratesTreasure;

import events.PiratesTreasure.data.PirateSearchHolder;
import events.PiratesTreasure.data.PirateSearchParser;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.mapregion.RestartArea;
import l2.gameserver.templates.mapregion.RestartPoint;
import l2.gameserver.templates.spawn.PeriodOfDay;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiratesTreasure extends Functions implements ScriptFile {
   private static ScheduledFuture<?> _startTask;
   private static ScheduledFuture<?> _endTask;
   public static NpcInstance pirateNpc = null;
   public static String pointInfo;
   public static boolean eventStoped;
   public Location loc;
   public static final Logger _log = LoggerFactory.getLogger(PiratesTreasure.class);
   private static boolean _active = false;
   private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

   private static boolean isActive() {
      return _active;
   }

   public void onLoad() {
      _log.info("=================================================================");
      _log.info("Load Pirates Treasure event.");
      _log.info("Telegram: MerdoxOne");
      _log.info("Skype: MerdoxOne");
      ConfigPiratesTreasure.load();
      PirateSearchParser.getInstance().load();
      _active = ServerVariables.getString("PiratesTreasure", "off").equalsIgnoreCase("on");
      if (isActive()) {
         this.scheduleEventStart(true);
      }

      _log.info("Loaded Event: PiratesTreasure [" + _active + "]");
      _log.info("=================================================================");
   }

   public void onReload() {
      if (_startTask != null) {
         _startTask.cancel(true);
      }

   }

   public void onShutdown() {
      this.onReload();
   }

   public void activateEvent() {
      Player player = this.getSelf();
      if (player.getPlayerAccess().IsEventGm) {
         if (!isActive()) {
            if (_startTask == null) {
               this.scheduleEventStart(false);
            }

            ServerVariables.set("PiratesTreasure", "on");
            _log.info("Event 'PiratesTreasure' activated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.AnnounceEventStarted", new String[]{"PiratesTreasure"});
         } else {
            player.sendMessage("Event 'PiratesTreasure' already active.");
         }

         _active = true;
         this.show("admin/events/events.htm", player);
      }
   }

   public void deactivateEvent() {
      Player player = this.getSelf();
      if (player.getPlayerAccess().IsEventGm) {
         if (isActive()) {
            if (_startTask != null) {
               _startTask.cancel(true);
               _startTask = null;
            }

            ServerVariables.unset("PiratesTreasure");
            _log.info("Event 'PiratesTreasure' deactivated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.AnnounceEventStoped", new String[]{"PiratesTreasure"});
         } else {
            player.sendMessage("Event 'PiratesTreasure' not active.");
         }

         _active = false;
         this.show(HtmCache.getInstance().getNotNull("admin/events/events.htm", player), player);
      }
   }

   public void startEvent() {
      Player player = this.getSelf();
      if (_endTask != null) {
         show(new CustomMessage("common.TryLater", player, new Object[0]), player);
      } else {
         sayToAll("scripts.events.PiratesTreasure.startEvent");
         eventStoped = false;
         executeTask("events.PiratesTreasure.PiratesTreasure", "callPirates", new Object[0], 60000L);
         _endTask = executeTask("events.PiratesTreasure.PiratesTreasure", "stopEvent", new Object[]{false}, (long)(ConfigPiratesTreasure.PiratesTreasureTimeEvent + 1) * 60000L);
      }
   }

   public void stopEvent(boolean isWin) {
      try {
         if (_endTask != null) {
            _endTask.cancel(false);
            _endTask = null;
         }
      } catch (Exception var3) {
      }

      if (isWin) {
         sayToAll("scripts.events.PiratesTreasure.PirateKingHasBeenDefeated");
      } else {
         sayToAll("scripts.events.PiratesTreasure.stopEvent");
      }

      eventStoped = true;
      this.scheduleEventStart(true);
   }

   public void scheduleEventStart(boolean check) {
      if (!check || isActive()) {
         try {
            Calendar currentTime = Calendar.getInstance();
            Calendar testStartTime = null;
            Calendar nextStartTime = null;
            Iterator var5 = ConfigPiratesTreasure.PiratesTreasureInterval.iterator();

            while(true) {
               do {
                  if (!var5.hasNext()) {
                     _log.info("PiratesTreasure: next start event will be at " + toSimpleFormat(nextStartTime.getTime()));
                     if (_startTask != null) {
                        _startTask.cancel(false);
                        _startTask = null;
                     }

                     _startTask = ThreadPoolManager.getInstance().schedule(new PiratesTreasure.StartTask(), nextStartTime.getTimeInMillis() - System.currentTimeMillis());
                     return;
                  }

                  EventInterval interval = (EventInterval)var5.next();
                  testStartTime = Calendar.getInstance();
                  testStartTime.setLenient(true);
                  testStartTime.set(11, interval.hour);
                  testStartTime.set(12, interval.minute);
                  if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                     testStartTime.add(5, 1);
                  }
               } while(nextStartTime != null && testStartTime.getTimeInMillis() >= nextStartTime.getTimeInMillis());

               nextStartTime = testStartTime;
            }
         } catch (Exception var7) {
            _log.warn("PirateTreasure.scheduleEventStart(): error figuring out a start time. Check config file.", var7);
         }
      }
   }

   public static void sayToAll(String text) {
      Announcements.getInstance().announceByCustomMessage(text, (String[])null);
   }

   public void callPirates() {
      if (ConfigPiratesTreasure.PiratesTreasureAltSearch) {
         Pair<String, List<Location>> pairPoint = (Pair)Rnd.get(PirateSearchHolder.getInstance().getSearchList());
         this.loc = (Location)Rnd.get((List)pairPoint.getRight());
         pirateNpc = spawn(this.loc, ConfigPiratesTreasure.PiratesTreasureNpcId);
         pointInfo = (String)pairPoint.getLeft();
         sayToAll(pointInfo);
         sayToAll("scripts.events.PiratesTreasure.hurryUp");
         _log.info("PiratesTreasure: call alt Pirates " + ConfigPiratesTreasure.PiratesTreasureNpcId + " in loc " + this.loc.toXYZString() + " msg " + (String)pairPoint.getLeft());
      } else {
         this.loc = getRandomSpawnPoint();
         pirateNpc = spawn(this.loc, ConfigPiratesTreasure.PiratesTreasureNpcId);
         pointInfo = getPointInfo(pirateNpc);
         sayToAll(pointInfo);
         sayToAll("scripts.events.PiratesTreasure.hurryUp");
         _log.info("PiratesTreasure: call Pirates " + ConfigPiratesTreasure.PiratesTreasureNpcId + " in loc " + this.loc.toXYZString() + " msg " + pointInfo);
      }

   }

   private static String getPointInfo(NpcInstance npc) {
      String nearestTown = getLocationName(npc);
      byte var4 = -1;
      switch(nearestTown.hashCode()) {
      case -2080264893:
         if (nearestTown.equals("[the_royal_family's_catacomb]")) {
            var4 = 15;
         }
         break;
      case -1856413256:
         if (nearestTown.equals("[kamael_town]")) {
            var4 = 9;
         }
         break;
      case -1615002114:
         if (nearestTown.equals("[godard_town]")) {
            var4 = 42;
         }
         break;
      case -1606278349:
         if (nearestTown.equals("[dwarf_town]")) {
            var4 = 25;
         }
         break;
      case -1550804639:
         if (nearestTown.equals("[rune_town]")) {
            var4 = 28;
         }
         break;
      case -1369756071:
         if (nearestTown.equals("[Einhovant's_School_of_Magic]")) {
            var4 = 21;
         }
         break;
      case -1366639708:
         if (nearestTown.equals("[gm_room]")) {
            var4 = 0;
         }
         break;
      case -1267511775:
         if (nearestTown.equals("[monster_race_PVP]")) {
            var4 = 36;
         }
         break;
      case -1191912863:
         if (nearestTown.equals("[gludin_arena]")) {
            var4 = 19;
         }
         break;
      case -1130893013:
         if (nearestTown.equals("[Temple_of_Shilen]")) {
            var4 = 11;
         }
         break;
      case -1028885467:
         if (nearestTown.equals("[talking_island_town]")) {
            var4 = 22;
         }
         break;
      case -950912845:
         if (nearestTown.equals("[aden_town]")) {
            var4 = 14;
         }
         break;
      case -882108722:
         if (nearestTown.equals("[border_of_nightmare]")) {
            var4 = 2;
         }
         break;
      case -848751485:
         if (nearestTown.equals("[Mothertree_Glade]")) {
            var4 = 27;
         }
         break;
      case -713868163:
         if (nearestTown.equals("[spirit_cave]")) {
            var4 = 6;
         }
         break;
      case -699637446:
         if (nearestTown.equals("[darkelf_town]")) {
            var4 = 10;
         }
         break;
      case -691382348:
         if (nearestTown.equals("[heiness_town]")) {
            var4 = 49;
         }
         break;
      case -643171181:
         if (nearestTown.equals("[giran_arena]")) {
            var4 = 39;
         }
         break;
      case -460794256:
         if (nearestTown.equals("[elf_town]")) {
            var4 = 26;
         }
         break;
      case -458554866:
         if (nearestTown.equals("[giran_habor]")) {
            var4 = 20;
         }
         break;
      case -313884527:
         if (nearestTown.equals("[primeval_peace]")) {
            var4 = 46;
         }
         break;
      case -169353330:
         if (nearestTown.equals("[gludio_castle_town]")) {
            var4 = 44;
         }
         break;
      case -86585700:
         if (nearestTown.equals("[oren_castle_town]")) {
            var4 = 41;
         }
         break;
      case -58058622:
         if (nearestTown.equals("[rift_bitween_worlds]")) {
            var4 = 38;
         }
         break;
      case 30755489:
         if (nearestTown.equals("[giran_castle_town]")) {
            var4 = 40;
         }
         break;
      case 86143079:
         if (nearestTown.equals("[DMZ]")) {
            var4 = 45;
         }
         break;
      case 250569765:
         if (nearestTown.equals("[themepark]")) {
            var4 = 7;
         }
         break;
      case 270329415:
         if (nearestTown.equals("[oracle_of_dawn]")) {
            var4 = 17;
         }
         break;
      case 270921298:
         if (nearestTown.equals("[oracle_of_dusk]")) {
            var4 = 16;
         }
         break;
      case 402366020:
         if (nearestTown.equals("[dion_castle_town]")) {
            var4 = 32;
         }
         break;
      case 453349684:
         if (nearestTown.equals("[crate01]")) {
            var4 = 8;
         }
         break;
      case 453349715:
         if (nearestTown.equals("[crate02]")) {
            var4 = 3;
         }
         break;
      case 453349746:
         if (nearestTown.equals("[crate03]")) {
            var4 = 4;
         }
         break;
      case 512732397:
         if (nearestTown.equals("[Temple_of_Paagrio]")) {
            var4 = 34;
         }
         break;
      case 582835181:
         if (nearestTown.equals("[Cedric's_Training_Hall]")) {
            var4 = 23;
         }
         break;
      case 636390751:
         if (nearestTown.equals("[ironcastle_inner_oasis]")) {
            var4 = 5;
         }
         break;
      case 776982894:
         if (nearestTown.equals("[town_of_schuttgart]")) {
            var4 = 47;
         }
         break;
      case 1000454876:
         if (nearestTown.equals("[rim_kamaroka]")) {
            var4 = 31;
         }
         break;
      case 1022531306:
         if (nearestTown.equals("[colosseum]")) {
            var4 = 13;
         }
         break;
      case 1087404672:
         if (nearestTown.equals("[gludin_town]")) {
            var4 = 18;
         }
         break;
      case 1188665996:
         if (nearestTown.equals("[monster_race]")) {
            var4 = 35;
         }
         break;
      case 1323661927:
         if (nearestTown.equals("[hunter_town]")) {
            var4 = 48;
         }
         break;
      case 1545263485:
         if (nearestTown.equals("[floran_town]")) {
            var4 = 37;
         }
         break;
      case 1561294149:
         if (nearestTown.equals("[kamaroka]")) {
            var4 = 29;
         }
         break;
      case 1680418449:
         if (nearestTown.equals("[gm_room_solo1]")) {
            var4 = 1;
         }
         break;
      case 1680418480:
         if (nearestTown.equals("[gm_room_solo2]")) {
            var4 = 12;
         }
         break;
      case 1998006438:
         if (nearestTown.equals("[Quarry]")) {
            var4 = 24;
         }
         break;
      case 2101473640:
         if (nearestTown.equals("[southern_wasteland]")) {
            var4 = 43;
         }
         break;
      case 2119791876:
         if (nearestTown.equals("[near_kamaroka]")) {
            var4 = 30;
         }
         break;
      case 2143913969:
         if (nearestTown.equals("[orc_town]")) {
            var4 = 33;
         }
      }

      String text;
      switch(var4) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
         text = "scripts.events.PiratesTreasure.PointInfoUnknown";
         break;
      case 6:
      case 7:
      case 8:
      case 9:
         text = "scripts.events.PiratesTreasure.PointInfoKamaelVillage";
         break;
      case 10:
      case 11:
         text = "scripts.events.PiratesTreasure.PointInfoDarkElvenVillage";
         break;
      case 12:
      case 13:
      case 14:
         text = "scripts.events.PiratesTreasure.PointInfoTownOfAden";
         break;
      case 15:
         text = "scripts.events.PiratesTreasure.PointInfoImperialTomb";
         break;
      case 16:
      case 17:
      case 18:
      case 19:
         text = "scripts.events.PiratesTreasure.PointInfoGludinTown";
         break;
      case 20:
         text = "scripts.events.PiratesTreasure.PointInfoGiranHabor";
         break;
      case 21:
      case 22:
      case 23:
         text = "scripts.events.PiratesTreasure.PointInfoTalkingIslandVillage";
         break;
      case 24:
      case 25:
         text = "scripts.events.PiratesTreasure.PointInfoDwarvenVillage";
         break;
      case 26:
      case 27:
         text = "scripts.events.PiratesTreasure.PointInfoElvenVillage";
         break;
      case 28:
         text = "scripts.events.PiratesTreasure.PointInfoRuneVillage";
         break;
      case 29:
      case 30:
      case 31:
      case 32:
         text = "scripts.events.PiratesTreasure.PointInfoDionCastleTown";
         break;
      case 33:
      case 34:
         text = "scripts.events.PiratesTreasure.PointInfoOrcVillage";
         break;
      case 35:
      case 36:
      case 37:
         text = "scripts.events.PiratesTreasure.PointInfoFloranVillage";
         break;
      case 38:
         text = "scripts.events.PiratesTreasure.PointInfoRift";
         break;
      case 39:
      case 40:
         text = "scripts.events.PiratesTreasure.PointInfoGiranCastleTown";
         break;
      case 41:
         text = "scripts.events.PiratesTreasure.PointInfoTownOfOren";
         break;
      case 42:
         text = "scripts.events.PiratesTreasure.PointInfoGoddartCastleTown";
         break;
      case 43:
         text = "scripts.events.PiratesTreasure.PointInfoSouthernWasteland";
         break;
      case 44:
      case 45:
         text = "scripts.events.PiratesTreasure.PointInfoGludioCastleTown";
         break;
      case 46:
         text = "scripts.events.PiratesTreasure.PointInfoPrimevalIsle";
         break;
      case 47:
         text = "scripts.events.PiratesTreasure.PointInfoTownOfSchuttgart";
         break;
      case 48:
         text = "scripts.events.PiratesTreasure.PointInfoHuntersVillage";
         break;
      case 49:
         text = "scripts.events.PiratesTreasure.PointInfoHeine";
         break;
      default:
         text = "scripts.events.PiratesTreasure.PointInfoUnknown";
      }

      return text;
   }

   public static String getLocationName(GameObject actor) {
      if (actor == null) {
         return "";
      } else {
         RestartArea ra = (RestartArea)MapRegionManager.getInstance().getRegionData(RestartArea.class, actor);
         if (ra != null) {
            RestartPoint rp = (RestartPoint)ra.getRestartPoint().get(Race.human);
            return rp.getName();
         } else {
            return "";
         }
      }
   }

   public static void annoncePointInfo() {
      sayToAll(pointInfo);
      _log.info("PiratesTreasure: announce " + ConfigPiratesTreasure.PiratesTreasureNpcId + " in loc " + pirateNpc.getLoc().toXYZString() + " msg " + pointInfo);
   }

   public static Location getRandomSpawnPoint() {
      List<Location> availableSpawns = new ArrayList();
      Iterator var1 = SpawnManager.getInstance().getSpawners(PeriodOfDay.ALL.name()).iterator();

      while(true) {
         NpcInstance npc;
         Location pos;
         do {
            do {
               do {
                  Spawner spawn;
                  do {
                     do {
                        do {
                           if (!var1.hasNext()) {
                              Location spawnPoint = (Location)availableSpawns.get(Rnd.get(availableSpawns.size()));
                              return spawnPoint;
                           }

                           spawn = (Spawner)var1.next();
                           npc = spawn.getFirstSpawned();
                           if (npc == null) {
                              npc = spawn.getLastSpawn();
                           }
                        } while(npc == null);
                     } while(!npc.isMonster());
                  } while(!npc.getReflection().isDefault());

                  pos = spawn.getCurrentSpawnRange().getRandomLoc(0);
               } while(pos == null);
            } while(pos.getX() < -166168);
         } while(pos.getZ() == 0 && pos.getY() == 0 && pos.getZ() == 0);

         if (!npc.getFaction().getName().equalsIgnoreCase("c_dungeon_clan") && !npc.isInZone(ZoneType.peace_zone) && !npc.isInZone(ZoneType.SIEGE) && !npc.isInZone(ZoneType.RESIDENCE) && !npc.isInZone(ZoneType.water) && !npc.isInZone(ZoneType.epic) && !npc.isInZone(ZoneType.ssq_zone)) {
            availableSpawns.add(pos);
         }
      }
   }

   public static String toSimpleFormat(Date date) {
      return SIMPLE_FORMAT.format(date.getTime());
   }

   public class StartTask extends RunnableImpl {
      public void runImpl() {
         if (PiratesTreasure._active) {
            PiratesTreasure.this.startEvent();
         }
      }
   }
}
