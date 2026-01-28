package npc.model;

import events.TreasureHunting.TreasureHunting;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class TreasureManagerInstance extends NpcInstance {
   public TreasureManagerInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
   }

   public void showChatWindow(Player player, int val, Object... replace) {
      if (val == 0) {
         TreasureHunting.openPage(player);
      } else {
         super.showChatWindow(player, val, replace);
      }
   }
}
