package services.community.custom.roulette.model;

import java.util.Objects;

public class RouletteItem {
   private final int groupId;
   private final int itemId;
   private final long itemCount;
   private final String itemName;
   private final String itemIcon;
   private final int enchant;
   private final double chance;
   private final boolean disabled;
   private final int dailyCount;
   private final String startTime;
   private final String endTime;
   private final boolean announce;
   private final boolean showEffect;

   public RouletteItem(int groupId, int itemId, long itemCount, String itemName, String itemIcon, int enchant, double chance, boolean disabled, int dailyCount, String startTime, String endTime, boolean announce, boolean showEffect) {
      this.groupId = groupId;
      this.itemId = itemId;
      this.itemCount = itemCount;
      this.itemName = itemName;
      this.itemIcon = itemIcon;
      this.enchant = enchant;
      this.chance = chance;
      this.disabled = disabled;
      this.dailyCount = dailyCount;
      this.startTime = startTime;
      this.endTime = endTime;
      this.announce = announce;
      this.showEffect = showEffect;
   }

   public int getGroupId() {
      return this.groupId;
   }

   public int getItemId() {
      return this.itemId;
   }

   public long getItemCount() {
      return this.itemCount;
   }

   public String getItemName() {
      return this.itemName;
   }

   public String getItemIcon() {
      return this.itemIcon;
   }

   public int getEnchant() {
      return this.enchant;
   }

   public double getChance() {
      return this.chance;
   }

   public boolean isDisabled() {
      return this.disabled;
   }

   public int getDailyCount() {
      return this.dailyCount;
   }

   public String getStartTime() {
      return this.startTime;
   }

   public String getEndTime() {
      return this.endTime;
   }

   public boolean isAnnounce() {
      return this.announce;
   }

   public boolean isShowEffect() {
      return this.showEffect;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RouletteItem that = (RouletteItem)o;
         return this.groupId == that.groupId && this.itemId == that.itemId && this.itemCount == that.itemCount && this.enchant == that.enchant && this.chance == that.chance && this.announce == that.announce && this.showEffect == that.showEffect && Objects.equals(this.itemName, that.itemName) && Objects.equals(this.itemIcon, that.itemIcon);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.groupId, this.itemId, this.itemCount, this.itemName, this.itemIcon, this.enchant, this.chance, this.announce, this.showEffect});
   }
}
