package services.community.custom.roulette.model;

import java.util.List;

public class ItemPositionsHistory {
   private final List<ItemPosition> positions;

   public ItemPositionsHistory(List<ItemPosition> positions) {
      this.positions = positions;
   }

   public List<ItemPosition> getPositions() {
      return this.positions;
   }
}
