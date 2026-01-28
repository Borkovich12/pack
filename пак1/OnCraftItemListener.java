package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;

public interface OnCraftItemListener extends PlayerListener {
   void onCraftItem(Player var1, int var2, long var3);
}
