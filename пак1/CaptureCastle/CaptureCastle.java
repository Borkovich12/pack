package events.CaptureCastle;

import events.EventUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.model.WorldRegion;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ConfirmDlg;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NickNameChanged;
import l2.gameserver.network.l2.s2c.ObserverStart;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import l2.gameserver.utils.TeleportUtils;
import npc.model.CaptureCastleEventInstance;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptureCastle extends Functions implements ScriptFile {
   private static final String TITLE_VAR = "pvp_castle_title";
   public static final Logger _log = LoggerFactory.getLogger(CaptureCastle.class);
   private final CaptureCastle.OnDeathListenerImpl deathListener = new CaptureCastle.OnDeathListenerImpl();
   private static ScheduledFuture<?> _startTask;
   private static List<Integer> players_list1 = new CopyOnWriteArrayList();
   private static List<String> players_hwid_list1 = new CopyOnWriteArrayList();
   private static List<String> players_ip_list1 = new CopyOnWriteArrayList();
   private static List<Integer> players_list2 = new CopyOnWriteArrayList();
   private static List<String> players_hwid_list2 = new CopyOnWriteArrayList();
   private static List<String> players_ip_list2 = new CopyOnWriteArrayList();
   private static Map<Integer, AtomicInteger> _killList1 = new ConcurrentHashMap();
   private static Map<Integer, AtomicInteger> _killList2 = new ConcurrentHashMap();
   private static NpcInstance _regNpc;
   private static CaptureCastleEventInstance redFlag = null;
   private static int[][] mage_buffs;
   private static int[][] fighter_buffs;
   private static boolean _isRegistrationActive = false;
   private static int _status = 0;
   private static int _time_to_start;
   private static int _category;
   private static int _pre_category;
   private static int _minLevel;
   private static int _maxLevel;
   private static TeamType _ownerTeam;
   private static ScheduledFuture<?> _endTask;
   private static Reflection _reflection;
   private static Zone _zone;
   private static InstantZone instantZone;
   private static CaptureCastle.ZoneListener _zoneListener;
   private static Map<Player, List<Effect>> returnBuffs;
   private static Map<Player, Location> returnLocation;
   private static Skill takeCastleSkill;
   private static String EVENT_NAME;
   private static Calendar _date;
   private static Calendar date;
   private static long fightBeginTime;
   private static ScheduledFuture<?> _timerTask;
   private static Map<Integer, Reflection> spectators;
   private static boolean _active;

   public void onLoad() {
      _log.info("=================================================================");
      _log.info("Load Capture Castle event.");
      _log.info("Telegram: MerdoxOne");
      _log.info("Skype: MerdoxOne");
      ConfigCaptureCastle.load();
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new CaptureCastle.VoicedCommand());
      CharListenerList.addGlobal(this.deathListener);
      CharListenerList.addGlobal(new CaptureCastle.OnPlayerExitListenerImpl());
      _active = ServerVariables.getString(EVENT_NAME, "off").equalsIgnoreCase("on");
      parseBuffs();
      if (_active) {
         executeTask("events.CaptureCastle.CaptureCastle", "preLoad", new Object[0], 70000L);
      }

      _date = Calendar.getInstance();
      date = (Calendar)_date.clone();
      date.set(11, 0);
      date.set(12, 0);
      date.set(13, 30);
      date.add(5, 1);
      if (ConfigCaptureCastle.CAPTURE_CASTLE_Allow_Calendar_Day == 0 && date.getTimeInMillis() > System.currentTimeMillis()) {
         executeTask("events.CaptureCastle.CaptureCastle", "addDay", new Object[0], (long)((int)(date.getTimeInMillis() - System.currentTimeMillis())));
      }

      _log.info("Loaded Event: " + EVENT_NAME + " [" + _active + "]");
      _log.info("=================================================================");
   }

   private static void parseBuffs() {
      if (ConfigCaptureCastle.CAPTURE_CASTLE_BuffPlayers && ConfigCaptureCastle.CAPTURE_CASTLE_MageBuffs.length > 0) {
         mage_buffs = new int[ConfigCaptureCastle.CAPTURE_CASTLE_MageBuffs.length][2];
      }

      if (ConfigCaptureCastle.CAPTURE_CASTLE_BuffPlayers && ConfigCaptureCastle.CAPTURE_CASTLE_FighterBuffs.length > 0) {
         fighter_buffs = new int[ConfigCaptureCastle.CAPTURE_CASTLE_FighterBuffs.length][2];
      }

      int i = 0;
      String[] var1;
      int var2;
      int var3;
      String skill;
      String[] splitSkill;
      if (ConfigCaptureCastle.CAPTURE_CASTLE_BuffPlayers && ConfigCaptureCastle.CAPTURE_CASTLE_MageBuffs.length > 0) {
         var1 = ConfigCaptureCastle.CAPTURE_CASTLE_MageBuffs;
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            skill = var1[var3];
            splitSkill = skill.split(",");
            mage_buffs[i][0] = Integer.parseInt(splitSkill[0]);
            mage_buffs[i][1] = Integer.parseInt(splitSkill[1]);
            ++i;
         }
      }

      i = 0;
      if (ConfigCaptureCastle.CAPTURE_CASTLE_BuffPlayers && ConfigCaptureCastle.CAPTURE_CASTLE_FighterBuffs.length > 0) {
         var1 = ConfigCaptureCastle.CAPTURE_CASTLE_FighterBuffs;
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            skill = var1[var3];
            splitSkill = skill.split(",");
            fighter_buffs[i][0] = Integer.parseInt(splitSkill[0]);
            fighter_buffs[i][1] = Integer.parseInt(splitSkill[1]);
            ++i;
         }
      }

   }

   public void onReload() {
   }

   public void onShutdown() {
   }

   private void teleportPlayers(List<Integer> players, TeamType team, Location spawnLoc) {
      Iterator var4 = getPlayers(players).iterator();

      while(var4.hasNext()) {
         Player player = (Player)var4.next();
         Location loc = player.getTeam() == team ? ConfigCaptureCastle.CAPTURE_CASTLE_SPAWN_LOC_OWNER_TEAM : spawnLoc;
         Location tele = Location.findPointToStay(loc, 0, 100, _reflection.getGeoIndex());
         player.teleToLocation(tele, _reflection);
         player.resetReuse();
         player.sendPacket(new SkillCoolTime(player));
         player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
         player.setCurrentCp((double)player.getMaxCp());
      }

   }

   private void teleportPlayersBeforeCasting(TeamType team) {
      this.teleportPlayers(players_list1, team, ConfigCaptureCastle.CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_BLUE_TEAM);
      this.teleportPlayers(players_list2, team, ConfigCaptureCastle.CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_RED_TEAM);
   }

   private static boolean isActive() {
      return _active;
   }

   public void activateEvent() {
      Player player = this.getSelf();
      if (player.getPlayerAccess().IsEventGm) {
         if (!isActive()) {
            executeTask("events.CaptureCastle.CaptureCastle", "preLoad", new Object[0], 10000L);
            ServerVariables.set(EVENT_NAME, "on");
            _log.info("Event " + EVENT_NAME + " activated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.CaptureCastle.AnnounceEventStarted", (String[])null);
         } else {
            player.sendMessage("Event " + EVENT_NAME + " already active.");
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

            ServerVariables.unset(EVENT_NAME);
            _log.info("Event " + EVENT_NAME + " deactivated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.CaptureCastle.AnnounceEventStoped", (String[])null);
         } else {
            player.sendMessage("Event " + EVENT_NAME + " not active.");
         }

         _active = false;
         this.show("admin/events/events.htm", player);
      }
   }

   private static boolean isRunned() {
      return _isRegistrationActive || _status > 0;
   }

   private static int getMinLevelForCategory(int category) {
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

   private static int getMaxLevelForCategory(int category) {
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
         return 0;
      }
   }

   public void start(String[] var) {
      Player player = this.getSelf();
      if (player != null && player.getPlayerAccess().IsEventGm) {
         startOk(var);
      }
   }

   private static void startOk(String[] var) {
      if (isRunned()) {
         _log.info("CaptureCastle: start task already running!");
      } else if (var.length != 1) {
         _log.info("CaptureCastle: wrong number of variables.");
      } else {
         try {
            _category = Integer.valueOf(var[0]);
         } catch (Exception var3) {
            _log.info("CaptureCastle: can't parse category");
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
            _log.info("CaptureCastle: end task is already running");
         } else {
            _status = 0;
            _isRegistrationActive = true;
            _time_to_start = ConfigCaptureCastle.CAPTURE_CASTLE_Time_TO_START;
            setOwnerTeam(TeamType.NONE);
            players_list1 = new CopyOnWriteArrayList();
            players_list2 = new CopyOnWriteArrayList();
            if (ConfigCaptureCastle.CAPTURE_CASTLE_EnableTopKiller) {
               _killList1 = new ConcurrentHashMap();
               _killList2 = new ConcurrentHashMap();
            }

            if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowHwidCheck) {
               players_hwid_list1 = new CopyOnWriteArrayList();
               players_hwid_list2 = new CopyOnWriteArrayList();
            }

            if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowIpCheck) {
               players_ip_list1 = new CopyOnWriteArrayList();
               players_ip_list2 = new CopyOnWriteArrayList();
            }

            initReflection();
            _zone.setActive(true);
            if (ConfigCaptureCastle.CAPTURE_CASTLE_DOORS.length > 0) {
               for(int i = 0; i < ConfigCaptureCastle.CAPTURE_CASTLE_DOORS.length; ++i) {
                  DoorInstance door = _reflection.getDoor(ConfigCaptureCastle.CAPTURE_CASTLE_DOORS[i]);
                  door.setIsInvul(false);
                  door.closeMe();
               }
            }

            if (redFlag != null) {
               redFlag.deleteMe();
            }

            spawnManager();
            redFlag = (CaptureCastleEventInstance)NpcUtils.spawnSingle(ConfigCaptureCastle.CAPTURE_CASTLE_FLAG_ID, ConfigCaptureCastle.CAPTURE_CASTLE_FLAG_LOC, _reflection);
            redFlag.decayMe();
            String[] param = new String[]{String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel)};
            sayToAll("scripts.events.CaptureCastle.AnnouncePreStart", param);
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.QuestionTask(), 1000L);
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.AnnounceTask(), 60000L);
            _log.info(EVENT_NAME + ": start event [" + _category + "]");
         }
      }
   }

   public static void spawnManager() {
      if (ConfigCaptureCastle.CAPTURE_CASTLE_SPAWN_REG_MANAGER) {
         try {
            Location npcLoc = Location.parseLoc(ConfigCaptureCastle.CAPTURE_CASTLE_REG_MANAGER_LOC);
            _regNpc = NpcUtils.spawnSingle(ConfigCaptureCastle.CAPTURE_CASTLE_REG_MANAGER_ID, npcLoc);
         } catch (Exception var1) {
            _log.error("CaptureCastle: fail spawn registered manager", var1);
         }
      }

   }

   private static void unSpawnManager() {
      if (ConfigCaptureCastle.CAPTURE_CASTLE_SPAWN_REG_MANAGER && _regNpc != null) {
         try {
            _regNpc.deleteMe();
            _regNpc = null;
         } catch (Exception var1) {
            _log.error("CaptureCastle: fail despawn manager", var1);
         }
      }

   }

   private static void sayToAll(String address, String[] replacements) {
      Announcements.getInstance().announceByCustomMessage(address, replacements, ChatType.CRITICAL_ANNOUNCE);
   }

   private static void sayToParticipants(String address, String[] replacements) {
      Iterator var2 = getPlayers(players_list1, players_list2).iterator();

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

                     player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "CaptureCastle", "CaptureCastle: " + cm.toString()));
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

         player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "CaptureCastle", "CaptureCastle: " + cm.toString()));
      }
   }

   private static boolean isWindowCheck(Player player) {
      if (player == null) {
         return false;
      } else if (player.isDead()) {
         return false;
      } else if (player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel) {
         if (!player.getReflection().isDefault()) {
            return false;
         } else if (!player.isOlyParticipant() && !player.isOlyObserver()) {
            if (player.isInZone(ZoneType.epic)) {
               return false;
            } else {
               return !player.isOnSiegeField();
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void eventCancel() {
      _isRegistrationActive = false;
      _status = 0;
      clearEventReg();
   }

   public void addPlayer() {
      Player player = this.getSelf();
      addPlayer(player);
   }

   public static void addPlayer(Player player) {
      if (player != null && checkPlayer(player, true)) {
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

         if (!checkCountTeam(team)) {
            show(new CustomMessage("scripts.events.CaptureCastle.MaxCountTeam", player, new Object[0]), player);
         } else {
            if (team == 1) {
               players_list1.add(player.getObjectId());
               if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowHwidCheck && player.getNetConnection() != null) {
                  players_hwid_list1.add(player.getNetConnection().getHwid());
               }

               if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowIpCheck) {
                  players_ip_list1.add(player.getIP());
               }

               show(new CustomMessage("scripts.events.CaptureCastle.Registered", player, new Object[0]), player);
            } else if (team == 2) {
               players_list2.add(player.getObjectId());
               if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowHwidCheck && player.getNetConnection() != null) {
                  players_hwid_list2.add(player.getNetConnection().getHwid());
               }

               if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowIpCheck) {
                  players_ip_list2.add(player.getIP());
               }

               show(new CustomMessage("scripts.events.CaptureCastle.Registered", player, new Object[0]), player);
            } else {
               _log.info("WTF??? Command id 0 in " + EVENT_NAME + " ...");
            }

            player.setReg(true);
            player.setIsInCaptureCastleEvent(true);
         }
      }
   }

   public void unreg() {
      Player player = this.getSelf();
      if (player != null) {
         if (!_isRegistrationActive) {
            player.sendMessage(player.isLangRus() ? "Доступно только в период регистрации." : "Available only during the registration period.");
         } else if (!players_list1.contains(player.getObjectId()) && !players_list2.contains(player.getObjectId())) {
            player.sendMessage(player.isLangRus() ? "Вы не являетесь участником " + EVENT_NAME + "." : "You are not a participant of " + EVENT_NAME + ".");
         } else {
            players_list1.remove(player.getObjectId());
            players_list2.remove(player.getObjectId());
            if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowHwidCheck && player.getNetConnection() != null) {
               players_hwid_list1.remove(player.getNetConnection().getHwid());
               players_hwid_list2.remove(player.getNetConnection().getHwid());
            }

            if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowIpCheck) {
               players_ip_list1.remove(player.getIP());
               players_ip_list2.remove(player.getIP());
            }

            if (player.isLangRus()) {
               player.sendMessage("Вы сняли регистрацию с " + EVENT_NAME + ".");
            } else {
               player.sendMessage("You have unregistered from " + EVENT_NAME + ".");
            }

            player.setReg(false);
            player.setIsInCaptureCastleEvent(false);
         }
      }
   }

   private static boolean checkCountTeam(int team) {
      if (team == 1 && players_list1.size() >= ConfigCaptureCastle.CAPTURE_CASTLE_MaxPlayerInTeam) {
         return false;
      } else {
         return team != 2 || players_list2.size() < ConfigCaptureCastle.CAPTURE_CASTLE_MaxPlayerInTeam;
      }
   }

   public static boolean checkPlayer(Player player, boolean first) {
      if (first && !_isRegistrationActive) {
         show(new CustomMessage("scripts.events.Late", player, new Object[0]), player);
         return false;
      } else if (first && (players_list1.contains(player.getObjectId()) || players_list2.contains(player.getObjectId()))) {
         show(new CustomMessage("scripts.events.CaptureCastle.Cancelled", player, new Object[0]), player);
         player.setReg(false);
         player.setIsInCaptureCastleEvent(false);
         player.setIsInCaptureCastleEventOwner(false);
         return false;
      } else if (first && ConfigCaptureCastle.CAPTURE_CASTLE_AllowHwidCheck && (player.getNetConnection() != null && players_hwid_list1.contains(player.getNetConnection().getHwid()) || player.getNetConnection() != null && players_hwid_list2.contains(player.getNetConnection().getHwid()))) {
         if (player.isLangRus()) {
            player.sendMessage("Игрок с данным железом уже зарегистрирован.");
         } else {
            player.sendMessage("A player with this HWID is already registered.");
         }

         return false;
      } else if (!first || !ConfigCaptureCastle.CAPTURE_CASTLE_AllowIpCheck || !players_ip_list1.contains(player.getIP()) && !players_ip_list2.contains(player.getIP())) {
         if (first && player.isDead()) {
            return false;
         } else if (player.isCursedWeaponEquipped()) {
            show(new CustomMessage("scripts.events.CaptureCastle.Cancelled", player, new Object[0]), player);
            return false;
         } else if (player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel) {
            if (player.isMounted()) {
               show(new CustomMessage("scripts.events.CaptureCastle.Cancelled", player, new Object[0]), player);
               return false;
            } else if (player.isInDuel()) {
               show(new CustomMessage("scripts.events.CaptureCastle.CancelledDuel", player, new Object[0]), player);
               return false;
            } else if (player.getTeam() != TeamType.NONE) {
               show(new CustomMessage("scripts.events.CaptureCastle.CancelledOtherEvent", player, new Object[0]), player);
               return false;
            } else if (player.isOlyParticipant() || first && ParticipantPool.getInstance().isRegistred(player)) {
               show(new CustomMessage("scripts.events.CaptureCastle.CancelledOlympiad", player, new Object[0]), player);
               return false;
            } else if (player.isInParty() && player.getParty().isInDimensionalRift()) {
               show(new CustomMessage("scripts.events.CaptureCastle.CancelledOtherEvent", player, new Object[0]), player);
               return false;
            } else if (player.isTeleporting()) {
               show(new CustomMessage("scripts.events.CaptureCastle.CancelledTeleport", player, new Object[0]), player);
               return false;
            } else if (player.getVar("jailed") != null) {
               player.sendMessage(player.isLangRus() ? "Вы не можете регистрироваться, Вы в тюрьме" : "You can not registered you in jail");
               return false;
            } else {
               return true;
            }
         } else {
            show(new CustomMessage("scripts.events.CaptureCastle.CancelledLevel", player, new Object[0]), player);
            return false;
         }
      } else {
         if (player.isLangRus()) {
            player.sendMessage("Игрок с данным IP уже зарегистрирован.");
         } else {
            player.sendMessage("A player with this IP is already registered.");
         }

         return false;
      }
   }

   private static void clearEventReg() {
      Iterator var0 = players_list1.iterator();

      int objId;
      Player player;
      while(var0.hasNext()) {
         objId = (Integer)var0.next();
         player = GameObjectsStorage.getPlayer(objId);
         if (player != null) {
            player.setReg(false);
            player.setIsInCaptureCastleEvent(false);
            player.setIsInCaptureCastleEventOwner(false);
         }
      }

      var0 = players_list2.iterator();

      while(var0.hasNext()) {
         objId = (Integer)var0.next();
         player = GameObjectsStorage.getPlayer(objId);
         if (player != null) {
            player.setReg(false);
            player.setIsInCaptureCastleEvent(false);
            player.setIsInCaptureCastleEventOwner(false);
         }
      }

      players_list1.clear();
      players_list2.clear();
      unSpawnManager();
   }

   public static void broadCastTimer() {
      int secondsLeft = (int)((fightBeginTime + TimeUnit.MINUTES.toMillis((long)ConfigCaptureCastle.CAPTURE_CASTLE_Time_Battle) - System.currentTimeMillis()) / 1000L);
      int minutes = secondsLeft / 60;
      int seconds = secondsLeft % 60;
      ExShowScreenMessage packet = new ExShowScreenMessage(String.format("%02d:%02d", minutes, seconds), 1010, -1, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, false);
      Iterator var4 = getPlayers(players_list1).iterator();

      Player player;
      while(var4.hasNext()) {
         player = (Player)var4.next();
         player.sendPacket(packet);
      }

      var4 = getPlayers(players_list2).iterator();

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

   private static void giveItemsToWinner(boolean team1, boolean team2, boolean draw) {
      Iterator var3 = getPlayers(players_list1, players_list2).iterator();

      Player player;
      while(var3.hasNext()) {
         player = (Player)var3.next();
         player.getListeners().onCaptureCastleEvent(false);
      }

      int[] rewards;
      int i;
      int item_id;
      long item_count;
      if (team1 && !draw) {
         var3 = getPlayers(players_list1).iterator();

         while(var3.hasNext()) {
            player = (Player)var3.next();
            rewards = ConfigCaptureCastle.CAPTURE_CASTLE_Rewards;

            for(i = 0; i < rewards.length; i += 2) {
               item_id = rewards[i];
               item_count = (long)rewards[i + 1];
               ItemFunctions.addItem(player, item_id, item_count, true);
            }

            player.getListeners().onCaptureCastleEvent(true);
         }

         if (ConfigCaptureCastle.CAPTURE_CASTLE_Rewards_Loose != null && ConfigCaptureCastle.CAPTURE_CASTLE_Rewards_Loose.length > 0) {
            var3 = getPlayers(players_list2).iterator();

            while(var3.hasNext()) {
               player = (Player)var3.next();
               rewards = ConfigCaptureCastle.CAPTURE_CASTLE_Rewards_Loose;

               for(i = 0; i < rewards.length; i += 2) {
                  item_id = rewards[i];
                  item_count = (long)rewards[i + 1];
                  ItemFunctions.addItem(player, item_id, item_count, true);
               }
            }
         }
      } else if (team2 && !draw) {
         var3 = getPlayers(players_list2).iterator();

         while(var3.hasNext()) {
            player = (Player)var3.next();
            rewards = ConfigCaptureCastle.CAPTURE_CASTLE_Rewards;

            for(i = 0; i < rewards.length; i += 2) {
               item_id = rewards[i];
               item_count = (long)rewards[i + 1];
               ItemFunctions.addItem(player, item_id, item_count, true);
            }

            player.getListeners().onCaptureCastleEvent(true);
         }

         if (ConfigCaptureCastle.CAPTURE_CASTLE_Rewards_Loose != null && ConfigCaptureCastle.CAPTURE_CASTLE_Rewards_Loose.length > 0) {
            var3 = getPlayers(players_list1).iterator();

            while(var3.hasNext()) {
               player = (Player)var3.next();
               rewards = ConfigCaptureCastle.CAPTURE_CASTLE_Rewards_Loose;

               for(i = 0; i < rewards.length; i += 2) {
                  item_id = rewards[i];
                  item_count = (long)rewards[i + 1];
                  ItemFunctions.addItem(player, item_id, item_count, true);
               }
            }
         }
      } else if (draw && ConfigCaptureCastle.CAPTURE_CASTLE_Rewards_Tie != null && ConfigCaptureCastle.CAPTURE_CASTLE_Rewards_Tie.length > 0) {
         var3 = getPlayers(players_list1, players_list2).iterator();

         while(var3.hasNext()) {
            player = (Player)var3.next();
            rewards = ConfigCaptureCastle.CAPTURE_CASTLE_Rewards_Tie;

            for(i = 0; i < rewards.length; i += 2) {
               item_id = rewards[i];
               item_count = (long)rewards[i + 1];
               ItemFunctions.addItem(player, item_id, item_count, true);
            }
         }
      }

      giveTopKillerRewards();
   }

   private static void giveTopKillerRewards() {
      if (ConfigCaptureCastle.CAPTURE_CASTLE_EnableTopKiller) {
         giveTopKillerReward(_killList1, TeamType.BLUE);
         giveTopKillerReward(_killList2, TeamType.RED);
      }

   }

   private static void giveTopKillerReward(Map<Integer, AtomicInteger> killList, TeamType teamType) {
      if (killList != null && !killList.isEmpty()) {
         List<Entry<Integer, AtomicInteger>> entryList = new ArrayList(killList.entrySet());
         entryList.sort(Comparator.comparingInt((e) -> {
            return -((AtomicInteger)e.getValue()).get();
         }));
         if (!entryList.isEmpty()) {
            sayToParticipants("scripts.events.CaptureCastle.AnnounceTopKillers", new String[]{teamType == TeamType.BLUE ? "Blue" : "Red"});
            int place = 1;

            for(Iterator var4 = entryList.iterator(); var4.hasNext(); ++place) {
               Entry<Integer, AtomicInteger> entry = (Entry)var4.next();
               int objectId = (Integer)entry.getKey();
               int count = ((AtomicInteger)entry.getValue()).get();
               if (ConfigCaptureCastle.CAPTURE_CASTLE_TopKillerReward.size() < place) {
                  break;
               }

               int[] rewards = (int[])ConfigCaptureCastle.CAPTURE_CASTLE_TopKillerReward.get(place);
               if (rewards != null) {
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

                     announceToKillersPlace(place, count, player.getName());
                  } else {
                     for(i = 0; i < rewards.length; i += 2) {
                        itemId = rewards[i];
                        itemCount = rewards[i + 1];
                        DelayedItemsManager.getInstance().addDelayed(objectId, itemId, itemCount, 0, 0, 0, "Reward top killer for castle capture place=" + place + " itemId=" + itemId + " itemCount=" + itemCount + " bought by " + player);
                     }

                     String playerName = CharacterDAO.getInstance().getNameByObjectId(objectId);
                     announceToKillersPlace(place, count, playerName);
                  }
               }
            }

            killList.clear();
            killList = null;
         }
      }
   }

   private static void announceToKillersPlace(int place, int kills, String playerName) {
      sayToParticipants("scripts.events.CaptureCastle.AnnounceTopKillerPlace", new String[]{String.valueOf(place), String.valueOf(kills), playerName});
   }

   private static void preparePlayer(Player player) {
      List<Effect> saveSkills = new ArrayList();
      if (player.getVar("jailed") != null) {
         removePlayer(player);
      }

      if (!ConfigCaptureCastle.CAPTURE_CASTLE_AllowSummons && player.getPet() != null) {
         Summon pet = player.getPet();
         if (pet.isPet()) {
            pet.unSummon();
         }

         if (player.getPet() != null) {
            player.getPet().unSummon();
         }
      }

      player.addSkill(takeCastleSkill, false);
      boolean skillUpdate = false;
      List<Effect> effects = new LinkedList();

      for(int i = 0; i < ConfigCaptureCastle.CAPTURE_CASTLE_RESTRICTED_SKILL_IDS.length; ++i) {
         Skill skill = player.getKnownSkill(ConfigCaptureCastle.CAPTURE_CASTLE_RESTRICTED_SKILL_IDS[i]);
         if (skill != null) {
            if (skill.isToggle()) {
               List<Effect> effectsBySkill = player.getEffectList().getEffectsBySkill(skill);
               if (effectsBySkill != null && !effectsBySkill.isEmpty()) {
                  effects.addAll(effectsBySkill);
               }
            }

            player.addUnActiveSkill(skill);
            skillUpdate = true;
         }
      }

      if (player.getClan() != null && !ConfigCaptureCastle.CAPTURE_CASTLE_AllowClanSkill) {
         player.getClan().disableSkills(player);
      }

      if (player.isHero() && !ConfigCaptureCastle.CAPTURE_CASTLE_AllowHeroSkill) {
         HeroController.removeSkills(player);
      }

      if (skillUpdate) {
         player.sendPacket(new SkillCoolTime(player));
         player.updateStats();
         player.updateEffectIcons();
      }

      if (!ConfigCaptureCastle.CAPTURE_CASTLE_DispelTransformation && player.isCursedWeaponEquipped()) {
         CursedWeaponsManager.getInstance().dropPlayer(player);
      }

      DuelEvent duel = (DuelEvent)player.getEvent(DuelEvent.class);
      if (duel != null) {
         duel.abortDuel(player);
      }

      if (player.getParty() != null && !ConfigCaptureCastle.CAPTURE_CASTLE_CAN_PARTY_INVITE) {
         player.getParty().removePartyMember(player, false);
      }

      if (player.isInvisible()) {
         player.setInvisibleType(InvisibleType.NONE);
      }

      if (ConfigCaptureCastle.CAPTURE_CASTLE_EnableKillsInTitle) {
         player.setVar("pvp_castle_title", player.getTitle() != null ? player.getTitle() : "", -1L);
         updateTitle(player, 0);
      }

      if (ConfigCaptureCastle.CAPTURE_CASTLE_INCLUDE_ITEMS.length > 0) {
         ItemInstance[] var11 = player.getInventory().getItems();
         int var14 = var11.length;

         for(int var7 = 0; var7 < var14; ++var7) {
            ItemInstance item = var11[var7];
            if (item != null && item.isEquipped() && ArrayUtils.contains(ConfigCaptureCastle.CAPTURE_CASTLE_INCLUDE_ITEMS, item.getItemId())) {
               player.getInventory().unEquipItem(item);
            }
         }

         int[] haveLockItems = player.getInventory().getLockItems();
         player.getInventory().lockItems(LockType.INCLUDE, ArrayUtils.addAll(ConfigCaptureCastle.CAPTURE_CASTLE_INCLUDE_ITEMS, haveLockItems));
      }

      if (!ConfigCaptureCastle.CAPTURE_CASTLE_AllowBuffs) {
         Iterator var13 = player.getEffectList().getAllEffects().iterator();

         while(var13.hasNext()) {
            Effect ef = (Effect)var13.next();
            Effect effect = ef.getTemplate().getEffect(new Env(ef.getEffector(), ef.getEffected(), ef.getSkill()));
            effect.setCount(ef.getCount());
            effect.setPeriod(ef.getCount() == 1 ? ef.getPeriod() - ef.getTime() : ef.getPeriod());
            saveSkills.add(effect);
            ef.exit();
         }

         if (player.getPet() != null) {
            player.getPet().getEffectList().stopAllEffects();
         }

         player.sendPacket(new SkillList(player));
      }

      player.setReg(false);
      player.setIsInCaptureCastleEvent(true);
      returnBuffs.put(player, saveSkills);
   }

   private static void teleportPlayerToArena(Player player, Location captureCastleFirstTeamSpawnLoc) {
      if (!ConfigCaptureCastle.CAPTURE_CASTLE_RETURN_POINT_ENABLE) {
         returnLocation.put(player, new Location(player.getX(), player.getY(), player.getZ()));
      }

      Location tele = Location.findPointToStay(captureCastleFirstTeamSpawnLoc, 0, 200, _reflection.getGeoIndex());
      player.teleToLocation(tele, _reflection);
   }

   private static void paralyzePlayer(Player player) {
      if (player != null) {
         if (!player.isRooted()) {
            player.getEffectList().stopEffect(1411);
            player.startRooted();
            player.startAbnormalEffect(AbnormalEffect.ROOT);
         }

         if (player.getPet() != null && !player.getPet().isRooted()) {
            player.getPet().startRooted();
            player.getPet().startAbnormalEffect(AbnormalEffect.ROOT);
         }

      }
   }

   private static void upParalyzePlayers() {
      Iterator var0 = getPlayers(players_list1).iterator();

      Player player;
      while(var0.hasNext()) {
         player = (Player)var0.next();
         unParalyzePlayer(player);
      }

      var0 = getPlayers(players_list2).iterator();

      while(var0.hasNext()) {
         player = (Player)var0.next();
         unParalyzePlayer(player);
      }

   }

   private static void unParalyzePlayer(Player player) {
      player.getEffectList().stopEffect(4515);
      if (player.getPet() != null) {
         player.getPet().getEffectList().stopEffect(4515);
      }

      if (player.isRooted()) {
         player.stopRooted();
         player.stopAbnormalEffect(AbnormalEffect.ROOT);
      }

      if (player.getPet() != null && player.getPet().isRooted()) {
         player.getPet().stopRooted();
         player.getPet().stopAbnormalEffect(AbnormalEffect.ROOT);
      }

      player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
      player.setCurrentCp((double)player.getMaxCp());
      player.sendChanges();
   }

   private static void resurrectPlayer(Player player) {
      if (player.isDead()) {
         player.doRevive(100.0D);
         player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
         player.setCurrentCp((double)player.getMaxCp());
      }

   }

   private static void healPlayer(Player player) {
      player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp(), false);
      player.setCurrentCp((double)player.getMaxCp());
   }

   private static void cleanPlayers() {
      Iterator var0 = getPlayers(players_list1).iterator();

      Player player;
      while(var0.hasNext()) {
         player = (Player)var0.next();
         cleanPlayer(player, TeamType.BLUE);
      }

      var0 = getPlayers(players_list2).iterator();

      while(var0.hasNext()) {
         player = (Player)var0.next();
         cleanPlayer(player, TeamType.RED);
      }

   }

   private static void cleanPlayer(Player player, TeamType teamType) {
      if (!checkPlayer(player, false)) {
         removePlayer(player);
      } else {
         player.setTeam(teamType);
         player.setIsInCaptureCastleEvent(true);
         player.setResurectProhibited(true);
      }

   }

   private static void removeAura() {
      Iterator var0 = getPlayers(players_list1).iterator();

      Player player;
      while(var0.hasNext()) {
         player = (Player)var0.next();
         removePlayerAura(player);
      }

      var0 = getPlayers(players_list2).iterator();

      while(var0.hasNext()) {
         player = (Player)var0.next();
         removePlayerAura(player);
      }

   }

   private static void removePlayerAura(Player player) {
      player.setTeam(TeamType.NONE);
      if (ConfigCaptureCastle.CAPTURE_CASTLE_EnableKillsInTitle) {
         String title = player.getVar("pvp_castle_title");
         if (title != null) {
            player.setTitle(title);
            player.unsetVar("pvp_castle_title");
         }
      }

      player.sendUserInfo(true);
      player.setReg(false);
      player.setIsInCaptureCastleEvent(false);
      player.setIsInCaptureCastleEventOwner(false);
      player.setResurectProhibited(false);
   }

   private void OnEscape(Player player) {
      if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && (players_list1.contains(player.getObjectId()) || players_list2.contains(player.getObjectId()))) {
         removePlayer(player);
      }

   }

   private static void removePlayer(Player player) {
      if (player != null) {
         players_list1.remove(player.getObjectId());
         players_list2.remove(player.getObjectId());
         player.removeSkill(takeCastleSkill, false);
         removePlayerAura(player);
         player.getEffectList().stopAllEffects();
      }

   }

   private static List<Player> getPlayers(List<Integer> list) {
      List<Player> result = new ArrayList();
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         Integer storeId = (Integer)var2.next();
         Player player = GameObjectsStorage.getPlayer(storeId);
         if (player != null) {
            result.add(player);
         }
      }

      return result;
   }

   private static List<Player> getPlayers(List<Integer> list1, List<Integer> list2) {
      List<Player> result = new ArrayList();
      Iterator var3 = list1.iterator();

      Integer storeId;
      Player player;
      while(var3.hasNext()) {
         storeId = (Integer)var3.next();
         player = GameObjectsStorage.getPlayer(storeId);
         if (player != null) {
            result.add(player);
         }
      }

      var3 = list2.iterator();

      while(var3.hasNext()) {
         storeId = (Integer)var3.next();
         player = GameObjectsStorage.getPlayer(storeId);
         if (player != null) {
            result.add(player);
         }
      }

      return result;
   }

   private static void updateTitle(Player player, int kills) {
      player.setTransformationTitle(String.format("Kills: %d", kills));
      player.setTitle(player.getTransformationTitle());
      player.broadcastPacket(new L2GameServerPacket[]{new NickNameChanged(player)});
   }

   private void backToTown(Player player) {
      Location returnLoc;
      if (ConfigCaptureCastle.CAPTURE_CASTLE_RETURN_POINT_ENABLE) {
         returnLoc = Location.findPointToStay(ConfigCaptureCastle.CAPTURE_CASTLE_RETURN_POINT, 0, 100, 0);
         if (player.isLogoutStarted()) {
            player.setReflection(ReflectionManager.DEFAULT);
            player.setXYZ(returnLoc.x, returnLoc.y, returnLoc.z);
         } else {
            player.teleToLocation(returnLoc, ReflectionManager.DEFAULT);
         }
      } else {
         returnLoc = (Location)returnLocation.get(player);
         if (returnLoc != null) {
            if (player.isLogoutStarted()) {
               player.setReflection(ReflectionManager.DEFAULT);
               player.setXYZ(returnLoc.x, returnLoc.y, returnLoc.z);
            } else {
               player.teleToLocation(returnLoc, ReflectionManager.DEFAULT);
            }
         } else if (player.isLogoutStarted()) {
            Location closeLoc = TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE);
            player.setReflection(ReflectionManager.DEFAULT);
            player.setXYZ(closeLoc.x, closeLoc.y, closeLoc.z);
         } else {
            player.teleToClosestTown();
         }
      }

   }

   private static void mageBuff(Player player) {
      int[][] var1 = mage_buffs;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int[] mage_buff = var1[var3];
         Skill buff = SkillTable.getInstance().getInfo(mage_buff[0], mage_buff[1]);
         buff.getEffects(player, player, false, false, (long)(ConfigCaptureCastle.CAPTURE_CASTLE_BUFF_TIME_MAGE * '\uea60'), 1.0D, false);
         if (player.getPet() != null) {
            buff.getEffects(player.getPet(), player.getPet(), false, false, (long)(ConfigCaptureCastle.CAPTURE_CASTLE_BUFF_TIME_MAGE * '\uea60'), 1.0D, false);
         }
      }

   }

   private static void fighterBuff(Player player) {
      int[][] var1 = fighter_buffs;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int[] fighter_buff = var1[var3];
         Skill buff = SkillTable.getInstance().getInfo(fighter_buff[0], fighter_buff[1]);
         buff.getEffects(player, player, false, false, (long)(ConfigCaptureCastle.CAPTURE_CASTLE_BUFF_TIME_FIGHTER * '\uea60'), 1.0D, false);
         if (player.getPet() != null) {
            buff.getEffects(player.getPet(), player.getPet(), false, false, (long)(ConfigCaptureCastle.CAPTURE_CASTLE_BUFF_TIME_FIGHTER * '\uea60'), 1.0D, false);
         }
      }

   }

   private static void buffPlayer(Player player) {
      if (player.isMageClass()) {
         mageBuff(player);
      } else {
         fighterBuff(player);
      }

   }

   private static void teleportPlayerFromArena(Player player) {
      player.removeSkill(takeCastleSkill, false);
      Location returnLoc;
      if (ConfigCaptureCastle.CAPTURE_CASTLE_RETURN_POINT_ENABLE) {
         returnLoc = Location.findPointToStay(ConfigCaptureCastle.CAPTURE_CASTLE_RETURN_POINT, 0, 100, 0);
         player.teleToLocation(returnLoc, ReflectionManager.DEFAULT);
      } else {
         returnLoc = (Location)returnLocation.get(player);
         if (returnLoc != null) {
            player.teleToLocation((Location)returnLocation.get(player), ReflectionManager.DEFAULT);
         } else {
            player.teleToClosestTown();
         }
      }

      for(int i = 0; i < ConfigCaptureCastle.CAPTURE_CASTLE_RESTRICTED_SKILL_IDS.length; ++i) {
         int id = ConfigCaptureCastle.CAPTURE_CASTLE_RESTRICTED_SKILL_IDS[i];
         if (player.isUnActiveSkill(id)) {
            Skill skill = player.getKnownSkill(id);
            if (skill != null) {
               player.removeUnActiveSkill(skill);
            }
         }
      }

      if (player.getClan() != null && !ConfigCaptureCastle.CAPTURE_CASTLE_AllowClanSkill) {
         player.getClan().enableSkills(player);
      }

      if (player.isHero() && !ConfigCaptureCastle.CAPTURE_CASTLE_AllowHeroSkill) {
         HeroController.addSkills(player);
      }

      if (ConfigCaptureCastle.CAPTURE_CASTLE_INCLUDE_ITEMS.length > 0) {
         player.getInventory().unlock();
      }

      player.setReg(false);
      player.setIsInCaptureCastleEvent(false);
      player.setIsInCaptureCastleEventOwner(false);
   }

   private static void initReflection() {
      _reflection = new Reflection();
      instantZone = InstantZoneHolder.getInstance().getInstantZone(ConfigCaptureCastle.CAPTURE_CASTLE_Instance);
      _reflection.init(instantZone);
      _zone = _reflection.getZone(ConfigCaptureCastle.CAPTURE_CASTLE_ZONE);
      _zone.addListener(_zoneListener);
   }

   public void onTake(int objectId, int typeIndex) {
      Player player = GameObjectsStorage.getPlayer(objectId);
      TeamType teamType = TeamType.values()[typeIndex];
      if (player != null && teamType != null && getOwnerTeam() != teamType && redFlag.getOwnerTeam() != teamType) {
         setOwnerTeam(teamType);
         redFlag.setOwnerTeam(_ownerTeam);
         if (players_list1.contains(player.getObjectId())) {
            this.setOwnerPlayers(getPlayers(players_list1), true);
            this.setOwnerPlayers(getPlayers(players_list2), false);
         } else if (players_list2.contains(player.getObjectId())) {
            this.setOwnerPlayers(getPlayers(players_list1), false);
            this.setOwnerPlayers(getPlayers(players_list2), true);
         }

         this.teleportPlayersBeforeCasting(teamType);
         if (ConfigCaptureCastle.CAPTURE_CASTLE_RESTORE_DESTROYED_DOORS && _reflection.getDoors() != null && _reflection.getDoors().size() > 0) {
            Iterator var5 = _reflection.getDoors().iterator();

            while(var5.hasNext()) {
               DoorInstance door = (DoorInstance)var5.next();
               if (door.isDead()) {
                  door.setCurrentHp((double)door.getMaxHp(), true, false);
               }
            }
         }
      }

   }

   private void setOwnerPlayers(List<Player> players, boolean owner) {
      Iterator var3 = players.iterator();

      while(var3.hasNext()) {
         Player player = (Player)var3.next();
         player.setIsInCaptureCastleEventOwner(owner);
      }

   }

   private static TeamType getOwnerTeam() {
      return _ownerTeam;
   }

   private static void setOwnerTeam(TeamType ownerTeam) {
      _ownerTeam = ownerTeam;
   }

   public static void preLoad() {
      if (_active) {
         byte day;
         if (ConfigCaptureCastle.CAPTURE_CASTLE_Allow_Calendar_Day > 0) {
            day = 4;
         } else {
            day = 3;
         }

         for(int i = 0; i < ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start.length; i += day) {
            if (ConfigCaptureCastle.CAPTURE_CASTLE_Allow_Calendar_Day == 2) {
               _date.set(5, ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i]);
               _date.set(11, ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i + 1]);
               _date.set(12, ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i + 2]);
            } else if (ConfigCaptureCastle.CAPTURE_CASTLE_Allow_Calendar_Day == 1) {
               _date.set(7, ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i]);
               _date.set(11, ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i + 1]);
               _date.set(12, ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i + 2]);
            } else {
               _date.set(11, ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i]);
               _date.set(12, ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i + 1]);
            }

            if (_date.getTimeInMillis() > System.currentTimeMillis() + 2000L) {
               if (ConfigCaptureCastle.CAPTURE_CASTLE_Allow_Calendar_Day > 0) {
                  _pre_category = ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i + 3];
               } else {
                  _pre_category = ConfigCaptureCastle.CAPTURE_CASTLE_Time_Start[i + 2];
               }

               executeTask("events.CaptureCastle.CaptureCastle", "preStartTask", new Object[0], (long)((int)(_date.getTimeInMillis() - System.currentTimeMillis())));
               break;
            }
         }

      }
   }

   public static void addDay() {
      _date.add(5, 1);
      date = Calendar.getInstance();
      date.set(11, 0);
      date.set(12, 0);
      date.set(13, 30);
      date.add(5, 1);
      if (date.getTimeInMillis() > System.currentTimeMillis()) {
         executeTask("events.CaptureCastle.CaptureCastle", "addDay", new Object[0], (long)((int)(date.getTimeInMillis() - System.currentTimeMillis())));
      }

      preLoad();
   }

   public static void preStartTask() {
      if (_active) {
         startOk(new String[]{String.valueOf(_pre_category)});
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
      if (ConfigCaptureCastle.CAPTURE_CASTLE_REG_MANAGER_ID > 0) {
         htm = HtmCache.getInstance().getNotNull("scripts/events/CaptureCastle/" + ConfigCaptureCastle.CAPTURE_CASTLE_REG_MANAGER_ID + ".htm", activeChar);
         htm = statusPageReplace(activeChar, htm);
         show(htm, activeChar, (NpcInstance)null, new Object[0]);
      } else {
         htm = HtmCache.getInstance().getNotNull("scripts/events/CaptureCastle/50020.htm", activeChar);
         htm = statusPageReplace(activeChar, htm);
         show(htm, activeChar, (NpcInstance)null, new Object[0]);
      }

   }

   private static String statusPageReplace(Player activeChar, String htm) {
      boolean activeEvent = _active;
      boolean regActive = _isRegistrationActive;
      htm = htm.replace("%active%", getActiveStatus(activeChar, activeEvent, regActive));
      if (_isRegistrationActive) {
         htm = htm.replace("%reg_count%", (new CustomMessage("scripts.events.CaptureCastle.registeredPlayers", activeChar, new Object[0])).addNumber((long)(players_list1.size() + players_list2.size())).toString());
         htm = htm.replace("%reg_time%", (new CustomMessage("scripts.events.CaptureCastle.registerTime", activeChar, new Object[0])).addNumber((long)(players_list1.size() + players_list2.size())).toString());
      } else {
         htm = htm.replace("%reg_count%", "");
         htm = htm.replace("%reg_time%", "");
      }

      return htm;
   }

   private static String getActiveStatus(Player player, boolean activeEvent, boolean regActive) {
      if (regActive) {
         return StringHolder.getInstance().getNotNull(player, "scripts.events.CaptureCastle.regActive");
      } else {
         return activeEvent ? StringHolder.getInstance().getNotNull(player, "scripts.events.CaptureCastle.activeEvent") : StringHolder.getInstance().getNotNull(player, "scripts.events.CaptureCastle.notActiveEvent");
      }
   }

   public static void stopTimerTask() {
      if (_timerTask != null) {
         _timerTask.cancel(true);
         _timerTask = null;
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

         } else if (!ConfigCaptureCastle.CAPTURE_CASTLE_ALLOW_OBSERVER) {
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
            if (!players_list1.contains(player.getObjectId()) && !players_list2.contains(player.getObjectId())) {
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
                        Location observerLoc = Location.parseLoc((String)Rnd.get(ConfigCaptureCastle.CAPTURE_CASTLE_OBSERVER_COORDS));
                        boolean result = player.enterObserverMode(observerLoc);
                        if (result) {
                           player.setReflection(_reflection);
                           player.setVar("onCCObservationEnd", "true", -1L);
                           addSpec(player.getObjectId(), _reflection);
                           if (ConfigCaptureCastle.CAPTURE_CASTLE_OBSERVER_COORDS.size() > 1) {
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

         } else if (!ConfigCaptureCastle.CAPTURE_CASTLE_ALLOW_OBSERVER) {
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
            if (!players_list1.contains(player.getObjectId()) && !players_list2.contains(player.getObjectId())) {
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
                        if (ConfigCaptureCastle.CAPTURE_CASTLE_OBSERVER_COORDS.size() > 1) {
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
      if (ConfigCaptureCastle.CAPTURE_CASTLE_ALLOW_OBSERVER) {
         String htm = HtmCache.getInstance().getNotNull("scripts/events/CaptureCastle/watch_list.htm", activeChar);
         String htmButton = HtmCache.getInstance().getNotNull("scripts/events/CaptureCastle/watch_button.htm", activeChar);
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < ConfigCaptureCastle.CAPTURE_CASTLE_OBSERVER_COORDS.size(); ++i) {
            int index = i + 1;
            Location loc = Location.parseLoc((String)ConfigCaptureCastle.CAPTURE_CASTLE_OBSERVER_COORDS.get(i));
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

   static {
      _ownerTeam = TeamType.NONE;
      _zoneListener = new CaptureCastle.ZoneListener();
      returnBuffs = new HashMap();
      returnLocation = new HashMap();
      takeCastleSkill = SkillTable.getInstance().getInfo(246, 1);
      EVENT_NAME = "CaptureCastle";
      spectators = new ConcurrentHashMap();
      _active = false;
   }

   public class VoicedCommand implements IVoicedCommandHandler {
      private String[] _commandList;

      public VoicedCommand() {
         this._commandList = new String[]{ConfigCaptureCastle.CAPTURE_CASTLE_VOICE_COMMAND};
      }

      public boolean useVoicedCommand(String command, Player activeChar, String target) {
         if (!ConfigCaptureCastle.CAPTURE_CASTLE_ENABLE_COMMAND) {
            return false;
         } else if (command.equalsIgnoreCase(this._commandList[0])) {
            CaptureCastle.openPage(activeChar);
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return this._commandList;
      }
   }

   public static class ReturnBuffsToPlayersTask extends RunnableImpl {
      public void runImpl() {
         if (CaptureCastle.returnBuffs != null && !CaptureCastle.returnBuffs.isEmpty()) {
            Iterator var1 = CaptureCastle.getPlayers(CaptureCastle.players_list1).iterator();

            while(true) {
               Player player;
               List effectList;
               Iterator var4;
               Effect e;
               do {
                  do {
                     if (!var1.hasNext()) {
                        var1 = CaptureCastle.getPlayers(CaptureCastle.players_list2).iterator();

                        while(true) {
                           do {
                              do {
                                 if (!var1.hasNext()) {
                                    return;
                                 }

                                 player = (Player)var1.next();
                              } while(CaptureCastle.returnBuffs.get(player) == null);
                           } while(((List)CaptureCastle.returnBuffs.get(player)).isEmpty());

                           effectList = (List)CaptureCastle.returnBuffs.remove(player);
                           if (effectList != null) {
                              var4 = effectList.iterator();

                              while(var4.hasNext()) {
                                 e = (Effect)var4.next();
                                 player.getEffectList().addEffect(e);
                              }
                           }

                           player.sendPacket(new SkillList(player));
                        }
                     }

                     player = (Player)var1.next();
                  } while(CaptureCastle.returnBuffs.get(player) == null);
               } while(((List)CaptureCastle.returnBuffs.get(player)).isEmpty());

               effectList = (List)CaptureCastle.returnBuffs.remove(player);
               if (effectList != null) {
                  var4 = effectList.iterator();

                  while(var4.hasNext()) {
                     e = (Effect)var4.next();
                     player.getEffectList().addEffect(e);
                  }
               }

               player.sendPacket(new SkillList(player));
            }
         }
      }
   }

   public static class ClearReflectionTask extends RunnableImpl {
      public void runImpl() {
         CaptureCastle._zone.removeListener(CaptureCastle._zoneListener);
         CaptureCastle._reflection.collapse();
      }
   }

   public static class RemoveSpectatorTask extends RunnableImpl {
      public void runImpl() {
         if (CaptureCastle.spectators != null && !CaptureCastle.spectators.isEmpty()) {
            Iterator var1 = EventUtils.getSpectators(CaptureCastle.spectators.keySet()).iterator();

            while(var1.hasNext()) {
               Player player = (Player)var1.next();
               if (player != null) {
                  player.leaveObserverMode();
                  player.unsetVar("onCCObservationEnd");
               }
            }

            CaptureCastle.spectators.clear();
         }

      }
   }

   public static class TeleportPlayersFromArenaTask extends RunnableImpl {
      public void runImpl() {
         Iterator var1 = CaptureCastle.getPlayers(CaptureCastle.players_list1).iterator();

         Player player;
         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.teleportPlayerFromArena(player);
         }

         var1 = CaptureCastle.getPlayers(CaptureCastle.players_list2).iterator();

         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.teleportPlayerFromArena(player);
         }

         CaptureCastle.players_list1.clear();
         CaptureCastle.players_list2.clear();
      }
   }

   public static class BuffPlayersTask extends RunnableImpl {
      public void runImpl() {
         Iterator var1 = CaptureCastle.getPlayers(CaptureCastle.players_list1).iterator();

         Player player;
         while(var1.hasNext()) {
            player = (Player)var1.next();
            if (player.isMageClass()) {
               CaptureCastle.mageBuff(player);
            } else {
               CaptureCastle.fighterBuff(player);
            }
         }

         var1 = CaptureCastle.getPlayers(CaptureCastle.players_list2).iterator();

         while(var1.hasNext()) {
            player = (Player)var1.next();
            if (player.isMageClass()) {
               CaptureCastle.mageBuff(player);
            } else {
               CaptureCastle.fighterBuff(player);
            }
         }

      }
   }

   private class OnPlayerExitListenerImpl implements OnPlayerExitListener {
      private OnPlayerExitListenerImpl() {
      }

      public void onPlayerExit(Player player) {
         if (player != null && player.getTeam() != TeamType.NONE) {
            if (CaptureCastle._status != 0 || !CaptureCastle._isRegistrationActive || player.getTeam() == TeamType.NONE || !CaptureCastle.players_list1.contains(player.getObjectId()) && !CaptureCastle.players_list2.contains(player.getObjectId())) {
               if (CaptureCastle._status == 0 && !CaptureCastle._isRegistrationActive && player.getTeam() != TeamType.NONE && (CaptureCastle.players_list1.contains(player.getObjectId()) || CaptureCastle.players_list2.contains(player.getObjectId()))) {
                  CaptureCastle.removePlayer(player);
                  CaptureCastle.this.backToTown(player);
               } else if (CaptureCastle._status != 1 || !CaptureCastle.players_list1.contains(player.getObjectId()) && !CaptureCastle.players_list2.contains(player.getObjectId())) {
                  CaptureCastle.this.OnEscape(player);
               } else {
                  CaptureCastle.removePlayer(player);
                  CaptureCastle.this.backToTown(player);
               }
            } else {
               CaptureCastle.removePlayer(player);
            }
         }
      }

      // $FF: synthetic method
      OnPlayerExitListenerImpl(Object x1) {
         this();
      }
   }

   private class OnDeathListenerImpl implements OnDeathListener {
      private OnDeathListenerImpl() {
      }

      public void onDeath(Creature self, Creature killer) {
         if (CaptureCastle._status > 1 && self != null && self.isPlayer() && self.getTeam() != TeamType.NONE && (CaptureCastle.players_list1.contains(self.getObjectId()) || CaptureCastle.players_list2.contains(self.getObjectId()))) {
            if (ConfigCaptureCastle.CAPTURE_CASTLE_EnableTopKiller && killer != null) {
               Player playerKiller;
               AtomicInteger count;
               int currentCount;
               if (CaptureCastle.players_list1.contains(self.getObjectId()) && CaptureCastle.players_list2.contains(killer.getObjectId())) {
                  playerKiller = (Player)killer;
                  count = (AtomicInteger)CaptureCastle._killList2.get(playerKiller.getObjectId());
                  if (count == null) {
                     CaptureCastle._killList2.put(playerKiller.getObjectId(), new AtomicInteger(0));
                     count = (AtomicInteger)CaptureCastle._killList2.get(playerKiller.getObjectId());
                  }

                  currentCount = count.incrementAndGet();
                  if (ConfigCaptureCastle.CAPTURE_CASTLE_EnableKillsInTitle) {
                     CaptureCastle.updateTitle(playerKiller, currentCount);
                  }
               } else if (CaptureCastle.players_list2.contains(self.getObjectId()) && CaptureCastle.players_list1.contains(killer.getObjectId())) {
                  playerKiller = (Player)killer;
                  count = (AtomicInteger)CaptureCastle._killList1.get(playerKiller.getObjectId());
                  if (count == null) {
                     CaptureCastle._killList1.put(playerKiller.getObjectId(), new AtomicInteger(0));
                     count = (AtomicInteger)CaptureCastle._killList1.get(playerKiller.getObjectId());
                  }

                  currentCount = count.incrementAndGet();
                  if (ConfigCaptureCastle.CAPTURE_CASTLE_EnableKillsInTitle) {
                     CaptureCastle.updateTitle(playerKiller, currentCount);
                  }
               }
            }

            Player player = (Player)self;
            Location loc;
            Location pos;
            if (CaptureCastle.getOwnerTeam() != TeamType.NONE) {
               if (player.getTeam() == CaptureCastle.getOwnerTeam()) {
                  pos = ConfigCaptureCastle.CAPTURE_CASTLE_SPAWN_LOC_OWNER_TEAM;
               } else if (player.getTeam() == TeamType.BLUE) {
                  pos = ConfigCaptureCastle.CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_BLUE_TEAM;
               } else {
                  pos = ConfigCaptureCastle.CAPTURE_CASTLE_SPAWN_LOC_ATTACKING_RED_TEAM;
               }

               loc = Location.findPointToStay(pos, 0, 100, CaptureCastle._reflection.getGeoIndex());
            } else {
               pos = CaptureCastle.players_list1.contains(player.getObjectId()) ? ConfigCaptureCastle.CAPTURE_CASTLE_FIRST_TEAM_SPAWN_LOC : ConfigCaptureCastle.CAPTURE_CASTLE_SECOND_TEAM_SPAWN_LOC;
               loc = Location.findPointToStay(pos, 0, 200, CaptureCastle._reflection.getGeoIndex());
            }

            ThreadPoolManager.getInstance().schedule(new CaptureCastle.ResurrectAtBaseTask(player, loc), (long)(ConfigCaptureCastle.CAPTURE_CASTLE_RES_DELAY * 1000));
            ThreadPoolManager.getInstance().schedule(() -> {
               double maxHp = (double)player.getMaxHp();
               double maxMp = (double)player.getMaxMp();
               int maxCp = player.getMaxCp();
               player.setCurrentHpMp(maxHp, maxMp);
               player.setCurrentCp((double)maxCp);
            }, (long)ConfigCaptureCastle.CAPTURE_CASTLE_RES_DELAY * 1000L + 1000L);
         }

      }

      // $FF: synthetic method
      OnDeathListenerImpl(Object x1) {
         this();
      }
   }

   private static class ZoneListener implements OnZoneEnterLeaveListener {
      private ZoneListener() {
      }

      public void onZoneEnter(Zone zone, Creature cha) {
      }

      public void onZoneLeave(Zone zone, Creature cha) {
      }

      // $FF: synthetic method
      ZoneListener(Object x0) {
         this();
      }
   }

   static class ResurrectAtBaseTask extends RunnableImpl {
      final Player player;
      final Location loc;

      ResurrectAtBaseTask(Player player1, Location loc) {
         this.player = player1;
         this.loc = loc;
      }

      public void runImpl() throws Exception {
         if (this.player.getTeam() != TeamType.NONE) {
            if (this.player.isDead()) {
               this.player.setCurrentHp((double)this.player.getMaxHp(), true, true);
               this.player.setCurrentCp((double)this.player.getMaxCp());
               this.player.setCurrentMp((double)this.player.getMaxMp());
               this.player.broadcastPacket(new L2GameServerPacket[]{new Revive(this.player)});
               if (ConfigCaptureCastle.CAPTURE_CASTLE_BuffPlayers) {
                  CaptureCastle.buffPlayer(this.player);
               }
            }

            this.player.teleToLocation(this.loc, CaptureCastle._reflection);
         }
      }
   }

   public static class HealPlayersTask extends RunnableImpl {
      public void runImpl() {
         Iterator var1 = CaptureCastle.getPlayers(CaptureCastle.players_list1).iterator();

         Player player;
         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.healPlayer(player);
         }

         var1 = CaptureCastle.getPlayers(CaptureCastle.players_list2).iterator();

         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.healPlayer(player);
         }

      }
   }

   public static class ResurrectPlayersTask extends RunnableImpl {
      public void runImpl() {
         Iterator var1 = CaptureCastle.getPlayers(CaptureCastle.players_list1).iterator();

         Player player;
         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.resurrectPlayer(player);
         }

         var1 = CaptureCastle.getPlayers(CaptureCastle.players_list2).iterator();

         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.resurrectPlayer(player);
         }

      }
   }

   public static class ParalyzePlayersTask extends RunnableImpl {
      public void runImpl() {
         Iterator var1 = CaptureCastle.getPlayers(CaptureCastle.players_list1).iterator();

         Player player;
         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.paralyzePlayer(player);
         }

         var1 = CaptureCastle.getPlayers(CaptureCastle.players_list2).iterator();

         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.paralyzePlayer(player);
         }

      }
   }

   public static class TeleportPlayersToArenaTask extends RunnableImpl {
      public void runImpl() {
         Iterator var1 = CaptureCastle.getPlayers(CaptureCastle.players_list1).iterator();

         Player player;
         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.teleportPlayerToArena(player, ConfigCaptureCastle.CAPTURE_CASTLE_FIRST_TEAM_SPAWN_LOC);
         }

         var1 = CaptureCastle.getPlayers(CaptureCastle.players_list2).iterator();

         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.teleportPlayerToArena(player, ConfigCaptureCastle.CAPTURE_CASTLE_SECOND_TEAM_SPAWN_LOC);
         }

      }
   }

   public static class PreparePlayersTask extends RunnableImpl {
      public void runImpl() {
         Iterator var1 = CaptureCastle.getPlayers(CaptureCastle.players_list1).iterator();

         Player player;
         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.preparePlayer(player);
         }

         var1 = CaptureCastle.getPlayers(CaptureCastle.players_list2).iterator();

         while(var1.hasNext()) {
            player = (Player)var1.next();
            CaptureCastle.preparePlayer(player);
         }

      }
   }

   public static class EndTask extends RunnableImpl {
      public void runImpl() {
         ThreadPoolManager.getInstance().schedule(new CaptureCastle.ResurrectPlayersTask(), 1000L);
         if (!ConfigCaptureCastle.CAPTURE_CASTLE_AllowBuffs) {
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.ReturnBuffsToPlayersTask(), 3000L);
         }

         ThreadPoolManager.getInstance().schedule(new CaptureCastle.HealPlayersTask(), 3500L);
         ThreadPoolManager.getInstance().schedule(new CaptureCastle.TeleportPlayersFromArenaTask(), 4000L);
         ThreadPoolManager.getInstance().schedule(new CaptureCastle.RemoveSpectatorTask(), 4500L);
         ThreadPoolManager.getInstance().schedule(new CaptureCastle.ClearReflectionTask(), 7000L);
         Functions.executeTask("events.CaptureCastle.CaptureCastle", "preLoad", new Object[0], 10000L);
      }
   }

   public static class EndBattleTask extends RunnableImpl {
      public void runImpl() {
         if (CaptureCastle._endTask != null) {
            CaptureCastle._endTask.cancel(false);
            CaptureCastle._endTask = null;
         }

         CaptureCastle.stopTimerTask();
         if (CaptureCastle.redFlag != null) {
            CaptureCastle.redFlag.deleteMe();
            CaptureCastle.redFlag = null;
         }

         CaptureCastle._status = 0;
         CaptureCastle._zone.setActive(false);
         CaptureCastle.removeAura();
         switch(CaptureCastle.getOwnerTeam()) {
         case RED:
            CaptureCastle.sayToParticipants("scripts.events.CaptureCastle.AnnounceFinishedRedWins", (String[])null);
            CaptureCastle.giveItemsToWinner(false, true, false);
            break;
         case BLUE:
            CaptureCastle.sayToParticipants("scripts.events.CaptureCastle.AnnounceFinishedBlueWins", (String[])null);
            CaptureCastle.giveItemsToWinner(true, false, false);
            break;
         case NONE:
            CaptureCastle.sayToParticipants("scripts.events.CaptureCastle.AnnounceFinishedDraw", (String[])null);
            CaptureCastle.giveItemsToWinner(true, true, true);
         }

         if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowHwidCheck) {
            CaptureCastle.players_hwid_list1.clear();
            CaptureCastle.players_hwid_list2.clear();
         }

         if (ConfigCaptureCastle.CAPTURE_CASTLE_AllowIpCheck) {
            CaptureCastle.players_ip_list1.clear();
            CaptureCastle.players_ip_list2.clear();
         }

         if (ConfigCaptureCastle.CAPTURE_CASTLE_BuffPlayers) {
            Iterator var1 = CaptureCastle.getPlayers(CaptureCastle.players_list1).iterator();

            Player player;
            while(var1.hasNext()) {
               player = (Player)var1.next();
               player.getEffectList().stopAllEffects();
               if (player.getPet() != null) {
                  player.getPet().getEffectList().stopAllEffects();
               }
            }

            var1 = CaptureCastle.getPlayers(CaptureCastle.players_list2).iterator();

            while(var1.hasNext()) {
               player = (Player)var1.next();
               player.getEffectList().stopAllEffects();
               if (player.getPet() != null) {
                  player.getPet().getEffectList().stopAllEffects();
               }
            }
         }

         CaptureCastle.sayToParticipants("scripts.events.CaptureCastle.AnnounceEnd", (String[])null);
         ThreadPoolManager.getInstance().schedule(new CaptureCastle.EndTask(), (long)ConfigCaptureCastle.CAPTURE_CASTLE_TIME_BACK * 1000L);
         CaptureCastle._isRegistrationActive = false;
      }
   }

   public static class BattleTimerTask implements Runnable {
      public void run() {
         CaptureCastle.broadCastTimer();
      }
   }

   public static class GoTask extends RunnableImpl {
      public void runImpl() {
         CaptureCastle._status = 2;
         CaptureCastle.fightBeginTime = System.currentTimeMillis();
         CaptureCastle.upParalyzePlayers();
         CaptureCastle.sayToParticipants("scripts.events.CaptureCastle.AnnounceFight", (String[])null);
         CaptureCastle._endTask = ThreadPoolManager.getInstance().schedule(new CaptureCastle.EndBattleTask(), TimeUnit.MINUTES.toMillis((long)ConfigCaptureCastle.CAPTURE_CASTLE_Time_Battle));
         if (ConfigCaptureCastle.CAPTURE_CASTLE_BROADCAST_TIMER) {
            CaptureCastle._timerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CaptureCastle.BattleTimerTask(), 0L, 1010L);
         }

      }
   }

   public static class PrepareTask extends RunnableImpl {
      public void runImpl() {
         CaptureCastle.cleanPlayers();
         int size = CaptureCastle.players_list1.size() + CaptureCastle.players_list2.size();
         if (!CaptureCastle.players_list1.isEmpty() && !CaptureCastle.players_list2.isEmpty() && size >= ConfigCaptureCastle.CAPTURE_CASTLE_MinPlayerInTeam) {
            CaptureCastle.redFlag.spawnMe();
            CaptureCastle.unSpawnManager();
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.ResurrectPlayersTask(), 100L);
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.ParalyzePlayersTask(), 400L);
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.PreparePlayersTask(), 600L);
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.TeleportPlayersToArenaTask(), 3000L);
            if (ConfigCaptureCastle.CAPTURE_CASTLE_BuffPlayers && ConfigCaptureCastle.CAPTURE_CASTLE_FighterBuffs.length > 0 && ConfigCaptureCastle.CAPTURE_CASTLE_MageBuffs.length > 0) {
               ThreadPoolManager.getInstance().schedule(new CaptureCastle.BuffPlayersTask(), 5000L);
            }

            ThreadPoolManager.getInstance().schedule(new CaptureCastle.HealPlayersTask(), 5500L);
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.GoTask(), (long)ConfigCaptureCastle.CAPTURE_CASTLE_Time_Paralyze * 1000L);
            CaptureCastle.sayToParticipants("scripts.events.CaptureCastle.AnnounceFinalCountdown", new String[]{String.valueOf(ConfigCaptureCastle.CAPTURE_CASTLE_Time_Paralyze)});
         } else {
            CaptureCastle.sayToAll("scripts.events.CaptureCastle.AnnounceEventCancelled", (String[])null);
            CaptureCastle.eventCancel();
            Functions.executeTask("events.CaptureCastle.CaptureCastle", "preLoad", new Object[0], 10000L);
         }
      }
   }

   public static class AnnounceTask extends RunnableImpl {
      public void runImpl() {
         if (CaptureCastle._time_to_start > 1) {
            CaptureCastle._time_to_start--;
            String[] param = new String[]{String.valueOf(CaptureCastle._time_to_start), String.valueOf(CaptureCastle._minLevel), String.valueOf(CaptureCastle._maxLevel)};
            CaptureCastle.sayToAll("scripts.events.CaptureCastle.AnnouncePreStart", param);
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.AnnounceTask(), 60000L);
         } else {
            if (CaptureCastle.players_list1.isEmpty() || CaptureCastle.players_list2.isEmpty() || CaptureCastle.players_list1.size() + CaptureCastle.players_list2.size() < ConfigCaptureCastle.CAPTURE_CASTLE_MinPlayerInTeam) {
               CaptureCastle.sayToAll("scripts.events.CaptureCastle.AnnounceEventCancelled", (String[])null);
               CaptureCastle.eventCancel();
               Functions.executeTask("events.CaptureCastle.CaptureCastle", "preLoad", new Object[0], 10000L);
               return;
            }

            CaptureCastle._status = 1;
            CaptureCastle._isRegistrationActive = false;
            CaptureCastle.sayToAll("scripts.events.CaptureCastle.AnnounceEventStarting", (String[])null);
            ThreadPoolManager.getInstance().schedule(new CaptureCastle.PrepareTask(), 5000L);
         }

      }
   }

   private static class RegAnswerListener implements OnAnswerListener {
      private HardReference<Player> _playerRef1;

      RegAnswerListener(Player player1) {
         this._playerRef1 = player1.getRef();
      }

      public void sayYes() {
         Player player1;
         if ((player1 = (Player)this._playerRef1.get()) != null && (!ConfigCaptureCastle.CAPTURE_CASTLE_AllowHwidCheck || (player1.getNetConnection() == null || !CaptureCastle.players_hwid_list1.contains(player1.getNetConnection().getHwid())) && (player1.getNetConnection() == null || !CaptureCastle.players_hwid_list2.contains(player1.getNetConnection().getHwid())))) {
            if ((player1 = (Player)this._playerRef1.get()) != null && (ConfigCaptureCastle.CAPTURE_CASTLE_AllowIpCheck || !CaptureCastle.players_ip_list1.contains(player1.getIP()) && !CaptureCastle.players_ip_list2.contains(player1.getIP()))) {
               player1.sendMessage(new CustomMessage("scripts.events.CaptureCastle.AnswerYes", player1, new Object[0]));
               CaptureCastle.addPlayer(player1);
            }
         }
      }

      public void sayNo() {
         Player player1;
         if ((player1 = (Player)this._playerRef1.get()) != null) {
            player1.sendMessage(new CustomMessage("scripts.events.CaptureCastle.AnswerNo", player1, new Object[0]));
         }
      }
   }

   public static class QuestionTask extends RunnableImpl {
      public void runImpl() {
         if (ConfigCaptureCastle.CAPTURE_CASTLE_SEND_REG_WINDOW) {
            Iterator var1 = GameObjectsStorage.getAllPlayersForIterate().iterator();

            while(var1.hasNext()) {
               Player player = (Player)var1.next();
               if (CaptureCastle.isWindowCheck(player)) {
                  ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000);
                  String message = (new CustomMessage("scripts.events.CaptureCastle.AskPlayer", player, new Object[0])).toString();
                  packet.addString(message);
                  player.ask(packet, new CaptureCastle.RegAnswerListener(player));
               }
            }
         }

      }
   }
}
