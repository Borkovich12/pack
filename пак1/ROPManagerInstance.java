package npc.model;

import events.RoomOfPower.RoomOfPower;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class ROPManagerInstance extends NpcInstance {
   public ROPManagerInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
   }

   public void showChatWindow(Player player, int val, Object... replace) {
      if (val == 0) {
         RoomOfPower.openPage(player);
      } else {
         super.showChatWindow(player, val, replace);
      }
   }
}
