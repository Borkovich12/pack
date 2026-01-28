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
import l2.gameserver.listener.actor.player.OnPvpPkKillListener;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;

public class OnPvpPkKillListenerImpl implements OnPvpPkKillListener {
   public void onPvpPkKill(Player killer, Player victim, boolean isPk) {
      List list;
      FirstOnServerTemplate template;
      Iterator var6;
      FirstOnServerTemplate t;
      FirstOnServerRecord record;
      Map winnerOnPvps;
      if (!isPk) {
         list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.PVP);
         if (list != null) {
            template = null;
            var6 = list.iterator();

            while(var6.hasNext()) {
               t = (FirstOnServerTemplate)var6.next();
               if (t.getValue()[0] == killer.getPvpKills()) {
                  template = t;
                  break;
               }
            }

            if (template != null) {
               synchronized(killer) {
                  winnerOnPvps = FirstOnServerEvent.getInstance().getWinnerOnPvps();
                  if (!winnerOnPvps.containsKey(template.getId())) {
                     template.giveReward(killer);
                     record = new FirstOnServerRecord(template.getType(), template.getId(), killer.getObjectId(), killer.getName());
                     winnerOnPvps.put(template.getId(), record);
                     FirstOnServerDAO.getInstance().insert(record);
                     killer.sendMessage((new CustomMessage(template.getPlayerMessage(), killer, new Object[0])).addNumber((long)killer.getPvpKills()));
                     if (template.isBroadcastAnnounce()) {
                        Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{killer.getName(), String.valueOf(killer.getPvpKills())});
                     }
                  }
               }
            }
         }
      } else {
         list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.PK);
         if (list != null) {
            template = null;
            var6 = list.iterator();

            while(var6.hasNext()) {
               t = (FirstOnServerTemplate)var6.next();
               if (t.getValue()[0] == killer.getPkKills()) {
                  template = t;
                  break;
               }
            }

            if (template != null) {
               synchronized(killer) {
                  winnerOnPvps = FirstOnServerEvent.getInstance().getWinnerOnPks();
                  if (!winnerOnPvps.containsKey(template.getId())) {
                     record = new FirstOnServerRecord(template.getType(), template.getId(), killer.getObjectId(), killer.getName());
                     winnerOnPvps.put(template.getId(), record);
                     FirstOnServerDAO.getInstance().insert(record);
                     killer.sendMessage((new CustomMessage(template.getPlayerMessage(), killer, new Object[0])).addNumber((long)killer.getPkKills()));
                     template.giveReward(killer);
                     if (template.isBroadcastAnnounce()) {
                        Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{killer.getName(), String.valueOf(killer.getPkKills())});
                     }
                  }
               }
            }
         }
      }

   }
}
