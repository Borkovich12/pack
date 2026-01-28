package events.TreasureHunting;

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
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.database.mysql;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.PlayerListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.model.WorldRegion;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NickNameChanged;
import l2.gameserver.network.l2.s2c.ObserverStart;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.ReflectionUtils;
import l2.gameserver.utils.TimeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class TreasureHunting extends Functions implements ScriptFile {
   private static final Logger _log = LoggerFactory.getLogger(TreasureHunting.class);
   private static List<Long> players_list1;
   private static List<Long> players_list2;
   private static List<Long> live_list1;
   private static List<Long> live_list2;
   private static List<String> _restrictIp = new ArrayList();
   private static List<String> _restrictHwid = new ArrayList();
   private static final int[] removeEffects = new int[]{442, 443, 1411, 1418, 1427};
   private static int count1 = 0;
   private static int count2 = 0;
   private static boolean _clearing;
   private static Reflection _reflection = null;
   private static boolean _isRegistrationActive = false;
   private static int _status = 0;
   private static int _time_to_start;
   private static int _category;
   private static int _pre_category;
   private static int _minLevel;
   private static int _maxLevel;
   private static ScheduledFuture<?> _startTask;
   private static ScheduledFuture<?> _endTask;
   private static Zone _zone = null;
   private static TreasureHunting.ZoneListener _zoneListener = new TreasureHunting.ZoneListener();
   private static Calendar _date;
   private static NpcInstance _regNpc;
   private static Map<Integer, List<Integer>> _equip = new ConcurrentHashMap();
   private static Map<Integer, List<Integer>> _destroy = new ConcurrentHashMap();
   private static Map<Integer, List<Effect>> _saveBuffs = new ConcurrentHashMap();
   private static Map<Integer, AtomicInteger> _killList1 = new ConcurrentHashMap();
   private static Map<Integer, AtomicInteger> _killList2 = new ConcurrentHashMap();
   private static Map<Integer, AtomicInteger> _scoreList1 = new ConcurrentHashMap();
   private static Map<Integer, AtomicInteger> _scoreList2 = new ConcurrentHashMap();
   private static long fightBeginTime;
   private static ScheduledFuture<?> _timerTask;
   private static Map<Integer, Reflection> spectators = new ConcurrentHashMap();
   private static boolean _active = false;
   private static final TreasureHunting.NpcOnDeathListenerImpl _npcListeners = new TreasureHunting.NpcOnDeathListenerImpl();
   private static final PlayerListener _listeners = new TreasureHunting.PlayerListenerImpl();
   private static final String TITLE_VAR = "treasure_hunter_title";

   public void onLoad() {
      _log.info("=================================================================");
      _log.info("Load Treasure Hunting event.");
      _log.info("Telegram: MerdoxOne");
      _log.info("Skype: MerdoxOne");
      ConfigTreasureHunting.load();
      if (ConfigTreasureHunting.TREASURE_HUNTING_ENABLE_COMMAND) {
         VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new TreasureHunting.VoicedCommand());
      }

      _active = ServerVariables.getString("TreasureHunting", "off").equalsIgnoreCase("on");
      loadCustomItems();
      if (_active) {
         executeTask("events.TreasureHunting.TreasureHunting", "preLoad", new Object[0], 1000L);
      }

      _log.info("Loaded Event: TreasureHunting");
      _log.info("=================================================================");
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
            executeTask("events.TreasureHunting.TreasureHunting", "preLoad", new Object[0], 10000L);
            ServerVariables.set("TreasureHunting", "on");
            _log.info("Event 'TreasureHunting' activated.");
            player.sendMessage(new CustomMessage("scripts.events.TreasureHunting.AnnounceEventStarted", player, new Object[0]));
         } else {
            player.sendMessage(player.isLangRus() ? "'TreasureHunting' эвент уже активен." : "Event 'TreasureHunting' already active.");
         }

         _active = true;
         this.show(HtmCache.getInstance().getNotNull("admin/events/events.htm", player), player);
      }
   }

   public void deactivateEvent() {
      Player player = this.getSelf();
      if (player != null && player.getPlayerAccess().IsEventGm) {
         if (_active) {
            ServerVariables.unset("TreasureHunting");
            _log.info("Event 'TreasureHunting' deactivated.");
            player.sendMessage(new CustomMessage("scripts.events.TreasureHunting.AnnounceEventStopped", player, new Object[0]));
         } else {
            player.sendMessage("Event 'TreasureHunting' not active.");
         }

         _active = false;
         this.show(HtmCache.getInstance().getNotNull("admin/events/events.htm", player), player);
      }
   }

   public static boolean isRunned(String name) {
      return _isRegistrationActive || _status > 0;
   }

   public static int getMinLevelForCategory(int category) {
      switch(category) {
      case 1:
         return 20;
      case 2:
         return 30;
      case 3:
         return 40;
      case 4:
         return 52;
      case 5:
         return 62;
      case 6:
         return 76;
      default:
         return 0;
      }
   }

   public static int getMaxLevelForCategory(int category) {
      switch(category) {
      case 1:
         return 29;
      case 2:
         return 39;
      case 3:
         return 51;
      case 4:
         return 61;
      case 5:
         return 75;
      case 6:
         return 80;
      default:
         return 80;
      }
   }

   public static int getCategory(int level) {
      if (level >= 20 && level <= 29) {
         return 1;
      } else if (level >= 30 && level <= 39) {
         return 2;
      } else if (level >= 40 && level <= 51) {
         return 3;
      } else if (level >= 52 && level <= 61) {
         return 4;
      } else if (level >= 62 && level <= 75) {
         return 5;
      } else {
         return level >= 76 ? 6 : 0;
      }
   }

   public void start(String[] var) {
      Player player = this.getSelf();
      if (player != null && player.getPlayerAccess().IsEventGm) {
         startOk(var);
      }
   }

   private static void initReflection() {
      int _instanceId = ConfigTreasureHunting.TREASURE_HUNTING_IN_INSTANCE ? 108 : 0;
      if (_instanceId > 0) {
         InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(_instanceId);
         _reflection = new Reflection();
         _reflection.init(instantZone);
         _zone = _reflection.getZone(ConfigTreasureHunting.TREASURE_HUNTING_ZONE_NAME);
         _zone.addListener(_zoneListener);
      } else {
         _zone = ReflectionUtils.getZone(ConfigTreasureHunting.TREASURE_HUNTING_ZONE_NAME);
         _zone.addListener(_zoneListener);
      }

   }

   public static void doneReflection() {
      if (_reflection != null) {
         _reflection.collapse();
         _reflection = null;
      }

      _zone.removeListener(_zoneListener);
   }

   public static void spawnManager() {
      if (ConfigTreasureHunting.TREASURE_HUNTING_SPAWN_REG_MANAGER) {
         try {
            Location npcLoc = Location.parseLoc(ConfigTreasureHunting.TREASURE_HUNTING_REG_MANAGER_LOC);
            _regNpc = NpcUtils.spawnSingle(ConfigTreasureHunting.TREASURE_HUNTING_REG_MANAGER_ID, npcLoc);
         } catch (Exception var1) {
            _log.error("TreasureHunting: fail spawn registered manager", var1);
         }
      }

   }

   private static void unSpawnManager() {
      if (ConfigTreasureHunting.TREASURE_HUNTING_SPAWN_REG_MANAGER && _regNpc != null) {
         try {
            _regNpc.deleteMe();
            _regNpc = null;
         } catch (Exception var1) {
            _log.error("TreasureHunting: fail despawn manager", var1);
         }
      }

   }

   private static void startOk(String[] var) {
      if (var.length >= 1) {
         if (!_isRegistrationActive && _status <= 0) {
            try {
               _category = Integer.parseInt(var[0]);
            } catch (Exception var2) {
               _log.info("TreasureHunting not started: can't parse category");
               return;
            }

            if (_category == -1) {
               _minLevel = 1;
               _maxLevel = 80;
            } else {
               _minLevel = getMinLevelForCategory(_category);
               _maxLevel = getMaxLevelForCategory(_category);
            }

            if (_endTask != null) {
               _log.info("TreasureHunting not started: end task is active");
            } else {
               if (_startTask != null) {
                  _startTask.cancel(false);
                  _startTask = null;
               }

               _status = 0;
               _isRegistrationActive = true;
               _clearing = false;
               _time_to_start = ConfigTreasureHunting.TREASURE_HUNTING_TIME_TO_START;
               players_list1 = new CopyOnWriteArrayList();
               players_list2 = new CopyOnWriteArrayList();
               live_list1 = new CopyOnWriteArrayList();
               live_list2 = new CopyOnWriteArrayList();
               _killList1 = new ConcurrentHashMap();
               _killList2 = new ConcurrentHashMap();
               _scoreList1 = new ConcurrentHashMap();
               _scoreList2 = new ConcurrentHashMap();
               _restrictIp = new ArrayList();
               _restrictHwid = new ArrayList();
               count1 = 0;
               count2 = 0;
               spawnManager();
               String[] param = new String[]{String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel)};
               sayToAll("scripts.events.TreasureHunting.AnnouncePreStart", param);
               sayToAll("scripts.events.TreasureHunting.AnnounceReg", (String[])null);
               executeTask("events.TreasureHunting.TreasureHunting", "question", new Object[0], 5000L);
               executeTask("events.TreasureHunting.TreasureHunting", "announce", new Object[0], 60000L);
            }
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

         player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "TreasureHunting", "TreasureHunting: " + cm.toString()));
      }
   }

   private static void sayToParticipants(String address, String[] replacements) {
      Iterator var2 = EventUtils.getPlayers(live_list1, live_list2).iterator();

      while(true) {
         Player player;
         CustomMessage cm;
         String[] var5;
         int var6;
         int var7;
         String s;
         do {
            if (!var2.hasNext()) {
               if (spectators != null && !spectators.isEmpty()) {
                  var2 = EventUtils.getSpectators(spectators.keySet()).iterator();

                  while(true) {
                     do {
                        if (!var2.hasNext()) {
                           return;
                        }

                        player = (Player)var2.next();
                     } while(player == null);

                     cm = new CustomMessage(address, player, new Object[0]);
                     if (replacements != null) {
                        var5 = replacements;
                        var6 = replacements.length;

                        for(var7 = 0; var7 < var6; ++var7) {
                           s = var5[var7];
                           cm.addString(s);
                        }
                     }

                     player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "TreasureHunting", "TreasureHunting: " + cm.toString()));
                  }
               }

               return;
            }

            player = (Player)var2.next();
         } while(player == null);

         cm = new CustomMessage(address, player, new Object[0]);
         if (replacements != null) {
            var5 = replacements;
            var6 = replacements.length;

            for(var7 = 0; var7 < var6; ++var7) {
               s = var5[var7];
               cm.addString(s);
            }
         }

         player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "TreasureHunting", "TreasureHunting: " + cm.toString()));
      }
   }

   public static void question() {
      if (ConfigTreasureHunting.TREASURE_HUNTING_SEND_REG_WINDOW) {
         Iterator var0 = GameObjectsStorage.getAllPlayersForIterate().iterator();

         while(var0.hasNext()) {
            Player player = (Player)var0.next();
            if (isCheckWindow(player)) {
               player.scriptRequest((new CustomMessage("scripts.events.TreasureHunting.AskPlayer", player, new Object[0])).toString(), "events.TreasureHunting.TreasureHunting:addPlayer", new Object[0]);
            }
         }
      }

   }

   private static boolean isCheckWindow(Player player) {
      if (player == null) {
         return false;
      } else if (player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel) {
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
         String[] param = new String[]{String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel)};
         sayToAll("scripts.events.TreasureHunting.AnnouncePreStart", param);
         executeTask("events.TreasureHunting.TreasureHunting", "announce", new Object[0], 60000L);
      } else {
         if (players_list1.isEmpty() || players_list2.isEmpty() || players_list1.size() + players_list2.size() < ConfigTreasureHunting.TREASURE_HUNTING_MIN_PLAYERS) {
            sayToAll("scripts.events.TreasureHunting.AnnounceEventCancelled", (String[])null);
            _isRegistrationActive = false;
            _status = 0;
            unSpawnManager();
            executeTask("events.TreasureHunting.TreasureHunting", "preLoad", new Object[0], 10000L);
            return;
         }

         _status = 1;
         _isRegistrationActive = false;
         sayToAll("scripts.events.TreasureHunting.AnnounceEventStarting", (String[])null);
         executeTask("events.TreasureHunting.TreasureHunting", "prepare", new Object[0], 5000L);
      }

   }

   public void unreg() {
      Player player = this.getSelf();
      if (player != null) {
         if (!_isRegistrationActive) {
            player.sendMessage(player.isLangRus() ? "Доступно только в период регистрации." : "Available only during the registration period.");
         } else {
            if (players_list1.contains(player.getStoredId())) {
               players_list1.remove(player.getStoredId());
               live_list1.remove(player.getStoredId());
               if (ConfigTreasureHunting.TREASURE_HUNTING_IP_RESTRICTION) {
                  _restrictIp.remove(player.getIP());
               }

               if (ConfigTreasureHunting.TREASURE_HUNTING_HWID_RESTRICTION && player.getNetConnection() != null) {
                  _restrictHwid.remove(player.getNetConnection().getHwid());
               }
            } else {
               if (!players_list2.contains(player.getStoredId())) {
                  player.sendMessage(player.isLangRus() ? "Вы не являетесь участником TreasureHunting." : "You are not a participant of TreasureHunting.");
                  return;
               }

               players_list2.remove(player.getStoredId());
               live_list2.remove(player.getStoredId());
               if (ConfigTreasureHunting.TREASURE_HUNTING_IP_RESTRICTION) {
                  _restrictIp.remove(player.getIP());
               }

               if (ConfigTreasureHunting.TREASURE_HUNTING_HWID_RESTRICTION && player.getNetConnection() != null) {
                  _restrictHwid.remove(player.getNetConnection().getHwid());
               }
            }

            player.sendMessage(player.isLangRus() ? "Вы успешно удалены с регистрации на TreasureHunting." : "You have been removed from registration on TreasureHunting.");
         }
      }
   }

   public void addPlayer() {
      Player player = this.getSelf();
      if (player != null && checkPlayer(player, true)) {
         if (players_list1.size() + players_list2.size() >= ConfigTreasureHunting.TREASURE_HUNTING_MAX_PLAYERS) {
            player.sendMessage(player.isLangRus() ? "Достигнут лимит допустимого кол-ва участников." : "The limit of the allowed number of participants has been reached.");
         } else {
            if (ConfigTreasureHunting.TREASURE_HUNTING_IP_RESTRICTION) {
               if (_restrictIp.contains(player.getIP())) {
                  player.sendMessage(player.isLangRus() ? "Игрок с данным IP уже зарегистрирован." : "The player with this IP is already registered.");
                  return;
               }

               _restrictIp.add(player.getIP());
            }

            if (ConfigTreasureHunting.TREASURE_HUNTING_HWID_RESTRICTION && player.getNetConnection() != null) {
               if (_restrictHwid.contains(player.getNetConnection().getHwid())) {
                  player.sendMessage(player.isLangRus() ? "Игрок с данным железом уже зарегистрирован." : "The player with this hwid is already registered.");
                  return;
               }

               _restrictHwid.add(player.getNetConnection().getHwid());
            }

            int team = false;
            int size1 = players_list1.size();
            int size2 = players_list2.size();
            int team;
            if (size1 > size2) {
               team = 2;
            } else if (size1 < size2) {
               team = 1;
            } else {
               team = Rnd.get(1, 2);
            }

            if (team == 1) {
               players_list1.add(player.getStoredId());
               live_list1.add(player.getStoredId());
               show(new CustomMessage("scripts.events.TreasureHunting.Registered", player, new Object[0]), player);
            } else if (team == 2) {
               players_list2.add(player.getStoredId());
               live_list2.add(player.getStoredId());
               show(new CustomMessage("scripts.events.TreasureHunting.Registered", player, new Object[0]), player);
            } else {
               _log.info("WTF??? Command id 0 in TreasureHunting...");
            }

         }
      }
   }

   public static boolean checkPlayer(Player player, boolean first) {
      if (first && !_isRegistrationActive) {
         show(new CustomMessage("scripts.events.Late", player, new Object[0]), player);
         return false;
      } else if (!first || !players_list1.contains(player.getStoredId()) && !players_list2.contains(player.getStoredId())) {
         if (player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel) {
            if (player.isMounted()) {
               player.sendMessage(player.isLangRus() ? "Верхом на эвент нельзя." : "You can't do it while riding.");
               return false;
            } else if (player.isInDuel()) {
               show(new CustomMessage("scripts.events.TreasureHunting.CancelledDuel", player, new Object[0]), player);
               return false;
            } else if (player.getTeam() != TeamType.NONE) {
               show(new CustomMessage("scripts.events.TreasureHunting.CancelledOtherEvent", player, new Object[0]), player);
               return false;
            } else if (!player.isOlyParticipant() && (!first || !ParticipantPool.getInstance().isRegistred(player))) {
               if (player.isInParty() && player.getParty().isInDimensionalRift()) {
                  show(new CustomMessage("scripts.events.TreasureHunting.CancelledOtherEvent", player, new Object[0]), player);
                  return false;
               } else if (player.isTeleporting()) {
                  show(new CustomMessage("scripts.events.TreasureHunting.CancelledTeleport", player, new Object[0]), player);
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
               } else if (ArrayUtils.contains(ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_CLASS_IDS, player.getActiveClassId())) {
                  player.sendMessage(player.isLangRus() ? "Данному классу запрещено участвовать в ивенте." : "This class is not allowed to participate in the event.");
                  return false;
               } else {
                  return true;
               }
            } else {
               show(new CustomMessage("scripts.events.TreasureHunting.CancelledOlympiad", player, new Object[0]), player);
               return false;
            }
         } else {
            show(new CustomMessage("scripts.events.TreasureHunting.CancelledLevel", player, new Object[0]), player);
            return false;
         }
      } else {
         show(new CustomMessage("scripts.events.TreasureHunting.Cancelled", player, new Object[0]), player);
         return false;
      }
   }

   public static void prepare() {
      initReflection();
      unSpawnManager();
      cleanPlayers();
      int size = players_list1.size() + players_list2.size();
      if (!players_list1.isEmpty() && !players_list2.isEmpty() && size >= ConfigTreasureHunting.TREASURE_HUNTING_MIN_PLAYERS) {
         clearArena();
         _saveBuffs = new ConcurrentHashMap(size);
         if (ConfigTreasureHunting.TREASURE_HUNTING_ENABLE_CUSTOM_ITEMS) {
            _equip = new ConcurrentHashMap(size);
            _destroy = new ConcurrentHashMap(size);
         }

         executeTask("events.TreasureHunting.TreasureHunting", "paralyzePlayers", new Object[0], 100L);
         executeTask("events.TreasureHunting.TreasureHunting", "teleportPlayersToColiseum", new Object[0], 3000L);
         executeTask("events.TreasureHunting.TreasureHunting", "go", new Object[0], (long)ConfigTreasureHunting.TREASURE_HUNTING_TIME_PARALYZE * 1000L);
         sayToParticipants("scripts.events.TreasureHunting.AnnounceFinalCountdown", new String[]{String.valueOf(ConfigTreasureHunting.TREASURE_HUNTING_TIME_PARALYZE)});
      } else {
         sayToAll("scripts.events.TreasureHunting.AnnounceEventCancelled", (String[])null);
         _isRegistrationActive = false;
         _status = 0;
         executeTask("events.TreasureHunting.TreasureHunting", "preLoad", new Object[0], 10000L);
         doneReflection();
      }
   }

   public static void go() {
      _status = 2;
      if (ConfigTreasureHunting.TREASURE_HUNTING_CANCEL_ALL_BUFF) {
         removeBuff();
      } else {
         upParalyzePlayers();
      }

      if (checkInZone()) {
         clearArena();
      } else {
         spawnChests();
         clearArena();
         buffPlayers();
         sayToParticipants("scripts.events.TreasureHunting.AnnounceFight", (String[])null);
         _endTask = executeTask("events.TreasureHunting.TreasureHunting", "endBattle", new Object[0], (long)ConfigTreasureHunting.TREASURE_HUNTING_BATTLE_DURATION * 60000L);
         if (ConfigTreasureHunting.TREASURE_HUNTING_BROADCAST_TIMER) {
            fightBeginTime = System.currentTimeMillis();
            _timerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new TreasureHunting.BattleTimer(), 0L, 1010L);
         }

      }
   }

   public static void broadCastTimer() {
      int secondsLeft = (int)((fightBeginTime + TimeUnit.MINUTES.toMillis((long)ConfigTreasureHunting.TREASURE_HUNTING_BATTLE_DURATION) - System.currentTimeMillis()) / 1000L);
      int minutes = secondsLeft / 60;
      int seconds = secondsLeft % 60;
      ExShowScreenMessage packet = new ExShowScreenMessage(String.format("%02d:%02d %d / %d", minutes, seconds, count1, count2), 1010, -1, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, false);
      Iterator var4 = EventUtils.getPlayers(players_list1).iterator();

      Player player;
      while(var4.hasNext()) {
         player = (Player)var4.next();
         player.sendPacket(packet);
      }

      var4 = EventUtils.getPlayers(players_list2).iterator();

      while(var4.hasNext()) {
         player = (Player)var4.next();
         player.sendPacket(packet);
      }

      if (spectators != null && !spectators.isEmpty()) {
         var4 = EventUtils.getSpectators(spectators.keySet()).iterator();

         while(var4.hasNext()) {
            player = (Player)var4.next();
            player.sendPacket(packet);
         }
      }

   }

   public static void stopTimerTask() {
      if (_timerTask != null) {
         _timerTask.cancel(true);
         _timerTask = null;
      }

   }

   private static void spawnChests() {
      _reflection.spawnByGroup(ConfigTreasureHunting.TREASURE_HUNTING_CHEST_SPAWN_GROUP);
      Map<String, List<Spawner>> spawners = getSpawners();
      if (spawners != null) {
         Iterator var1 = ((List)spawners.get(ConfigTreasureHunting.TREASURE_HUNTING_CHEST_SPAWN_GROUP)).iterator();

         while(var1.hasNext()) {
            Spawner spawner = (Spawner)var1.next();
            List<NpcInstance> chests = spawner.getAllSpawned();
            Iterator var4 = chests.iterator();

            while(var4.hasNext()) {
               NpcInstance chest = (NpcInstance)var4.next();
               chest.addListener(_npcListeners);
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

   private static void despawnChests() {
      Iterator var0 = _reflection.getSpawns().iterator();

      while(var0.hasNext()) {
         Spawner spawner = (Spawner)var0.next();
         List<NpcInstance> chests = spawner.getAllSpawned();
         Iterator var3 = chests.iterator();

         while(var3.hasNext()) {
            NpcInstance chest = (NpcInstance)var3.next();
            chest.removeListener(_npcListeners);
         }
      }

      _reflection.despawnByGroup(ConfigTreasureHunting.TREASURE_HUNTING_CHEST_SPAWN_GROUP);
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
         _saveBuffs.put(player.getObjectId(), effects);
      }

   }

   private static void buffPlayers() {
      if (ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_FIGHTER.length >= 2 || ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_MAGE.length >= 2) {
         Iterator var0 = EventUtils.getPlayers(players_list1, players_list2).iterator();

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

                  if (ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_FIGHTER.length > 1) {
                     n = 0;

                     for(i = 0; i < ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_FIGHTER.length; i += 2) {
                        EventUtils.giveBuff(player, SkillTable.getInstance().getInfo(ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_FIGHTER[i], ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_FIGHTER[i + 1]), ConfigTreasureHunting.TREASURE_HUNTING_ALT_BUFFS_DURATION, n++);
                     }
                  }
               }
            } while(ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_MAGE.length <= 1);

            n = 0;

            for(i = 0; i < ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_MAGE.length; i += 2) {
               EventUtils.giveBuff(player, SkillTable.getInstance().getInfo(ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_MAGE[i], ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_MAGE[i + 1]), ConfigTreasureHunting.TREASURE_HUNTING_ALT_BUFFS_DURATION, n++);
            }
         }
      }
   }

   private static boolean checkInZone() {
      boolean useBC = ConfigTreasureHunting.TREASURE_HUNTING_RETURN_POINT.length < 3;
      Location ClearLoc = Location.parseLoc(ConfigTreasureHunting.TREASURE_HUNTING_CLEAR_LOC);
      Iterator var2 = EventUtils.getPlayers(players_list1, players_list2).iterator();

      while(true) {
         while(true) {
            Player player;
            do {
               do {
                  if (!var2.hasNext()) {
                     if (players_list1.size() >= 1 && players_list2.size() >= 1) {
                        return false;
                     }

                     endBattle();
                     return true;
                  }

                  player = (Player)var2.next();
               } while(player == null);
            } while(_zone.checkIfInZone(player.getX(), player.getY(), player.getZ(), getReflection()));

            removePlayer(player);
            if (useBC && player.getVar("TreasureHunting_backCoords") == null) {
               player.teleToLocation(ClearLoc, ReflectionManager.DEFAULT);
            } else {
               backPlayer(player);
            }
         }
      }
   }

   private static void removeBuff() {
      Iterator var0 = EventUtils.getPlayers(players_list1, players_list2).iterator();

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
               if (summon.isPet() || ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_SUMMONS.length > 0 && ArrayUtils.contains(ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_SUMMONS, summon.getNpcId())) {
                  summon.unSummon();
               }
            }
         } catch (Exception var3) {
            _log.error("on removeBuff", var3);
         }
      }
   }

   public static void endBattle() {
      if (!_clearing) {
         _clearing = true;

         try {
            if (_endTask != null) {
               _endTask.cancel(false);
               _endTask = null;
            }
         } catch (Exception var1) {
         }

         stopTimerTask();
         if (_status != 0) {
            _status = 0;
            despawnChests();
            removeAura();
            String[] param = new String[]{String.valueOf(count1), String.valueOf(count2)};
            if (live_list1.isEmpty() && live_list2.isEmpty()) {
               sayToAll("scripts.events.TreasureHunting.AnnounceFinishedDraw", (String[])null);
               if (ConfigTreasureHunting.TREASURE_HUNTING_REWARD_DRAW.length > 1 || ConfigTreasureHunting.TREASURE_HUNTING_REWARD_DRAW_PREMIUM.length > 1) {
                  giveItemsToWinner(0);
               }
            } else if (live_list1.isEmpty()) {
               sayToAll("scripts.events.TreasureHunting.AnnounceFinishedRedWins", param);
               giveItemsToWinner(2);
            } else if (live_list2.isEmpty()) {
               sayToAll("scripts.events.TreasureHunting.AnnounceFinishedBlueWins", param);
               giveItemsToWinner(1);
            } else if (count1 > count2) {
               sayToAll("scripts.events.TreasureHunting.AnnounceFinishedBlueWins", param);
               giveItemsToWinner(1);
            } else if (count2 > count1) {
               sayToAll("scripts.events.TreasureHunting.AnnounceFinishedRedWins", param);
               giveItemsToWinner(2);
            } else {
               sayToAll("scripts.events.TreasureHunting.AnnounceFinishedDraw", (String[])null);
               if (ConfigTreasureHunting.TREASURE_HUNTING_REWARD_DRAW.length > 1 || ConfigTreasureHunting.TREASURE_HUNTING_REWARD_DRAW_PREMIUM.length > 1) {
                  giveItemsToWinner(0);
               }
            }

            sayToParticipants("scripts.events.TreasureHunting.AnnounceEnd", new String[]{String.valueOf(ConfigTreasureHunting.TREASURE_HUNTING_TIME_BACK)});
            executeTask("events.TreasureHunting.TreasureHunting", "end", new Object[0], (long)ConfigTreasureHunting.TREASURE_HUNTING_TIME_BACK * 1000L);
            _isRegistrationActive = false;
            _saveBuffs.clear();
            count1 = 0;
            count2 = 0;
            _killList1.clear();
            _killList2.clear();
            _scoreList1.clear();
            _scoreList2.clear();
            _clearing = false;
         }
      }
   }

   public static void end() {
      ressurectPlayers();
      executeTask("events.TreasureHunting.TreasureHunting", "teleportPlayersToSavedCoords", new Object[0], 100L);
      executeTask("events.TreasureHunting.TreasureHunting", "removeSpectators", new Object[0], 200L);
      executeTask("events.TreasureHunting.TreasureHunting", "doneReflection", new Object[0], 1000L);
      executeTask("events.TreasureHunting.TreasureHunting", "preLoad", new Object[0], 10000L);
   }

   private static void giveItemsToWinner(int team) {
      List<Player> players = getWinningTeamPlayers(0);
      giveListenerParticipate(players);
      List<Integer> topPlayers = new ArrayList();
      List<Integer> topKillers = giveTopKillerReward(players);
      if (!topKillers.isEmpty()) {
         topPlayers.addAll(topKillers);
      }

      List<Integer> topOpeners = giveTopOpenerReward(players);
      if (!topOpeners.isEmpty()) {
         topPlayers.addAll(topOpeners);
      }

      giveWinnersReward(team, topPlayers);
      giveLoosersReward(team, topPlayers);
   }

   private static void giveListenerParticipate(List<Player> players) {
      Iterator var1 = players.iterator();

      while(var1.hasNext()) {
         Player player = (Player)var1.next();
         player.getListeners().onTreasureHuntingEvent(false);
      }

   }

   private static List<Integer> giveTopKillerReward(List<Player> players) {
      if (!ConfigTreasureHunting.TREASURE_HUNTING_TOP_KILLER_ENABLE) {
         return Collections.emptyList();
      } else {
         Map<Integer, Integer> allPlayerScores = new HashMap();
         Iterator var2 = players.iterator();

         while(var2.hasNext()) {
            Player player = (Player)var2.next();
            if (player != null) {
               int currentKills = 0;
               if (players_list1.contains(player.getStoredId())) {
                  currentKills = EventUtils.getEventKills(player, _killList1);
               } else if (players_list2.contains(player.getStoredId())) {
                  currentKills = EventUtils.getEventKills(player, _killList2);
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

         if (ConfigTreasureHunting.TREASURE_HUNTING_TOP_KILLER_ANNOUNCE && sortedMap.size() > 0 && (ConfigTreasureHunting.TREASURE_HUNTING_TOP_KILLER_REWARD.size() > 0 || ConfigTreasureHunting.TREASURE_HUNTING_TOP_KILLER_REWARD_PREMIUM.size() > 0)) {
            sayToAll("scripts.events.TreasureHunting.AnnounceMsgTopKiller", (String[])null);
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
               if (players_list1.contains(player.getStoredId())) {
                  currentKills = EventUtils.getEventKills(player, _killList1);
               } else if (players_list2.contains(player.getStoredId())) {
                  currentKills = EventUtils.getEventKills(player, _killList2);
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

                  topKillers.add(objectId);
                  if (ConfigTreasureHunting.TREASURE_HUNTING_TOP_KILLER_ANNOUNCE) {
                     sayToAll("scripts.events.TreasureHunting.AnnouncePlaceTopKiller", new String[]{String.valueOf(place), player.getName(), String.valueOf(currentKills)});
                  }

                  ++place;
               }
            }
         }

         return topKillers;
      }
   }

   private static List<Integer> giveTopOpenerReward(List<Player> players) {
      if (!ConfigTreasureHunting.TREASURE_HUNTING_TOP_OPENER_ENABLE) {
         return Collections.emptyList();
      } else {
         Map<Integer, Integer> allPlayerScores = new HashMap();
         Iterator var2 = players.iterator();

         int place;
         while(var2.hasNext()) {
            Player player = (Player)var2.next();
            if (player != null) {
               place = 0;
               if (players_list1.contains(player.getStoredId())) {
                  place = EventUtils.getEventKills(player, _scoreList1);
               } else if (players_list2.contains(player.getStoredId())) {
                  place = EventUtils.getEventKills(player, _scoreList2);
               }

               allPlayerScores.put(player.getObjectId(), place);
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

         if (ConfigTreasureHunting.TREASURE_HUNTING_TOP_OPENER_ANNOUNCE && sortedMap.size() > 0 && (ConfigTreasureHunting.TREASURE_HUNTING_TOP_OPENER_REWARD.size() > 0 || ConfigTreasureHunting.TREASURE_HUNTING_TOP_OPENER_REWARD_PREMIUM.size() > 0)) {
            sayToAll("scripts.events.TreasureHunting.AnnounceMsgTopOpener", (String[])null);
         }

         place = 1;
         List<Integer> topOpener = new ArrayList();
         Iterator var6 = sortedMap.entrySet().iterator();

         while(var6.hasNext()) {
            Entry<Integer, Integer> entry = (Entry)var6.next();
            int objectId = (Integer)entry.getKey();
            Player player = GameObjectsStorage.getPlayer(objectId);
            if (player != null) {
               int currentOpens = 0;
               if (players_list1.contains(player.getStoredId())) {
                  currentOpens = EventUtils.getEventKills(player, _scoreList1);
               } else if (players_list2.contains(player.getStoredId())) {
                  currentOpens = EventUtils.getEventKills(player, _scoreList2);
               }

               if (currentOpens >= 1) {
                  int[] reward = (int[])getTopOpenerReward(player).get(place);
                  if (reward == null || reward.length <= 0) {
                     break;
                  }

                  for(int i = 0; i < reward.length; i += 2) {
                     int itemId = reward[i];
                     long itemCount = (long)reward[i + 1];
                     addItem(player, itemId, itemCount);
                  }

                  topOpener.add(objectId);
                  if (ConfigTreasureHunting.TREASURE_HUNTING_TOP_OPENER_ANNOUNCE) {
                     sayToAll("scripts.events.TreasureHunting.AnnouncePlaceTopOpener", new String[]{String.valueOf(place), player.getName(), String.valueOf(currentOpens)});
                  }

                  ++place;
               }
            }
         }

         return topOpener;
      }
   }

   private static void giveWinnersReward(int team, List<Integer> topPlayers) {
      Iterator var2 = getWinningTeamPlayers(team).iterator();

      while(true) {
         Player player;
         do {
            if (!var2.hasNext()) {
               return;
            }

            player = (Player)var2.next();
         } while(player == null);

         if (!topPlayers.contains(player.getObjectId())) {
            int[] reward = getWinnerReward(player, team);
            int currentKills = 0;
            if (players_list1.contains(player.getStoredId())) {
               currentKills = EventUtils.getEventKills(player, _killList1);
            } else if (players_list2.contains(player.getStoredId())) {
               currentKills = EventUtils.getEventKills(player, _killList2);
            }

            if (currentKills >= getMinWinnerKill(player)) {
               for(int i = 0; i < reward.length; i += 2) {
                  int itemId = reward[i];
                  long itemCount = (long)reward[i + 1];
                  addItem(player, itemId, itemCount);
               }
            }
         }

         player.getListeners().onTreasureHuntingEvent(true);
      }
   }

   private static void giveLoosersReward(int team, List<Integer> topPlayers) {
      if (team != 0 && (ConfigTreasureHunting.TREASURE_HUNTING_REWARD_LOSERS.length > 1 || ConfigTreasureHunting.TREASURE_HUNTING_REWARD_LOSERS_PREMIUM.length > 1)) {
         Iterator var2 = getLooserTeamPlayers(team).iterator();

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
               } while(topPlayers.contains(player.getObjectId()));

               reward = getLooserReward(player);
               currentKills = 0;
               if (players_list1.contains(player.getStoredId())) {
                  currentKills = EventUtils.getEventKills(player, _killList1);
               } else if (players_list2.contains(player.getStoredId())) {
                  currentKills = EventUtils.getEventKills(player, _killList2);
               }
            } while(currentKills < getMinLooserKill(player));

            for(int i = 0; i < reward.length; i += 2) {
               int itemId = reward[i];
               long itemCount = (long)reward[i + 1];
               addItem(player, itemId, itemCount);
            }
         }
      }
   }

   private static int[] getWinnerReward(Player player, int team) {
      if (team == 0) {
         return player.hasBonus() ? ConfigTreasureHunting.TREASURE_HUNTING_REWARD_DRAW_PREMIUM : ConfigTreasureHunting.TREASURE_HUNTING_REWARD_DRAW;
      } else {
         return player.hasBonus() ? ConfigTreasureHunting.TREASURE_HUNTING_REWARD_FINAL_PREMIUM : ConfigTreasureHunting.TREASURE_HUNTING_REWARD_FINAL;
      }
   }

   private static int[] getLooserReward(Player player) {
      return player.hasBonus() ? ConfigTreasureHunting.TREASURE_HUNTING_REWARD_LOSERS_PREMIUM : ConfigTreasureHunting.TREASURE_HUNTING_REWARD_LOSERS;
   }

   private static List<Player> getWinningTeamPlayers(int team) {
      if (team == 1) {
         return EventUtils.getPlayers(players_list1);
      } else {
         return team == 2 ? EventUtils.getPlayers(players_list2) : EventUtils.getPlayers(players_list1, players_list2);
      }
   }

   private static List<Player> getLooserTeamPlayers(int team) {
      return team == 1 ? EventUtils.getPlayers(players_list2) : EventUtils.getPlayers(players_list1);
   }

   private static Map<Integer, int[]> getTopOpenerReward(Player player) {
      return player.hasBonus() ? ConfigTreasureHunting.TREASURE_HUNTING_TOP_OPENER_REWARD_PREMIUM : ConfigTreasureHunting.TREASURE_HUNTING_TOP_OPENER_REWARD;
   }

   private static Map<Integer, int[]> getTopKillerReward(Player player) {
      return player.hasBonus() ? ConfigTreasureHunting.TREASURE_HUNTING_TOP_KILLER_REWARD_PREMIUM : ConfigTreasureHunting.TREASURE_HUNTING_TOP_KILLER_REWARD;
   }

   private static int getMinWinnerKill(Player player) {
      return player.hasBonus() ? ConfigTreasureHunting.TREASURE_HUNTING_WINNER_MIN_KILLS_PREMIUM : ConfigTreasureHunting.TREASURE_HUNTING_WINNER_MIN_KILLS;
   }

   private static int getMinLooserKill(Player player) {
      return player.hasBonus() ? ConfigTreasureHunting.TREASURE_HUNTING_LOSERS_MIN_KILLS_PREMIUM : ConfigTreasureHunting.TREASURE_HUNTING_LOSERS_MIN_KILLS;
   }

   public static void teleportPlayersToColiseum() {
      boolean useBC = ConfigTreasureHunting.TREASURE_HUNTING_RETURN_POINT.length < 3;
      Location ClearLoc = Location.parseLoc(ConfigTreasureHunting.TREASURE_HUNTING_CLEAR_LOC);
      Iterator var2 = EventUtils.getPlayers(players_list1, players_list2).iterator();

      while(true) {
         Player player;
         do {
            if (!var2.hasNext()) {
               return;
            }

            player = (Player)var2.next();
         } while(player == null);

         saveTitle(player);
         if (players_list1.contains(player.getStoredId())) {
            _scoreList1.put(player.getObjectId(), new AtomicInteger(0));
         }

         if (players_list2.contains(player.getStoredId())) {
            _scoreList2.put(player.getObjectId(), new AtomicInteger(0));
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

            player.setVar("TreasureHunting_backCoords", backCoords, -1L);
         }

         player.leaveParty();
         List<Location> teamLoc = player.getTeam() == TeamType.BLUE ? ConfigTreasureHunting.TREASURE_HUNTING_BLUE_TEAM_LOCS : ConfigTreasureHunting.TREASURE_HUNTING_RED_TEAM_LOCS;
         player.teleToLocation(Location.findAroundPosition((Location)Rnd.get(teamLoc), 100, getReflection().getGeoIndex()), getReflection());
      }
   }

   public static void teleportPlayersToSavedCoords() {
      Iterator var0 = EventUtils.getPlayers(players_list1, players_list2).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         if (player != null) {
            backPlayer(player);
         }
      }

      unsetLastCoords();
   }

   public static void removeSpectators() {
      if (spectators != null && !spectators.isEmpty()) {
         Iterator var0 = EventUtils.getSpectators(spectators.keySet()).iterator();

         while(var0.hasNext()) {
            Player player = (Player)var0.next();
            if (player != null) {
               player.leaveObserverMode();
               player.unsetVar("onTHObservationEnd");
            }
         }

         spectators.clear();
      }

   }

   public static void paralyzePlayers() {
      Skill sk = SkillTable.getInstance().getInfo(4515, 1);
      Iterator var1 = EventUtils.getPlayers(players_list1, players_list2).iterator();

      while(true) {
         Player player;
         do {
            if (!var1.hasNext()) {
               return;
            }

            player = (Player)var1.next();
         } while(player == null);

         EventUtils.healPlayer(player);
         player.setInTreasureHunting(true);
         player.setResurectProhibited(true);
         player.addListener(_listeners);
         saveBuffs(player);
         int[] equipableIds = ArrayUtils.EMPTY_INT_ARRAY;
         int var6;
         if (ConfigTreasureHunting.TREASURE_HUNTING_ENABLE_CUSTOM_ITEMS) {
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

            for(Iterator var17 = ((List)ConfigTreasureHunting.TREASURE_HUNTING_CUSTOM_ITEMS.get(player.getActiveClassId())).iterator(); var17.hasNext(); cm.add(item.getObjectId())) {
               id = (Integer)var17.next();
               item = ItemFunctions.createItem(id);
               if (item.canBeEnchanted(true)) {
                  item.setEnchantLevel(ConfigTreasureHunting.TREASURE_HUNTING_CUSTOM_ITEMS_ENCHANT);
               }

               player.getInventory().addItem(item);
               if (item.isEquipable() && !item.getTemplate().isArrow()) {
                  player.getInventory().equipItem(item);
               }
            }

            if (!cm.isEmpty()) {
               List<Integer> equipableItems = new ArrayList();
               ItemInstance[] var19 = player.getInventory().getItems();
               int var21 = var19.length;

               for(int var9 = 0; var9 < var21; ++var9) {
                  ItemInstance item = var19[var9];
                  if (item.isEquipable()) {
                     equipableItems.add(item.getItemId());
                  }
               }

               equipableIds = ArrayUtils.toPrimitive((Integer[])equipableItems.toArray(new Integer[equipableItems.size()]));
               _destroy.put(player.getObjectId(), cm);
            }
         }

         if (ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_ITEMS.length > 0 || equipableIds.length > 0) {
            ItemInstance[] var11 = player.getInventory().getItems();
            int var15 = var11.length;

            for(var6 = 0; var6 < var15; ++var6) {
               ItemInstance item = var11[var6];
               if (item != null && item.isEquipped() && ArrayUtils.contains(ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_ITEMS, item.getItemId())) {
                  player.getInventory().unEquipItem(item);
               }
            }

            player.getInventory().lockItems(LockType.INCLUDE, ArrayUtils.addAll(ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_ITEMS, equipableIds));
         }

         if (ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_SKILLS.length > 0) {
            Iterator var12 = player.getAllSkills().iterator();

            while(var12.hasNext()) {
               Skill skill = (Skill)var12.next();
               if (ArrayUtils.contains(ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_SKILLS, skill.getId())) {
                  player.addUnActiveSkill(skill);
               }
            }
         }

         if (ConfigTreasureHunting.TREASURE_HUNTING_DISABLE_HERO_SKILLS) {
            EventUtils.unActivateHeroSkills(player);
         }

         if (ConfigTreasureHunting.TREASURE_HUNTING_REMOVE_KEYS) {
            long countKeys = player.getInventory().getCountOf(ConfigTreasureHunting.TREASURE_HUNTING_CHEST_KEY_ID);
            if (countKeys > 0L) {
               Functions.removeItem(player, ConfigTreasureHunting.TREASURE_HUNTING_CHEST_KEY_ID, countKeys);
            }
         }

         EventUtils.stopEffects(player, removeEffects);
         player.getEffectList().stopEffects(EffectType.Paralyze);
         player.getEffectList().stopEffects(EffectType.Petrification);
         sk.getEffects(player, player, false, false);
         if (player.getPet() != null) {
            sk.getEffects(player, player.getPet(), false, false);
         }
      }
   }

   public static void upParalyzePlayers() {
      Iterator var0 = EventUtils.getPlayers(players_list1, players_list2).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         if (player != null) {
            player.getEffectList().stopEffect(4515);
            if (player.getPet() != null) {
               player.getPet().getEffectList().stopEffect(4515);
            }
         }
      }

   }

   private static void ressurectPlayers() {
      Iterator var0 = EventUtils.getPlayers(players_list1, players_list2).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         EventUtils.healPlayer(player);
         skillsOn(player);
         itemsOn(player);
      }

   }

   private static void skillsOn(Player player) {
      if (player != null) {
         if (ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_SKILLS.length > 0) {
            Iterator var1 = player.getAllSkills().iterator();

            while(var1.hasNext()) {
               Skill skill = (Skill)var1.next();
               if (ArrayUtils.contains(ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_SKILLS, skill.getId())) {
                  player.removeUnActiveSkill(skill);
               }
            }
         }

         if (ConfigTreasureHunting.TREASURE_HUNTING_DISABLE_HERO_SKILLS) {
            EventUtils.activateHeroSkills(player);
         }

      }
   }

   private static void itemsOn(Player player) {
      if (player != null) {
         if (ConfigTreasureHunting.TREASURE_HUNTING_RESTRICTED_ITEMS.length > 0 || ConfigTreasureHunting.TREASURE_HUNTING_ENABLE_CUSTOM_ITEMS) {
            player.getInventory().unlock();
         }

      }
   }

   private static void cleanPlayers() {
      Iterator var0 = EventUtils.getPlayers(players_list1).iterator();

      Player player;
      while(var0.hasNext()) {
         player = (Player)var0.next();
         if (player != null) {
            if (!checkPlayer(player, false)) {
               removePlayer(player);
            } else {
               player.setTeam(TeamType.BLUE);
            }
         }
      }

      var0 = EventUtils.getPlayers(players_list2).iterator();

      while(var0.hasNext()) {
         player = (Player)var0.next();
         if (player != null) {
            if (!checkPlayer(player, false)) {
               removePlayer(player);
            } else {
               player.setTeam(TeamType.RED);
            }
         }
      }

   }

   private static void checkLive() {
      List<Long> new_live_list1 = new CopyOnWriteArrayList();
      List<Long> new_live_list2 = new CopyOnWriteArrayList();
      Iterator var2 = live_list1.iterator();

      Long stId;
      Player player;
      while(var2.hasNext()) {
         stId = (Long)var2.next();
         player = GameObjectsStorage.getAsPlayer(stId);
         if (player != null) {
            new_live_list1.add(player.getStoredId());
         }
      }

      var2 = live_list2.iterator();

      while(var2.hasNext()) {
         stId = (Long)var2.next();
         player = GameObjectsStorage.getAsPlayer(stId);
         if (player != null) {
            new_live_list2.add(player.getStoredId());
         }
      }

      live_list1 = new_live_list1;
      live_list2 = new_live_list2;
      if (live_list1.size() < 1 || live_list2.size() < 1) {
         endBattle();
      }

   }

   private static void removeAura() {
      Iterator var0 = EventUtils.getPlayers(live_list1, live_list2).iterator();

      while(var0.hasNext()) {
         Player player = (Player)var0.next();
         if (player != null) {
            resetEventTitle(player);
            player.removeListener(_listeners);
            buffsItems(player);
            player.setTeam(TeamType.NONE);
            player.setInTreasureHunting(false);
            player.setResurectProhibited(false);
            if (ConfigTreasureHunting.TREASURE_HUNTING_REMOVE_KEYS) {
               long countKeys = player.getInventory().getCountOf(ConfigTreasureHunting.TREASURE_HUNTING_CHEST_KEY_ID);
               if (countKeys > 0L) {
                  Functions.removeItem(player, ConfigTreasureHunting.TREASURE_HUNTING_CHEST_KEY_ID, countKeys);
               }
            }
         }
      }

   }

   private static void clearArena() {
      Location ClearLoc = Location.parseLoc(ConfigTreasureHunting.TREASURE_HUNTING_CLEAR_LOC);
      Creature[] var1 = _zone.getObjects();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GameObject obj = var1[var3];
         if (obj != null) {
            Player player = obj.getPlayer();
            if (player != null && !live_list1.contains(player.getStoredId()) && !live_list2.contains(player.getStoredId()) && !player.isGM() && player.getReflection() == getReflection()) {
               player.teleToLocation(ClearLoc, ReflectionManager.DEFAULT);
            }
         }
      }

   }

   private static Reflection getReflection() {
      return _reflection != null ? _reflection : ReflectionManager.DEFAULT;
   }

   private static void backPlayer(Player player) {
      boolean useBC = ConfigTreasureHunting.TREASURE_HUNTING_RETURN_POINT.length < 3;
      if (useBC) {
         try {
            String var = player.getVar("TreasureHunting_backCoords");
            if (var == null) {
               return;
            }

            if (var.equals("")) {
               player.unsetVar("TreasureHunting_backCoords");
               return;
            }

            String[] coords = var.split(" ");
            if (coords.length < 3) {
               return;
            }

            player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), ReflectionManager.DEFAULT);
            player.unsetVar("TreasureHunting_backCoords");
         } catch (Exception var4) {
            _log.error("TreasureHunting: on backPlayer", var4);
         }
      } else {
         player.teleToLocation(Location.findAroundPosition(ConfigTreasureHunting.TREASURE_HUNTING_RETURN_POINT[0], ConfigTreasureHunting.TREASURE_HUNTING_RETURN_POINT[1], ConfigTreasureHunting.TREASURE_HUNTING_RETURN_POINT[2], 0, 150, 0), ReflectionManager.DEFAULT);
      }

   }

   private static void unsetLastCoords() {
      boolean useBC = ConfigTreasureHunting.TREASURE_HUNTING_RETURN_POINT.length < 3;
      if (useBC) {
         mysql.set("DELETE FROM `character_variables` WHERE `name`='TreasureHunting_backCoords'");
      }

   }

   private static void removePlayer(Player player) {
      if (player != null) {
         player.removeListener(_listeners);
         live_list1.remove(player.getStoredId());
         live_list2.remove(player.getStoredId());
         players_list1.remove(player.getStoredId());
         players_list2.remove(player.getStoredId());
         resetEventTitle(player);
         buffsItems(player);
         player.setTeam(TeamType.NONE);
         player.setInTreasureHunting(false);
         player.setResurectProhibited(false);
         skillsOn(player);
         itemsOn(player);
         if (ConfigTreasureHunting.TREASURE_HUNTING_REMOVE_KEYS) {
            long countKeys = player.getInventory().getCountOf(ConfigTreasureHunting.TREASURE_HUNTING_CHEST_KEY_ID);
            if (countKeys > 0L) {
               Functions.removeItem(player, ConfigTreasureHunting.TREASURE_HUNTING_CHEST_KEY_ID, countKeys);
            }
         }
      }

   }

   private static void buffsItems(Player player) {
      try {
         List items;
         Iterator var2;
         if (ConfigTreasureHunting.TREASURE_HUNTING_ENABLE_CUSTOM_ITEMS) {
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
         items = (List)_saveBuffs.remove(player.getObjectId());
         if (items != null) {
            var2 = items.iterator();

            while(var2.hasNext()) {
               Effect e = (Effect)var2.next();
               player.getEffectList().addEffect(e);
            }
         }
      } catch (Exception var5) {
         _log.error("TreasureHunting: failed restore buffs and items", var5);
      }

   }

   private static void loadCustomItems() {
      if (ConfigTreasureHunting.TREASURE_HUNTING_ENABLE_CUSTOM_ITEMS) {
         try {
            File file = new File(Config.DATAPACK_ROOT, "config/events/treasure_hunting_items.xml");
            if (!file.exists()) {
               _log.error("not found config/events/treasure_hunting_items.xml !!!");
               return;
            }

            ConfigTreasureHunting.TREASURE_HUNTING_CUSTOM_ITEMS = new ConcurrentHashMap(ClassId.values().length);
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
                           ConfigTreasureHunting.TREASURE_HUNTING_CUSTOM_ITEMS.put(classId, is);
                        }
                     }
                  }
               }
            }
         } catch (Exception var14) {
            _log.error("RoomOfPower: error load treasure_hunting_items.xml", var14);
         }
      }

   }

   public static void preLoad() {
      if (_active) {
         if (noStart(false)) {
            _date.add(ConfigTreasureHunting.TREASURE_HUNTING_ALLOW_CALENDAR_DAY ? 2 : 5, 1);
            noStart(true);
         }

      }
   }

   private static void printInfo() {
      StringBuilder sb = new StringBuilder();
      Calendar date = Calendar.getInstance();
      date.set(13, 5);
      int day = ConfigTreasureHunting.TREASURE_HUNTING_ALLOW_CALENDAR_DAY ? 4 : 3;

      for(int i = 0; i < ConfigTreasureHunting.TREASURE_HUNTING_START_TIME.length; i += day) {
         if (!sb.toString().isEmpty()) {
            sb.append(";");
         }

         if (ConfigTreasureHunting.TREASURE_HUNTING_ALLOW_CALENDAR_DAY) {
            date.set(5, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i]);
            date.set(11, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i + 1]);
            date.set(12, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i + 2]);
         } else {
            date.set(11, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i]);
            date.set(12, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i + 1]);
         }

         sb.append(TimeUtils.toSimpleFormat(date.getTimeInMillis()));
      }

      _log.info("TreasureHunting: competitions schedule at [" + sb.toString() + "]");
   }

   private static boolean noStart(boolean msg) {
      _date = Calendar.getInstance();
      _date.set(13, 5);
      int day = ConfigTreasureHunting.TREASURE_HUNTING_ALLOW_CALENDAR_DAY ? 4 : 3;

      for(int i = 0; i < ConfigTreasureHunting.TREASURE_HUNTING_START_TIME.length; i += day) {
         if (ConfigTreasureHunting.TREASURE_HUNTING_ALLOW_CALENDAR_DAY) {
            _date.set(5, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i]);
            _date.set(11, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i + 1]);
            _date.set(12, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i + 2]);
         } else {
            _date.set(11, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i]);
            _date.set(12, ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i + 1]);
         }

         if (_date.getTimeInMillis() > System.currentTimeMillis() + 2000L) {
            _pre_category = ConfigTreasureHunting.TREASURE_HUNTING_START_TIME[i + (ConfigTreasureHunting.TREASURE_HUNTING_ALLOW_CALENDAR_DAY ? 3 : 2)];

            try {
               if (_startTask != null) {
                  _startTask.cancel(false);
               }
            } catch (Exception var4) {
            }

            _startTask = executeTask("events.TreasureHunting.TreasureHunting", "preStartTask", new Object[0], (long)((int)(_date.getTimeInMillis() - System.currentTimeMillis())));
            return false;
         }
      }

      if (msg) {
         _log.warn("TreasureHuntingStartTime config did not find battles for this day, or it is incorrect.");
      }

      return true;
   }

   public static void preStartTask() {
      if (_active) {
         startOk(new String[]{String.valueOf(_pre_category)});
      }

   }

   public static void saveTitle(Player player) {
      if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_KILLS) {
         player.setVar("treasure_hunter_title", player.getTitle() != null ? player.getTitle() : "", -1L);
         updateTitle(player, 0);
      } else if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_OPEN_CHEST) {
         player.setVar("treasure_hunter_title", player.getTitle() != null ? player.getTitle() : "", -1L);
         updateTitle(player, 0);
      }

   }

   public static void updateTitle(Player player, int kills) {
      if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_KILLS) {
         player.setTransformationTitle(String.format("Kills: %d", kills));
         player.setTitle(player.getTransformationTitle());
         player.broadcastPacket(new L2GameServerPacket[]{new NickNameChanged(player)});
      } else if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_OPEN_CHEST) {
         player.setTransformationTitle(String.format("Chests: %d", kills));
         player.setTitle(player.getTransformationTitle());
         player.broadcastPacket(new L2GameServerPacket[]{new NickNameChanged(player)});
      }

   }

   public static void resetEventTitle(Player player) {
      String title;
      if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_KILLS) {
         title = player.getVar("treasure_hunter_title");
         if (title != null) {
            player.setTitle(title);
            player.unsetVar("treasure_hunter_title");
         }

         player.sendUserInfo(true);
      } else if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_OPEN_CHEST) {
         title = player.getVar("treasure_hunter_title");
         if (title != null) {
            player.setTitle(title);
            player.unsetVar("treasure_hunter_title");
         }

         player.sendUserInfo(true);
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
      if (ConfigTreasureHunting.TREASURE_HUNTING_REG_MANAGER_ID > 0) {
         htm = HtmCache.getInstance().getNotNull("scripts/events/TreasureHunting/" + ConfigTreasureHunting.TREASURE_HUNTING_REG_MANAGER_ID + ".htm", activeChar);
         htm = statusPageReplace(activeChar, htm);
         show(htm, activeChar, (NpcInstance)null, new Object[0]);
      } else {
         htm = HtmCache.getInstance().getNotNull("scripts/events/TreasureHunting/50020.htm", activeChar);
         htm = statusPageReplace(activeChar, htm);
         show(htm, activeChar, (NpcInstance)null, new Object[0]);
      }

   }

   private static String statusPageReplace(Player activeChar, String htm) {
      boolean activeEvent = _active;
      boolean regActive = _isRegistrationActive;
      htm = htm.replace("%active%", getActiveStatus(activeChar, activeEvent, regActive));
      if (_isRegistrationActive) {
         htm = htm.replace("%reg_count%", (new CustomMessage("scripts.events.TreasureHunting.registeredPlayers", activeChar, new Object[0])).addNumber((long)(players_list1.size() + players_list2.size())).toString());
         htm = htm.replace("%reg_time%", (new CustomMessage("scripts.events.TreasureHunting.registerTime", activeChar, new Object[0])).addNumber((long)(players_list1.size() + players_list2.size())).toString());
      } else {
         htm = htm.replace("%reg_count%", "");
         htm = htm.replace("%reg_time%", "");
      }

      return htm;
   }

   private static String getActiveStatus(Player player, boolean activeEvent, boolean regActive) {
      if (regActive) {
         return StringHolder.getInstance().getNotNull(player, "scripts.events.TreasureHunting.regActive");
      } else {
         return activeEvent ? StringHolder.getInstance().getNotNull(player, "scripts.events.TreasureHunting.activeEvent") : StringHolder.getInstance().getNotNull(player, "scripts.events.TreasureHunting.notActiveEvent");
      }
   }

   public void watch() {
      Player player = this.getSelf();
      if (player != null) {
         if (!_active) {
            if (player.isLangRus()) {
               player.sendMessage("Ивент не активен.");
            } else {
               player.sendMessage("Event not active.");
            }

         } else if (_isRegistrationActive) {
            if (player.isLangRus()) {
               player.sendMessage("В настоящее время проходит регистрация.");
            } else {
               player.sendMessage("Registration is currently in progress.");
            }

         } else if (!ConfigTreasureHunting.TREASURE_HUNTING_ALLOW_OBSERVER) {
            if (player.isLangRus()) {
               player.sendMessage("Наблюдение за ивентом отключено.");
            } else {
               player.sendMessage("Battle observation is disabled.");
            }

         } else if (_status == 0) {
            if (player.isLangRus()) {
               player.sendMessage("Ивент еще не начался.");
            } else {
               player.sendMessage("The event has not started yet.");
            }

         } else if (!player.isInZonePeace()) {
            if (player.isLangRus()) {
               player.sendMessage("Наблюдение доступно только из мирной зоны.");
            } else {
               player.sendMessage("Observation is only available from a peaceful zone.");
            }

         } else if (!player.isOlyParticipant() && !ParticipantPool.getInstance().isRegistred(player)) {
            if (!players_list1.contains(player.getStoredId()) && !players_list2.contains(player.getStoredId())) {
               if (player.getTeam() != TeamType.NONE) {
                  if (player.isLangRus()) {
                     player.sendMessage("Вы являетесь участником другого ивента!");
                  } else {
                     player.sendMessage("You are a member of another event!");
                  }

               } else if (!player.isInObserverMode() && !player.isOlyObserver()) {
                  if (player.isTeleporting()) {
                     if (player.isLangRus()) {
                        player.sendMessage("Недоступно во время телепортации!");
                     } else {
                        player.sendMessage("Unavailable while teleporting!");
                     }

                  } else if (!player.isLogoutStarted()) {
                     if (_reflection == null) {
                        if (player.isLangRus()) {
                           player.sendMessage("Рефлекшин не установлен.");
                        } else {
                           player.sendMessage("Reflection is not installed.");
                        }

                     } else {
                        Location observerLoc = Location.parseLoc((String)Rnd.get(ConfigTreasureHunting.TREASURE_HUNTING_OBSERVER_COORDS));
                        boolean result = player.enterObserverMode(observerLoc);
                        if (result) {
                           player.setReflection(_reflection);
                           player.setVar("onTHObservationEnd", "true", -1L);
                           addSpec(player.getObjectId(), _reflection);
                           if (ConfigTreasureHunting.TREASURE_HUNTING_OBSERVER_COORDS.size() > 1) {
                              this.watchListPage(player);
                           }

                        }
                     }
                  }
               } else {
                  if (player.isLangRus()) {
                     player.sendMessage("Вы находитесь в режиме зрителя.");
                  } else {
                     player.sendMessage("You are in spectator mode.");
                  }

               }
            } else {
               player.sendPacket(SystemMsg.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
            }
         } else {
            player.sendPacket(SystemMsg.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
         }
      }
   }

   public void switchWatch(String[] args) {
      Player player = this.getSelf();
      if (player != null) {
         int x = Integer.parseInt(args[0]);
         int y = Integer.parseInt(args[1]);
         int z = Integer.parseInt(args[2]);
         if (!_active) {
            if (player.isLangRus()) {
               player.sendMessage("Ивент не активен.");
            } else {
               player.sendMessage("Event not active.");
            }

         } else if (_isRegistrationActive) {
            if (player.isLangRus()) {
               player.sendMessage("В настоящее время проходит регистрация.");
            } else {
               player.sendMessage("Registration is currently in progress.");
            }

         } else if (!ConfigTreasureHunting.TREASURE_HUNTING_ALLOW_OBSERVER) {
            if (player.isLangRus()) {
               player.sendMessage("Наблюдение за ивентом отключено.");
            } else {
               player.sendMessage("Battle observation is disabled.");
            }

         } else if (_status == 0) {
            if (player.isLangRus()) {
               player.sendMessage("Ивент еще не начался.");
            } else {
               player.sendMessage("The event has not started yet.");
            }

         } else if (!player.isOlyParticipant() && !ParticipantPool.getInstance().isRegistred(player)) {
            if (!players_list1.contains(player.getStoredId()) && !players_list2.contains(player.getStoredId())) {
               if (player.getTeam() != TeamType.NONE) {
                  if (player.isLangRus()) {
                     player.sendMessage("Вы являетесь участником другого ивента!");
                  } else {
                     player.sendMessage("You are a member of another event!");
                  }

               } else if (!player.isInObserverMode()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Вы не находитесь в режиме зрителя, чтобы переключить.");
                  } else {
                     player.sendMessage("You are not in spectator mode to switch.");
                  }

               } else if (player.isTeleporting()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Недоступно во время телепортации!");
                  } else {
                     player.sendMessage("Unavailable while teleporting!");
                  }

               } else if (!player.isLogoutStarted()) {
                  if (_reflection == null) {
                     if (player.isLangRus()) {
                        player.sendMessage("Рефлекшин не установлен.");
                     } else {
                        player.sendMessage("Reflection is not installed.");
                     }

                  } else {
                     Location observerLoc = new Location(x, y, z);
                     WorldRegion currentRegion = player.getCurrentRegion();
                     WorldRegion observerRegion = World.getRegion(observerLoc);
                     if (observerRegion != null) {
                        World.removeObjectsFromPlayer(player);
                        player.setObserverRegion(observerRegion);
                        player.sendPacket(new ObserverStart(observerLoc));
                        World.showObjectsToPlayer(player);
                        player.broadcastCharInfo();
                        if (ConfigTreasureHunting.TREASURE_HUNTING_OBSERVER_COORDS.size() > 1) {
                           this.watchListPage(player);
                        }

                     }
                  }
               }
            } else {
               player.sendPacket(SystemMsg.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
            }
         } else {
            player.sendPacket(SystemMsg.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
         }
      }
   }

   private void watchListPage(Player activeChar) {
      if (ConfigTreasureHunting.TREASURE_HUNTING_ALLOW_OBSERVER) {
         String htm = HtmCache.getInstance().getNotNull("scripts/events/TreasureHunting/watch_list.htm", activeChar);
         String htmButton = HtmCache.getInstance().getNotNull("scripts/events/TreasureHunting/watch_button.htm", activeChar);
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < ConfigTreasureHunting.TREASURE_HUNTING_OBSERVER_COORDS.size(); ++i) {
            int index = i + 1;
            Location loc = Location.parseLoc((String)ConfigTreasureHunting.TREASURE_HUNTING_OBSERVER_COORDS.get(i));
            String temp = htmButton.replace("%index%", String.valueOf(index));
            temp = temp.replace("%locx%", String.valueOf(loc.getX()));
            temp = temp.replace("%locy%", String.valueOf(loc.getY()));
            temp = temp.replace("%locz%", String.valueOf(loc.getZ()));
            sb.append(temp);
         }

         htm = htm.replace("%list%", sb.toString());
         this.show(htm, activeChar);
      }
   }

   public static void addSpec(int objectId, Reflection instanceId) {
      spectators.put(objectId, instanceId);
   }

   public static void removeSpec(int objectId) {
      if (spectators.containsKey(objectId)) {
         spectators.remove(objectId);
      }

   }

   public static Map<Integer, Reflection> getSpectators() {
      return spectators;
   }

   private static void status(Player activeChar) {
      if (_status == 2) {
         String scoreHtm = HtmCache.getInstance().getNotNull("scripts/events/TreasureHunting/status.htm", activeChar);
         String scoreInfoHtm = HtmCache.getInstance().getNotNull("scripts/events/TreasureHunting/score_info.htm", activeChar);
         Map<Integer, AtomicInteger> combinedMap = combineMaps(_scoreList1, _scoreList2);
         List<Entry<Integer, AtomicInteger>> entryList = new ArrayList(combinedMap.entrySet());
         sortByValueDescending(entryList);
         StringBuilder sb = new StringBuilder();

         int myOpens;
         for(myOpens = 0; myOpens < Math.min(20, entryList.size()); ++myOpens) {
            Entry<Integer, AtomicInteger> entry = (Entry)entryList.get(myOpens);
            int place = myOpens + 1;
            int playerId = (Integer)entry.getKey();
            int score = ((AtomicInteger)entry.getValue()).get();
            int kills = 0;
            if (_killList1.containsKey(playerId)) {
               kills = ((AtomicInteger)_killList1.get(playerId)).get();
            } else if (_killList2.containsKey(playerId)) {
               kills = ((AtomicInteger)_killList2.get(playerId)).get();
            }

            Player player = GameObjectsStorage.getPlayer(playerId);
            String playerName;
            if (player != null) {
               playerName = player.getName();
            } else {
               playerName = CharacterDAO.getInstance().getNameByObjectId(playerId);
            }

            String temp = scoreInfoHtm.replace("%player_place%", String.valueOf(place));
            temp = temp.replace("%player_name%", playerName);
            temp = temp.replace("%player_opens%", String.valueOf(score));
            temp = temp.replace("%player_kills%", String.valueOf(kills));
            sb.append(temp);
         }

         scoreHtm = scoreHtm.replace("%score_list%", sb.toString());
         myOpens = 0;
         if (_scoreList1.containsKey(activeChar.getObjectId())) {
            myOpens = ((AtomicInteger)_scoreList1.get(activeChar.getObjectId())).get();
         } else if (_scoreList2.containsKey(activeChar.getObjectId())) {
            myOpens = ((AtomicInteger)_scoreList2.get(activeChar.getObjectId())).get();
         }

         scoreHtm = scoreHtm.replace("%my_score%", String.valueOf(myOpens));
         int myKills = 0;
         if (_killList1.containsKey(activeChar.getObjectId())) {
            myKills = ((AtomicInteger)_killList1.get(activeChar.getObjectId())).get();
         } else if (_killList2.containsKey(activeChar.getObjectId())) {
            myKills = ((AtomicInteger)_killList2.get(activeChar.getObjectId())).get();
         }

         scoreHtm = scoreHtm.replace("%my_kills%", String.valueOf(myKills));
         scoreHtm = scoreHtm.replace("%blue_score%", String.valueOf(count1));
         scoreHtm = scoreHtm.replace("%red_score%", String.valueOf(count2));
         show(scoreHtm, activeChar, (NpcInstance)null, new Object[0]);
      }
   }

   private static void sortByValueDescending(List<Entry<Integer, AtomicInteger>> list) {
      Collections.sort(list, new Comparator<Entry<Integer, AtomicInteger>>() {
         public int compare(Entry<Integer, AtomicInteger> o1, Entry<Integer, AtomicInteger> o2) {
            return ((AtomicInteger)o2.getValue()).get() - ((AtomicInteger)o1.getValue()).get();
         }
      });
   }

   private static Map<Integer, AtomicInteger> combineMaps(Map<Integer, AtomicInteger> map1, Map<Integer, AtomicInteger> map2) {
      Map<Integer, AtomicInteger> combinedMap = new HashMap(map1);
      Iterator var3 = map2.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<Integer, AtomicInteger> entry = (Entry)var3.next();
         combinedMap.merge(entry.getKey(), entry.getValue(), (v1, v2) -> {
            return new AtomicInteger(v1.get() + v2.get());
         });
      }

      return combinedMap;
   }

   public class VoicedCommand implements IVoicedCommandHandler {
      private String[] _commandList;

      public VoicedCommand() {
         this._commandList = new String[]{ConfigTreasureHunting.TREASURE_HUNTING_VOICE_COMMAND};
      }

      public boolean useVoicedCommand(String command, Player activeChar, String target) {
         if (!ConfigTreasureHunting.TREASURE_HUNTING_ENABLE_COMMAND) {
            return false;
         } else if (command.equalsIgnoreCase(this._commandList[0])) {
            TreasureHunting.openPage(activeChar);
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return this._commandList;
      }
   }

   private static class TeleportRes implements Runnable {
      private final long playerStoreId;

      public TeleportRes(long id) {
         this.playerStoreId = id;
      }

      public void run() {
         Player player = GameObjectsStorage.getAsPlayer(this.playerStoreId);
         if (player != null && TreasureHunting._status == 2 && player.getTeam() != TeamType.NONE) {
            if (player.isDead()) {
               player.doRevive(100.0D);
               player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
               player.setCurrentCp((double)player.getMaxCp());
            }

            List<Location> teamLoc = player.getTeam() == TeamType.BLUE ? ConfigTreasureHunting.TREASURE_HUNTING_BLUE_TEAM_RES_LOCS : ConfigTreasureHunting.TREASURE_HUNTING_RED_TEAM_RES_LOCS;
            player.teleToLocation(Location.findAroundPosition((Location)Rnd.get(teamLoc), 100, TreasureHunting.getReflection().getGeoIndex()), TreasureHunting.getReflection());
            int n;
            int i;
            if (player.isMageClass()) {
               if (ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_MAGE.length > 1) {
                  n = 0;

                  for(i = 0; i < ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_MAGE.length; i += 2) {
                     EventUtils.giveBuff(player, SkillTable.getInstance().getInfo(ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_MAGE[i], ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_MAGE[i + 1]), ConfigTreasureHunting.TREASURE_HUNTING_ALT_BUFFS_DURATION, n++);
                  }
               }
            } else if (ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_FIGHTER.length > 1) {
               n = 0;

               for(i = 0; i < ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_FIGHTER.length; i += 2) {
                  EventUtils.giveBuff(player, SkillTable.getInstance().getInfo(ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_FIGHTER[i], ConfigTreasureHunting.TREASURE_HUNTING_BUFFS_FIGHTER[i + 1]), ConfigTreasureHunting.TREASURE_HUNTING_ALT_BUFFS_DURATION, n++);
               }
            }
         }

      }
   }

   public static class TeleportTask implements Runnable {
      Location loc;
      Creature target;

      public TeleportTask(Creature target, Location loc) {
         this.target = target;
         this.loc = loc;
         target.startStunning();
      }

      public void run() {
         this.target.stopStunning();
         this.target.teleToLocation(this.loc, TreasureHunting.getReflection());
      }
   }

   private static class ZoneListener implements OnZoneEnterLeaveListener {
      private ZoneListener() {
      }

      public void onZoneEnter(Zone zone, Creature object) {
         if (object != null) {
            Player player = object.getPlayer();
            if (TreasureHunting._status > 0 && player != null && !TreasureHunting.live_list1.contains(player.getStoredId()) && !TreasureHunting.live_list2.contains(player.getStoredId()) && !player.isGM() && player.getReflection() == TreasureHunting.getReflection() && zone == TreasureHunting._zone) {
               Location ClearLoc = Location.parseLoc(ConfigTreasureHunting.TREASURE_HUNTING_CLEAR_LOC);
               ThreadPoolManager.getInstance().schedule(new TreasureHunting.TeleportTask(object, ClearLoc), 3000L);
            }

         }
      }

      public void onZoneLeave(Zone zone, Creature object) {
         if (object != null) {
            Player player = object.getPlayer();
            if (TreasureHunting._status == 2 && player != null && player.getTeam() != TeamType.NONE && (TreasureHunting.live_list1.contains(player.getStoredId()) || TreasureHunting.live_list2.contains(player.getStoredId())) && player.getReflection() == TreasureHunting.getReflection() && !TreasureHunting._zone.checkIfInZone(player.getX(), player.getY(), player.getZ(), TreasureHunting.getReflection())) {
               double angle = PositionUtils.convertHeadingToDegree(object.getHeading());
               double radian = Math.toRadians(angle - 90.0D);
               int x = (int)((double)object.getX() + 50.0D * Math.sin(radian));
               int y = (int)((double)object.getY() - 50.0D * Math.cos(radian));
               int z = object.getZ();
               ThreadPoolManager.getInstance().schedule(new TreasureHunting.TeleportTask(object, new Location(x, y, z)), 3000L);
            }

         }
      }

      // $FF: synthetic method
      ZoneListener(Object x0) {
         this();
      }
   }

   private static final class PlayerListenerImpl implements OnPlayerExitListener, OnDeathListener {
      private PlayerListenerImpl() {
      }

      public void onPlayerExit(Player player) {
         if (TreasureHunting._status == 0 && TreasureHunting._isRegistrationActive) {
            TreasureHunting.removePlayer(player);
         } else if (TreasureHunting._status == 1) {
            TreasureHunting.removePlayer(player);
            TreasureHunting.backPlayer(player);
         } else {
            if (TreasureHunting._status == 2 && player != null && player.getTeam() != TeamType.NONE) {
               TreasureHunting.removePlayer(player);
               TreasureHunting.checkLive();
            }

         }
      }

      public void onDeath(Creature actor, Creature killer) {
         if (TreasureHunting._status == 2 && actor != null && actor.getTeam() != TeamType.NONE) {
            Player pk = killer.getPlayer();
            if (pk != null && actor.getTeam() != pk.getTeam()) {
               int i;
               if (pk.getTeam() == TeamType.BLUE) {
                  i = EventUtils.incAndGetEventKills(pk, TreasureHunting._killList1, 1);
                  if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_KILLS) {
                     TreasureHunting.updateTitle(pk, i);
                  }
               } else {
                  i = EventUtils.incAndGetEventKills(pk, TreasureHunting._killList2, 1);
                  if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_KILLS) {
                     TreasureHunting.updateTitle(pk, i);
                  }
               }

               if (ConfigTreasureHunting.TREASURE_HUNTING_KILL_REWARD.length > 1) {
                  for(i = 0; i < ConfigTreasureHunting.TREASURE_HUNTING_KILL_REWARD.length; i += 2) {
                     int itemId = ConfigTreasureHunting.TREASURE_HUNTING_KILL_REWARD[i];
                     long itemCount = (long)Math.round((float)((ConfigTreasureHunting.TREASURE_HUNTING_MULT_REWARD_FOR_KILL_BY_LVL ? pk.getLevel() : 1) * ConfigTreasureHunting.TREASURE_HUNTING_KILL_REWARD[i + 1]));
                     Functions.addItem(pk, itemId, itemCount);
                  }
               }
            }

            if (ConfigTreasureHunting.TREASURE_HUNTING_STATUS_INFO_ON_DEATH && actor.isPlayer()) {
               Player player = actor.getPlayer();
               TreasureHunting.status(player);
            }

            ThreadPoolManager.getInstance().schedule(new TreasureHunting.TeleportRes(actor.getStoredId()), (long)ConfigTreasureHunting.TREASURE_HUNTING_RESURRECT_DELAY * 1000L);
            actor.sendMessage((new CustomMessage("scripts.events.TreasureHunting.Ressurection", (Player)actor, new Object[0])).addNumber((long)ConfigTreasureHunting.TREASURE_HUNTING_RESURRECT_DELAY));
            TreasureHunting.checkLive();
         }

      }

      // $FF: synthetic method
      PlayerListenerImpl(Object x0) {
         this();
      }
   }

   public static class NpcOnDeathListenerImpl implements OnDeathListener {
      public void onDeath(Creature actor, Creature killer) {
         if (actor.isNpc() && killer.isPlayer()) {
            Player player = killer.getPlayer();
            int currentCount;
            if (TreasureHunting.players_list1.contains(player.getStoredId())) {
               TreasureHunting.count1++;
               currentCount = EventUtils.incAndGetEventKills(player, TreasureHunting._scoreList1, 1);
               if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_KILLS) {
                  TreasureHunting.updateTitle(player, currentCount);
               }
            } else if (TreasureHunting.players_list2.contains(player.getStoredId())) {
               TreasureHunting.count2++;
               currentCount = EventUtils.incAndGetEventKills(player, TreasureHunting._scoreList2, 1);
               if (ConfigTreasureHunting.TREASURE_HUNTING_SHOW_KILLS) {
                  TreasureHunting.updateTitle(player, currentCount);
               }
            }
         }

      }
   }

   public static class BattleTimer implements Runnable {
      public void run() {
         TreasureHunting.broadCastTimer();
      }
   }
}
