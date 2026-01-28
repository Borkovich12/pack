package events.battle.model;

public class BattleAug {
   private int id;
   private int _variation_stat1;
   private int _variation_stat2;

   public BattleAug(int i, int var1, int var2) {
      this.id = i;
      this._variation_stat1 = var1;
      this._variation_stat2 = var2;
   }

   public int getId() {
      return this.id;
   }

   public int getVariationStat1() {
      return this._variation_stat1;
   }

   public int getVariationStat2() {
      return this._variation_stat2;
   }
}
