package events.battle.tasks;

import events.battle.BattleGvG;

public class BattleRegTask implements Runnable {
   private BattleGvG battle;

   public BattleRegTask(BattleGvG battle) {
      this.battle = battle;
   }

   public void run() {
      this.battle.prepare();
   }
}
