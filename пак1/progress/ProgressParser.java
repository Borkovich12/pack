package services.community.custom.progress;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import org.dom4j.Element;

public class ProgressParser extends AbstractFileParser<ProgressHolder> {
   private static final ProgressParser _instance = new ProgressParser();

   public static ProgressParser getInstance() {
      return _instance;
   }

   private ProgressParser() {
      super(ProgressHolder.getInstance());
   }

   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/progress.xml");
   }

   public String getDTDFileName() {
      return "progress.dtd";
   }

   protected void readData(Element rootElement) throws Exception {
      boolean enabled = Boolean.parseBoolean(rootElement.attributeValue("enabled", "false"));
      if (enabled) {
         Map<ProgressMetricType, List<ProgressInfo>> progressByMetric = new HashMap();
         Map<Integer, Map<ProgressMetricType, List<ProgressInfo>>> progressMetricByStage = new HashMap();
         Map<ProgressInfoStage, Map<Integer, ProgressInfo>> progressByStage = new HashMap();
         Map<Integer, ProgressInfoStage> stageList = new HashMap();
         List<ProgressInfo> progressInfoList = new ArrayList();
         Iterator progressesElementIt = rootElement.elementIterator();

         while(true) {
            Element progressesElement;
            do {
               if (!progressesElementIt.hasNext()) {
                  ProgressHolder holder = (ProgressHolder)this.getHolder();
                  holder.setEnabled(enabled);
                  holder.setStages(stageList);
                  holder.setAchievementByMetric(progressByMetric);
                  holder.setAchievementsMetricByStage(progressMetricByStage);
                  holder.setAchievementByStage(progressByStage);
                  holder.setAchievementInfos(progressInfoList);
                  ProgressMetricListeners.getInstance().init();
                  return;
               }

               progressesElement = (Element)progressesElementIt.next();
            } while(!"stage".equalsIgnoreCase(progressesElement.getName()));

            int level = Integer.parseInt(progressesElement.attributeValue("level"));
            String name = progressesElement.attributeValue("name");
            String titleAddressRu = progressesElement.attributeValue("title_address_ru");
            String titleAddressEn = progressesElement.attributeValue("title_address_en");
            ProgressInfoStage stage = new ProgressInfoStage(level, name, titleAddressRu, titleAddressEn);
            Map<Integer, ProgressInfo> progressListByStage = new HashMap();
            Map<ProgressMetricType, List<ProgressInfo>> progressMetricTypeListMap = new HashMap();
            Iterator stageElementIt = progressesElement.elementIterator();

            while(true) {
               label76:
               while(stageElementIt.hasNext()) {
                  Element stageElement = (Element)stageElementIt.next();
                  if ("achievement".equalsIgnoreCase(stageElement.getName())) {
                     int progressId = Integer.parseInt(stageElement.attributeValue("id"));
                     int achValue = Integer.parseInt(stageElement.attributeValue("value"));
                     String achNameRu = stageElement.attributeValue("name_address_ru");
                     String achNameEn = stageElement.attributeValue("name_address_en");
                     ProgressMetricType metricType = ProgressMetricType.valueOf(stageElement.attributeValue("type"));
                     ProgressInfo progressInfo = new ProgressInfo(progressId, achValue, metricType, achNameRu, achNameEn);
                     Iterator progressElementIt = stageElement.elementIterator();

                     while(true) {
                        Element progressElement;
                        do {
                           if (!progressElementIt.hasNext()) {
                              List<ProgressInfo> byMetric = (List)progressByMetric.get(progressInfo.getMetricType());
                              if (byMetric == null) {
                                 progressByMetric.put(progressInfo.getMetricType(), byMetric = new ArrayList());
                              }

                              ((List)byMetric).add(progressInfo);
                              List<ProgressInfo> byMetric2 = (List)progressMetricTypeListMap.get(progressInfo.getMetricType());
                              if (byMetric2 == null) {
                                 progressMetricTypeListMap.put(progressInfo.getMetricType(), byMetric2 = new ArrayList());
                              }

                              ((List)byMetric2).add(progressInfo);
                              progressInfoList.add(progressInfo);
                              progressListByStage.put(progressId, progressInfo);
                              continue label76;
                           }

                           progressElement = (Element)progressElementIt.next();
                        } while(!"conds".equalsIgnoreCase(progressElement.getName()));

                        Iterator condElementIt = progressElement.elementIterator();

                        while(condElementIt.hasNext()) {
                           Element condElement = (Element)condElementIt.next();
                           if ("cond".equalsIgnoreCase(condElement.getName())) {
                              ProgressCondition cond = ProgressCondition.makeCond(condElement.attributeValue("name"), condElement.attributeValue("value"));
                              if (cond == null) {
                                 throw new RuntimeException("Unknown condition \"" + condElement.getName() + " of achievement " + name + "(" + progressInfo.getId() + ")");
                              }

                              progressInfo.addCond(cond);
                           }
                        }
                     }
                  } else if ("rewards".equals(stageElement.getName())) {
                     Iterator rewardElementIt = stageElement.elementIterator();

                     while(rewardElementIt.hasNext()) {
                        Element rewardElement = (Element)rewardElementIt.next();
                        if ("reward".equalsIgnoreCase(rewardElement.getName())) {
                           ProgressRewardType rewardType = ProgressRewardType.valueOf(rewardElement.attributeValue("reward_type"));
                           ProgressRewardData data = new ProgressRewardData(rewardType);
                           if (data.getRewardType() == ProgressRewardType.ITEM) {
                              data.setItemId(Integer.parseInt(rewardElement.attributeValue("item_id")));
                              data.setItemCount(Integer.parseInt(rewardElement.attributeValue("item_count")));
                           } else if (data.getRewardType() == ProgressRewardType.PREMIUM) {
                              data.setPremiumHours(Integer.parseInt(rewardElement.attributeValue("hours")));
                              data.setPremiumHours(Integer.parseInt(rewardElement.attributeValue("hours")));
                           } else if (data.getRewardType() == ProgressRewardType.HERO) {
                              data.setHeroHours(Integer.parseInt(rewardElement.attributeValue("hours")));
                           }

                           stage.addRewardData(data);
                        }
                     }
                  }
               }

               stageList.put(level, stage);
               progressMetricByStage.put(level, progressMetricTypeListMap);
               progressByStage.put(stage, progressListByStage);
               break;
            }
         }
      }
   }
}
