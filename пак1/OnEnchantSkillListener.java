package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;

public interface OnEnchantSkillListener extends PlayerListener {
   void onEnchantSkill(Player var1, Skill var2, boolean var3);
}
