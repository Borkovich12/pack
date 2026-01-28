package services.community.custom.roulette.model;

import java.util.Objects;

public class RouletteItemKey {
   private final int itemId;
   private final int itemEnchant;

   public RouletteItemKey(int itemId, int itemEnchant) {
      this.itemId = itemId;
      this.itemEnchant = itemEnchant;
   }

   public int getItemId() {
      return this.itemId;
   }

   public int getItemEnchant() {
      return this.itemEnchant;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RouletteItemKey itemKey = (RouletteItemKey)o;
         return this.itemId == itemKey.itemId && this.itemEnchant == itemKey.itemEnchant;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.itemId, this.itemEnchant});
   }
}
