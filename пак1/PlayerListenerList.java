package l2.gameserver.model.actor.listener;

import l2.gameserver.listener.actor.player.OnAugmentItemListener;
import l2.gameserver.listener.actor.player.OnAutoSoulShotListener;
import l2.gameserver.listener.actor.player.OnBecomeHeroListener;
import l2.gameserver.listener.actor.player.OnBossHuntingEventListener;
import l2.gameserver.listener.actor.player.OnCaptureCastleEventListener;
import l2.gameserver.listener.actor.player.OnCraftItemListener;
import l2.gameserver.listener.actor.player.OnCtfEventListener;
import l2.gameserver.listener.actor.player.OnDeathMatchEventListener;
import l2.gameserver.listener.actor.player.OnEnchantItemListener;
import l2.gameserver.listener.actor.player.OnEnchantSkillListener;
import l2.gameserver.listener.actor.player.OnFinishQuestListener;
import l2.gameserver.listener.actor.player.OnGainExpSpListener;
import l2.gameserver.listener.actor.player.OnGvGEventListener;
import l2.gameserver.listener.actor.player.OnHtmlProgressOpeningListener;
import l2.gameserver.listener.actor.player.OnItemPickupListener;
import l2.gameserver.listener.actor.player.OnLevelUpListener;
import l2.gameserver.listener.actor.player.OnOlyCompetitionListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnPlayerPartyInviteListener;
import l2.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2.gameserver.listener.actor.player.OnPlayerSayListener;
import l2.gameserver.listener.actor.player.OnPvpPkKillListener;
import l2.gameserver.listener.actor.player.OnQuestStateChangeListener;
import l2.gameserver.listener.actor.player.OnRoomOfPowerEventListener;
import l2.gameserver.listener.actor.player.OnSetClassListener;
import l2.gameserver.listener.actor.player.OnSetLevelListener;
import l2.gameserver.listener.actor.player.OnSetPrivateStoreType;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.listener.actor.player.OnTopKillerDinoEventListener;
import l2.gameserver.listener.actor.player.OnTopKillerPkEventListener;
import l2.gameserver.listener.actor.player.OnTopKillerPvpEventListener;
import l2.gameserver.listener.actor.player.OnTreasureHuntingEventListener;
import l2.gameserver.listener.actor.player.OnTvtEventListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.QuestState;

public class PlayerListenerList extends CharListenerList {
   public PlayerListenerList(Player actor) {
      super(actor);
   }

   public Player getActor() {
      return (Player)this.actor;
   }

   public void onEnter() {
      this.forEachListenerWithGlobal(OnPlayerEnterListener.class, (onPlayerEnterListener) -> {
         onPlayerEnterListener.onPlayerEnter(this.getActor());
      });
   }

   public void onExit() {
      this.forEachListenerWithGlobal(OnPlayerExitListener.class, (onPlayerExitListener) -> {
         onPlayerExitListener.onPlayerExit(this.getActor());
      });
   }

   public void onTeleport(int x, int y, int z, Reflection reflection) {
      this.forEachListenerWithGlobal(OnTeleportListener.class, (onTeleportListener) -> {
         onTeleportListener.onTeleport(this.getActor(), x, y, z, reflection);
      });
   }

   public void onQuestStateChange(QuestState questState) {
      this.forEachListenerWithGlobal(OnQuestStateChangeListener.class, (onQuestStateChangeListener) -> {
         onQuestStateChangeListener.onQuestStateChange(this.getActor(), questState);
      });
   }

   public void onOlyCompetitionCompleted(Competition competition, boolean isWin) {
      this.forEachListenerWithGlobal(OnOlyCompetitionListener.class, (onOlyCompetitionListener) -> {
         onOlyCompetitionListener.onOlyCompetitionCompleted(this.getActor(), competition, isWin);
      });
   }

   public void onGainExpSp(long exp, long sp) {
      this.forEachListenerWithGlobal(OnGainExpSpListener.class, (onGainExpSpListener) -> {
         onGainExpSpListener.onGainExpSp(this.getActor(), exp, sp);
      });
   }

   public void onPvpPkKill(Player victim, boolean isPk) {
      this.forEachListenerWithGlobal(OnPvpPkKillListener.class, (onPvpPkKillListener) -> {
         onPvpPkKillListener.onPvpPkKill(this.getActor(), victim, isPk);
      });
   }

   public void onItemPickup(ItemInstance item) {
      this.forEachListenerWithGlobal(OnItemPickupListener.class, (onItemPickupListener) -> {
         onItemPickupListener.onItemPickup(this.getActor(), item);
      });
   }

   public void onLevelUp(int level) {
      this.forEachListenerWithGlobal(OnLevelUpListener.class, (onLevelUpListener) -> {
         onLevelUpListener.onLevelUp(this.getActor(), level);
      });
   }

   public void onSay(int type, String target, String text) {
      this.forEachListenerWithGlobal(OnPlayerSayListener.class, (onPlayerSayListener) -> {
         onPlayerSayListener.onSay(this.getActor(), type, target, text);
      });
   }

   public void onSetPrivateStoreType(int type) {
      this.forEachListenerWithGlobal(OnSetPrivateStoreType.class, (onSetPrivateStoreType) -> {
         onSetPrivateStoreType.onSetPrivateStoreType(this.getActor(), type);
      });
   }

   public void onAutoSoulShot(int itemId, boolean enable) {
      this.forEachListenerWithGlobal(OnAutoSoulShotListener.class, (onAutoSoulShotListener) -> {
         onAutoSoulShotListener.onAutoSoulShot(this.getActor(), itemId, enable);
      });
   }

   public void onSetLevel(int level) {
      this.forEachListenerWithGlobal(OnSetLevelListener.class, (onSetLevelListener) -> {
         onSetLevelListener.onSetLevel(this.getActor(), level);
      });
   }

   public void onSetClass(int classId) {
      this.forEachListenerWithGlobal(OnSetClassListener.class, (onSetClassListener) -> {
         onSetClassListener.onSetClass(this.getActor(), classId);
      });
   }

   public void onPartyInvite() {
      this.forEachListenerWithGlobal(OnPlayerPartyInviteListener.class, (onPlayerPartyInviteListener) -> {
         onPlayerPartyInviteListener.onPartyInvite(this.getActor());
      });
   }

   public void onPartyLeave() {
      this.forEachListenerWithGlobal(OnPlayerPartyLeaveListener.class, (onPlayerPartyLeaveListener) -> {
         onPlayerPartyLeaveListener.onPartyLeave(this.getActor());
      });
   }

   public void onAugmentItem(ItemInstance item, int opt1, int opt2) {
      this.forEachListenerWithGlobal(OnAugmentItemListener.class, (onAugmentItemListener) -> {
         onAugmentItemListener.onAugmentItem(this.getActor(), item, opt1, opt2);
      });
   }

   public void onBecomeHero(int classId) {
      this.forEachListenerWithGlobal(OnBecomeHeroListener.class, (onBecomeHeroListener) -> {
         onBecomeHeroListener.onBecomeHero(this.getActor(), classId);
      });
   }

   public void onCraftItem(int itemId, long itemCount) {
      this.forEachListenerWithGlobal(OnCraftItemListener.class, (onCraftItemListener) -> {
         onCraftItemListener.onCraftItem(this.getActor(), itemId, itemCount);
      });
   }

   public void onEnchantItem(ItemInstance item, boolean success) {
      this.forEachListenerWithGlobal(OnEnchantItemListener.class, (onEnchantItemListener) -> {
         onEnchantItemListener.onEnchantItem(this.getActor(), item, success);
      });
   }

   public void onEnchantSkill(Skill skill, boolean success) {
      this.forEachListenerWithGlobal(OnEnchantSkillListener.class, (onEnchantSkillListener) -> {
         onEnchantSkillListener.onEnchantSkill(this.getActor(), skill, success);
      });
   }

   public void onFinishQuest(QuestState questState) {
      this.forEachListenerWithGlobal(OnFinishQuestListener.class, (onFinishQuestListener) -> {
         onFinishQuestListener.onFinishQuest(this.getActor(), questState);
      });
   }

   public void onHtmlProgressOpening() {
      this.forEachListenerWithGlobal(OnHtmlProgressOpeningListener.class, (onHtmlProgressOpeningListener) -> {
         onHtmlProgressOpeningListener.onHtmlProgressOpening(this.getActor());
      });
   }

   public void onBossHuntingEvent(boolean isWin) {
      this.forEachListenerWithGlobal(OnBossHuntingEventListener.class, (onBossHuntingEventListener) -> {
         onBossHuntingEventListener.onBossHuntingEvent(this.getActor(), isWin);
      });
   }

   public void onTvtEvent(boolean isWin) {
      this.forEachListenerWithGlobal(OnTvtEventListener.class, (onTvtEventListener) -> {
         onTvtEventListener.onTvtEvent(this.getActor(), isWin);
      });
   }

   public void onCtFEvent(boolean isWin) {
      this.forEachListenerWithGlobal(OnCtfEventListener.class, (onCtfEventListener) -> {
         onCtfEventListener.onCtfEvent(this.getActor(), isWin);
      });
   }

   public void onDeathMatchEvent(boolean isWin, int place) {
      this.forEachListenerWithGlobal(OnDeathMatchEventListener.class, (onDeathMatchEventListener) -> {
         onDeathMatchEventListener.onDeathMatchEvent(this.getActor(), isWin, place);
      });
   }

   public void onRoomOfPowerEvent(boolean isWin) {
      this.forEachListenerWithGlobal(OnRoomOfPowerEventListener.class, (onRoomOfPowerEventListener) -> {
         onRoomOfPowerEventListener.onRoomOfPowerEvent(this.getActor(), isWin);
      });
   }

   public void onTreasureHuntingEvent(boolean isWin) {
      this.forEachListenerWithGlobal(OnTreasureHuntingEventListener.class, (onTreasureHuntingEventListener) -> {
         onTreasureHuntingEventListener.onTreasureHuntingEvent(this.getActor(), isWin);
      });
   }

   public void onCaptureCastleEvent(boolean isWin) {
      this.forEachListenerWithGlobal(OnCaptureCastleEventListener.class, (onCaptureCastleEventListener) -> {
         onCaptureCastleEventListener.onCaptureCastleEvent(this.getActor(), isWin);
      });
   }

   public void onGvGEvent(boolean isWin) {
      this.forEachListenerWithGlobal(OnGvGEventListener.class, (onCaptureCastleEventListener) -> {
         onCaptureCastleEventListener.onGvGEvent(this.getActor(), isWin);
      });
   }

   public void onTopKillerDinoEvent(int place) {
      this.forEachListenerWithGlobal(OnTopKillerDinoEventListener.class, (onTopKillerDinoEventListener) -> {
         onTopKillerDinoEventListener.onTopKillerDinoEvent(this.getActor(), place);
      });
   }

   public void onTopKillerPvpEvent(int place) {
      this.forEachListenerWithGlobal(OnTopKillerPvpEventListener.class, (onTopKillerPvpEventListener) -> {
         onTopKillerPvpEventListener.onTopKillerPvpEvent(this.getActor(), place);
      });
   }

   public void onTopKillerPkEvent(int place) {
      this.forEachListenerWithGlobal(OnTopKillerPkEventListener.class, (onTopKillerPkEventListener) -> {
         onTopKillerPkEventListener.onTopKillerPkEvent(this.getActor(), place);
      });
   }
}
