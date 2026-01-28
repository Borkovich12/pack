package services.community.custom.roulette.model;

public class ItemPosition {
   private final int itemId;
   private final int position;

   public ItemPosition(int itemId, int position) {
      this.itemId = itemId;
      this.position = position;
   }

   public int getItemId() {
      return this.itemId;
   }

   public int getPosition() {
      return this.position;
   }
}
