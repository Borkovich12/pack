package events.TvT2;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.lang.reference.HardReference;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ItemStateFlags;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.model.items.attachment.ItemAttachment;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExEventMatchMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NickNameChanged;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.TimeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.GlobalServices;

public class PvPEvent extends Functions implements ScriptFile {
   private static PvPEvent _instance = null;
   private static final Logger _log = LoggerFactory.getLogger(PvPEvent.class);
   public static final String EVENT_NAME = "PvP";
   private static final Skill BUFF_PROTECTION_EFFECT = SkillTable.getInstance().getInfo(1323, 1);
   private static final Skill AFK_PROTECTION_EFFECT = SkillTable.getInstance().getInfo(4083, 1);
   private PvPEvent.DieListner _dieListner;
   private PvPEvent.ZoneEnterLeaveListner _zoneListner;
   private PvPEvent.ExitListner _exitListner;
   private static final long RANK_BROADCAST_TIME = 20000L;
   public static final String VAR_EVENT_ACTIVE = "PvP_is_active";
   public static final String VAR_START_TIME = "PvP_start_time";
   public static final String VAR_ANNOUNCE_TIME = "PvP_announce_time";
   public static final String VAR_ANNOUNCE_REDUCT = "PvP_announce_reduct";
   public static final String VAR_INSTANCES_IDS = "PvP_instances_ids";
   public static final String VAR_EVENT_COUNTDOWN = "PvP_event_countdown";
   public static final String VAR_EVENT_REG_TYPE = "PvP_event_reg_type";
   private boolean _event_active;
   private boolean _event_countdown;
   private boolean _event_reg_type;
   private String _event_start_time;
   private int _event_announce_time;
   private int _event_announce_reductor;
   private int[] _event_instances_ids;
   private PvPEvent.PvPEventState _state;
   private PvPEvent.PvPEventRule _rule = null;
   private ScheduledFuture<?> _stateTask;
   private ScheduledFuture<?> _processTask;
   private Collection<Integer> _participants;
   private PvPEvent.RegisrationState _regState;
   private Collection<Integer> _desireContainer;

   public static final PvPEvent getInstance() {
      return _instance;
   }

   public void LoadVars() {
      this._event_active = ServerVariables.getBool("PvP_is_active", false);
      this._event_countdown = ServerVariables.getBool("PvP_event_countdown", true);
      this._event_reg_type = ServerVariables.getBool("PvP_event_reg_type", true);
      this._event_start_time = ServerVariables.getString("PvP_start_time", "");
      this._event_announce_time = ServerVariables.getInt("PvP_announce_time", 5);
      this._event_announce_reductor = ServerVariables.getInt("PvP_announce_reduct", 1);
      String[] inst_ids = ServerVariables.getString("PvP_instances_ids", "").split("\\s*;\\s*");
      List<Integer> event_instances_ids = new LinkedList();

      int i;
      for(i = 0; i < inst_ids.length; ++i) {
         String instIdStr = inst_ids[i].trim();
         if (!instIdStr.isEmpty()) {
            event_instances_ids.add(Integer.parseInt(instIdStr));
         }
      }

      this._event_instances_ids = new int[event_instances_ids.size()];

      for(i = 0; i < event_instances_ids.size(); ++i) {
         this._event_instances_ids[i] = (Integer)event_instances_ids.get(i);
      }

   }

   private PvPEvent.PvPEventState getState() {
      return this._state;
   }

   private synchronized void setState(PvPEvent.PvPEventState state) {
      _log.info("PvPEventState changet to " + state.name());
      this._state = state;
   }

   public PvPEvent.PvPEventRule getRule() {
      return this._rule;
   }

   public void setRule(PvPEvent.PvPEventRule rule) {
      this._rule = rule;
   }

   public PvPEvent.PvPEventRule getNextRule(PvPEvent.PvPEventRule rule) {
      if (rule == null) {
         if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.TVT)) {
            return PvPEvent.PvPEventRule.TVT;
         } else if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.CTF)) {
            return PvPEvent.PvPEventRule.CTF;
         } else {
            return this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.DM) ? PvPEvent.PvPEventRule.DM : null;
         }
      } else {
         switch(rule) {
         case TVT:
            if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.CTF)) {
               return PvPEvent.PvPEventRule.CTF;
            }

            if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.DM)) {
               return PvPEvent.PvPEventRule.DM;
            }

            if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.TVT)) {
               return PvPEvent.PvPEventRule.TVT;
            }
            break;
         case CTF:
            if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.DM)) {
               return PvPEvent.PvPEventRule.DM;
            }

            if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.TVT)) {
               return PvPEvent.PvPEventRule.TVT;
            }

            if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.CTF)) {
               return PvPEvent.PvPEventRule.CTF;
            }
            break;
         case DM:
            if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.TVT)) {
               return PvPEvent.PvPEventRule.TVT;
            }

            if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.CTF)) {
               return PvPEvent.PvPEventRule.CTF;
            }

            if (this.config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule.DM)) {
               return PvPEvent.PvPEventRule.DM;
            }
         }

         return null;
      }
   }

   private boolean config_isUseCapcha() {
      return ServerVariables.getBool("PvP_" + this.getRule().name() + "_use_capcha", false);
   }

   private boolean config_hideIdentiti() {
      return ServerVariables.getBool("PvP_" + this.getRule().name() + "_hide_identiti", false);
   }

   private int config_MaxParticipants() {
      return ServerVariables.getInt("PvP_" + this.getRule().name() + "_max_parts", 100);
   }

   private int config_ItemPerKill() {
      return ServerVariables.getInt("PvP_" + this.getRule().name() + "_item_per_kill", 0);
   }

   private int config_ReviveDelay() {
      return ServerVariables.getInt("PvP_" + this.getRule().name() + "_revive_delay", 1);
   }

   private boolean config_isPvPEventRuleEnabled(PvPEvent.PvPEventRule rule) {
      return ServerVariables.getBool("PvP_" + rule.name() + "_enabled", false);
   }

   private boolean config_isBuffProtection() {
      return ServerVariables.getBool("PvP_" + this.getRule().name() + "_buff_protection", false);
   }

   private boolean config_isAfkParticipants() {
      return ServerVariables.getBool("PvP_" + this.getRule().name() + "_afk_protection", false);
   }

   private int config_ReqParticipants() {
      return ServerVariables.getInt("PvP_" + this.getRule().name() + "_req_parts", 50);
   }

   private int config_MinLevel() {
      return ServerVariables.getInt("PvP_" + this.getRule().name() + "_min_lvl", 1);
   }

   private int config_MaxLevel() {
      return ServerVariables.getInt("PvP_" + this.getRule().name() + "_max_lvl", 86);
   }

   private int config_RewardHeroHours() {
      return ServerVariables.getInt("PvP_" + this.getRule().name() + "_herorevhours", 0);
   }

   private List<Pair<ItemTemplate, Long>> config_RewardWinnerTeamItemIdAndAmount() {
      String teamRevListText = ServerVariables.getString("PvP_" + this.getRule().name() + "_rev_team", "");
      return Functions.parseItemIdAmountList(teamRevListText);
   }

   private List<Pair<ItemTemplate, Long>> config_RewardLooserTeamItemIdAndAmount() {
      String teamRevListText = ServerVariables.getString("PvP_" + this.getRule().name() + "_rev_lose_team", "");
      return Functions.parseItemIdAmountList(teamRevListText);
   }

   private List<Pair<ItemTemplate, Long>> config_RewardTieItemIdAndAmount() {
      String teamRevListText = ServerVariables.getString("PvP_" + this.getRule().name() + "_rev_tie_team", "");
      return Functions.parseItemIdAmountList(teamRevListText);
   }

   private List<Pair<ItemTemplate, Long>> config_RewardTopItemIdAndAmount() {
      String teamRevListText = ServerVariables.getString("PvP_" + this.getRule().name() + "_rev_top", "");
      return Functions.parseItemIdAmountList(teamRevListText);
   }

   private boolean config_dispellEffects() {
      return ServerVariables.getBool("PvP_" + this.getRule().name() + "_dispell", true);
   }

   private boolean config_dispellEffectsAfter() {
      return ServerVariables.getBool("PvP_" + this.getRule().name() + "_dispell_after", true);
   }

   private int config_EventTime() {
      switch(this.getRule()) {
      case TVT:
      case CTF:
         return ServerVariables.getInt("PvP_" + this.getRule().name() + "_time", 10);
      case DM:
         return ServerVariables.getInt("PvP_" + this.getRule().name() + "_time", 5);
      default:
         return 0;
      }
   }

   private int config_minKillCountForReward() {
      return ServerVariables.getInt("PvP_" + this.getRule().name() + "_min_kill_reward", 0);
   }

   private int getNewReflectionId() {
      return this._event_instances_ids[Rnd.get(this._event_instances_ids.length)];
   }

   private synchronized void scheduleStateChange(PvPEvent.PvPEventState to_state, long delay) {
      this._stateTask = ThreadPoolManager.getInstance().schedule(new PvPEvent.PvPStateTask(to_state), delay);
   }

   private synchronized void cancelStateChange() {
      if (this._stateTask != null) {
         this._stateTask.cancel(false);
         this._stateTask = null;
      }

   }

   private void goStandby() {
      this.setState(PvPEvent.PvPEventState.STANDBY);
      long mills = this.getMillsToNextActivation(this._event_start_time);
      if (mills > 0L) {
         PvPEvent.PvPEventRule nextRule = this.getNextRule(this.getRule());
         if (nextRule != null) {
            this.setRule(nextRule);
            this.scheduleStateChange(PvPEvent.PvPEventState.REGISTRATION, mills);
            _log.info("PvPEvent: Next scheduled at " + TimeUtils.toSimpleFormat(System.currentTimeMillis() + mills));
         } else {
            _log.info("PvPEvent: No active next event");
         }
      } else {
         _log.warn("PvPEvent: Wrong event time: " + this._event_start_time);
      }

   }

   public void goRegistration() {
      this.setState(PvPEvent.PvPEventState.REGISTRATION);
      getInstance().scheduleProcessTask(new PvPEvent.RegisrationTask(PvPEvent.RegisrationState.ANNOUNCE, this._event_announce_time), 1000L);
   }

   private void goPrepareTo() {
      this.setState(PvPEvent.PvPEventState.PREPARE_TO);
      this.getRule().getParticipantController().initReflection();
      this.getRule().getParticipantController().prepareParticipantsTo();
      this.scheduleStateChange(PvPEvent.PvPEventState.PORTING_TO, 2000L);
   }

   private void goPortingTo() {
      this.setState(PvPEvent.PvPEventState.PORTING_TO);
      this.getRule().getParticipantController().portParticipantsTo();
      getInstance().scheduleProcessTask(new PvPEvent.CompetitionRunTask(30), 1000L);
   }

   private void goCompetition() {
      this.setState(PvPEvent.PvPEventState.COMPETITION);
      this.getRule().getParticipantController().initParticipant();
      this.scheduleStateChange(PvPEvent.PvPEventState.WINNER, (long)(this.config_EventTime() * 60 * 1000));
   }

   private void goWinner() {
      this.setState(PvPEvent.PvPEventState.WINNER);
      this.getRule().getParticipantController().MakeWinner();
      this.scheduleStateChange(PvPEvent.PvPEventState.PREPARE_FROM, 1000L);
   }

   private void goPrepareFrom() {
      this.setState(PvPEvent.PvPEventState.PREPARE_FROM);
      this.getRule().getParticipantController().prepareParticipantsFrom();
      this.scheduleStateChange(PvPEvent.PvPEventState.PORTING_FROM, 10000L);
   }

   private void goPortingFrom() {
      this.setState(PvPEvent.PvPEventState.PORTING_FROM);
      this.getRule().getParticipantController().portParticipantsBack();
      this.getRule().getParticipantController().doneParicipant();
      this.getRule().getParticipantController().doneReflection();
      this._participants.clear();
      this._participants = null;
      this.scheduleStateChange(PvPEvent.PvPEventState.STANDBY, 5000L);
   }

   private synchronized void scheduleProcessTask(Runnable r, long delay) {
      this._processTask = ThreadPoolManager.getInstance().schedule(r, delay);
   }

   private synchronized void cancelProcessTask() {
      if (this._processTask != null) {
         this._processTask.cancel(false);
         this._processTask = null;
      }

   }

   private Collection<Player> getPlayers() {
      List<Player> result = new ArrayList(this._participants.size());
      Iterator var2 = GameObjectsStorage.getAllPlayersForIterate().iterator();

      while(true) {
         Player player;
         do {
            if (!var2.hasNext()) {
               return result;
            }

            player = (Player)var2.next();
         } while(player == null);

         Iterator var4 = this._participants.iterator();

         while(var4.hasNext()) {
            Integer oid = (Integer)var4.next();
            if (oid == player.getObjectId()) {
               result.add(player);
            }
         }
      }
   }

   private void broadcastParticipationRequest() {
      List<Player> players = new ArrayList();
      Iterator var2 = GameObjectsStorage.getAllPlayers().iterator();

      Player player;
      while(var2.hasNext()) {
         player = (Player)var2.next();
         if (isDesirePlayer(player)) {
            players.add(player);
         }
      }

      if (this._event_reg_type) {
         var2 = players.iterator();

         while(var2.hasNext()) {
            player = (Player)var2.next();
            player.scriptRequest((new CustomMessage("events.PvPEvent.AskToS1Participation", player, new Object[]{this.getRule().name()})).toString(), "events.TvT2.PvPEvent:addDesire", new Object[0]);
         }
      }

   }

   private void broadcastCapchaRequest() {
      if (this._regState == PvPEvent.RegisrationState.CAPCHA && this._desireContainer != null) {
         List<Player> players = new ArrayList();
         Iterator var2 = this._desireContainer.iterator();

         while(var2.hasNext()) {
            Integer oid = (Integer)var2.next();
            Player player = GameObjectsStorage.getPlayer(oid);
            if (isDesirePlayer(player)) {
               players.add(player);
            }
         }

         this._desireContainer.clear();
         this._desireContainer = null;
         getInstance()._desireContainer = new ConcurrentSkipListSet();
         var2 = players.iterator();

         while(var2.hasNext()) {
            Player player = (Player)var2.next();
            Scripts.getInstance().callScripts(player, "Util", "RequestCapcha", new Object[]{"events.TvT2.PvPEvent:addDesire", player.getStoredId(), new Integer(30)});
         }

      }
   }

   private static boolean isDesirePlayer(Player player) {
      if (player != null && player.getNetConnection() != null && player.isConnected()) {
         if (!player.isDead() && !player.isBlocked() && !player.isInZone(ZoneType.epic) && !player.isInZone(ZoneType.SIEGE) && !player.isInZone(ZoneType.SIEGE) && player.getReflectionId() == 0) {
            if (!player.isFishing() && player.getTransformation() == 0 && !player.isCursedWeaponEquipped()) {
               if (player.getLevel() >= getInstance().config_MinLevel() && player.getLevel() <= getInstance().config_MaxLevel()) {
                  if (!player.isOlyParticipant() && !ParticipantPool.getInstance().isRegistred(player)) {
                     if (Config.PVP_EVENT_RESTRICT_HWID && getInstance().isHWIDRegistred(player.getNetConnection().getHwid(), player)) {
                        return false;
                     } else if (Config.PVP_EVENT_RESTRICT_IP && getInstance().isIPRegistered(player.getNetConnection().getIpAddr(), player)) {
                        return false;
                     } else if (Config.PVP_EVENT_RESTRICT_CLASS_IDS.length > 0 && ArrayUtils.contains(Config.PVP_EVENT_RESTRICT_CLASS_IDS, player.getActiveClassId())) {
                        return false;
                     } else {
                        return player.getTeam() == TeamType.NONE && !player.isInDuel();
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isHWIDRegistred(String hwid, Player player) {
      if (hwid != null && !hwid.isEmpty() && this._desireContainer != null) {
         Iterator var3 = this._desireContainer.iterator();

         Player p;
         GameClient gameClient;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            Integer oid = (Integer)var3.next();
            p = GameObjectsStorage.getPlayer(oid);
         } while(p == null || p == player || (gameClient = p.getNetConnection()) == null || !gameClient.isConnected() || gameClient.getHwid() == null || !hwid.equalsIgnoreCase(gameClient.getHwid()));

         return true;
      } else {
         return false;
      }
   }

   public boolean isIPRegistered(String ip, Player player) {
      if (ip != null && !ip.isEmpty() && this._desireContainer != null) {
         Iterator var3 = this._desireContainer.iterator();

         Player p;
         GameClient gameClient;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            Integer oid = (Integer)var3.next();
            p = GameObjectsStorage.getPlayer(oid);
         } while(p == null || p == player || (gameClient = p.getNetConnection()) == null || !gameClient.isConnected() || gameClient.getIpAddr() == null || !ip.equalsIgnoreCase(gameClient.getIpAddr()));

         return true;
      } else {
         return false;
      }
   }

   public void addDesire() {
      Player player = this.getSelf();
      if (player != null) {
         if (getInstance()._regState != PvPEvent.RegisrationState.REQUEST && getInstance()._regState != PvPEvent.RegisrationState.CAPCHA) {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireInappropriateState", player, new Object[0]));
         } else if (isDesirePlayer(player) && getInstance()._desireContainer != null) {
            if (getInstance()._desireContainer.contains(player.getObjectId())) {
               player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireAlreadyAccepted", player, new Object[0]));
            } else {
               player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireAccepted", player, new Object[0]));
               getInstance()._desireContainer.add(player.getObjectId());
            }
         } else {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireInsufficientConditions", player, new Object[0]));
         }
      }
   }

   public void addDesireDuringAnnounce() {
      Player player = this.getSelf();
      if (player != null) {
         if (getInstance()._regState != PvPEvent.RegisrationState.ANNOUNCE) {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireInappropriateState", player, new Object[0]));
         } else if (isDesirePlayer(player) && getInstance()._desireContainer != null) {
            if (getInstance()._desireContainer.contains(player.getObjectId())) {
               player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireAlreadyAccepted", player, new Object[0]));
            } else {
               player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireAccepted", player, new Object[0]));
               getInstance()._desireContainer.add(player.getObjectId());
            }
         } else {
            player.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireInsufficientConditions", player, new Object[0]));
         }
      }
   }

   private void morphDesires() {
      if (this._regState == PvPEvent.RegisrationState.MORPH && this._desireContainer != null) {
         List<Player> players = new LinkedList();
         Iterator var2 = this._desireContainer.iterator();

         while(var2.hasNext()) {
            Integer oid = (Integer)var2.next();
            Player player = GameObjectsStorage.getPlayer(oid);
            if (isDesirePlayer(player)) {
               players.add(player);
            }
         }

         this._desireContainer.clear();
         this._desireContainer = null;
         List<Player> participants = new ArrayList();
         int max_part = this.config_MaxParticipants();

         while(participants.size() < max_part && !players.isEmpty()) {
            participants.add(players.remove(Rnd.get(players.size())));
         }

         if (participants.size() < this.config_ReqParticipants()) {
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.EventS1LackParticipants", new String[]{this.getRule().name()});
            this.goStandby();
         } else {
            this._participants = new ConcurrentSkipListSet();
            Iterator var8 = players.iterator();

            Player participant;
            while(var8.hasNext()) {
               participant = (Player)var8.next();
               participant.sendMessage(new CustomMessage("events.PvPEvent.ParticipantAskLater", participant, new Object[0]));
            }

            var8 = participants.iterator();

            while(var8.hasNext()) {
               participant = (Player)var8.next();
               participant.sendMessage(new CustomMessage("events.PvPEvent.ParticipantAccepted", participant, new Object[0]));
               this._participants.add(participant.getObjectId());
            }

            players.clear();
            this.scheduleStateChange(PvPEvent.PvPEventState.PREPARE_TO, 10000L);
         }
      }
   }

   public void Activate() {
      getInstance().scheduleStateChange(PvPEvent.PvPEventState.STANDBY, 1000L);
      _log.info("PvPEvent: [state: active]");
   }

   public void Deativate() {
      ServerVariables.set("PvP_is_active", false);
      getInstance().LoadVars();
      getInstance().cancelStateChange();
      _log.info("PvPEvent: [state: inactive]");
   }

   public void onLoad() {
      _instance = this;
      this.LoadVars();
      if (this._event_active) {
         this.Activate();
      } else {
         _log.info("PvPEvent: [state: inactive]");
      }

      this._dieListner = new PvPEvent.DieListner();
      this._zoneListner = new PvPEvent.ZoneEnterLeaveListner();
      this._exitListner = new PvPEvent.ExitListner();
   }

   public void onReload() {
   }

   public void onShutdown() {
   }

   private long getMillsToNextActivation(String schedule) {
      Matcher m = Pattern.compile("(\\d{2})\\:(\\d{2});*").matcher(schedule);
      long now = System.currentTimeMillis();
      long ret_mills = Long.MAX_VALUE;

      while(m.find()) {
         String hour_str = m.group(1);
         String minute_str = m.group(2);
         int hour = Integer.parseInt(hour_str);
         int minute = Integer.parseInt(minute_str);
         Calendar next_c = Calendar.getInstance();
         next_c.set(11, hour);
         next_c.set(12, minute);
         next_c.set(13, 0);
         next_c.set(14, 0);
         if (next_c.getTimeInMillis() < now) {
            next_c.add(5, 1);
         }

         long mills_left = next_c.getTimeInMillis() - now;
         if (mills_left > 0L && mills_left < ret_mills) {
            ret_mills = mills_left;
         }
      }

      return ret_mills < Long.MAX_VALUE ? ret_mills : -1L;
   }

   private void broadcast(L2GameServerPacket... gsp) {
      Collection<Player> players = this.getPlayers();
      Iterator var3 = players.iterator();

      while(var3.hasNext()) {
         Player player = (Player)var3.next();
         player.sendPacket(gsp);
      }

   }

   public void removeParticipant() {
      Player p = this.getSelf();
      if (getInstance()._regState != PvPEvent.RegisrationState.ANNOUNCE) {
         p.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireInappropriateState", p, new Object[0]));
      } else {
         if (!getInstance()._desireContainer.contains(p.getObjectId())) {
            p.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireDoesNotExist", p, new Object[0]));
         } else {
            getInstance()._desireContainer.remove(p.getObjectId());
            p.sendMessage(new CustomMessage("events.PvPEvent.ParticipationDesireRemove", p, new Object[0]));
         }

      }
   }

   public boolean isEventParticipant() {
      Player p = this.getSelf();
      if (getInstance() != null && getInstance()._participants != null && p != null) {
         int poid = p.getObjectId();
         Iterator var3 = getInstance()._participants.iterator();

         Integer oid;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            oid = (Integer)var3.next();
         } while(oid != poid);

         return true;
      } else {
         return false;
      }
   }

   private class TeleportAndReviveTask implements Runnable {
      private final HardReference<Player> _playerRef;
      private Location _loc;
      private Reflection _ref;

      public TeleportAndReviveTask(Player player, Location loc, Reflection ref) {
         this._playerRef = player.getRef();
         this._loc = loc;
         this._ref = ref;
      }

      public void run() {
         Player player = (Player)this._playerRef.get();
         if (player != null) {
            synchronized(player) {
               player.teleToLocation(this._loc, this._ref);
               if (!player.isConnected()) {
                  player.onTeleported();
               }

               if (player.isDead()) {
                  player.setCurrentHp((double)player.getMaxHp(), true, true);
                  player.setCurrentCp((double)player.getMaxCp());
                  player.setCurrentMp((double)player.getMaxMp());
                  player.broadcastPacket(new L2GameServerPacket[]{new Revive(player)});
                  Set<Skill> exitEffectSkills = new HashSet();
                  Iterator var4 = player.getEffectList().getAllEffects().iterator();

                  while(var4.hasNext()) {
                     Effect e = (Effect)var4.next();
                     if (e.getSkill().isOffensive()) {
                        exitEffectSkills.add(e.getSkill());
                     }
                  }

                  var4 = exitEffectSkills.iterator();

                  while(var4.hasNext()) {
                     Skill skill = (Skill)var4.next();
                     Iterator var6 = player.getEffectList().getEffectsBySkill(skill).iterator();

                     while(var6.hasNext()) {
                        Effect effect = (Effect)var6.next();
                        effect.exit();
                     }
                  }

                  if (PvPEvent.getInstance().config_isBuffProtection()) {
                     PvPEvent.BUFF_PROTECTION_EFFECT.getEffects(player, player, false, false, false);
                  }

                  if (!Config.PVP_EVENTS_MAGE_BUFF_ON_REVIVE.isEmpty() || !Config.PVP_EVENTS_WARRIOR_BUFF_ON_REVIVE.isEmpty()) {
                     var4 = (player.isMageClass() ? Config.PVP_EVENTS_MAGE_BUFF_ON_REVIVE : Config.PVP_EVENTS_WARRIOR_BUFF_ON_REVIVE).iterator();

                     while(var4.hasNext()) {
                        Pair<Integer, Integer> pair = (Pair)var4.next();
                        SkillTable.getInstance().getInfo((Integer)pair.getLeft(), (Integer)pair.getRight()).getEffects(player, player, false, false);
                     }
                  }
               }

            }
         }
      }
   }

   private class TeleportTask implements Runnable {
      private Player _player;
      private Location _loc;
      private Reflection _ref;

      public TeleportTask(Player player, Location loc, Reflection ref) {
         this._player = player;
         this._loc = loc;
         this._ref = ref;
      }

      public void run() {
         this._player.teleToLocation(this._loc, this._ref);
      }
   }

   private class ExitListner implements OnPlayerExitListener {
      private ExitListner() {
      }

      public void onPlayerExit(Player player) {
         try {
            if (PvPEvent.getInstance().getState() == PvPEvent.PvPEventState.STANDBY) {
               return;
            }

            PvPEvent.getInstance().getRule().getParticipantController().OnExit(player);
            PvPEvent.getInstance()._participants.remove(player.getObjectId());
         } catch (Exception var3) {
            PvPEvent._log.warn("PVPEvent.onPlayerExit :", var3);
         }

      }

      // $FF: synthetic method
      ExitListner(Object x1) {
         this();
      }
   }

   private class TeleportListner implements OnTeleportListener {
      public void onTeleport(Player player, int x, int y, int z, Reflection r) {
         try {
            if (PvPEvent.getInstance().getState() != PvPEvent.PvPEventState.COMPETITION) {
               return;
            }

            PvPEvent.getInstance().getRule().getParticipantController().OnTeleport(player, x, y, z, r);
         } catch (Exception var7) {
            PvPEvent._log.warn("PVPEvent.onTeleport :", var7);
         }

      }
   }

   private class ZoneEnterLeaveListner implements OnZoneEnterLeaveListener {
      private ZoneEnterLeaveListner() {
      }

      public void onZoneEnter(Zone zone, Creature actor) {
         try {
            if (PvPEvent.getInstance().getState() != PvPEvent.PvPEventState.COMPETITION || !actor.isPlayer()) {
               return;
            }

            PvPEvent.getInstance().getRule().getParticipantController().OnEnter(actor.getPlayer(), zone);
         } catch (Exception var4) {
            PvPEvent._log.warn("PVPEvent.onZoneEnter :", var4);
         }

      }

      public void onZoneLeave(Zone zone, Creature actor) {
         try {
            if (PvPEvent.getInstance().getState() != PvPEvent.PvPEventState.COMPETITION || !actor.isPlayer()) {
               return;
            }

            PvPEvent.getInstance().getRule().getParticipantController().OnLeave(actor.getPlayer(), zone);
         } catch (Exception var4) {
            PvPEvent._log.warn("PVPEvent.onZoneLeave :", var4);
         }

      }

      // $FF: synthetic method
      ZoneEnterLeaveListner(Object x1) {
         this();
      }
   }

   private class DieListner implements OnDeathListener {
      private DieListner() {
      }

      public void onDeath(Creature actor, Creature killer) {
         try {
            if (PvPEvent.getInstance().getState() != PvPEvent.PvPEventState.COMPETITION) {
               return;
            }

            Player ptarget = actor.getPlayer();
            Player pkiller = killer.getPlayer();
            if (ptarget != null) {
               PvPEvent.getInstance().getRule().getParticipantController().OnPlayerDied(ptarget, pkiller);
            }
         } catch (Exception var5) {
            PvPEvent._log.warn("PVPEvent.onDeath :", var5);
         }

      }

      // $FF: synthetic method
      DieListner(Object x1) {
         this();
      }
   }

   private class CompetitionRunTask implements Runnable {
      private int _left;

      public CompetitionRunTask(int left) {
         this._left = left;
      }

      public void run() {
         label37:
         switch(this._left) {
         case 0:
            PvPEvent.getInstance().scheduleStateChange(PvPEvent.PvPEventState.COMPETITION, 100L);
            if (PvPEvent.this._event_countdown) {
               PvPEvent.getInstance().broadcast(ExEventMatchMessage.START);
            }

            return;
         case 1:
            if (PvPEvent.this._event_countdown) {
               PvPEvent.getInstance().broadcast(ExEventMatchMessage.COUNT1);
            }
            break;
         case 2:
            if (PvPEvent.this._event_countdown) {
               PvPEvent.getInstance().broadcast(ExEventMatchMessage.COUNT2);
            }
            break;
         case 3:
            if (PvPEvent.this._event_countdown) {
               PvPEvent.getInstance().broadcast(ExEventMatchMessage.COUNT3);
            }
            break;
         case 4:
            if (PvPEvent.this._event_countdown) {
               PvPEvent.getInstance().broadcast(ExEventMatchMessage.COUNT4);
            }
            break;
         case 5:
            if (PvPEvent.this._event_countdown) {
               PvPEvent.getInstance().broadcast(ExEventMatchMessage.COUNT5);
            }

            Iterator var1 = PvPEvent.getInstance().getPlayers().iterator();

            while(true) {
               if (!var1.hasNext()) {
                  break label37;
               }

               Player player = (Player)var1.next();
               player.broadcastUserInfo(true);
            }
         case 30:
            PvPEvent.getInstance().scheduleProcessTask(PvPEvent.this.new CompetitionRunTask(this._left - 25), 25000L);
            return;
         }

         PvPEvent.getInstance().scheduleProcessTask(PvPEvent.this.new CompetitionRunTask(this._left - 1), 1000L);
      }
   }

   private class RegisrationTask implements Runnable {
      private final int _left;
      private final PvPEvent.RegisrationState _to_reg_state;

      public RegisrationTask(PvPEvent.RegisrationState to_state, int left) {
         this._left = left;
         this._to_reg_state = to_state;
      }

      public void run() {
         if (PvPEvent.getInstance()._regState != this._to_reg_state && this._to_reg_state == PvPEvent.RegisrationState.ANNOUNCE) {
            if (PvPEvent.getInstance()._desireContainer != null) {
               PvPEvent.getInstance()._desireContainer.clear();
               PvPEvent.getInstance()._desireContainer = null;
            }

            PvPEvent.getInstance()._desireContainer = new ConcurrentSkipListSet();
         }

         PvPEvent.getInstance()._regState = this._to_reg_state;
         switch(this._to_reg_state) {
         case ANNOUNCE:
            if (this._left > 0) {
               Announcements.getInstance().announceByCustomMessage("events.PvPEvent.EventS1StartAtS2Minutes", new String[]{PvPEvent.this.getRule().name(), String.valueOf(this._left)});
               PvPEvent.getInstance().scheduleProcessTask(PvPEvent.this.new RegisrationTask(PvPEvent.RegisrationState.ANNOUNCE, Math.max(0, this._left - PvPEvent.getInstance()._event_announce_reductor)), (long)(PvPEvent.getInstance()._event_announce_reductor * 60 * 1000));
            } else {
               PvPEvent.getInstance().scheduleProcessTask(PvPEvent.this.new RegisrationTask(PvPEvent.RegisrationState.REQUEST, 0), 1000L);
            }
            break;
         case REQUEST:
            PvPEvent.getInstance().broadcastParticipationRequest();
            PvPEvent.getInstance().scheduleProcessTask(PvPEvent.this.new RegisrationTask(PvPEvent.getInstance().config_isUseCapcha() ? PvPEvent.RegisrationState.CAPCHA : PvPEvent.RegisrationState.MORPH, 0), 40000L);
            break;
         case CAPCHA:
            PvPEvent.getInstance().broadcastCapchaRequest();
            PvPEvent.getInstance().scheduleProcessTask(PvPEvent.this.new RegisrationTask(PvPEvent.RegisrationState.MORPH, 0), 40000L);
            break;
         case MORPH:
            PvPEvent.getInstance().morphDesires();
         }

      }
   }

   private static enum RegisrationState {
      ANNOUNCE,
      REQUEST,
      MORPH,
      CAPCHA;
   }

   private class PvPStateTask implements Runnable {
      private final PvPEvent.PvPEventState _to_state;

      public PvPStateTask(PvPEvent.PvPEventState to_state) {
         this._to_state = to_state;
      }

      public void run() {
         try {
            switch(this._to_state) {
            case STANDBY:
               PvPEvent.getInstance().goStandby();
               break;
            case REGISTRATION:
               PvPEvent.getInstance().goRegistration();
               break;
            case PORTING_TO:
               PvPEvent.getInstance().goPortingTo();
               break;
            case PREPARE_TO:
               PvPEvent.getInstance().goPrepareTo();
               break;
            case COMPETITION:
               PvPEvent.getInstance().goCompetition();
               break;
            case WINNER:
               PvPEvent.getInstance().goWinner();
               break;
            case PREPARE_FROM:
               PvPEvent.getInstance().goPrepareFrom();
               break;
            case PORTING_FROM:
               PvPEvent.getInstance().goPortingFrom();
            }
         } catch (Exception var2) {
            PvPEvent._log.warn("PvPEvent: Exception on changing state to " + this._to_state + " state.", var2);
            var2.printStackTrace();
         }

      }
   }

   public static enum PvPEventRule {
      TVT(new PvPEvent.TvTParticipantController()),
      CTF(new PvPEvent.CTFParticipantController()),
      DM(new PvPEvent.DMParticipantController());

      public static PvPEvent.PvPEventRule[] VALUES = values();
      private final PvPEvent.IParticipantController _part_conteiner;

      private PvPEventRule(PvPEvent.IParticipantController conteiner) {
         this._part_conteiner = conteiner;
      }

      public PvPEvent.IParticipantController getParticipantController() {
         return this._part_conteiner;
      }
   }

   private static class DMParticipantController implements PvPEvent.IParticipantController {
      private Map<Integer, AtomicInteger> _kills;
      private static final String TITLE_VAR = "pvp_dm_title";
      private ScheduledFuture<?> _rankBroadcastTask;
      private Reflection _reflection;
      private int _instance_id;
      private String ZONE_DEFAULT;
      private String ZONE_SPAWN;
      private String RET_LOC_VAR;
      private Zone _default_zone;
      private Zone _spawn_zone;
      private Map<Integer, List<Effect>> _saveEffects;

      private DMParticipantController() {
         this._reflection = null;
         this._instance_id = 0;
         this.ZONE_DEFAULT = "[pvp_%d_dm_default]";
         this.ZONE_SPAWN = "[pvp_%d_dm_spawn]";
         this.RET_LOC_VAR = "backCoords";
         this._default_zone = null;
         this._spawn_zone = null;
      }

      public void prepareParticipantsTo() {
         this._kills = new ConcurrentHashMap();
         this._saveEffects = new ConcurrentHashMap();
         boolean dispell = PvPEvent.getInstance().config_dispellEffects();
         Iterator var2 = PvPEvent.getInstance().getPlayers().iterator();

         Player player;
         while(var2.hasNext()) {
            player = (Player)var2.next();
            if (!PvPEvent.isDesirePlayer(player)) {
               PvPEvent.getInstance()._participants.remove(player.getObjectId());
               this.OnExit(player);
            }
         }

         var2 = PvPEvent.getInstance().getPlayers().iterator();

         label122:
         while(var2.hasNext()) {
            player = (Player)var2.next();

            try {
               player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
               if (player.isAttackingNow()) {
                  player.abortAttack(true, false);
               }

               if (player.isCastingNow()) {
                  player.abortCast(true, false);
               }

               player.sendActionFailed();
               player.stopMove();
               player.sitDown((StaticObjectInstance)null);
               player.block();
               Set<Effect> effects = new HashSet();
               if (dispell) {
                  effects.addAll(player.getEffectList().getAllEffects());
                  if (player.getPet() != null) {
                     player.getPet().getEffectList().stopAllEffects();
                  }
               }

               boolean updateSkills = false;
               if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                  player.getClan().disableSkills(player);
               }

               for(int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                  Skill skill = player.getKnownSkill(Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i]);
                  if (skill != null) {
                     if (skill.isToggle()) {
                        List<Effect> effectsBySkill = player.getEffectList().getEffectsBySkill(skill);
                        if (effectsBySkill != null && !effectsBySkill.isEmpty()) {
                           effects.addAll(effectsBySkill);
                        }
                     }

                     player.addUnActiveSkill(skill);
                     updateSkills = true;
                  }
               }

               Iterator var12 = effects.iterator();

               while(true) {
                  Effect $effect;
                  while(true) {
                     if (!var12.hasNext()) {
                        if (updateSkills) {
                           player.sendPacket(new SkillCoolTime(player));
                           player.updateStats();
                           player.updateEffectIcons();
                        }

                        if (!Config.PVP_EVENTS_MAGE_BUFF.isEmpty() || !Config.PVP_EVENTS_WARRIOR_BUFF.isEmpty()) {
                           var12 = (player.isMageClass() ? Config.PVP_EVENTS_MAGE_BUFF : Config.PVP_EVENTS_WARRIOR_BUFF).iterator();

                           while(var12.hasNext()) {
                              Pair<Integer, Integer> pair = (Pair)var12.next();
                              SkillTable.getInstance().getInfo((Integer)pair.getLeft(), (Integer)pair.getRight()).getEffects(player, player, false, false);
                           }
                        }

                        player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
                        player.setCurrentCp((double)player.getMaxCp());
                        player.setVar("pvp_dm_title", player.getTitle() != null ? player.getTitle() : "", -1L);
                        this._kills.put(player.getObjectId(), new AtomicInteger(0));
                        this.updateTitle(player, 0);
                        continue label122;
                     }

                     $effect = (Effect)var12.next();
                     Skill skill = $effect.getSkill();
                     if (skill.isToggle()) {
                        break;
                     }

                     List<Effect> savedEffects = (List)this._saveEffects.get(player.getObjectId());
                     if (savedEffects == null) {
                        this._saveEffects.put(player.getObjectId(), savedEffects = new ArrayList());
                     }

                     Effect effect = $effect.getTemplate().getEffect(new Env($effect.getEffector(), $effect.getEffected(), skill));
                     if (effect.isSaveable()) {
                        effect.setCount($effect.getCount());
                        effect.setPeriod($effect.getCount() == 1 ? $effect.getPeriod() - $effect.getTime() : $effect.getPeriod());
                        ((List)savedEffects).add(effect);
                        break;
                     }
                  }

                  $effect.exit();
               }
            } catch (Exception var11) {
               var11.printStackTrace();
            }
         }

      }

      private void updateTitle(Player player, int kills) {
         player.setTransformationTitle(String.format("Kills: %d", kills));
         player.setTitle(player.getTransformationTitle());
         player.broadcastPacket(new L2GameServerPacket[]{new NickNameChanged(player)});
      }

      public void prepareParticipantsFrom() {
         try {
            boolean dispell_after = PvPEvent.getInstance().config_dispellEffectsAfter();
            Iterator var2 = PvPEvent.getInstance().getPlayers().iterator();

            while(var2.hasNext()) {
               Player player = (Player)var2.next();

               try {
                  player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                  if (player.isAttackingNow()) {
                     player.abortAttack(true, false);
                  }

                  if (player.isCastingNow()) {
                     player.abortCast(true, false);
                  }

                  player.sendActionFailed();
                  player.stopMove();
                  player.sitDown((StaticObjectInstance)null);
                  player.block();
                  if (dispell_after) {
                     player.getEffectList().stopAllEffects();
                  }

                  if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                     player.getClan().enableSkills(player);
                  }

                  for(int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                     int id = Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i];
                     if (player.isUnActiveSkill(id)) {
                        Skill skill = player.getKnownSkill(id);
                        if (skill != null) {
                           player.removeUnActiveSkill(skill);
                        }
                     }
                  }

                  List<Effect> savedEffects = (List)this._saveEffects.get(player.getObjectId());
                  if (savedEffects != null) {
                     Iterator var13 = savedEffects.iterator();

                     while(var13.hasNext()) {
                        Effect effect = (Effect)var13.next();
                        if (player.getEffectList().getEffectsBySkill(effect.getSkill()) == null) {
                           player.getEffectList().addEffect(effect);
                        }
                     }
                  }

                  player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp(), true);
                  player.setCurrentCp((double)player.getMaxCp());
                  if (PvPEvent.getInstance().config_hideIdentiti() && player.getTransformation() == 0) {
                     player.setTransformationName((String)null);
                     player.setTransformationTitle((String)null);
                  }

                  String title = player.getVar("pvp_dm_title");
                  if (title != null) {
                     player.setTitle(title);
                     player.unsetVar("pvp_dm_title");
                  }

                  player.sendUserInfo(true);
               } catch (Exception var10) {
                  var10.printStackTrace();
               }
            }
         } finally {
            this._kills.clear();
            if (this._saveEffects != null) {
               this._saveEffects.clear();
            }

            this._kills = null;
            this._saveEffects = null;
         }

      }

      public void initParticipant() {
         boolean isBuffProtection = PvPEvent.getInstance().config_isBuffProtection();
         Iterator var2 = PvPEvent.getInstance().getPlayers().iterator();

         while(var2.hasNext()) {
            Player player = (Player)var2.next();
            player.addListener(PvPEvent.getInstance()._dieListner);
            player.addListener(PvPEvent.getInstance()._exitListner);
            player.setResurectProhibited(true);
            player.unblock();
            player.standUp();
            if (isBuffProtection) {
               PvPEvent.BUFF_PROTECTION_EFFECT.getEffects(player, player, false, false, false);
            }
         }

         this._rankBroadcastTask = ThreadPoolManager.getInstance().schedule(new PvPEvent.DMParticipantController.RankBroadcastTask(this), 20000L);
      }

      public void doneParicipant() {
         if (this._rankBroadcastTask != null) {
            this._rankBroadcastTask.cancel(true);
            this._rankBroadcastTask = null;
         }

         Iterator var1 = PvPEvent.getInstance().getPlayers().iterator();

         while(var1.hasNext()) {
            Player player = (Player)var1.next();
            player.removeListener(PvPEvent.getInstance()._dieListner);
            player.removeListener(PvPEvent.getInstance()._exitListner);
            player.setResurectProhibited(false);
            player.unblock();
            if (player.isDead()) {
               player.doRevive(100.0D);
            }

            player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp(), true);
            player.setCurrentCp((double)player.getMaxCp());
            player.standUp();
         }

      }

      public void portParticipantsTo() {
         int playerCnt = 0;

         Player player;
         for(Iterator var2 = PvPEvent.getInstance().getPlayers().iterator(); var2.hasNext(); player.teleToLocation(this.getRandomSpawnLoc(), this.getReflection())) {
            player = (Player)var2.next();
            player.setVar(this.RET_LOC_VAR, player.getLoc().toXYZString(), -1L);
            if (player.getParty() != null) {
               player.getParty().removePartyMember(player, false);
            }

            if (PvPEvent.getInstance().config_hideIdentiti()) {
               Object[] var10002 = new Object[1];
               ++playerCnt;
               var10002[0] = playerCnt;
               player.setTransformationName(String.format("Player %d", var10002));
            }
         }

      }

      public void portParticipantsBack() {
         Iterator var1 = PvPEvent.getInstance().getPlayers().iterator();

         while(var1.hasNext()) {
            Player player = (Player)var1.next();
            if (PvPEvent.getInstance().config_hideIdentiti() && player.getTransformation() == 0) {
               player.setTransformationName((String)null);
            }

            String sloc = player.getVar(this.RET_LOC_VAR);
            if (sloc != null) {
               player.unsetVar(this.RET_LOC_VAR);
               player.teleToLocation(Location.parseLoc(sloc), ReflectionManager.DEFAULT);
            } else {
               player.teleToClosestTown();
            }
         }

      }

      public void initReflection() {
         this._instance_id = PvPEvent.getInstance().getNewReflectionId();
         InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(this._instance_id);
         this.ZONE_DEFAULT = String.format("[pvp_%d_dm_default]", this._instance_id);
         this.ZONE_SPAWN = String.format("[pvp_%d_dm_spawn]", this._instance_id);
         this._reflection = new Reflection();
         this._reflection.init(instantZone);
         this._default_zone = this._reflection.getZone(this.ZONE_DEFAULT);
         this._default_zone.addListener(PvPEvent.getInstance()._zoneListner);
         this._spawn_zone = this._reflection.getZone(this.ZONE_SPAWN);
      }

      private Location getRandomSpawnLoc() {
         return this._spawn_zone.getTerritory().getRandomLoc(this._reflection.getGeoIndex());
      }

      public void doneReflection() {
         this._default_zone.removeListener(PvPEvent.getInstance()._zoneListner);
         this._default_zone = null;
         this._spawn_zone = null;
         this._reflection.collapse();
         this._reflection = null;
      }

      public Reflection getReflection() {
         return this._reflection;
      }

      public void OnPlayerDied(Player target, Player killer) {
         if (target != null && killer != null && this._kills.containsKey(target.getObjectId()) && this._kills.containsKey(killer.getObjectId())) {
            int kcntp = false;
            AtomicInteger tcnt = (AtomicInteger)this._kills.get(target.getObjectId());
            AtomicInteger kcnt = (AtomicInteger)this._kills.get(killer.getObjectId());
            int kcntp = kcnt.addAndGet(tcnt.getAndSet(0) + 1);
            this.updateTitle(target, 0);
            this.updateTitle(killer, kcntp);
         }

         ThreadPoolManager.getInstance().schedule(PvPEvent.getInstance().new TeleportAndReviveTask(target, this.getRandomSpawnLoc(), this.getReflection()), (long)(PvPEvent.getInstance().config_ReviveDelay() * 1000));
      }

      public void OnEnter(Player player, Zone zone) {
         if (player != null && !this._kills.containsKey(player.getObjectId())) {
            if (PvPEvent.getInstance().config_hideIdentiti() && player.getTransformation() == 0) {
               player.setTransformationName((String)null);
            }

            player.teleToClosestTown();
         }

      }

      public void OnLeave(Player player, Zone zone) {
         if (player != null && !this._default_zone.checkIfInZone(player.getX(), player.getY(), player.getZ(), this.getReflection()) && this._kills.containsKey(player.getObjectId())) {
            double radian = 6.283185307179586D - PositionUtils.convertHeadingToRadian(player.getHeading());
            double cos = Math.cos(radian);
            double sin = Math.sin(radian);
            Location randomLoc = zone.getTerritory().getRandomLoc(player.getGeoIndex());

            for(int i = 32; i < 512; i += 32) {
               int x = (int)Math.floor((double)player.getX() - (double)i * cos);
               int y = (int)Math.floor((double)player.getY() + (double)i * sin);
               if (zone.getTerritory().isInside(x, y)) {
                  randomLoc.set(x, y, player.getZ(), player.getHeading());
                  break;
               }
            }

            ThreadPoolManager.getInstance().schedule(PvPEvent.getInstance().new TeleportTask(player, randomLoc.correctGeoZ(), this.getReflection()), 3000L);
         }

      }

      public void OnExit(Player player) {
         String title = player.getVar("pvp_dm_title");
         if (title != null) {
            player.setTitle(player.getVar("pvp_dm_title"));
            player.unsetVar("pvp_dm_title");
         }

         this._kills.remove(player.getObjectId());
      }

      public void OnTeleport(Player player, int x, int y, int z, Reflection r) {
         if (player != null && !this._default_zone.checkIfInZone(x, y, z, this.getReflection())) {
            Location loc = this.getRandomSpawnLoc();
            if (loc != null) {
               ThreadPoolManager.getInstance().schedule(PvPEvent.getInstance().new TeleportTask(player, loc, this.getReflection()), 3000L);
            }
         }

      }

      private void rewardPerKill() {
         int itemId = PvPEvent.getInstance().config_ItemPerKill();
         if (itemId > 0) {
            Iterator var2 = this._kills.entrySet().iterator();

            while(var2.hasNext()) {
               Entry<Integer, AtomicInteger> e = (Entry)var2.next();
               int kills = ((AtomicInteger)e.getValue()).get();
               int killerOid = (Integer)e.getKey();
               Player player = GameObjectsStorage.getPlayer(killerOid);
               if (kills > 0 && player != null) {
                  Functions.addItem(player, itemId, (long)kills);
               }
            }

         }
      }

      public void MakeWinner() {
         this.giveListenerParticipate();
         int max_oid = 0;
         int max = Integer.MIN_VALUE;
         Iterator var3 = this._kills.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<Integer, AtomicInteger> e = (Entry)var3.next();
            int val = ((AtomicInteger)e.getValue()).get();
            if (val > max) {
               max_oid = (Integer)e.getKey();
               max = val;
            }
         }

         if (max_oid != 0 && max > 0) {
            Player winner = GameObjectsStorage.getPlayer(max_oid);
            Iterator var7 = PvPEvent.getInstance().config_RewardTopItemIdAndAmount().iterator();

            while(var7.hasNext()) {
               Pair<ItemTemplate, Long> rewardItemInfo = (Pair)var7.next();
               Functions.addItem(winner, ((ItemTemplate)rewardItemInfo.getLeft()).getItemId(), (Long)rewardItemInfo.getRight());
            }

            if (PvPEvent.getInstance().config_RewardHeroHours() > 0) {
               GlobalServices.makeCustomHero(winner, (long)(Config.PVP_EVENT_GIVE_HERO_STATUS * 60) * 60L);
            }

            PvPEvent.getInstance().broadcast(new ExEventMatchMessage("'" + winner.getName() + "' winns!"), (new SystemMessage(SystemMsg.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH)).addName(winner));
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.PlayerS1WonTheDMGame", new String[]{winner.getName()});
            winner.getListeners().onDeathMatchEvent(true, 1);
         } else {
            PvPEvent.getInstance().broadcast(new ExEventMatchMessage("Tie"), new SystemMessage(SystemMsg.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE));
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TheDMGameEndedInATie", ArrayUtils.EMPTY_STRING_ARRAY);
         }

         this.rewardPerKill();
      }

      private void giveListenerParticipate() {
         List<Entry<Integer, AtomicInteger>> entryList = new ArrayList(this._kills.entrySet());
         Collections.sort(entryList, new Comparator<Entry<Integer, AtomicInteger>>() {
            public int compare(Entry<Integer, AtomicInteger> e1, Entry<Integer, AtomicInteger> e2) {
               return Integer.compare(((AtomicInteger)e2.getValue()).get(), ((AtomicInteger)e1.getValue()).get());
            }
         });
         int place = 1;

         for(Iterator var3 = entryList.iterator(); var3.hasNext(); ++place) {
            Entry<Integer, AtomicInteger> entry = (Entry)var3.next();
            int objectId = (Integer)entry.getKey();
            Player player = GameObjectsStorage.getPlayer(objectId);
            if (player != null) {
               player.getListeners().onDeathMatchEvent(false, place);
            }
         }

      }

      // $FF: synthetic method
      DMParticipantController(Object x0) {
         this();
      }

      private class RankBroadcastTask implements Runnable {
         private PvPEvent.DMParticipantController _controller;

         public RankBroadcastTask(PvPEvent.DMParticipantController controller) {
            this._controller = controller;
         }

         public void run() {
            if (PvPEvent.getInstance().getState() == PvPEvent.PvPEventState.COMPETITION) {
               this._controller._rankBroadcastTask = ThreadPoolManager.getInstance().schedule(this, 20000L);
            }
         }
      }
   }

   private static class CTFParticipantController implements PvPEvent.IParticipantController {
      private Map<Integer, AtomicInteger> _red_team;
      private Map<Integer, AtomicInteger> _blue_team;
      private AtomicInteger _red_points;
      private AtomicInteger _blue_points;
      private static final String TITLE_VAR = "pvp_ctf_title";
      private String ZONE_DEFAULT;
      private String ZONE_BLUE;
      private String ZONE_RED;
      private String RET_LOC_VAR;
      private Reflection _reflection;
      private Zone _default_zone;
      private Zone _blue_zone;
      private Zone _red_zone;
      private int _instance_id;
      private Map<Integer, List<Effect>> _saveEffects;
      private static final int BLUE_FLAG_NPC = 32027;
      private static final int RED_FLAG_NPC = 32027;
      private static final int BLUE_FLAG_ITEM = 6718;
      private static final int RED_FLAG_ITEM = 6718;
      private WeakReference<PvPEvent.CTFParticipantController.CTFFlagInstance> _redFlag;
      private WeakReference<PvPEvent.CTFParticipantController.CTFFlagInstance> _blueFlag;
      private ScheduledFuture<?> _rankBroadcastTask;

      private CTFParticipantController() {
         this.ZONE_DEFAULT = "[pvp_%d_ctf_default]";
         this.ZONE_BLUE = "[pvp_%d_ctf_spawn_blue]";
         this.ZONE_RED = "[pvp_%d_ctf_spawn_red]";
         this.RET_LOC_VAR = "backCoords";
         this._reflection = null;
         this._default_zone = null;
         this._blue_zone = null;
         this._red_zone = null;
         this._instance_id = 0;
      }

      public void prepareParticipantsTo() {
         this._red_team = new ConcurrentHashMap();
         this._blue_team = new ConcurrentHashMap();
         this._saveEffects = new ConcurrentHashMap();
         this._red_points = new AtomicInteger(0);
         this._blue_points = new AtomicInteger(0);
         TeamType team_type = TeamType.BLUE;
         Iterator var2 = PvPEvent.getInstance().getPlayers().iterator();

         while(var2.hasNext()) {
            Player player = (Player)var2.next();
            if (!PvPEvent.isDesirePlayer(player)) {
               PvPEvent.getInstance()._participants.remove(player.getObjectId());
               this.OnExit(player);
            }
         }

         boolean dispell = PvPEvent.getInstance().config_dispellEffects();
         Iterator var14 = PvPEvent.getInstance().getPlayers().iterator();

         label125:
         while(var14.hasNext()) {
            Player player = (Player)var14.next();

            try {
               player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
               if (player.isAttackingNow()) {
                  player.abortAttack(true, false);
               }

               if (player.isCastingNow()) {
                  player.abortCast(true, false);
               }

               player.sendActionFailed();
               player.stopMove();
               player.sitDown((StaticObjectInstance)null);
               player.block();
               boolean updateSkills = false;
               if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                  player.getClan().disableSkills(player);
               }

               List<Effect> effects = new LinkedList();

               for(int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                  Skill skill = player.getKnownSkill(Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i]);
                  if (skill != null) {
                     if (skill.isToggle()) {
                        List<Effect> effectsBySkill = player.getEffectList().getEffectsBySkill(skill);
                        if (effectsBySkill != null && !effectsBySkill.isEmpty()) {
                           effects.addAll(effectsBySkill);
                        }
                     }

                     player.addUnActiveSkill(skill);
                     updateSkills = true;
                  }
               }

               if (dispell) {
                  effects.addAll(player.getEffectList().getAllEffects());
                  if (player.getPet() != null) {
                     player.getPet().getEffectList().stopAllEffects();
                  }
               }

               Iterator var15 = effects.iterator();

               while(true) {
                  Effect $effect;
                  while(true) {
                     if (!var15.hasNext()) {
                        if (!Config.PVP_EVENTS_MAGE_BUFF.isEmpty() || !Config.PVP_EVENTS_WARRIOR_BUFF.isEmpty()) {
                           var15 = (player.isMageClass() ? Config.PVP_EVENTS_MAGE_BUFF : Config.PVP_EVENTS_WARRIOR_BUFF).iterator();

                           while(var15.hasNext()) {
                              Pair<Integer, Integer> pair = (Pair)var15.next();
                              SkillTable.getInstance().getInfo((Integer)pair.getLeft(), (Integer)pair.getRight()).getEffects(player, player, false, false);
                           }
                        }

                        if (updateSkills) {
                           player.sendPacket(new SkillCoolTime(player));
                           player.updateStats();
                           player.updateEffectIcons();
                        }

                        player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
                        player.setCurrentCp((double)player.getMaxCp());
                        player.sendChanges();
                        player.setVar("pvp_ctf_title", player.getTitle() != null ? player.getTitle() : "", -1L);
                        if (team_type == TeamType.BLUE) {
                           player.setTeam(TeamType.BLUE);
                           this._blue_team.put(player.getObjectId(), new AtomicInteger(0));
                           team_type = TeamType.RED;
                        } else {
                           player.setTeam(TeamType.RED);
                           this._red_team.put(player.getObjectId(), new AtomicInteger(0));
                           team_type = TeamType.BLUE;
                        }
                        continue label125;
                     }

                     $effect = (Effect)var15.next();
                     Skill skill = $effect.getSkill();
                     if (skill.isToggle()) {
                        break;
                     }

                     List<Effect> savedEffects = (List)this._saveEffects.get(player.getObjectId());
                     if (savedEffects == null) {
                        this._saveEffects.put(player.getObjectId(), savedEffects = new ArrayList());
                     }

                     Effect effect = $effect.getTemplate().getEffect(new Env($effect.getEffector(), $effect.getEffected(), skill));
                     if (effect.isSaveable()) {
                        effect.setCount($effect.getCount());
                        effect.setPeriod($effect.getCount() == 1 ? $effect.getPeriod() - $effect.getTime() : $effect.getPeriod());
                        ((List)savedEffects).add(effect);
                        break;
                     }
                  }

                  $effect.exit();
               }
            } catch (Exception var12) {
               var12.printStackTrace();
            }
         }

      }

      public void prepareParticipantsFrom() {
         try {
            ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._redFlag.get()).removeFlag((Player)null);
            ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._blueFlag.get()).removeFlag((Player)null);
            boolean dispell_after = PvPEvent.getInstance().config_dispellEffectsAfter();

            Player player;
            for(Iterator var2 = PvPEvent.getInstance().getPlayers().iterator(); var2.hasNext(); player.sendUserInfo(true)) {
               player = (Player)var2.next();
               player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
               if (player.isAttackingNow()) {
                  player.abortAttack(true, false);
               }

               if (player.isCastingNow()) {
                  player.abortCast(true, false);
               }

               player.sendActionFailed();
               player.stopMove();
               player.sitDown((StaticObjectInstance)null);
               player.block();
               if (dispell_after) {
                  player.getEffectList().stopAllEffects();
               }

               if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                  player.getClan().enableSkills(player);
               }

               for(int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                  int id = Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i];
                  if (player.isUnActiveSkill(id)) {
                     Skill skill = player.getKnownSkill(id);
                     if (skill != null) {
                        player.removeUnActiveSkill(skill);
                     }
                  }
               }

               List<Effect> savedEffects = (List)this._saveEffects.get(player.getObjectId());
               if (savedEffects != null) {
                  Iterator var11 = savedEffects.iterator();

                  while(var11.hasNext()) {
                     Effect effect = (Effect)var11.next();
                     if (player.getEffectList().getEffectsBySkill(effect.getSkill()) == null) {
                        player.getEffectList().addEffect(effect);
                     }
                  }
               }

               player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
               player.setCurrentCp((double)player.getMaxCp());
               player.sendChanges();
               if (PvPEvent.getInstance().config_hideIdentiti() && player.getTransformation() == 0) {
                  player.setTransformationName((String)null);
                  player.setTransformationTitle((String)null);
               }

               player.setTeam(TeamType.NONE);
               String title = player.getVar("pvp_ctf_title");
               if (title != null) {
                  player.setTitle(title);
                  player.unsetVar("pvp_ctf_title");
               }
            }
         } finally {
            this._red_team.clear();
            this._blue_team.clear();
            if (this._saveEffects != null) {
               this._saveEffects.clear();
            }

            this._red_team = null;
            this._blue_team = null;
            this._red_points = null;
            this._blue_points = null;
            this._saveEffects = null;
         }

      }

      public void initParticipant() {
         boolean isBuffProtection = PvPEvent.getInstance().config_isBuffProtection();
         Iterator var2 = PvPEvent.getInstance().getPlayers().iterator();

         while(var2.hasNext()) {
            Player player = (Player)var2.next();
            player.addListener(PvPEvent.getInstance()._dieListner);
            player.addListener(PvPEvent.getInstance()._exitListner);
            player.setResurectProhibited(true);
            player.unblock();
            player.standUp();
            if (isBuffProtection) {
               PvPEvent.BUFF_PROTECTION_EFFECT.getEffects(player, player, false, false, false);
            }
         }

      }

      public void doneParicipant() {
         if (this._rankBroadcastTask != null) {
            this._rankBroadcastTask.cancel(true);
            this._rankBroadcastTask = null;
         }

         Player player;
         for(Iterator var1 = PvPEvent.getInstance().getPlayers().iterator(); var1.hasNext(); player.standUp()) {
            player = (Player)var1.next();
            player.removeListener(PvPEvent.getInstance()._dieListner);
            player.removeListener(PvPEvent.getInstance()._exitListner);
            player.setResurectProhibited(false);
            player.unblock();
            if (player.isDead()) {
               player.doRevive(100.0D);
               player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
               player.setCurrentCp((double)player.getMaxCp());
            }
         }

      }

      public void portParticipantsTo() {
         int redCnt = 0;
         int blueCnt = 0;
         Iterator var3 = PvPEvent.getInstance().getPlayers().iterator();

         while(true) {
            while(var3.hasNext()) {
               Player player = (Player)var3.next();
               TeamType playerTeam = player.getTeam();
               if (playerTeam != TeamType.BLUE && playerTeam != TeamType.RED) {
                  PvPEvent.getInstance()._participants.remove(player.getObjectId());
                  this.OnExit(player);
               } else {
                  player.setVar(this.RET_LOC_VAR, player.getLoc().toXYZString(), -1L);
                  if (player.getParty() != null) {
                     player.getParty().removePartyMember(player, false);
                  }

                  if (PvPEvent.getInstance().config_hideIdentiti()) {
                     Object[] var10002;
                     switch(playerTeam) {
                     case RED:
                        var10002 = new Object[1];
                        ++redCnt;
                        var10002[0] = redCnt;
                        player.setTransformationName(String.format("Red %d", var10002));
                        break;
                     case BLUE:
                        var10002 = new Object[1];
                        ++blueCnt;
                        var10002[0] = blueCnt;
                        player.setTransformationName(String.format("Blue %d", var10002));
                     }
                  }

                  player.teleToLocation(this.getRandomTeamLoc(playerTeam), this.getReflection());
               }
            }

            return;
         }
      }

      public void portParticipantsBack() {
         Iterator var1 = PvPEvent.getInstance().getPlayers().iterator();

         while(var1.hasNext()) {
            Player player = (Player)var1.next();
            String sloc = player.getVar(this.RET_LOC_VAR);
            if (sloc != null) {
               player.unsetVar(this.RET_LOC_VAR);
               player.teleToLocation(Location.parseLoc(sloc), ReflectionManager.DEFAULT);
            } else {
               player.teleToClosestTown();
            }
         }

      }

      public void initReflection() {
         this._instance_id = PvPEvent.getInstance().getNewReflectionId();
         InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(this._instance_id);
         this.ZONE_DEFAULT = String.format("[pvp_%d_ctf_default]", this._instance_id);
         this.ZONE_BLUE = String.format("[pvp_%d_ctf_spawn_blue]", this._instance_id);
         this.ZONE_RED = String.format("[pvp_%d_ctf_spawn_red]", this._instance_id);
         this._reflection = new Reflection();
         this._reflection.init(instantZone);
         this._default_zone = this._reflection.getZone(this.ZONE_DEFAULT);
         this._blue_zone = this._reflection.getZone(this.ZONE_BLUE);
         this._red_zone = this._reflection.getZone(this.ZONE_RED);
         this._default_zone.addListener(PvPEvent.getInstance()._zoneListner);
         this._blue_zone.addListener(PvPEvent.getInstance()._zoneListner);
         this._red_zone.addListener(PvPEvent.getInstance()._zoneListner);
         PvPEvent.CTFParticipantController.CTFFlagInstance red_flag = new PvPEvent.CTFParticipantController.CTFFlagInstance(TeamType.RED, this);
         red_flag.setSpawnedLoc(this.getRandomTeamLoc(TeamType.RED));
         red_flag.setReflection(this.getReflection());
         red_flag.setCurrentHpMp((double)red_flag.getMaxHp(), (double)red_flag.getMaxMp(), true);
         red_flag.spawnMe(red_flag.getSpawnedLoc());
         this._redFlag = new WeakReference(red_flag);
         PvPEvent.CTFParticipantController.CTFFlagInstance blue_flag = new PvPEvent.CTFParticipantController.CTFFlagInstance(TeamType.BLUE, this);
         blue_flag.setSpawnedLoc(this.getRandomTeamLoc(TeamType.BLUE));
         blue_flag.setReflection(this.getReflection());
         blue_flag.setCurrentHpMp((double)blue_flag.getMaxHp(), (double)blue_flag.getMaxMp(), true);
         blue_flag.spawnMe(blue_flag.getSpawnedLoc());
         this._blueFlag = new WeakReference(blue_flag);
      }

      public void doneReflection() {
         if (this._blueFlag.get() != null) {
            ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._blueFlag.get()).destroy();
            this._blueFlag.clear();
         }

         if (this._redFlag.get() != null) {
            ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._redFlag.get()).destroy();
            this._redFlag.clear();
         }

         this._redFlag = null;
         this._blueFlag = null;
         this._default_zone.removeListener(PvPEvent.getInstance()._zoneListner);
         this._blue_zone.removeListener(PvPEvent.getInstance()._zoneListner);
         this._red_zone.removeListener(PvPEvent.getInstance()._zoneListner);
         this._default_zone = null;
         this._blue_zone = null;
         this._red_zone = null;
         this._reflection.collapse();
         this._reflection = null;
      }

      public Reflection getReflection() {
         return this._reflection;
      }

      public void OnPlayerDied(Player target, Player killer) {
         ThreadPoolManager.getInstance().schedule(PvPEvent.getInstance().new TeleportAndReviveTask(target, this.getRandomTeamLoc(target.getTeam()), this.getReflection()), (long)(PvPEvent.getInstance().config_ReviveDelay() * 1000));
      }

      public void OnEnter(Player player, Zone zone) {
         if (player != null && !player.isDead()) {
            if (zone == this._default_zone && player.getTeam() != TeamType.BLUE && player.getTeam() != TeamType.RED) {
               player.teleToClosestTown();
               PvPEvent._log.warn("PvPEvent.CTF: '" + player.getName() + "' in zone.");
            } else if (zone == this._blue_zone && player.getTeam() == TeamType.BLUE && player.getObjectId() == ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._redFlag.get()).getOwnerOid()) {
               ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._redFlag.get()).removeFlag((Player)null);
               this._blue_points.incrementAndGet();
            } else if (zone == this._red_zone && player.getTeam() == TeamType.RED && player.getObjectId() == ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._blueFlag.get()).getOwnerOid()) {
               ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._blueFlag.get()).removeFlag((Player)null);
               this._red_points.incrementAndGet();
            }
         }

      }

      public void OnLeave(Player player, Zone zone) {
         if (player != null && !this._default_zone.checkIfInZone(player.getX(), player.getY(), player.getZ(), this.getReflection()) && zone == this._default_zone) {
            if (player.getTeam() != TeamType.BLUE && player.getTeam() != TeamType.RED) {
               if (PvPEvent.getInstance().config_hideIdentiti() && player.getTransformation() == 0) {
                  player.setTransformationName((String)null);
               }

               player.teleToClosestTown();
               return;
            }

            double radian = 6.283185307179586D - PositionUtils.convertHeadingToRadian(player.getHeading());
            double cos = Math.cos(radian);
            double sin = Math.sin(radian);
            Location randomLoc = zone.getTerritory().getRandomLoc(player.getGeoIndex());

            for(int i = 32; i < 512; i += 32) {
               int x = (int)Math.floor((double)player.getX() - (double)i * cos);
               int y = (int)Math.floor((double)player.getY() + (double)i * sin);
               if (zone.getTerritory().isInside(x, y)) {
                  randomLoc.set(x, y, player.getZ(), player.getHeading());
                  break;
               }
            }

            ThreadPoolManager.getInstance().schedule(PvPEvent.getInstance().new TeleportTask(player, randomLoc.correctGeoZ(), this.getReflection()), 3000L);
            if (player.getObjectId() == ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._blueFlag.get()).getOwnerOid()) {
               ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._blueFlag.get()).removeFlag(player);
            } else if (player.getObjectId() == ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._redFlag.get()).getOwnerOid()) {
               ((PvPEvent.CTFParticipantController.CTFFlagInstance)this._redFlag.get()).removeFlag(player);
            }
         }

      }

      public void OnExit(Player player) {
         if (this._blue_team.containsKey(player.getObjectId())) {
            this._blue_team.remove(player.getObjectId());
         } else if (this._red_team.containsKey(player.getObjectId())) {
            this._red_team.remove(player.getObjectId());
         }

         if (player.getTransformation() == 0) {
            player.setTransformationName((String)null);
            player.setTransformationTitle((String)null);
         }

         String title = player.getVar("pvp_ctf_title");
         if (title != null) {
            player.setTitle(player.getVar("pvp_ctf_title"));
            player.unsetVar("pvp_ctf_title");
         }

      }

      public void OnTeleport(Player player, int x, int y, int z, Reflection r) {
         if (player != null && !this._default_zone.checkIfInZone(x, y, z, r)) {
            Location loc = this.getRandomTeamLoc(player.getTeam());
            if (loc != null) {
               ThreadPoolManager.getInstance().schedule(PvPEvent.getInstance().new TeleportTask(player, loc, this.getReflection()), 3000L);
            }
         }

      }

      private void reward(Map<Integer, AtomicInteger> team, List<Pair<ItemTemplate, Long>> rewardList) {
         Iterator var3 = team.entrySet().iterator();

         while(true) {
            Player player;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               Entry<Integer, AtomicInteger> e = (Entry)var3.next();
               int oid = (Integer)e.getKey();
               player = GameObjectsStorage.getPlayer(oid);
            } while(player == null);

            Iterator var7 = rewardList.iterator();

            while(var7.hasNext()) {
               Pair<ItemTemplate, Long> rewardInfo = (Pair)var7.next();
               Functions.addItem(player, ((ItemTemplate)rewardInfo.getLeft()).getItemId(), (Long)rewardInfo.getRight());
            }
         }
      }

      public void MakeWinner() {
         int blue_pnt = this._blue_points.get();
         int red_pnt = this._red_points.get();
         giveListenerParticipate(this._blue_team, this._red_team);
         if (blue_pnt > red_pnt) {
            this.reward(this._blue_team, PvPEvent.getInstance().config_RewardWinnerTeamItemIdAndAmount());
            this.reward(this._red_team, PvPEvent.getInstance().config_RewardLooserTeamItemIdAndAmount());
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TeamBlueWonTheCTFGameCountIsS1S2", new String[]{String.valueOf(blue_pnt), String.valueOf(red_pnt)});
            giveListenerWinnerTeam(this._blue_team);
         } else if (blue_pnt < red_pnt) {
            this.reward(this._red_team, PvPEvent.getInstance().config_RewardWinnerTeamItemIdAndAmount());
            this.reward(this._blue_team, PvPEvent.getInstance().config_RewardLooserTeamItemIdAndAmount());
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TeamRedWonTheCTFGameCountIsS1S2", new String[]{String.valueOf(red_pnt), String.valueOf(blue_pnt)});
            giveListenerWinnerTeam(this._red_team);
         } else if (blue_pnt == red_pnt) {
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TheCTFGameEndedInATie", ArrayUtils.EMPTY_STRING_ARRAY);
            this.reward(this._red_team, PvPEvent.getInstance().config_RewardTieItemIdAndAmount());
            this.reward(this._blue_team, PvPEvent.getInstance().config_RewardTieItemIdAndAmount());
         }

         PvPEvent.getInstance().broadcast(ExEventMatchMessage.FINISH);
      }

      private static void giveListenerParticipate(Map<Integer, AtomicInteger> team1, Map<Integer, AtomicInteger> team2) {
         Iterator var2 = team1.entrySet().iterator();

         Entry e;
         int oid;
         Player player;
         while(var2.hasNext()) {
            e = (Entry)var2.next();
            oid = (Integer)e.getKey();
            player = GameObjectsStorage.getPlayer(oid);
            if (player != null) {
               player.getListeners().onCtFEvent(false);
            }
         }

         var2 = team2.entrySet().iterator();

         while(var2.hasNext()) {
            e = (Entry)var2.next();
            oid = (Integer)e.getKey();
            player = GameObjectsStorage.getPlayer(oid);
            if (player != null) {
               player.getListeners().onCtFEvent(false);
            }
         }

      }

      private static void giveListenerWinnerTeam(Map<Integer, AtomicInteger> team) {
         Iterator var1 = team.entrySet().iterator();

         while(var1.hasNext()) {
            Entry<Integer, AtomicInteger> e = (Entry)var1.next();
            int oid = (Integer)e.getKey();
            Player player = GameObjectsStorage.getPlayer(oid);
            if (player != null) {
               player.getListeners().onCtFEvent(true);
            }
         }

      }

      private Location getRandomTeamLoc(TeamType tt) {
         if (tt == TeamType.BLUE) {
            return this._blue_zone.getTerritory().getRandomLoc(this._reflection.getGeoIndex());
         } else {
            return tt == TeamType.RED ? this._red_zone.getTerritory().getRandomLoc(this._reflection.getGeoIndex()) : null;
         }
      }

      // $FF: synthetic method
      CTFParticipantController(Object x0) {
         this();
      }

      private class CTFFlagInstance extends MonsterInstance implements FlagItemAttachment {
         private ItemInstance _flag;
         private final TeamType _team;
         private PvPEvent.CTFParticipantController _controller;

         public CTFFlagInstance(TeamType team, PvPEvent.CTFParticipantController controller) {
            super(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(team == TeamType.BLUE ? 32027 : (team == TeamType.RED ? 32027 : -1)));
            this._team = team;
            this._flag = ItemFunctions.createItem(team == TeamType.BLUE ? 6718 : (team == TeamType.RED ? 6718 : -1));
            this._flag.setAttachment(this);
            this._flag.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, false);
            this._controller = controller;
         }

         public void destroy() {
            Player owner = GameObjectsStorage.getPlayer(this._flag.getOwnerId());
            if (owner != null) {
               owner.getInventory().destroyItem(this._flag);
               owner.sendDisarmMessage(this._flag);
               owner.stopAbnormalEffect(AbnormalEffect.SLEEP);
            }

            this._flag.setAttachment((ItemAttachment)null);
            this._flag.deleteMe();
            this._flag.delete();
            this._flag = null;
            this.deleteMe();
         }

         public boolean isAutoAttackable(Creature attacker) {
            return this.isAttackable(attacker);
         }

         public boolean isAttackable(Creature attacker) {
            return attacker != null && attacker.getTeam() != null && attacker.getTeam() != TeamType.NONE && attacker.getTeam() != this._team;
         }

         protected void onDeath(Creature killer) {
            boolean afkParticipants = PvPEvent.getInstance().config_isAfkParticipants();
            if (this.isAttackable(killer)) {
               Player pkiller = killer.getPlayer();
               if (pkiller != null && (this._team == TeamType.RED && killer.isInZone(this._controller._red_zone) || this._team == TeamType.BLUE && killer.isInZone(this._controller._blue_zone))) {
                  pkiller.getInventory().addItem(this._flag);
                  pkiller.startAbnormalEffect(AbnormalEffect.SLEEP);
                  pkiller.getInventory().equipItem(this._flag);
                  this._flag.getItemStateFlag().set(ItemStateFlags.STATE_CHANGED, false);
                  if (afkParticipants && PvPEvent.AFK_PROTECTION_EFFECT == null) {
                     PvPEvent.AFK_PROTECTION_EFFECT.getEffects(killer, killer, false, false, false);
                  }

                  pkiller.sendPacket(new ExShowScreenMessage((new CustomMessage("events.PvPEvent.TheCTFCaptureTheFlag", pkiller, new Object[0])).toString(), 10000, ScreenMessageAlign.MIDDLE_CENTER, false));
                  this.decayMe();
                  Iterator var4;
                  Integer poid;
                  Player player;
                  ExShowScreenMessage essm;
                  if (this._team == TeamType.RED) {
                     var4 = this._controller._red_team.keySet().iterator();

                     while(var4.hasNext()) {
                        poid = (Integer)var4.next();
                        player = GameObjectsStorage.getPlayer(poid);
                        essm = new ExShowScreenMessage((new CustomMessage("events.PvPEvent.RedTeamCaptureTheFlag", player, new Object[0])).addString(pkiller.getName()).toString(), 10000, ScreenMessageAlign.MIDDLE_CENTER, false);
                        if (player != null) {
                           player.sendPacket(essm);
                        }
                     }
                  } else if (this._team == TeamType.BLUE) {
                     var4 = this._controller._blue_team.keySet().iterator();

                     while(var4.hasNext()) {
                        poid = (Integer)var4.next();
                        player = GameObjectsStorage.getPlayer(poid);
                        essm = new ExShowScreenMessage((new CustomMessage("events.PvPEvent.BlueTeamCaptureTheFlag", player, new Object[0])).addString(pkiller.getName()).toString(), 10000, ScreenMessageAlign.MIDDLE_CENTER, false);
                        if (player != null) {
                           player.sendPacket(essm);
                        }
                     }
                  }

                  return;
               }
            }

            this.setCurrentHpMp((double)this.getMaxHp(), (double)this.getMaxMp(), true);
         }

         public int getOwnerOid() {
            return this._flag.getOwnerId();
         }

         public void removeFlag(Player owner) {
            if (owner == null) {
               owner = GameObjectsStorage.getPlayer(this._flag.getOwnerId());
            }

            if (owner != null) {
               owner.getInventory().removeItem(this._flag);
               owner.sendDisarmMessage(this._flag);
               owner.stopAbnormalEffect(AbnormalEffect.SLEEP);
            }

            this._flag.setOwnerId(0);
            this._flag.delete();
            this.setCurrentHpMp((double)this.getMaxHp(), (double)this.getMaxMp(), true);
            this.spawnMe(this._controller.getRandomTeamLoc(this._team));
         }

         public boolean canPickUp(Player player) {
            return false;
         }

         public void pickUp(Player player) {
         }

         public void setItem(ItemInstance item) {
            if (item != null) {
               item.setCustomFlags(39);
            }

         }

         public void onLogout(Player player) {
            player.getInventory().removeItem(this._flag);
            this._flag.setOwnerId(0);
            this._flag.delete();
            this.setCurrentHpMp((double)this.getMaxHp(), (double)this.getMaxMp(), true);
            this.spawnMe(this._controller.getRandomTeamLoc(this._team));
         }

         public void onDeath(Player owner, Creature killer) {
            owner.getInventory().removeItem(this._flag);
            owner.sendDisarmMessage(this._flag);
            owner.stopAbnormalEffect(AbnormalEffect.SLEEP);
            this._flag.setOwnerId(0);
            this._flag.delete();
            this.setCurrentHpMp((double)this.getMaxHp(), (double)this.getMaxMp(), true);
            this.spawnMe(this._controller.getRandomTeamLoc(this._team));
         }

         public void onEnterPeace(Player owner) {
         }

         public boolean canAttack(Player player) {
            player.sendMessage(new CustomMessage("THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS", player, new Object[0]));
            return false;
         }

         public boolean canCast(Player player, Skill skill) {
            player.sendMessage(new CustomMessage("THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL", player, new Object[0]));
            return false;
         }

         public boolean isEffectImmune() {
            return true;
         }

         public boolean isDebuffImmune() {
            return true;
         }
      }
   }

   private class RankBroadcastTask implements Runnable {
      private PvPEvent.TvTParticipantController _controller;

      public RankBroadcastTask(PvPEvent.TvTParticipantController controller) {
         this._controller = controller;
      }

      public void run() {
         if (PvPEvent.getInstance().getState() == PvPEvent.PvPEventState.COMPETITION) {
            this._controller._rankBroadcastTask = ThreadPoolManager.getInstance().schedule(this, 20000L);
         }
      }
   }

   private static class TvTParticipantController implements PvPEvent.IParticipantController {
      private static final PvPEvent.TvTParticipantController.RankComparator _rankComparator = new PvPEvent.TvTParticipantController.RankComparator();
      private Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> _red_team;
      private Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> _blue_team;
      private static final String TITLE_VAR = "pvp_tvt_title";
      private int _instance_id;
      private String ZONE_DEFAULT;
      private String ZONE_BLUE;
      private String ZONE_RED;
      private String RET_LOC_VAR;
      private Reflection _reflection;
      private Zone _default_zone;
      private AtomicInteger _red_points;
      private AtomicInteger _blue_points;
      private Map<Integer, List<Effect>> _saveEffects;
      private ScheduledFuture<?> _rankBroadcastTask;

      private TvTParticipantController() {
         this._instance_id = 0;
         this.ZONE_DEFAULT = "[pvp_%d_tvt_default]";
         this.ZONE_BLUE = "[pvp_%d_tvt_spawn_blue]";
         this.ZONE_RED = "[pvp_%d_tvt_spawn_red]";
         this.RET_LOC_VAR = "backCoords";
         this._reflection = null;
         this._default_zone = null;
      }

      public int getKills(TeamType team) {
         int result = 0;
         Iterator var3;
         ImmutablePair entry;
         if (team == TeamType.RED) {
            for(var3 = this._red_team.values().iterator(); var3.hasNext(); result += ((AtomicInteger)entry.getLeft()).get()) {
               entry = (ImmutablePair)var3.next();
            }
         }

         if (team == TeamType.BLUE) {
            for(var3 = this._blue_team.values().iterator(); var3.hasNext(); result += ((AtomicInteger)entry.getLeft()).get()) {
               entry = (ImmutablePair)var3.next();
            }
         }

         return result;
      }

      public void prepareParticipantsTo() {
         this._red_team = new ConcurrentHashMap();
         this._blue_team = new ConcurrentHashMap();
         this._red_points = new AtomicInteger(0);
         this._blue_points = new AtomicInteger(0);
         this._saveEffects = new ConcurrentHashMap();
         TeamType team_type = TeamType.BLUE;
         boolean dispell = PvPEvent.getInstance().config_dispellEffects();
         Iterator var3 = PvPEvent.getInstance().getPlayers().iterator();

         Player player;
         while(var3.hasNext()) {
            player = (Player)var3.next();
            if (!PvPEvent.isDesirePlayer(player)) {
               PvPEvent.getInstance()._participants.remove(player.getObjectId());
               this.OnExit(player);
            }
         }

         label119:
         for(var3 = PvPEvent.getInstance().getPlayers().iterator(); var3.hasNext(); this.updateTitle(player, 0)) {
            player = (Player)var3.next();
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            if (player.isAttackingNow()) {
               player.abortAttack(true, false);
            }

            if (player.isCastingNow()) {
               player.abortCast(true, false);
            }

            player.sendActionFailed();
            player.stopMove();
            player.sitDown((StaticObjectInstance)null);
            player.block();
            boolean skillUpdate = false;
            if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
               player.getClan().disableSkills(player);
            }

            Set<Effect> effects = new HashSet();

            for(int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
               Skill skill = player.getKnownSkill(Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i]);
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

            player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
            player.setCurrentCp((double)player.getMaxCp());
            player.sendChanges();
            player.setVar("pvp_tvt_title", player.getTitle() != null ? player.getTitle() : "", -1L);
            if (dispell) {
               effects.addAll(player.getEffectList().getAllEffects());
               if (player.getPet() != null) {
                  player.getPet().getEffectList().stopAllEffects();
               }
            }

            Iterator var12 = effects.iterator();

            while(true) {
               Effect $effect;
               while(true) {
                  if (!var12.hasNext()) {
                     if (!Config.PVP_EVENTS_MAGE_BUFF.isEmpty() || !Config.PVP_EVENTS_WARRIOR_BUFF.isEmpty()) {
                        var12 = (player.isMageClass() ? Config.PVP_EVENTS_MAGE_BUFF : Config.PVP_EVENTS_WARRIOR_BUFF).iterator();

                        while(var12.hasNext()) {
                           Pair<Integer, Integer> pair = (Pair)var12.next();
                           SkillTable.getInstance().getInfo((Integer)pair.getLeft(), (Integer)pair.getRight()).getEffects(player, player, false, false);
                        }
                     }

                     if (skillUpdate) {
                        player.sendPacket(new SkillCoolTime(player));
                        player.updateStats();
                        player.updateEffectIcons();
                     }

                     if (team_type == TeamType.BLUE) {
                        player.setTeam(TeamType.BLUE);
                        this._blue_team.put(player.getObjectId(), new ImmutablePair(new AtomicInteger(0), new AtomicInteger(0)));
                        team_type = TeamType.RED;
                     } else {
                        player.setTeam(TeamType.RED);
                        this._red_team.put(player.getObjectId(), new ImmutablePair(new AtomicInteger(0), new AtomicInteger(0)));
                        team_type = TeamType.BLUE;
                     }
                     continue label119;
                  }

                  $effect = (Effect)var12.next();
                  Skill skill = $effect.getSkill();
                  if (skill.isToggle()) {
                     break;
                  }

                  List<Effect> savedEffects = (List)this._saveEffects.get(player.getObjectId());
                  if (savedEffects == null) {
                     this._saveEffects.put(player.getObjectId(), savedEffects = new ArrayList());
                  }

                  Effect effect = $effect.getTemplate().getEffect(new Env($effect.getEffector(), $effect.getEffected(), skill));
                  if (effect.isSaveable()) {
                     effect.setCount($effect.getCount());
                     effect.setPeriod($effect.getCount() == 1 ? $effect.getPeriod() - $effect.getTime() : $effect.getPeriod());
                     ((List)savedEffects).add(effect);
                     break;
                  }
               }

               $effect.exit();
            }
         }

      }

      public void prepareParticipantsFrom() {
         try {
            boolean dispell_after = PvPEvent.getInstance().config_dispellEffectsAfter();
            Iterator var2 = PvPEvent.getInstance().getPlayers().iterator();

            while(var2.hasNext()) {
               Player player = (Player)var2.next();

               try {
                  player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                  if (player.isAttackingNow()) {
                     player.abortAttack(true, false);
                  }

                  if (player.isCastingNow()) {
                     player.abortCast(true, false);
                  }

                  player.sendActionFailed();
                  player.stopMove();
                  player.sitDown((StaticObjectInstance)null);
                  player.block();
                  if (dispell_after) {
                     player.getEffectList().stopAllEffects();
                  }

                  if (player.getClan() != null && Config.PVP_EVENTS_RESTRICTED_CLAN_SKILLS) {
                     player.getClan().enableSkills(player);
                  }

                  for(int i = 0; i < Config.PVP_EVENTS_RESTRICTED_SKILL_IDS.length; ++i) {
                     int id = Config.PVP_EVENTS_RESTRICTED_SKILL_IDS[i];
                     if (player.isUnActiveSkill(id)) {
                        Skill skill = player.getKnownSkill(id);
                        if (skill != null) {
                           player.removeUnActiveSkill(skill);
                        }
                     }
                  }

                  List<Effect> effects = (List)this._saveEffects.get(player.getObjectId());
                  if (effects != null) {
                     Iterator var13 = effects.iterator();

                     while(var13.hasNext()) {
                        Effect effect = (Effect)var13.next();
                        if (player.getEffectList().getEffectsBySkill(effect.getSkill()) == null) {
                           player.getEffectList().addEffect(effect);
                        }
                     }
                  }

                  player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
                  player.setCurrentCp((double)player.getMaxCp());
                  player.sendChanges();
                  player.setTeam(TeamType.NONE);
                  String title = player.getVar("pvp_tvt_title");
                  if (title != null) {
                     player.setTitle(title);
                     player.unsetVar("pvp_tvt_title");
                  }

                  if (PvPEvent.getInstance().config_hideIdentiti() && player.getTransformation() == 0) {
                     player.setTransformationName((String)null);
                     player.setTransformationTitle((String)null);
                  }

                  player.sendUserInfo(true);
               } catch (Exception var10) {
                  var10.printStackTrace();
               }
            }
         } finally {
            this._red_team.clear();
            this._blue_team.clear();
            if (this._saveEffects != null) {
               this._saveEffects.clear();
            }

            this._red_team = null;
            this._blue_team = null;
            this._red_points = null;
            this._blue_points = null;
            this._saveEffects = null;
         }

      }

      public void initParticipant() {
         boolean isBuffProtection = PvPEvent.getInstance().config_isBuffProtection();
         Iterator var2 = PvPEvent.getInstance().getPlayers().iterator();

         while(var2.hasNext()) {
            Player player = (Player)var2.next();
            player.addListener(PvPEvent.getInstance()._dieListner);
            player.addListener(PvPEvent.getInstance()._exitListner);
            player.setResurectProhibited(true);
            player.unblock();
            player.standUp();
            if (isBuffProtection) {
               PvPEvent.BUFF_PROTECTION_EFFECT.getEffects(player, player, false, false, false);
            }
         }

      }

      public void doneParicipant() {
         if (this._rankBroadcastTask != null) {
            this._rankBroadcastTask.cancel(true);
            this._rankBroadcastTask = null;
         }

         Player player;
         for(Iterator var1 = PvPEvent.getInstance().getPlayers().iterator(); var1.hasNext(); player.standUp()) {
            player = (Player)var1.next();
            player.removeListener(PvPEvent.getInstance()._dieListner);
            player.removeListener(PvPEvent.getInstance()._exitListner);
            player.setResurectProhibited(false);
            player.unblock();
            if (player.isDead()) {
               player.doRevive(100.0D);
               player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
               player.setCurrentCp((double)player.getMaxCp());
            }
         }

      }

      private void updateTitle(Player player, int kills) {
         player.setTransformationTitle(String.format("Kills: %d", kills));
         player.setTitle(player.getTransformationTitle());
         player.broadcastPacket(new L2GameServerPacket[]{new NickNameChanged(player)});
      }

      public void OnPlayerDied(Player target, Player killer) {
         ImmutablePair entry;
         if (killer != null && killer.getTeam() != target.getTeam()) {
            AtomicInteger cnt;
            if (killer.getTeam() == TeamType.RED && this._red_team.containsKey(killer.getObjectId())) {
               entry = (ImmutablePair)this._red_team.get(killer.getObjectId());
               cnt = (AtomicInteger)entry.getLeft();
               this.updateTitle(killer, cnt.incrementAndGet());
               this._red_points.incrementAndGet();
            } else if (killer.getTeam() == TeamType.BLUE && this._blue_team.containsKey(killer.getObjectId())) {
               entry = (ImmutablePair)this._blue_team.get(killer.getObjectId());
               cnt = (AtomicInteger)entry.getLeft();
               this.updateTitle(killer, cnt.incrementAndGet());
               this._blue_points.incrementAndGet();
            } else if (killer.getTeam() != TeamType.NONE) {
               PvPEvent._log.warn("PvPEvent.TVT: '" + killer.getName() + "' got color but not at list.");
            }

            killer.sendUserInfo(true);
         }

         if (target.getTeam() == TeamType.RED && this._red_team.containsKey(target.getObjectId())) {
            entry = (ImmutablePair)this._red_team.get(target.getObjectId());
            ((AtomicInteger)entry.getRight()).incrementAndGet();
         } else if (target.getTeam() == TeamType.BLUE && this._blue_team.containsKey(target.getObjectId())) {
            entry = (ImmutablePair)this._blue_team.get(target.getObjectId());
            ((AtomicInteger)entry.getRight()).incrementAndGet();
         }

         ThreadPoolManager.getInstance().schedule(PvPEvent.getInstance().new TeleportAndReviveTask(target, this.getRandomTeamLoc(target.getTeam()), this.getReflection()), (long)(PvPEvent.getInstance().config_ReviveDelay() * 1000));
      }

      public void portParticipantsTo() {
         int redCnt = 0;
         int blueCnt = 0;
         Iterator var3 = PvPEvent.getInstance().getPlayers().iterator();

         while(true) {
            while(var3.hasNext()) {
               Player player = (Player)var3.next();
               TeamType playerTeam = player.getTeam();
               if (playerTeam != TeamType.BLUE && playerTeam != TeamType.RED) {
                  PvPEvent.getInstance()._participants.remove(player.getObjectId());
                  this.OnExit(player);
               } else {
                  player.setVar(this.RET_LOC_VAR, player.getLoc().toXYZString(), -1L);
                  if (player.getParty() != null) {
                     player.getParty().removePartyMember(player, false);
                  }

                  if (PvPEvent.getInstance().config_hideIdentiti()) {
                     Object[] var10002;
                     switch(playerTeam) {
                     case RED:
                        var10002 = new Object[1];
                        ++redCnt;
                        var10002[0] = redCnt;
                        player.setTransformationName(String.format("Red %d", var10002));
                        break;
                     case BLUE:
                        var10002 = new Object[1];
                        ++blueCnt;
                        var10002[0] = blueCnt;
                        player.setTransformationName(String.format("Blue %d", var10002));
                     }
                  }

                  player.teleToLocation(this.getRandomTeamLoc(playerTeam), this.getReflection());
               }
            }

            return;
         }
      }

      public void portParticipantsBack() {
         Iterator var1 = PvPEvent.getInstance().getPlayers().iterator();

         while(var1.hasNext()) {
            Player player = (Player)var1.next();
            if (player.getTransformation() == 0) {
               player.setTransformationName((String)null);
            }

            String sloc = player.getVar(this.RET_LOC_VAR);
            if (sloc != null) {
               player.unsetVar(this.RET_LOC_VAR);
               player.teleToLocation(Location.parseLoc(sloc), ReflectionManager.DEFAULT);
            } else {
               player.teleToClosestTown();
            }
         }

      }

      public void initReflection() {
         this._instance_id = PvPEvent.getInstance().getNewReflectionId();
         InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(this._instance_id);
         this.ZONE_DEFAULT = String.format("[pvp_%d_tvt_default]", this._instance_id);
         this.ZONE_BLUE = String.format("[pvp_%d_tvt_spawn_blue]", this._instance_id);
         this.ZONE_RED = String.format("[pvp_%d_tvt_spawn_red]", this._instance_id);
         this._reflection = new Reflection();
         this._reflection.init(instantZone);
         this._default_zone = this._reflection.getZone(this.ZONE_DEFAULT);
         this._default_zone.addListener(PvPEvent.getInstance()._zoneListner);
      }

      public void doneReflection() {
         this._default_zone.removeListener(PvPEvent.getInstance()._zoneListner);
         this._reflection.collapse();
         this._reflection = null;
      }

      public Reflection getReflection() {
         return this._reflection;
      }

      private Location getRandomTeamLoc(TeamType tt) {
         if (tt == TeamType.BLUE) {
            return this._reflection.getZone(this.ZONE_BLUE).getTerritory().getRandomLoc(this._reflection.getGeoIndex());
         } else {
            return tt == TeamType.RED ? this._reflection.getZone(this.ZONE_RED).getTerritory().getRandomLoc(this._reflection.getGeoIndex()) : null;
         }
      }

      public void OnEnter(Player player, Zone zone) {
         if (player != null && !player.isGM() && player.getTeam() != TeamType.BLUE && player.getTeam() != TeamType.RED && zone == this._default_zone) {
            player.teleToClosestTown();
         }

      }

      public void OnLeave(Player player, Zone zone) {
         if (player != null && !this._default_zone.checkIfInZone(player.getX(), player.getY(), player.getZ(), this.getReflection())) {
            if (player.getTeam() != TeamType.BLUE && player.getTeam() != TeamType.RED && zone == this._default_zone) {
               player.teleToClosestTown();
               return;
            }

            double radian = 6.283185307179586D - PositionUtils.convertHeadingToRadian(player.getHeading());
            double cos = Math.cos(radian);
            double sin = Math.sin(radian);
            Location randomLoc = zone.getTerritory().getRandomLoc(player.getGeoIndex());

            for(int i = 32; i < 512; i += 32) {
               int x = (int)Math.floor((double)player.getX() - (double)i * cos);
               int y = (int)Math.floor((double)player.getY() + (double)i * sin);
               if (zone.getTerritory().isInside(x, y)) {
                  randomLoc.set(x, y, player.getZ(), player.getHeading());
                  break;
               }
            }

            ThreadPoolManager.getInstance().schedule(PvPEvent.getInstance().new TeleportTask(player, randomLoc.correctGeoZ(), this.getReflection()), 3000L);
         }

      }

      public void OnExit(Player player) {
         if (this._blue_team.containsKey(player.getObjectId())) {
            this._blue_team.remove(player.getObjectId());
         } else if (this._red_team.containsKey(player.getObjectId())) {
            this._red_team.remove(player.getObjectId());
         }

         String title = player.getVar("pvp_tvt_title");
         if (title != null) {
            player.setTitle(player.getVar("pvp_tvt_title"));
            player.unsetVar("pvp_tvt_title");
         }

      }

      public void OnTeleport(Player player, int x, int y, int z, Reflection r) {
         if (player != null && !this._default_zone.checkIfInZone(x, y, z, this.getReflection())) {
            Location loc = this.getRandomTeamLoc(player.getTeam());
            if (loc != null) {
               ThreadPoolManager.getInstance().schedule(PvPEvent.getInstance().new TeleportTask(player, loc, this.getReflection()), 3000L);
            }
         }

      }

      private void rewardPerKill(Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> team) {
         int itemId = PvPEvent.getInstance().config_ItemPerKill();
         if (itemId > 0) {
            Iterator var3 = team.entrySet().iterator();

            while(var3.hasNext()) {
               Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> e = (Entry)var3.next();
               int oid = (Integer)e.getKey();
               ImmutablePair<AtomicInteger, AtomicInteger> p = (ImmutablePair)e.getValue();
               int kills = ((AtomicInteger)p.getLeft()).get();
               Player player = GameObjectsStorage.getPlayer(oid);
               if (kills > 0 && player != null) {
                  Functions.addItem(player, itemId, (long)kills);
               }
            }

         }
      }

      private void reward(Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> team, List<Pair<ItemTemplate, Long>> teamReward, List<Pair<ItemTemplate, Long>> topReward) {
         int top_oid = -1;
         int top_cnt = Integer.MIN_VALUE;
         Iterator var6 = team.entrySet().iterator();

         while(true) {
            int oid;
            int kills;
            Player player;
            do {
               if (!var6.hasNext()) {
                  if (top_oid > 0 && top_cnt > 0) {
                     Player player = GameObjectsStorage.getPlayer(top_oid);
                     if (player != null) {
                        Iterator var16 = topReward.iterator();

                        while(var16.hasNext()) {
                           Pair<ItemTemplate, Long> topRewardItemInfo = (Pair)var16.next();
                           Functions.addItem(player, ((ItemTemplate)topRewardItemInfo.getLeft()).getItemId(), (Long)topRewardItemInfo.getRight());
                        }

                        Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TheTvTGameTopPlayerIsS1", new String[]{player.getName(), player.getTeam().name()});
                        if (Config.PVP_EVENT_GIVE_HERO_STATUS > 0) {
                           GlobalServices.makeCustomHero(player, (long)(Config.PVP_EVENT_GIVE_HERO_STATUS * 60) * 60L);
                        }
                     }
                  }

                  return;
               }

               Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> e = (Entry)var6.next();
               oid = (Integer)e.getKey();
               ImmutablePair<AtomicInteger, AtomicInteger> p = (ImmutablePair)e.getValue();
               kills = ((AtomicInteger)p.getLeft()).get();
               player = GameObjectsStorage.getPlayer(oid);
            } while(player == null);

            boolean rewarded = false;
            if (kills >= Config.PVP_EVENT_CHECK_MIN_KILL_COUNT_FOR_REWARD) {
               Iterator var13 = teamReward.iterator();

               while(var13.hasNext()) {
                  Pair<ItemTemplate, Long> teamRewardItemInfo = (Pair)var13.next();
                  rewarded = true;
                  Functions.addItem(player, ((ItemTemplate)teamRewardItemInfo.getLeft()).getItemId(), (Long)teamRewardItemInfo.getRight());
               }
            }

            if (top_cnt < kills) {
               top_cnt = kills;
               top_oid = oid;
            }

            if (!rewarded) {
               player.sendMessage(new CustomMessage("PVPEVENTS_YOUR_TEAM_WIN_BUT_NO_PRIZE", player, new Object[0]));
            }
         }
      }

      public void MakeWinner() {
         boolean _event_countdown = ServerVariables.getBool("PvP_event_countdown", true);
         int blue_pnt = this._blue_points.get();
         int red_pnt = this._red_points.get();
         giveListenerParticipate(this._blue_team, this._red_team);
         if (blue_pnt > red_pnt) {
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TeamBlueWonTheTvTGameCountIsS1S2", new String[]{String.valueOf(blue_pnt), String.valueOf(red_pnt)});
            this.reward(this._blue_team, PvPEvent.getInstance().config_RewardWinnerTeamItemIdAndAmount(), PvPEvent.getInstance().config_RewardTopItemIdAndAmount());
            this.reward(this._red_team, PvPEvent.getInstance().config_RewardLooserTeamItemIdAndAmount(), PvPEvent.getInstance().config_RewardLooserTeamItemIdAndAmount());
            giveListenerWinnerTeam(this._blue_team);
         } else if (blue_pnt < red_pnt) {
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TeamRedWonTheTvTGameCountIsS1S2", new String[]{String.valueOf(red_pnt), String.valueOf(blue_pnt)});
            this.reward(this._red_team, PvPEvent.getInstance().config_RewardWinnerTeamItemIdAndAmount(), PvPEvent.getInstance().config_RewardTopItemIdAndAmount());
            this.reward(this._blue_team, PvPEvent.getInstance().config_RewardLooserTeamItemIdAndAmount(), PvPEvent.getInstance().config_RewardLooserTeamItemIdAndAmount());
            giveListenerWinnerTeam(this._red_team);
         } else if (blue_pnt == red_pnt) {
            Announcements.getInstance().announceByCustomMessage("events.PvPEvent.TheTvTGameEndedInATie", ArrayUtils.EMPTY_STRING_ARRAY);
            this.reward(this._red_team, PvPEvent.getInstance().config_RewardTieItemIdAndAmount(), PvPEvent.getInstance().config_RewardTieItemIdAndAmount());
            this.reward(this._blue_team, PvPEvent.getInstance().config_RewardTieItemIdAndAmount(), PvPEvent.getInstance().config_RewardTieItemIdAndAmount());
         }

         this.rewardPerKill(this._red_team);
         this.rewardPerKill(this._blue_team);
         if (_event_countdown) {
            PvPEvent.getInstance().broadcast(ExEventMatchMessage.FINISH);
         }

      }

      private static void giveListenerParticipate(Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> team1, Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> team2) {
         Iterator var2 = team1.entrySet().iterator();

         Entry e;
         int oid;
         Player player;
         while(var2.hasNext()) {
            e = (Entry)var2.next();
            oid = (Integer)e.getKey();
            player = GameObjectsStorage.getPlayer(oid);
            if (player != null) {
               player.getListeners().onTvtEvent(false);
            }
         }

         var2 = team2.entrySet().iterator();

         while(var2.hasNext()) {
            e = (Entry)var2.next();
            oid = (Integer)e.getKey();
            player = GameObjectsStorage.getPlayer(oid);
            if (player != null) {
               player.getListeners().onTvtEvent(false);
            }
         }

      }

      private static void giveListenerWinnerTeam(Map<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> team) {
         Iterator var1 = team.entrySet().iterator();

         while(var1.hasNext()) {
            Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> e = (Entry)var1.next();
            int oid = (Integer)e.getKey();
            Player player = GameObjectsStorage.getPlayer(oid);
            if (player != null) {
               player.getListeners().onTvtEvent(true);
            }
         }

      }

      // $FF: synthetic method
      TvTParticipantController(Object x0) {
         this();
      }

      private static class RankComparator implements Comparator<Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>>> {
         private RankComparator() {
         }

         public int compare(Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> o1, Entry<Integer, ImmutablePair<AtomicInteger, AtomicInteger>> o2) {
            try {
               if (o1 == null && o2 == null) {
                  return 1;
               } else if (o1 == null) {
                  return 1;
               } else if (o2 == null) {
                  return -1;
               } else {
                  int i1 = (Integer)o1.getKey();
                  int i2 = (Integer)o2.getKey();
                  int k1 = ((AtomicInteger)((ImmutablePair)o1.getValue()).getLeft()).get();
                  int k2 = ((AtomicInteger)((ImmutablePair)o2.getValue()).getLeft()).get();
                  return k1 != k2 ? k2 - k1 : i2 - i1;
               }
            } catch (Exception var7) {
               return 0;
            }
         }

         // $FF: synthetic method
         RankComparator(Object x0) {
            this();
         }
      }
   }

   private interface IParticipantController {
      void prepareParticipantsTo();

      void prepareParticipantsFrom();

      void initParticipant();

      void doneParicipant();

      void portParticipantsTo();

      void portParticipantsBack();

      void initReflection();

      void doneReflection();

      Reflection getReflection();

      void OnPlayerDied(Player var1, Player var2);

      void OnEnter(Player var1, Zone var2);

      void OnLeave(Player var1, Zone var2);

      void OnExit(Player var1);

      void OnTeleport(Player var1, int var2, int var3, int var4, Reflection var5);

      void MakeWinner();
   }

   protected static enum PvPEventState {
      STANDBY,
      REGISTRATION,
      PORTING_TO,
      PREPARE_TO,
      COMPETITION,
      WINNER,
      PREPARE_FROM,
      PORTING_FROM;
   }
}
