package services.community.custom.progress;

public class ProgressRewardData implements Cloneable {
   private ProgressRewardType _rewardType;
   private int _itemId;
   private int _itemCount;
   private int _premiumHours;
   private int _premiumIndex;
   private int _premiumBuffHours;
   private int _heroHours;
   private int _nickColorCount;

   public ProgressRewardData(ProgressRewardType rewardType) {
      this._rewardType = rewardType;
   }

   public ProgressRewardType getRewardType() {
      return this._rewardType;
   }

   public int getItemId() {
      return this._itemId;
   }

   public void setItemId(int itemId) {
      this._itemId = itemId;
   }

   public int getItemCount() {
      return this._itemCount;
   }

   public void setItemCount(int itemCount) {
      this._itemCount = itemCount;
   }

   public int getPremiumHours() {
      return this._premiumHours;
   }

   public void setPremiumHours(int premiumHours) {
      this._premiumHours = premiumHours;
   }

   public void setPremiumIndex(int premiumIndex) {
      this._premiumIndex = premiumIndex;
   }

   public int getPremiumIndex() {
      return this._premiumIndex;
   }

   public int getPremiumBuffHours() {
      return this._premiumBuffHours;
   }

   public void setPremiumBuffHours(int premiumBuffHours) {
      this._premiumBuffHours = premiumBuffHours;
   }

   public int getHeroHours() {
      return this._heroHours;
   }

   public void setHeroHours(int heroHours) {
      this._heroHours = heroHours;
   }

   public int getNickColorCount() {
      return this._nickColorCount;
   }

   public void setNickColorCount(int nickColorCount) {
      this._nickColorCount = nickColorCount;
   }

   public ProgressRewardData clone() {
      return new ProgressRewardData(this.getRewardType());
   }

   public boolean equals(Object o) {
      if (o instanceof ProgressRewardData) {
         ProgressRewardData rewardData = (ProgressRewardData)o;
         return rewardData.getRewardType() == this.getRewardType();
      } else {
         return false;
      }
   }
}
