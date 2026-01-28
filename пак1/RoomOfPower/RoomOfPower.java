package events.RoomOfPower;

import events.EventUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.database.mysql;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.CharListener;
import l2.gameserver.listener.PlayerListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.MinionList;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.Summon;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NickNameChanged;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class RoomOfPower extends Functions implements ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(RoomOfPower.class);
   private static List<Long> players_list;
   public static List<Long> live_list;
   private static List<String> _restrict = new ArrayList();
   private static final int[] removeEffects = new int[]{442, 443, 1411, 1418, 1427};
   private static boolean _clearing;
   private static Reflection _reflection = null;
   private static boolean _isRegistrationActive = false;
   public static int _status = 0;
   private static int _time_to_start;
   private static ScheduledFuture<?> _startTask;
   private static ScheduledFuture<?> _endTask;
   private static ScheduledFuture<?> _eventTask;
   private static Zone _roomZone = null;
   private static RoomOfPower.ZoneListener _zoneListener = new RoomOfPower.ZoneListener();
   private static NpcInstance _regNpc;
   private static long fightBeginTime;
   private static ScheduledFuture<?> _timerTask;
   private static int MOB_WAVES;
   private static int CURRENT_MOB_STAGE;
   private static Calendar _date;
   private static Map<Integer, List<Integer>> _equip = new ConcurrentHashMap();
   private static Map<Integer, List<Integer>> _destroy = new ConcurrentHashMap();
   private static Map<Integer, List<Effect>> _sbuffs = new ConcurrentHashMap();
   private static Map<Integer, AtomicInteger> _killList = new ConcurrentHashMap();
   private static Map<Integer, AtomicInteger> _deathList = new ConcurrentHashMap();
   private static Map<Integer, String> MOB_STAGE_SPAWN_GROUP = new HashMap();
   public static Map<Integer, Double> PLAYER_DAMAGE_MODIFY = new HashMap();
   public static Map<Integer, Double> NPC_DAMAGE_MODIFY = new HashMap();
   private static boolean _active = false;
   private static final PlayerListener _listeners = new RoomOfPower.PlayerListenerImpl();
   private static final CharListener _npcListeners = new RoomOfPower.NpcListenerImpl();
   private static final String TITLE_VAR = "rop_title";

   public void onLoad() {
      ConfigRoomOfPower.load();
      if (ConfigRoomOfPower.ROP_ENABLE_COMMAND) {
         VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new RoomOfPower.VoicedCommand());
      }

      _active = ServerVariables.getString("RoomOfPower", "off").equalsIgnoreCase("on");
      loadCustomItems();
      if (_active) {
         executeTask("events.RoomOfPower.RoomOfPower", "preLoad", new Object[0], 1000L);
      }

      _log.info("Loaded Event: RoomOfPower");
   }

   public void onReload() {
   }

   public void onShutdown() {
      this.onReload();
   }

   public void activateEvent() {
      Player player = this.getSelf();
      if (player != null && player.getPlayerAccess().IsEventGm) {
         if (!_active) {
            executeTask("events.RoomOfPower.RoomOfPower", "preLoad", new Object[0], 10000L);
            ServerVariables.set("RoomOfPower", "on");
            _log.info("Event 'RoomOfPower' activated.");
            player.sendMessage(new CustomMessage("scripts.events.RoomOfPower.AnnounceEventStarted", player, new Object[0]));
         } else {
            player.sendMessage(player.isLangRus() ? "'RoomOfPower' эвент уже активен." : "Event 'RoomOfPower' already active.");
         }

         _active = true;
         this.show(HtmCache.getInstance().getNotNull("admin/events/events.htm", player), player);
      }
   }

   public void deactivateEvent() {
      Player player = this.getSelf();
      if (player != null && player.getPlayerAccess().IsEventGm) {
         if (_active) {
            ServerVariables.unset("RoomOfPower");
            _log.info("Event 'RoomOfPower' deactivated.");
            player.sendMessage(new CustomMessage("scripts.events.RoomOfPower.AnnounceEventStopped", player, new Object[0]));
         } else {
            player.sendMessage("Event 'RoomOfPower' not active.");
         }

         _active = false;
         this.show(HtmCache.getInstance().getNotNull("admin/events/events.htm", player), player);
      }
   }

   public static boolean isRunned(String name) {
      return _isRegistrationActive || _status > 0;
   }

   public void start() {
      Player player = this.getSelf();
      if (player != null && player.getPlayerAccess().IsEventGm) {
         startOk();
      }
   }

   private static void initReflection() {
      InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(107);
      _reflection = new Reflection();
      _reflection.init(instantZone);
      _roomZone = _reflection.getZone(ConfigRoomOfPower.ROP_ZONE_NAME);
      _roomZone.addListener(_zoneListener);
      CURRENT_MOB_STAGE = 0;
      MOB_STAGE_SPAWN_GROUP.clear();
      StatsSet params = instantZone.getAddParams();
      MOB_WAVES = params.getInteger("mob_waves");

      for(int i = 1; i <= MOB_WAVES; ++i) {
         String mobGroup = params.getString("mobs_stage_group_" + i);
         MOB_STAGE_SPAWN_GROUP.put(i, mobGroup);
      }

      PLAYER_DAMAGE_MODIFY.clear();
      double currentDamageModify = 1.0D;

      int i;
      double damageModify;
      for(i = 1; i <= ConfigRoomOfPower.ROP_MAX_PLAYERS; ++i) {
         damageModify = params.getDouble("player_damage_" + i, currentDamageModify);
         currentDamageModify = damageModify;
         PLAYER_DAMAGE_MODIFY.put(i, damageModify);
      }

      NPC_DAMAGE_MODIFY.clear();
      currentDamageModify = 1.0D;

      for(i = 1; i <= ConfigRoomOfPower.ROP_MAX_PLAYERS; ++i) {
         damageModify = params.getDouble("npc_damage_" + i, currentDamageModify);
         currentDamageModify = damageModify;
         NPC_DAMAGE_MODIFY.put(i, damageModify);
      }

   }

   public static void doneReflection() {
      if (_reflection != null) {
         _reflection.collapse();
         _reflection = null;
      }

      _roomZone.removeListener(_zoneListener);
   }

   private static void startOk() {
      if (!_isRegistrationActive && _status <= 0) {
         if (_endTask != null) {
            _log.info("RoomOfPower not started: end task is active");
         } else {
            if (_startTask != null) {
               _startTask.cancel(false);
               _startTask = null;
            }

            _status = 0;
            _isRegistrationActive = true;
            _clearing = false;
            _time_to_start = ConfigRoomOfPower.ROP_TIME_TO_START;
            players_list = new CopyOnWriteArrayList();
            live_list = new CopyOnWriteArrayList();
            _killList = new ConcurrentHashMap();
            _deathList = new ConcurrentHashMap();
            _restrict = new ArrayList();
            spawnEventManager();
            String[] param = new String[]{String.valueOf(_time_to_start)};
            sayToAll("scripts.events.RoomOfPower.AnnouncePreStart", param);
            sayToAll("scripts.events.RoomOfPower.AnnounceReg", (String[])null);
            executeTask("events.RoomOfPower.RoomOfPower", "question", new Object[0], 5000L);
            executeTask("events.RoomOfPower.RoomOfPower", "announce", new Object[0], 60000L);
         }
      }
   }

   private static void spawnEventManager() {
      if (ConfigRoomOfPower.ROP_SPAWN_REG_MANAGER) {
         try {
            Location npcLoc = Location.parseLoc(ConfigRoomOfPower.ROP_REG_MANAGER_LOC);
            _regNpc = NpcUtils.spawnSingle(ConfigRoomOfPower.ROP_REG_MANAGER_ID, npcLoc);
         } catch (Exception var1) {
            _log.error("RoomOfPower: fail spawn registered manager", var1);
         }
      }

   }

   private static void despawnEventManager() {
      if (ConfigRoomOfPower.ROP_SPAWN_REG_MANAGER && _regNpc != null) {
         try {
            _regNpc.deleteMe();
            _regNpc = null;
         } catch (Exception var1) {
            _log.error("RoomOfPower: fail despawn manager", var1);
         }
      }

   }

   private static void sayToAll(String address, String[] replacements) {
      Iterator var2 = GameObjectsStorage.getAllPlayersForIterate().iterator();

      while(true) {
         Player player;
         do {
            if (!var2.hasNext()) {
               return;
            }

            player = (Player)var2.next();
         } while(player == null);

         CustomMessage cm = new CustomMessage(address, player, new Object[0]);
         if (replacements != null) {
            String[] var5 = replacements;
            int var6 = replacements.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String s = var5[var7];
               cm.addString(s);
            }
         }

         player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "RoomOfPower", "RoomOfPower: " + cm.toString()));
      }
   }

   private static void sayToParticipants(String address, String[] replacements) {
      Iterator var2 = EventUtils.getPlayers(live_list).iterator();

      while(true) {
         Player player;
         do {
            if (!var2.hasNext()) {
               return;
            }

            player = (Player)var2.next();
         } while(player == null);

         CustomMessage cm = new CustomMessage(address, player, new Object[0]);
         if (replacements != null) {
            String[] var5 = replacements;
            int var6 = replacements.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String s = var5[var7];
               cm.addString(s);
            }
         }

         player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "RoomOfPower", "RoomOfPower: " + cm.toString()));
      }
   }

   public static void question() {
      if (ConfigRoomOfPower.ROP_SEND_REG_WINDOW) {
         Iterator var0 = GameObjectsStorage.getAllPlayersForIterate().iterator();

         while(var0.hasNext()) {
            Player player = (Player)var0.next();
            if (isCheckWindow(player)) {
               player.scriptRequest((new CustomMessage("scripts.events.RoomOfPower.AskPlayer", player, new Object[0])).toString(), "events.RoomOfPower.RoomOfPower:addPlayer", new Object[0]);
            }
         }
      }

   }

   private static boolean isCheckWindow(Player player) {
      if (player == null) {
         return false;
      } else if (player.getLevel() >= ConfigRoomOfPower.ROP_MIN_LEVEL && player.getLevel() <= ConfigRoomOfPower.ROP_MAX_LEVEL) {
         if (!player.getReflection().isDefault()) {
            return false;
         } else if (player.isOlyParticipant()) {
            return false;
         } else if (player.isCursedWeaponEquipped()) {
            return false;
         } else if (player.isInObserverMode()) {
            return false;
         } else if (player.isDead()) {
            return false;
         } else if (player.isInZone(ZoneType.epic)) {
            return false;
         } else if (player.isFlying()) {
            return false;
         } else if (player.getTeam() != TeamType.NONE) {
            return false;
         } else if (player.isInGvG()) {
            return false;
         } else {
            return player.getVar("jailed") == null;
         }
      } else {
         return false;
      }
   }

   public static void announce() {
      if (_time_to_start > 1) {
         --_time_to_start;
         String[] param = new String[]{String.valueOf(_time_to_start)};
         sayToAll("scripts.events.RoomOfPower.AnnouncePreStart", param);
         executeTask("events.RoomOfPower.RoomOfPower", "announce", new Object[0], 60000L);
      } else {
         if (players_list.isEmpty() || players_list.size() < ConfigRoomOfPower.ROP_MIN_PLAYERS) {
            sayToAll("scripts.events.RoomOfPower.AnnounceEventCancelled", (String[])null);
            _isRegistrationActive = false;
            _status = 0;
            despawnEventManager();
            executeTask("events.RoomOfPower.RoomOfPower", "preLoad", new Object[0], 10000L);
            return;
         }

         _status = 1;
         _isRegistrationActive = false;
         sayToAll("scripts.events.RoomOfPower.AnnounceEventStarting", (String[])null);
         executeTask("events.RoomOfPower.RoomOfPower", "prepare", new Object[0], 5000L);
      }

   }

   public void unreg() {
      Player player = this.getSelf();
      if (player != null) {
         if (!_isRegistrationActive) {
            player.sendMessage(player.isLangRus() ? "Доступно только в период регистрации." : "Available only during the registration period.");
         } else if (players_list.contains(player.getStoredId())) {
            players_list.remove(player.getStoredId());
            live_list.remove(player.getStoredId());
            if (ConfigRoomOfPower.ROP_IP_RESTRICTION) {
               _restrict.remove(player.getIP());
            }

            if (ConfigRoomOfPower.ROP_HWID_RESTRICTION && player.getNetConnection() != null) {
               _restrict.remove(player.getNetConnection().getHwid());
            }

            player.sendMessage(player.isLangRus() ? "Вы успешно удалены с регистрации на RoomOfPower." : "You have been removed from registration on RoomOfPower.");
         } else {
            player.sendMessage(player.isLangRus() ? "Вы не являетесь участником RoomOfPower." : "You are not a participant of RoomOfPower.");
         }
      }
   }

   public void addPlayer() {
      Player player = this.getSelf();
      if (player != null && checkPlayer(player, true)) {
         if (players_list.size() >= ConfigRoomOfPower.ROP_MAX_PLAYERS) {
            player.sendMessage(player.isLangRus() ? "Достигнут лимит допустимого кол-ва участников." : "The limit of the allowed number of participants has been reached.");
         } else {
            if (ConfigRoomOfPower.ROP_IP_RESTRICTION) {
               if (_restrict.contains(player.getIP())) {
                  player.sendMessage(player.isLangRus() ? "Игрок с данным IP уже зарегистрирован." : "The player with this IP is already registered.");
                  return;
               }

               _restrict.add(player.getIP());
            }

            if (ConfigRoomOfPower.ROP_HWID_RESTRICTION && player.getNetConnection() != null) {
               if (_restrict.contains(player.getNetConnection().getHwid())) {
                  player.sendMessage(player.isLangRus() ? "Игрок с данным железом уже зарегистрирован." : "The player with this hwid is already registered.");
                  return;
               }

               _restrict.add(player.getNetConnection().getHwid());
            }

            players_list.add(player.getStoredId());
            live_list.add(player.getStoredId());
            show(new CustomMessage("scripts.events.RoomOfPower.Registered", player, new Object[0]), player);
         }
      }
   }

   public static boolean checkPlayer(Player player, boolean first) {
      if (first && !_isRegistrationActive) {
         show(new CustomMessage("scripts.events.Late", player, new Object[0]), player);
         return false;
      } else if (first && players_list.contains(player.getStoredId())) {
         show(new CustomMessage("scripts.events.RoomOfPower.Cancelled", player, new Object[0]), player);
         return false;
      } else if (player.getLevel() >= ConfigRoomOfPower.ROP_MIN_LEVEL && player.getLevel() <= ConfigRoomOfPower.ROP_MAX_LEVEL) {
         if (player.isMounted()) {
            player.sendMessage(player.isLangRus() ? "Верхом на эвент нельзя." : "You can't do it while riding.");
            return false;
         } else if (player.isInDuel()) {
            show(new CustomMessage("scripts.events.RoomOfPower.CancelledDuel", player, new Object[0]), player);
            return false;
         } else if (player.getTeam() != TeamType.NONE) {
            show(new CustomMessage("scripts.events.RoomOfPower.CancelledOtherEvent", player, new Object[0]), player);
            return false;
         } else if (!player.isOlyParticipant() && (!first || !ParticipantPool.getInstance().isRegistred(player))) {
            if (player.isInParty() && player.getParty().isInDimensionalRift()) {
               show(new CustomMessage("scripts.events.RoomOfPower.CancelledOtherEvent", player, new Object[0]), player);
               return false;
            } else if (player.isTeleporting()) {
               show(new CustomMessage("scripts.events.RoomOfPower.CancelledTeleport", player, new Object[0]), player);
               return false;
            } else if (player.isCursedWeaponEquipped()) {
               player.sendMessage(player.isLangRus() ? "С проклятым оружием на эвент нельзя." : "You can't do it with cursed weapon.");
               return false;
            } else if (player.isInObserverMode()) {
               player.sendMessage(player.isLangRus() ? "В режиме просмотра на эвент нельзя." : "You can't do it while observing.");
               return false;
            } else if (player.getVar("jailed") != null) {
               player.sendMessage(player.isLangRus() ? "Не получиться сбежать." : "You can't do it in jail.");
               return false;
            } else if (ArrayUtils.contains(ConfigRoomOfPower.ROP_RESTRICTED_CLASS_IDS, player.getActiveClassId())) {
               player.sendMessage(player.isLangRus() ? "Данному классу запрещено участвовать в ивенте." : "This class is not allowed to participate in the event.");
               return false;
            } else {
               return true;
            }
         } else {
            show(new CustomMessage("scripts.events.RoomOfPower.CancelledOlympiad", player, new Object[0]), player);
            return false;
         }
      } else {
         show(new CustomMessage("scripts.events.RoomOfPower.CancelledLevel", player, new Object[0]), player);
         return false;
      }
   }

   public static void prepare() {
      despawnEventManager();
      initReflection();
      cleanPlayers();
      int size = players_list.size();
      if (!players_list.isEmpty() && size >= ConfigRoomOfPower.ROP_MIN_PLAYERS) {
         clearArena();
         _sbuffs = new ConcurrentHashMap(size);
         if (ConfigRoomOfPower.ROP_ENABLE_CUSTOM_ITEMS) {
            _equip = new ConcurrentHashMap(size);
            _destroy = new ConcurrentHashMap(size);
         }

         executeTask("events.RoomOfPower.RoomOfPower", "paralyzePlayers", new Object[0], 100L);
         executeTask("events.RoomOfPower.RoomOfPower", "teleportPlayersToColiseum", new Object[0], 3000L);
         executeTask("events.RoomOfPower.RoomOfPower", "go", new Object[0], (long)ConfigRoomOfPower.ROP_TIME_PARALYZE * 1000L);
         sayToParticipants("scripts.events.RoomOfPower.AnnounceFinalCountdown", new String[]{String.valueOf(ConfigRoomOfPower.ROP_TIME_PARALYZE)});
      } else {
         sayToAll("scripts.events.RoomOfPower.AnnounceEventCancelled", (String[])null);
         _isRegistrationActive = false;
         _status = 0;
         executeTask("events.RoomOfPower.RoomOfPower", "preLoad", new Object[0], 10000L);
         doneReflection();
      }
   }

   public static void go() {
      _status = 2;
      fightBeginTime = System.currentTimeMillis();
      if (ConfigRoomOfPower.ROP_CANCEL_ALL_BUFF) {
         removeBuff();
      } else {
         upParalyzePlayers();
      }

      if (checkInZone()) {
         clearArena();
      } else {
         clearArena();
         buffPlayers();
         giveListeners();
         ++CURRENT_MOB_STAGE;
         appointNextState(CURRENT_MOB_STAGE);
         Object[] args = new Object[]{false};
         _endTask = executeTask("events.RoomOfPower.RoomOfPower", "endBattle", args, (long)ConfigRoomOfPower.ROP_BATTLE_DURATION * 60000L);
         if (ConfigRoomOfPower.ROP_BROADCAST_TIMER) {
            _timerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RoomOfPower.BattleTimer(), 0L, 1010L);
         }

      }
   }

   private static void giveListeners() {
      Iterator var0 = EventUtils.getPlayers(players_list).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         if (player != null) {
            player.getListeners().onRoomOfPowerEvent(false);
         }
      }

   }

   public static void broadCastTimer() {
      int secondsLeft = (int)((fightBeginTime + TimeUnit.MINUTES.toMillis((long)ConfigRoomOfPower.ROP_BATTLE_DURATION) - System.currentTimeMillis()) / 1000L);
      int minutes = secondsLeft / 60;
      int seconds = secondsLeft % 60;
      ExShowScreenMessage packet = new ExShowScreenMessage(String.format("%02d:%02d", minutes, seconds), 1010, -1, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, false);
      Iterator var4 = EventUtils.getPlayers(players_list).iterator();

      while(var4.hasNext()) {
         Player player = (Player)var4.next();
         player.sendPacket(packet);
      }

   }

   public static void stopTimerTask() {
      if (_timerTask != null) {
         _timerTask.cancel(true);
         _timerTask = null;
      }

   }

   private static void saveBuffs(Player player) {
      List<Effect> effectList = player.getEffectList().getAllEffects();
      List<Effect> effects = new ArrayList(effectList.size());
      Iterator var3 = effectList.iterator();

      while(var3.hasNext()) {
         Effect e = (Effect)var3.next();
         if (!e.getSkill().isToggle() && e.getSkill().isSaveable() && !e.getSkill().isItemSkill()) {
            Effect effect = e.getTemplate().getEffect(new Env(e.getEffector(), e.getEffected(), e.getSkill()));
            effect.setCount(e.getCount());
            effect.setPeriod(e.getCount() == 1 ? e.getPeriod() - e.getTime() : e.getPeriod());
            effects.add(effect);
         }
      }

      if (!effects.isEmpty()) {
         _sbuffs.put(player.getObjectId(), effects);
      }

   }

   private static void buffPlayers() {
      if (ConfigRoomOfPower.ROP_BUFFS_FIGHTER.length >= 2 || ConfigRoomOfPower.ROP_BUFFS_MAGE.length >= 2) {
         Iterator var0 = EventUtils.getPlayers(players_list).iterator();

         while(true) {
            Player player;
            int n;
            int i;
            do {
               while(true) {
                  do {
                     if (!var0.hasNext()) {
                        return;
                     }

                     player = (Player)var0.next();
                  } while(player == null);

                  if (player.isMageClass()) {
                     break;
                  }

                  if (ConfigRoomOfPower.ROP_BUFFS_FIGHTER.length > 1) {
                     n = 0;

                     for(i = 0; i < ConfigRoomOfPower.ROP_BUFFS_FIGHTER.length; i += 2) {
                        EventUtils.giveBuff(player, SkillTable.getInstance().getInfo(ConfigRoomOfPower.ROP_BUFFS_FIGHTER[i], ConfigRoomOfPower.ROP_BUFFS_FIGHTER[i + 1]), ConfigRoomOfPower.ROP_ALT_BUFFS_DURATION, n++);
                     }
                  }
               }
            } while(ConfigRoomOfPower.ROP_BUFFS_MAGE.length <= 1);

            n = 0;

            for(i = 0; i < ConfigRoomOfPower.ROP_BUFFS_MAGE.length; i += 2) {
               EventUtils.giveBuff(player, SkillTable.getInstance().getInfo(ConfigRoomOfPower.ROP_BUFFS_MAGE[i], ConfigRoomOfPower.ROP_BUFFS_MAGE[i + 1]), ConfigRoomOfPower.ROP_ALT_BUFFS_DURATION, n++);
            }
         }
      }
   }

   private static boolean checkInZone() {
      boolean useBC = ConfigRoomOfPower.ROP_RETURN_POINT.length < 3;
      Location ClearLoc = Location.parseLoc(ConfigRoomOfPower.ROP_CLEAR_LOC);
      Iterator var2 = EventUtils.getPlayers(players_list).iterator();

      while(true) {
         while(true) {
            Player player;
            do {
               do {
                  if (!var2.hasNext()) {
                     if (players_list.size() < 1) {
                        endBattle(false);
                        return true;
                     }

                     return false;
                  }

                  player = (Player)var2.next();
               } while(player == null);
            } while(_roomZone.checkIfInZone(player.getX(), player.getY(), player.getZ(), getEventReflection()));

            removePlayer(player);
            if (useBC && player.getVar("RoP_backCoords") == null) {
               player.teleToLocation(ClearLoc, ReflectionManager.DEFAULT);
            } else {
               backPlayer(player);
            }
         }
      }
   }

   private static void removeBuff() {
      Iterator var0 = EventUtils.getPlayers(players_list).iterator();

      while(true) {
         Player player;
         do {
            if (!var0.hasNext()) {
               return;
            }

            player = (Player)var0.next();
         } while(player == null);

         try {
            player.getEffectList().stopAllEffects();
            Summon summon = player.getPet();
            if (summon != null) {
               summon.getEffectList().stopAllEffects();
               if (summon.isPet() || ConfigRoomOfPower.ROP_RESTRICTED_SUMMONS.length > 0 && ArrayUtils.contains(ConfigRoomOfPower.ROP_RESTRICTED_SUMMONS, summon.getNpcId())) {
                  summon.unSummon();
               }
            }
         } catch (Exception var3) {
            _log.error("on removeBuff", var3);
         }
      }
   }

   public static void endBattle(boolean allKill) {
      if (!_clearing) {
         _clearing = true;

         try {
            if (_endTask != null) {
               _endTask.cancel(false);
               _endTask = null;
            }
         } catch (Exception var2) {
         }

         cancelEventTask();
         stopTimerTask();
         if (_status != 0) {
            _status = 0;
            if (allKill) {
               sayToAll("scripts.events.RoomOfPower.AnnounceFinishedWins", (String[])null);
               giveItemsToWinner();
            } else {
               sayToAll("scripts.events.RoomOfPower.AnnounceFinishedLoose", (String[])null);
            }

            removeAura();
            sayToParticipants("scripts.events.RoomOfPower.AnnounceEnd", new String[]{String.valueOf(ConfigRoomOfPower.ROP_TIME_BACK)});
            executeTask("events.RoomOfPower.RoomOfPower", "end", new Object[0], (long)ConfigRoomOfPower.ROP_TIME_BACK * 1000L);
            _isRegistrationActive = false;
            _killList.clear();
            _deathList.clear();
            _sbuffs.clear();
            _clearing = false;
         }
      }
   }

   public static void end() {
      ressurectPlayers();
      executeTask("events.RoomOfPower.RoomOfPower", "teleportPlayersToSavedCoords", new Object[0], 100L);
      executeTask("events.RoomOfPower.RoomOfPower", "doneReflection", new Object[0], 1000L);
      executeTask("events.RoomOfPower.RoomOfPower", "preLoad", new Object[0], 10000L);
   }

   private static void giveItemsToWinner() {
      List<Player> winningPlayers = getWinningTeamPlayers();
      List<Integer> topKillers = giveTopKillerReward(winningPlayers);
      Iterator var2 = winningPlayers.iterator();

      while(true) {
         Player player;
         int[] reward;
         int currentKills;
         do {
            do {
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  player = (Player)var2.next();
               } while(player == null);
            } while(topKillers.contains(player.getObjectId()));

            reward = getWinnerReward(player);
            currentKills = 0;
            if (players_list.contains(player.getStoredId())) {
               currentKills = EventUtils.getEventKills(player, _killList);
            }
         } while(currentKills < getMinWinnerKill(player) && !ArrayUtils.contains(ConfigRoomOfPower.ROP_CLASS_REWARD_NO_KILLS, player.getActiveClassId()));

         for(int i = 0; i < reward.length; i += 2) {
            int itemId = reward[i];
            long itemCount = (long)reward[i + 1];
            addItem(player, itemId, itemCount);
         }

         player.getListeners().onRoomOfPowerEvent(true);
      }
   }

   private static List<Integer> giveTopKillerReward(List<Player> players) {
      if (!ConfigRoomOfPower.ROP_TOP_KILLER_ENABLE) {
         return Collections.emptyList();
      } else {
         Map<Integer, Integer> allPlayerScores = new HashMap();
         Iterator var2 = players.iterator();

         while(var2.hasNext()) {
            Player player = (Player)var2.next();
            if (player != null) {
               int currentKills = 0;
               if (players_list.contains(player.getStoredId())) {
                  currentKills = EventUtils.getEventKills(player, _killList);
               }

               allPlayerScores.put(player.getObjectId(), currentKills);
            }
         }

         List<Entry<Integer, Integer>> entryList = new ArrayList(allPlayerScores.entrySet());
         entryList.sort(Entry.comparingByValue(Comparator.reverseOrder()));
         Map<Integer, Integer> sortedMap = new LinkedHashMap();
         Iterator var18 = entryList.iterator();

         while(var18.hasNext()) {
            Entry<Integer, Integer> entry = (Entry)var18.next();
            sortedMap.put(entry.getKey(), entry.getValue());
         }

         if (ConfigRoomOfPower.ROP_TOP_KILLER_ANNOUNCE && sortedMap.size() > 0 && (ConfigRoomOfPower.ROP_TOP_KILLER_REWARD.size() > 0 || ConfigRoomOfPower.ROP_TOP_KILLER_REWARD_PREMIUM.size() > 0)) {
            sayToAll("scripts.events.RoomOfPower.AnnounceMsgTopKiller", (String[])null);
         }

         List<Integer> topKillers = new ArrayList();
         int place = 1;
         Iterator var6 = sortedMap.entrySet().iterator();

         while(var6.hasNext()) {
            Entry<Integer, Integer> entry = (Entry)var6.next();
            int objectId = (Integer)entry.getKey();
            Player player = GameObjectsStorage.getPlayer(objectId);
            if (player != null) {
               int currentKills = 0;
               if (players_list.contains(player.getStoredId())) {
                  currentKills = EventUtils.getEventKills(player, _killList);
               }

               if (currentKills >= getMinWinnerKill(player)) {
                  int[] reward = (int[])getTopKillerReward(player).get(place);
                  if (reward == null || reward.length <= 0) {
                     break;
                  }

                  for(int i = 0; i < reward.length; i += 2) {
                     int itemId = reward[i];
                     long itemCount = (long)reward[i + 1];
                     addItem(player, itemId, itemCount);
                  }

                  player.getListeners().onRoomOfPowerEvent(true);
                  topKillers.add(objectId);
                  if (ConfigRoomOfPower.ROP_TOP_KILLER_ANNOUNCE) {
                     sayToAll("scripts.events.RoomOfPower.AnnouncePlaceTopKiller", new String[]{String.valueOf(place), player.getName(), String.valueOf(currentKills)});
                  }

                  ++place;
               }
            }
         }

         return topKillers;
      }
   }

   private static Map<Integer, int[]> getTopKillerReward(Player player) {
      return player.hasBonus() ? ConfigRoomOfPower.ROP_TOP_KILLER_REWARD_PREMIUM : ConfigRoomOfPower.ROP_TOP_KILLER_REWARD;
   }

   private static int[] getWinnerReward(Player player) {
      return player.hasBonus() ? ConfigRoomOfPower.ROP_REWARD_FINAL_PREMIUM : ConfigRoomOfPower.ROP_REWARD_FINAL;
   }

   private static List<Player> getWinningTeamPlayers() {
      List<Player> players = new ArrayList();
      Iterator var1 = EventUtils.getPlayers(live_list).iterator();

      while(var1.hasNext()) {
         Player player = (Player)var1.next();
         if (!player.isDead()) {
            players.add(player);
         }
      }

      return players;
   }

   private static int getMinWinnerKill(Player player) {
      return player.hasBonus() ? ConfigRoomOfPower.ROP_WINNER_MIN_KILLS_PREMIUM : ConfigRoomOfPower.ROP_WINNER_MIN_KILLS;
   }

   public static void teleportPlayersToColiseum() {
      boolean useBC = ConfigRoomOfPower.ROP_RETURN_POINT.length < 3;
      Location ClearLoc = Location.parseLoc(ConfigRoomOfPower.ROP_CLEAR_LOC);
      Iterator var2 = EventUtils.getPlayers(players_list).iterator();

      while(true) {
         Player player;
         do {
            if (!var2.hasNext()) {
               return;
            }

            player = (Player)var2.next();
         } while(player == null);

         saveTitle(player);
         if (players_list.contains(player.getStoredId())) {
            _killList.put(player.getObjectId(), new AtomicInteger(0));
            if (ConfigRoomOfPower.ROP_DEATH_COUNT > 0) {
               _deathList.put(player.getObjectId(), new AtomicInteger(ConfigRoomOfPower.ROP_DEATH_COUNT));
            }
         }

         unRide(player);
         unSummonPet(player, true);
         if (useBC) {
            String backCoords;
            if (!player.isInZone(ZoneType.no_restart) && !player.isInZone(ZoneType.epic)) {
               backCoords = player.getX() + " " + player.getY() + " " + player.getZ();
            } else {
               backCoords = ClearLoc.getX() + " " + ClearLoc.getY() + " " + ClearLoc.getZ();
            }

            player.setVar("RoP_backCoords", backCoords, -1L);
         }

         player.leaveParty();
         List<Location> teamLoc = ConfigRoomOfPower.ROP_TELE_ZONE_LOCS;
         player.teleToLocation(Location.findAroundPosition((Location)Rnd.get(teamLoc), 100, getEventReflection().getGeoIndex()), getEventReflection());
      }
   }

   public static void teleportPlayersToSavedCoords() {
      Iterator var0 = EventUtils.getPlayers(players_list).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         if (player != null) {
            backPlayer(player);
         }
      }

      unsetLastCoords();
   }

   public static void paralyzePlayers() {
      Skill sk = SkillTable.getInstance().getInfo(4515, 1);
      Iterator var1 = EventUtils.getPlayers(players_list).iterator();

      while(true) {
         Player player;
         do {
            if (!var1.hasNext()) {
               return;
            }

            player = (Player)var1.next();
         } while(player == null);

         EventUtils.healPlayer(player);
         player.setIsInRop(true);
         player.addListener(_listeners);
         saveBuffs(player);
         int[] equipableIds = ArrayUtils.EMPTY_INT_ARRAY;
         int var6;
         if (ConfigRoomOfPower.ROP_ENABLE_CUSTOM_ITEMS) {
            List<Integer> items = new ArrayList();
            ItemInstance[] var5 = player.getInventory().getPaperdollItems();
            var6 = var5.length;

            int id;
            ItemInstance item;
            for(id = 0; id < var6; ++id) {
               item = var5[id];
               if (item != null) {
                  player.getInventory().unEquipItem(item);
                  items.add(item.getObjectId());
               }
            }

            if (!items.isEmpty()) {
               _equip.put(player.getObjectId(), items);
            }

            List<Integer> cm = new ArrayList();

            for(Iterator var16 = ((List)ConfigRoomOfPower.ROP_CUSTOM_ITEMS.get(player.getActiveClassId())).iterator(); var16.hasNext(); cm.add(item.getObjectId())) {
               id = (Integer)var16.next();
               item = ItemFunctions.createItem(id);
               if (item.canBeEnchanted(true)) {
                  item.setEnchantLevel(ConfigRoomOfPower.ROP_CUSTOM_ITEMS_ENCHANT);
               }

               player.getInventory().addItem(item);
               if (item.isEquipable() && !item.getTemplate().isArrow()) {
                  player.getInventory().equipItem(item);
               }
            }

            if (!cm.isEmpty()) {
               List<Integer> equipableItems = new ArrayList();
               ItemInstance[] var18 = player.getInventory().getItems();
               int var20 = var18.length;

               for(int var9 = 0; var9 < var20; ++var9) {
                  ItemInstance item = var18[var9];
                  if (item.isEquipable()) {
                     equipableItems.add(item.getItemId());
                  }
               }

               equipableIds = ArrayUtils.toPrimitive((Integer[])equipableItems.toArray(new Integer[equipableItems.size()]));
               _destroy.put(player.getObjectId(), cm);
            }
         }

         if (ConfigRoomOfPower.ROP_RESTRICTED_ITEMS.length > 0 || equipableIds.length > 0) {
            ItemInstance[] var11 = player.getInventory().getItems();
            int var14 = var11.length;

            for(var6 = 0; var6 < var14; ++var6) {
               ItemInstance item = var11[var6];
               if (item != null && item.isEquipped() && ArrayUtils.contains(ConfigRoomOfPower.ROP_RESTRICTED_ITEMS, item.getItemId())) {
                  player.getInventory().unEquipItem(item);
               }
            }

            player.getInventory().lockItems(LockType.INCLUDE, ArrayUtils.addAll(ConfigRoomOfPower.ROP_RESTRICTED_ITEMS, equipableIds));
         }

         if (ConfigRoomOfPower.ROP_RESTRICTED_SKILLS.length > 0) {
            Iterator var12 = player.getAllSkills().iterator();

            while(var12.hasNext()) {
               Skill skill = (Skill)var12.next();
               if (ArrayUtils.contains(ConfigRoomOfPower.ROP_RESTRICTED_SKILLS, skill.getId())) {
                  player.addUnActiveSkill(skill);
               }
            }
         }

         if (ConfigRoomOfPower.ROP_DISABLE_HERO_SKILLS) {
            EventUtils.unActivateHeroSkills(player);
         }

         EventUtils.stopEffects(player, removeEffects);
         player.getEffectList().stopEffects(EffectType.Paralyze);
         player.getEffectList().stopEffects(EffectType.Petrification);
         if (ConfigRoomOfPower.ROP_PARALYZE_PLAYERS) {
            sk.getEffects(player, player, false, false);
            if (player.getPet() != null) {
               sk.getEffects(player, player.getPet(), false, false);
            }
         }
      }
   }

   public static void upParalyzePlayers() {
      Iterator var0 = EventUtils.getPlayers(players_list).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         if (player != null && ConfigRoomOfPower.ROP_PARALYZE_PLAYERS) {
            player.getEffectList().stopEffect(4515);
            if (player.getPet() != null) {
               player.getPet().getEffectList().stopEffect(4515);
            }
         }
      }

   }

   private static void ressurectPlayers() {
      Iterator var0 = EventUtils.getPlayers(players_list).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         EventUtils.healPlayer(player);
         skillsOn(player);
         itemsOn(player);
      }

   }

   private static void skillsOn(Player player) {
      if (player != null) {
         if (ConfigRoomOfPower.ROP_RESTRICTED_SKILLS.length > 0) {
            Iterator var1 = player.getAllSkills().iterator();

            while(var1.hasNext()) {
               Skill skill = (Skill)var1.next();
               if (ArrayUtils.contains(ConfigRoomOfPower.ROP_RESTRICTED_SKILLS, skill.getId())) {
                  player.removeUnActiveSkill(skill);
               }
            }
         }

         if (ConfigRoomOfPower.ROP_DISABLE_HERO_SKILLS) {
            EventUtils.activateHeroSkills(player);
         }

      }
   }

   private static void itemsOn(Player player) {
      if (player != null) {
         if (ConfigRoomOfPower.ROP_RESTRICTED_ITEMS.length > 0 || ConfigRoomOfPower.ROP_ENABLE_CUSTOM_ITEMS) {
            player.getInventory().unlock();
         }

      }
   }

   private static void cleanPlayers() {
      Iterator var0 = EventUtils.getPlayers(players_list).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         if (player != null) {
            if (!checkPlayer(player, false)) {
               removePlayer(player);
            } else {
               player.setTeam(TeamType.BLUE);
            }
         }
      }

   }

   private static void checkLive() {
      List<Long> new_live_list1 = new CopyOnWriteArrayList();
      Iterator var1 = live_list.iterator();

      while(var1.hasNext()) {
         Long stId = (Long)var1.next();
         Player player = GameObjectsStorage.getAsPlayer(stId);
         if (player != null) {
            new_live_list1.add(player.getStoredId());
         }
      }

      live_list = new_live_list1;
      if (live_list.size() < 1) {
         endBattle(false);
      }

   }

   private static void removeAura() {
      Iterator var0 = EventUtils.getPlayers(live_list).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         if (player != null) {
            resetEventTitle(player);
            player.removeListener(_listeners);
            buffsItems(player);
            player.setTeam(TeamType.NONE);
            player.setIsInRop(false);
         }
      }

   }

   private static void clearArena() {
      Location ClearLoc = Location.parseLoc(ConfigRoomOfPower.ROP_CLEAR_LOC);
      Creature[] var1 = _roomZone.getObjects();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GameObject obj = var1[var3];
         if (obj != null) {
            Player player = obj.getPlayer();
            if (player != null && !live_list.contains(player.getStoredId()) && !player.isGM() && player.getReflection() == getEventReflection()) {
               player.teleToLocation(ClearLoc, ReflectionManager.DEFAULT);
            }
         }
      }

   }

   private static Reflection getEventReflection() {
      return _reflection != null ? _reflection : ReflectionManager.DEFAULT;
   }

   public void resurrect() {
      Player player = this.getSelf();
      if (player != null) {
         if (player.isDead()) {
            AtomicInteger count = (AtomicInteger)_deathList.get(player.getObjectId());
            if (count == null) {
               _deathList.put(player.getObjectId(), new AtomicInteger(ConfigRoomOfPower.ROP_DEATH_COUNT));
               count = (AtomicInteger)_deathList.get(player.getObjectId());
            }

            int currentCount = count.get();
            if (currentCount <= 0) {
               if (player.isLangRus()) {
                  player.sendMessage("У Вас закончились воскрешения.");
               } else {
                  player.sendMessage("You have run out of resurrections.");
               }

            } else {
               currentCount = count.decrementAndGet();
               ThreadPoolManager.getInstance().schedule(new RoomOfPower.TeleportRes(player.getStoredId()), (long)ConfigRoomOfPower.ROP_RESURRECT_DELAY * 1000L);
               player.sendMessage((new CustomMessage("scripts.events.RoomOfPower.Ressurection", player, new Object[0])).addNumber((long)ConfigRoomOfPower.ROP_RESURRECT_DELAY).addNumber((long)currentCount));
            }
         }
      }
   }

   public void endEvent() {
      Player player = this.getSelf();
      if (player != null) {
         if (_status == 2 && live_list.contains(player.getStoredId())) {
            removePlayer(player);
            backPlayer(player);
         }
      }
   }

   private static void appointNextState(int currentMobStage) {
      cancelEventTask();
      _eventTask = ThreadPoolManager.getInstance().schedule(new RoomOfPower.EventTask(currentMobStage), 3000L);
   }

   public static void cancelEventTask() {
      if (_eventTask != null) {
         _eventTask.cancel(false);
         _eventTask = null;
      }

   }

   private static boolean checkAlive() {
      Iterator var0 = _reflection.getNpcs().iterator();

      NpcInstance npc;
      do {
         if (!var0.hasNext()) {
            return false;
         }

         npc = (NpcInstance)var0.next();
      } while(npc.isDead());

      return true;
   }

   private static void backPlayer(Player player) {
      boolean useBC = ConfigRoomOfPower.ROP_RETURN_POINT.length < 3;
      if (useBC) {
         try {
            String var = player.getVar("RoP_backCoords");
            if (var == null) {
               return;
            }

            if (var.equals("")) {
               player.unsetVar("RoP_backCoords");
               return;
            }

            String[] coords = var.split(" ");
            if (coords.length < 3) {
               return;
            }

            player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), ReflectionManager.DEFAULT);
            player.unsetVar("RoP_backCoords");
         } catch (Exception var4) {
            _log.error("on backPlayer", var4);
         }
      } else {
         player.teleToLocation(Location.findAroundPosition(ConfigRoomOfPower.ROP_RETURN_POINT[0], ConfigRoomOfPower.ROP_RETURN_POINT[1], ConfigRoomOfPower.ROP_RETURN_POINT[2], 0, 150, 0), ReflectionManager.DEFAULT);
      }

   }

   private static void spawnAndAddListeners(String spawnGroup) {
      _reflection.spawnByGroup(spawnGroup);
      Map<String, List<Spawner>> spawners = getSpawners();
      if (spawners != null) {
         Iterator var2 = ((List)spawners.get(spawnGroup)).iterator();

         while(var2.hasNext()) {
            Spawner spawner = (Spawner)var2.next();
            List<NpcInstance> npcs = spawner.getAllSpawned();

            NpcInstance npc;
            for(Iterator var5 = npcs.iterator(); var5.hasNext(); npc.addListener(_npcListeners)) {
               npc = (NpcInstance)var5.next();
               MinionList minionList = npc.getMinionList();
               if (minionList != null && minionList.hasMinions()) {
                  ThreadPoolManager.getInstance().schedule(() -> {
                     Iterator var1 = minionList.getAliveMinions().iterator();

                     while(var1.hasNext()) {
                        MinionInstance minion = (MinionInstance)var1.next();
                        minion.addListener(_npcListeners);
                     }

                  }, 1500L);
               }

               Iterator var8 = _reflection.getPlayers().iterator();

               while(var8.hasNext()) {
                  Player player = (Player)var8.next();
                  npc.getAggroList().addDamageHate(player, 0, Rnd.get(1, 100));
               }

               List<Player> players = _reflection.getPlayers();
               if (players != null && !players.isEmpty()) {
                  npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, Rnd.get(players), 100);
               }
            }
         }
      }

   }

   private static Map<String, List<Spawner>> getSpawners() {
      Field spawnersField = null;

      try {
         spawnersField = Reflection.class.getDeclaredField("_spawners");
         spawnersField.setAccessible(true);
         Map<String, List<Spawner>> spawners = (Map)spawnersField.get(_reflection);
         return spawners;
      } catch (IllegalAccessException | NoSuchFieldException var3) {
         throw new RuntimeException(var3);
      }
   }

   private static void despawnAndAddListeners(String despawnGroup) {
      Iterator var1 = _reflection.getSpawns().iterator();

      while(var1.hasNext()) {
         Spawner spawner = (Spawner)var1.next();
         List<NpcInstance> npcs = spawner.getAllSpawned();
         Iterator var4 = npcs.iterator();

         while(var4.hasNext()) {
            NpcInstance npc = (NpcInstance)var4.next();
            npc.removeListener(_npcListeners);
         }
      }

      _reflection.despawnByGroup(despawnGroup);
   }

   private static void unsetLastCoords() {
      boolean useBC = ConfigRoomOfPower.ROP_RETURN_POINT.length < 3;
      if (useBC) {
         mysql.set("DELETE FROM `character_variables` WHERE `name`='RoP_backCoords'");
      }

   }

   private static void removePlayer(Player player) {
      if (player != null) {
         player.removeListener(_listeners);
         live_list.remove(player.getStoredId());
         players_list.remove(player.getStoredId());
         resetEventTitle(player);
         buffsItems(player);
         player.setTeam(TeamType.NONE);
         player.setIsInRop(false);
         skillsOn(player);
         itemsOn(player);
      }

   }

   private static void buffsItems(Player player) {
      try {
         List items;
         Iterator var2;
         if (ConfigRoomOfPower.ROP_ENABLE_CUSTOM_ITEMS) {
            items = (List)_destroy.remove(player.getObjectId());
            int id;
            ItemInstance item;
            if (items != null) {
               var2 = items.iterator();

               while(var2.hasNext()) {
                  id = (Integer)var2.next();
                  item = player.getInventory().getItemByObjectId(id);
                  if (item != null && !item.getTemplate().isArrow()) {
                     player.getInventory().destroyItem(item);
                  }
               }
            }

            items = (List)_equip.remove(player.getObjectId());
            if (items != null) {
               var2 = items.iterator();

               while(var2.hasNext()) {
                  id = (Integer)var2.next();
                  item = player.getInventory().getItemByObjectId(id);
                  if (item != null && item.isEquipable() && !item.getTemplate().isArrow()) {
                     player.getInventory().equipItem(item);
                  }
               }
            }
         }

         player.getEffectList().stopAllEffects();
         items = (List)_sbuffs.remove(player.getObjectId());
         if (items != null) {
            var2 = items.iterator();

            while(var2.hasNext()) {
               Effect e = (Effect)var2.next();
               player.getEffectList().addEffect(e);
            }
         }
      } catch (Exception var5) {
         _log.error("RoomOfPower: failed restore buffs and items", var5);
      }

   }

   private static void loadCustomItems() {
      if (ConfigRoomOfPower.ROP_ENABLE_CUSTOM_ITEMS) {
         try {
            File file = new File(Config.DATAPACK_ROOT, "config/events/room_of_power_items.xml");
            if (!file.exists()) {
               _log.error("not found config/events/room_of_power_items.xml !!!");
               return;
            }

            ConfigRoomOfPower.ROP_CUSTOM_ITEMS = new ConcurrentHashMap(ClassId.values().length);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            Document doc = factory.newDocumentBuilder().parse(file);

            for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
               if ("list".equalsIgnoreCase(n.getNodeName())) {
                  for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                     if ("class".equalsIgnoreCase(d.getNodeName())) {
                        NamedNodeMap attrs = d.getAttributes();
                        int classId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                        String items = attrs.getNamedItem("items").getNodeValue().trim();
                        List<Integer> is = null;
                        if (items != null) {
                           String[] itemsSplit = items.split(",");
                           is = new ArrayList(itemsSplit.length);
                           String[] var10 = itemsSplit;
                           int var11 = itemsSplit.length;

                           for(int var12 = 0; var12 < var11; ++var12) {
                              String element = var10[var12];
                              is.add(Integer.parseInt(element));
                           }
                        }

                        if (is != null) {
                           ConfigRoomOfPower.ROP_CUSTOM_ITEMS.put(classId, is);
                        }
                     }
                  }
               }
            }
         } catch (Exception var14) {
            _log.error("RoomOfPower: error load room_of_power_items.xml", var14);
         }
      }

   }

   public static void preLoad() {
      if (_active) {
         if (noStart(false)) {
            _date.add(ConfigRoomOfPower.ROP_ALLOW_CALENDAR_DAY ? 2 : 5, 1);
            noStart(true);
         }

      }
   }

   private static boolean noStart(boolean msg) {
      _date = Calendar.getInstance();
      _date.set(13, 5);
      int day = ConfigRoomOfPower.ROP_ALLOW_CALENDAR_DAY ? 3 : 2;

      for(int i = 0; i < ConfigRoomOfPower.ROP_START_TIME.length; i += day) {
         if (ConfigRoomOfPower.ROP_ALLOW_CALENDAR_DAY) {
            _date.set(5, ConfigRoomOfPower.ROP_START_TIME[i]);
            _date.set(11, ConfigRoomOfPower.ROP_START_TIME[i + 1]);
            _date.set(12, ConfigRoomOfPower.ROP_START_TIME[i + 2]);
         } else {
            _date.set(11, ConfigRoomOfPower.ROP_START_TIME[i]);
            _date.set(12, ConfigRoomOfPower.ROP_START_TIME[i + 1]);
         }

         if (_date.getTimeInMillis() > System.currentTimeMillis() + 2000L) {
            try {
               if (_startTask != null) {
                  _startTask.cancel(false);
               }
            } catch (Exception var4) {
            }

            _startTask = executeTask("events.RoomOfPower.RoomOfPower", "preStartTask", new Object[0], (long)((int)(_date.getTimeInMillis() - System.currentTimeMillis())));
            return false;
         }
      }

      if (msg) {
         _log.warn("RoomOfPower not loaded! Check RoP_Time_Start in events.properties");
      }

      return true;
   }

   public static void preStartTask() {
      if (_active) {
         startOk();
      }

   }

   public void openPage() {
      Player player = this.getSelf();
      if (player != null) {
         openPage(player);
      }
   }

   public static void openPage(Player activeChar) {
      String htm;
      if (ConfigRoomOfPower.ROP_REG_MANAGER_ID > 0) {
         htm = HtmCache.getInstance().getNotNull("scripts/events/roomofpower/" + ConfigRoomOfPower.ROP_REG_MANAGER_ID + ".htm", activeChar);
         htm = statusPageReplace(activeChar, htm);
         show(htm, activeChar, (NpcInstance)null, new Object[0]);
      } else {
         htm = HtmCache.getInstance().getNotNull("scripts/events/roomofpower/50026.htm", activeChar);
         htm = statusPageReplace(activeChar, htm);
         show(htm, activeChar, (NpcInstance)null, new Object[0]);
      }

   }

   private static String statusPageReplace(Player activeChar, String htm) {
      boolean activeEvent = _active;
      boolean regActive = _isRegistrationActive;
      htm = htm.replace("%active%", getActiveStatus(activeChar, activeEvent, regActive));
      if (_isRegistrationActive) {
         htm = htm.replace("%reg_count%", (new CustomMessage("scripts.events.RoomOfPower.registeredPlayers", activeChar, new Object[0])).addNumber((long)players_list.size()).toString());
         htm = htm.replace("%reg_time%", (new CustomMessage("scripts.events.RoomOfPower.registerTime", activeChar, new Object[0])).addNumber((long)players_list.size()).toString());
      } else {
         htm = htm.replace("%reg_count%", "");
         htm = htm.replace("%reg_time%", "");
      }

      return htm;
   }

   private static String getActiveStatus(Player player, boolean activeEvent, boolean regActive) {
      if (regActive) {
         return StringHolder.getInstance().getNotNull(player, "scripts.events.RoomOfPower.regActive");
      } else {
         return activeEvent ? StringHolder.getInstance().getNotNull(player, "scripts.events.RoomOfPower.activeEvent") : StringHolder.getInstance().getNotNull(player, "scripts.events.RoomOfPower.notActiveEvent");
      }
   }

   public static void saveTitle(Player player) {
      if (ConfigRoomOfPower.ROP_SHOW_KILLS) {
         player.setVar("rop_title", player.getTitle() != null ? player.getTitle() : "", -1L);
         updateTitle(player, 0);
      }

   }

   public static void updateTitle(Player player, int kills) {
      if (ConfigRoomOfPower.ROP_SHOW_KILLS) {
         player.setTransformationTitle(String.format("Kills: %d", kills));
         player.setTitle(player.getTransformationTitle());
         player.broadcastPacket(new L2GameServerPacket[]{new NickNameChanged(player)});
      }

   }

   public static void resetEventTitle(Player player) {
      if (ConfigRoomOfPower.ROP_SHOW_KILLS) {
         String title = player.getVar("rop_title");
         if (title != null) {
            player.setTitle(title);
            player.unsetVar("rop_title");
         }

         player.sendUserInfo(true);
      }

   }

   public class VoicedCommand implements IVoicedCommandHandler {
      private String[] _commandList;

      public VoicedCommand() {
         this._commandList = new String[]{ConfigRoomOfPower.ROP_VOICE_COMMAND};
      }

      public boolean useVoicedCommand(String command, Player activeChar, String target) {
         if (!ConfigRoomOfPower.ROP_ENABLE_COMMAND) {
            return false;
         } else if (command.equalsIgnoreCase(this._commandList[0])) {
            RoomOfPower.openPage(activeChar);
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return this._commandList;
      }
   }

   public static class TeleportTask extends RunnableImpl {
      Location loc;
      Creature target;

      public TeleportTask(Creature target, Location loc) {
         this.target = target;
         this.loc = loc;
         target.startStunning();
      }

      public void runImpl() {
         this.target.stopStunning();
         this.target.teleToLocation(this.loc, RoomOfPower.getEventReflection());
      }
   }

   private static class ZoneListener implements OnZoneEnterLeaveListener {
      private ZoneListener() {
      }

      public void onZoneEnter(Zone zone, Creature object) {
         if (object != null) {
            Player player = object.getPlayer();
            if (RoomOfPower._status > 0 && player != null && !RoomOfPower.live_list.contains(player.getStoredId()) && !player.isGM() && player.getReflection() == RoomOfPower.getEventReflection() && zone == RoomOfPower._roomZone) {
               Location ClearLoc = Location.parseLoc(ConfigRoomOfPower.ROP_CLEAR_LOC);
               ThreadPoolManager.getInstance().schedule(new RoomOfPower.TeleportTask(object, ClearLoc), 3000L);
            }

         }
      }

      public void onZoneLeave(Zone zone, Creature object) {
         if (object != null) {
            Player player = object.getPlayer();
            if (RoomOfPower._status == 2 && player != null && player.getTeam() != TeamType.NONE && RoomOfPower.live_list.contains(player.getStoredId()) && player.getReflection() == RoomOfPower.getEventReflection() && !RoomOfPower._roomZone.checkIfInZone(player.getX(), player.getY(), player.getZ(), RoomOfPower.getEventReflection())) {
            }

         }
      }

      // $FF: synthetic method
      ZoneListener(Object x0) {
         this();
      }
   }

   private static class EventTask extends RunnableImpl {
      private int currentMobStage;

      public EventTask(int stage) {
         this.currentMobStage = stage;
      }

      public void runImpl() {
         String spawnGroup = (String)RoomOfPower.MOB_STAGE_SPAWN_GROUP.get(this.currentMobStage);
         RoomOfPower.spawnAndAddListeners(spawnGroup);
      }
   }

   private static class DespawnNpcTask extends RunnableImpl {
      private String despawnGroup;

      private DespawnNpcTask(String despawnGroup) {
         this.despawnGroup = despawnGroup;
      }

      public void runImpl() throws Exception {
         RoomOfPower.despawnAndAddListeners(this.despawnGroup);
      }

      // $FF: synthetic method
      DespawnNpcTask(String x0, Object x1) {
         this(x0);
      }
   }

   private static class StartNextStateTask extends RunnableImpl {
      private int nextWaveMob;

      private StartNextStateTask(int waveMob) {
         this.nextWaveMob = waveMob;
      }

      public void runImpl() throws Exception {
         RoomOfPower.appointNextState(this.nextWaveMob);
      }

      // $FF: synthetic method
      StartNextStateTask(int x0, Object x1) {
         this(x0);
      }
   }

   private static class AnnounceNextStage extends RunnableImpl {
      private int nextWaveMob;

      private AnnounceNextStage(int waveMob) {
         this.nextWaveMob = waveMob;
      }

      public void runImpl() throws Exception {
         RoomOfPower.sayToParticipants("scripts.events.RoomOfPower.AnnounceStage" + this.nextWaveMob, (String[])null);
      }

      // $FF: synthetic method
      AnnounceNextStage(int x0, Object x1) {
         this(x0);
      }
   }

   private static final class NpcListenerImpl implements OnDeathListener {
      private NpcListenerImpl() {
      }

      public void onDeath(Creature actor, Creature killer) {
         if (RoomOfPower._status == 2 && actor.getReflection() == RoomOfPower._reflection && actor.isNpc() && RoomOfPower._reflection.getNpcs().contains((NpcInstance)actor) && killer != null && killer.isPlayer() && RoomOfPower.live_list.contains(killer.getStoredId())) {
            Player playerKiller = killer.getPlayer();
            int currentCount = EventUtils.incAndGetEventKills(playerKiller, RoomOfPower._killList, 1);
            if (ConfigRoomOfPower.ROP_SHOW_KILLS) {
               RoomOfPower.updateTitle(playerKiller, currentCount);
            }

            if (!RoomOfPower.checkAlive()) {
               String despawnGroup = (String)RoomOfPower.MOB_STAGE_SPAWN_GROUP.get(RoomOfPower.CURRENT_MOB_STAGE);
               RoomOfPower.CURRENT_MOB_STAGE++;
               ThreadPoolManager.getInstance().schedule(new RoomOfPower.DespawnNpcTask(despawnGroup), 2000L);
               if (RoomOfPower.CURRENT_MOB_STAGE <= RoomOfPower.MOB_WAVES) {
                  ThreadPoolManager.getInstance().schedule(new RoomOfPower.AnnounceNextStage(RoomOfPower.CURRENT_MOB_STAGE), 3000L);
                  ThreadPoolManager.getInstance().schedule(new RoomOfPower.StartNextStateTask(RoomOfPower.CURRENT_MOB_STAGE), 10000L);
               } else {
                  ThreadPoolManager.getInstance().schedule(() -> {
                     RoomOfPower.endBattle(true);
                  }, 2500L);
               }
            }
         }

      }

      // $FF: synthetic method
      NpcListenerImpl(Object x0) {
         this();
      }
   }

   private static class TeleportRes implements Runnable {
      private final long playerStoreId;

      public TeleportRes(long id) {
         this.playerStoreId = id;
      }

      public void run() {
         Player player = GameObjectsStorage.getAsPlayer(this.playerStoreId);
         if (player != null && RoomOfPower._status == 2 && player.getTeam() != TeamType.NONE && RoomOfPower.live_list.contains(player.getStoredId())) {
            if (player.isDead()) {
               player.doRevive(100.0D);
               player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
               player.setCurrentCp((double)player.getMaxCp());
            }

            int n;
            int i;
            if (player.isMageClass()) {
               if (ConfigRoomOfPower.ROP_BUFFS_MAGE.length > 1) {
                  n = 0;

                  for(i = 0; i < ConfigRoomOfPower.ROP_BUFFS_MAGE.length; i += 2) {
                     EventUtils.giveBuff(player, SkillTable.getInstance().getInfo(ConfigRoomOfPower.ROP_BUFFS_MAGE[i], ConfigRoomOfPower.ROP_BUFFS_MAGE[i + 1]), ConfigRoomOfPower.ROP_ALT_BUFFS_DURATION, n++);
                  }
               }
            } else if (ConfigRoomOfPower.ROP_BUFFS_FIGHTER.length > 1) {
               n = 0;

               for(i = 0; i < ConfigRoomOfPower.ROP_BUFFS_FIGHTER.length; i += 2) {
                  EventUtils.giveBuff(player, SkillTable.getInstance().getInfo(ConfigRoomOfPower.ROP_BUFFS_FIGHTER[i], ConfigRoomOfPower.ROP_BUFFS_FIGHTER[i + 1]), ConfigRoomOfPower.ROP_ALT_BUFFS_DURATION, n++);
               }
            }
         }

      }
   }

   private static final class PlayerListenerImpl implements OnPlayerExitListener, OnDeathListener, OnTeleportListener {
      private PlayerListenerImpl() {
      }

      public void onPlayerExit(Player player) {
         if (RoomOfPower._status == 0 && RoomOfPower._isRegistrationActive) {
            RoomOfPower.removePlayer(player);
         } else if (RoomOfPower._status == 1) {
            RoomOfPower.removePlayer(player);
            RoomOfPower.backPlayer(player);
         } else {
            if (RoomOfPower._status == 2 && player != null && player.getTeam() != TeamType.NONE) {
               RoomOfPower.removePlayer(player);
               RoomOfPower.checkLive();
            }

         }
      }

      public void onDeath(Creature actor, Creature killer) {
         if (RoomOfPower._status == 2 && actor != null && actor.isPlayer() && actor.getTeam() != TeamType.NONE) {
            if (ConfigRoomOfPower.ROP_DEATH_COUNT > 0) {
               AtomicInteger count = (AtomicInteger)RoomOfPower._deathList.get(actor.getObjectId());
               if (count == null) {
                  RoomOfPower._deathList.put(actor.getObjectId(), new AtomicInteger(ConfigRoomOfPower.ROP_DEATH_COUNT));
                  count = (AtomicInteger)RoomOfPower._deathList.get(actor.getObjectId());
               }

               int currentCount = count.get();
               if (currentCount > 0) {
                  currentCount = count.decrementAndGet();
                  ThreadPoolManager.getInstance().schedule(new RoomOfPower.TeleportRes(actor.getStoredId()), (long)ConfigRoomOfPower.ROP_RESURRECT_DELAY * 1000L);
                  actor.sendMessage((new CustomMessage("scripts.events.RoomOfPower.Ressurection", (Player)actor, new Object[0])).addNumber((long)ConfigRoomOfPower.ROP_RESURRECT_DELAY).addNumber((long)currentCount));
               } else {
                  actor.sendMessage(new CustomMessage("scripts.events.RoomOfPower.Death", (Player)actor, new Object[0]));
               }
            } else {
               actor.sendMessage(new CustomMessage("scripts.events.RoomOfPower.Death", (Player)actor, new Object[0]));
            }

            RoomOfPower.checkLive();
         }

      }

      public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
         if (RoomOfPower._status == 2 && RoomOfPower.live_list.contains(player.getStoredId()) && (!RoomOfPower._roomZone.checkIfInZone(x, y, z) || reflection != RoomOfPower._reflection)) {
            RoomOfPower.removePlayer(player);
            RoomOfPower.backPlayer(player);
         }
      }

      // $FF: synthetic method
      PlayerListenerImpl(Object x0) {
         this();
      }
   }

   public static class BattleTimer extends RunnableImpl {
      public void runImpl() {
         RoomOfPower.broadCastTimer();
      }
   }
}
