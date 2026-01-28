package services.community.custom.roulette.model;

public class RouletteResult {
   private int position;
   private RouletteItem winItem;

   public int getPosition() {
      return this.position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public RouletteItem getWinItem() {
      return this.winItem;
   }

   public void setWinItem(RouletteItem winItem) {
      this.winItem = winItem;
   }
}
