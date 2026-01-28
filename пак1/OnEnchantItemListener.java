package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;

public interface OnEnchantItemListener extends PlayerListener {
   void onEnchantItem(Player var1, ItemInstance var2, boolean var3);
}
