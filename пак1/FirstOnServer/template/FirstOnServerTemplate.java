package events.FirstOnServer.template;

import events.FirstOnServer.type.FirstOnServerRewardType;
import events.FirstOnServer.type.FirstOnServerType;
import java.util.Iterator;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.Config.RateBonusInfo;
import l2.gameserver.dao.AccountBonusDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.Bonus;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class FirstOnServerTemplate {
   private final int id;
   private final String name;
   private final int pageIndex;
   private final FirstOnServerType type;
   private final int[] value;
   private final int[] addValue;
   private final List<FirstOnServerRewardTemplate> rewardList;
   private final String playerMessage;
   private final boolean broadcastAnnounce;
   private final String announceMessage;

   public FirstOnServerTemplate(int id, String name, int pageIndex, FirstOnServerType type, int[] value, int[] addValue, List<FirstOnServerRewardTemplate> rewardList, String playerMessage, boolean broadcastAnnounce, String announceMessage) {
      this.id = id;
      this.name = name;
      this.pageIndex = pageIndex;
      this.type = type;
      this.value = value;
      this.addValue = addValue;
      this.rewardList = rewardList;
      this.playerMessage = playerMessage;
      this.broadcastAnnounce = broadcastAnnounce;
      this.announceMessage = announceMessage;
   }

   public int getId() {
      return this.id;
   }

   public int getPageIndex() {
      return this.pageIndex;
   }

   public String getName() {
      return this.name;
   }

   public FirstOnServerType getType() {
      return this.type;
   }

   public int[] getValue() {
      return this.value;
   }

   public int[] getAddValue() {
      return this.addValue;
   }

   public List<FirstOnServerRewardTemplate> getRewardList() {
      return this.rewardList;
   }

   public String getPlayerMessage() {
      return this.playerMessage;
   }

   public boolean isBroadcastAnnounce() {
      return this.broadcastAnnounce;
   }

   public String getAnnounceMessage() {
      return this.announceMessage;
   }

   public void giveReward(Player player) {
      Iterator var2 = this.rewardList.iterator();

      while(true) {
         while(var2.hasNext()) {
            FirstOnServerRewardTemplate rewardData = (FirstOnServerRewardTemplate)var2.next();
            if (rewardData.getRewardType() == FirstOnServerRewardType.ITEM) {
               ItemFunctions.addItem(player, rewardData.getItemId(), (long)rewardData.getItemCount(), true);
            } else if (rewardData.getRewardType() == FirstOnServerRewardType.PREMIUM) {
               if (player.hasBonus()) {
                  Bonus bonus = player.getBonus();
                  bonus.setBonusExpire(bonus.getBonusExpire() + (long)(rewardData.getPremiumHours() * 60) * 60L);
                  Log.service("FirstOnServer", player, "|add a rate bonus|" + (long)(rewardData.getPremiumHours() * 60) * 60L + "| total " + bonus.getBonusExpire());
                  AccountBonusDAO.getInstance().store(player.getAccountName(), bonus);
                  player.stopBonusTask();
                  player.startBonusTask();
                  if (player.getParty() != null) {
                     player.getParty().recalculatePartyData();
                  }

                  player.broadcastUserInfo(true);
               } else {
                  int id = 1;
                  RateBonusInfo rateBonusInfo = null;
                  RateBonusInfo[] var6 = Config.SERVICES_RATE_BONUS_INFO;
                  int var7 = var6.length;

                  for(int var8 = 0; var8 < var7; ++var8) {
                     RateBonusInfo rbi = var6[var8];
                     if (rbi.id == id) {
                        rateBonusInfo = rbi;
                     }
                  }

                  if (rateBonusInfo == null) {
                     return;
                  }

                  Log.service("FirstOnServer", player, "|bought a rate bonus|" + rateBonusInfo.id + "|" + rateBonusInfo.bonusTimeSeconds + "| consume " + rateBonusInfo.consumeItemId + " amount " + rateBonusInfo.consumeItemAmount);
                  AccountBonusDAO.getInstance().store(player.getAccountName(), rateBonusInfo.makeBonus());
                  player.stopBonusTask();
                  player.startBonusTask();
                  if (player.getParty() != null) {
                     player.getParty().recalculatePartyData();
                  }

                  player.broadcastUserInfo(true);
               }
            }
         }

         return;
      }
   }
}
