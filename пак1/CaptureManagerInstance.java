package npc.model;

import events.CaptureCastle.CaptureCastle;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class CaptureManagerInstance extends NpcInstance {
   public CaptureManagerInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
   }

   public void showChatWindow(Player player, int val, Object... replace) {
      if (val == 0) {
         CaptureCastle.openPage(player);
      } else {
         super.showChatWindow(player, val, replace);
      }
   }
}
