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
import l2.gameserver.listener.actor.player.OnEnchantSkillListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.CustomMessage;

public class OnEnchantSkillListenerImpl implements OnEnchantSkillListener {
   public void onEnchantSkill(Player player, Skill skill, boolean success) {
      if (success) {
         List<FirstOnServerTemplate> list = FirstOnServerRewardsHolder.getInstance().getAllRewardsByType(FirstOnServerType.ENCHANT_SKILL);
         if (list != null) {
            FirstOnServerTemplate template = null;
            Iterator var6 = list.iterator();

            while(var6.hasNext()) {
               FirstOnServerTemplate t = (FirstOnServerTemplate)var6.next();
               if (t.getValue()[0] == skill.getId() && t.getAddValue()[0] == skill.getLevel()) {
                  template = t;
                  break;
               }
            }

            if (template != null) {
               synchronized(player) {
                  Map<Integer, FirstOnServerRecord> winnerOnEnchantSkills = FirstOnServerEvent.getInstance().getWinnerOnEnchantSkills();
                  if (!winnerOnEnchantSkills.containsKey(template.getId())) {
                     FirstOnServerRecord record = new FirstOnServerRecord(template.getType(), template.getId(), player.getObjectId(), player.getName());
                     winnerOnEnchantSkills.put(template.getId(), record);
                     FirstOnServerDAO.getInstance().insert(record);
                     int enchantSkillLevel = 1;
                     if (skill.getLevel() >= 101 && skill.getLevel() <= 130) {
                        enchantSkillLevel = skill.getLevel() - 100;
                     } else if (skill.getLevel() >= 141 && skill.getLevel() <= 170) {
                        enchantSkillLevel = skill.getLevel() - 140;
                     }

                     player.sendMessage((new CustomMessage(template.getPlayerMessage(), player, new Object[0])).addString(skill.getName()).addNumber((long)enchantSkillLevel));
                     template.giveReward(player);
                     if (template.isBroadcastAnnounce()) {
                        Announcements.getInstance().announceByCustomMessage(template.getAnnounceMessage(), new String[]{player.getName(), skill.getName(), String.valueOf(enchantSkillLevel)});
                     }
                  }
               }
            }
         }
      }

   }
}
