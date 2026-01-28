package services.community.custom.progress;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.model.Player;

public class ProgressInfo {
   private final int _id;
   private final int _value;
   private final ProgressMetricType _metricType;
   private final String _nameAddrRu;
   private final String _nameAddrEn;
   private List<ProgressCondition> _condList = new ArrayList();

   public ProgressInfo(int id, int value, ProgressMetricType metricType, String nameAddrRu, String nameAddrEn) {
      this._id = id;
      this._value = value;
      this._metricType = metricType;
      this._nameAddrRu = nameAddrRu;
      this._nameAddrEn = nameAddrEn;
   }

   public int getId() {
      return this._id;
   }

   public void addCond(ProgressCondition cond) {
      this._condList.add(cond);
   }

   public int getValue() {
      return this._value;
   }

   public List<ProgressCondition> getCondList() {
      return this._condList;
   }

   public void setCondList(List<ProgressCondition> condList) {
      this._condList = condList;
   }

   public ProgressMetricType getMetricType() {
      return this._metricType;
   }

   public String getNameAddrRu() {
      return this._nameAddrRu;
   }

   public String getNameAddrEn() {
      return this._nameAddrEn;
   }

   public String getName(Player player) {
      return player.isLangRus() ? this.getNameAddrRu() : this.getNameAddrEn();
   }

   public boolean testConds(Player player, Object... args) {
      Iterator var3 = this.getCondList().iterator();

      ProgressCondition cond;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         cond = (ProgressCondition)var3.next();
      } while(cond.test(player, args));

      return false;
   }
}
