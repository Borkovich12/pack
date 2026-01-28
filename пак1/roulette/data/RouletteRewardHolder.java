package services.community.custom.roulette.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import l2.commons.data.xml.AbstractHolder;
import services.community.custom.roulette.model.RouletteItem;

public class RouletteRewardHolder extends AbstractHolder {
   private static final RouletteRewardHolder instance = new RouletteRewardHolder();
   private final Map<Integer, Map<Integer, List<RouletteItem>>> _rewards = new HashMap();
   private final Map<Integer, List<RouletteItem>> _rewardsByGroup = new HashMap();
   private final List<RouletteItem> _allRewards = new ArrayList();

   public static RouletteRewardHolder getInstance() {
      return instance;
   }

   public void addReward(int groupId, int groupCount, List<RouletteItem> reward) {
      Map<Integer, List<RouletteItem>> rouletteItemPair = (Map)this._rewards.get(groupId);
      List<RouletteItem> rewardsGroup = (List)this._rewardsByGroup.get(groupId);
      if (rouletteItemPair == null) {
         rouletteItemPair = new HashMap();
         this._rewards.put(groupId, rouletteItemPair);
      }

      if (rewardsGroup == null) {
         rewardsGroup = new ArrayList();
         this._rewardsByGroup.put(groupId, rewardsGroup);
      }

      this._allRewards.addAll(reward);
      ((List)rewardsGroup).addAll(reward);
      ((Map)rouletteItemPair).put(groupCount, reward);
   }

   public int getGroupForSpinCount(int spinCount) {
      int groupId = 1;
      Iterator var3 = this._rewards.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<Integer, Map<Integer, List<RouletteItem>>> entryGroup = (Entry)var3.next();
         int entryGroupId = (Integer)entryGroup.getKey();
         int entryGroupCount = (Integer)((Map)entryGroup.getValue()).keySet().stream().findFirst().orElse(0);
         if (spinCount % entryGroupCount == 0) {
            groupId = entryGroupId;
         }
      }

      return groupId;
   }

   public List<RouletteItem> getGroupRewardsBySpin(int spinCount) {
      int groupId = this.getGroupForSpinCount(spinCount);
      return (List)this._rewardsByGroup.get(groupId);
   }

   public Set<Integer> getGroups() {
      return this._rewardsByGroup.keySet();
   }

   public RouletteItem getRewardByIndex(int index) {
      return (RouletteItem)this._allRewards.get(index);
   }

   public List<RouletteItem> getRewards() {
      return this._allRewards;
   }

   public int getRewardCount() {
      return this._allRewards.size();
   }

   public int getIndexOfReward(RouletteItem rouletteItem) {
      return this._allRewards.indexOf(rouletteItem);
   }

   public void log() {
      this.info("load " + this._rewards.size() + " group and " + this._allRewards.size() + " roulette reward(s).");
   }

   public int size() {
      return this._rewards.size();
   }

   public void clear() {
      this._rewards.clear();
      this._rewardsByGroup.clear();
      this._allRewards.clear();
   }
}
