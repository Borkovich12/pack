package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.AskJoinParty;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestJoinParty extends L2GameClientPacket {
   private String _name;
   private int _itemDistribution;

   protected void readImpl() {
      this._name = this.readS(Config.CNAME_MAXLEN);
      this._itemDistribution = this.readD();
   }

   protected void runImpl() {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
         if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
         } else if (activeChar.isProcessingRequest()) {
            activeChar.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
         } else {
            Player target = World.getPlayer(this._name);
            if (target == null) {
               activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            } else if (target == activeChar) {
               activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
               activeChar.sendActionFailed();
            } else {
               if (!target.isInTreasureHunting() && !target.isInRop() && !target.isInBossHunting()) {
                  if (target.isBusy()) {
                     activeChar.sendPacket((new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK)).addName(target));
                     return;
                  }
               } else if (this.isBusyEvent(target)) {
                  activeChar.sendPacket((new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK)).addName(target));
                  return;
               }

               if (target.getVarB("blockparty@")) {
                  activeChar.sendMessage((new CustomMessage("voicecommands.party_invite_blocked", activeChar, new Object[0])).addCharName(target));
               } else {
                  IStaticPacket problem;
                  if (!target.isInTreasureHunting() && !target.isInRop() && !target.isInBossHunting()) {
                     problem = target.canJoinParty(activeChar);
                     if (problem != null) {
                        activeChar.sendPacket(problem);
                        return;
                     }
                  } else {
                     problem = this.canEventJoinParty(target, activeChar);
                     if (problem != null) {
                        activeChar.sendPacket(problem);
                        return;
                     }
                  }

                  if (activeChar.isInParty()) {
                     if (activeChar.getParty().getMemberCount() >= Config.ALT_MAX_PARTY_SIZE) {
                        activeChar.sendPacket(SystemMsg.THE_PARTY_IS_FULL);
                        return;
                     }

                     if (Config.PARTY_LEADER_ONLY_CAN_INVITE && !activeChar.getParty().isLeader(activeChar)) {
                        activeChar.sendPacket(SystemMsg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
                        return;
                     }

                     if (activeChar.getParty().isInDimensionalRift()) {
                        activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestJoinParty.InDimensionalRift", activeChar, new Object[0]));
                        activeChar.sendActionFailed();
                        return;
                     }
                  }

                  (new Request(L2RequestType.PARTY, activeChar, target)).setTimeout(10000L).set("itemDistribution", this._itemDistribution);
                  target.sendPacket(new AskJoinParty(activeChar.getName(), this._itemDistribution));
                  activeChar.sendPacket((new SystemMessage(SystemMsg.C1_HAS_BEEN_INVITED_TO_THE_PARTY)).addName(target));
               }
            }
         }
      }
   }

   public boolean isBusyEvent(Player player) {
      return player.isProcessingRequest() || player.isOutOfControl() || player.isOlyParticipant() || player.isInStoreMode() || player.isInDuel() || player.getMessageRefusal() || player.isBlockAll() || player.isInvisible();
   }

   public IStaticPacket canEventJoinParty(Player target, Player inviter) {
      Request request = target.getRequest();
      if (request != null && request.isInProgress() && request.getOtherPlayer(target) != inviter) {
         return SystemMsg.WAITING_FOR_ANOTHER_REPLY.packet(inviter);
      } else if (!target.isBlockAll() && !target.getMessageRefusal()) {
         if (target.isInParty()) {
            return (new SystemMessage(SystemMsg.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED)).addName(target);
         } else if (inviter.getReflection() != target.getReflection() && inviter.getReflection() != ReflectionManager.DEFAULT && target.getReflection() != ReflectionManager.DEFAULT) {
            return SystemMsg.INVALID_TARGET.packet(inviter);
         } else if (!target.isCursedWeaponEquipped() && !inviter.isCursedWeaponEquipped()) {
            if (!inviter.isOlyParticipant() && !target.isOlyParticipant()) {
               if (inviter.getPlayerAccess().CanJoinParty && target.getPlayerAccess().CanJoinParty) {
                  return !target.isPartyRefusal() && !inviter.isPartyRefusal() ? null : SystemMsg.INVALID_TARGET.packet(inviter);
               } else {
                  return SystemMsg.INVALID_TARGET.packet(inviter);
               }
            } else {
               return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS.packet(inviter);
            }
         } else {
            return SystemMsg.INVALID_TARGET.packet(inviter);
         }
      } else {
         return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE.packet(inviter);
      }
   }
}
