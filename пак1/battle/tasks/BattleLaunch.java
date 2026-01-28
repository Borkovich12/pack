package events.battle.tasks;

import events.battle.BattleGvG;

public class BattleLaunch implements Runnable {
   private BattleGvG battle;

   public BattleLaunch(BattleGvG battle) {
      this.battle = battle;
   }

   public void run() {
      this.battle.activateEvent(false);
   }
}
