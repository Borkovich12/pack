package services.community.custom.progress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import l2.commons.listener.Listener;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.listener.CharListener;
import l2.gameserver.listener.PlayerListener;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.OnKillListener;
import l2.gameserver.listener.actor.player.OnBossHuntingEventListener;
import l2.gameserver.listener.actor.player.OnCaptureCastleEventListener;
import l2.gameserver.listener.actor.player.OnCraftItemListener;
import l2.gameserver.listener.actor.player.OnCtfEventListener;
import l2.gameserver.listener.actor.player.OnDeathMatchEventListener;
import l2.gameserver.listener.actor.player.OnGvGEventListener;
import l2.gameserver.listener.actor.player.OnHtmlProgressOpeningListener;
import l2.gameserver.listener.actor.player.OnOlyCompetitionListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.listener.actor.player.OnRoomOfPowerEventListener;
import l2.gameserver.listener.actor.player.OnTopKillerDinoEventListener;
import l2.gameserver.listener.actor.player.OnTopKillerPkEventListener;
import l2.gameserver.listener.actor.player.OnTopKillerPvpEventListener;
import l2.gameserver.listener.actor.player.OnTreasureHuntingEventListener;
import l2.gameserver.listener.actor.player.OnTvtEventListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemTemplate.Grade;
import l2.gameserver.utils.Location;

public class ProgressMetricListeners {
   private static final ProgressMetricListeners INSTANCE = new ProgressMetricListeners();
   private List<? extends Listener<?>> _listenersInstances = Arrays.asList(new ProgressMetricListeners.ProgressOnPlayerEnter(), new ProgressMetricListeners.ProgressOnKill(), new ProgressMetricListeners.ProgressOnDeath(), new ProgressMetricListeners.ProgressOnHtmlOpening(), new ProgressMetricListeners.ProgressOnOlyCompetitionCompleted(), new ProgressMetricListeners.ProgressOnTvtEvent(), new ProgressMetricListeners.ProgressOnCtfEvent(), new ProgressMetricListeners.ProgressOnRoomOfPowerEvent(), new ProgressMetricListeners.ProgressOnDeathMatchEvent(), new ProgressMetricListeners.ProgressOnBossHuntingEvent(), new ProgressMetricListeners.ProgressOnTreasureHuntingEvent(), new ProgressMetricListeners.ProgressOnCaptureCastleEvent(), new ProgressMetricListeners.ProgressOnGvGEvent(), new ProgressMetricListeners.ProgressOnTopKillerDinoEvent(), new ProgressMetricListeners.ProgressOnTopKillerPvpEvent(), new ProgressMetricListeners.ProgressOnTopKillerPkEvent(), new ProgressMetricListeners.ProgressCraftItem());

   private ProgressMetricListeners() {
   }

   public static ProgressMetricListeners getInstance() {
      return INSTANCE;
   }

   public void metricEvent(Player player, ProgressMetricType eventType, Object... args) {
      if (ProgressHolder.getInstance().isEnabled()) {
         Map<Integer, ProgressInfoStage> stages = ProgressHolder.getInstance().getStages();
         int activeStageId = player.getVarInt("active_stage", 1);
         int totalStages = stages.size();
         if (activeStageId != totalStages + 1) {
            Map<ProgressMetricType, List<ProgressInfo>> metricTypeListMap = ProgressHolder.getInstance().getProgressMetricByStage(activeStageId);
            List<ProgressInfo> progressInfos = (List)metricTypeListMap.get(eventType);
            if (progressInfos == null || progressInfos.isEmpty()) {
               return;
            }

            Iterator var9 = progressInfos.iterator();

            while(var9.hasNext()) {
               ProgressInfo progressInfo = (ProgressInfo)var9.next();
               ProgressComponent progressComponent = new ProgressComponent(progressInfo, activeStageId, player);
               if (!progressComponent.isCompleted()) {
                  progressComponent.onMetricEvent(args);
               }
            }
         }

      }
   }

   public void init() {
      Iterator var1 = this._listenersInstances.iterator();

      while(var1.hasNext()) {
         Listener<?> listener = (Listener)var1.next();
         if (listener instanceof PlayerListener) {
            PlayerListenerList.addGlobal((PlayerListener)listener);
         } else {
            if (!(listener instanceof CharListener)) {
               throw new IllegalStateException("Unknown listener " + listener.getClass());
            }

            CharListenerList.addGlobal((CharListener)listener);
         }
      }

   }

   public void done() {
      Iterator var1 = this._listenersInstances.iterator();

      while(var1.hasNext()) {
         Listener<?> listener = (Listener)var1.next();
         if (listener instanceof PlayerListener) {
            PlayerListenerList.removeGlobal((PlayerListener)listener);
         } else if (listener instanceof CharListener) {
            CharListenerList.removeGlobal((CharListener)listener);
         }
      }

   }

   public static class ProgressCraftItem implements OnCraftItemListener {
      public void onCraftItem(Player player, int itemId, long itemCount) {
         ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(itemId);
         if (itemTemplate != null) {
            if (itemTemplate.isWeapon() && itemTemplate.getItemGrade() == Grade.S) {
               ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.CRAFT_WEAPON_S, itemTemplate);
            }

         }
      }
   }

   public static class ProgressOnTopKillerPkEvent implements OnTopKillerPkEventListener {
      public void onTopKillerPkEvent(Player player, int place) {
         if (place == 1) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_WIN_EVENT, place);
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_PK_WIN_EVENT, place);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_EVENT, place);
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_PK_EVENT, place);
         }

      }
   }

   public static class ProgressOnTopKillerPvpEvent implements OnTopKillerPvpEventListener {
      public void onTopKillerPvpEvent(Player player, int place) {
         if (place == 1) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_WIN_EVENT, place);
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_PVP_WIN_EVENT, place);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_EVENT, place);
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_PVP_EVENT, place);
         }

      }
   }

   public static class ProgressOnTopKillerDinoEvent implements OnTopKillerDinoEventListener {
      public void onTopKillerDinoEvent(Player player, int place) {
         if (place == 1) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_WIN_EVENT, place);
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_DINO_WIN_EVENT, place);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_EVENT, place);
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TOP_KILLER_DINO_EVENT, place);
         }

      }
   }

   public static class ProgressOnGvGEvent implements OnGvGEventListener {
      public void onGvGEvent(Player player, boolean isWin) {
         if (isWin) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.GVG_WIN_EVENT, isWin);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.GVG_EVENT, isWin);
         }

      }
   }

   public static class ProgressOnCaptureCastleEvent implements OnCaptureCastleEventListener {
      public void onCaptureCastleEvent(Player player, boolean isWin) {
         if (isWin) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.CAPTURE_CASTLE_WIN_EVENT, isWin);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.CAPTURE_CASTLE_EVENT, isWin);
         }

      }
   }

   public static class ProgressOnTreasureHuntingEvent implements OnTreasureHuntingEventListener {
      public void onTreasureHuntingEvent(Player player, boolean isWin) {
         if (isWin) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TREASURE_HUNTING_WIN_EVENT, isWin);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TREASURE_HUNTING_EVENT, isWin);
         }

      }
   }

   public static class ProgressOnBossHuntingEvent implements OnBossHuntingEventListener {
      public void onBossHuntingEvent(Player player, boolean isWin) {
         if (isWin) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.BOSS_HUNTING_WIN_EVENT, isWin);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.BOSS_HUNTING_EVENT, isWin);
         }

      }
   }

   public static class ProgressOnRoomOfPowerEvent implements OnRoomOfPowerEventListener {
      public void onRoomOfPowerEvent(Player player, boolean isWin) {
         if (isWin) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.ROOM_OF_POWER_WIN_EVENT, isWin);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.ROOM_OF_POWER_EVENT, isWin);
         }

      }
   }

   public static class ProgressOnDeathMatchEvent implements OnDeathMatchEventListener {
      public void onDeathMatchEvent(Player player, boolean isWin, int place) {
         if (isWin) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.DEATH_MATCH_WIN_EVENT, isWin);
         } else {
            if (place <= 10) {
               ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.DEATH_MATCH_TEN_PLACE_EVENT, isWin, place);
            }

            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.DEATH_MATCH_EVENT, isWin);
         }

      }
   }

   public static class ProgressOnCtfEvent implements OnCtfEventListener {
      public void onCtfEvent(Player player, boolean isWin) {
         if (isWin) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.CTF_WIN_EVENT, isWin);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.CTF_EVENT, isWin);
         }

      }
   }

   public static class ProgressOnTvtEvent implements OnTvtEventListener {
      public void onTvtEvent(Player player, boolean isWin) {
         if (isWin) {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TVT_WIN_EVENT, isWin);
         } else {
            ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.TVT_EVENT, isWin);
         }

      }
   }

   public static class ProgressOnOlyCompetitionCompleted implements OnOlyCompetitionListener {
      public void onOlyCompetitionCompleted(Player player, Competition competition, boolean isWin) {
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.OLYMPIAD, competition, isWin);
      }
   }

   public static class ProgressOnHtmlOpening implements OnHtmlProgressOpeningListener {
      public void onHtmlProgressOpening(Player player) {
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.SUB_LEVEL, player.getLevel());
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.LEVEL, player.getLevel());
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.PVP_KILL, player.getPvpKills());
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.PK_KILL, player.getPkKills());
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.NOBLE, player.isNoble());
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.REC_COUNT, player.getReceivedRec());
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.ENCHANT_ITEM, player);
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.ENCHANT_WEAPON, player);
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.ENCHANT_A_S_ARMOR, player);
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.ENCHANT_ARMOR_AND_JEWELRY, player);
      }
   }

   public static class ProgressOnDeath implements OnDeathListener {
      public void onDeath(Creature actor, Creature killer) {
         if (actor.isPlayer()) {
            ProgressMetricListeners.getInstance().metricEvent(actor.getPlayer(), ProgressMetricType.DEATH, killer);
         }

      }
   }

   public static class ProgressOnKill implements OnKillListener {
      public void onKill(Creature actor, Creature victim) {
         if (actor != null && actor.isPlayer() && victim != null) {
            if (victim.isNpc()) {
               if (victim.isMonster() && ((MonsterInstance)victim).getChampion() > 0) {
                  ProgressMetricListeners.getInstance().metricEvent(actor.getPlayer(), ProgressMetricType.NPC_CHAMP_KILL, victim);
               } else {
                  Player player = actor.getPlayer();
                  ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.NPC_KILL, victim);
                  Party party = player.getParty();
                  Location npcLoc = victim.getLoc();
                  if (party != null) {
                     ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.NPC_KILL_PARTY, victim);
                     Iterator var6 = party.getPartyMembers().iterator();

                     while(var6.hasNext()) {
                        Player pm = (Player)var6.next();
                        if (player.getObjectId() != pm.getObjectId() && !(npcLoc.distance3D(pm.getLoc()) > 1500.0D)) {
                           ProgressMetricListeners.getInstance().metricEvent(pm, ProgressMetricType.NPC_KILL_PARTY, victim);
                        }
                     }
                  }
               }

               if (victim instanceof RaidBossInstance) {
                  RaidBossInstance raidBoss = (RaidBossInstance)victim;
                  Location raidBossLoc = raidBoss.getLoc();
                  List<Creature> raidParticipants = new ArrayList(raidBoss.getAggroList().getCharMap().keySet());
                  Set<Player> raidPlayerParticipants = new LinkedHashSet();
                  Iterator var13 = raidParticipants.iterator();

                  while(var13.hasNext()) {
                     Creature creature = (Creature)var13.next();
                     if (creature != null && !(raidBossLoc.distance3D(creature.getLoc()) > 1500.0D) && creature instanceof Player) {
                        raidPlayerParticipants.add((Player)creature);
                     }
                  }

                  var13 = raidPlayerParticipants.iterator();

                  while(var13.hasNext()) {
                     Player raidParticipant = (Player)var13.next();
                     ProgressMetricListeners.getInstance().metricEvent(raidParticipant, ProgressMetricType.RAID_PARTICIPATION, victim);
                  }
               }
            }

         }
      }

      public boolean ignorePetOrSummon() {
         return true;
      }
   }

   public static class ProgressOnPlayerEnter implements OnPlayerEnterListener {
      public void onPlayerEnter(Player player) {
         ProgressMetricListeners.getInstance().metricEvent(player, ProgressMetricType.LOGIN, player);
      }
   }
}
