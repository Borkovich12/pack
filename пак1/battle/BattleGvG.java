package events.battle;

import events.battle.enums.BattleType;
import events.battle.model.BattleConditions;
import events.battle.model.BattleGrp;
import events.battle.model.BattleMatch;
import events.battle.tasks.BattleLaunch;
import events.battle.tasks.BattleRegAnnounceTask;
import events.battle.tasks.BattleRegTask;
import events.battle.util.BattleUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.time.cron.SchedulingPattern;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ConfirmDlg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.NpcUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleGvG extends Functions {
   private static final Logger _log = LoggerFactory.getLogger(BattleGvG.class);
   private static AtomicInteger indexBattle = new AtomicInteger(0);
   protected BattleType _type;
   private boolean _active = false;
   private boolean _isRegistrationActive = false;
   private boolean _auto = true;
   private NpcInstance _regNpc;
   private List<BattleMatch> _matches = new ArrayList();
   private int _stage = 1;
   private boolean _secondPlace;
   private boolean _thirdPlace;
   private String _w1;
   private String _w2;
   private String _w3;
   private List<BattleGrp> _winners = new ArrayList(3);
   private ScheduledFuture<?> _globalTask;
   private ScheduledFuture<?> _regTask;
   private ScheduledFuture<?> _regAnnounceTask;
   private List<HardReference<Player>> _leaderList = new CopyOnWriteArrayList();
   private List<BattleGrp> _firstList = new CopyOnWriteArrayList();
   private List<BattleGrp> _nextList = new CopyOnWriteArrayList();
   private Map<Integer, String> _commandNames = new HashMap();
   private Map<Integer, List<HardReference<Player>>> _commands = new ConcurrentHashMap();
   private List<HardReference<Player>> _spectators = new CopyOnWriteArrayList();
   private Map<Integer, Location> _pointByCommand;
   private List<Location> _points;
   private List<String> _restrictIp;
   private List<String> _restrictHwid;
   private Reflection _reflection;
   private Map<Integer, List<Integer>> _customItemList;
   protected final OnPlayerExitListener _exitListener;

   public BattleGvG() {
      this._reflection = ReflectionManager.DEFAULT;
      this._exitListener = new BattleGvG.ExitListener();
   }

   protected void finish() {
      BattleUtil.sayToAll("events.battle.BattleGvG.finish.tourEnd", new String[]{this.getType().getNameType()});
      BattleUtil.sayToAll("events.battle.BattleGvG.finish.firstPlace", new String[]{this._w1, ItemHolder.getInstance().getTemplate(this.getType().getReward()[0][0]).getName(), String.valueOf(this.getType().getReward()[0][1])});
      BattleUtil.sayToAll("events.battle.BattleGvG.finish.secondPlace", new String[]{this._w2, ItemHolder.getInstance().getTemplate(this.getType().getReward()[1][0]).getName(), String.valueOf(this.getType().getReward()[1][1])});
      if (this._w3 != null) {
         BattleUtil.sayToAll("events.battle.BattleGvG.finish.thirdPlace", new String[]{this._w3, ItemHolder.getInstance().getTemplate(this.getType().getReward()[2][0]).getName(), String.valueOf(this.getType().getReward()[2][1])});
      }

      BattleUtil.sayToAll("events.battle.BattleGvG.finish.victory", (String[])null);
      BattleUtil.giveWinnerReward(this._winners, this.getType());
      this.unSpawnManager();
      this.clearList(!this.getType().isToArena());
      this.clearSpectators();
      this.clearBattleMatches();
      this._stage = 1;
      this._secondPlace = false;
      this._thirdPlace = false;
      this.setActive(false);
      if (this._auto) {
         this.initTimer();
      }

      Log.add("GvG " + this.getType().getNameType() + " finished. 1st: " + this._w1 + " 2nd: " + this._w2 + (this._w3 != null ? " 3rd: " + this._w3 : ""), "gvg");
   }

   private void clearSpectators() {
      Iterator var1 = this._spectators.iterator();

      while(var1.hasNext()) {
         HardReference<Player> playerRef = (HardReference)var1.next();
         Player player = (Player)playerRef.get();
         if (player != null) {
            player.leaveObserverMode();
            player.unsetVar("onObservationEnd");
         }
      }

      this._spectators.clear();
   }

   private void clearList(boolean fast) {
      if (this.getType().isToArena()) {
         this._pointByCommand = new HashMap();
         this._points = new CopyOnWriteArrayList();
         String[] var2 = this.getType().getArenaPoints();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String i = var2[var4];
            this._points.add(Location.parseLoc(i));
         }
      }

      Iterator var6;
      if (!this._leaderList.isEmpty()) {
         var6 = this._leaderList.iterator();

         while(var6.hasNext()) {
            HardReference<Player> ref = (HardReference)var6.next();
            Player leader = (Player)ref.get();
            if (leader != null) {
               leader.removeListener(this._exitListener);
            }
         }

         this._leaderList.clear();
      }

      if (fast && !this._commands.isEmpty()) {
         var6 = this._commands.values().iterator();

         while(var6.hasNext()) {
            List<HardReference<Player>> cms = (List)var6.next();
            Iterator var10 = HardReferences.unwrap(cms).iterator();

            while(var10.hasNext()) {
               Player member = (Player)var10.next();
               member.setReg(false);
               member.setInGvG(0);
            }
         }

         this._commands.clear();
      }

      if (this._restrictIp != null && !this._restrictIp.isEmpty()) {
         this._restrictIp.clear();
      }

      if (this._restrictHwid != null && !this._restrictHwid.isEmpty()) {
         this._restrictHwid.clear();
      }

      if (!this._firstList.isEmpty()) {
         this._firstList.clear();
      }

      if (!this._nextList.isEmpty()) {
         this._nextList.clear();
      }

      indexBattle.set(0);
      this.doneReflection();
   }

   private void unSpawnManager() {
      if (this.getType().getManagerSpawnType() <= 2) {
         if (this._regNpc != null) {
            try {
               this._regNpc.deleteMe();
               this._regNpc = null;
            } catch (Exception var2) {
               _log.error("BattleGvG: failed despawn registered manager", var2);
            }
         }

      }
   }

   public void onLoad(BattleType type) {
      this.setType(type);
      this.loadCustomItems();
      this.initTimer();
      if (this.getType().getManagerId() > 0 && this.getType().getManagerSpawnType() > 2) {
         try {
            Location npcLoc = new Location(this.getType().getManagerCoords()[0], this.getType().getManagerCoords()[1], this.getType().getManagerCoords()[2], this.getType().getManagerCoords()[3]);
            this._regNpc = NpcUtils.spawnSingle(this.getType().getManagerId(), npcLoc);
         } catch (Exception var3) {
            _log.error("on spawn manager", var3);
         }
      }

   }

   private void initTimer() {
      SchedulingPattern pattern = new SchedulingPattern(this.getType().getStartTime());
      long time = pattern.next(System.currentTimeMillis());
      if (this._globalTask != null) {
         this._globalTask.cancel(true);
         this._globalTask = null;
      }

      long diff = time - System.currentTimeMillis();
      this._globalTask = ThreadPoolManager.getInstance().schedule(new BattleLaunch(this), diff);
      _log.info("GvG " + this.getType().getNameType() + " started automatically. Next start: " + BattleUtil.toSimpleFormat(time));
   }

   public boolean activateEvent(boolean wa) {
      if (!this.isActive()) {
         this._w1 = null;
         this._w2 = null;
         this._w3 = null;
         this._winners = new ArrayList(3);
         this._restrictIp = new ArrayList();
         this._restrictHwid = new ArrayList();
         this._stage = 1;
         this._secondPlace = false;
         this._thirdPlace = false;
         this.clearList(true);
         this.clearBattleMatches();
         this._commandNames.clear();
         this._regTask = ThreadPoolManager.getInstance().schedule(new BattleRegTask(this), (long)this.getType().getRegTime() * 60000L);
         this._regAnnounceTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BattleRegAnnounceTask(this.getType(), this.getType().getRegTime()), 0L, 60000L);
         BattleUtil.sayToAll("events.battle.BattleGvG.activateEvent.regHasStarted", new String[]{this.getType().getNameType()});
         BattleUtil.sayToAll("events.battle.BattleGvG.activateEvent.appTime", new String[]{String.valueOf(this.getType().getRegTime())});
         this.setActive(true);
         this.setRegistrationActive(true);
         if (this.getType().getManagerId() > 0 && this.getType().getManagerSpawnType() > 0 && this.getType().getManagerSpawnType() < 3) {
            try {
               Location npcLoc = new Location(this.getType().getManagerCoords()[0], this.getType().getManagerCoords()[1], this.getType().getManagerCoords()[2], this.getType().getManagerCoords()[3]);
               this._regNpc = NpcUtils.spawnSingle(this.getType().getManagerId(), npcLoc);
            } catch (Exception var3) {
               _log.error("on spawn manager", var3);
            }
         }

         return true;
      } else {
         if (!wa) {
            _log.warn("GvG " + this.getType().getNameType() + " event already active!");
         }

         return false;
      }
   }

   public void showTeams() {
      Player player = this.getSelf();
      if (player != null) {
         this.showTeams(player);
      }
   }

   public void showTeams(Player player) {
      this.showTeams(player, (NpcInstance)null);
   }

   public void showTeams(Player player, NpcInstance npc) {
      NpcHtmlMessage html;
      if (!this.isRegistrationActive()) {
         html = new NpcHtmlMessage(player, npc);
         html.setFile("scripts/events/tournament/showTeamsNotActive.htm");
         html.replace("%type%", this.getType().getNameType());
         html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
         player.sendPacket(html);
      } else {
         if (!this._leaderList.isEmpty()) {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/showTeams.htm");
            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            html.replace("%bypass_refresh%", "bypass -h user_" + this.getVoiceCommandsByType()[1]);
            int i = 0;
            String msgList = HtmCache.getInstance().getNotNull("scripts/events/tournament/showTeamsList.htm", player);
            StringBuilder htmList = new StringBuilder();
            Iterator var7 = HardReferences.unwrap(this._leaderList).iterator();

            while(var7.hasNext()) {
               Player leader = (Player)var7.next();
               ++i;
               String commandName = (String)this._commandNames.get(leader.getObjectId());
               String temp = msgList.replace("%name%", commandName);
               temp = temp.replace("%count%", String.valueOf(((List)this._commands.get(leader.getObjectId())).size()));
               htmList.append(temp);
            }

            html.replace("%team_counts%", String.valueOf(i));
            html.replace("%list%", htmList.toString());
            player.sendPacket(html);
         } else {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/showTeamsEmptyList.htm");
            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            html.replace("%bypass_refresh%", "bypass -h user_" + this.getVoiceCommandsByType()[1]);
            player.sendPacket(html);
         }

      }
   }

   public void showReg() {
      Player player = this.getSelf();
      if (player != null) {
         this.showReg(player);
      }
   }

   public void showReg(Player player) {
      this.showReg(player, (NpcInstance)null);
   }

   public void showReg(Player player, NpcInstance npc) {
      NpcHtmlMessage html;
      if (!this.isRegistrationActive()) {
         html = new NpcHtmlMessage(player, npc);
         html.setFile("scripts/events/tournament/showRegNotActive.htm");
         html.replace("%type%", this.getType().getNameType());
         html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
         player.sendPacket(html);
      } else {
         if (!this._leaderList.contains(player.getRef())) {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/showReg.htm");
            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_part%", "bypass -h user_" + this.getVoiceCommandsByType()[7]);
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            player.sendPacket(html);
         } else {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/showRegEmptyList.htm");
            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            html.replace("%bypass_surrender%", "bypass -h user_" + this.getVoiceCommandsByType()[2]);
            player.sendPacket(html);
         }

      }
   }

   public void watch(String[] cn) {
      Player player = this.getSelf();
      if (player != null) {
         this.watch(player, cn);
      }
   }

   public void watch(Player player, String[] cn) {
      this.watch(player, (NpcInstance)null, cn);
   }

   public void watch(Player player, NpcInstance npc, String[] cn) {
      if (!this.getType().isAllowSpec()) {
         player.sendMessage(player.isLangRus() ? "Наблюдение за сражениями отключено." : "Battle spectating is disabled.");
      } else {
         NpcHtmlMessage html;
         if (!this.isActive()) {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/watchNotActive.htm");
            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            player.sendPacket(html);
         } else if (this.isRegistrationActive()) {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/watchRegActive.htm");
            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            player.sendPacket(html);
         } else if (!player.isInZonePeace()) {
            player.sendMessage(player.isLangRus() ? "Наблюдение доступно только из мирной зоны." : "Observation is only available from a peaceful zone.");
         } else if (!player.isOlyParticipant() && !ParticipantPool.getInstance().isRegistred(player)) {
            if (player.getTeam() != TeamType.NONE) {
               player.sendMessage(player.isLangRus() ? "Вы участник другого мероприятия!" : "You are a member of another event!");
            } else if (player.isInObserverMode()) {
               player.sendMessage(player.isLangRus() ? "Вы находитесь в режиме наблюдения!" : "You are in spectator mode!");
            } else if (player.isOlyObserver()) {
               player.sendMessage(player.isLangRus() ? "Вы уже в наблюдении!" : "You are already watching!");
            } else if (player.isTeleporting()) {
               player.sendMessage(player.isLangRus() ? "Недоступно во время телепортации!" : "Unavailable while teleporting!");
            } else if (this.getType().isProhibitParticipantsSpec() && player.isReg()) {
               player.sendMessage(player.isLangRus() ? "Участник данного турнира не может наблюдать за боем." : "A participant in this tournament cannot watch the fight.");
            } else {
               boolean enter = player.enterObserverMode(new Location(this._type.getCoordsSpectators()[0], this._type.getCoordsSpectators()[1], this._type.getCoordsSpectators()[2]));
               if (enter) {
                  player.setReflection(this._reflection);
                  player.setVar("onObservationEnd", this._type.getNameType(), -1L);
                  this._spectators.add(player.getRef());
               }
            }
         } else {
            player.sendPacket(SystemMsg.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
         }
      }
   }

   public void status() {
      Player player = this.getSelf();
      if (player != null) {
         this.status(player);
      }
   }

   public void status(Player player) {
      this.status(player, (NpcInstance)null);
   }

   public void status(Player player, NpcInstance npc) {
      NpcHtmlMessage html = new NpcHtmlMessage(player, npc);
      html.setFile("scripts/events/tournament/status.htm");
      boolean activeEvent = this.isActive();
      boolean regActive = this.isRegistrationActive();
      html.replace("%type%", this.getType().getNameType());
      html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
      html.replace("%active%", this.getActiveStatus(player, activeEvent, regActive));
      html.replace("%current_match%", this.getCurrentMatchStatus(player, activeEvent, regActive));
      String membersHtm = "";
      String regHtm = "";
      String storyHtm = "";
      String observerHtm = "";
      String expectHtm = "";
      String storyBypass;
      String refuseBypass;
      if (regActive) {
         storyBypass = "bypass -h user_" + this.getVoiceCommandsByType()[1];
         membersHtm = (new CustomMessage("events.tournament.BattleGvG.status.membersButton", player, new Object[0])).addString(storyBypass).toString();
         if (this._leaderList.contains(player.getRef())) {
            refuseBypass = "bypass -h user_" + this.getVoiceCommandsByType()[2];
            regHtm = (new CustomMessage("events.tournament.BattleGvG.status.refuseButton", player, new Object[0])).addString(refuseBypass).toString();
         } else {
            refuseBypass = "bypass -h user_" + this.getVoiceCommandsByType()[3];
            regHtm = (new CustomMessage("events.tournament.BattleGvG.status.regButton", player, new Object[0])).addString(refuseBypass).toString();
         }
      } else if (activeEvent) {
         storyBypass = "bypass -h user_" + this.getVoiceCommandsByType()[4];
         storyHtm = (new CustomMessage("events.tournament.BattleGvG.status.storyButton", player, new Object[0])).addString(storyBypass).toString();
         refuseBypass = "bypass -h user_" + this.getVoiceCommandsByType()[5];
         expectHtm = (new CustomMessage("events.tournament.BattleGvG.status.expectButton", player, new Object[0])).addString(refuseBypass).toString();
         if (this.getType().isAllowSpec()) {
            String observerBypass = "bypass -h user_" + this.getVoiceCommandsByType()[6] + " 0";
            observerHtm = (new CustomMessage("events.tournament.BattleGvG.status.observerButton", player, new Object[0])).addString(observerBypass).toString();
         }
      }

      html.replace("%member_button%", membersHtm);
      html.replace("%reg_button%", regHtm);
      html.replace("%expect_button%", expectHtm);
      html.replace("%observer_button%", observerHtm);
      html.replace("%story_button%", storyHtm);
      html.replace("%rating_button%", "");
      player.sendPacket(html);
   }

   private String getCurrentMatchStatus(Player player, boolean activeEvent, boolean regActive) {
      String currentMatch = HtmCache.getInstance().getNotNull("scripts/events/tournament/currentMatch.htm", player);
      String curMtc = "";
      if (activeEvent && !regActive) {
         List<BattleMatch> ms = this.getMatches();
         if (ms.size() > 0) {
            BattleMatch nm = (BattleMatch)ms.get(ms.size() - 1);
            if (nm != null) {
               curMtc = currentMatch.replace("%team_one%", nm.getGr1().getName());
               curMtc = curMtc.replace("%team_two%", nm.getGr2().getName());
            }
         }
      }

      return curMtc;
   }

   private String getActiveStatus(Player player, boolean activeEvent, boolean regActive) {
      if (regActive) {
         return StringHolder.getInstance().getNotNull(player, "events.tournament.BattleGvG.activeStatus.regActive");
      } else {
         return activeEvent ? StringHolder.getInstance().getNotNull(player, "events.tournament.BattleGvG.activeStatus.activeEvent") : StringHolder.getInstance().getNotNull(player, "events.tournament.BattleGvG.activeStatus.notActiveEvent");
      }
   }

   public void story() {
      Player player = this.getSelf();
      if (player != null) {
         this.story(player);
      }
   }

   public void story(Player player) {
      this.story(player, (NpcInstance)null);
   }

   public void story(Player player, NpcInstance npc) {
      if (this.isRegistrationActive()) {
         NpcHtmlMessage html = new NpcHtmlMessage(player, npc);
         html.setFile("scripts/events/tournament/storyRegActive.htm");
         html.replace("%type%", this.getType().getNameType());
         html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
         player.sendPacket(html);
      } else {
         List<BattleMatch> ms = this.getMatches();
         int size = ms.size();
         NpcHtmlMessage html;
         if (size == 0) {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/storyNotStarted.htm");
            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            player.sendPacket(html);
         } else {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/story.htm");
            int ee = 0;
            int i = 1;
            String txt = "";

            for(Iterator var9 = ms.iterator(); var9.hasNext(); ++i) {
               BattleMatch nm = (BattleMatch)var9.next();
               if (nm.getStage() != ee) {
                  ee = nm.getStage();
                  txt = txt + (new CustomMessage("events.tournament.BattleGvG.story.stage", player, new Object[0])).addNumber((long)ee).toString();
               }

               if (size != i) {
                  txt = txt + (new CustomMessage("events.tournament.BattleGvG.story.paramOne", player, new Object[0])).addNumber((long)nm.getWin1()).addString(nm.getGr1().getName()).addString(nm.getGr2().getName()).addNumber((long)nm.getWin2()).toString();
               } else {
                  txt = txt + (new CustomMessage("events.tournament.BattleGvG.story.paramTwo", player, new Object[0])).addNumber((long)nm.getWin1()).addString(nm.getGr1().getName()).addString(nm.getGr2().getName()).addNumber((long)nm.getWin2()).toString();
               }
            }

            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            html.replace("%list%", txt);
            player.sendPacket(html);
         }
      }
   }

   public void expec() {
      Player player = this.getSelf();
      if (player != null) {
         this.expec(player);
      }
   }

   public void expec(Player player) {
      this.expec(player, (NpcInstance)null);
   }

   public void expec(Player player, NpcInstance npc) {
      if (this.isRegistrationActive()) {
         player.sendMessage(player.isLangRus() ? "Дождитесь окончания регистрации." : "Wait for the end of registration.");
      } else {
         List<BattleGrp> list1 = this.getFirstList();
         List<BattleGrp> list2 = this.getNextList();
         int size1 = list1.size();
         int size2 = list2.size();
         String listTeamOne = "";
         String listTeamTwo = "";
         NpcHtmlMessage html;
         if (size1 == 0 && size2 == 0) {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/expecEmptyList.htm");
            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            player.sendPacket(html);
         } else {
            html = new NpcHtmlMessage(player, npc);
            html.setFile("scripts/events/tournament/expec.htm");
            Iterator var10;
            BattleGrp cc;
            if (size1 > 0) {
               for(var10 = list1.iterator(); var10.hasNext(); listTeamOne = listTeamOne + (new CustomMessage("events.tournament.BattleGvG.expec.name", player, new Object[0])).addString(cc.getName()).toString()) {
                  cc = (BattleGrp)var10.next();
               }
            }

            if (size2 > 0) {
               for(var10 = list2.iterator(); var10.hasNext(); listTeamTwo = listTeamTwo + (new CustomMessage("events.tournament.BattleGvG.expec.name", player, new Object[0])).addString(cc.getName()).toString()) {
                  cc = (BattleGrp)var10.next();
               }
            }

            html.replace("%type%", this.getType().getNameType());
            html.replace("%bypass_back%", "bypass -h user_" + this.getVoiceCommandsByType()[0]);
            html.replace("%team_one_list%", listTeamOne);
            html.replace("%team_two_list%", listTeamTwo);
            player.sendPacket(html);
         }

      }
   }

   public void removeSpec(HardReference<Player> ref) {
      if (this._spectators.contains(ref)) {
         this._spectators.remove(ref);
      }

      ((Player)ref.get()).unsetVar("onObservationEnd");
   }

   public void addGroup(String[] param) {
      if (param.length >= 1) {
         Player player = this.getSelf();
         if (player != null) {
            this.addGroup(player, param);
         }
      }
   }

   public void addGroup(Player player, String[] param) {
      if (BattleConditions.preCheckAddGroup(player, param[0], this)) {
         List<Player> party = this.isCCType() ? player.getParty().getCommandChannel().getMembers() : player.getParty().getPartyMembers();
         Map<String, Pair<String, String>> errorMessages = BattleConditions.getGroupCheckError();
         List<HardReference<Player>> cms = new CopyOnWriteArrayList();

         Player member;
         for(Iterator var6 = party.iterator(); var6.hasNext(); cms.add(member.getRef())) {
            member = (Player)var6.next();
            String rsn = BattleConditions.validatePlayerForBattle(this, member, false, false);
            if (rsn != null) {
               Pair<String, String> messages = (Pair)errorMessages.get(rsn);
               if (messages != null) {
                  String messageRu = String.format((String)messages.getLeft(), member.getName());
                  String messageEn = String.format((String)messages.getRight(), member.getName());
                  player.sendMessage(player.isLangRus() ? messageRu : messageEn);
               }

               Iterator var18 = party.iterator();

               while(var18.hasNext()) {
                  Player mr = (Player)var18.next();
                  if (mr != null) {
                     mr.setReg(false);
                     mr.setInGvG(0);
                     if (this.getType().isRestrictIp()) {
                        this._restrictIp.remove(mr.getIP());
                     }

                     if (this.getType().isRestrictHwid()) {
                        GameClient memberClient = mr.getNetConnection();
                        if (memberClient != null) {
                           this._restrictHwid.remove(memberClient.getHwid());
                        }
                     }
                  }
               }

               return;
            }

            member.setReg(true);
            if (this.getType().isRestrictIp()) {
               this._restrictIp.add(member.getIP());
            }

            if (this.getType().isRestrictHwid()) {
               GameClient memberClient = member.getNetConnection();
               if (memberClient != null) {
                  this._restrictHwid.add(memberClient.getHwid());
               }
            }
         }

         this._commands.put(player.getObjectId(), cms);
         if (this.getType().isToArena()) {
            this._pointByCommand.put(player.getObjectId(), this._points.remove(0));
         }

         this._commandNames.put(player.getObjectId(), param[0]);
         this._leaderList.add(player.getRef());
         player.addListener(this._exitListener);
         String msgRu = "Ваша группа внесена в список ожидания GvG " + this.getType().getNameType() + " турнира. Пожалуйста, не регистрируйтесь в других ивентах и не участвуйте в дуэлях.";
         String msgEn = "Your group has been placed on the GvG " + this.getType().getNameType() + " tournament waiting list. Please do not register in other events and do not participate in duels.";
         Iterator var15 = HardReferences.unwrap((Collection)this._commands.get(player.getObjectId())).iterator();

         while(var15.hasNext()) {
            Player member = (Player)var15.next();
            member.sendMessage(member.isLangRus() ? msgRu : msgEn);
         }

      }
   }

   public void removeGroup() {
      Player player = this.getSelf();
      if (player != null) {
         this.removeGroup(player);
      }
   }

   public void removeGroup(Player player) {
      if (!this._leaderList.contains(player.getRef())) {
         if (player.isLangRus()) {
            player.sendMessage("Вы не зарегистрированы на GvG " + this.getType().getNameType() + " турнир.");
         } else {
            player.sendMessage("You are not registered for the GvG " + this.getType().getNameType() + " tournament.");
         }

      } else if (!this.isRegistrationActive()) {
         player.sendMessage(player.isLangRus() ? "Нельзя отменить участие по истечению периода регистрации." : "Membership cannot be canceled after the registration period has expired.");
      } else {
         String askMsg = "";
         if (player.isLangRus()) {
            askMsg = "Вы уверены, что хотите струсить и отказаться от участия в GvG " + this.getType().getNameType() + " турнире?";
         } else {
            askMsg = "Are you sure you want to get cold feet and refuse to participate in the GvG " + this.getType().getNameType() + " tournament?";
         }

         final int id = player.getObjectId();
         player.ask((ConfirmDlg)(new ConfirmDlg(SystemMsg.S1, 0)).addString(askMsg), new OnAnswerListener() {
            public void sayYes() {
               BattleGvG.this.done(id);
            }

            public void sayNo() {
            }
         });
      }
   }

   private void done(int id) {
      Player player = GameObjectsStorage.getPlayer(id);
      if (player != null) {
         if (!this._leaderList.contains(player.getRef())) {
            if (player.isLangRus()) {
               player.sendMessage("Вы не зарегистрированы на GvG " + this.getType().getNameType() + " турнир.");
            } else {
               player.sendMessage("You are not registered for the GvG " + this.getType().getNameType() + " tournament.");
            }

         } else if (!this.isRegistrationActive()) {
            if (player.isLangRus()) {
               player.sendMessage("Нельзя отменить участие по истечению периода регистрации.");
            } else {
               player.sendMessage("Membership cannot be canceled after the registration period has expired.");
            }

         } else {
            this._leaderList.remove(player.getRef());
            List<HardReference<Player>> list = (List)this._commands.remove(player.getObjectId());
            if (this.getType().isToArena()) {
               this._points.add(this._pointByCommand.remove(player.getObjectId()));
            }

            player.removeListener(this._exitListener);
            this._commandNames.remove(player.getObjectId());
            if (list != null) {
               String msgRu = "Ваша группа удалена из списка ожидания GvG " + this.getType().getNameType() + " турнира.";
               String msgEn = "Your group has been removed from the GvG " + this.getType().getNameType() + " tournament waitlist.";
               Iterator var6 = HardReferences.unwrap(list).iterator();

               while(var6.hasNext()) {
                  Player member = (Player)var6.next();
                  member.sendMessage(member.isLangRus() ? msgRu : msgEn);
                  member.setReg(false);
               }
            }

         }
      }
   }

   public void stopTimers() {
      if (this._regTask != null) {
         this._regTask.cancel(false);
         this._regTask = null;
      }

      if (this._regAnnounceTask != null) {
         this._regAnnounceTask.cancel(false);
         this._regAnnounceTask = null;
      }

   }

   public void prepare() {
      this.stopTimers();
      this.setRegistrationActive(false);
      if (this.getType().getManagerSpawnType() == 1) {
         this.unSpawnManager();
      }

      if (this.getType().isPreCheck()) {
         this.checkPlayers();
      }

      this._firstList.clear();

      while(this._leaderList.size() > 0) {
         Player player = (Player)((HardReference)this._leaderList.remove(Rnd.get(this._leaderList.size()))).get();
         if (player != null) {
            player.removeListener(this._exitListener);
            this._firstList.add(new BattleGrp(player, (String)this._commandNames.get(player.getObjectId()), (List)this._commands.get(player.getObjectId())));
         }
      }

      if (this._firstList.size() < Math.max(this.getType().getCommandsMin(), 2)) {
         this.unSpawnManager();
         this.clearList(true);
         BattleUtil.sayToAll("events.battle.BattleGvG.prepare.tourCancel", (String[])null);
         this.setActive(false);
         if (this._auto) {
            this.initTimer();
         }

      } else {
         this.initReflection();
         Log.add("GvG " + this.getType().getNameType() + " started.", "gvg");
         BattleUtil.sayToAll("events.battle.BattleGvG.prepare.appComp", (String[])null);
         if (this.getType().isToArena() && this.isActive()) {
            ThreadPoolManager.getInstance().execute(new BattleGvG.TeleportToArenaTask());
         } else {
            this.nextMatch(true);
         }

      }
   }

   private void initReflection() {
      int instanceId = this.getType().getInstanceId();
      if (instanceId > 0) {
         InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(instanceId);
         if (instantZone != null) {
            this._reflection = new Reflection();
            this._reflection.init(instantZone);
         }
      }

      if (this._reflection.getDoors() != null) {
         Iterator var4 = this._reflection.getDoors().iterator();

         while(var4.hasNext()) {
            DoorInstance door = (DoorInstance)var4.next();
            door.closeMe();
         }
      }

   }

   public void doneReflection() {
      if (this._reflection != null && !this._reflection.isDefault()) {
         this._reflection.collapse();
         this._reflection = null;
      }

   }

   protected void checkPlayers() {
      List<HardReference<Player>> toRemove = new ArrayList();
      Map<String, Pair<String, String>> errorMessages = BattleConditions.getCheckPlayersError();
      Iterator var3 = HardReferences.unwrap(this._leaderList).iterator();

      while(var3.hasNext()) {
         Player player = (Player)var3.next();
         String rsn = BattleConditions.validatePlayerForBattle(this, player, true, false);
         if (rsn != null) {
            Pair<String, String> pair = (Pair)errorMessages.get(rsn);
            if (pair != null) {
               String errorMessage = player.isLangRus() ? (String)pair.getLeft() : (String)pair.getRight();
               player.sendMessage(String.format(errorMessage, player.getName()));
            }

            toRemove.add(player.getRef());
         } else if (!this.validateParticipants(player, true)) {
            toRemove.add(player.getRef());
         }
      }

      if (!toRemove.isEmpty()) {
         this.processRemovedPlayers(toRemove);
      }

   }

   protected void processRemovedPlayers(List<HardReference<Player>> toRemove) {
      HardReference ref;
      for(Iterator var2 = toRemove.iterator(); var2.hasNext(); this._leaderList.remove(ref)) {
         ref = (HardReference)var2.next();
         Player leader = (Player)ref.get();
         if (leader != null) {
            leader.removeListener(this._exitListener);
            if (this.getType().isToArena()) {
               this._points.add(this._pointByCommand.remove(leader.getObjectId()));
            }

            this._commandNames.remove(leader.getObjectId());
            if (this._commands.containsKey(leader.getObjectId())) {
               List<HardReference<Player>> cms = (List)this._commands.remove(leader.getObjectId());
               Iterator var6 = HardReferences.unwrap(cms).iterator();

               while(var6.hasNext()) {
                  Player member = (Player)var6.next();
                  member.setReg(false);
                  member.setInGvG(0);
                  if (this.getType().isRestrictIp()) {
                     this._restrictIp.remove(member.getIP());
                  }

                  if (this.getType().isRestrictHwid()) {
                     GameClient memberClient = member.getNetConnection();
                     if (memberClient != null) {
                        this._restrictHwid.remove(memberClient.getHwid());
                     }
                  }
               }
            }
         }
      }

   }

   protected boolean validateParticipants(Player player, boolean me) {
      if (!BattleConditions.preCheckValidate(player, this)) {
         return false;
      } else {
         List<Player> party = this.isCCType() ? player.getParty().getCommandChannel().getMembers() : player.getParty().getPartyMembers();
         Map<String, Pair<String, String>> errorMessages = BattleConditions.getCheckDError();
         Iterator var5 = party.iterator();

         Player partyMember;
         String rsn;
         do {
            if (!var5.hasNext()) {
               return true;
            }

            partyMember = (Player)var5.next();
            rsn = BattleConditions.validatePlayerForBattle(this, partyMember, false, false);
         } while(rsn == null);

         if (me) {
            List<HardReference<Player>> list = (List)this._commands.get(player.getObjectId());
            if (list != null) {
               Pair<String, String> messages = (Pair)errorMessages.get(rsn);
               if (messages != null) {
                  String messageRu = String.format((String)messages.getLeft(), partyMember.getName());
                  String messageEn = String.format((String)messages.getRight(), partyMember.getName());
                  Iterator var12 = HardReferences.unwrap(list).iterator();

                  while(var12.hasNext()) {
                     Player member = (Player)var12.next();
                     member.sendMessage(member.isLangRus() ? messageRu : messageEn);
                  }
               }
            }
         }

         return false;
      }
   }

   public void teamBack(int id, List<HardReference<Player>> players) {
      this._points.add(this._pointByCommand.remove(id));
      this._commandNames.remove(id);
      this._commands.remove(id);
      Iterator var3 = HardReferences.unwrap(players).iterator();

      while(var3.hasNext()) {
         Player player = (Player)var3.next();
         BattleUtil.backPlayer(player, this, false);
      }

   }

   public void now(Player player) {
      if (player.isGM()) {
         if (this._globalTask != null) {
            this._globalTask.cancel(false);
            this._globalTask = null;
         }

         this._auto = false;
         if (this.activateEvent(true)) {
            _log.info("GvG " + this.getType().getNameType() + " started, but stopped automatically.");
         } else {
            _log.info("GvG " + this.getType().getNameType() + " already started, but stopped automatically.");
         }

      }
   }

   public void stop(Player player) {
      if (player.isGM()) {
         if (this._globalTask != null) {
            this._globalTask.cancel(false);
            this._globalTask = null;
         }

         this._auto = false;
         if (!this.isActive()) {
            _log.info("GvG " + this.getType().getNameType() + " event stopped. Automatically disabled.");
         } else if (this.isRegistrationActive()) {
            this.stopTimers();
            this.setRegistrationActive(false);
            this.setActive(false);
            this.unSpawnManager();
            this.clearList(true);
            _log.info("GvG " + this.getType().getNameType() + " stopped on registration state and stopped automatically.");
         } else {
            _log.info("GvG " + this.getType().getNameType() + " can't be stopped, but stopped automatically.");
         }

      }
   }

   public void fugas(Player player) {
      if (player.getPlayerAccess().IsEventGm) {
         Iterator var2 = this._commands.values().iterator();

         while(var2.hasNext()) {
            List<HardReference<Player>> cms = (List)var2.next();
            Iterator var4 = HardReferences.unwrap(cms).iterator();

            while(var4.hasNext()) {
               Player p = (Player)var4.next();
               if (!p.isOlyParticipant() && !p.isLogoutStarted() && !p.isTeleporting()) {
                  if (p.isInGvG()) {
                     if (this.getType().isRestrictIp()) {
                        this.getRestrictIp().remove(player.getIP());
                     }

                     if (this.getType().isRestrictHwid()) {
                        GameClient playerClient = player.getNetConnection();
                        if (playerClient != null) {
                           this.getRestrictHwid().remove(playerClient.getHwid());
                        }
                     }

                     p.kick();
                  } else {
                     BattleUtil.backPlayer(p, this, true);
                  }
               }
            }
         }

         var2 = GameObjectsStorage.getAllPlayersForIterate().iterator();

         while(var2.hasNext()) {
            Player p = (Player)var2.next();
            if (p.getX() == this.getType().getCoordsSpectators()[0] && p.getY() == this.getType().getCoordsSpectators()[1] && p.getReflection() == this.getReflection() && !p.isOlyParticipant() && !p.isLogoutStarted() && !p.isTeleporting()) {
               if (p.isInGvG()) {
                  if (this.getType().isRestrictIp()) {
                     this.getRestrictIp().remove(player.getIP());
                  }

                  if (this.getType().isRestrictHwid()) {
                     GameClient playerClient = player.getNetConnection();
                     if (playerClient != null) {
                        this.getRestrictHwid().remove(playerClient.getHwid());
                     }
                  }

                  p.kick();
               } else {
                  BattleUtil.backPlayer(p, this, true);
               }
            }
         }

      }
   }

   public void auto(Player player) {
      if (player.getPlayerAccess().IsEventGm) {
         this._auto = true;
         if (!this.isActive()) {
            this.initTimer();
         } else {
            _log.info("GvG " + this.getType().getNameType() + " changed to automatically.");
         }

      }
   }

   public void addReg(Player player, Player partic, String team) {
      if (BattleConditions.preCheckAddReg(player, partic, team, this)) {
         Player leader = this.isCCType() ? partic.getParty().getCommandChannel().getChannelLeader() : partic.getParty().getPartyLeader();
         List<HardReference<Player>> cms = new CopyOnWriteArrayList();
         Map<String, Pair<String, String>> errorMessages = BattleConditions.getRegCheckError();
         List<Player> party = this.isCCType() ? leader.getParty().getCommandChannel().getMembers() : leader.getParty().getPartyMembers();

         Player member;
         for(Iterator var8 = party.iterator(); var8.hasNext(); cms.add(member.getRef())) {
            member = (Player)var8.next();
            String rsn = BattleConditions.validatePlayerForBattle(this, member, false, false);
            if (rsn != null) {
               Pair<String, String> messages = (Pair)errorMessages.get(rsn);
               if (messages != null) {
                  String messageRu = String.format((String)messages.getLeft(), member.getName());
                  String messageEn = String.format((String)messages.getRight(), member.getName());
                  player.sendMessage(player.isLangRus() ? messageRu : messageEn);
               }

               Iterator var20 = party.iterator();

               while(var20.hasNext()) {
                  Player mr = (Player)var20.next();
                  if (mr != null) {
                     mr.setReg(false);
                     mr.setInGvG(0);
                     if (this.getType().isRestrictIp()) {
                        this._restrictIp.remove(mr.getIP());
                     }

                     if (this.getType().isRestrictHwid()) {
                        GameClient memberClient = mr.getNetConnection();
                        this._restrictHwid.remove(memberClient.getHwid());
                     }
                  }
               }

               return;
            }

            member.setReg(true);
            if (this.getType().isRestrictIp()) {
               this._restrictIp.add(member.getIP());
            }

            if (this.getType().isRestrictHwid()) {
               GameClient memberClient = member.getNetConnection();
               this._restrictHwid.add(memberClient.getHwid());
            }
         }

         this._commands.put(leader.getObjectId(), cms);
         if (this.getType().isToArena()) {
            this._pointByCommand.put(leader.getObjectId(), this._points.remove(0));
         }

         this._commandNames.put(leader.getObjectId(), team);
         String msgRu = "Гейммастер " + player.getName() + " внёс Вашу группу в список ожидания GvG " + this.getType().getNameType() + " турнира. Пожалуйста, не регистрируйтесь в других ивентах и не участвуйте в дуэлях.";
         String msgEn = "Gamemaster " + player.getName() + " has added your party to the GvG " + this.getType().getNameType() + " waiting list of the tournament. Please do not register in other events and do not participate in duels.";
         Iterator var17;
         Player member;
         if (this.isRegistrationActive()) {
            this._leaderList.add(leader.getRef());
            leader.addListener(this._exitListener);
            var17 = HardReferences.unwrap((Collection)this._commands.get(leader.getObjectId())).iterator();

            while(var17.hasNext()) {
               member = (Player)var17.next();
               member.sendMessage(member.isLangRus() ? msgRu : msgEn);
            }
         } else {
            var17 = HardReferences.unwrap((Collection)this._commands.get(leader.getObjectId())).iterator();

            while(var17.hasNext()) {
               member = (Player)var17.next();
               member.sendMessage(member.isLangRus() ? msgRu : msgEn);
               if (this.getType().isToArena()) {
                  Location point = (Location)this._pointByCommand.get(leader.getObjectId());
                  if (point != null) {
                     BattleUtil.onArena(member, this.getReflection(), point);
                  } else {
                     _log.warn("BattleGvG: For the member " + member.getName() + " with leader objId " + leader.getObjectId() + ", it was not possible to find a position for teleportation to the arena when registering a team.");
                  }
               }
            }

            this._firstList.add(new BattleGrp(leader, (String)this._commandNames.get(leader.getObjectId()), (List)this._commands.get(leader.getObjectId())));
         }

         if (player.isLangRus()) {
            player.sendMessage("Команда " + team + " добавлена в список участников GvG " + this.getType().getNameType() + " турнира.");
         } else {
            player.sendMessage("The " + team + " team has been added to the list of participants in the GvG " + this.getType().getNameType() + " tournament.");
         }

      }
   }

   public void nextMatch(boolean first) {
      int countBattleMatches = this.getCountBattleMatches();
      BattleGrp LSTMLooser;
      BattleGrp gr1;
      BattleMatch ms;
      if (!first) {
         BattleMatch LSTM;
         BattleGrp LSTMWinner;
         if (this._thirdPlace) {
            LSTM = this.getBattleMatchByIndex(countBattleMatches - 1);
            LSTMWinner = LSTM.getWinner() == 1 ? LSTM.getGr1() : LSTM.getGr2();
            if (LSTM.getWinner() == 1) {
               LSTM.getGr2();
            } else {
               LSTM.getGr1();
            }

            this._w3 = LSTMWinner.getName();
            this._winners.add(2, LSTMWinner);
            Log.add(this._w3 + " take 3rd place", "gvg");
            BattleUtil.sayToAll("events.battle.BattleGvG.nextMatch.thirdPlace", new String[]{this._w3});
            BattleUtil.actions(LSTM.getWinner() == 1 ? LSTM.getPlayers1() : LSTM.getPlayers2(), LSTM.getSpectators(), 12);
            this.finish();
            return;
         }

         if (this._secondPlace) {
            LSTM = this.getBattleMatchByIndex(countBattleMatches - 1);
            LSTMWinner = LSTM.getWinner() == 1 ? LSTM.getGr1() : LSTM.getGr2();
            LSTMLooser = LSTM.getWinner() == 1 ? LSTM.getGr2() : LSTM.getGr1();
            this._w2 = LSTMWinner.getName();
            this._winners.add(1, LSTMWinner);
            this._nextList.remove(LSTMWinner);
            Log.add(this._w2 + " take 2nd place", "gvg");
            BattleUtil.sayToAll("events.battle.BattleGvG.nextMatch.secondPlace", new String[]{this._w2});
            BattleUtil.actions(LSTM.getWinner() == 1 ? LSTM.getPlayers1() : LSTM.getPlayers2(), LSTM.getSpectators(), 2024);
            BattleMatch PRSM = this.getBattleMatchByIndex(countBattleMatches - 2);
            if (PRSM.getWinner() == 1) {
               PRSM.getGr1();
            } else {
               PRSM.getGr2();
            }

            BattleGrp PRSMLooser = PRSM.getWinner() == 1 ? PRSM.getGr2() : PRSM.getGr1();
            if (PRSMLooser.getId() == LSTMLooser.getId()) {
               this._w3 = LSTMLooser.getName();
               this._winners.add(2, LSTMLooser);
               Log.add(this._w3 + " get 3rd place", "gvg");
               this.finish();
               return;
            }

            BattleMatch TRDM = this.getBattleMatchByIndex(countBattleMatches - 3);
            BattleGrp TRDMWinner = TRDM.getWinner() == 1 ? TRDM.getGr1() : TRDM.getGr2();
            BattleGrp TRDMLooser = TRDM.getWinner() == 1 ? TRDM.getGr2() : TRDM.getGr1();
            if (TRDMWinner.getId() == LSTMLooser.getId() || TRDMLooser.getId() == LSTMLooser.getId()) {
               List matches = this.getMatches();

               try {
                  TRDM = (BattleMatch)matches.get(countBattleMatches - 4);
               } catch (Exception var16) {
                  TRDM = null;
               }
            }

            BattleGrp gr1;
            if (TRDM != null) {
               gr1 = LSTMLooser;
               BattleGrp gr2 = TRDMLooser;
               if (LSTMLooser.getId() == TRDMLooser.getId()) {
                  this._w3 = LSTMLooser.getName();
                  this._winners.add(2, LSTMLooser);
                  Log.add(this._w3 + " take 3rd place", "gvg");
                  BattleUtil.sayToAll("events.battle.BattleGvG.nextMatch.thirdPlace", new String[]{this._w3});
                  BattleUtil.actions(LSTM.getWinner() == 1 ? LSTM.getPlayers1() : LSTM.getPlayers2(), LSTM.getSpectators(), 12);
                  this.finish();
                  return;
               }

               Iterator var14 = this.getMatches().iterator();

               while(var14.hasNext()) {
                  BattleMatch cm = (BattleMatch)var14.next();
                  if (cm.getResTask() == null) {
                     if (cm.getGr1().getId() != gr1.getId() && cm.getGr1().getId() != gr2.getId() && !cm.getGr1().isEndBattle()) {
                        this.teamBack(cm.getGr1().getId(), cm.getPlayers1());
                        cm.getGr1().setEndBattle(true);
                        this.replaceBattleMatch(cm.getIndex(), cm);
                     }

                     if (cm.getGr2().getId() != gr1.getId() && cm.getGr2().getId() != gr2.getId() && !cm.getGr2().isEndBattle()) {
                        this.teamBack(cm.getGr2().getId(), cm.getPlayers2());
                        cm.getGr2().setEndBattle(true);
                        this.replaceBattleMatch(cm.getIndex(), cm);
                     }
                  }
               }

               this._thirdPlace = true;
               BattleUtil.sayToAll("events.battle.BattleGvG.nextMatch.soonBattleForThirdPlace");
               BattleMatch ms = new BattleMatch(this, gr1, gr2, getIndexBattle());
               this.addBattleMatch(ms);
               ms.start(true, this._stage);
               return;
            }

            gr1 = LSTM.getWinner() == 1 ? LSTM.getGr2() : LSTM.getGr1();
            this._w3 = gr1.getName();
            this._winners.add(2, gr1);
            Log.add(this._w3 + " take 3rd place", "gvg");
            BattleUtil.sayToAll("events.battle.BattleGvG.nextMatch.thirdPlace", new String[]{this._w3});
            BattleUtil.actions(LSTM.getWinner() == 1 ? LSTM.getPlayers1() : LSTM.getPlayers2(), LSTM.getSpectators(), 12);
            this.finish();
            return;
         }

         if (this._firstList.size() + this._nextList.size() < 2) {
            LSTM = this.getBattleMatchByIndex(countBattleMatches - 1);
            this._w1 = LSTM.getWinner() == 1 ? LSTM.getGr1().getName() : LSTM.getGr2().getName();
            this._winners.add(0, LSTM.getWinner() == 1 ? LSTM.getGr1() : LSTM.getGr2());
            this._nextList.remove(LSTM.getWinner() == 1 ? LSTM.getGr1() : LSTM.getGr2());
            Log.add(this._w1 + " take 1st place", "gvg");
            BattleUtil.sayToAll("events.battle.BattleGvG.nextMatch.firstPlace", new String[]{this._w1});
            BattleUtil.actions(LSTM.getWinner() == 1 ? LSTM.getPlayers1() : LSTM.getPlayers2(), LSTM.getSpectators(), 2025);
            if (countBattleMatches > 1) {
               BattleMatch PRSM = this.getBattleMatchByIndex(countBattleMatches - 2);
               if ((PRSM.getWinner() == 1 ? PRSM.getGr1().getId() : PRSM.getGr2().getId()) == (LSTM.getWinner() == 1 ? LSTM.getGr2().getId() : LSTM.getGr1().getId())) {
                  if (countBattleMatches < 3) {
                     this._w2 = PRSM.getWinner() == 1 ? PRSM.getGr1().getName() : PRSM.getGr2().getName();
                     this._winners.add(1, PRSM.getWinner() == 1 ? PRSM.getGr1() : PRSM.getGr2());
                     Log.add(this._w2 + " get 2nd place", "gvg");
                     this.finish();
                     return;
                  }

                  BattleMatch mm = this.getBattleMatchByIndex(countBattleMatches - 3);
                  gr1 = LSTM.getWinner() == 1 ? LSTM.getGr2() : LSTM.getGr1();
                  BattleGrp gr2 = mm.getWinner() == 1 ? mm.getGr2() : mm.getGr1();
                  this._secondPlace = true;
                  BattleUtil.sayToAll("events.battle.BattleGvG.nextMatch.soonBattleForSecondPlace");
                  BattleMatch ms = new BattleMatch(this, gr1, gr2, getIndexBattle());
                  this.addBattleMatch(ms);
                  ms.start(true, this._stage);
                  return;
               }

               LSTMLooser = LSTM.getWinner() == 1 ? LSTM.getGr2() : LSTM.getGr1();
               gr1 = PRSM.getWinner() == 1 ? PRSM.getGr2() : PRSM.getGr1();
               this._secondPlace = true;
               BattleUtil.sayToAll("events.battle.BattleGvG.nextMatch.soonBattleForSecondPlace");
               ms = new BattleMatch(this, LSTMLooser, gr1, getIndexBattle());
               this.addBattleMatch(ms);
               ms.start(true, this._stage);
               return;
            }

            this._w2 = LSTM.getWinner() == 1 ? LSTM.getGr2().getName() : LSTM.getGr1().getName();
            this._winners.add(1, LSTM.getWinner() == 1 ? LSTM.getGr2() : LSTM.getGr1());
            Log.add(this._w2 + " set 2nd place", "gvg");
            this.finish();
            return;
         }
      }

      Player player1 = null;
      Player player2 = null;
      LSTMLooser = null;
      gr1 = null;

      while(!this._firstList.isEmpty()) {
         if (player1 == null) {
            LSTMLooser = (BattleGrp)this._firstList.remove(0);
            player1 = GameObjectsStorage.getPlayer(LSTMLooser.getId());
         }

         if (player2 == null && !this._firstList.isEmpty()) {
            gr1 = (BattleGrp)this._firstList.remove(0);
            player2 = GameObjectsStorage.getPlayer(gr1.getId());
         }

         if (player1 != null && player2 != null) {
            ms = new BattleMatch(this, LSTMLooser, gr1, getIndexBattle());
            this.addBattleMatch(ms);
            ms.start(true, this._stage);
            return;
         }
      }

      if (player1 == null && player2 == null) {
         if (first) {
            this.unSpawnManager();
            this.clearList(true);
            BattleUtil.sayToAll("events.battle.BattleGvG.nextMatch.tournamentCanceled");
            this.setActive(false);
            if (this._auto) {
               this.initTimer();
            }
         } else {
            this.nextStage(false);
         }

      } else {
         boolean order = false;
         if (player1 != null && player2 == null) {
            this._nextList.add(LSTMLooser);
            order = true;
            Say2 csRu = new Say2(0, ChatType.ALLIANCE, "GvG", "При формировании списка участников турнира Ваша команда была перенесена в следующий этап. Ожидайте подбора противника.");
            Say2 csEn = new Say2(0, ChatType.ALLIANCE, "GvG", "When forming the list of participants in the tournament, your team was moved to the next stage. Wait for the selection of the enemy.");
            Iterator var10 = HardReferences.unwrap((Collection)this._commands.get(LSTMLooser.getId())).iterator();

            while(var10.hasNext()) {
               Player player = (Player)var10.next();
               player.sendPacket(player.isLangRus() ? csRu : csEn);
            }
         }

         this.nextStage(order);
      }
   }

   private void nextStage(boolean order) {
      ++this._stage;
      this._firstList.clear();
      List<BattleGrp> newList = new CopyOnWriteArrayList();
      Iterator var3 = this._nextList.iterator();

      while(var3.hasNext()) {
         BattleGrp i = (BattleGrp)var3.next();
         newList.add(i);
      }

      this._nextList.clear();
      if (order) {
         this._firstList.add(newList.remove(newList.size() - 1));
      }

      while(newList.size() > 0) {
         this._firstList.add(newList.remove(Rnd.get(newList.size())));
      }

      this.nextMatch(false);
   }

   public List<BattleGrp> getFirstList() {
      return this._firstList;
   }

   public List<BattleGrp> getNextList() {
      return this._nextList;
   }

   public String getW1() {
      return this._w1;
   }

   public String getW2() {
      return this._w2;
   }

   public String getW3() {
      return this._w3;
   }

   public void addNextList(BattleGrp grp) {
      this._nextList.add(grp);
   }

   public boolean isRegistrationActive() {
      return this._isRegistrationActive;
   }

   public void setRegistrationActive(boolean reg) {
      this._isRegistrationActive = reg;
   }

   public boolean isActive() {
      return this._active;
   }

   public void setActive(boolean active) {
      this._active = active;
   }

   public Map<Integer, List<HardReference<Player>>> getCommands() {
      return this._commands;
   }

   public Map<Integer, String> getCommandNames() {
      return this._commandNames;
   }

   public List<HardReference<Player>> getLeaderList() {
      return this._leaderList;
   }

   public List<Location> getPoints() {
      return this._points;
   }

   public Map<Integer, Location> getPointByCommand() {
      return this._pointByCommand;
   }

   public List<HardReference<Player>> getSpectators() {
      return this._spectators;
   }

   public List<BattleMatch> getMatches() {
      return this._matches;
   }

   public void addBattleMatch(BattleMatch match) {
      this._matches.add(match.getIndex(), match);
   }

   public void replaceBattleMatch(int index, BattleMatch match) {
      this._matches.set(index, match);
   }

   public int getCountBattleMatches() {
      return this._matches.size();
   }

   public void clearBattleMatches() {
      this._matches.clear();
   }

   public BattleMatch getBattleMatchByIndex(int index) {
      return (BattleMatch)this._matches.get(index);
   }

   public List<String> getRestrictIp() {
      return this._restrictIp;
   }

   public List<String> getRestrictHwid() {
      return this._restrictHwid;
   }

   public BattleType getType() {
      return this._type;
   }

   public void setType(BattleType type) {
      this._type = type;
   }

   public String[] getVoiceCommandsByType() {
      return new String[0];
   }

   public boolean isCCType() {
      return false;
   }

   public int getCCMax() {
      return 0;
   }

   public Reflection getReflection() {
      return this._reflection;
   }

   private void loadCustomItems() {
      if (this.getType().isCustomItemsEnable()) {
         try {
            File file = new File(Config.DATAPACK_ROOT, this.getType().getCustomItemsPath());
            if (!file.exists()) {
               _log.error("not found " + this.getType().getCustomItemsPath() + "!!!");
               return;
            }

            this._customItemList = new ConcurrentHashMap(ClassId.values().length);
            Document document = (new SAXReader(false)).read(file);
            Iterator achievementsElementIt = document.getRootElement().elementIterator();

            while(true) {
               Element achievementsElement;
               do {
                  if (!achievementsElementIt.hasNext()) {
                     return;
                  }

                  achievementsElement = (Element)achievementsElementIt.next();
               } while(!"class".equalsIgnoreCase(achievementsElement.getName()));

               int classId = Integer.parseInt(achievementsElement.attributeValue("id"));
               String items = achievementsElement.attributeValue("items");
               List<Integer> is = null;
               if (items != null) {
                  String[] itemsSplit = items.split(",");
                  is = new ArrayList(itemsSplit.length);
                  String[] var9 = itemsSplit;
                  int var10 = itemsSplit.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     String element = var9[var11];
                     is.add(Integer.parseInt(element));
                  }
               }

               if (is != null) {
                  this._customItemList.put(classId, is);
               }
            }
         } catch (Exception var13) {
            _log.error("BattleGvG: error load " + this.getType().getCustomItemsPath(), var13);
         }
      }

   }

   public Map<Integer, List<Integer>> getCustomItemList() {
      return this._customItemList;
   }

   public static synchronized int getIndexBattle() {
      return indexBattle.getAndIncrement();
   }

   private final class ExitListener implements OnPlayerExitListener {
      private ExitListener() {
      }

      public void onPlayerExit(Player player) {
         BattleGvG.this._leaderList.remove(player.getRef());
         BattleGvG.this._commands.remove(player.getObjectId());
         if (BattleGvG.this.getType().isToArena()) {
            BattleGvG.this._points.add(BattleGvG.this._pointByCommand.remove(player.getObjectId()));
         }

         BattleGvG.this._commandNames.remove(player.getObjectId());
      }

      // $FF: synthetic method
      ExitListener(Object x1) {
         this();
      }
   }

   public class StartFirstMatchTask implements Runnable {
      public void run() {
         BattleGvG.this.nextMatch(true);
      }
   }

   public class TeleportToArenaTask implements Runnable {
      public void run() {
         Location ClearLoc = Location.parseLoc(BattleGvG.this.getType().getClearLoc());
         Iterator var2 = BattleGvG.this._commands.keySet().iterator();

         while(var2.hasNext()) {
            int i = (Integer)var2.next();
            Iterator var4 = HardReferences.unwrap((Collection)BattleGvG.this._commands.get(i)).iterator();

            while(var4.hasNext()) {
               Player player = (Player)var4.next();
               if (BattleGvG.this.getType().getReturnPoint().length < 3) {
                  player.setVar("BattleGvG_backCoords", !player.isInZone(ZoneType.no_restart) && !player.isInZone(ZoneType.epic) ? player.getX() + " " + player.getY() + " " + player.getZ() : ClearLoc.x + " " + ClearLoc.y + " " + ClearLoc.z, -1L);
               }

               player.setResurectProhibited(true);
               Location point = (Location)BattleGvG.this._pointByCommand.get(i);
               if (point != null) {
                  BattleUtil.onArena(player, BattleGvG.this.getReflection(), point);
               } else {
                  BattleGvG._log.warn("BattleGvG: For the member " + player.getName() + " with index " + i + ", it was not possible to find a position for teleportation to the arena.");
               }
            }
         }

         ThreadPoolManager.getInstance().schedule(BattleGvG.this.new StartFirstMatchTask(), 5000L);
      }
   }
}
