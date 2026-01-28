package ai;

import events.PiratesTreasure.ConfigPiratesTreasure;
import events.PiratesTreasure.PiratesTreasure;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class PiratesKing extends Fighter {
   private boolean isFind = false;
   long _wait_timeout = 0L;
   private boolean isFirst = true;
   private ScheduledFuture<?> despawnTask = null;
   private ScheduledFuture<?> waitingTask = null;

   public PiratesKing(NpcInstance actor) {
      super(actor);
   }

   protected void onEvtSpawn() {
      NpcInstance actor = this.getActor();
      actor.setTargetable(false);
      if (this.despawnTask == null) {
         this.despawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            public void runImpl() throws Exception {
               NpcInstance actor = PiratesKing.this.getActor();
               actor.deleteMe();
            }
         }, (long)ConfigPiratesTreasure.PiratesTreasureTimeEvent * 60000L);
      }

      super.onEvtSpawn();
   }

   protected boolean thinkActive() {
      NpcInstance actor = this.getActor();
      if (actor != null && !actor.isDead()) {
         if (this._wait_timeout < System.currentTimeMillis() && !this.isFind) {
            this._wait_timeout = System.currentTimeMillis() + 60000L;
            if (this.waitingTask == null) {
               this.waitingTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                  public void runImpl() throws Exception {
                     PiratesTreasure.annoncePointInfo();
                  }
               }, 60000L);
            }

            return true;
         } else {
            if (PiratesTreasure.eventStoped) {
               actor.deleteMe();
            }

            return super.thinkActive();
         }
      } else {
         return true;
      }
   }

   protected void onIntentionAttack(Creature target) {
      if (target.isPlayable()) {
         NpcInstance actor = this.getActor();
         actor.setTargetable(true);
         if (this.isFirst) {
            Iterator var3 = ConfigPiratesTreasure.PiratesTreasureRewards.entrySet().iterator();

            while(var3.hasNext()) {
               Entry<Integer, Integer> entry = (Entry)var3.next();
               Functions.addItem((Playable)target, (Integer)entry.getKey(), (long)(Integer)entry.getValue());
            }

            this.isFirst = false;
            Announcements.getInstance().announceByCustomMessage("scripts.events.PiratesTreasure.PirateKingHasBeenFound", new String[]{String.valueOf(target.getPlayer().getName())});
         }

         this.isFind = true;
         super.onIntentionAttack(target);
      }
   }

   protected void onEvtDead(Creature killer) {
      this.stopDespawnTask();
      this.stopWaitingTask();
      Functions.executeTask("events.PiratesTreasure.PiratesTreasure", "stopEvent", new Object[]{true}, 1000L);
      super.onEvtDead(killer);
   }

   private void stopWaitingTask() {
      try {
         if (this.waitingTask != null) {
            this.waitingTask.cancel(false);
            this.waitingTask = null;
         }
      } catch (Exception var2) {
      }

   }

   private void stopDespawnTask() {
      try {
         if (this.despawnTask != null) {
            this.despawnTask.cancel(false);
            this.despawnTask = null;
         }
      } catch (Exception var2) {
      }

   }

   protected boolean randomWalk() {
      return false;
   }

   protected boolean randomAnimation() {
      return false;
   }

   protected boolean canSeeInSilentMove(Playable target) {
      return true;
   }

   protected boolean canSeeInHide(Playable target) {
      return true;
   }
}
