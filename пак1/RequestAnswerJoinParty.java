package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.Request.L2RequestType;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.JoinParty;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestAnswerJoinParty extends L2GameClientPacket {
   private int _response;

   protected void readImpl() {
      if (this._buf.hasRemaining()) {
         this._response = this.readD();
      } else {
         this._response = 0;
      }

   }

   protected void runImpl() {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
         Request request = activeChar.getRequest();
         if (request != null && request.isTypeOf(L2RequestType.PARTY)) {
            if (!request.isInProgress()) {
               request.cancel();
               activeChar.sendActionFailed();
            } else if (activeChar.isOutOfControl()) {
               request.cancel();
               activeChar.sendActionFailed();
            } else {
               Player requestor = request.getRequestor();
               if (requestor == null) {
                  request.cancel();
                  activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
                  activeChar.sendActionFailed();
               } else if (requestor.getRequest() != request) {
                  request.cancel();
                  activeChar.sendActionFailed();
               } else if (this._response <= 0) {
                  request.cancel();
                  requestor.sendPacket(JoinParty.FAIL);
               } else if (activeChar.isOlyParticipant()) {
                  request.cancel();
                  activeChar.sendPacket(SystemMsg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
                  requestor.sendPacket(JoinParty.FAIL);
               } else if (requestor.isOlyParticipant()) {
                  request.cancel();
                  requestor.sendPacket(JoinParty.FAIL);
               } else {
                  Party party = requestor.getParty();
                  if (party != null && party.getMemberCount() >= Config.ALT_MAX_PARTY_SIZE) {
                     request.cancel();
                     activeChar.sendPacket(SystemMsg.THE_PARTY_IS_FULL);
                     requestor.sendPacket(SystemMsg.THE_PARTY_IS_FULL);
                     requestor.sendPacket(JoinParty.FAIL);
                  } else {
                     int itemDistribution;
                     if (!Config.ALT_PARTY_CLASS_LIMIT.isEmpty() && Config.ALT_PARTY_CLASS_LIMIT.containsKey(activeChar.getActiveClass().getClassId())) {
                        itemDistribution = 0;
                        if (party != null) {
                           Iterator var6 = party.getPartyMembers().iterator();

                           while(var6.hasNext()) {
                              Player member = (Player)var6.next();
                              if (member.getActiveClass().getClassId() == activeChar.getActiveClass().getClassId()) {
                                 ++itemDistribution;
                              }
                           }
                        } else if (requestor.getActiveClass().getClassId() == activeChar.getActiveClass().getClassId()) {
                           ++itemDistribution;
                        }

                        if (itemDistribution >= (Integer)Config.ALT_PARTY_CLASS_LIMIT.get(activeChar.getActiveClass().getClassId())) {
                           request.cancel();
                           activeChar.sendMessage(new CustomMessage("PARTY_PARTICIPATION_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET", activeChar, new Object[0]));
                           requestor.sendMessage(new CustomMessage("PARTY_PARTICIPATION_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET", activeChar, new Object[0]));
                           requestor.sendPacket(JoinParty.FAIL);
                           return;
                        }
                     }

                     IStaticPacket problem;
                     if (!activeChar.isInTreasureHunting() && !activeChar.isInRop() && !activeChar.isInBossHunting()) {
                        problem = activeChar.canJoinParty(requestor);
                        if (problem != null) {
                           request.cancel();
                           activeChar.sendPacket(new IStaticPacket[]{problem, ActionFail.STATIC});
                           requestor.sendPacket(JoinParty.FAIL);
                           return;
                        }
                     } else {
                        problem = this.canEventJoinParty(activeChar, requestor);
                        if (problem != null) {
                           request.cancel();
                           activeChar.sendPacket(new IStaticPacket[]{problem, ActionFail.STATIC});
                           requestor.sendPacket(JoinParty.FAIL);
                           return;
                        }
                     }

                     if (party == null) {
                        itemDistribution = request.getInteger("itemDistribution");
                        requestor.setParty(party = new Party(requestor, itemDistribution));
                     }

                     try {
                        activeChar.joinParty(party);
                        requestor.sendPacket(JoinParty.SUCCESS);
                     } finally {
                        request.done();
                     }

                  }
               }
            }
         }
      }
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
