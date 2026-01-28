package npc.model;

import events.TreasureHunting.ConfigTreasureHunting;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class TreasureEventInstance extends ChestInstance {
   private static final int TREASURE_BOMB_ID = 4143;

   public TreasureEventInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
   }

   public void tryOpen(Player opener, Skill skill) {
      if (skill.getItemConsumeId() != null && skill.getItemConsumeId().length != 0 && ConfigTreasureHunting.TREASURE_HUNTING_CHEST_KEY_ID == skill.getItemConsumeId()[0]) {
         double chance = ConfigTreasureHunting.TREASURE_HUNTING_CHEST_OPENED_CHANCE;
         if (Rnd.chance(chance)) {
            this.doDie(opener);
         } else {
            this.fakeOpen(opener);
         }

         opener.sendPacket(new PlaySound("ItemSound2.broken_key"));
      } else {
         if (opener.isLangRus()) {
            opener.sendMessage("Данный предмет не подходит для открытия сундука.");
         } else {
            opener.sendMessage("This item is not suitable for opening chests.");
         }

      }
   }

   private void fakeOpen(Creature opener) {
      Skill bomb = SkillTable.getInstance().getInfo(4143, this.getBombLvl());
      if (bomb != null) {
         this.doCast(bomb, opener, false);
      }

      this.onDecay();
   }

   private int getBombLvl() {
      int npcLvl = this.getLevel();
      int lvl = 1;
      if (npcLvl >= 78) {
         lvl = 10;
      } else if (npcLvl >= 72) {
         lvl = 9;
      } else if (npcLvl >= 66) {
         lvl = 8;
      } else if (npcLvl >= 60) {
         lvl = 7;
      } else if (npcLvl >= 54) {
         lvl = 6;
      } else if (npcLvl >= 48) {
         lvl = 5;
      } else if (npcLvl >= 42) {
         lvl = 4;
      } else if (npcLvl >= 36) {
         lvl = 3;
      } else if (npcLvl >= 30) {
         lvl = 2;
      }

      return lvl;
   }

   public boolean isInvul() {
      return true;
   }

   public boolean isImmobilized() {
      return true;
   }
}
