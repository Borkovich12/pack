package npc.model;

import events.TopKiller.TopKiller;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class TopKillerManagerInstance extends NpcInstance {
   public TopKillerManagerInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
   }

   public void showChatWindow(Player player, int val, Object... replace) {
      if (val == 0) {
         TopKiller.sendBoard(player, this, "Dino");
      } else {
         super.showChatWindow(player, val, replace);
      }
   }
}
