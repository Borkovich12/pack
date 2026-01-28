package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.GameClient.GameClientState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.RestartResponse;

public class RequestRestart extends L2GameClientPacket {
   protected void readImpl() {
   }

   protected void runImpl() {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
         if (activeChar.isInObserverMode()) {
            activeChar.sendPacket(new IStaticPacket[]{SystemMsg.OBSERVERS_CANNOT_PARTICIPATE, RestartResponse.FAIL, ActionFail.STATIC});
         } else if (!activeChar.isReg() && !activeChar.isInGvG() && !activeChar.isInTreasureHunting() && !activeChar.isInBossHunting() && !activeChar.isInRop()) {
            if (activeChar.isInCombat()) {
               activeChar.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_RESTART_WHILE_IN_COMBAT, RestartResponse.FAIL, ActionFail.STATIC});
            } else if (activeChar.isFishing()) {
               activeChar.sendPacket(new IStaticPacket[]{SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2, RestartResponse.FAIL, ActionFail.STATIC});
            } else if (activeChar.isBlocked() && !activeChar.isFlying()) {
               activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestRestart.OutOfControl", activeChar, new Object[0]));
               activeChar.sendPacket(new IStaticPacket[]{RestartResponse.FAIL, ActionFail.STATIC});
            } else if (activeChar.isFestivalParticipant() && SevenSignsFestival.getInstance().isFestivalInitialized()) {
               activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestRestart.Festival", activeChar, new Object[0]));
               activeChar.sendPacket(new IStaticPacket[]{RestartResponse.FAIL, ActionFail.STATIC});
            } else {
               if (this.getClient() != null) {
                  ((GameClient)this.getClient()).setState(GameClientState.AUTHED);
               }

               activeChar.restart();
               CharacterSelectionInfo cl = new CharacterSelectionInfo(((GameClient)this.getClient()).getLogin(), ((GameClient)this.getClient()).getSessionKey().playOkID1);
               this.sendPacket(new L2GameServerPacket[]{RestartResponse.OK, cl});
               ((GameClient)this.getClient()).setCharSelection(cl.getCharInfo());
            }
         } else {
            activeChar.sendMessage(activeChar.isLangRus() ? "Вы участник регистрационного события и не можете покинуть игру." : "You can't do it by participating in the event.");
            activeChar.sendActionFailed();
         }
      }
   }
}
