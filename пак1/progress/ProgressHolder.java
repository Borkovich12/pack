package services.community.custom.progress;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;

public class ProgressHolder extends AbstractHolder {
   private boolean _isEnabled = false;
   private Map<ProgressMetricType, List<ProgressInfo>> _achievementByMetric = Collections.emptyMap();
   private Map<Integer, Map<ProgressMetricType, List<ProgressInfo>>> _achievementsMetricByStage = Collections.emptyMap();
   private Map<ProgressInfoStage, Map<Integer, ProgressInfo>> _achievementByStage = Collections.emptyMap();
   private Map<Integer, ProgressInfoStage> _stages = Collections.emptyMap();
   private List<ProgressInfo> _achievementInfos = Collections.emptyList();
   private static ProgressHolder instance = new ProgressHolder();

   public static ProgressHolder getInstance() {
      return instance;
   }

   public boolean isEnabled() {
      return this._isEnabled;
   }

   public void setEnabled(boolean isEnabled) {
      this._isEnabled = isEnabled;
   }

   public List<ProgressInfo> getAchievementInfosByMetric(ProgressMetricType metricType) {
      return (List)this._achievementByMetric.get(metricType);
   }

   public void addAchievementByMetric(ProgressMetricType metricType, List<ProgressInfo> achievementByMetric) {
      this._achievementByMetric.put(metricType, achievementByMetric);
   }

   public void setAchievementByMetric(Map<ProgressMetricType, List<ProgressInfo>> achievementByMetric) {
      this._achievementByMetric = achievementByMetric;
   }

   public Map<ProgressMetricType, List<ProgressInfo>> getProgressMetricByStage(int stageId) {
      return (Map)this._achievementsMetricByStage.get(stageId);
   }

   public void setAchievementsMetricByStage(Map<Integer, Map<ProgressMetricType, List<ProgressInfo>>> ach) {
      this._achievementsMetricByStage = ach;
   }

   public void addAchievementsMetricByStage(int level, Map<ProgressMetricType, List<ProgressInfo>> achievementMetricTypeListMap) {
      this._achievementsMetricByStage.put(level, achievementMetricTypeListMap);
   }

   public Map<Integer, ProgressInfo> getProgressInfosByStage(ProgressInfoStage stage) {
      return (Map)this._achievementByStage.get(stage);
   }

   public void setAchievementByStage(Map<ProgressInfoStage, Map<Integer, ProgressInfo>> achievementByStage) {
      this._achievementByStage = achievementByStage;
   }

   public void addAchievementByStage(ProgressInfoStage stage, Map<Integer, ProgressInfo> achievementByStage) {
      this._achievementByStage.put(stage, achievementByStage);
   }

   public ProgressInfo getAchievementInfoById(int achievementId) {
      Iterator var2 = this._achievementInfos.iterator();

      ProgressInfo achievementInfo;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         achievementInfo = (ProgressInfo)var2.next();
      } while(achievementInfo.getId() != achievementId);

      return achievementInfo;
   }

   public void setAchievementInfos(List<ProgressInfo> achievementInfos) {
      this._achievementInfos = achievementInfos;
   }

   public void addAchievementInfos(ProgressInfo achievementInfo) {
      this._achievementInfos.add(achievementInfo);
   }

   public Map<Integer, ProgressInfoStage> getStages() {
      return this._stages;
   }

   public void setStages(Map<Integer, ProgressInfoStage> stages) {
      this._stages = stages;
   }

   public void addStages(int level, ProgressInfoStage stage) {
      this._stages.put(level, stage);
   }

   public void log() {
      this.info("load " + this._achievementInfos.size() + " achievement(s) for " + this._stages.size() + " stage(s).");
   }

   public int size() {
      return this._achievementInfos.size() + this._stages.size();
   }

   public void clear() {
      this._achievementInfos.clear();
      this._stages.clear();
   }
}
