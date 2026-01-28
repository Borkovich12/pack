package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.data.xml.holder.PetDataHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.PetData;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.StatsSet;
import services.ConfigAgathion;

public class PetSummon extends Skill {
   public PetSummon(StatsSet set) {
      super(set);
   }

   public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
      Player player = activeChar.getPlayer();
      if (player == null) {
         return false;
      } else {
         Iterator var8;
         GameObject o;
         if (player.getPetControlItem() != null) {
            PetData petData = PetDataHolder.getInstance().getByControlItemId(player.getPetControlItem());
            if (petData == null) {
               return false;
            } else if (player.isInCombat()) {
               player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_COMBAT);
               return false;
            } else if (player.isProcessingRequest()) {
               player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
               return false;
            } else if (!player.isMounted() && player.getPet() == null) {
               if (player.isInBoat()) {
                  player.sendPacket(SystemMsg.YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION);
                  return false;
               } else if (player.isInFlyingTransform()) {
                  return false;
               } else if (player.isOlyParticipant()) {
                  player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
                  return false;
               } else if (player.isCursedWeaponEquipped()) {
                  player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME);
                  return false;
               } else {
                  var8 = World.getAroundObjects(player, 120, 200).iterator();

                  do {
                     if (!var8.hasNext()) {
                        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
                     }

                     o = (GameObject)var8.next();
                  } while(!o.isDoor());

                  player.sendPacket(SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
                  return false;
               }
            } else {
               player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
               return false;
            }
         } else if (player.getAgathionControlItem() != null) {
            Integer npcId = (Integer)ConfigAgathion.AGATHION_DATAS.get(player.getAgathionControlItem().getItemId());
            if (npcId != null && npcId != 0) {
               if (player.isInCombat()) {
                  player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_COMBAT);
                  return false;
               } else if (player.isProcessingRequest()) {
                  player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
                  return false;
               } else if (player.getAgathion() != null) {
                  player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
                  return false;
               } else if (player.isInBoat()) {
                  player.sendPacket(SystemMsg.YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION);
                  return false;
               } else if (player.isInFlyingTransform()) {
                  return false;
               } else if (player.isOlyParticipant()) {
                  player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
                  return false;
               } else if (player.isCursedWeaponEquipped()) {
                  player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME);
                  return false;
               } else {
                  var8 = World.getAroundObjects(player, 120, 200).iterator();

                  do {
                     if (!var8.hasNext()) {
                        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
                     }

                     o = (GameObject)var8.next();
                  } while(!o.isDoor());

                  player.sendPacket(SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public void useSkill(Creature caster, List<Creature> targets) {
      Player activeChar = caster.getPlayer();
      if (activeChar.getPetControlItem() != null) {
         activeChar.summonPet();
      } else if (activeChar.getAgathionControlItem() != null) {
         activeChar.summonAgathion(activeChar);
      }

      if (this.isSSPossible()) {
         caster.unChargeShots(this.isMagic());
      }

   }
}
