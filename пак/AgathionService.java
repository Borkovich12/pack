package services;

import l2.gameserver.ThreadPoolManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgathionService implements ScriptFile, OnPlayerEnterListener, OnPlayerExitListener, OnDeathListener, OnTeleportListener {
   private static final Logger _log = LoggerFactory.getLogger(Player.class);

   public void onLoad() {
      PlayerListenerList.addGlobal(this);
   }

   public void onReload() {
   }

   public void onShutdown() {
   }

   public void onPlayerEnter(Player player) {
      try {
         String var = player.getVar("agathion");
         if (var != null) {
            player.setAgathionControlItem(player, Integer.parseInt(var));
            player.unsetVar("agathion");
         }
      } catch (Exception var3) {
         _log.error("", var3);
      }

   }

   public void onPlayerExit(Player player) {
      if (player.getAgathion() != null) {
         player.getAgathion().unSummon();
      }

   }

   public void onDeath(Creature actor, Creature killer) {
      if (actor.isPlayer()) {
         Player player = actor.getPlayer();
         if (player.getAgathion() != null) {
            player.getAgathion().unSummon();
         }
      }

   }

   public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
      if (player.getAgathion() != null) {
         ThreadPoolManager.getInstance().schedule(() -> {
            player.getAgathion().teleportToOwner();
         }, 500L);
      }

   }
}
