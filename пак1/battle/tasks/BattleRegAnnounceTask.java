package events.battle.tasks;

import events.battle.enums.BattleType;
import events.battle.util.BattleUtil;
import l2.commons.threading.RunnableImpl;

public class BattleRegAnnounceTask extends RunnableImpl {
   private BattleType _type;
   private int _minutesToStart;

   public BattleRegAnnounceTask(BattleType type, int minutesToStart) {
      this._type = type;
      this._minutesToStart = minutesToStart;
   }

   public void runImpl() throws Exception {
      if (this.isAnnounce(this._type, this._minutesToStart)) {
         this.announce(this._type, this._minutesToStart);
      }

      --this._minutesToStart;
   }

   private void announce(BattleType type, int minutesToStart) {
      BattleUtil.sayToAll("events.battle.tasks.BattleCountdown.time", new String[]{type.getNameType(), String.valueOf(minutesToStart)});
   }

   private boolean isAnnounce(BattleType type, int minutesToStart) {
      int[] var3 = type.getAnnounceRegTimes();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         long time = (long)var3[var5];
         if (time == (long)minutesToStart) {
            return true;
         }
      }

      return false;
   }
}
