package events.battle.tasks;

import events.battle.model.BattleMatch;

public class BattleTimer implements Runnable {
   private final BattleMatch battle;

   public BattleTimer(BattleMatch battle) {
      this.battle = battle;
   }

   public void run() {
      this.battle.broadCastTimer();
   }
}
