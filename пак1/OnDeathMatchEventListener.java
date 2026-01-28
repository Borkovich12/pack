package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;

public interface OnDeathMatchEventListener extends PlayerListener {
   void onDeathMatchEvent(Player var1, boolean var2, int var3);
}
