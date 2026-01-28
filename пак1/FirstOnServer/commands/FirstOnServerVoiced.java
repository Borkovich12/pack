package events.FirstOnServer.commands;

import events.FirstOnServer.FirstOnServerBypass;
import events.FirstOnServer.data.FirstOnServerRewardsHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;

public class FirstOnServerVoiced implements IVoicedCommandHandler {
   public boolean useVoicedCommand(String command, Player activeChar, String target) {
      if (command.equalsIgnoreCase(FirstOnServerRewardsHolder.getInstance().getVoicedCommand())) {
         String filePath = FirstOnServerBypass.getHtmRoot(0);
         FirstOnServerBypass.sendPage(activeChar, 0, 1, (NpcInstance)null, filePath);
         return true;
      } else {
         return false;
      }
   }

   public String[] getVoicedCommandList() {
      return !FirstOnServerRewardsHolder.getInstance().isEnableVoiced() ? new String[0] : new String[]{FirstOnServerRewardsHolder.getInstance().getVoicedCommand()};
   }
}
