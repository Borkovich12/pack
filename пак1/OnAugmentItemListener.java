package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;

public interface OnAugmentItemListener extends PlayerListener {
   void onAugmentItem(Player var1, ItemInstance var2, int var3, int var4);
}
