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
import l2.gameserver.listener.actor.player.OnDonateItemListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;

public class OnDonateItemListenerImpl implements OnDonateItemListener {
   private static final Object LOCK = new Object();

   public void onDonateItem(int objectId, int itemId, long itemCount) {
      List<FirstOnServerTemplate> list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.DONATE);
      if (list != null) {
         FirstOnServerTemplate template = null;
         Iterator var7 = list.iterator();

         while(var7.hasNext()) {
            FirstOnServerTemplate t = (FirstOnServerTemplate)var7.next();
            if (t.getValue()[0] == itemId && itemCount >= (long)t.getAddValue()[0] && itemCount <= (long)t.getAddValue()[1]) {
               template = t;
               break;
            }
         }

         if (template != null) {
            synchronized(LOCK) {
               Map<Integer, FirstOnServerRecord> winnerOnDonateItems = FirstOnServerEvent.getInstance().getWinnerOnDonateItems();
               if (!winnerOnDonateItems.containsKey(template.getId())) {
                  Player player = GameObjectsStorage.getPlayer(objectId);
                  if (player != null) {
                     FirstOnServerRecord record = new FirstOnServerRecord(template.getType(), template.getId(), player.getObjectId(), player.getName());
                     winnerOnDonateItems.put(template.getId(), record);
                     FirstOnServerDAO.getInstance().insert(record);
                     player.sendMessage((new CustomMessage(template.getPlayerMessage(), player, new Object[0])).addNumber(itemCount).addString(ItemHolder.getInstance().getTemplate(itemId).getName()));
                     template.giveReward(player);
                     if (template.isBroadcastAnnounce()) {
                        Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{player.getName(), String.valueOf(itemCount), ItemHolder.getInstance().getTemplate(itemId).getName()});
                     }
                  }
               }
            }
         }
      }

   }
}
