package events.battle.tasks;

import events.battle.model.BattleMatch;
import l2.commons.threading.RunnableImpl;

public class BattleEndTask extends RunnableImpl {
   private BattleMatch match;

   public BattleEndTask(BattleMatch match) {
      this.match = match;
   }

   public void runImpl() throws Exception {
      this.match.endBattle();
   }
}
