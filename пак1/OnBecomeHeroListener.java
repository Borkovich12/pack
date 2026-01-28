package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;

public interface OnBecomeHeroListener extends PlayerListener {
   void onBecomeHero(Player var1, int var2);
}
