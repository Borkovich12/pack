package services.community.custom.progress;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;

public class ProgressInfoStage {
   private final int _level;
   private final String _name;
   private final String _titleAddressRu;
   private final String _titleAddressEn;
   private List<ProgressRewardData> _rewardDataList = new ArrayList();

   public ProgressInfoStage(int level, String name, String titleAddressRu, String titleAddressEn) {
      this._level = level;
      this._name = name;
      this._titleAddressRu = titleAddressRu;
      this._titleAddressEn = titleAddressEn;
   }

   public int getLevel() {
      return this._level;
   }

   public String getName() {
      return this._name;
   }

   public String getTitle(Player player) {
      return player.isLangRus() ? this._titleAddressRu : this._titleAddressEn;
   }

   public List<ProgressRewardData> getRewardDataList() {
      return this._rewardDataList;
   }

   public void addRewardData(ProgressRewardData rewardData) {
      this._rewardDataList.add(rewardData);
   }

   public boolean isRewardableStage(int stageId, Player player) {
      return !this.isStageRewarded(stageId, player);
   }

   public boolean isStageRewarded(int stageId, Player player) {
      return player.getVarInt("stage_rewarded_" + stageId, 0) != 0;
   }

   public void setStageRewarded(Player player, int stageId, boolean rewarded) {
      player.setVar("stage_rewarded_" + stageId, String.valueOf(rewarded ? 1 : 0), -1L);
   }
}
