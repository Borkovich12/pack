package events.battle.model;

import events.EventUtils;
import events.battle.BattleGvG;
import events.battle.enums.BattleType;
import events.battle.tasks.BattleAnnounce;
import events.battle.tasks.BattleEndTask;
import events.battle.tasks.BattleTimer;
import events.battle.util.BattleUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.listener.PlayerListener;
import l2.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.ShortCutRegister;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleMatch {
   private static final Logger _log = LoggerFactory.getLogger(BattleMatch.class);
   private int _index;
   private BattleGvG _battle;
   private int _round = 1;
   private long dmg1 = 0L;
   private long dmg2 = 0L;
   private int win1 = 0;
   private int win2 = 0;
   private int _winner = 0;
   private BattleType _type;
   private BattleGrp _gr1;
   private BattleGrp _gr2;
   private List<HardReference<Player>> players1 = new CopyOnWriteArrayList();
   private List<HardReference<Player>> players2 = new CopyOnWriteArrayList();
   private List<HardReference<Player>> all_players = new CopyOnWriteArrayList();
   private boolean _clearing;
   private int _status;
   private boolean _fast;
   private boolean _first;
   private int _to_stadium;
   private int _counter;
   private long fightBeginTime;
   private ScheduledFuture<?> _endTask;
   private ScheduledFuture<?> _timerTask;
   private ScheduledFuture<?> _announceTask;
   private ScheduledFuture<?> _liveTask;
   private ScheduledFuture<?> _resTask;
   private List<NpcInstance> _spawn1 = new ArrayList();
   private List<NpcInstance> _spawn2 = new ArrayList();
   private Map<Integer, List<Effect>> _savedBuffs = new ConcurrentHashMap();
   private Map<Integer, List<BattleAug>> _savedAugs = new ConcurrentHashMap();
   private Map<Integer, List<Skill>> _savedEnchantSkills = new ConcurrentHashMap();
   private Map<Integer, List<Integer>> _equip = new ConcurrentHashMap();
   private Map<Integer, List<Integer>> _destroy = new ConcurrentHashMap();
   private final PlayerListener _playerListeners = new BattleMatch.PlayerListenerImpl();
   private int _stage;
   private boolean saveCoords;

   public BattleMatch(BattleGvG battle, BattleGrp gr1, BattleGrp gr2, int index) {
      this._battle = battle;
      this._type = battle.getType();
      this._gr1 = gr1;
      this._gr2 = gr2;
      this._index = index;
      Iterator var5 = HardReferences.unwrap((Collection)this._battle.getCommands().get(this._gr1.getId())).iterator();

      Player player;
      while(var5.hasNext()) {
         player = (Player)var5.next();
         this.players1.add(player.getRef());
         this.all_players.add(player.getRef());
      }

      var5 = HardReferences.unwrap((Collection)this._battle.getCommands().get(this._gr2.getId())).iterator();

      while(var5.hasNext()) {
         player = (Player)var5.next();
         if (!this.players1.contains(player.getRef())) {
            this.players2.add(player.getRef());
         }

         if (!this.all_players.contains(player.getRef())) {
            this.all_players.add(player.getRef());
         }
      }

   }

   public void start(boolean first, int stage) {
      if (this._endTask != null) {
         _log.warn("GvG: " + this._gr1.getName() + " vs " + this._gr2.getName() + " round " + this._round + " wrong start!");

         try {
            this._endTask.cancel(true);
            this._endTask = null;
         } catch (Exception var9) {
         }
      }

      this._status = 0;
      this._clearing = false;
      this._first = first;
      this._winner = 0;
      this._to_stadium = 6;
      this._counter = 7;
      this.dmg1 = 0L;
      this.dmg2 = 0L;
      this._stage = stage;
      this.saveCoords = this._type.getReturnPoint().length < 3 && this._first && !this._type.isToArena();
      if (this._first) {
         int time = this._to_stadium * 15 + (this._type.getBufferId() > 0 ? 30 : 45) + 15;
         BattleUtil.sayToAll("events.battle.model.BattleMatch.start.firstStart", new String[]{String.valueOf(time), this._type.getNameType(), this._gr1.getName(), this._gr2.getName()});
         this.announce();
         if (this._type.isShowStatus()) {
            Map<Integer, List<HardReference<Player>>> commands = new HashMap(this._battle.getCommands());
            Iterator var5 = commands.values().iterator();

            while(var5.hasNext()) {
               List<HardReference<Player>> cms = (List)var5.next();
               Iterator var7 = HardReferences.unwrap(cms).iterator();

               while(var7.hasNext()) {
                  Player player = (Player)var7.next();
                  this._battle.status(player);
               }
            }
         }
      } else {
         int time = 15;
         BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.start.notFirstStart", new String[]{String.valueOf(this._round), String.valueOf(time)});
         ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            public void runImpl() {
               BattleMatch.this._status = 1;
               BattleMatch.this.prepare();
            }
         }, (long)(time * 1000));
      }

   }

   public void announce() {
      if (this._to_stadium > 0) {
         int time = 15;
         BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), false, "events.battle.model.BattleMatch.announce.toStadium", new String[]{String.valueOf(this._to_stadium * time)});
         --this._to_stadium;
         ThreadPoolManager.getInstance().schedule(this::announce, (long)time * 1000L);
      } else {
         this._status = 1;
         BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), false, "events.battle.model.BattleMatch.announce.notStadium", (String[])null);
         this.prepare();
      }

   }

   public void prepare() {
      if (this._first) {
         this._savedBuffs = new ConcurrentHashMap(this.all_players.size());
         this._savedAugs.clear();
         this._savedEnchantSkills.clear();
         if (this._type.isCustomItemsEnable()) {
            this._equip = new ConcurrentHashMap(this.all_players.size());
            this._destroy = new ConcurrentHashMap(this.all_players.size());
         }
      }

      ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
         public void runImpl() throws Exception {
            BattleMatch.this.prepareToTeleport();
         }
      }, 100L);
      ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
         public void runImpl() throws Exception {
            BattleMatch.this.teleportPlayersToArena();
         }
      }, 2500L);
      this.spawnNpc();
      this.despawnNpc();
   }

   private void prepareToTeleport() {
      Player player;
      for(Iterator var1 = HardReferences.unwrap(this.all_players).iterator(); var1.hasNext(); this.preparePlayer(player)) {
         player = (Player)var1.next();
         if (this._first) {
            player.standUp();
            if (this._type.isToArena()) {
               player.setIsInvul(false);
            }

            player.broadcastCharInfo();
            player.setHeading(0);
            player.unblock();
         }

         if (this._first) {
            if (this.players1.contains(player.getRef())) {
               player.setTeam(TeamType.BLUE);
            } else if (this.players2.contains(player.getRef())) {
               player.setTeam(TeamType.RED);
            }

            player.setResurectProhibited(true);
         }
      }

   }

   public void teleportPlayersToArena() {
      Iterator var1 = HardReferences.unwrap(this.all_players).iterator();

      while(var1.hasNext()) {
         Player player = (Player)var1.next();

         try {
            if (this._type.getTpRange() > 0) {
               if (this.players1.contains(player.getRef())) {
                  player.teleToLocation(Location.findAroundPosition((Location)this._type.getBlueTeamLoc().get(0), 0, this._type.getTpRange(), this._battle.getReflection().isDefault() ? 0 : this._battle.getReflection().getGeoIndex()), this._battle.getReflection());
               } else if (this.players2.contains(player.getRef())) {
                  player.teleToLocation(Location.findAroundPosition((Location)this._type.getRedTeamLoc().get(0), 0, this._type.getTpRange(), this._battle.getReflection().isDefault() ? 0 : this._battle.getReflection().getGeoIndex()), this._battle.getReflection());
               }
            } else if (this.players1.contains(player.getRef())) {
               player.teleToLocation((Location)Rnd.get(this._type.getBlueTeamLoc()), this._battle.getReflection());
            } else if (this.players2.contains(player.getRef())) {
               player.teleToLocation((Location)Rnd.get(this._type.getRedTeamLoc()), this._battle.getReflection());
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

   }

   private void despawnNpc() {
      ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
         public void runImpl() {
            BattleMatch.this.despawnBuffer();
         }
      }, this._type.getBufferId() > 0 ? 30000L : 45000L);
   }

   private void spawnNpc() {
      if (this._type.getBufferId() > 0) {
         try {
            String[] var1 = this._type.getBuffer1Coords();
            int var2 = var1.length;

            int var3;
            String npcLoc;
            for(var3 = 0; var3 < var2; ++var3) {
               npcLoc = var1[var3];
               this._spawn1.add(BattleUtil.spawnSingle(this._type.getBufferId(), Location.parseLoc(npcLoc), this._battle.getReflection()));
            }

            var1 = this._type.getBuffer2Coords();
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
               npcLoc = var1[var3];
               this._spawn2.add(BattleUtil.spawnSingle(this._type.getBufferId(), Location.parseLoc(npcLoc), this._battle.getReflection()));
            }
         } catch (Exception var5) {
            _log.error("on spawn buffer", var5);
         }

         BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.teleportToArena.bufferHave", new String[]{String.valueOf(30), String.valueOf(45)});
      } else {
         BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.teleportToArena.bufferNotHave", new String[]{String.valueOf(60)});
      }

   }

   private void despawnBuffer() {
      if (this._type.getBufferId() > 0) {
         try {
            Iterator var1 = this._spawn1.iterator();

            NpcInstance buffer;
            while(var1.hasNext()) {
               buffer = (NpcInstance)var1.next();
               buffer.deleteMe();
            }

            this._spawn1.clear();
            var1 = this._spawn2.iterator();

            while(var1.hasNext()) {
               buffer = (NpcInstance)var1.next();
               buffer.deleteMe();
            }

            this._spawn2.clear();
         } catch (Exception var3) {
            _log.error("on despawn buffer", var3);
         }
      }

      int despawnTime = 15;
      BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.despawnBuffer", new String[]{String.valueOf(despawnTime)});
      BattleUtil.sayScreen(this.all_players, "events.battle.model.BattleMatch.screenDespawnBuffer", new String[]{String.valueOf(despawnTime)});
      ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
         public void runImpl() {
            BattleMatch.this.counter();
         }
      }, (long)(despawnTime * 1000));
   }

   public void counter() {
      --this._counter;
      int delay = this._counter > 5 ? 10 : this._counter;
      BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.counter", new String[]{String.valueOf(delay)});
      BattleUtil.sayScreen(this.all_players, "events.battle.model.BattleMatch.screenCounter", new String[]{String.valueOf(delay)});
      ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
         public void runImpl() {
            if (BattleMatch.this._counter == 1) {
               BattleMatch.this.go();
            } else {
               BattleMatch.this.counter();
            }

         }
      }, this._counter > 5 ? 5000L : 1000L);
   }

   private void go() {
      this._status = 2;
      Iterator var1;
      if (this._battle.getReflection().getDoors() != null) {
         var1 = this._battle.getReflection().getDoors().iterator();

         while(var1.hasNext()) {
            DoorInstance doorId = (DoorInstance)var1.next();
            doorId.openMe();
         }
      }

      Player player;
      for(var1 = HardReferences.unwrap(this.all_players).iterator(); var1.hasNext(); player.setPrepare(false)) {
         player = (Player)var1.next();
         EventUtils.healPlayer(player);
         if (this._type.isBlockMove()) {
            player.stopRooted();
         }
      }

      BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.go", new String[]{String.valueOf(this._round)});
      BattleUtil.sayScreen(this.all_players, "events.battle.model.BattleMatch.screenGo", new String[]{String.valueOf(this._round)});
      this.fightBeginTime = System.currentTimeMillis();
      this._endTask = ThreadPoolManager.getInstance().schedule(new BattleEndTask(this), (long)this._type.getTimeBattle() * 60000L);
      if (this._type.isAllowBattleTimer()) {
         this._timerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BattleTimer(this), 0L, 1010L);
      } else {
         this._announceTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BattleAnnounce(this), 60000L, 60000L);
      }

      this.checkLive();
   }

   private void saveBuffs(Player player) {
      List<Effect> effectList = player.getEffectList().getAllEffects();
      List<Effect> effects = new ArrayList(effectList.size());
      Iterator var4 = effectList.iterator();

      while(var4.hasNext()) {
         Effect e = (Effect)var4.next();
         if (!e.getSkill().isToggle()) {
            Effect effect = e.getTemplate().getEffect(new Env(e.getEffector(), e.getEffected(), e.getSkill()));
            effect.setCount(e.getCount());
            effect.setPeriod(e.getCount() == 1 ? e.getPeriod() - e.getTime() : e.getPeriod());
            effects.add(effect);
         }
      }

      if (!effects.isEmpty()) {
         this._savedBuffs.put(player.getObjectId(), effects);
      }

   }

   private void giveBuffs(Player player) {
      if (this._battle.getType().isEnableBuffs() && (!this._battle.getType().getMageBuff().isEmpty() || !this._battle.getType().getWarriorBuff().isEmpty())) {
         Iterator var2 = (player.isMageClass() ? this._battle.getType().getMageBuff() : this._battle.getType().getWarriorBuff()).iterator();

         while(var2.hasNext()) {
            Pair<Integer, Integer> pair = (Pair)var2.next();
            Skill skill = SkillTable.getInstance().getInfo((Integer)pair.getLeft(), (Integer)pair.getRight());
            skill.getEffects(player, player, false, false);
         }
      }

   }

   public void endBattle() {
      if (!this._clearing) {
         this._clearing = true;

         try {
            if (this._endTask != null) {
               this._endTask.cancel(true);
               this._endTask = null;
            }
         } catch (Exception var6) {
         }

         try {
            if (this._timerTask != null) {
               this._timerTask.cancel(true);
               this._timerTask = null;
            }
         } catch (Exception var5) {
         }

         try {
            if (this._announceTask != null) {
               this._announceTask.cancel(true);
               this._announceTask = null;
            }
         } catch (Exception var4) {
         }

         try {
            if (this._liveTask != null) {
               this._liveTask.cancel(true);
               this._liveTask = null;
            }
         } catch (Exception var3) {
         }

         Iterator var1;
         if (this._battle.getReflection().getDoors() != null) {
            var1 = this._battle.getReflection().getDoors().iterator();

            while(var1.hasNext()) {
               DoorInstance door = (DoorInstance)var1.next();
               door.closeMe();
            }
         }

         if (this._status != 0) {
            this._status = 0;
            var1 = HardReferences.unwrap(this.all_players).iterator();

            while(var1.hasNext()) {
               Player player = (Player)var1.next();
               player.setPrepare(true);
            }

            boolean finalRound = false;
            if (this._winner == 1) {
               ++this.win1;
               finalRound = this.win1 >= this._type.getRoundsWin();
               if (this._fast) {
                  BattleUtil.sayToAll("events.battle.model.BattleMatch.endBattle.winnerFast", new String[]{this._gr2.getName(), this._gr1.getName()});
               } else if (finalRound) {
                  BattleUtil.sayToAll("events.battle.model.BattleMatch.endBattle.winnerFinalRound", new String[]{this._gr1.getName(), this._gr2.getName(), String.valueOf(this.win1), String.valueOf(this.win2)});
               } else {
                  BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.endBattle.winnerRound", new String[]{this._gr1.getName(), String.valueOf(this._round), String.valueOf(this.win1), String.valueOf(this.win2)});
               }

               BattleUtil.sayScreen(this.all_players, "events.battle.model.BattleMatch.endBattle.screenWin", new String[]{this._gr1.getName()});
            } else if (this._winner == 2) {
               ++this.win2;
               finalRound = this.win2 >= this._type.getRoundsWin();
               if (this._fast) {
                  BattleUtil.sayToAll("events.battle.model.BattleMatch.endBattle.winnerFast", new String[]{this._gr1.getName(), this._gr2.getName()});
               } else if (finalRound) {
                  BattleUtil.sayToAll("events.battle.model.BattleMatch.endBattle.winnerFinalRound", new String[]{this._gr2.getName(), this._gr1.getName(), String.valueOf(this.win2), String.valueOf(this.win1)});
               } else {
                  BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.endBattle.winnerRound", new String[]{this._gr2.getName(), String.valueOf(this._round), String.valueOf(this.win2), String.valueOf(this.win1)});
               }

               BattleUtil.sayScreen(this.all_players, "events.battle.model.BattleMatch.endBattle.screenWin", new String[]{this._gr2.getName()});
            } else if (this.dmg1 > this.dmg2) {
               ++this.win1;
               finalRound = this.win1 >= this._type.getRoundsWin();
               if (finalRound) {
                  BattleUtil.sayToAll("events.battle.model.BattleMatch.endBattle.winnerFinalRoundDmg", new String[]{this._gr1.getName(), this._gr2.getName(), String.valueOf(this.win1), String.valueOf(this.win2), String.valueOf(this.dmg1), String.valueOf(this.dmg2)});
               } else {
                  BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.endBattle.winnerRoundDmg", new String[]{String.valueOf(this._round), this._gr1.getName(), String.valueOf(this.dmg1), String.valueOf(this.dmg2), String.valueOf(this.win1), String.valueOf(this.win2)});
               }

               BattleUtil.sayScreen(this.all_players, "events.battle.model.BattleMatch.endBattle.screenWin", new String[]{this._gr1.getName()});
               this._winner = 1;
            } else {
               ++this.win2;
               finalRound = this.win2 >= this._type.getRoundsWin();
               if (finalRound) {
                  BattleUtil.sayToAll("events.battle.model.BattleMatch.endBattle.winnerFinalRoundDmg", new String[]{this._gr2.getName(), this._gr1.getName(), String.valueOf(this.win2), String.valueOf(this.win1), String.valueOf(this.dmg2), String.valueOf(this.dmg1)});
               } else {
                  BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.endBattle.winnerRoundDmg", new String[]{String.valueOf(this._round), this._gr2.getName(), String.valueOf(this.dmg2), String.valueOf(this.dmg1), String.valueOf(this.win2), String.valueOf(this.win1)});
               }

               BattleUtil.sayScreen(this.all_players, "events.battle.model.BattleMatch.endBattle.screenWin", new String[]{this._gr2.getName()});
               this._winner = 2;
            }

            this._savedBuffs.clear();
            this._clearing = false;
            if (!this._fast && !finalRound) {
               ++this._round;
               this.start(false, this._stage);
            } else {
               this._fast = false;
               this.removeAura();
               this._round = 1;
               this._battle.addNextList(this._winner == 1 ? this._gr1 : this._gr2);
               Log.add((this._winner == 1 ? this._gr1.getName() : this._gr2.getName()) + " defeat " + (this._winner == 1 ? this._gr2.getName() : this._gr1.getName()), "gvg");
               BattleUtil.sayToParticipants(this.all_players, this._battle.getSpectators(), true, "events.battle.model.BattleMatch.endBattle.healAndTeleBack", new String[]{String.valueOf(this._type.getHealTpBackTime())});
               this._resTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                  public void runImpl() {
                     BattleMatch.this.ressurectPlayers();
                  }
               }, (long)this._type.getHealTpBackTime() * 1000L);
               this._battle.nextMatch(false);
            }

         }
      }
   }

   private void preparePlayer(Player player) {
      EventUtils.healPlayer(player);
      if (this._first) {
         if (player.getVarB("onObservationEnd")) {
            player.leaveObserverMode();
            player.unsetVar("onObservationEnd");
         }

         if (player.isInvisible()) {
            player.setInvisibleType(InvisibleType.NONE);
            player.stopAbnormalEffect(AbnormalEffect.STEALTH);
            player.broadcastCharInfo();
            if (player.getPet() != null) {
               player.getPet().broadcastCharInfo();
            }
         }

         player.setInGvG(this._type.getMembers());
         player.setPrepare(true);
         player.addListener(this._playerListeners);
         int[] equipableIds = ArrayUtils.EMPTY_INT_ARRAY;
         ArrayList augs;
         int activeClassId;
         int var6;
         ItemInstance item;
         int id;
         if (this._type.isCustomItemsEnable()) {
            augs = new ArrayList();
            ItemInstance[] var4 = player.getInventory().getPaperdollItems();
            activeClassId = var4.length;

            for(var6 = 0; var6 < activeClassId; ++var6) {
               ItemInstance item = var4[var6];
               if (item != null) {
                  player.getInventory().unEquipItem(item);
                  augs.add(item.getObjectId());
               }
            }

            if (!augs.isEmpty()) {
               this._equip.put(player.getObjectId(), augs);
            }

            List<Integer> cm = new ArrayList();
            activeClassId = player.getActiveClassId();

            for(Iterator var22 = ((List)this._battle.getCustomItemList().get(activeClassId)).iterator(); var22.hasNext(); cm.add(item.getObjectId())) {
               id = (Integer)var22.next();
               item = ItemFunctions.createItem(id);
               if (item.canBeEnchanted(true)) {
                  if (item.isWeapon()) {
                     item.setEnchantLevel(this._type.getCustomItemsEnchantWeapon());
                  } else {
                     item.setEnchantLevel(this._type.getCustomItemsEnchantArmor());
                  }
               }

               player.getInventory().addItem(item);
               if (item.isEquipable() && !item.getTemplate().isArrow()) {
                  player.getInventory().equipItem(item);
               }
            }

            if (!cm.isEmpty()) {
               List<Integer> equipableItems = new ArrayList();
               ItemInstance[] var27 = player.getInventory().getItems();
               int var29 = var27.length;

               for(int var9 = 0; var9 < var29; ++var9) {
                  ItemInstance item = var27[var9];
                  if (item.isEquipable()) {
                     equipableItems.add(item.getItemId());
                  }
               }

               equipableIds = ArrayUtils.toPrimitive((Integer[])equipableItems.toArray(new Integer[equipableItems.size()]));
               this._destroy.put(player.getObjectId(), cm);
            }
         }

         this.saveBuffs(player);
         this.giveBuffs(player);
         if (this._type.getRestrictedItems().length > 0 || equipableIds.length > 0) {
            ItemInstance[] var14 = player.getInventory().getItems();
            int var17 = var14.length;

            for(activeClassId = 0; activeClassId < var17; ++activeClassId) {
               ItemInstance item = var14[activeClassId];
               if (item != null && item.isEquipped() && ArrayUtils.contains(this._type.getRestrictedItems(), item.getItemId())) {
                  player.getInventory().unEquipItem(item);
               }
            }

            player.getInventory().lockItems(LockType.INCLUDE, ArrayUtils.addAll(this._type.getRestrictedItems(), equipableIds));
         }

         if (this._type.getRestrictedSkills().length > 0) {
            Iterator var15 = player.getAllSkills().iterator();

            while(var15.hasNext()) {
               Skill skill = (Skill)var15.next();
               if (ArrayUtils.contains(this._type.getRestrictedSkills(), skill.getId())) {
                  player.addUnActiveSkill(skill);
               }
            }
         }

         if (this._type.isNoAn()) {
            augs = new ArrayList();
            InventoryUpdate iu = null;
            ItemInstance[] var20 = player.getInventory().getItems();
            var6 = var20.length;

            for(id = 0; id < var6; ++id) {
               item = var20[id];
               if (item != null && item.isAugmented()) {
                  item.setCustomFlags(38);
                  augs.add(new BattleAug(item.getObjectId(), item.getVariationStat1(), item.getVariationStat2()));
                  item.setVariationStat1(0);
                  item.setVariationStat2(0);
                  player.sendChanges();
                  item.save();
                  if (iu == null) {
                     iu = new InventoryUpdate();
                  }

                  iu.addModifiedItem(item);
               }
            }

            if (iu != null) {
               player.sendPacket(iu);
            }

            if (!augs.isEmpty()) {
               this._savedAugs.put(player.getObjectId(), augs);
            }
         }

         if (this._type.isNoEnchantSkills()) {
            augs = new ArrayList();
            Iterator var21 = player.getAllSkills().iterator();

            while(var21.hasNext()) {
               Skill skill = (Skill)var21.next();
               if (skill != null && skill.getLevel() > 100) {
                  augs.add(skill);
                  player.removeSkill(skill, false);
                  Skill newSkill = SkillTable.getInstance().getInfo(skill.getId(), BattleUtil.getLevelWithoutEnchant(skill));
                  player.addSkill(newSkill, false);
               }
            }

            if (!augs.isEmpty()) {
               this._savedEnchantSkills.put(player.getObjectId(), augs);
               player.sendPacket(new SkillList(player));
               player.updateStats();
            }
         }

         Functions.unRide(player);
         Functions.unSummonPet(player, true);
      }

      player.resetReuse();

      try {
         player.getEffectList().stopAllEffects();
         Summon summon = player.getPet();
         if (summon != null) {
            summon.getEffectList().stopAllEffects();
            if (summon.isPet() || this._type.getRestrictedSummons().length > 0 && ArrayUtils.contains(this._type.getRestrictedSummons(), summon.getNpcId())) {
               summon.unSummon();
            }
         }
      } catch (Exception var11) {
         _log.error("on removeBuff", var11);
      }

      if (this._type.isBlockMove()) {
         player.startRooted();
      }

      Location ClearLoc = Location.parseLoc(this._type.getClearLoc());
      if (this.saveCoords && player.getVar("BattleGvG_backCoords") == null) {
         player.setVar("BattleGvG_backCoords", !player.isInZone(ZoneType.no_restart) && !player.isInZone(ZoneType.epic) ? player.getX() + " " + player.getY() + " " + player.getZ() : ClearLoc.x + " " + ClearLoc.y + " " + ClearLoc.z, -1L);
      }

   }

   private synchronized void checkLive() {
      if (this._status == 2) {
         try {
            if (this._liveTask != null) {
               this._liveTask.cancel(true);
               this._liveTask = null;
            }
         } catch (Exception var7) {
         }

         List<HardReference<Player>> live_list1 = new CopyOnWriteArrayList();
         List<HardReference<Player>> live_list2 = new CopyOnWriteArrayList();
         Iterator var3 = this.players1.iterator();

         HardReference ref;
         Player player;
         while(var3.hasNext()) {
            ref = (HardReference)var3.next();
            player = (Player)ref.get();
            if (player != null) {
               live_list1.add(player.getRef());
            }
         }

         var3 = this.players2.iterator();

         while(var3.hasNext()) {
            ref = (HardReference)var3.next();
            player = (Player)ref.get();
            if (player != null) {
               live_list2.add(player.getRef());
            }
         }

         if (live_list1.size() >= 1 && live_list2.size() >= 1) {
            boolean d1 = true;
            boolean d2 = true;
            Iterator var10 = HardReferences.unwrap(live_list1).iterator();

            Player player;
            while(var10.hasNext()) {
               player = (Player)var10.next();
               if (!player.isDead()) {
                  d1 = false;
                  break;
               }
            }

            var10 = HardReferences.unwrap(live_list2).iterator();

            while(var10.hasNext()) {
               player = (Player)var10.next();
               if (!player.isDead()) {
                  d2 = false;
                  break;
               }
            }

            if (d1 && d2) {
               this._liveTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                  public void runImpl() {
                     BattleMatch.this.checkLive();
                  }
               }, System.currentTimeMillis() + 30000L);
            } else {
               if (d1 || d2) {
                  this._winner = d1 ? 2 : 1;
                  this.endBattle();
               }

            }
         } else {
            this._winner = live_list1.size() < 1 ? 2 : 1;
            this._fast = true;
            this.endBattle();
         }
      }
   }

   private void removeAura() {
      Iterator var1 = HardReferences.unwrap(this.all_players).iterator();

      while(var1.hasNext()) {
         Player player = (Player)var1.next();
         player.removeListener(this._playerListeners);
         player.setPrepare(false);
         player.setTeam(TeamType.NONE);
         player.setInGvG(0);
         this.buffsItems(player);
      }

   }

   private void giveRewardPerKill(Creature actor, Creature killer) {
      if (this._type.getRewardPerKill().length > 0 && actor.getTeam() != killer.getTeam()) {
         for(int i = 0; i < this._type.getRewardPerKill().length; ++i) {
            if (this._type.getRewardPerKill()[i].length > 1) {
               Player player = killer.getPlayer();

               for(int n = 0; n < this._type.getRewardPerKill()[i].length; n += 2) {
                  int itemId = this._type.getRewardPerKill()[i][n];
                  int itemCount = this._type.getRewardPerKill()[i][n + 1];
                  Functions.addItem(player, itemId, (long)itemCount);
               }
            }
         }
      }

   }

   private void removePlayer(Player player) {
      if (player != null) {
         player.removeListener(this._playerListeners);
         if (player.getTeam() == TeamType.BLUE) {
            this.players1.remove(player.getRef());
         } else {
            this.players2.remove(player.getRef());
         }

         player.setTeam(TeamType.NONE);
         player.setInGvG(0);
         player.setResurectProhibited(false);
         player.setPrepare(false);
         player.setReg(false);
         if (this._type.isRestrictIp()) {
            this._battle.getRestrictIp().remove(player.getIP());
         }

         if (this._type.isRestrictHwid()) {
            GameClient playerClient = player.getNetConnection();
            if (playerClient != null) {
               this._battle.getRestrictHwid().remove(playerClient.getHwid());
            }
         }

         this.all_players.remove(player.getRef());
         this.buffsItems(player);
      }

   }

   private void ressurectPlayers() {
      String win = this._winner == 1 ? this._gr1.getName() : this._gr2.getName();
      String los = this._winner == 1 ? this._gr2.getName() : this._gr1.getName();
      Iterator var3;
      Player player;
      if (this._type.isToArena()) {
         if (this._battle.isActive()) {
            if (win.equals(this._battle.getW1()) && los.equals(this._battle.getW2())) {
               this._battle.teamBack(this._gr1.getId(), this.players1);
               this._battle.teamBack(this._gr2.getId(), this.players2);
               this._gr1.setEndBattle(true);
               this._gr2.setEndBattle(true);
               this._battle.replaceBattleMatch(this.getIndex(), this);
               this._resTask = null;
               return;
            }

            int teamId;
            Location point;
            if (win.equals(this._battle.getW1()) || win.equals(this._battle.getW2())) {
               this._battle.teamBack(this._winner == 1 ? this._gr1.getId() : this._gr2.getId(), this._winner == 1 ? this.players1 : this.players2);
               if (this._winner == 1) {
                  this._gr1.setEndBattle(true);
               } else {
                  this._gr2.setEndBattle(true);
               }

               this._battle.replaceBattleMatch(this.getIndex(), this);
               var3 = HardReferences.unwrap(this._winner == 1 ? this.players2 : this.players1).iterator();

               while(var3.hasNext()) {
                  player = (Player)var3.next();
                  if (player != null) {
                     teamId = this._winner == 1 ? this._gr2.getId() : this._gr1.getId();
                     point = (Location)this._battle.getPointByCommand().get(teamId);
                     if (point != null) {
                        BattleUtil.onArena(player, this._battle.getReflection(), point);
                     } else {
                        _log.warn("BattleMatch: For the player " + player.getName() + " from team " + teamId + ", the return position to the arena was not found#1");
                     }
                  }
               }

               this._resTask = null;
               return;
            }

            if (this._battle.getFirstList().size() + this._battle.getNextList().size() < 2) {
               var3 = HardReferences.unwrap(this._winner == 1 ? this.players1 : this.players2).iterator();

               while(var3.hasNext()) {
                  player = (Player)var3.next();
                  if (player != null) {
                     teamId = this._winner == 1 ? this._gr2.getId() : this._gr1.getId();
                     point = (Location)this._battle.getPointByCommand().get(teamId);
                     if (point != null) {
                        BattleUtil.onArena(player, this._battle.getReflection(), point);
                     } else {
                        _log.warn("BattleMatch: For the player " + player.getName() + " from team " + teamId + ", the return position to the arena was not found#2");
                     }
                  }
               }

               var3 = HardReferences.unwrap(this._winner == 1 ? this.players2 : this.players1).iterator();

               while(var3.hasNext()) {
                  player = (Player)var3.next();
                  if (player != null) {
                     teamId = this._winner == 1 ? this._gr2.getId() : this._gr1.getId();
                     point = (Location)this._battle.getPointByCommand().get(teamId);
                     if (point != null) {
                        BattleUtil.onArena(player, this._battle.getReflection(), point);
                     } else {
                        _log.warn("BattleMatch: For the player " + player.getName() + " from team " + teamId + ", the return position to the arena was not found#3");
                     }
                  }
               }

               this._resTask = null;
               return;
            }

            this._battle.teamBack(this._winner == 1 ? this._gr2.getId() : this._gr1.getId(), this._winner == 1 ? this.players2 : this.players1);
            if (this._winner == 1) {
               this._gr2.setEndBattle(true);
            } else {
               this._gr1.setEndBattle(true);
            }

            this._battle.replaceBattleMatch(this.getIndex(), this);
            var3 = HardReferences.unwrap(this._winner == 1 ? this.players1 : this.players2).iterator();

            while(var3.hasNext()) {
               player = (Player)var3.next();
               if (player != null) {
                  teamId = this._winner == 1 ? this._gr1.getId() : this._gr2.getId();
                  point = (Location)this._battle.getPointByCommand().get(teamId);
                  if (point != null) {
                     BattleUtil.onArena(player, this._battle.getReflection(), point);
                  } else {
                     _log.warn("BattleMatch: For the player " + player.getName() + " from team " + teamId + ", the return position to the arena was not found#4");
                  }
               }
            }
         } else {
            var3 = this._battle.getCommands().values().iterator();

            while(var3.hasNext()) {
               List<HardReference<Player>> cms = (List)var3.next();
               Iterator var5 = HardReferences.unwrap(cms).iterator();

               while(var5.hasNext()) {
                  Player player = (Player)var5.next();
                  BattleUtil.backPlayer(player, this._battle, false);
               }
            }

            this._battle.getCommands().clear();
         }

         this._resTask = null;
      } else {
         if (this._battle.isActive()) {
            if (win.equals(this._battle.getW1()) && los.equals(this._battle.getW2())) {
               this._battle.getCommands().remove(this._gr1.getId());
               this._battle.getCommands().remove(this._gr2.getId());
               this._battle.getCommandNames().remove(this._gr1.getId());
               this._battle.getCommandNames().remove(this._gr2.getId());
            } else if (!win.equals(this._battle.getW1()) && !win.equals(this._battle.getW2())) {
               if (this._battle.getFirstList().size() + this._battle.getNextList().size() < 2) {
                  var3 = HardReferences.unwrap(this.all_players).iterator();

                  while(var3.hasNext()) {
                     player = (Player)var3.next();
                     BattleUtil.backPlayer(player, this._battle, false);
                  }

                  this._resTask = null;
                  return;
               }
            } else {
               this._battle.getCommands().remove(this._winner == 1 ? this._gr1.getId() : this._gr2.getId());
               this._battle.getCommandNames().remove(this._winner == 1 ? this._gr1.getId() : this._gr2.getId());
            }
         }

         this._battle.getCommands().remove(this._winner == 1 ? this._gr2.getId() : this._gr1.getId());
         this._battle.getCommandNames().remove(this._winner == 1 ? this._gr2.getId() : this._gr1.getId());
         var3 = HardReferences.unwrap(this.all_players).iterator();

         while(var3.hasNext()) {
            player = (Player)var3.next();
            BattleUtil.backPlayer(player, this._battle, false);
         }

         this._resTask = null;
      }
   }

   private void buffsItems(Player player) {
      try {
         List effectList;
         Iterator var3;
         if (this._type.isCustomItemsEnable()) {
            effectList = (List)this._destroy.remove(player.getObjectId());
            int id;
            ItemInstance item;
            if (effectList != null) {
               var3 = effectList.iterator();

               while(var3.hasNext()) {
                  id = (Integer)var3.next();
                  item = player.getInventory().getItemByObjectId(id);
                  if (item != null && !item.getTemplate().isArrow()) {
                     player.getInventory().destroyItem(item);
                  }
               }
            }

            player.getInventory().unlock();
            effectList = (List)this._equip.remove(player.getObjectId());
            if (effectList != null) {
               var3 = effectList.iterator();

               while(var3.hasNext()) {
                  id = (Integer)var3.next();
                  item = player.getInventory().getItemByObjectId(id);
                  if (item != null && item.isEquipable() && !item.getTemplate().isArrow()) {
                     player.getInventory().equipItem(item);
                  }
               }
            }
         }

         if (this._type.getRestrictedSkills().length > 0) {
            Iterator var10 = player.getAllSkills().iterator();

            while(var10.hasNext()) {
               Skill skill = (Skill)var10.next();
               if (ArrayUtils.contains(this._type.getRestrictedSkills(), skill.getId())) {
                  player.enableSkill(skill);
               }
            }
         }

         if (this._type.getRestrictedItems().length > 0) {
            player.getInventory().unlock();
         }

         Iterator var14;
         if (this._type.isNoAn()) {
            effectList = (List)this._savedAugs.remove(player.getObjectId());
            if (effectList != null) {
               InventoryUpdate iu = null;
               var14 = effectList.iterator();

               label108:
               while(true) {
                  ItemInstance item;
                  BattleAug a;
                  do {
                     if (!var14.hasNext()) {
                        if (iu != null) {
                           player.sendPacket(iu);
                        }
                        break label108;
                     }

                     a = (BattleAug)var14.next();
                     item = player.getInventory().getItemByObjectId(a.getId());
                  } while(item == null);

                  item.setVariationStat1(a.getVariationStat1());
                  item.setVariationStat2(a.getVariationStat2());
                  item.setCustomFlags(0);
                  if (item.isEquipped()) {
                     player.getInventory().equipItem(item);
                  }

                  if (iu == null) {
                     iu = new InventoryUpdate();
                  }

                  iu.addModifiedItem(item);
                  Iterator var7 = player.getAllShortCuts().iterator();

                  while(var7.hasNext()) {
                     ShortCut sc = (ShortCut)var7.next();
                     if (sc.getId() == item.getObjectId() && sc.getType() == 1) {
                        player.sendPacket(new ShortCutRegister(player, sc));
                     }
                  }
               }
            }
         }

         if (this._type.isNoEnchantSkills()) {
            boolean updateSkills = false;
            List<Skill> enchantSkill = (List)this._savedEnchantSkills.get(player.getObjectId());

            for(var14 = enchantSkill.iterator(); var14.hasNext(); updateSkills = true) {
               Skill skill = (Skill)var14.next();
               player.addSkill(skill, false);
            }

            if (updateSkills) {
               player.sendPacket(new SkillList(player));
               player.updateStats();
            }
         }

         player.getEffectList().stopAllEffects();
         effectList = (List)this._savedBuffs.remove(player.getObjectId());
         if (effectList != null) {
            var3 = effectList.iterator();

            while(var3.hasNext()) {
               Effect e = (Effect)var3.next();
               player.getEffectList().addEffect(e);
            }
         }
      } catch (Exception var9) {
         _log.error("BattleMatch: on buffsItems", var9);
      }

   }

   public BattleType getType() {
      return this._type;
   }

   public ScheduledFuture<?> getResTask() {
      return this._resTask;
   }

   public int getWin1() {
      return this.win1;
   }

   public int getWin2() {
      return this.win2;
   }

   public int getStage() {
      return this._stage;
   }

   public int getWinner() {
      return this._winner;
   }

   public BattleGrp getGr1() {
      return this._gr1;
   }

   public BattleGrp getGr2() {
      return this._gr2;
   }

   public List<HardReference<Player>> getPlayers1() {
      return this.players1;
   }

   public List<HardReference<Player>> getPlayers2() {
      return this.players2;
   }

   public List<HardReference<Player>> getAllPlayers() {
      return this.all_players;
   }

   public BattleGvG getBattle() {
      return this._battle;
   }

   public void broadCastTimer() {
      int secondsLeft = (int)((this.fightBeginTime + (long)(this._type.getTimeBattle() * 60 * 1000) - System.currentTimeMillis()) / 1000L);
      int minutes = secondsLeft / 60;
      int seconds = secondsLeft % 60;
      ExShowScreenMessage packet = new ExShowScreenMessage(String.format("%02d:%02d", minutes, seconds), 1010, -1, ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, false);
      Iterator var5 = this.getPlayers1().iterator();

      HardReference player;
      while(var5.hasNext()) {
         player = (HardReference)var5.next();
         ((Player)player.get()).sendPacket(packet);
      }

      var5 = this.getPlayers2().iterator();

      while(var5.hasNext()) {
         player = (HardReference)var5.next();
         ((Player)player.get()).sendPacket(packet);
      }

   }

   public int getIndex() {
      return this._index;
   }

   public List<HardReference<Player>> getSpectators() {
      return this._battle.getSpectators();
   }

   private final class PlayerListenerImpl implements OnPlayerExitListener, OnCurrentHpDamageListener, OnDeathListener {
      private PlayerListenerImpl() {
      }

      public void onPlayerExit(Player player) {
         if (player != null) {
            if (BattleMatch.this._status == 1 && BattleMatch.this.all_players.contains(player.getRef())) {
               BattleMatch.this.removePlayer(player);
               BattleUtil.backPlayer(player, BattleMatch.this._battle, true);
            } else if (BattleMatch.this._status == 2 && player.getTeam() != TeamType.NONE && BattleMatch.this.all_players.contains(player.getRef())) {
               BattleMatch.this.removePlayer(player);
               BattleUtil.backPlayer(player, BattleMatch.this._battle, true);
               BattleMatch.this.checkLive();
            } else {
               if (player.getVar("BattleGvG_backCoords") != null) {
                  BattleUtil.backPlayer(player, BattleMatch.this._battle, true);
               }

            }
         }
      }

      public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill) {
         Player actorPlayer = actor.getPlayer();
         Player attackerPlayer = attacker.getPlayer();
         if (BattleMatch.this._status == 2 && attackerPlayer.getTeam() != actorPlayer.getTeam()) {
            if (actorPlayer.getTeam() == TeamType.BLUE) {
               BattleMatch.this.dmg2 = (long)((double)BattleMatch.this.dmg2 + Math.min(actorPlayer.getCurrentHp() + actorPlayer.getCurrentCp(), damage));
            } else {
               BattleMatch.this.dmg1 = (long)((double)BattleMatch.this.dmg1 + Math.min(actorPlayer.getCurrentHp() + actorPlayer.getCurrentCp(), damage));
            }

         }
      }

      public void onDeath(Creature actor, Creature killer) {
         if (BattleMatch.this._status == 2) {
            BattleMatch.this.giveRewardPerKill(actor, killer);
            BattleMatch.this.checkLive();
            Player diePlayer;
            if (killer.isPlayer()) {
               diePlayer = killer.getPlayer();
            }

            if (actor.isPlayer()) {
               diePlayer = actor.getPlayer();
               diePlayer.broadcastUserInfo(false);
            }
         }

      }

      // $FF: synthetic method
      PlayerListenerImpl(Object x1) {
         this();
      }
   }
}
