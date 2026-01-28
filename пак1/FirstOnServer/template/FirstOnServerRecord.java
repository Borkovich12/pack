package events.FirstOnServer.template;

import events.FirstOnServer.type.FirstOnServerType;

public class FirstOnServerRecord {
   private final FirstOnServerType type;
   private final int id;
   private final int winnerId;
   private final String winnerName;

   public FirstOnServerRecord(FirstOnServerType type, int id, int winnerId, String winnerName) {
      this.type = type;
      this.id = id;
      this.winnerId = winnerId;
      this.winnerName = winnerName;
   }

   public FirstOnServerType getType() {
      return this.type;
   }

   public int getId() {
      return this.id;
   }

   public int getWinnerId() {
      return this.winnerId;
   }

   public String getWinnerName() {
      return this.winnerName;
   }
}
