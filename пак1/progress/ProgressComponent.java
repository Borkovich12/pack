package services.community.custom.progress;

import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Player;

public class ProgressComponent {
   public static final String VAR_NAME_FORMAT = "achievement_%d_%s";
   public static final String VAR_STAGE_REWARDED_FORMAT = "stage_rewarded_";
   public static final String VAR_IS_COMPLETED = "is_completed";
   public static final String VAR_COMPLETED_SET_COUNTS = "completed_set_counts";
   public static final String VAR_ACTIVE_STAGE = "active_stage";
   private final ProgressInfo _achInfo;
   private final HardReference<Player> _playerRef;
   private final ProgressCounter _counter;

   public ProgressComponent(ProgressInfo achInfo, int activeStageId, Player player) {
      this._achInfo = achInfo;
      this._playerRef = player.getRef();
      this._counter = achInfo.getMetricType().getCounter(player, activeStageId, achInfo);
   }

   private Player getPlayer() {
      return (Player)this._playerRef.get();
   }

   public ProgressInfo getAchInfo() {
      return this._achInfo;
   }

   private boolean isCompetedVar() {
      return this.getVarB("is_completed", false);
   }

   public void setCompletedVar(boolean force) {
      this.setVar("is_completed", String.valueOf(force));
   }

   private String getVar(String key, String defVal) {
      Player player = this.getPlayer();
      if (player == null) {
         return defVal;
      } else {
         String val = player.getVar(String.format("achievement_%d_%s", this._achInfo.getId(), key));
         return val != null ? val : defVal;
      }
   }

   private int getVar(String key, int defVal) {
      return Integer.parseInt(this.getVar(key, String.valueOf(defVal)));
   }

   private boolean getVarB(String key, boolean defVal) {
      return Boolean.parseBoolean(this.getVar(key, String.valueOf(defVal)));
   }

   private void setVar(String key, String val) {
      Player player = this.getPlayer();
      if (player != null) {
         player.setVar(String.format("achievement_%d_%s", this._achInfo.getId(), key), val, -1L);
      }
   }

   private void setVar(String key, int val) {
      this.setVar(key, String.valueOf(val));
   }

   public boolean isCompleted() {
      return this.isCompetedVar();
   }

   public ProgressCounter getCounter() {
      return this._counter;
   }

   public void onMetricEvent(Object... args) {
      Player player = this.getPlayer();
      if (player != null) {
         if (this._achInfo.testConds(this.getPlayer(), args)) {
            ThreadPoolManager.getInstance().execute(new ProgressComponent.EventMetric(player, args));
         }
      }
   }

   public void calculatePoints(Player player) {
      player.setVar("completed_set_counts", String.valueOf(player.getVarInt("completed_set_counts", 0) + 1), -1L);
   }

   private class EventMetric extends RunnableImpl {
      private final Object[] _args;
      private final HardReference<Player> _playerRef;

      private EventMetric(Player player, Object[] args) {
         this._args = args;
         this._playerRef = player.getRef();
      }

      public void runImpl() throws Exception {
         Player player = (Player)this._playerRef.get();
         if (player != null) {
            ProgressCounter counter = ProgressComponent.this.getCounter();
            int value = counter.incrementAndGetValue();
            if (value >= ProgressComponent.this._achInfo.getValue()) {
               ProgressComponent.this.setCompletedVar(true);
               ProgressComponent.this.calculatePoints(player);
               if (player.isLangRus()) {
                  player.sendMessage("Вы получили достижение " + ProgressComponent.this._achInfo.getName(player) + ".");
               } else {
                  player.sendMessage("You got the achievement " + ProgressComponent.this._achInfo.getName(player) + ".");
               }
            }

            counter.store();
         }
      }

      // $FF: synthetic method
      EventMetric(Player x1, Object[] x2, Object x3) {
         this(x1, x2);
      }
   }
}
