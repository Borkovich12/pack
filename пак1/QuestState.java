package l2.gameserver.model.quest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.OnKillListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.ExShowQuestMark;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.QuestList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.TutorialEnableClientEvent;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.network.l2.s2c.TutorialShowQuestionMark;
import l2.gameserver.network.l2.s2c.PlaySound.Type;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.spawn.PeriodOfDay;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QuestState {
   private static final Logger _log = LoggerFactory.getLogger(QuestState.class);
   public static final int RESTART_HOUR = 6;
   public static final int RESTART_MINUTES = 30;
   public static final String VAR_COND = "cond";
   public static final QuestState[] EMPTY_ARRAY = new QuestState[0];
   private final Player _player;
   private Quest _quest;
   private int _state;
   private Integer _cond = null;
   private Map<String, String> _vars = new ConcurrentHashMap();
   private Map<String, QuestTimer> _timers = new ConcurrentHashMap();
   private OnKillListener _onKillListener = null;

   public QuestState(Quest quest, Player player, int state) {
      this._quest = quest;
      this._player = player;
      player.setQuestState(this);
      this._state = state;
      quest.notifyCreate(this);
   }

   public void addExpAndSp(long exp, long sp) {
      Player player = this.getPlayer();
      if (player != null) {
         exp = (long)((double)exp * this.getRateQuestsRewardExp());
         sp = (long)((double)sp * this.getRateQuestsRewardSp());
         if (exp > 0L && sp > 0L) {
            player.addExpAndSp(exp, sp);
         } else {
            if (exp > 0L) {
               player.addExpAndSp(exp, 0L);
            }

            if (sp > 0L) {
               player.addExpAndSp(0L, sp);
            }
         }

      }
   }

   public void addNotifyOfDeath(Player player, boolean withPet) {
      QuestState.OnDeathListenerImpl listener = new QuestState.OnDeathListenerImpl();
      player.addListener(listener);
      if (withPet) {
         Summon summon = player.getPet();
         if (summon != null) {
            summon.addListener(listener);
         }
      }

   }

   public void addPlayerOnKillListener() {
      if (this._onKillListener != null) {
         throw new IllegalArgumentException("Cant add twice kill listener to player");
      } else {
         this._onKillListener = new QuestState.PlayerOnKillListenerImpl();
         this._player.addListener(this._onKillListener);
      }
   }

   public void removePlayerOnKillListener() {
      if (this._onKillListener != null) {
         this._player.removeListener(this._onKillListener);
      }

   }

   public void addRadar(int x, int y, int z) {
      Player player = this.getPlayer();
      if (player != null) {
         player.addRadar(x, y, z);
      }

   }

   public void addRadarWithMap(int x, int y, int z) {
      Player player = this.getPlayer();
      if (player != null) {
         player.addRadarWithMap(x, y, z);
      }

   }

   public void exitCurrentQuest(Quest quest) {
      Player player = this.getPlayer();
      this.exitCurrentQuest(true);
      quest.newQuestState(player, 4);
      QuestState qs = player.getQuestState(quest.getClass());
      qs.setRestartTime();
   }

   public QuestState exitCurrentQuest(boolean repeatable) {
      return this.exitCurrentQuest(repeatable, false);
   }

   public QuestState exitCurrentQuest(boolean repeatable, boolean abort) {
      Player player = this.getPlayer();
      if (player == null) {
         return this;
      } else {
         this.removePlayerOnKillListener();
         int[] var4 = this._quest.getItems();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            int itemId = var4[var6];
            ItemInstance item = player.getInventory().getItemByItemId(itemId);
            if (item != null && itemId != 57) {
               long count = item.getCount();
               player.getInventory().destroyItemByItemId(itemId, count);
               player.getWarehouse().destroyItemByItemId(itemId, count);
            }
         }

         if (repeatable) {
            player.removeQuestState(this._quest.getName());
            Quest.deleteQuestInDb(this);
            this._vars.clear();
         } else {
            Iterator var11 = this._vars.keySet().iterator();

            while(var11.hasNext()) {
               String var = (String)var11.next();
               if (var != null) {
                  this.unset(var);
               }
            }

            this.setState(3);
            Quest.updateQuestInDb(this);
         }

         if (!abort && !repeatable) {
            player.getListeners().onFinishQuest(this);
         }

         player.sendPacket(new QuestList(player));
         return this;
      }
   }

   public void abortQuest() {
      this._quest.onAbort(this);
      this.exitCurrentQuest(true, true);
   }

   public String get(String var) {
      return (String)this._vars.get(var);
   }

   public Map<String, String> getVars() {
      return this._vars;
   }

   public int getInt(String var) {
      int varint = 0;

      try {
         String val = this.get(var);
         if (val == null) {
            return 0;
         }

         varint = Integer.parseInt(val);
      } catch (Exception var4) {
         _log.error(this.getPlayer().getName() + ": variable " + var + " isn't an integer: " + varint, var4);
      }

      return varint;
   }

   public int getItemEquipped(int loc) {
      return this.getPlayer().getInventory().getPaperdollItemId(loc);
   }

   public Player getPlayer() {
      return this._player;
   }

   public Quest getQuest() {
      return this._quest;
   }

   public boolean checkQuestItemsCount(int... itemIds) {
      Player player = this.getPlayer();
      if (player == null) {
         return false;
      } else {
         int[] var3 = itemIds;
         int var4 = itemIds.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int itemId = var3[var5];
            if (player.getInventory().getCountOf(itemId) <= 0L) {
               return false;
            }
         }

         return true;
      }
   }

   public long getSumQuestItemsCount(int... itemIds) {
      Player player = this.getPlayer();
      if (player == null) {
         return 0L;
      } else {
         long count = 0L;
         int[] var5 = itemIds;
         int var6 = itemIds.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            int itemId = var5[var7];
            count += player.getInventory().getCountOf(itemId);
         }

         return count;
      }
   }

   public long getQuestItemsCount(int itemId) {
      Player player = this.getPlayer();
      return player == null ? 0L : player.getInventory().getCountOf(itemId);
   }

   public long getQuestItemsCount(int... itemsIds) {
      long result = 0L;
      int[] var4 = itemsIds;
      int var5 = itemsIds.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int id = var4[var6];
         result += this.getQuestItemsCount(id);
      }

      return result;
   }

   public boolean haveQuestItem(int itemId, int count) {
      return this.getQuestItemsCount(itemId) >= (long)count;
   }

   public boolean haveQuestItem(int itemId) {
      return this.haveQuestItem(itemId, 1);
   }

   public int getState() {
      return this._state == 4 ? 1 : this._state;
   }

   public String getStateName() {
      return Quest.getStateName(this._state);
   }

   public void giveItems(int itemId, long count) {
      if (itemId == 57) {
         this.giveItems(itemId, count, true);
         Log.LogItem(this.getPlayer(), ItemLog.QuestCreate, itemId, count, 0L, this.getQuest().getQuestIntId());
      } else {
         this.giveItems(itemId, count, false);
      }

   }

   public void giveItems(int itemId, long count, boolean rate) {
      Player player = this.getPlayer();
      if (player != null) {
         if (count <= 0L) {
            count = 1L;
         }

         if (rate) {
            count *= (long)(itemId != 57 ? this.getRateQuestsReward() : this.getRateQuestsAdenaReward());
         }

         ItemFunctions.addItem(player, itemId, count, true);
         player.sendChanges();
      }
   }

   public void giveItems(int itemId, long count, Element element, int power) {
      Player player = this.getPlayer();
      if (player != null) {
         if (count <= 0L) {
            count = 1L;
         }

         ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
         if (template != null) {
            for(int i = 0; (long)i < count; ++i) {
               ItemInstance item = ItemFunctions.createItem(itemId);
               if (element != Element.NONE) {
                  item.setAttributeElement(element, power);
               }

               player.getInventory().addItem(item);
            }

            player.sendPacket(SystemMessage.obtainItems(template.getItemId(), count, 0));
            player.sendChanges();
         }
      }
   }

   public void dropItem(NpcInstance npc, int itemId, long count) {
      Player player = this.getPlayer();
      if (player != null) {
         ItemInstance item = ItemFunctions.createItem(itemId);
         item.setCount(count);
         item.dropToTheGround(player, npc);
      }
   }

   public int rollDrop(int count, double calcChance) {
      return !(calcChance <= 0.0D) && count > 0 ? this.rollDrop(count, count, calcChance) : 0;
   }

   public int rollDrop(int min, int max, double calcChance) {
      if (!(calcChance <= 0.0D) && min > 0 && max > 0) {
         int dropmult = 1;
         calcChance *= this.getRateQuestsDrop();
         if (Config.ALT_PARTY_BONUS_FOR_QUESTS && this.getQuest().getParty() != 0) {
            Player player = this.getPlayer();
            Party party = player.getParty();
            if (party != null) {
               calcChance *= Config.ALT_PARTY_BONUS[party.getMemberCountInRange(player, Config.ALT_PARTY_DISTRIBUTION_RANGE) - 1];
            }
         }

         if (calcChance > 100.0D) {
            double chance = calcChance - 100.0D;
            calcChance = 100.0D;
            dropmult = 1 + Math.max(0, (int)(chance / 100.0D));
            if (Rnd.chance(chance - (double)(dropmult - 1) * 100.0D)) {
               ++dropmult;
            }
         }

         return Rnd.chance(calcChance) ? Rnd.get(min * dropmult, max * dropmult) : 0;
      } else {
         return 0;
      }
   }

   public double getRateQuestsDrop() {
      Player player = this.getPlayer();
      double Bonus = player == null ? 1.0D : player.getQuestDropBonusMul();
      return Config.RATE_QUESTS_DROP * Bonus * this.getQuest().getRates().getDropRate();
   }

   public double getRateQuestsReward() {
      Player player = this.getPlayer();
      double Bonus = player == null ? 1.0D : (double)player.getBonus().getQuestRewardRate();
      return Config.RATE_QUESTS_REWARD * Bonus * this.getQuest().getRates().getRewardRate();
   }

   public double getRateQuestsAdenaReward() {
      Player player = this.getPlayer();
      double Bonus = player == null ? 1.0D : (double)player.getBonus().getQuestRewardAdenaRate();
      return Config.RATE_QUESTS_ADENA_REWARD * Bonus * this.getQuest().getRates().getRewardAdenaRate();
   }

   public double getRateQuestsRewardExp() {
      Player player = this.getPlayer();
      double Bonus = player == null ? 1.0D : (double)player.getBonus().getQuestRewardRate();
      return Config.RATE_QUESTS_REWARD_EXP_SP * Bonus * this.getQuest().getRates().getExpRate();
   }

   public double getRateQuestsRewardSp() {
      Player player = this.getPlayer();
      double Bonus = player == null ? 1.0D : (double)player.getBonus().getQuestRewardRate();
      return Config.RATE_QUESTS_REWARD_EXP_SP * Bonus * this.getQuest().getRates().getSpRate();
   }

   public boolean rollAndGive(int itemId, int min, int max, int limit, double calcChance) {
      if (!(calcChance <= 0.0D) && min > 0 && max > 0 && limit > 0 && itemId > 0) {
         long count = (long)this.rollDrop(min, max, calcChance);
         if (count > 0L) {
            long alreadyCount = this.getQuestItemsCount(itemId);
            if (alreadyCount + count > (long)limit) {
               count = (long)limit - alreadyCount;
            }

            if (count > 0L) {
               this.giveItems(itemId, count, false);
               if (count + alreadyCount >= (long)limit) {
                  this.playSound("ItemSound.quest_middle");
                  return true;
               }

               this.playSound("ItemSound.quest_itemget");
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void rollAndGive(int itemId, int min, int max, double calcChance) {
      if (!(calcChance <= 0.0D) && min > 0 && max > 0 && itemId > 0) {
         int count = this.rollDrop(min, max, calcChance);
         if (count > 0) {
            this.giveItems(itemId, (long)count, false);
            this.playSound("ItemSound.quest_itemget");
         }

      }
   }

   public boolean rollAndGive(int itemId, int count, double calcChance) {
      if (!(calcChance <= 0.0D) && count > 0 && itemId > 0) {
         int countToDrop = this.rollDrop(count, calcChance);
         if (countToDrop > 0) {
            this.giveItems(itemId, (long)countToDrop, false);
            this.playSound("ItemSound.quest_itemget");
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isCompleted() {
      return this.getState() == 3;
   }

   public boolean isStarted() {
      return this.getState() == 2;
   }

   public boolean isCreated() {
      return this.getState() == 1;
   }

   public void killNpcByObjectId(int _objId) {
      NpcInstance npc = GameObjectsStorage.getNpc(_objId);
      if (npc != null) {
         npc.doDie((Creature)null);
      } else {
         _log.warn("Attemp to kill object that is not npc in quest " + this.getQuest().getQuestIntId());
      }

   }

   public String set(String var, String val) {
      return this.set(var, val, true);
   }

   public String set(String var, int intval) {
      return this.set(var, String.valueOf(intval), true);
   }

   public String set(String var, String val, boolean store) {
      if (val == null) {
         val = "";
      }

      this._vars.put(var, val);
      if (store) {
         Quest.updateQuestVarInDb(this, var, val);
      }

      return val;
   }

   public Object setState(int state) {
      Player player = this.getPlayer();
      if (player == null) {
         return null;
      } else {
         this._state = state;
         if (this.getQuest().isVisible() && this.isStarted()) {
            player.sendPacket(new ExShowQuestMark(this.getQuest().getQuestIntId()));
         }

         Quest.updateQuestInDb(this);
         player.sendPacket(new QuestList(player));
         player.getListeners().onQuestStateChange(this);
         return state;
      }
   }

   public Object setStateAndNotSave(int state) {
      Player player = this.getPlayer();
      if (player == null) {
         return null;
      } else {
         this._state = state;
         if (this.getQuest().isVisible() && this.isStarted()) {
            player.sendPacket(new ExShowQuestMark(this.getQuest().getQuestIntId()));
         }

         player.sendPacket(new QuestList(player));
         return state;
      }
   }

   public void playSound(String sound) {
      Player player = this.getPlayer();
      if (player != null) {
         player.sendPacket(new PlaySound(sound));
      }

   }

   public void playTutorialVoice(String voice) {
      Player player = this.getPlayer();
      if (player != null) {
         player.sendPacket(new PlaySound(Type.VOICE, voice, 0, 0, player.getLoc()));
      }

   }

   public void onTutorialClientEvent(int number) {
      Player player = this.getPlayer();
      if (player != null) {
         player.sendPacket(new TutorialEnableClientEvent(number));
      }

   }

   public void showQuestionMark(int number) {
      Player player = this.getPlayer();
      if (player != null) {
         player.sendPacket(new TutorialShowQuestionMark(number));
      }

   }

   public void showTutorialHTML(String html) {
      Player player = this.getPlayer();
      if (player != null) {
         if (player.isGM()) {
            Functions.sendDebugMessage(player, "Tutorial: " + html);
         }

         String text = HtmCache.getInstance().getNotNull("quests/_255_Tutorial/" + html, player);
         player.sendPacket(new TutorialShowHtml(text));
      }
   }

   public void startQuestTimer(String name, long time) {
      this.startQuestTimer(name, time, (NpcInstance)null);
   }

   public void startQuestTimer(String name, long time, NpcInstance npc) {
      QuestTimer timer = new QuestTimer(name, time, npc);
      timer.setQuestState(this);
      QuestTimer oldTimer = (QuestTimer)this.getTimers().put(name, timer);
      if (oldTimer != null) {
         oldTimer.stop();
      }

      timer.start();
   }

   public boolean isRunningQuestTimer(String name) {
      return this.getTimers().get(name) != null;
   }

   public boolean cancelQuestTimer(String name) {
      QuestTimer timer = this.removeQuestTimer(name);
      if (timer != null) {
         timer.stop();
      }

      return timer != null;
   }

   QuestTimer removeQuestTimer(String name) {
      QuestTimer timer = (QuestTimer)this.getTimers().remove(name);
      if (timer != null) {
         timer.setQuestState((QuestState)null);
      }

      return timer;
   }

   public void pauseQuestTimers() {
      this.getQuest().pauseQuestTimers(this);
   }

   public void stopQuestTimers() {
      Iterator var1 = this.getTimers().values().iterator();

      while(var1.hasNext()) {
         QuestTimer timer = (QuestTimer)var1.next();
         timer.setQuestState((QuestState)null);
         timer.stop();
      }

      this._timers.clear();
   }

   public void resumeQuestTimers() {
      this.getQuest().resumeQuestTimers(this);
   }

   Map<String, QuestTimer> getTimers() {
      return this._timers;
   }

   public long takeItems(int itemId, long count) {
      Player player = this.getPlayer();
      if (player == null) {
         return 0L;
      } else {
         ItemInstance item = player.getInventory().getItemByItemId(itemId);
         if (item == null) {
            return 0L;
         } else {
            if (count < 0L || count > item.getCount()) {
               count = item.getCount();
            }

            player.getInventory().destroyItemByItemId(itemId, count);
            player.sendPacket(SystemMessage.removeItems(itemId, count));
            Log.LogItem(player, ItemLog.QuestDelete, itemId, count, 0L, this.getQuest().getQuestIntId());
            return count;
         }
      }
   }

   public long takeAllItems(int itemId) {
      return this.takeItems(itemId, -1L);
   }

   public long takeAllItems(int... itemsIds) {
      long result = 0L;
      int[] var4 = itemsIds;
      int var5 = itemsIds.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int id = var4[var6];
         result += this.takeAllItems(id);
      }

      return result;
   }

   public long takeAllItems(Collection<Integer> itemsIds) {
      long result = 0L;

      int id;
      for(Iterator var4 = itemsIds.iterator(); var4.hasNext(); result += this.takeAllItems(id)) {
         id = (Integer)var4.next();
      }

      return result;
   }

   public String unset(String var) {
      if (var == null) {
         return null;
      } else {
         String old = (String)this._vars.remove(var);
         if (old != null) {
            Quest.deleteQuestVarInDb(this, var);
         }

         return old;
      }
   }

   private boolean checkPartyMember(Player member, int state, int maxrange, GameObject rangefrom) {
      if (member == null) {
         return false;
      } else if (rangefrom != null && maxrange > 0 && !member.isInRange(rangefrom, (long)maxrange)) {
         return false;
      } else {
         QuestState qs = member.getQuestState(this.getQuest().getName());
         return qs != null && qs.getState() == state;
      }
   }

   public List<Player> getPartyMembers(int state, int maxrange, GameObject rangefrom) {
      List<Player> result = new ArrayList();
      Party party = this.getPlayer().getParty();
      if (party == null) {
         if (this.checkPartyMember(this.getPlayer(), state, maxrange, rangefrom)) {
            result.add(this.getPlayer());
         }

         return result;
      } else {
         Iterator var6 = party.getPartyMembers().iterator();

         while(var6.hasNext()) {
            Player member = (Player)var6.next();
            if (this.checkPartyMember(member, state, maxrange, rangefrom)) {
               result.add(member);
            }
         }

         return result;
      }
   }

   public Player getRandomPartyMember(int state, int maxrangefromplayer) {
      return this.getRandomPartyMember(state, maxrangefromplayer, this.getPlayer());
   }

   public Player getRandomPartyMember(int state, int maxrange, GameObject rangefrom) {
      List<Player> list = this.getPartyMembers(state, maxrange, rangefrom);
      return list.size() == 0 ? null : (Player)list.get(Rnd.get(list.size()));
   }

   public NpcInstance addSpawn(int npcId) {
      return this.addSpawn(npcId, this.getPlayer().getX(), this.getPlayer().getY(), this.getPlayer().getZ(), 0, 0, 0);
   }

   public NpcInstance addSpawn(int npcId, int despawnDelay) {
      return this.addSpawn(npcId, this.getPlayer().getX(), this.getPlayer().getY(), this.getPlayer().getZ(), 0, 0, despawnDelay);
   }

   public NpcInstance addSpawn(int npcId, int x, int y, int z) {
      return this.addSpawn(npcId, x, y, z, 0, 0, 0);
   }

   public NpcInstance addSpawn(int npcId, int x, int y, int z, int despawnDelay) {
      return this.addSpawn(npcId, x, y, z, 0, 0, despawnDelay);
   }

   public NpcInstance addSpawn(int npcId, int x, int y, int z, int heading, int randomOffset, int despawnDelay) {
      return this.getQuest().addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay);
   }

   public NpcInstance findTemplate(int npcId) {
      Iterator var2 = SpawnManager.getInstance().getSpawners(PeriodOfDay.ALL.name()).iterator();

      Spawner spawn;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         spawn = (Spawner)var2.next();
      } while(spawn == null || spawn.getCurrentNpcId() != npcId);

      return spawn.getLastSpawn();
   }

   public int calculateLevelDiffForDrop(int mobLevel, int player) {
      return !Config.DEEPBLUE_DROP_RULES ? 0 : Math.max(player - mobLevel - Config.DEEPBLUE_DROP_MAXDIFF, 0);
   }

   public int getCond() {
      if (this._cond == null) {
         int val = this.getInt("cond");
         if ((val & Integer.MIN_VALUE) != 0) {
            val &= Integer.MAX_VALUE;

            for(int i = 1; i < 32; ++i) {
               val >>= 1;
               if (val == 0) {
                  val = i;
                  break;
               }
            }
         }

         this._cond = val;
      }

      return this._cond;
   }

   public String setCond(int newCond) {
      return this.setCond(newCond, true);
   }

   public String setCond(int newCond, boolean store) {
      if (newCond == this.getCond()) {
         return String.valueOf(newCond);
      } else {
         int oldCond = this.getInt("cond");
         this._cond = newCond;
         if ((oldCond & Integer.MIN_VALUE) != 0) {
            if (newCond > 2) {
               oldCond &= -2147483647 | (1 << newCond) - 1;
               newCond = oldCond | 1 << newCond - 1;
            }
         } else if (newCond > 2) {
            newCond = -2147483647 | 1 << newCond - 1 | (1 << oldCond) - 1;
         }

         String sVal = String.valueOf(newCond);
         String result = this.set("cond", sVal, false);
         if (store) {
            Quest.updateQuestVarInDb(this, "cond", sVal);
         }

         Player player = this.getPlayer();
         if (player != null) {
            player.sendPacket(new QuestList(player));
            if (newCond != 0 && this.getQuest().isVisible() && this.isStarted()) {
               player.sendPacket(new ExShowQuestMark(this.getQuest().getQuestIntId()));
            }
         }

         return result;
      }
   }

   public void setRestartTime() {
      Calendar reDo = Calendar.getInstance();
      if (reDo.get(11) >= 6) {
         reDo.add(5, 1);
      }

      reDo.set(11, 6);
      reDo.set(12, 30);
      this.set("restartTime", String.valueOf(reDo.getTimeInMillis()));
   }

   public boolean isNowAvailable() {
      String val = this.get("restartTime");
      if (val == null) {
         return true;
      } else {
         long restartTime = Long.parseLong(val);
         return restartTime <= System.currentTimeMillis();
      }
   }

   public void dropItemDelay(NpcInstance npc, int itemId, long count) {
      ThreadPoolManager.getInstance().schedule(() -> {
         Player player = this.getPlayer();
         if (player != null) {
            ItemInstance item = ItemFunctions.createItem(itemId);
            item.setCount(count);
            item.dropToTheGround(player, npc);
            Log.add("Added itemId: " + itemId + " count: " + count, "QuestState:giveItems(int, long, int) QuestName: " + this.getQuest().getName(), this.getPlayer());
         }
      }, 4000L);
   }

   public class PlayerOnKillListenerImpl implements OnKillListener {
      public void onKill(Creature actor, Creature victim) {
         if (victim.isPlayer()) {
            Object players;
            Iterator var5;
            Player $member;
            Player actorPlayer = (Player)actor;
            players = null;
            label38:
            switch(QuestState.this._quest.getParty()) {
            case 0:
               players = Collections.singletonList(actorPlayer);
               break;
            case 2:
               if (actorPlayer.getParty() == null) {
                  players = Collections.singletonList(actorPlayer);
                  break;
               } else {
                  players = new ArrayList(actorPlayer.getParty().getMemberCount());
                  var5 = actorPlayer.getParty().getPartyMembers().iterator();

                  while(true) {
                     if (!var5.hasNext()) {
                        break label38;
                     }

                     $member = (Player)var5.next();
                     if ($member.isInActingRange(actorPlayer)) {
                        ((List)players).add($member);
                     }
                  }
               }
            default:
               players = Collections.emptyList();
            }

            var5 = ((List)players).iterator();

            while(var5.hasNext()) {
               $member = (Player)var5.next();
               QuestState questState = $member.getQuestState(QuestState.this._quest.getClass());
               if (questState != null && !questState.isCompleted()) {
                  QuestState.this._quest.notifyKill((Player)victim, questState);
               }
            }

         }
      }

      public boolean ignorePetOrSummon() {
         return true;
      }
   }

   public class OnDeathListenerImpl implements OnDeathListener {
      public void onDeath(Creature actor, Creature killer) {
         Player player = actor.getPlayer();
         if (player != null) {
            player.removeListener(this);
            QuestState.this._quest.notifyDeath(killer, actor, QuestState.this);
         }
      }
   }
}
