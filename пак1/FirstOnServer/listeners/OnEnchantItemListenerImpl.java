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
import l2.gameserver.listener.actor.player.OnEnchantItemListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import org.apache.commons.lang3.ArrayUtils;

public class OnEnchantItemListenerImpl implements OnEnchantItemListener {
   public void onEnchantItem(Player player, ItemInstance item, boolean success) {
      if (success) {
         List<FirstOnServerTemplate> list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.ENCHANT_ITEM);
         if (list != null) {
            FirstOnServerTemplate template = null;
            Iterator var6 = list.iterator();

            while(var6.hasNext()) {
               FirstOnServerTemplate t = (FirstOnServerTemplate)var6.next();
               if (ArrayUtils.contains(t.getValue(), item.getItemId()) && t.getAddValue()[0] == item.getEnchantLevel()) {
                  template = t;
                  break;
               }
            }

            if (template != null) {
               synchronized(player) {
                  Map<Integer, FirstOnServerRecord> winnerOnEnchantItems = FirstOnServerEvent.getInstance().getWinnerOnEnchantItems();
                  if (!winnerOnEnchantItems.containsKey(template.getId())) {
                     FirstOnServerRecord record = new FirstOnServerRecord(template.getType(), template.getId(), player.getObjectId(), player.getName());
                     winnerOnEnchantItems.put(template.getId(), record);
                     FirstOnServerDAO.getInstance().insert(record);
                     player.sendMessage((new CustomMessage(template.getPlayerMessage(), player, new Object[0])).addString(ItemHolder.getInstance().getTemplate(item.getItemId()).getName()).addNumber((long)item.getEnchantLevel()));
                     template.giveReward(player);
                     if (template.isBroadcastAnnounce()) {
                        Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{player.getName(), ItemHolder.getInstance().getTemplate(item.getItemId()).getName(), String.valueOf(item.getEnchantLevel())});
                     }
                  }
               }
            }
         }
      }

   }
}
