package services.community.custom.roulette.model;

public class RouletteStat {
   private final int _itemId;
   private final String _itemName;
   private final int _itemEnchant;
   private final long _value;
   private final int _count;

   public RouletteStat(int itemId, String itemName, int itemEnchant, long value, int count) {
      this._itemId = itemId;
      this._itemName = itemName;
      this._itemEnchant = itemEnchant;
      this._value = value;
      this._count = count;
   }

   public int getItemId() {
      return this._itemId;
   }

   public String getItemName() {
      return this._itemName;
   }

   public int getItemEnchant() {
      return this._itemEnchant;
   }

   public long getValue() {
      return this._value;
   }

   public int getCount() {
      return this._count;
   }
}
