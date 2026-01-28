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

public class BattleGvG5x5 extends BattleGvG {
   private static final Logger _log = LoggerFactory.getLogger(BattleGvG5x5.class);
   private static final BattleGvG5x5 _instance = new BattleGvG5x5();
   private final String[] _voiceCommandList = new String[]{"gvg5", "g5showteams", "g5removegroup", "g5showreg", "g5story", "g5expec", "g5watch", "g5addgroup"};

   public static final BattleGvG5x5 getInstance() {
      return _instance;
   }

   public void load() {
      AdminCommandHandler.getInstance().registerAdminCommandHandler(new BattleGvG5x5.BattleGvgAdm());
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new BattleGvG5x5.BattleGvgVoice());
      this.onLoad(BattleType.B5X5);
   }

   public String[] getVoiceCommandsByType() {
      return this._voiceCommandList;
   }

   public class BattleGvgAdm implements IAdminCommandHandler {
      public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
         if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
         } else {
            BattleGvG5x5.Commands command = (BattleGvG5x5.Commands)comm;
            switch(command) {
            case admin_gvg5_start:
               activeChar.sendMessage("Try to start GvG " + BattleGvG5x5.this.getType().getNameType() + " registration and stop global task.");
               BattleGvG5x5.this.now(activeChar);
               break;
            case admin_gvg5_stop:
               activeChar.sendMessage("Try to stop GvG " + BattleGvG5x5.this.getType().getNameType() + " event.");
               BattleGvG5x5.this.stop(activeChar);
               break;
            case admin_gvg5_auto:
               activeChar.sendMessage("Try to start GvG " + BattleGvG5x5.this.getType().getNameType() + " global task.");
               BattleGvG5x5.this.auto(activeChar);
               break;
            case admin_gvg5_reg:
               if (wordList.length > 2) {
                  String name = wordList[1];
                  String team = wordList[2];
                  Player partic = GameObjectsStorage.getPlayer(name);
                  if (partic == null) {
                     activeChar.sendMessage("Player " + name + " not found in game.");
                     return false;
                  }

                  BattleGvG5x5.this.addReg(activeChar, partic, team);
               } else {
                  activeChar.sendMessage("Usage: //gvg_reg5 <nick> <team_name>");
               }
               break;
            case admin_gvg5_fugas:
               activeChar.sendMessage("Try fugas all players on GvG " + BattleGvG5x5.this.getType().getNameType() + " arena.");
               BattleGvG5x5.this.fugas(activeChar);
            }

            return true;
         }
      }

      public Enum[] getAdminCommandEnum() {
         return BattleGvG5x5.Commands.values();
      }
   }

   private static enum Commands {
      admin_gvg5_start,
      admin_gvg5_stop,
      admin_gvg5_auto,
      admin_gvg5_reg,
      admin_gvg5_fugas;
   }

   public class BattleGvgVoice implements IVoicedCommandHandler {
      public boolean useVoicedCommand(String command, Player player, String args) {
         if (command.equalsIgnoreCase(BattleGvG5x5.this.getVoiceCommandsByType()[0])) {
            BattleGvG5x5.this.status(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG5x5.this.getVoiceCommandsByType()[1])) {
            BattleGvG5x5.this.showTeams(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG5x5.this.getVoiceCommandsByType()[2])) {
            BattleGvG5x5.this.removeGroup(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG5x5.this.getVoiceCommandsByType()[3])) {
            BattleGvG5x5.this.showReg(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG5x5.this.getVoiceCommandsByType()[4])) {
            BattleGvG5x5.this.story(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG5x5.this.getVoiceCommandsByType()[5])) {
            BattleGvG5x5.this.expec(player);
            return true;
         } else if (command.startsWith(BattleGvG5x5.this.getVoiceCommandsByType()[6])) {
            BattleGvG5x5.this.watch(player, args.split(" "));
            return true;
         } else if (command.startsWith(BattleGvG5x5.this.getVoiceCommandsByType()[7])) {
            BattleGvG5x5.this.addGroup(player, args.split(" "));
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return BattleGvG5x5.this._voiceCommandList;
      }
   }
}
