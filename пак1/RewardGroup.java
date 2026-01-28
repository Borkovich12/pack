package l2.gameserver.model.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.Player;

public class RewardGroup implements Cloneable {
   private double _chance;
   private double _premiumChance;
   private boolean _isAdena = false;
   private boolean _isSealStone = false;
   private boolean _notRate = false;
   private List<RewardData> _items = new ArrayList();
   private double _chanceSum;
   private double _chancePremiumSum;

   public RewardGroup(double chance, double premiumChance) {
      this.setChance(chance);
      this.setPremiumChance(premiumChance);
   }

   public RewardGroup(double chance) {
      this.setChance(chance);
      this.setPremiumChance(chance);
   }

   public boolean notRate() {
      return this._notRate;
   }

   public void setNotRate(boolean notRate) {
      this._notRate = notRate;
   }

   public double getChance() {
      return this._chance;
   }

   public void setChance(double chance) {
      this._chance = chance;
   }

   public double getPremiumChance() {
      return this._premiumChance;
   }

   public void setPremiumChance(double chance) {
      this._premiumChance = chance;
   }

   public boolean isAdena() {
      return this._isAdena;
   }

   public boolean isSealStone() {
      return this._isSealStone;
   }

   public void setIsAdena(boolean isAdena) {
      this._isAdena = isAdena;
   }

   public void addData(RewardData item) {
      if (item.getItem().isAdena()) {
         this._isAdena = true;
      } else if (item.getItem().isSealStone()) {
         this._isSealStone = true;
      }

      this._chanceSum += item.getChance();
      this._chancePremiumSum += item.getPremiumChance();
      item.setChanceInGroup(this._chanceSum);
      item.setChancePremiumInGroup(this._chancePremiumSum);
      this._items.add(item);
   }

   public List<RewardData> getItems() {
      return this._items;
   }

   public RewardGroup clone() {
      RewardGroup ret = new RewardGroup(this._chance, this._premiumChance);
      Iterator var2 = this._items.iterator();

      while(var2.hasNext()) {
         RewardData i = (RewardData)var2.next();
         ret.addData(i.clone());
      }

      return ret;
   }

   public List<RewardItem> roll(RewardType type, Player player, double mod, boolean isRaid, boolean isSiegeGuard) {
      switch(type) {
      case NOT_RATED_GROUPED:
      case NOT_RATED_NOT_GROUPED:
         return this.rollItems(player, mod, 1.0D, 1.0D);
      case SWEEP:
         return this.rollSpoil(player, Config.RATE_DROP_SPOIL, player.getRateSpoil(), mod);
      case RATED_GROUPED:
         if (this._isAdena) {
            return this.rollAdena(player, mod, player.getRateAdena());
         } else if (this._isSealStone) {
            return this.rollSealStones(player, mod, player.getRateSealStones());
         } else if (isRaid) {
            return this.rollItems(player, mod, Config.RATE_DROP_RAIDBOSS * (double)player.getBonus().getDropRaidItems(), 1.0D);
         } else {
            if (isSiegeGuard) {
               return this.rollItems(player, mod, Config.RATE_DROP_SIEGE_GUARD, 1.0D);
            }

            return this.rollItems(player, mod, Config.RATE_DROP_ITEMS, player.getRateItems());
         }
      default:
         return Collections.emptyList();
      }
   }

   private List<RewardItem> rollSealStones(Player player, double mod, double playerRate) {
      List<RewardItem> ret = this.rollItems(player, mod, Config.RATE_DROP_SEAL_STONES, playerRate);

      RewardItem rewardItem;
      for(Iterator var7 = ret.iterator(); var7.hasNext(); rewardItem.isSealStone = true) {
         rewardItem = (RewardItem)var7.next();
      }

      return ret;
   }

   public List<RewardItem> rollItems(Player player, double mod, double baseRate, double playerRate) {
      if (mod <= 0.0D) {
         return Collections.emptyList();
      } else {
         double rate;
         if (this._notRate) {
            rate = Math.min(mod, 1.0D);
         } else {
            rate = baseRate * playerRate * mod;
         }

         double mult = Math.ceil(rate);
         boolean firstPass = true;
         List<RewardItem> ret = new ArrayList(this._items.size() * 3 / 2);
         double chance = player.hasBonus() ? this._premiumChance : this._chance;

         for(long n = 0L; (double)n < mult; ++n) {
            double gmult = rate - (double)n;
            if ((double)Rnd.get(1, 1000000) <= chance * Math.min(gmult, 1.0D)) {
               if (!Config.ALT_MULTI_DROP) {
                  this.rollFinal(player, this._items, ret, Math.max(gmult, 1.0D), firstPass);
                  break;
               }

               this.rollFinal(player, this._items, ret, 1.0D, firstPass);
            }

            firstPass = false;
         }

         return ret;
      }
   }

   private List<RewardItem> rollSpoil(Player player, double baseRate, double playerRate, double mod) {
      if (mod <= 0.0D) {
         return Collections.emptyList();
      } else {
         double rate;
         if (this._notRate) {
            rate = Math.min(mod, 1.0D);
         } else {
            rate = baseRate * playerRate * mod;
         }

         double mult = Math.ceil(rate);
         boolean firstPass = true;
         List<RewardItem> ret = new ArrayList(this._items.size() * 3 / 2);
         double chance = player.hasBonus() ? this._premiumChance : this._chance;

         for(long n = 0L; (double)n < mult; ++n) {
            if ((double)Rnd.get(1, 1000000) <= chance * Math.min(rate - (double)n, 1.0D)) {
               this.rollFinal(player, this._items, ret, 1.0D, firstPass);
            }

            firstPass = false;
         }

         return ret;
      }
   }

   private List<RewardItem> rollAdena(Player player, double mod, double playerRate) {
      return this.rollAdena(player, mod, Config.RATE_DROP_ADENA, playerRate);
   }

   private List<RewardItem> rollAdena(Player player, double mod, double baseRate, double playerRate) {
      double playerChance = player.hasBonus() ? this._premiumChance : this._chance;
      double chance = playerChance;
      if (mod > 10.0D) {
         mod *= playerChance / 1000000.0D;
         chance = 1000000.0D;
      }

      if (mod <= 0.0D) {
         return Collections.emptyList();
      } else if ((double)Rnd.get(1, 1000000) > chance) {
         return Collections.emptyList();
      } else {
         double rate = baseRate * playerRate * mod;
         List<RewardItem> ret = new ArrayList(this._items.size());
         this.rollFinal(player, this._items, ret, rate, true);

         RewardItem i;
         for(Iterator var15 = ret.iterator(); var15.hasNext(); i.isAdena = true) {
            i = (RewardItem)var15.next();
         }

         return ret;
      }
   }

   private void rollFinal(Player player, List<RewardData> items, List<RewardItem> ret, double mult, boolean firstPass) {
      double chanceSum = player.hasBonus() ? this._chancePremiumSum : this._chanceSum;
      int chance = Rnd.get(0, (int)Math.max(chanceSum, 1000000.0D));
      Iterator var12 = items.iterator();

      while(var12.hasNext()) {
         RewardData i = (RewardData)var12.next();
         if (firstPass || !i.onePassOnly()) {
            double chanceInGroup = player.hasBonus() ? i.getChancePremiumInGroup() : i.getChanceInGroup();
            double itemChance = player.hasBonus() ? i.getPremiumChance() : i.getChance();
            if ((double)chance < chanceInGroup && (double)chance > chanceInGroup - itemChance) {
               double imult = i.notRate() ? 1.0D : mult;
               long count = (long)Math.floor((double)i.getMinDrop() * imult);
               long max = (long)Math.ceil((double)i.getMaxDrop() * imult);
               if (count != max) {
                  count = Rnd.get(count, max);
               }

               RewardItem t = null;
               Iterator var23 = ret.iterator();

               while(var23.hasNext()) {
                  RewardItem r = (RewardItem)var23.next();
                  if (i.getItemId() == r.itemId) {
                     t = r;
                     break;
                  }
               }

               if (t == null) {
                  ret.add(t = new RewardItem(i.getItemId()));
                  t.count = count;
                  t.enchantMin = i.getEnchantMin();
                  t.enchantMax = i.getEnchantMax();
               } else if (!i.notRate()) {
                  t.count = SafeMath.addAndLimit(t.count, count);
               }
               break;
            }
         }
      }

   }
}
