package npc.model;

import events.BossHunting.BossHunting;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class BossManagerInstance extends NpcInstance {
   public BossManagerInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
   }

   public void showChatWindow(Player player, int val, Object... replace) {
      if (val == 0) {
         BossHunting.openPage(player);
      } else {
         super.showChatWindow(player, val, replace);
      }
   }
}
