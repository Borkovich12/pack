package events.TopKiller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPvpPkKillListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.NpcUtils;
import l2.gameserver.utils.TimeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopKiller extends Functions implements ScriptFile, IVoicedCommandHandler {
   private static final Logger _log = LoggerFactory.getLogger(TopKiller.class);
   private static String[] _commandList;
   private static ScheduledFuture<?> _scheduledEventEnd;
   private static long _collectionEnd;
   private static Map<Integer, KillerData> _score = new HashMap();
   private static List<NpcInstance> _manager = null;
   private static final TopKiller.ValuesComparator _valuesComparator = new TopKiller.ValuesComparator();
   private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   private static final Lock readLock;
   private static final Lock writeLock;
   private static boolean _active;
   private static final OnDeathListener _listener;

   private static boolean isActive() {
      return IsActive("TopKiller");
   }

   public void startEvent() {
      Player player = this.getSelf();
      if (player.getPlayerAccess().IsEventGm) {
         if (SetActive("TopKiller", true)) {
            this.spawnEventManagers();
            this.load();
            _log.info("Event: Top Killer started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TopKiller.AnnounceEventStarted", (String[])null);
         } else {
            player.sendMessage("Event 'Top Killer' already started.");
         }

         _active = true;
         this.show("admin/events/events.htm", player);
      }
   }

   public void stopEvent() {
      Player player = this.getSelf();
      if (player.getPlayerAccess().IsEventGm) {
         if (SetActive("TopKiller", false)) {
            this.unSpawnEventManagers();
            stop();
            _log.info("Event: Top Killer stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TopKiller.AnnounceEventStopped", (String[])null);
         } else {
            player.sendMessage("Event 'Top Killer' not started.");
         }

         _active = false;
         this.show("admin/events/events.htm", player);
      }
   }

   public static void writeLock() {
      writeLock.lock();
   }

   public static void writeUnlock() {
      writeLock.unlock();
   }

   public static void readLock() {
      readLock.lock();
   }

   public static void readUnlock() {
      readLock.unlock();
   }

   public static Map<Integer, KillerData> getScore() {
      readLock();

      Map var0;
      try {
         var0 = _score;
      } finally {
         readUnlock();
      }

      return var0;
   }

   private void spawnEventManagers() {
      if (ConfigTopKiller.TOP_KILLER_MANAGER_NPC_ID > 0) {
         _manager = new ArrayList();

         for(int i = 0; i < ConfigTopKiller.TOP_KILLER_MANAGER_SPAWN.length; i += 3) {
            int x = ConfigTopKiller.TOP_KILLER_MANAGER_SPAWN[i];
            int y = ConfigTopKiller.TOP_KILLER_MANAGER_SPAWN[i + 1];
            int z = ConfigTopKiller.TOP_KILLER_MANAGER_SPAWN[i + 2];
            _manager.add(NpcUtils.spawnSingle(ConfigTopKiller.TOP_KILLER_MANAGER_NPC_ID, new Location(x, y, z)));
         }
      }

   }

   private void unSpawnEventManagers() {
      if (ConfigTopKiller.TOP_KILLER_MANAGER_NPC_ID > 0 && _manager != null) {
         Iterator var1 = _manager.iterator();

         while(var1.hasNext()) {
            NpcInstance manager = (NpcInstance)var1.next();
            manager.deleteMe();
         }

         _manager.clear();
         _manager = null;
      }

   }

   private void doEndAction() {
      _active = false;
      Map<Integer, Integer> dinoScore = new HashMap();
      Map<Integer, Integer> pvpScore = new HashMap();
      Map<Integer, Integer> pkScore = new HashMap();
      Map<Integer, KillerData> score = new HashMap(getScore());
      Iterator var5 = score.entrySet().iterator();

      while(var5.hasNext()) {
         Entry<Integer, KillerData> entry = (Entry)var5.next();
         int objId = (Integer)entry.getKey();
         KillerData killerData = (KillerData)entry.getValue();
         if (killerData.getDinoPoint() > 0) {
            dinoScore.put(objId, killerData.getDinoPoint());
         }

         if (killerData.getPvpPoint() > 0) {
            pvpScore.put(objId, killerData.getPvpPoint());
         }

         if (killerData.getPkPoint() > 0) {
            pkScore.put(objId, killerData.getPkPoint());
         }
      }

      List<Entry<Integer, Integer>> dinoScoreList = new ArrayList(dinoScore.entrySet());
      if (dinoScoreList.size() > 1) {
         Collections.sort(dinoScoreList, _valuesComparator);
      }

      List<Entry<Integer, Integer>> pvpScoreList = new ArrayList(pvpScore.entrySet());
      if (pvpScoreList.size() > 1) {
         Collections.sort(pvpScoreList, _valuesComparator);
      }

      List<Entry<Integer, Integer>> pkScoreList = new ArrayList(pkScore.entrySet());
      if (pkScoreList.size() > 1) {
         Collections.sort(pkScoreList, _valuesComparator);
      }

      if (ConfigTopKiller.TOP_KILLER_DINO_REWARD.size() > 0 && dinoScoreList.size() > 0) {
         Announcements.getInstance().announceByCustomMessage("scripts.events.TopKiller.AnnounceEventDinoEnd", (String[])null);
         giveReward(dinoScoreList, ConfigTopKiller.TOP_KILLER_DINO_REWARD, "scripts.events.TopKiller.AnnounceEventDinoWinner", "Dino");
      }

      if (ConfigTopKiller.TOP_KILLER_PVP_REWARD.size() > 0 && pvpScoreList.size() > 0) {
         Announcements.getInstance().announceByCustomMessage("scripts.events.TopKiller.AnnounceEventPvpEnd", (String[])null);
         giveReward(pvpScoreList, ConfigTopKiller.TOP_KILLER_PVP_REWARD, "scripts.events.TopKiller.AnnounceEventPvPWinner", "Pvp");
      }

      if (ConfigTopKiller.TOP_KILLER_PK_REWARD.size() > 0 && pkScoreList.size() > 0) {
         Announcements.getInstance().announceByCustomMessage("scripts.events.TopKiller.AnnounceEventPkEnd", (String[])null);
         giveReward(pkScoreList, ConfigTopKiller.TOP_KILLER_PK_REWARD, "scripts.events.TopKiller.AnnounceEventPkWinner", "Pk");
      }

      ThreadPoolManager.getInstance().schedule(new Runnable() {
         public void run() {
            TopKiller.clearScores();
            TopKiller.setNewCollectionEnd();
            if (TopKiller._scheduledEventEnd != null) {
               TopKiller._scheduledEventEnd.cancel(false);
            }

            TopKiller._scheduledEventEnd = ThreadPoolManager.getInstance().schedule(TopKiller.this.new CollectionEndTask(), TopKiller.getMillisToCollectionEnd());
            TopKiller._active = true;
            Announcements.getInstance().announceByCustomMessage("scripts.events.TopKiller.AnnounceEventStart", (String[])null);
         }
      }, (long)ConfigTopKiller.TOP_KILLER_EVENT_DELETE_DELAY * 1000L);
   }

   private static void giveReward(List<Entry<Integer, Integer>> scoreList, Map<Integer, int[]> rewardMap, String address, String typeStr) {
      int place = 1;

      for(Iterator var5 = scoreList.iterator(); var5.hasNext(); ++place) {
         Entry<Integer, Integer> entry = (Entry)var5.next();
         int[] reward = (int[])rewardMap.get(place);
         if (reward == null) {
            break;
         }

         int objectId = (Integer)entry.getKey();
         int score = (Integer)entry.getValue();
         Player player = GameObjectsStorage.getPlayer(objectId);
         if (player != null && player.isOnline()) {
            Announcements.getInstance().announceByCustomMessage(address, new String[]{String.valueOf(place), player.getName(), String.valueOf(score)});

            for(int i = 0; i < reward.length; i += 2) {
               addItem(player, reward[i], (long)reward[i + 1]);
            }

            Log.add("player=" + player.getName() + "(" + player.getObjectId() + ") place=" + place, "TopKiller" + typeStr + "_reward");
            if (typeStr.equalsIgnoreCase("Dino")) {
               player.getListeners().onTopKillerDinoEvent(place);
            } else if (typeStr.equalsIgnoreCase("Pvp")) {
               player.getListeners().onTopKillerPvpEvent(place);
            } else if (typeStr.equalsIgnoreCase("Pk")) {
               player.getListeners().onTopKillerPkEvent(place);
            }
         } else {
            String name = CharacterDAO.getInstance().getNameByObjectId(objectId);
            Announcements.getInstance().announceByCustomMessage(address, new String[]{String.valueOf(place), name, String.valueOf(score)});

            for(int i = 0; i < reward.length; i += 2) {
               DelayedItemsManager.getInstance().addDelayed(objectId, reward[i], reward[i + 1], 0, 0, 0, "<CollectRew>");
            }

            Log.add("player=" + name + "(" + objectId + ") place=" + place, "TopKiller" + typeStr + "_reward");
         }
      }

   }

   private static long getMillisToCollectionEnd() {
      return _collectionEnd - System.currentTimeMillis();
   }

   private static void setNewCollectionEnd() {
      Calendar currentTime = Calendar.getInstance();
      currentTime.add(6, ConfigTopKiller.TOP_KILLER_EVENT_DAYS);
      currentTime.set(11, ConfigTopKiller.TOP_KILLER_EVENT_HOUR_OF_DAY);
      currentTime.set(12, ConfigTopKiller.TOP_KILLER_EVENT_MINUTE);
      currentTime.set(13, 10);
      _collectionEnd = currentTime.getTimeInMillis();
      ServerVariables.set("TopKiller_End", _collectionEnd);
   }

   public void board() {
      Player player = this.getSelf();
      if (player != null) {
         NpcInstance npc = null;
         if (ConfigTopKiller.TOP_KILLER_MANAGER_NPC_ID > 0) {
            npc = this.getNpc();
            if (!NpcInstance.canBypassCheck(player, npc) || npc.getNpcId() != ConfigTopKiller.TOP_KILLER_MANAGER_NPC_ID) {
               return;
            }
         }

         String htmFile = "dino";
         sendBoard(player, npc, htmFile);
      }
   }

   public void board(String[] args) {
      Player player = this.getSelf();
      if (player != null) {
         NpcInstance npc = null;
         String htmFile = args[0];
         sendBoard(player, (NpcInstance)npc, htmFile);
      }
   }

   public static void sendBoard(Player player, NpcInstance npc, String htmFile) {
      List<Entry<Integer, Integer>> scoreList = null;
      Map<Integer, KillerData> score = new HashMap(getScore());
      HashMap pkScore;
      Iterator var6;
      Entry entry;
      int objId;
      KillerData killerData;
      if (htmFile.equalsIgnoreCase("dino")) {
         pkScore = new HashMap();
         var6 = score.entrySet().iterator();

         while(var6.hasNext()) {
            entry = (Entry)var6.next();
            objId = (Integer)entry.getKey();
            killerData = (KillerData)entry.getValue();
            if (killerData.getDinoPoint() > 0) {
               pkScore.put(objId, killerData.getDinoPoint());
            }
         }

         scoreList = new ArrayList(pkScore.entrySet());
         Collections.sort(scoreList, _valuesComparator);
      } else if (htmFile.equalsIgnoreCase("pvp")) {
         pkScore = new HashMap();
         var6 = score.entrySet().iterator();

         while(var6.hasNext()) {
            entry = (Entry)var6.next();
            objId = (Integer)entry.getKey();
            killerData = (KillerData)entry.getValue();
            if (killerData.getPvpPoint() > 0) {
               pkScore.put(objId, killerData.getPvpPoint());
            }
         }

         scoreList = new ArrayList(pkScore.entrySet());
         Collections.sort(scoreList, _valuesComparator);
      } else if (htmFile.equalsIgnoreCase("pk")) {
         pkScore = new HashMap();
         var6 = score.entrySet().iterator();

         while(var6.hasNext()) {
            entry = (Entry)var6.next();
            objId = (Integer)entry.getKey();
            killerData = (KillerData)entry.getValue();
            if (killerData.getPkPoint() > 0) {
               pkScore.put(objId, killerData.getPkPoint());
            }
         }

         scoreList = new ArrayList(pkScore.entrySet());
         Collections.sort(scoreList, _valuesComparator);
      }

      if (scoreList != null) {
         NpcHtmlMessage reply = new NpcHtmlMessage(player, npc);
         String htm = HtmCache.getInstance().getNotNull("mods/TopKiller/" + htmFile + ".htm", player);
         String htmBody = HtmCache.getInstance().getNotNull("mods/TopKiller/body.htm", player);
         String htmBodyEmpty = HtmCache.getInstance().getNotNull("mods/TopKiller/bodyEmpty.htm", player);
         String htmList = HtmCache.getInstance().getNotNull("mods/TopKiller/list.htm", player);
         int index;
         if (scoreList.size() > 0) {
            StringBuilder temp = new StringBuilder();
            index = 1;

            for(Iterator var12 = scoreList.iterator(); var12.hasNext(); ++index) {
               Entry<Integer, Integer> data = (Entry)var12.next();
               int objectId = (Integer)data.getKey();
               String name = CharacterDAO.getInstance().getNameByObjectId(objectId);
               String text = htmList.replace("%index%", String.valueOf(index));
               text = text.replace("%name%", name);
               text = text.replace("%count%", String.valueOf(data.getValue()));
               temp.append(text);
            }

            htmBody = htmBody.replace("%list%", temp.toString());
         }

         KillerData killerData = (KillerData)score.get(player.getObjectId());
         index = 0;
         int myPvpPoints = 0;
         int myPkPoints = 0;
         if (killerData != null) {
            index = killerData.getDinoPoint();
            myPvpPoints = killerData.getPvpPoint();
            myPkPoints = killerData.getPkPoint();
         }

         htm = htm.replace("%body%", scoreList.size() > 0 ? htmBody : htmBodyEmpty);
         htm = htm.replace("%my_dino_points%", String.valueOf(index));
         htm = htm.replace("%my_pvp_points%", String.valueOf(myPvpPoints));
         htm = htm.replace("%my_pk_points%", String.valueOf(myPkPoints));
         htm = htm.replace("%end_data%", TimeUtils.toSimpleFormat(_collectionEnd));
         reply.setHtml(htm);
         player.sendPacket(reply);
      }
   }

   private static int incSaveAndGetPvpPoints(Player killer) {
      writeLock();

      int var1;
      try {
         var1 = ((KillerData)_score.compute(killer.getObjectId(), (key, existingKillerData) -> {
            if (existingKillerData == null) {
               existingKillerData = new KillerData(killer.getObjectId());
            }

            boolean isNew = existingKillerData.isNew();
            int newPoint = existingKillerData.getPvpPoint() + 1;
            existingKillerData.setPvpPoint(newPoint);
            if (isNew) {
               TopKillerDAO.getInstance().saveTopKiller(existingKillerData);
            } else {
               TopKillerDAO.getInstance().updateTopKiller(existingKillerData);
            }

            return existingKillerData;
         })).getPvpPoint();
      } finally {
         writeUnlock();
      }

      return var1;
   }

   private static int incSaveAndGetPkPoints(Player killer) {
      writeLock();

      int var1;
      try {
         var1 = ((KillerData)_score.compute(killer.getObjectId(), (key, existingKillerData) -> {
            if (existingKillerData == null) {
               existingKillerData = new KillerData(killer.getObjectId());
            }

            boolean isNew = existingKillerData.isNew();
            int newPoint = existingKillerData.getPkPoint() + 1;
            existingKillerData.setPkPoint(newPoint);
            if (isNew) {
               TopKillerDAO.getInstance().saveTopKiller(existingKillerData);
            } else {
               TopKillerDAO.getInstance().updateTopKiller(existingKillerData);
            }

            return existingKillerData;
         })).getPkPoint();
      } finally {
         writeUnlock();
      }

      return var1;
   }

   private static int incSaveAndGetDinoPoints(Player killer) {
      writeLock();

      int var1;
      try {
         var1 = ((KillerData)_score.compute(killer.getObjectId(), (key, existingKillerData) -> {
            if (existingKillerData == null) {
               existingKillerData = new KillerData(killer.getObjectId());
            }

            boolean isNew = existingKillerData.isNew();
            int newPoint = existingKillerData.getDinoPoint() + 1;
            existingKillerData.setDinoPoint(newPoint);
            if (isNew) {
               TopKillerDAO.getInstance().saveTopKiller(existingKillerData);
            } else {
               TopKillerDAO.getInstance().updateTopKiller(existingKillerData);
            }

            return existingKillerData;
         })).getDinoPoint();
      } finally {
         writeUnlock();
      }

      return var1;
   }

   public String[] getVoicedCommandList() {
      return _commandList;
   }

   public boolean useVoicedCommand(String command, Player player, String args) {
      if (!ConfigTopKiller.TOP_KILLER_VOICE_COMMAND_ENABLE) {
         return false;
      } else if (command.equalsIgnoreCase(_commandList[0])) {
         sendBoard(player, (NpcInstance)null, "dino");
         return true;
      } else {
         return false;
      }
   }

   public void onLoad() {
      ConfigTopKiller.load();
      if (isActive()) {
         _active = true;
         this.spawnEventManagers();
         this.load();
         _log.info("Loaded Event: Top Killer [state: activated]");
      } else {
         _log.info("Loaded Event: Top Killer [state: deactivated]");
      }

   }

   private void load() {
      if (ConfigTopKiller.TOP_KILLER_ENABLE) {
         _collectionEnd = ServerVariables.getLong("TopKiller_End", -1L);
         if (_collectionEnd <= 0L) {
            setNewCollectionEnd();
         } else if (_collectionEnd < Calendar.getInstance().getTimeInMillis()) {
            clearScores();
            setNewCollectionEnd();
         }

         Iterator var1 = TopKillerDAO.getInstance().loadTopKillers().iterator();

         while(var1.hasNext()) {
            KillerData killerData = (KillerData)var1.next();
            _score.put(killerData.getObjectId(), killerData);
         }

         if (_scheduledEventEnd != null) {
            _scheduledEventEnd.cancel(false);
         }

         _scheduledEventEnd = ThreadPoolManager.getInstance().schedule(new TopKiller.CollectionEndTask(), getMillisToCollectionEnd());
         CharListenerList.addGlobal(_listener);
         if (ConfigTopKiller.TOP_KILLER_VOICE_COMMAND_ENABLE) {
            _commandList = new String[]{ConfigTopKiller.TOP_KILLER_VOICE_COMMAND};
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
         }

         _log.info("Loaded Event: Top Killer event.");
      }

   }

   private static void stop() {
      clearScores();
   }

   private static void clearScores() {
      writeLock();

      try {
         _score.clear();
         TopKillerDAO.getInstance().clearTopKillersData();
      } finally {
         writeUnlock();
      }

   }

   public void onReload() {
   }

   public void onShutdown() {
   }

   static {
      readLock = lock.readLock();
      writeLock = lock.writeLock();
      _active = false;
      _listener = new TopKiller.ListenerImpl();
   }

   private static class ValuesComparator implements Comparator<Entry<Integer, Integer>> {
      private ValuesComparator() {
      }

      public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
         return (Integer)o2.getValue() - (Integer)o1.getValue();
      }

      // $FF: synthetic method
      ValuesComparator(Object x0) {
         this();
      }
   }

   private static final class ListenerImpl implements OnDeathListener, OnPvpPkKillListener {
      private ListenerImpl() {
      }

      public void onDeath(Creature actor, Creature killer) {
         if (TopKiller._active && actor != null && killer != null) {
            if (actor.isMonster() && ArrayUtils.contains(ConfigTopKiller.TOP_KILLER_DINO_MOBS, actor.getNpcId())) {
               Player player = killer.getPlayer();
               if (player != null) {
                  int score = TopKiller.incSaveAndGetDinoPoints(player);
                  if (ConfigTopKiller.TOP_KILLER_DINO_MSG_GET) {
                     player.sendMessage((new CustomMessage("scripts.events.TopKiller.MsgPlayerDino", player, new Object[0])).addNumber((long)score));
                  }
               }
            }

         }
      }

      public void onPvpPkKill(Player killer, Player victim, boolean isPk) {
         if (TopKiller._active && victim != null && killer != null) {
            Iterator var4;
            Zone zone;
            int score;
            if (isPk) {
               if (ConfigTopKiller.TOP_KILLER_PK_REWARD_HWID && victim.getNetConnection() != null && killer.getNetConnection() != null && victim.getNetConnection().getHwid().equals(killer.getNetConnection().getHwid()) || ConfigTopKiller.TOP_KILLER_PK_REWARD_IP && victim.getIP().equals(killer.getIP())) {
                  return;
               }

               if (ConfigTopKiller.TOP_KILLER_PK_DISABLE_IN_EPIC_SIEGE_ZONES) {
                  var4 = killer.getZones().iterator();

                  while(var4.hasNext()) {
                     zone = (Zone)var4.next();
                     if (zone.isAnyType(new ZoneType[]{ZoneType.epic, ZoneType.SIEGE})) {
                        return;
                     }
                  }
               }

               if (ConfigTopKiller.TOP_KILLER_PK_DISABLE_ZONES.length > 0) {
                  var4 = killer.getZones().iterator();

                  while(var4.hasNext()) {
                     zone = (Zone)var4.next();
                     if (ArrayUtils.contains(ConfigTopKiller.TOP_KILLER_PK_DISABLE_ZONES, zone.getName())) {
                        return;
                     }
                  }
               }

               score = TopKiller.incSaveAndGetPkPoints(killer);
               if (ConfigTopKiller.TOP_KILLER_PK_MSG_GET) {
                  killer.sendMessage((new CustomMessage("scripts.events.TopKiller.MsgPlayerPk", killer, new Object[0])).addNumber((long)score));
               }
            } else {
               if (ConfigTopKiller.TOP_KILLER_PVP_REWARD_HWID && victim.getNetConnection() != null && killer.getNetConnection() != null && victim.getNetConnection().getHwid().equals(killer.getNetConnection().getHwid()) || ConfigTopKiller.TOP_KILLER_PVP_REWARD_IP && victim.getIP().equals(killer.getIP())) {
                  return;
               }

               if (ConfigTopKiller.TOP_KILLER_PVP_DISABLE_IN_EPIC_SIEGE_ZONES) {
                  var4 = killer.getZones().iterator();

                  while(var4.hasNext()) {
                     zone = (Zone)var4.next();
                     if (zone.isAnyType(new ZoneType[]{ZoneType.epic, ZoneType.SIEGE})) {
                        return;
                     }
                  }
               }

               if (ConfigTopKiller.TOP_KILLER_PVP_DISABLE_ZONES.length > 0) {
                  var4 = killer.getZones().iterator();

                  while(var4.hasNext()) {
                     zone = (Zone)var4.next();
                     if (ArrayUtils.contains(ConfigTopKiller.TOP_KILLER_PVP_DISABLE_ZONES, zone.getName())) {
                        return;
                     }
                  }
               }

               score = TopKiller.incSaveAndGetPvpPoints(killer);
               if (ConfigTopKiller.TOP_KILLER_PVP_MSG_GET) {
                  killer.sendMessage((new CustomMessage("scripts.events.TopKiller.MsgPlayerPvp", killer, new Object[0])).addNumber((long)score));
               }
            }

         }
      }

      // $FF: synthetic method
      ListenerImpl(Object x0) {
         this();
      }
   }

   private class CollectionEndTask implements Runnable {
      private CollectionEndTask() {
      }

      public void run() {
         TopKiller.this.doEndAction();
      }

      // $FF: synthetic method
      CollectionEndTask(Object x1) {
         this();
      }
   }
}
