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

public class BattleGvG3x3 extends BattleGvG {
   private static final Logger _log = LoggerFactory.getLogger(BattleGvG3x3.class);
   private static final BattleGvG3x3 _instance = new BattleGvG3x3();
   private final String[] _voiceCommandList = new String[]{"gvg3", "g3showteams", "g3removegroup", "g3showreg", "g3story", "g3expec", "g3watch", "g3addgroup"};

   public static final BattleGvG3x3 getInstance() {
      return _instance;
   }

   public void load() {
      AdminCommandHandler.getInstance().registerAdminCommandHandler(new BattleGvG3x3.BattleGvgAdm());
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new BattleGvG3x3.BattleGvgVoice());
      this.onLoad(BattleType.B3X3);
   }

   public String[] getVoiceCommandsByType() {
      return this._voiceCommandList;
   }

   public class BattleGvgAdm implements IAdminCommandHandler {
      public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
         if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
         } else {
            BattleGvG3x3.Commands command = (BattleGvG3x3.Commands)comm;
            switch(command) {
            case admin_gvg3_start:
               activeChar.sendMessage("Try to start GvG " + BattleGvG3x3.this.getType().getNameType() + " registration and stop global task.");
               BattleGvG3x3.this.now(activeChar);
               break;
            case admin_gvg3_stop:
               activeChar.sendMessage("Try to stop GvG " + BattleGvG3x3.this.getType().getNameType() + " event.");
               BattleGvG3x3.this.stop(activeChar);
               break;
            case admin_gvg3_auto:
               activeChar.sendMessage("Try to start GvG " + BattleGvG3x3.this.getType().getNameType() + " global task.");
               BattleGvG3x3.this.auto(activeChar);
               break;
            case admin_gvg3_reg:
               if (wordList.length > 2) {
                  String name = wordList[1];
                  String team = wordList[2];
                  Player partic = GameObjectsStorage.getPlayer(name);
                  if (partic == null) {
                     activeChar.sendMessage("Player " + name + " not found in game.");
                     return false;
                  }

                  BattleGvG3x3.this.addReg(activeChar, partic, team);
               } else {
                  activeChar.sendMessage("Usage: //gvg_reg3 <nick> <team_name>");
               }
               break;
            case admin_gvg3_fugas:
               activeChar.sendMessage("Try fugas all players on GvG " + BattleGvG3x3.this.getType().getNameType() + " arena.");
               BattleGvG3x3.this.fugas(activeChar);
            }

            return true;
         }
      }

      public Enum[] getAdminCommandEnum() {
         return BattleGvG3x3.Commands.values();
      }
   }

   private static enum Commands {
      admin_gvg3_start,
      admin_gvg3_stop,
      admin_gvg3_auto,
      admin_gvg3_reg,
      admin_gvg3_fugas;
   }

   public class BattleGvgVoice implements IVoicedCommandHandler {
      public boolean useVoicedCommand(String command, Player player, String args) {
         if (command.equalsIgnoreCase(BattleGvG3x3.this.getVoiceCommandsByType()[0])) {
            BattleGvG3x3.this.status(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG3x3.this.getVoiceCommandsByType()[1])) {
            BattleGvG3x3.this.showTeams(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG3x3.this.getVoiceCommandsByType()[2])) {
            BattleGvG3x3.this.removeGroup(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG3x3.this.getVoiceCommandsByType()[3])) {
            BattleGvG3x3.this.showReg(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG3x3.this.getVoiceCommandsByType()[4])) {
            BattleGvG3x3.this.story(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG3x3.this.getVoiceCommandsByType()[5])) {
            BattleGvG3x3.this.expec(player);
            return true;
         } else if (command.startsWith(BattleGvG3x3.this.getVoiceCommandsByType()[6])) {
            BattleGvG3x3.this.watch(player, args.split(" "));
            return true;
         } else if (command.startsWith(BattleGvG3x3.this.getVoiceCommandsByType()[7])) {
            BattleGvG3x3.this.addGroup(player, args.split(" "));
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return BattleGvG3x3.this._voiceCommandList;
      }
   }
}
