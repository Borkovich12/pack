package npc.model;

import events.RoomOfPower.RoomOfPower;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class ROPRaidBossInstance extends RaidBossInstance {
   public ROPRaidBossInstance(int objectId, NpcTemplate template) {
      super(objectId, template);
   }

   public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
      if (RoomOfPower._status == 2 && damage > 0.0D && attacker != null && attacker.isPlayer() && attacker.getTeam() != TeamType.NONE && RoomOfPower.live_list.contains(attacker.getStoredId())) {
         double damageModify = (Double)RoomOfPower.PLAYER_DAMAGE_MODIFY.get(RoomOfPower.live_list.size());
         damage = Math.max(1.0D, damage * damageModify);
      }

      super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
   }

   public int getPAtk(Creature creature) {
      int pAtk = super.getPAtk(creature);
      double damageModify = (Double)RoomOfPower.NPC_DAMAGE_MODIFY.get(RoomOfPower.live_list.size());
      int finalPAtk = (int)Math.max(1.0D, (double)pAtk * damageModify);
      return finalPAtk;
   }
}
