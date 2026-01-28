package services.community.custom.progress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.Config.RateBonusInfo;
import l2.gameserver.dao.AccountBonusDAO;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.Bonus;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.community.custom.CommunityTools;

public class CommunityProgress implements ScriptFile, ICommunityBoardHandler {
   private static final Logger _log = LoggerFactory.getLogger(CommunityProgress.class);
   private static final int COUNT_PLACED_IN_MAIN_PAGE = 3;
   private static final int COUNT_PLACED_IN_RATING_PAGE = 10;
   private static CommunityProgress _Instance = null;
   private final CommunityProgress.ValuesComparator _valuesComparator = new CommunityProgress.ValuesComparator();

   public void onLoad() {
      ProgressParser.getInstance().load();
      if (ProgressHolder.getInstance().isEnabled()) {
         CommunityBoardManager.getInstance().registerHandler(this);
         AdminCommandHandler.getInstance().registerAdminCommandHandler(new ProgressAdminCommand());
      }

   }

   public void onReload() {
      if (ProgressHolder.getInstance().isEnabled()) {
         CommunityBoardManager.getInstance().removeHandler(this);
      }

   }

   public void onShutdown() {
   }

   public static CommunityProgress getInstance() {
      if (_Instance == null) {
         _Instance = new CommunityProgress();
      }

      return _Instance;
   }

   public String[] getBypassCommands() {
      return new String[]{"_bbsach"};
   }

   public void onBypassCommand(Player player, String bypass) {
      if (ProgressHolder.getInstance().isEnabled()) {
         if (!CommunityTools.checkConditions(player)) {
            String html = HtmCache.getInstance().getNotNull("scripts/services/community/pages/locked.htm", player);
            html = html.replace("%name%", player.getName());
            ShowBoard.separateAndSend(html, player);
         } else {
            if (bypass.equals("_bbsach;")) {
               this.progress(player);
            } else if (bypass.startsWith("_bbsach;achieveReward;")) {
               StringTokenizer st = new StringTokenizer(bypass, ";");
               st.nextToken();
               st.nextToken();
               int stageId = Integer.parseInt(st.nextToken());
               this.progressReward(player, stageId);
            } else if (bypass.equals("_bbsach;ratingInfo;")) {
               this.ratingInfo(player);
            } else if (bypass.equals("_bbsach;description;")) {
               this.description(player);
            }

         }
      }
   }

   public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
   }

   public void progress(Player player) {
      if (!ProgressHolder.getInstance().isEnabled()) {
         player.sendMessage(player.isLangRus() ? "В данный момент эта функция отключена." : "This function disabled.");
      } else {
         Map<Integer, ProgressInfoStage> stages = ProgressHolder.getInstance().getStages();
         int activeStageIdx = player.getVarInt("active_stage", 1);
         int totalStages = stages.size();
         if (activeStageIdx == totalStages + 1) {
            this.ratingInfo(player);
         } else {
            player.getListeners().onHtmlProgressOpening();
            String htmlContent = HtmCache.getInstance().getNotNull("scripts/services/community/progress/progresses.htm", player);
            ProgressInfoStage activeStage = (ProgressInfoStage)stages.get(activeStageIdx);
            htmlContent = htmlContent.replace("%active_stages%", String.valueOf(activeStageIdx));
            htmlContent = htmlContent.replace("%stages_count%", String.valueOf(stages.size()));
            htmlContent = htmlContent.replace("%complete_set_counts%", String.valueOf(player.getVarInt("completed_set_counts", 0)));
            if (activeStage != null) {
               htmlContent = htmlContent.replace("%active_stage_title%", activeStage.getTitle(player));
               Map<Integer, ProgressInfo> progressInfosByStage = ProgressHolder.getInstance().getProgressInfosByStage(activeStage);
               int counter = 0;
               StringBuilder progressHtml = new StringBuilder();
               if (progressInfosByStage != null && !progressInfosByStage.isEmpty()) {
                  ProgressInfo progressInfo;
                  ProgressComponent progressComponent;
                  for(Iterator var10 = progressInfosByStage.values().iterator(); var10.hasNext(); progressHtml.append(this.buildProgressHtml(player, progressInfo, progressComponent))) {
                     progressInfo = (ProgressInfo)var10.next();
                     progressComponent = new ProgressComponent(progressInfo, activeStageIdx, player);
                     if (progressComponent.isCompleted()) {
                        ++counter;
                     }
                  }

                  List<ProgressRewardData> rewardDatas = activeStage.getRewardDataList();
                  htmlContent = htmlContent.replace("%reward_list%", this.buildProgressLevelRewardHtml(player, rewardDatas));
                  htmlContent = htmlContent.replace("%stage_id%", String.valueOf(activeStageIdx));
               }

               htmlContent = htmlContent.replace("%achievements_list%", progressHtml.toString());
               String htmlRatingTemp = HtmCache.getInstance().getNotNull("scripts/services/community/progress/progress_rating_temp.htm", player);
               String htmlRatingTempEmpty = HtmCache.getInstance().getNotNull("scripts/services/community/progress/progress_rating_temp_empty.htm", player);
               String temp = "";
               int i = 0;
               Map<Integer, Integer> results = this.getLoadResultValues();
               if (results != null && !results.isEmpty()) {
                  List<Entry<Integer, Integer>> list = new ArrayList(results.entrySet());
                  Collections.sort(list, this._valuesComparator);

                  String text;
                  for(Iterator var16 = list.iterator(); var16.hasNext(); temp = temp + text) {
                     Entry<Integer, Integer> entry = (Entry)var16.next();
                     ++i;
                     if (i == 4) {
                        break;
                     }

                     text = htmlRatingTemp.replace("%place%", String.valueOf(i));
                     text = text.replace("%obj_id%", String.valueOf(CharacterDAO.getInstance().getNameByObjectId((Integer)entry.getKey())));
                     text = text.replace("%set_counts%", String.valueOf(entry.getValue()));
                  }

                  htmlContent = htmlContent.replace("%rating_list%", temp);
               } else {
                  htmlContent = htmlContent.replace("%rating_list%", htmlRatingTempEmpty);
               }
            }

            htmlContent = Functions.truncateHtmlTagsSpaces(htmlContent);
            ShowBoard.separateAndSend(htmlContent, player);
         }
      }
   }

   private String buildProgressHtml(Player player, ProgressInfo progressInfo, ProgressComponent progressComponent) {
      String progressHtml;
      if (progressComponent.isCompleted()) {
         progressHtml = HtmCache.getInstance().getNotNull("scripts/services/community/progress/entry.completed.htm", player);
      } else {
         progressHtml = HtmCache.getInstance().getNotNull("scripts/services/community/progress/entry.htm", player);
      }

      progressHtml = progressHtml.replace("%achievement_name%", Strings.bbParse(progressInfo.getName(player)));
      progressHtml = progressHtml.replace("%achievement_level_description%", progressInfo.getName(player).replace("\\n", "<br1>"));
      progressHtml = progressHtml.replace("%achievement_level_progress_bar%", progressComponent.getCounter().getVal() + "/" + progressInfo.getValue());
      return progressHtml.trim();
   }

   private String buildProgressLevelRewardHtml(Player player, List<ProgressRewardData> rewardDatas) {
      StringBuilder rewardListHtml = new StringBuilder();
      Iterator var4 = rewardDatas.iterator();

      while(var4.hasNext()) {
         ProgressRewardData rewardData = (ProgressRewardData)var4.next();
         String rewardHtml;
         if (rewardData.getRewardType() == ProgressRewardType.ITEM) {
            rewardHtml = HtmCache.getInstance().getNotNull("scripts/services/community/progress/reward_item.htm", player);
            ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(rewardData.getItemId());
            rewardHtml = rewardHtml.replace("%item_icon%", itemTemplate.getIcon());
            rewardHtml = rewardHtml.replace("%item_id%", String.valueOf(itemTemplate.getItemId()));
            rewardHtml = rewardHtml.replace("%item_name%", itemTemplate.getName());
            rewardHtml = rewardHtml.replace("%item_count%", String.valueOf(rewardData.getItemCount()));
            if (!rewardListHtml.toString().isEmpty()) {
               rewardListHtml.append(",");
            }

            rewardListHtml.append(rewardHtml);
         } else if (rewardData.getRewardType() == ProgressRewardType.PREMIUM) {
            rewardHtml = HtmCache.getInstance().getNotNull("scripts/services/community/progress/reward_premium.htm", player);
            rewardHtml = rewardHtml.replace("%hours%", rewardData.getPremiumHours() + " " + ProgressUtils.hourFormat(player.isLangRus(), String.valueOf(rewardData.getPremiumHours())));
            if (!rewardListHtml.toString().isEmpty()) {
               rewardListHtml.append(",");
            }

            rewardListHtml.append(rewardHtml);
         } else if (rewardData.getRewardType() == ProgressRewardType.HERO) {
            rewardHtml = HtmCache.getInstance().getNotNull("scripts/services/community/progress/reward_hero.htm", player);
            rewardHtml = rewardHtml.replace("%hours%", rewardData.getHeroHours() + " " + ProgressUtils.hourFormat(player.isLangRus(), String.valueOf(rewardData.getHeroHours())));
            if (!rewardListHtml.toString().isEmpty()) {
               rewardListHtml.append(",");
            }

            rewardListHtml.append(rewardHtml);
         }
      }

      return rewardListHtml.toString().trim();
   }

   public void progressReward(Player player, int stageId) {
      if (!ProgressHolder.getInstance().isEnabled()) {
         player.sendMessage(player.isLangRus() ? "В данный момент эта функция отключена." : "This function disabled.");
      } else {
         Map<Integer, ProgressInfoStage> stages = ProgressHolder.getInstance().getStages();
         ProgressInfoStage progressInfoStage = (ProgressInfoStage)stages.get(stageId);
         if (progressInfoStage != null) {
            Map<Integer, ProgressInfo> progressInfosByStage = ProgressHolder.getInstance().getProgressInfosByStage(progressInfoStage);
            int completedAch = 0;
            Iterator var7 = progressInfosByStage.values().iterator();

            while(var7.hasNext()) {
               ProgressInfo progressInfo = (ProgressInfo)var7.next();
               ProgressComponent progressComponent = new ProgressComponent(progressInfo, stageId, player);
               if (progressComponent.isCompleted()) {
                  ++completedAch;
               }
            }

            if (progressInfosByStage.size() != completedAch) {
               player.sendMessage(player.isLangRus() ? "Вы не закончили все задания данного этапа." : "You have not completed all the tasks of this stage.");
               this.onBypassCommand(player, "_bbsach;");
            } else {
               if (progressInfoStage.isRewardableStage(stageId, player)) {
                  List<ProgressRewardData> rewardDataList = progressInfoStage.getRewardDataList();
                  int weight = 0;
                  int slots = 0;
                  Iterator var10 = rewardDataList.iterator();

                  ProgressRewardData rewardData;
                  while(var10.hasNext()) {
                     rewardData = (ProgressRewardData)var10.next();
                     if (rewardData.getRewardType() == ProgressRewardType.ITEM) {
                        ItemTemplate item = ItemHolder.getInstance().getTemplate(rewardData.getItemId());
                        weight += item.getWeight() * rewardData.getItemCount();
                        slots += item.isStackable() ? 1 : rewardData.getItemCount();
                     }
                  }

                  if (!player.getInventory().validateWeight((long)weight)) {
                     player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                     return;
                  }

                  if (!player.getInventory().validateCapacity((long)slots)) {
                     player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                     return;
                  }

                  progressInfoStage.setStageRewarded(player, stageId, true);
                  var10 = rewardDataList.iterator();

                  while(true) {
                     while(var10.hasNext()) {
                        rewardData = (ProgressRewardData)var10.next();
                        if (rewardData.getRewardType() == ProgressRewardType.ITEM) {
                           Functions.addItem(player, rewardData.getItemId(), (long)rewardData.getItemCount());
                        } else if (rewardData.getRewardType() != ProgressRewardType.PREMIUM) {
                           if (rewardData.getRewardType() == ProgressRewardType.HERO) {
                              makeCustomHero(player, (long)(rewardData.getHeroHours() * 60) * 60L);
                           }
                        } else if (player.hasBonus()) {
                           Bonus bonus = player.getBonus();
                           bonus.setBonusExpire(bonus.getBonusExpire() + (long)(rewardData.getPremiumHours() * 60) * 60L);
                           Log.service("Progress", player, "|add a rate bonus|" + (long)(rewardData.getPremiumHours() * 60) * 60L + "| total " + bonus.getBonusExpire());
                           AccountBonusDAO.getInstance().store(player.getAccountName(), bonus);
                           player.stopBonusTask();
                           player.startBonusTask();
                           if (player.getParty() != null) {
                              player.getParty().recalculatePartyData();
                           }

                           player.broadcastUserInfo(true);
                        } else {
                           int id = rewardData.getPremiumIndex();
                           RateBonusInfo rateBonusInfo = null;
                           RateBonusInfo[] var14 = Config.SERVICES_RATE_BONUS_INFO;
                           int var15 = var14.length;

                           for(int var16 = 0; var16 < var15; ++var16) {
                              RateBonusInfo rbi = var14[var16];
                              if (rbi.id == id) {
                                 rateBonusInfo = rbi;
                              }
                           }

                           if (rateBonusInfo == null) {
                              return;
                           }

                           Log.service("Progress", player, "|bought a rate bonus|" + rateBonusInfo.id + "|" + rateBonusInfo.bonusTimeSeconds + "| consume " + rateBonusInfo.consumeItemId + " amount " + rateBonusInfo.consumeItemAmount);
                           AccountBonusDAO.getInstance().store(player.getAccountName(), rateBonusInfo.makeBonus());
                           player.stopBonusTask();
                           player.startBonusTask();
                           if (player.getParty() != null) {
                              player.getParty().recalculatePartyData();
                           }

                           player.broadcastUserInfo(true);
                        }
                     }

                     if (player.isLangRus()) {
                        player.sendMessage("Награда за достижение " + progressInfoStage.getTitle(player) + ".");
                     } else {
                        player.sendMessage("Achievement award " + progressInfoStage.getTitle(player) + ".");
                     }

                     int lastStage = stageId + 1;
                     int totalStages = stages.size();
                     if (stageId == totalStages) {
                        this.removeStageRewardedVars(player, progressInfosByStage.size());
                        ProgressCounter.deleteProgressAchievemntsFromDd(player.getObjectId());
                        player.setVar("active_stage", String.valueOf(lastStage), -1L);
                     } else {
                        this.removeCompleteVars(player, progressInfosByStage.size());
                        player.setVar("active_stage", String.valueOf(lastStage), -1L);
                     }
                     break;
                  }
               }

               this.progress(player);
            }
         }
      }
   }

   private static boolean makeCustomHero(Player player, long customHeroDuration) {
      if (!player.isHero() && customHeroDuration > 0L) {
         player.setCustomHero(true, customHeroDuration, Config.ALT_ALLOW_CUSTOM_HERO_SKILLS);
         player.broadcastPacket(new L2GameServerPacket[]{new SocialAction(player.getObjectId(), 16)});
         player.broadcastUserInfo(true);
         return true;
      } else {
         return false;
      }
   }

   private void removeCompleteVars(Player player, int size) {
      for(int i = 1; i <= size; ++i) {
         this.unsetCompleteVar(player, i, "is_completed");
      }

   }

   private void removeStageRewardedVars(Player player, int size) {
      for(int i = 1; i <= size; ++i) {
         player.unsetVar("stage_rewarded_" + i);
      }

   }

   private void unsetCompleteVar(Player player, int id, String key) {
      player.unsetVar(String.format("achievement_%d_%s", id, key));
   }

   public void description(Player player) {
      if (!ProgressHolder.getInstance().isEnabled()) {
         player.sendMessage(player.isLangRus() ? "В данный момент эта функция отключена." : "This function disabled.");
      } else {
         String htmlContent = HtmCache.getInstance().getNotNull("scripts/services/community/progress/progress_desc.htm", player);
         ShowBoard.separateAndSend(htmlContent, player);
      }
   }

   public void ratingInfo(Player player) {
      if (!ProgressHolder.getInstance().isEnabled()) {
         player.sendMessage(player.isLangRus() ? "В данный момент эта функция отключена." : "This function disabled.");
      } else {
         String htmlContent = HtmCache.getInstance().getNotNull("scripts/services/community/progress/progress_rating.htm", player);
         String htmlTemp = HtmCache.getInstance().getNotNull("scripts/services/community/progress/progress_rating_temp.htm", player);
         String htmlRatingTempEmpty = HtmCache.getInstance().getNotNull("scripts/services/community/progress/progress_rating_temp_empty.htm", player);
         String temp = "";
         int i = 0;
         Map<Integer, Integer> results = this.getLoadResultValues();
         if (results != null && !results.isEmpty()) {
            List<Entry<Integer, Integer>> list = new ArrayList(results.entrySet());
            Collections.sort(list, this._valuesComparator);

            String text;
            for(Iterator var9 = list.iterator(); var9.hasNext(); temp = temp + text) {
               Entry<Integer, Integer> entry = (Entry)var9.next();
               if (i == 11) {
                  break;
               }

               ++i;
               text = htmlTemp.replace("%place%", String.valueOf(i));
               text = text.replace("%obj_id%", String.valueOf(CharacterDAO.getInstance().getNameByObjectId((Integer)entry.getKey())));
               text = text.replace("%set_counts%", String.valueOf(entry.getValue()));
            }

            htmlContent = htmlContent.replace("%list%", temp);
         } else {
            htmlContent = htmlContent.replace("%list%", htmlRatingTempEmpty);
         }

         ShowBoard.separateAndSend(htmlContent, player);
      }
   }

   public Map<Integer, Integer> getLoadResultValues() {
      Map<Integer, Integer> results = new HashMap();
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rs = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("SELECT obj_id,value FROM character_variables WHERE name=?");
         statement.setString(1, "completed_set_counts");
         rs = statement.executeQuery();

         while(rs.next()) {
            int objId = rs.getInt(1);
            int value = rs.getInt(2);
            results.put(objId, value);
         }
      } catch (Exception var10) {
         _log.error("AchievementBBSManager.getLoadResultValues(): " + var10, var10);
      } finally {
         DbUtils.closeQuietly(con, statement, rs);
      }

      return results;
   }

   private class ValuesComparator implements Comparator<Entry<Integer, Integer>> {
      private ValuesComparator() {
      }

      public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
         return (Integer)o2.getValue() - (Integer)o1.getValue();
      }

      // $FF: synthetic method
      ValuesComparator(Object x1) {
         this();
      }
   }
}
