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
import l2.gameserver.listener.actor.OnKillListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import org.apache.commons.lang3.ArrayUtils;

public class OnKillListenerImpl implements OnKillListener {
   public void onKill(Creature actor, Creature victim) {
      if (actor != null && actor.isPlayer() && victim != null) {
         if (victim.isNpc()) {
            List<FirstOnServerTemplate> list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.KILL_MOBS);
            Player player;
            if (list != null) {
               player = actor.getPlayer();
               int killsNpc = player.getVarInt("KillsNpc", 0);
               ++killsNpc;
               player.setVar("KillsNpc", killsNpc, -1L);
               FirstOnServerTemplate template = null;
               Iterator var7 = list.iterator();

               while(var7.hasNext()) {
                  FirstOnServerTemplate t = (FirstOnServerTemplate)var7.next();
                  if (t.getValue()[0] == killsNpc) {
                     template = t;
                     break;
                  }
               }

               if (template != null) {
                  synchronized(player) {
                     Map<Integer, FirstOnServerRecord> winnerOnKillNpcs = FirstOnServerEvent.getInstance().getWinnerOnKillNpcs();
                     if (!winnerOnKillNpcs.containsKey(template.getId())) {
                        FirstOnServerRecord record = new FirstOnServerRecord(template.getType(), template.getId(), player.getObjectId(), player.getName());
                        winnerOnKillNpcs.put(template.getId(), record);
                        FirstOnServerDAO.getInstance().insert(record);
                        player.sendMessage((new CustomMessage(template.getPlayerMessage(), player, new Object[0])).addNumber((long)killsNpc));
                        template.giveReward(player);
                        if (template.isBroadcastAnnounce()) {
                           Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{player.getName(), String.valueOf(killsNpc)});
                        }
                     }
                  }
               }
            }

            if (victim instanceof RaidBossInstance) {
               list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.KILL_RAID_BOSS);
               if (list != null) {
                  player = actor.getPlayer();
                  FirstOnServerTemplate template = null;
                  Iterator var15 = list.iterator();

                  while(var15.hasNext()) {
                     FirstOnServerTemplate t = (FirstOnServerTemplate)var15.next();
                     if (ArrayUtils.contains(t.getValue(), victim.getNpcId())) {
                        template = t;
                        break;
                     }
                  }

                  if (template != null) {
                     synchronized(player) {
                        Map<Integer, FirstOnServerRecord> winnerOnKillRaidBosses = FirstOnServerEvent.getInstance().getWinnerOnKillRaidBosses();
                        if (!winnerOnKillRaidBosses.containsKey(template.getId())) {
                           FirstOnServerRecord record = new FirstOnServerRecord(template.getType(), template.getId(), player.getObjectId(), player.getName());
                           winnerOnKillRaidBosses.put(template.getId(), record);
                           FirstOnServerDAO.getInstance().insert(record);
                           player.sendMessage((new CustomMessage(template.getPlayerMessage(), player, new Object[0])).addString(victim.getName()));
                           template.giveReward(player);
                           if (template.isBroadcastAnnounce()) {
                              Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{player.getName(), String.valueOf(victim.getName())});
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public boolean ignorePetOrSummon() {
      return true;
   }
}
