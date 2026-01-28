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

public class BattleGvG8x8 extends BattleGvG {
   private static final Logger _log = LoggerFactory.getLogger(BattleGvG8x8.class);
   private static final BattleGvG8x8 _instance = new BattleGvG8x8();
   private final String[] _voiceCommandList = new String[]{"gvg8", "g8showteams", "g8removegroup", "g8showreg", "g8story", "g8expec", "g8watch", "g8addgroup"};

   public static final BattleGvG8x8 getInstance() {
      return _instance;
   }

   public void load() {
      AdminCommandHandler.getInstance().registerAdminCommandHandler(new BattleGvG8x8.BattleGvgAdm());
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new BattleGvG8x8.BattleGvgVoice());
      this.onLoad(BattleType.B8X8);
   }

   public String[] getVoiceCommandsByType() {
      return this._voiceCommandList;
   }

   public class BattleGvgAdm implements IAdminCommandHandler {
      public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
         if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
         } else {
            BattleGvG8x8.Commands command = (BattleGvG8x8.Commands)comm;
            switch(command) {
            case admin_gvg8_start:
               activeChar.sendMessage("Try to start GvG " + BattleGvG8x8.this.getType().getNameType() + " registration and stop global task.");
               BattleGvG8x8.this.now(activeChar);
               break;
            case admin_gvg8_stop:
               activeChar.sendMessage("Try to stop GvG " + BattleGvG8x8.this.getType().getNameType() + " event.");
               BattleGvG8x8.this.stop(activeChar);
               break;
            case admin_gvg8_auto:
               activeChar.sendMessage("Try to start GvG " + BattleGvG8x8.this.getType().getNameType() + " global task.");
               BattleGvG8x8.this.auto(activeChar);
               break;
            case admin_gvg8_reg:
               if (wordList.length > 2) {
                  String name = wordList[1];
                  String team = wordList[2];
                  Player partic = GameObjectsStorage.getPlayer(name);
                  if (partic == null) {
                     activeChar.sendMessage("Player " + name + " not found in game.");
                     return false;
                  }

                  BattleGvG8x8.this.addReg(activeChar, partic, team);
               } else {
                  activeChar.sendMessage("Usage: //gvg_reg8 <nick> <team_name>");
               }
               break;
            case admin_gvg8_fugas:
               activeChar.sendMessage("Try fugas all players on GvG " + BattleGvG8x8.this.getType().getNameType() + " arena.");
               BattleGvG8x8.this.fugas(activeChar);
            }

            return true;
         }
      }

      public Enum[] getAdminCommandEnum() {
         return BattleGvG8x8.Commands.values();
      }
   }

   private static enum Commands {
      admin_gvg8_start,
      admin_gvg8_stop,
      admin_gvg8_auto,
      admin_gvg8_reg,
      admin_gvg8_fugas;
   }

   public class BattleGvgVoice implements IVoicedCommandHandler {
      public boolean useVoicedCommand(String command, Player player, String args) {
         if (command.equalsIgnoreCase(BattleGvG8x8.this.getVoiceCommandsByType()[0])) {
            BattleGvG8x8.this.status(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG8x8.this.getVoiceCommandsByType()[1])) {
            BattleGvG8x8.this.showTeams(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG8x8.this.getVoiceCommandsByType()[2])) {
            BattleGvG8x8.this.removeGroup(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG8x8.this.getVoiceCommandsByType()[3])) {
            BattleGvG8x8.this.showReg(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG8x8.this.getVoiceCommandsByType()[4])) {
            BattleGvG8x8.this.story(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG8x8.this.getVoiceCommandsByType()[5])) {
            BattleGvG8x8.this.expec(player);
            return true;
         } else if (command.startsWith(BattleGvG8x8.this.getVoiceCommandsByType()[6])) {
            BattleGvG8x8.this.watch(player, args.split(" "));
            return true;
         } else if (command.startsWith(BattleGvG8x8.this.getVoiceCommandsByType()[7])) {
            BattleGvG8x8.this.addGroup(player, args.split(" "));
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return BattleGvG8x8.this._voiceCommandList;
      }
   }
}
