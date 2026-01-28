package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;

public class Logout extends L2GameClientPacket {
   protected void readImpl() {
   }

   protected void runImpl() {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
         if (activeChar.isInCombat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_EXIT_THE_GAME_WHILE_IN_COMBAT);
            activeChar.sendActionFailed();
         } else if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
            activeChar.sendActionFailed();
         } else if (activeChar.isBlocked() && !activeChar.isFlying()) {
            activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.Logout.OutOfControl", activeChar, new Object[0]));
            activeChar.sendActionFailed();
         } else if (activeChar.isFestivalParticipant() && SevenSignsFestival.getInstance().isFestivalInitialized()) {
            activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.Logout.Festival", activeChar, new Object[0]));
            activeChar.sendActionFailed();
         } else if (activeChar.isOlyParticipant()) {
            activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.Logout.Olympiad", activeChar, new Object[0]));
            activeChar.sendActionFailed();
         } else if (activeChar.isInObserverMode()) {
            activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.Logout.Observer", activeChar, new Object[0]));
            activeChar.sendActionFailed();
         } else if (!activeChar.isReg() && !activeChar.isInGvG() && !activeChar.isInTreasureHunting() && !activeChar.isInBossHunting() && !activeChar.isInRop()) {
            activeChar.kick();
         } else {
            activeChar.sendMessage(activeChar.isLangRus() ? "Вы участник регистрационного события и не можете покинуть игру." : "You can't do it by participating in the event.");
            activeChar.sendActionFailed();
         }
      }
   }
}
