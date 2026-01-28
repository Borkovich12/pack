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
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.listener.actor.player.OnCraftItemListener;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import org.apache.commons.lang3.ArrayUtils;

public class OnCraftItemListenerImpl implements OnCraftItemListener {
   public void onCraftItem(Player player, int itemId, long itemCount) {
      List<FirstOnServerTemplate> list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.CRAFT);
      if (list != null) {
         FirstOnServerTemplate template = null;
         Iterator var7 = list.iterator();

         while(var7.hasNext()) {
            FirstOnServerTemplate t = (FirstOnServerTemplate)var7.next();
            if (ArrayUtils.contains(t.getValue(), itemId)) {
               template = t;
               break;
            }
         }

         if (template != null) {
            synchronized(player) {
               Map<Integer, FirstOnServerRecord> winnerOnCraftItems = FirstOnServerEvent.getInstance().getWinnerOnCraftItems();
               if (!winnerOnCraftItems.containsKey(template.getId())) {
                  FirstOnServerRecord record = new FirstOnServerRecord(template.getType(), template.getId(), player.getObjectId(), player.getName());
                  winnerOnCraftItems.put(template.getId(), record);
                  FirstOnServerDAO.getInstance().insert(record);
                  player.sendMessage((new CustomMessage(template.getPlayerMessage(), player, new Object[0])).addString(ItemHolder.getInstance().getTemplate(itemId).getName()));
                  template.giveReward(player);
                  if (template.isBroadcastAnnounce()) {
                     Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{player.getName(), ItemHolder.getInstance().getTemplate(itemId).getName()});
                  }
               }
            }
         }
      }

   }
}
