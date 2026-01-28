package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;

public interface OnTreasureHuntingEventListener extends PlayerListener {
   void onTreasureHuntingEvent(Player var1, boolean var2);
}
