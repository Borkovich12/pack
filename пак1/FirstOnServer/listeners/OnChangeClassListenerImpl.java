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
import l2.gameserver.listener.actor.player.OnSetClassListener;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.HtmlUtils;
import org.apache.commons.lang3.ArrayUtils;

public class OnChangeClassListenerImpl implements OnSetClassListener {
   public void onSetClass(Player player, int id) {
      List<FirstOnServerTemplate> list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.CLASS);
      if (list != null) {
         FirstOnServerTemplate template = null;
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            FirstOnServerTemplate t = (FirstOnServerTemplate)var5.next();
            if (ArrayUtils.contains(t.getValue(), id)) {
               template = t;
               break;
            }
         }

         if (template != null) {
            synchronized(player) {
               Map<Integer, FirstOnServerRecord> winnerOnChangeClass = FirstOnServerEvent.getInstance().getWinnerOnChangeClass();
               if (!winnerOnChangeClass.containsKey(template.getId())) {
                  FirstOnServerRecord record = new FirstOnServerRecord(template.getType(), template.getId(), player.getObjectId(), player.getName());
                  winnerOnChangeClass.put(template.getId(), record);
                  FirstOnServerDAO.getInstance().insert(record);
                  player.sendMessage((new CustomMessage(template.getPlayerMessage(), player, new Object[0])).addString(HtmlUtils.htmlClassName(id, player)));
                  template.giveReward(player);
                  if (template.isBroadcastAnnounce()) {
                     Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{player.getName(), HtmlUtils.htmlClassName(id, player)});
                  }
               }
            }
         }
      }

   }
}
