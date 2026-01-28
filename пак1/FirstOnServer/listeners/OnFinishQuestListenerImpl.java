package events.FirstOnServer.listeners;

import events.FirstOnServer.FirstOnServerEvent;
import events.FirstOnServer.dao.FirstOnServerDAO;
import events.FirstOnServer.data.FirstOnServerRewardsHolder;
import events.FirstOnServer.template.FirstOnServerRecord;
import events.FirstOnServer.template.FirstOnServerTemplate;
import events.FirstOnServer.type.FirstOnServerType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.gameserver.Announcements;
import l2.gameserver.listener.actor.player.OnFinishQuestListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.CustomMessage;

public class OnFinishQuestListenerImpl implements OnFinishQuestListener {
   public void onFinishQuest(Player player, QuestState questState) {
      List<FirstOnServerTemplate> list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.QUEST);
      if (list != null) {
         FirstOnServerTemplate template = null;
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            FirstOnServerTemplate t = (FirstOnServerTemplate)var5.next();
            if (t.getValue()[0] == questState.getQuest().getQuestIntId()) {
               template = t;
               break;
            }
         }

         if (template != null) {
            synchronized(player) {
               Map<Integer, FirstOnServerRecord> winnerOnQuests = FirstOnServerEvent.getInstance().getWinnerOnQuests();
               if (!winnerOnQuests.containsKey(template.getId())) {
                  FirstOnServerRecord record = new FirstOnServerRecord(template.getType(), template.getId(), player.getObjectId(), player.getName());
                  winnerOnQuests.put(template.getId(), record);
                  FirstOnServerDAO.getInstance().insert(record);
                  String questName = (new CustomMessage(template.getName(), player, new Object[0])).toString();
                  player.sendMessage((new CustomMessage(template.getPlayerMessage(), player, new Object[0])).addString(questName));
                  template.giveReward(player);
                  if (template.isBroadcastAnnounce()) {
                     Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{player.getName(), questName});
                  }
               }
            }
         }
      }

   }
}
