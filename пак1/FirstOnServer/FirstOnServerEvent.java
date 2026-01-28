package events.FirstOnServer;

import events.FirstOnServer.commands.FirstOnServerVoiced;
import events.FirstOnServer.dao.FirstOnServerDAO;
import events.FirstOnServer.data.FirstOnServerRewardsHolder;
import events.FirstOnServer.data.FirstOnServerRewardsParser;
import events.FirstOnServer.listeners.OnAugmentItemListenerImpl;
import events.FirstOnServer.listeners.OnBecomeHeroListenerImpl;
import events.FirstOnServer.listeners.OnChangeClassListenerImpl;
import events.FirstOnServer.listeners.OnCraftItemListenerImpl;
import events.FirstOnServer.listeners.OnDonateItemListenerImpl;
import events.FirstOnServer.listeners.OnEnchantItemListenerImpl;
import events.FirstOnServer.listeners.OnEnchantSkillListenerImpl;
import events.FirstOnServer.listeners.OnFinishQuestListenerImpl;
import events.FirstOnServer.listeners.OnKillListenerImpl;
import events.FirstOnServer.listeners.OnLevelChangeListenerImpl;
import events.FirstOnServer.listeners.OnPvpPkKillListenerImpl;
import events.FirstOnServer.template.FirstOnServerRecord;
import events.FirstOnServer.template.FirstOnServerTemplate;
import events.FirstOnServer.type.FirstOnServerType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.listener.Listener;
import l2.gameserver.Announcements;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.listener.CharListener;
import l2.gameserver.listener.GameListener;
import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstOnServerEvent extends Functions implements ScriptFile {
   private static final Logger LOGGER = LoggerFactory.getLogger(FirstOnServerEvent.class);
   private static FirstOnServerEvent _instance = null;
   private final Map<Integer, FirstOnServerRecord> _winnerOnLevels = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnKillNpcs = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnKillRaidBosses = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnPvps = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnPks = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnQuests = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnCraftItems = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnEnchantItems = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnEnchantSkills = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnBecomeHeroes = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnAugmentItems = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnDonateItems = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnChangeClass = new ConcurrentHashMap();
   private final Map<Integer, FirstOnServerRecord> _winnerOnVoteServer = new ConcurrentHashMap();
   private List<NpcInstance> _manager = null;
   private final List<? extends Listener<?>> _listenersInstances = Arrays.asList(new OnLevelChangeListenerImpl(), new OnKillListenerImpl(), new OnPvpPkKillListenerImpl(), new OnFinishQuestListenerImpl(), new OnCraftItemListenerImpl(), new OnChangeClassListenerImpl(), new OnEnchantItemListenerImpl(), new OnEnchantSkillListenerImpl(), new OnBecomeHeroListenerImpl(), new OnAugmentItemListenerImpl(), new OnDonateItemListenerImpl());
   private static boolean _active = false;

   public static FirstOnServerEvent getInstance() {
      return _instance;
   }

   private static boolean isActive() {
      return IsActive("FirstOnServer");
   }

   public void startEvent() {
      Player player = this.getSelf();
      if (player.getPlayerAccess().IsEventGm) {
         if (SetActive("FirstOnServer", true)) {
            this.spawnEventManagers();
            this.init();
            LOGGER.info("Event: First On Server started.");
            Announcements.getInstance().announceByCustomMessage("FirstOnServerEvent.AnnounceEventStarted", (String[])null);
         } else {
            player.sendMessage("Event 'First On Server' already started.");
         }

         _active = true;
         this.show("admin/events/events.htm", player);
      }
   }

   public void stopEvent() {
      Player player = this.getSelf();
      if (player.getPlayerAccess().IsEventGm) {
         if (SetActive("FirstOnServer", false)) {
            this.unSpawnEventManagers();
            this.stop();
            LOGGER.info("Event: First On Server stopped.");
            Announcements.getInstance().announceByCustomMessage("FirstOnServerEvent.AnnounceEventStopped", (String[])null);
         } else {
            player.sendMessage("Event 'First On Server' not started.");
         }

         _active = false;
         this.show("admin/events/events.htm", player);
      }
   }

   public void onLoad() {
      _instance = this;
      if (isActive()) {
         _active = true;
         this.spawnEventManagers();
         this.init();
         LOGGER.info("Loaded Event: First On Server [state: activated]");
      } else {
         LOGGER.info("Loaded Event: First On Server [state: deactivated]");
      }

   }

   public void onReload() {
      this.unSpawnEventManagers();
   }

   public void onShutdown() {
      this.unSpawnEventManagers();
   }

   private void spawnEventManagers() {
      FirstOnServerRewardsHolder holder = FirstOnServerRewardsHolder.getInstance();
      if (holder.getManagerId() > 0) {
         this._manager = new ArrayList();
         Iterator var2 = holder.getManagerSpawn().iterator();

         while(var2.hasNext()) {
            Location loc = (Location)var2.next();
            this._manager.add(NpcUtils.spawnSingle(holder.getManagerId(), loc));
         }
      }

   }

   private void unSpawnEventManagers() {
      FirstOnServerRewardsHolder holder = FirstOnServerRewardsHolder.getInstance();
      if (holder.getManagerId() > 0 && this._manager != null) {
         Iterator var2 = this._manager.iterator();

         while(var2.hasNext()) {
            NpcInstance manager = (NpcInstance)var2.next();
            manager.deleteMe();
         }

         this._manager.clear();
         this._manager = null;
      }

   }

   public void init() {
      FirstOnServerRewardsParser.getInstance().load();
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new FirstOnServerVoiced());
      this.loadValuesInDb();
      Iterator var1 = this._listenersInstances.iterator();

      while(var1.hasNext()) {
         Listener<?> listener = (Listener)var1.next();
         if (listener instanceof PlayerListener) {
            PlayerListenerList.addGlobal((PlayerListener)listener);
         } else if (listener instanceof CharListener) {
            CharListenerList.addGlobal((CharListener)listener);
         } else {
            if (!(listener instanceof GameListener)) {
               throw new IllegalStateException("Unknown listener " + listener.getClass());
            }

            DelayedItemsManager.getDonateItemListenerList().add((GameListener)listener);
         }
      }

   }

   public void stop() {
      Iterator var1 = this._listenersInstances.iterator();

      while(var1.hasNext()) {
         Listener<?> listener = (Listener)var1.next();
         if (listener instanceof PlayerListener) {
            PlayerListenerList.removeGlobal((PlayerListener)listener);
         } else if (listener instanceof CharListener) {
            CharListenerList.removeGlobal((CharListener)listener);
         } else if (listener instanceof GameListener) {
            DelayedItemsManager.getDonateItemListenerList().remove((GameListener)listener);
         }
      }

   }

   private void loadValuesInDb() {
      List<FirstOnServerRecord> records = FirstOnServerDAO.getInstance().load();
      Iterator var2 = records.iterator();

      while(var2.hasNext()) {
         FirstOnServerRecord record = (FirstOnServerRecord)var2.next();
         FirstOnServerTemplate template = FirstOnServerRewardsHolder.getInstance().getRewardByType(record.getType(), record.getId());
         if (template == null) {
            LOGGER.warn("Failed to load data from database because type=" + record.getType().name() + " and id=" + record.getId() + " was not found");
         } else if (template.getType() == FirstOnServerType.LEVEL) {
            this._winnerOnLevels.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.KILL_MOBS) {
            this._winnerOnKillNpcs.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.KILL_RAID_BOSS) {
            this._winnerOnKillRaidBosses.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.PVP) {
            this._winnerOnPvps.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.PK) {
            this._winnerOnPks.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.QUEST) {
            this._winnerOnQuests.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.CRAFT) {
            this._winnerOnCraftItems.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.ENCHANT_ITEM) {
            this._winnerOnEnchantItems.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.ENCHANT_SKILL) {
            this._winnerOnEnchantSkills.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.HERO_STATUS) {
            this._winnerOnBecomeHeroes.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.LS) {
            this._winnerOnAugmentItems.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.DONATE) {
            this._winnerOnDonateItems.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.CLASS) {
            this._winnerOnChangeClass.put(template.getId(), record);
         } else if (template.getType() == FirstOnServerType.VOTE) {
            this._winnerOnVoteServer.put(template.getId(), record);
         }
      }

   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnLevels() {
      return this._winnerOnLevels;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnKillNpcs() {
      return this._winnerOnKillNpcs;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnKillRaidBosses() {
      return this._winnerOnKillRaidBosses;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnPvps() {
      return this._winnerOnPvps;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnPks() {
      return this._winnerOnPks;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnQuests() {
      return this._winnerOnQuests;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnCraftItems() {
      return this._winnerOnCraftItems;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnEnchantItems() {
      return this._winnerOnEnchantItems;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnEnchantSkills() {
      return this._winnerOnEnchantSkills;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnBecomeHeroes() {
      return this._winnerOnBecomeHeroes;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnAugmentItems() {
      return this._winnerOnAugmentItems;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnDonateItems() {
      return this._winnerOnDonateItems;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnChangeClass() {
      return this._winnerOnChangeClass;
   }

   public Map<Integer, FirstOnServerRecord> getWinnerOnVoteServer() {
      return this._winnerOnVoteServer;
   }

   public Map<Integer, FirstOnServerRecord> getWinnersByType(FirstOnServerType type) {
      if (type == FirstOnServerType.LEVEL) {
         return this.getWinnerOnLevels();
      } else if (type == FirstOnServerType.KILL_MOBS) {
         return this.getWinnerOnKillNpcs();
      } else if (type == FirstOnServerType.KILL_RAID_BOSS) {
         return this.getWinnerOnKillRaidBosses();
      } else if (type == FirstOnServerType.PVP) {
         return this.getWinnerOnPvps();
      } else if (type == FirstOnServerType.PK) {
         return this.getWinnerOnPks();
      } else if (type == FirstOnServerType.QUEST) {
         return this.getWinnerOnQuests();
      } else if (type == FirstOnServerType.CRAFT) {
         return this.getWinnerOnCraftItems();
      } else if (type == FirstOnServerType.ENCHANT_ITEM) {
         return this.getWinnerOnEnchantItems();
      } else if (type == FirstOnServerType.ENCHANT_SKILL) {
         return this.getWinnerOnEnchantSkills();
      } else if (type == FirstOnServerType.HERO_STATUS) {
         return this.getWinnerOnBecomeHeroes();
      } else if (type == FirstOnServerType.LS) {
         return this.getWinnerOnAugmentItems();
      } else if (type == FirstOnServerType.DONATE) {
         return this.getWinnerOnDonateItems();
      } else if (type == FirstOnServerType.CLASS) {
         return this.getWinnerOnChangeClass();
      } else {
         return type == FirstOnServerType.VOTE ? this.getWinnerOnVoteServer() : null;
      }
   }
}
