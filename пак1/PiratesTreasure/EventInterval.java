package events.PiratesTreasure;

public class EventInterval {
   public final int hour;
   public final int minute;
   public final int category;

   public EventInterval(int h, int m, int category) {
      this.hour = h;
      this.minute = m;
      this.category = category;
   }

   public final String toString() {
      return "interval: " + this.hour + ":" + this.minute + ", g: " + this.category;
   }
}
