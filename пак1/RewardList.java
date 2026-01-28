package l2.gameserver.model.reward;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;

public class RewardList extends ArrayList<RewardGroup> {
   public static final int MAX_CHANCE = 1000000;
   private final RewardType _type;
   private final boolean _autoLoot;

   public RewardList(RewardType rewardType, boolean a) {
      super(5);
      this._type = rewardType;
      this._autoLoot = a;
   }

   public List<RewardItem> roll(Player player) {
      return this.roll(player, 1.0D, false, false);
   }

   public List<RewardItem> roll(Player player, double mod) {
      return this.roll(player, mod, false, false);
   }

   public List<RewardItem> roll(Player player, double mod, boolean isRaid) {
      return this.roll(player, mod, isRaid, false);
   }

   public List<RewardItem> roll(Player player, double mod, boolean isRaid, boolean isSiegeGuard) {
      List<RewardItem> temp = new ArrayList(this.size());
      Iterator var7 = this.iterator();

      while(true) {
         List tdl;
         do {
            if (!var7.hasNext()) {
               return temp;
            }

            RewardGroup g = (RewardGroup)var7.next();
            tdl = g.roll(this._type, player, mod, isRaid, isSiegeGuard);
         } while(tdl.isEmpty());

         Iterator var10 = tdl.iterator();

         while(var10.hasNext()) {
            RewardItem itd = (RewardItem)var10.next();
            temp.add(itd);
         }
      }
   }

   public int validate() {
      int validationResult = 0;

      Iterator var2;
      RewardGroup g;
      int premiumChanceSum;
      Iterator var5;
      RewardData d;
      Iterator var7;
      RewardData d;
      double premiumChance;
      double mod;
      for(var2 = this.iterator(); var2.hasNext(); validationResult |= 1) {
         g = (RewardGroup)var2.next();
         premiumChanceSum = 0;

         for(var5 = g.getItems().iterator(); var5.hasNext(); premiumChanceSum += (int)d.getChance()) {
            d = (RewardData)var5.next();
         }

         if (premiumChanceSum <= 1000000) {
            break;
         }

         mod = 1000000.0D / (double)premiumChanceSum;
         var7 = g.getItems().iterator();

         while(var7.hasNext()) {
            d = (RewardData)var7.next();
            premiumChance = d.getChance() * mod;
            d.setChance(premiumChance);
         }

         g.setChance(1000000.0D);
      }

      for(var2 = this.iterator(); var2.hasNext(); validationResult |= 2) {
         g = (RewardGroup)var2.next();
         premiumChanceSum = 0;

         for(var5 = g.getItems().iterator(); var5.hasNext(); premiumChanceSum += (int)d.getPremiumChance()) {
            d = (RewardData)var5.next();
         }

         if (premiumChanceSum <= 1000000) {
            break;
         }

         mod = 1000000.0D / (double)premiumChanceSum;
         var7 = g.getItems().iterator();

         while(var7.hasNext()) {
            d = (RewardData)var7.next();
            premiumChance = d.getPremiumChance() * mod;
            d.setPremiumChance(premiumChance);
         }

         g.setPremiumChance(1000000.0D);
      }

      return validationResult;
   }

   public boolean isAutoLoot() {
      return this._autoLoot;
   }

   public RewardType getType() {
      return this._type;
   }
}
