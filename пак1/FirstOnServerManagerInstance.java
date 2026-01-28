package npc.model;

import events.FirstOnServer.FirstOnServerBypass;
import events.FirstOnServer.data.FirstOnServerRewardsHolder;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class FirstOnServerManagerInstance extends NpcInstance {
   public FirstOnServerManagerInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
   }

   public void showChatWindow(Player player, int val, Object... replace) {
      if (val == 0 && FirstOnServerRewardsHolder.getInstance().isEnable()) {
         if (isAllowedNpc(player, this)) {
            String filePath = this.getHtmlPath(this.getNpcId(), val, player);
            FirstOnServerBypass.sendPage(player, val, 1, this, filePath);
         }
      } else {
         super.showChatWindow(player, val, replace);
      }
   }

   protected static boolean isAllowedNpc(Player player, NpcInstance npc) {
      if (player != null && npc != null) {
         return !player.isActionsDisabled() && (Config.ALLOW_TALK_WHILE_SITTING || !player.isSitting()) && npc.isInActingRange(player);
      } else {
         return false;
      }
   }

   public String getHtmlPath(int npcId, int val, Player player) {
      return FirstOnServerBypass.getHtmRoot(val);
   }
}
