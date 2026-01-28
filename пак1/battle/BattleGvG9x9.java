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

public class BattleGvG9x9 extends BattleGvG {
   private static final Logger _log = LoggerFactory.getLogger(BattleGvG9x9.class);
   private static final BattleGvG9x9 _instance = new BattleGvG9x9();
   private final String[] _voiceCommandList = new String[]{"gvg9", "g9showteams", "g9removegroup", "g9showreg", "g9story", "g9expec", "g9watch", "g9addgroup"};

   public static final BattleGvG9x9 getInstance() {
      return _instance;
   }

   public void load() {
      AdminCommandHandler.getInstance().registerAdminCommandHandler(new BattleGvG9x9.BattleGvgAdm());
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new BattleGvG9x9.BattleGvgVoice());
      this.onLoad(BattleType.B9X9);
   }

   public String[] getVoiceCommandsByType() {
      return this._voiceCommandList;
   }

   public class BattleGvgAdm implements IAdminCommandHandler {
      public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
         if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
         } else {
            BattleGvG9x9.Commands command = (BattleGvG9x9.Commands)comm;
            switch(command) {
            case admin_gvg9_start:
               activeChar.sendMessage("Try to start GvG " + BattleGvG9x9.this.getType().getNameType() + " registration and stop global task.");
               BattleGvG9x9.this.now(activeChar);
               break;
            case admin_gvg9_stop:
               activeChar.sendMessage("Try to stop GvG " + BattleGvG9x9.this.getType().getNameType() + " event.");
               BattleGvG9x9.this.stop(activeChar);
               break;
            case admin_gvg9_auto:
               activeChar.sendMessage("Try to start GvG " + BattleGvG9x9.this.getType().getNameType() + " global task.");
               BattleGvG9x9.this.auto(activeChar);
               break;
            case admin_gvg9_reg:
               if (wordList.length > 2) {
                  String name = wordList[1];
                  String team = wordList[2];
                  Player partic = GameObjectsStorage.getPlayer(name);
                  if (partic == null) {
                     activeChar.sendMessage("Player " + name + " not found in game.");
                     return false;
                  }

                  BattleGvG9x9.this.addReg(activeChar, partic, team);
               } else {
                  activeChar.sendMessage("Usage: //gvg_reg9 <nick> <team_name>");
               }
               break;
            case admin_gvg9_fugas:
               activeChar.sendMessage("Try fugas all players on GvG " + BattleGvG9x9.this.getType().getNameType() + " arena.");
               BattleGvG9x9.this.fugas(activeChar);
            }

            return true;
         }
      }

      public Enum[] getAdminCommandEnum() {
         return BattleGvG9x9.Commands.values();
      }
   }

   private static enum Commands {
      admin_gvg9_start,
      admin_gvg9_stop,
      admin_gvg9_auto,
      admin_gvg9_reg,
      admin_gvg9_fugas;
   }

   public class BattleGvgVoice implements IVoicedCommandHandler {
      public boolean useVoicedCommand(String command, Player player, String args) {
         if (command.equalsIgnoreCase(BattleGvG9x9.this.getVoiceCommandsByType()[0])) {
            BattleGvG9x9.this.status(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG9x9.this.getVoiceCommandsByType()[1])) {
            BattleGvG9x9.this.showTeams(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG9x9.this.getVoiceCommandsByType()[2])) {
            BattleGvG9x9.this.removeGroup(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG9x9.this.getVoiceCommandsByType()[3])) {
            BattleGvG9x9.this.showReg(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG9x9.this.getVoiceCommandsByType()[4])) {
            BattleGvG9x9.this.story(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG9x9.this.getVoiceCommandsByType()[5])) {
            BattleGvG9x9.this.expec(player);
            return true;
         } else if (command.startsWith(BattleGvG9x9.this.getVoiceCommandsByType()[6])) {
            BattleGvG9x9.this.watch(player, args.split(" "));
            return true;
         } else if (command.startsWith(BattleGvG9x9.this.getVoiceCommandsByType()[7])) {
            BattleGvG9x9.this.addGroup(player, args.split(" "));
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return BattleGvG9x9.this._voiceCommandList;
      }
   }
}
