package npc.model;

import events.battle.BattleGvG1x1;
import events.battle.BattleGvG2x2;
import events.battle.BattleGvG3x3;
import events.battle.BattleGvG4x4;
import events.battle.BattleGvG5x5;
import events.battle.BattleGvG6x6;
import events.battle.BattleGvG7x7;
import events.battle.BattleGvG8x8;
import events.battle.BattleGvG9x9;
import events.battle.BattleGvGCxC;
import events.battle.enums.BattleType;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class GvgManagerInstance extends NpcInstance {
   public GvgManagerInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
   }

   public void showChatWindow(Player player, int val, Object... replace) {
      if (val == 0) {
         if (this.getNpcId() == BattleType.B1X1.getManagerId()) {
            BattleGvG1x1.getInstance().status(player, this);
         } else if (this.getNpcId() == BattleType.B2X2.getManagerId()) {
            BattleGvG2x2.getInstance().status(player, this);
         } else if (this.getNpcId() == BattleType.B3X3.getManagerId()) {
            BattleGvG3x3.getInstance().status(player, this);
         } else if (this.getNpcId() == BattleType.B4X4.getManagerId()) {
            BattleGvG4x4.getInstance().status(player, this);
         } else if (this.getNpcId() == BattleType.B5X5.getManagerId()) {
            BattleGvG5x5.getInstance().status(player, this);
         } else if (this.getNpcId() == BattleType.B6X6.getManagerId()) {
            BattleGvG6x6.getInstance().status(player, this);
         } else if (this.getNpcId() == BattleType.B7X7.getManagerId()) {
            BattleGvG7x7.getInstance().status(player, this);
         } else if (this.getNpcId() == BattleType.B8X8.getManagerId()) {
            BattleGvG8x8.getInstance().status(player, this);
         } else if (this.getNpcId() == BattleType.B9X9.getManagerId()) {
            BattleGvG9x9.getInstance().status(player, this);
         } else if (this.getNpcId() == BattleType.BCXC.getManagerId()) {
            BattleGvGCxC.getInstance().status(player, this);
         }

      } else {
         super.showChatWindow(player, val, replace);
      }
   }
}
