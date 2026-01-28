package events.FirstOnServer.template;

import events.FirstOnServer.type.FirstOnServerRewardType;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;

public class FirstOnServerRewardTemplate implements Cloneable {
   private FirstOnServerRewardType _rewardType;
   private int _itemId;
   private int _itemCount;
   private int _premiumHours;
   private int _premiumBuffHours;
   private int _heroHours;
   private int _vipHours;

   public FirstOnServerRewardTemplate(FirstOnServerRewardType rewardType) {
      this._rewardType = rewardType;
   }

   public FirstOnServerRewardType getRewardType() {
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

   public int getVipHours() {
      return this._vipHours;
   }

   public void setVipHours(int vipHours) {
      this._vipHours = vipHours;
   }

   public String getRewardString(Player player) {
      if (this._rewardType == FirstOnServerRewardType.ITEM) {
         return this._itemCount + " " + ItemHolder.getInstance().getTemplate(this._itemId).getName();
      } else if (this._rewardType == FirstOnServerRewardType.PREMIUM) {
         return player.isLangRus() ? this._premiumHours + "ч. премиума" : this._premiumHours + "h. premium";
      } else if (this._rewardType == FirstOnServerRewardType.HERO) {
         return player.isLangRus() ? this._heroHours + "ч. статус героя" : this._heroHours + "h. hero status";
      } else {
         return "";
      }
   }

   public FirstOnServerRewardTemplate clone() {
      return new FirstOnServerRewardTemplate(this.getRewardType());
   }

   public boolean equals(Object o) {
      if (o instanceof FirstOnServerRewardTemplate) {
         FirstOnServerRewardTemplate rewardData = (FirstOnServerRewardTemplate)o;
         return rewardData.getRewardType() == this.getRewardType();
      } else {
         return false;
      }
   }
}
