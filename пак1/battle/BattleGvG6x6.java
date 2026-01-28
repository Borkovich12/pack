package events.battle;

import events.battle.enums.BattleType;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleGvG6x6 extends BattleGvG {
   private static final Logger _log = LoggerFactory.getLogger(BattleGvG6x6.class);
   private static final BattleGvG6x6 _instance = new BattleGvG6x6();
   private final String[] _voiceCommandList = new String[]{"gvg6", "g6showteams", "g6removegroup", "g6showreg", "g6story", "g6expec", "g6watch", "g6addgroup"};

   public static final BattleGvG6x6 getInstance() {
      return _instance;
   }

   public void load() {
      AdminCommandHandler.getInstance().registerAdminCommandHandler(new BattleGvG6x6.BattleGvgAdm());
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new BattleGvG6x6.BattleGvgVoice());
      this.onLoad(BattleType.B6X6);
   }

   public String[] getVoiceCommandsByType() {
      return this._voiceCommandList;
   }

   public class BattleGvgAdm implements IAdminCommandHandler {
      public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
         if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
         } else {
            BattleGvG6x6.Commands command = (BattleGvG6x6.Commands)comm;
            switch(command) {
            case admin_gvg6_start:
               activeChar.sendMessage("Try to start GvG " + BattleGvG6x6.this.getType().getNameType() + " registration and stop global task.");
               BattleGvG6x6.this.now(activeChar);
               break;
            case admin_gvg6_stop:
               activeChar.sendMessage("Try to stop GvG " + BattleGvG6x6.this.getType().getNameType() + " event.");
               BattleGvG6x6.this.stop(activeChar);
               break;
            case admin_gvg6_auto:
               activeChar.sendMessage("Try to start GvG " + BattleGvG6x6.this.getType().getNameType() + " global task.");
               BattleGvG6x6.this.auto(activeChar);
               break;
            case admin_gvg6_reg:
               if (wordList.length > 2) {
                  String name = wordList[1];
                  String team = wordList[2];
                  Player partic = GameObjectsStorage.getPlayer(name);
                  if (partic == null) {
                     activeChar.sendMessage("Player " + name + " not found in game.");
                     return false;
                  }

                  BattleGvG6x6.this.addReg(activeChar, partic, team);
               } else {
                  activeChar.sendMessage("Usage: //gvg_reg6 <nick> <team_name>");
               }
               break;
            case admin_gvg6_fugas:
               activeChar.sendMessage("Try fugas all players on GvG " + BattleGvG6x6.this.getType().getNameType() + " arena.");
               BattleGvG6x6.this.fugas(activeChar);
            }

            return true;
         }
      }

      public Enum[] getAdminCommandEnum() {
         return BattleGvG6x6.Commands.values();
      }
   }

   private static enum Commands {
      admin_gvg6_start,
      admin_gvg6_stop,
      admin_gvg6_auto,
      admin_gvg6_reg,
      admin_gvg6_fugas;
   }

   public class BattleGvgVoice implements IVoicedCommandHandler {
      public boolean useVoicedCommand(String command, Player player, String args) {
         if (command.equalsIgnoreCase(BattleGvG6x6.this.getVoiceCommandsByType()[0])) {
            BattleGvG6x6.this.status(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG6x6.this.getVoiceCommandsByType()[1])) {
            BattleGvG6x6.this.showTeams(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG6x6.this.getVoiceCommandsByType()[2])) {
            BattleGvG6x6.this.removeGroup(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG6x6.this.getVoiceCommandsByType()[3])) {
            BattleGvG6x6.this.showReg(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG6x6.this.getVoiceCommandsByType()[4])) {
            BattleGvG6x6.this.story(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG6x6.this.getVoiceCommandsByType()[5])) {
            BattleGvG6x6.this.expec(player);
            return true;
         } else if (command.startsWith(BattleGvG6x6.this.getVoiceCommandsByType()[6])) {
            BattleGvG6x6.this.watch(player, args.split(" "));
            return true;
         } else if (command.startsWith(BattleGvG6x6.this.getVoiceCommandsByType()[7])) {
            BattleGvG6x6.this.addGroup(player, args.split(" "));
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return BattleGvG6x6.this._voiceCommandList;
      }
   }
}
