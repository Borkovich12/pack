package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.scripts.Functions;

public class RequestObserverEnd extends L2GameClientPacket {
   protected void readImpl() {
   }

   protected void runImpl() {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
         if (activeChar.isInObserverMode()) {
            if (activeChar.isOlyObserver()) {
               activeChar.leaveOlympiadObserverMode();
            } else {
               activeChar.leaveObserverMode();
               if (activeChar.getVarB("onObservationEnd")) {
                  Functions.callScripts("events.battle.util.BattleUtil", "removeSpec", new Object[]{activeChar.getRef(), activeChar.getVar("onObservationEnd")});
               }

               if (activeChar.getVarB("onTHObservationEnd")) {
                  Functions.callScripts("events.TreasureHunting.TreasureHunting", "removeSpec", new Object[]{activeChar.getObjectId()});
                  activeChar.unsetVar("onTHObservationEnd");
               }

               if (activeChar.getVarB("onBHObservationEnd")) {
                  Functions.callScripts("events.BossHunting.BossHunting", "removeSpec", new Object[]{activeChar.getObjectId()});
                  activeChar.unsetVar("onBHObservationEnd");
               }

               if (activeChar.getVarB("onCCObservationEnd")) {
                  Functions.callScripts("events.CaptureCastle.CaptureCastle", "removeSpec", new Object[]{activeChar.getObjectId()});
                  activeChar.unsetVar("onCCObservationEnd");
               }

               activeChar.setReflection(ReflectionManager.DEFAULT);
            }
         }

      }
   }
}
