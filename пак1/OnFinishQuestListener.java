package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.QuestState;

public interface OnFinishQuestListener extends PlayerListener {
   void onFinishQuest(Player var1, QuestState var2);
}
