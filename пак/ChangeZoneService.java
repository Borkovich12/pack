package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ConfirmDlg;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeZoneService extends Functions implements ScriptFile {
   public static final String CHANGE_ZONE_PARAM_NAME = "IsChangeZone";
   public static final String CHANGE_ZONE_VISIBLE_NAME_PARAM_NAME = "ChangeZoneVisibleName";
   public static final String CHANGE_ZONE_POINTS_PARAM_NAME = "ChangeZonePoints";
   public static final String CHANGE_ZONE_SPAWN_PARAM_NAME = "ChangeZoneSpawn";
   public static final String CHANGE_ZONE_RACE_PARAM_NAME = "ChangeZoneRaceId";
   private static final Logger _log = LoggerFactory.getLogger(ChangeZoneService.class);
   private static ChangeZoneService _instance;
   private boolean isFirstChange = true;
   private ScheduledFuture<?> zoneChangeTask;
   private Zone _activeZone;
   private final ChangeZoneService.ZoneListener _zoneListener = new ChangeZoneService.ZoneListener();
   private long fightBeginTime;
   private ScheduledFuture<?> _timerTask;
   private List<Zone> _changeZones = new ArrayList();
   private ChangeZoneService.OnPvpPkKillListenerImpl _playerListener = new ChangeZoneService.OnPvpPkKillListenerImpl();
   private Map<Integer, Integer> _bestPvpKillers = new ConcurrentHashMap();

   public static ChangeZoneService getInstance() {
      if (_instance == null) {
         _instance = new ChangeZoneService();
      }

      return _instance;
   }

   public void onLoad() {
      this.load();
   }

   public void onReload() {
      _instance = new ChangeZoneService();
   }

   public void onShutdown() {
      this.shutdown();
   }

   private static void teleToNewActiveChangeZone(Player player, Location changePoint) {
      ThreadPoolManager.getInstance().schedule(() -> {
         if (player != null) {
            boolean result = false;
            String activeChangeZone = ServerVariables.getString("active_change_zone", (String)null);
            Iterator var4 = player.getZones().iterator();

            while(var4.hasNext()) {
               Zone zone = (Zone)var4.next();
               boolean isChangeZone = zone.getParams().isSet("IsChangeZone");
               if (isChangeZone && activeChangeZone != null && zone.getName().equalsIgnoreCase(activeChangeZone)) {
                  result = true;
                  break;
               }
            }

            if (!result) {
               player.teleToLocation(changePoint);
            }

         }
      }, 500L);
   }

   public void load() {
      ConfigChangeZone.load();
      if (ConfigChangeZone.ENABLE_CHANGE_ZONES) {
         ZoneEquipParser.getInstance().load();
         PlayerListenerList.addGlobal(this._playerListener);
         if (ConfigChangeZone.ALLOW_CHANGE_ZONE_TOP_PVP && this._bestPvpKillers != null && !this._bestPvpKillers.isEmpty()) {
            this._bestPvpKillers.clear();
         }

         Collection<Zone> zones = ReflectionManager.DEFAULT.getZones();
         Iterator var2 = zones.iterator();

         while(var2.hasNext()) {
            Zone zone = (Zone)var2.next();
            boolean isChangeZone = zone.getParams().isSet("IsChangeZone");
            if (isChangeZone) {
               String activeChangeZone = ServerVariables.getString("active_change_zone", "");
               if (!zone.getName().equalsIgnoreCase(activeChangeZone)) {
               }

               zone.addListener(this._zoneListener);
               this._changeZones.add(zone);
            }
         }

         this.cancelZoneChangeTask();
         this.zoneChangeTask = ThreadPoolManager.getInstance().schedule(new ChangeZoneService.NextChangeZoneTask(), (long)ConfigChangeZone.INIT_TIME_CHANGE_ZONE * 1000L);
         _log.info("ChangeZoneManager: Loaded " + this._changeZones.size() + " change zone.");
      }
   }

   public void shutdown() {
      if (ConfigChangeZone.ENABLE_CHANGE_ZONES) {
         PlayerListenerList.removeGlobal(this._playerListener);
         this.cancelZoneChangeTask();
         Iterator var1 = ReflectionManager.DEFAULT.getZones().iterator();

         while(var1.hasNext()) {
            Zone zone = (Zone)var1.next();
            boolean isChangeZone = zone.getParams().isSet("IsChangeZone");
            if (isChangeZone) {
               String spawnGroup = zone.getParams().getString("ChangeZoneSpawn", (String)null);
               if (spawnGroup != null && !spawnGroup.isEmpty()) {
                  SpawnManager.getInstance().despawn(spawnGroup);
               }

               zone.removeListener(this._zoneListener);
            }
         }

      }
   }

   public void changeZones() {
      this.cancelZoneChangeTask();
      _log.info("ChangeZoneManager: start changing zones...");

      try {
         String activeChangeZone;
         if (this._activeZone != null) {
            activeChangeZone = this._activeZone.getParams().getString("ChangeZoneSpawn", (String)null);
            if (activeChangeZone != null && !activeChangeZone.isEmpty()) {
               SpawnManager.getInstance().despawn(activeChangeZone);
            }

            this._activeZone = null;
         }

         this.determineWinners();
         this.changeZoneParams();
         activeChangeZone = ServerVariables.getString("active_change_zone", "");
         if (this.checkIsEmptyAndGeneratedNewActiveZones(activeChangeZone)) {
            return;
         }

         Zone newActiveChangeZone = this.setNewActiveChangeZone();
         List<Player> insidePlayers = getAllInsidePlayersInOldZone(activeChangeZone);
         teleportInsidePlayersInNewZone(insidePlayers, newActiveChangeZone);
         this.sendTeleportRequest(insidePlayers, newActiveChangeZone);
         this.setAndActiveteNewZone(newActiveChangeZone);
         ServerVariables.set("active_change_zone", newActiveChangeZone.getName());
         this.announceSetChangeZone(newActiveChangeZone);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      this.zoneChangeTask = ThreadPoolManager.getInstance().schedule(new ChangeZoneService.NextChangeZoneTask(), (long)ConfigChangeZone.TIME_TO_CHANGE_ZONE * 60000L);
      _log.info("ChangeZoneManager: end changing zones...");
   }

   private void determineWinners() {
      if (ConfigChangeZone.ALLOW_CHANGE_ZONE_TOP_PVP) {
         if (!this._bestPvpKillers.isEmpty()) {
            List<Entry<Integer, Integer>> list = new ArrayList(this._bestPvpKillers.entrySet());
            Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
               public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
                  return ((Integer)o2.getValue()).compareTo((Integer)o1.getValue());
               }
            });
            if (ConfigChangeZone.ALLOW_CHANGE_ZONE_TOP_PVP_ANNOUNCE) {
               Announcements.getInstance().announceByCustomMessage("changeZone.determineWinnersAnnounce", (String[])null);
            }

            int place = 1;

            for(Iterator var3 = list.iterator(); var3.hasNext(); ++place) {
               Entry<Integer, Integer> entry = (Entry)var3.next();
               if (place > ConfigChangeZone.CHANGE_ZONE_PVP_WINNERS.size()) {
                  break;
               }

               int objectId = (Integer)entry.getKey();
               int pvpCount = (Integer)entry.getValue();
               int[] rewards = (int[])ConfigChangeZone.CHANGE_ZONE_PVP_WINNERS.get(place);
               Player player = GameObjectsStorage.getPlayer(objectId);
               int i;
               int itemId;
               int itemCount;
               if (player != null) {
                  for(i = 0; i < rewards.length; i += 2) {
                     itemId = rewards[i];
                     itemCount = rewards[i + 1];
                     ItemFunctions.addItem(player, itemId, (long)itemCount, true);
                  }

                  player.sendMessage((new CustomMessage("changeZone.winnerMsg", player, new Object[0])).addNumber((long)place).addNumber((long)pvpCount));
                  if (ConfigChangeZone.ALLOW_CHANGE_ZONE_TOP_PVP_ANNOUNCE) {
                     Announcements.getInstance().announceByCustomMessage("changeZone.winnerAnnounce", new String[]{String.valueOf(place), player.getName(), String.valueOf(pvpCount)});
                  }
               } else {
                  for(i = 0; i < rewards.length; i += 2) {
                     itemId = rewards[i];
                     itemCount = rewards[i + 1];
                     DelayedItemsManager.getInstance().addDelayed(objectId, itemId, itemCount, 0, 0, 0, "Change zone add reward for " + place + " place");
                  }

                  if (ConfigChangeZone.ALLOW_CHANGE_ZONE_TOP_PVP_ANNOUNCE) {
                     String name = CharacterDAO.getInstance().getNameByObjectId(objectId);
                     Announcements.getInstance().announceByCustomMessage("changeZone.winnerAnnounce", new String[]{String.valueOf(place), name, String.valueOf(pvpCount)});
                  }
               }
            }

            this._bestPvpKillers.clear();
         } else if (ConfigChangeZone.ALLOW_CHANGE_ZONE_TOP_PVP_ANNOUNCE) {
            Announcements.getInstance().announceByCustomMessage("changeZone.notDetermineWinnersAnnounce", (String[])null);
         }

      }
   }

   private void setAndActiveteNewZone(Zone newActiveChangeZone) {
      if (this._activeZone == null) {
         this._activeZone = newActiveChangeZone;
         String spawnGroup = this._activeZone.getParams().getString("ChangeZoneSpawn", (String)null);
         if (spawnGroup != null && !spawnGroup.isEmpty()) {
            SpawnManager.getInstance().spawn(spawnGroup);
         }

         this.broadcastTimerInNewZone();
      }

   }

   private boolean checkIsEmptyAndGeneratedNewActiveZones(String activeChangeZone) {
      if (this._changeZones.isEmpty()) {
         Iterator var2 = ReflectionManager.DEFAULT.getZones().iterator();

         while(var2.hasNext()) {
            Zone zone = (Zone)var2.next();
            boolean isChangeZone = zone.getParams().isSet("IsChangeZone");
            if (isChangeZone && !zone.getName().equalsIgnoreCase(activeChangeZone)) {
               this._changeZones.add(zone);
            }
         }

         if (this._changeZones.isEmpty()) {
            _log.warn("ChangeZoneManager: New change zone not found.");
            return true;
         }
      }

      return false;
   }

   private Zone setNewActiveChangeZone() {
      if (this._changeZones.size() > 1) {
         Collections.shuffle(this._changeZones);
      }

      return (Zone)this._changeZones.remove(0);
   }

   private static List<Player> getAllInsidePlayersInOldZone(String activeChangeZone) {
      List<Player> insidePlayers = new ArrayList();
      Zone oldZone = ReflectionUtils.getZone(activeChangeZone);
      if (oldZone != null) {
         insidePlayers.addAll(oldZone.getInsidePlayers());
      }

      return insidePlayers;
   }

   private void announceSetChangeZone(Zone newActiveChangeZone) {
      String zoneName = newActiveChangeZone.getParams().getString("ChangeZoneVisibleName", (String)null) != null ? newActiveChangeZone.getParams().getString("ChangeZoneVisibleName") : newActiveChangeZone.getName();
      if (this.isFirstChange) {
         Announcements.getInstance().announceByCustomMessage("changeZone.firstChange", new String[]{zoneName});
         _log.info("ChangeZoneManager: appointed active change zone " + newActiveChangeZone);
         this.isFirstChange = false;
      } else {
         Announcements.getInstance().announceByCustomMessage("changeZone.otherChange", new String[]{zoneName});
         _log.info("ChangeZoneManager: new active change zone " + newActiveChangeZone);
      }

   }

   private void broadcastTimerInNewZone() {
      if (ConfigChangeZone.BROADCAST_TIMER_IN_ACTIVE_CHANGE_ZONE) {
         this.cancelTimer();
         this.fightBeginTime = System.currentTimeMillis();
         this._timerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ChangeZoneService.BattleTimer(this._activeZone), 0L, 1000L);
      }

   }

   private static void teleportInsidePlayersInNewZone(List<Player> insidePlayers, Zone newActiveChangeZone) {
      Iterator var2 = insidePlayers.iterator();

      while(true) {
         Player player;
         String pointStr;
         do {
            do {
               if (!var2.hasNext()) {
                  return;
               }

               player = (Player)var2.next();
               pointStr = newActiveChangeZone.getParams().getString("ChangeZonePoints", (String)null);
            } while(pointStr == null);
         } while(pointStr.isEmpty());

         String[] points = pointStr.split(";");
         List<Location> changePoints = new ArrayList(points.length);
         String[] var7 = points;
         int var8 = points.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String point = var7[var9];
            changePoints.add(Location.parseLoc(point));
         }

         Location changePoint = (Location)Rnd.get(changePoints);
         teleToNewActiveChangeZone(player, changePoint);
      }
   }

   private void sendTeleportRequest(List<Player> insidePlayers, Zone newActiveChangeZone) {
      if (ConfigChangeZone.SEND_REQUEST_TELEPORT_CHANGE_ZONE) {
         Iterator var3 = GameObjectsStorage.getAllPlayersForIterate().iterator();

         while(var3.hasNext()) {
            Player player = (Player)var3.next();
            if (this.isCheckSendWindow(insidePlayers, player)) {
               String activeZoneName = newActiveChangeZone.getParams().getString("ChangeZoneVisibleName", (String)null) != null ? newActiveChangeZone.getParams().getString("ChangeZoneVisibleName") : newActiveChangeZone.getName();
               ConfirmDlg packet = (ConfirmDlg)(new ConfirmDlg(SystemMsg.S1, ConfigChangeZone.SECONDS_REQUEST_TELEPORT_CHANGE_ZONE * 1000)).addString((new CustomMessage("changeZone.askPlayer", player, new Object[0])).addString(activeZoneName).toString());
               player.ask(packet, new ChangeZoneService.AnswerListener(player, newActiveChangeZone));
            }
         }
      }

   }

   private boolean isCheckSendWindow(List<Player> insidePlayers, Player player) {
      if (player == null) {
         return false;
      } else if (insidePlayers.contains(player)) {
         return false;
      } else if (!player.getReflection().isDefault()) {
         return false;
      } else if (player.isOlyParticipant()) {
         return false;
      } else if (player.isCursedWeaponEquipped()) {
         return false;
      } else if (player.isInObserverMode()) {
         return false;
      } else {
         return player.getTeam() == TeamType.NONE;
      }
   }

   private void changeZoneParams() {
   }

   public void broadCastTimer(Zone zone) {
      int secondsLeft = (int)((this.fightBeginTime + (long)(ConfigChangeZone.TIME_TO_CHANGE_ZONE * 60 * 1000) - System.currentTimeMillis()) / 1000L);
      int minutes = secondsLeft / 60;
      int seconds = secondsLeft % 60;
      ExShowScreenMessage packet = new ExShowScreenMessage(String.format("%02d:%02d", minutes, seconds), 1000, -1, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, false);
      Iterator var6 = zone.getInsidePlayers().iterator();

      while(var6.hasNext()) {
         Player player = (Player)var6.next();
         player.sendPacket(packet);
      }

   }

   public void cancelZoneChangeTask() {
      if (this.zoneChangeTask != null) {
         this.zoneChangeTask.cancel(false);
         this.zoneChangeTask = null;
      }

   }

   public void cancelTimer() {
      if (this._timerTask != null) {
         this._timerTask.cancel(true);
         this._timerTask = null;
      }

   }

   public void addPvpKill(int objectId) {
      if (ConfigChangeZone.ALLOW_CHANGE_ZONE_TOP_PVP) {
         Integer pvpCount = (Integer)this._bestPvpKillers.get(objectId);
         if (pvpCount == null) {
            this._bestPvpKillers.put(objectId, 1);
         } else {
            this._bestPvpKillers.replace(objectId, pvpCount + 1);
         }

      }
   }

   private void pvpReward(Player killer, Player victim) {
      if ((!ConfigChangeZone.CHANGE_ZONE_PVP_REWARD_HWID || victim.getNetConnection() == null || killer.getNetConnection() == null || !victim.getNetConnection().getHwid().equals(killer.getNetConnection().getHwid())) && (!ConfigChangeZone.CHANGE_ZONE_PVP_REWARD_IP || !victim.getIP().equals(killer.getIP()))) {
         if (ConfigChangeZone.ALLOW_CHANGE_ZONE_PVP_REWARD && killer.getLevel() - victim.getLevel() <= ConfigChangeZone.CHANGE_ZONE_PVP_REWARD_LVL_DIFF && (!killer._lastRewPvP.containsKey(victim.getObjectId()) || (Long)killer._lastRewPvP.get(victim.getObjectId()) + ConfigChangeZone.CHANGE_ZONE_PVP_REWARD_TIME < System.currentTimeMillis())) {
            for(int i = 0; i < ConfigChangeZone.CHANGE_ZONE_PVP_REWARD.length; i += 2) {
               killer.getInventory().addItem(ConfigChangeZone.CHANGE_ZONE_PVP_REWARD[i], (long)ConfigChangeZone.CHANGE_ZONE_PVP_REWARD[i + 1]);
               killer.sendPacket(SystemMessage.obtainItems(ConfigChangeZone.CHANGE_ZONE_PVP_REWARD[i], (long)ConfigChangeZone.CHANGE_ZONE_PVP_REWARD[i + 1], 0));
            }

            this.addPvpKill(killer.getObjectId());
            killer._lastRewPvP.put(victim.getObjectId(), System.currentTimeMillis());
         }

      }
   }

   public void teleToChangeZone() {
      Player player = this.getSelf();
      if (player != null) {
         if (!ConfigChangeZone.ENABLE_CHANGE_ZONES) {
            if (player.isLangRus()) {
               player.sendMessage("Данная функция отключена.");
            } else {
               player.sendMessage("This feature is disabled.");
            }

         } else {
            String activeChangeZone = ServerVariables.getString("active_change_zone", (String)null);
            if (activeChangeZone != null && !activeChangeZone.isEmpty()) {
               Zone activeZone = ReflectionUtils.getZone(activeChangeZone);
               if (activeZone != null) {
                  String pointStr = activeZone.getParams().getString("ChangeZonePoints", (String)null);
                  if (pointStr != null && !pointStr.isEmpty()) {
                     String[] points = pointStr.split(";");
                     List<Location> changePoints = new ArrayList(points.length);
                     String[] var7 = points;
                     int var8 = points.length;

                     for(int var9 = 0; var9 < var8; ++var9) {
                        String point = var7[var9];
                        changePoints.add(Location.parseLoc(point));
                     }

                     Location changePoint = (Location)Rnd.get(changePoints);
                     player.teleToLocation(changePoint);
                  }
               }

            } else {
               if (player.isLangRus()) {
                  player.sendMessage("Зона не установлена.");
               } else {
                  player.sendMessage("The zone is not set.");
               }

            }
         }
      }
   }

   public class OnPvpPkKillListenerImpl implements OnDeathListener {
      public void onDeath(Creature actor, Creature killer) {
         if (killer != null && actor != null && killer.isPlayer() && actor.isPlayer() && killer != actor) {
            if (killer.getKarma() <= 0) {
               Player playerKiller = killer.getPlayer();
               Player victim = actor.getPlayer();
               boolean result = false;
               String activeChangeZone = ServerVariables.getString("active_change_zone", (String)null);
               Iterator var7 = playerKiller.getZones().iterator();

               while(var7.hasNext()) {
                  Zone zone = (Zone)var7.next();
                  boolean isChangeZone = zone.getParams().isSet("IsChangeZone");
                  if (isChangeZone && zone.checkIfInZone(victim) && activeChangeZone != null && zone.getName().equalsIgnoreCase(activeChangeZone)) {
                     result = true;
                     break;
                  }
               }

               if (result) {
                  ChangeZoneService.this.pvpReward(playerKiller, victim);
               }

            }
         }
      }
   }

   public class BattleTimer extends RunnableImpl {
      private final Zone _activeZone;

      protected BattleTimer(Zone zone) {
         this._activeZone = zone;
      }

      public void runImpl() {
         ChangeZoneService.this.broadCastTimer(this._activeZone);
      }
   }

   private class AnswerListener implements OnAnswerListener {
      private final HardReference<Player> _playerRef;
      private final Zone _changeZone;

      private AnswerListener(Player player, Zone zone) {
         this._playerRef = player.getRef();
         this._changeZone = zone;
      }

      public void sayYes() {
         Player player;
         if ((player = (Player)this._playerRef.get()) != null) {
            if (this._changeZone != null) {
               String pointStr = this._changeZone.getParams().getString("ChangeZonePoints", (String)null);
               if (pointStr != null && !pointStr.isEmpty()) {
                  String[] points = pointStr.split(";");
                  List<Location> changePoints = new ArrayList(points.length);
                  String[] var5 = points;
                  int var6 = points.length;

                  for(int var7 = 0; var7 < var6; ++var7) {
                     String point = var5[var7];
                     changePoints.add(Location.parseLoc(point));
                  }

                  Location changePoint = (Location)Rnd.get(changePoints);
                  player.teleToLocation(changePoint);
               }

            }
         }
      }

      public void sayNo() {
      }

      // $FF: synthetic method
      AnswerListener(Player x1, Zone x2, Object x3) {
         this(x1, x2);
      }
   }

   public class NextChangeZoneTask extends RunnableImpl {
      public void runImpl() throws Exception {
         ChangeZoneService.this.changeZones();
      }
   }

   private final class ZoneListener implements OnZoneEnterLeaveListener {
      private ZoneListener() {
      }

      public void onZoneEnter(Zone zone, Creature cha) {
         if (ConfigChangeZone.ENABLE_CHANGE_ZONES) {
            boolean isChangeZone = zone.getParams().isSet("IsChangeZone");
            if (cha.isPlayer() && isChangeZone) {
               Player player = cha.getPlayer();
               String activeChangeZone = ServerVariables.getString("active_change_zone", "");
               if (!zone.getName().equalsIgnoreCase(activeChangeZone)) {
                  Zone activeZone = ReflectionUtils.getZone(activeChangeZone);
                  if (activeZone != null) {
                     String pointStr = activeZone.getParams().getString("ChangeZonePoints", (String)null);
                     if (pointStr != null && !pointStr.isEmpty()) {
                        String[] points = pointStr.split(";");
                        List<Location> changePoints = new ArrayList(points.length);
                        String[] var10 = points;
                        int var11 = points.length;

                        for(int var12 = 0; var12 < var11; ++var12) {
                           String point = var10[var12];
                           changePoints.add(Location.parseLoc(point));
                        }

                        Location changePoint = (Location)Rnd.get(changePoints);
                        ChangeZoneService.teleToNewActiveChangeZone(player, changePoint);
                     }
                  }
               } else if (ConfigChangeZone.SEND_MESSAGE_TIME_WHEN_ENTER_ZONE) {
                  int secondsLeft = (int)((ChangeZoneService.this.fightBeginTime + (long)(ConfigChangeZone.TIME_TO_CHANGE_ZONE * 60 * 1000) - System.currentTimeMillis()) / 1000L);
                  int minutes = secondsLeft / 60;
                  int seconds = secondsLeft % 60;
                  if (player.isLangRus()) {
                     player.sendMessage("До смены зоны осталось " + minutes + " мин. " + seconds + " сек.");
                  } else {
                     player.sendMessage(minutes + " minutes " + seconds + " seconds left before zone change.");
                  }
               }
            }

         }
      }

      public void onZoneLeave(Zone zone, Creature cha) {
      }

      // $FF: synthetic method
      ZoneListener(Object x1) {
         this();
      }
   }
}
