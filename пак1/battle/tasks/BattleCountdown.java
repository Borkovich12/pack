package events.battle.tasks;

import events.battle.enums.BattleType;
import events.battle.util.BattleUtil;

public class BattleCountdown implements Runnable {
   private BattleType _type;
   private int _timer;

   public BattleCountdown(BattleType type, int timer) {
      this._type = type;
      this._timer = timer;
   }

   public void run() {
      BattleUtil.sayToAll("events.battle.tasks.BattleCountdown.time", new String[]{this._type.getNameType(), String.valueOf(this._timer)});
   }
}
