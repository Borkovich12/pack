package events.battle.tasks;

import events.battle.model.BattleMatch;
import events.battle.util.BattleUtil;

public class BattleAnnounce implements Runnable {
   private BattleMatch match;
   private int time;

   public BattleAnnounce(BattleMatch match) {
      this.match = match;
      this.time = match.getType().getTimeBattle();
   }

   public void run() {
      --this.time;
      if (this.time >= 1) {
         BattleUtil.sayToParticipants(this.match.getAllPlayers(), this.match.getBattle().getSpectators(), true, "events.battle.model.BattleMatch.announce.endBattleAnnounce", new String[]{String.valueOf(this.time)});
      }

   }
}
