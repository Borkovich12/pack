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

public class BattleGvGCxC extends BattleGvG {
   private static final Logger _log = LoggerFactory.getLogger(BattleGvGCxC.class);
   private static final BattleGvGCxC _instance = new BattleGvGCxC();
   private final String[] _voiceCommandList = new String[]{"gvgc", "gcshowteams", "gcremovegroup", "gcshowreg", "gcstory", "gcexpec", "gcwatch", "gcaddgroup"};

   public static final BattleGvGCxC getInstance() {
      return _instance;
   }

   public void load() {
      AdminCommandHandler.getInstance().registerAdminCommandHandler(new BattleGvGCxC.BattleGvgAdm());
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new BattleGvGCxC.BattleGvgVoice());
      this.onLoad(BattleType.BCXC);
   }

   public String[] getVoiceCommandsByType() {
      return this._voiceCommandList;
   }

   public boolean isCCType() {
      return true;
   }

   public int getCCMax() {
      return 2;
   }

   public class BattleGvgAdm implements IAdminCommandHandler {
      public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
         if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
         } else {
            BattleGvGCxC.Commands command = (BattleGvGCxC.Commands)comm;
            switch(command) {
            case admin_gvgc_start:
               activeChar.sendMessage("Try to start GvG " + BattleGvGCxC.this.getType().getNameType() + " registration and stop global task.");
               BattleGvGCxC.this.now(activeChar);
               break;
            case admin_gvgc_stop:
               activeChar.sendMessage("Try to stop GvG " + BattleGvGCxC.this.getType().getNameType() + " event.");
               BattleGvGCxC.this.stop(activeChar);
               break;
            case admin_gvgc_auto:
               activeChar.sendMessage("Try to start GvG " + BattleGvGCxC.this.getType().getNameType() + " global task.");
               BattleGvGCxC.this.auto(activeChar);
               break;
            case admin_gvgc_reg:
               if (wordList.length > 2) {
                  String name = wordList[1];
                  String team = wordList[2];
                  Player partic = GameObjectsStorage.getPlayer(name);
                  if (partic == null) {
                     activeChar.sendMessage("Player " + name + " not found in game.");
                     return false;
                  }

                  BattleGvGCxC.this.addReg(activeChar, partic, team);
               } else {
                  activeChar.sendMessage("Usage: //gvg_regc <nick> <team_name>");
               }
               break;
            case admin_gvgc_fugas:
               activeChar.sendMessage("Try fugas all players on GvG " + BattleGvGCxC.this.getType().getNameType() + " arena.");
               BattleGvGCxC.this.fugas(activeChar);
            }

            return true;
         }
      }

      public Enum[] getAdminCommandEnum() {
         return BattleGvGCxC.Commands.values();
      }
   }

   private static enum Commands {
      admin_gvgc_start,
      admin_gvgc_stop,
      admin_gvgc_auto,
      admin_gvgc_reg,
      admin_gvgc_fugas;
   }

   public class BattleGvgVoice implements IVoicedCommandHandler {
      public boolean useVoicedCommand(String command, Player player, String args) {
         if (command.equalsIgnoreCase(BattleGvGCxC.this.getVoiceCommandsByType()[0])) {
            BattleGvGCxC.this.status(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvGCxC.this.getVoiceCommandsByType()[1])) {
            BattleGvGCxC.this.showTeams(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvGCxC.this.getVoiceCommandsByType()[2])) {
            BattleGvGCxC.this.removeGroup(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvGCxC.this.getVoiceCommandsByType()[3])) {
            BattleGvGCxC.this.showReg(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvGCxC.this.getVoiceCommandsByType()[4])) {
            BattleGvGCxC.this.story(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvGCxC.this.getVoiceCommandsByType()[5])) {
            BattleGvGCxC.this.expec(player);
            return true;
         } else if (command.startsWith(BattleGvGCxC.this.getVoiceCommandsByType()[6])) {
            BattleGvGCxC.this.watch(player, args.split(" "));
            return true;
         } else if (command.startsWith(BattleGvGCxC.this.getVoiceCommandsByType()[7])) {
            BattleGvGCxC.this.addGroup(player, args.split(" "));
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return BattleGvGCxC.this._voiceCommandList;
      }
   }
}
