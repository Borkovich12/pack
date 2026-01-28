package services.community.custom.progress;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;

public class ProgressAdminCommand implements IAdminCommandHandler {
   public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
      ProgressAdminCommand.Commands command = (ProgressAdminCommand.Commands)comm;
      if (activeChar.getPlayerAccess().Menu) {
         ProgressInfo achievementInfo;
         ProgressComponent progressComponent;
         int value;
         ProgressCounter achievementCounter;
         switch(command) {
         case admin_fin_ach:
            StringTokenizer st = new StringTokenizer(fullString);
            st.nextToken();
            int id = Integer.parseInt(st.nextToken());
            GameObject target = activeChar.getTarget();
            if (target != null && !target.isPlayer()) {
               activeChar.sendMessage(activeChar.isLangRus() ? "Неверная цель." : "Wrong target.");
               return false;
            }

            Player playerTarget;
            if (target == null) {
               playerTarget = activeChar;
            } else {
               playerTarget = target.getPlayer();
            }

            int activeCategoryId = playerTarget.getVarInt("active_stage", 1);
            Map<Integer, ProgressInfoStage> categories = ProgressHolder.getInstance().getStages();
            if (categories != null && !categories.isEmpty()) {
               ProgressInfoStage progressInfoStageInfoStage = (ProgressInfoStage)categories.get(activeCategoryId);
               if (progressInfoStageInfoStage == null) {
                  return false;
               }

               Map<Integer, ProgressInfo> achievementInfosByCategory = ProgressHolder.getInstance().getProgressInfosByStage(progressInfoStageInfoStage);
               if (achievementInfosByCategory == null || achievementInfosByCategory.isEmpty()) {
                  return false;
               }

               achievementInfo = (ProgressInfo)achievementInfosByCategory.get(id);
               if (achievementInfo == null) {
                  return false;
               }

               progressComponent = new ProgressComponent(achievementInfo, activeCategoryId, playerTarget);
               if (progressComponent == null) {
                  return false;
               }

               value = achievementInfo.getValue();
               achievementCounter = progressComponent.getCounter();
               achievementCounter.setVal(value);
               progressComponent.setCompletedVar(true);
               progressComponent.calculatePoints(playerTarget);
               achievementCounter.store();
               if (playerTarget.isLangRus()) {
                  playerTarget.sendMessage("Вы получили достижение " + achievementInfo.getName(playerTarget) + ".");
               } else {
                  playerTarget.sendMessage("You got the achievement " + achievementInfo.getName(playerTarget) + ".");
               }

               if (activeChar != playerTarget) {
                  if (activeChar.isLangRus()) {
                     activeChar.sendMessage("Вы добавили достижение " + achievementInfo.getName(playerTarget) + " персонажу " + playerTarget.getName());
                  } else {
                     activeChar.sendMessage("You added achievements " + achievementInfo.getName(playerTarget) + " character " + playerTarget.getName());
                  }
               }
               break;
            }

            return false;
         case admin_fin_all_ach:
            GameObject target = activeChar.getTarget();
            if (target != null && !target.isPlayer()) {
               activeChar.sendMessage(activeChar.isLangRus() ? "Неверная цель." : "Wrong target.");
               return false;
            }

            Player playerTarget;
            if (target == null) {
               playerTarget = activeChar;
            } else {
               playerTarget = target.getPlayer();
            }

            int activeCategoryId = playerTarget.getVarInt("active_stage", 1);
            Map<Integer, ProgressInfoStage> categories = ProgressHolder.getInstance().getStages();
            if (categories == null || categories.isEmpty()) {
               return false;
            }

            ProgressInfoStage achievementInfoStage = (ProgressInfoStage)categories.get(activeCategoryId);
            if (achievementInfoStage == null) {
               return false;
            }

            Map<Integer, ProgressInfo> achievementInfosByCategory = ProgressHolder.getInstance().getProgressInfosByStage(achievementInfoStage);
            if (achievementInfosByCategory == null || achievementInfosByCategory.isEmpty()) {
               return false;
            }

            Iterator var13 = achievementInfosByCategory.values().iterator();

            while(var13.hasNext()) {
               achievementInfo = (ProgressInfo)var13.next();
               if (achievementInfo != null) {
                  progressComponent = new ProgressComponent(achievementInfo, activeCategoryId, playerTarget);
                  if (progressComponent != null) {
                     value = achievementInfo.getValue();
                     achievementCounter = progressComponent.getCounter();
                     achievementCounter.setVal(value);
                     progressComponent.setCompletedVar(true);
                     progressComponent.calculatePoints(playerTarget);
                     achievementCounter.store();
                  }
               }
            }

            if (playerTarget.isLangRus()) {
               playerTarget.sendMessage("Вы успешно завершили " + achievementInfoStage.getTitle(playerTarget) + ".");
            } else {
               playerTarget.sendMessage("You have successfully completed " + achievementInfoStage.getTitle(playerTarget) + ".");
            }

            if (activeChar != playerTarget) {
               if (activeChar.isLangRus()) {
                  activeChar.sendMessage("You have added all the achievements of the stage " + achievementInfoStage.getTitle(playerTarget) + " персонажу " + playerTarget.getName() + ".");
               } else {
                  activeChar.sendMessage("Вы добавили все ачивки этапа " + achievementInfoStage.getTitle(playerTarget) + " character " + playerTarget.getName() + ".");
               }
            }
         }
      }

      return false;
   }

   public Enum[] getAdminCommandEnum() {
      return ProgressAdminCommand.Commands.values();
   }

   private static enum Commands {
      admin_fin_ach,
      admin_fin_all_ach;
   }
}
