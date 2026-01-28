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
import l2.gameserver.listener.actor.player.OnAugmentItemListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import org.apache.commons.lang3.ArrayUtils;

public class OnAugmentItemListenerImpl implements OnAugmentItemListener {
   public void onAugmentItem(Player player, ItemInstance item, int opt1, int opt2) {
      List<FirstOnServerTemplate> list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.LS);
      if (list != null) {
         FirstOnServerTemplate template = null;
         int optVariation = false;
         Iterator var8 = list.iterator();

         while(var8.hasNext()) {
            FirstOnServerTemplate t = (FirstOnServerTemplate)var8.next();
            if (ArrayUtils.contains(t.getValue(), opt1) && ArrayUtils.contains(t.getAddValue(), item.getItemId())) {
               template = t;
               break;
            }

            if (ArrayUtils.contains(t.getValue(), opt2) && ArrayUtils.contains(t.getAddValue(), item.getItemId())) {
               template = t;
               break;
            }
         }

         if (template != null) {
            synchronized(player) {
               Map<Integer, FirstOnServerRecord> winnerOnAugmentItems = FirstOnServerEvent.getInstance().getWinnerOnAugmentItems();
               if (!winnerOnAugmentItems.containsKey(template.getId())) {
                  FirstOnServerRecord record = new FirstOnServerRecord(template.getType(), template.getId(), player.getObjectId(), player.getName());
                  winnerOnAugmentItems.put(template.getId(), record);
                  FirstOnServerDAO.getInstance().insert(record);
                  player.sendMessage((new CustomMessage(template.getPlayerMessage(), player, new Object[0])).addString(item.getName()));
                  template.giveReward(player);
                  if (template.isBroadcastAnnounce()) {
                     Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{player.getName(), item.getName()});
                  }
               }
            }
         }
      }

   }
}
