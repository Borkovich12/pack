package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;

public interface OnGvGEventListener extends PlayerListener {
   void onGvGEvent(Player var1, boolean var2);
}
