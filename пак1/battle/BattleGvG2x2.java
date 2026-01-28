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

public class BattleGvG2x2 extends BattleGvG {
   private static final Logger _log = LoggerFactory.getLogger(BattleGvG2x2.class);
   private static final BattleGvG2x2 _instance = new BattleGvG2x2();
   private final String[] _voiceCommandList = new String[]{"gvg2", "g2showteams", "g2removegroup", "g2showreg", "g2story", "g2expec", "g2watch", "g2addgroup"};

   public static final BattleGvG2x2 getInstance() {
      return _instance;
   }

   public void load() {
      AdminCommandHandler.getInstance().registerAdminCommandHandler(new BattleGvG2x2.BattleGvgAdm());
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new BattleGvG2x2.BattleGvgVoice());
      this.onLoad(BattleType.B2X2);
   }

   public String[] getVoiceCommandsByType() {
      return this._voiceCommandList;
   }

   public class BattleGvgAdm implements IAdminCommandHandler {
      public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
         if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
         } else {
            BattleGvG2x2.Commands command = (BattleGvG2x2.Commands)comm;
            switch(command) {
            case admin_gvg2_start:
               activeChar.sendMessage("Try to start GvG " + BattleGvG2x2.this.getType().getNameType() + " registration and stop global task.");
               BattleGvG2x2.this.now(activeChar);
               break;
            case admin_gvg2_stop:
               activeChar.sendMessage("Try to stop GvG " + BattleGvG2x2.this.getType().getNameType() + " event.");
               BattleGvG2x2.this.stop(activeChar);
               break;
            case admin_gvg2_auto:
               activeChar.sendMessage("Try to start GvG " + BattleGvG2x2.this.getType().getNameType() + " global task.");
               BattleGvG2x2.this.auto(activeChar);
               break;
            case admin_gvg2_reg:
               if (wordList.length > 2) {
                  String name = wordList[1];
                  String team = wordList[2];
                  Player partic = GameObjectsStorage.getPlayer(name);
                  if (partic == null) {
                     activeChar.sendMessage("Player " + name + " not found in game.");
                     return false;
                  }

                  BattleGvG2x2.this.addReg(activeChar, partic, team);
               } else {
                  activeChar.sendMessage("Usage: //gvg_reg2 <nick> <team_name>");
               }
               break;
            case admin_gvg2_fugas:
               activeChar.sendMessage("Try fugas all players on GvG " + BattleGvG2x2.this.getType().getNameType() + " arena.");
               BattleGvG2x2.this.fugas(activeChar);
            }

            return true;
         }
      }

      public Enum[] getAdminCommandEnum() {
         return BattleGvG2x2.Commands.values();
      }
   }

   private static enum Commands {
      admin_gvg2_start,
      admin_gvg2_stop,
      admin_gvg2_auto,
      admin_gvg2_reg,
      admin_gvg2_fugas;
   }

   public class BattleGvgVoice implements IVoicedCommandHandler {
      public boolean useVoicedCommand(String command, Player player, String args) {
         if (command.equalsIgnoreCase(BattleGvG2x2.this.getVoiceCommandsByType()[0])) {
            BattleGvG2x2.this.status(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG2x2.this.getVoiceCommandsByType()[1])) {
            BattleGvG2x2.this.showTeams(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG2x2.this.getVoiceCommandsByType()[2])) {
            BattleGvG2x2.this.removeGroup(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG2x2.this.getVoiceCommandsByType()[3])) {
            BattleGvG2x2.this.showReg(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG2x2.this.getVoiceCommandsByType()[4])) {
            BattleGvG2x2.this.story(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG2x2.this.getVoiceCommandsByType()[5])) {
            BattleGvG2x2.this.expec(player);
            return true;
         } else if (command.startsWith(BattleGvG2x2.this.getVoiceCommandsByType()[6])) {
            BattleGvG2x2.this.watch(player, args.split(" "));
            return true;
         } else if (command.startsWith(BattleGvG2x2.this.getVoiceCommandsByType()[7])) {
            BattleGvG2x2.this.addGroup(player, args.split(" "));
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return BattleGvG2x2.this._voiceCommandList;
      }
   }
}
