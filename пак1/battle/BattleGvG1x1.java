package events.battle;

import events.battle.enums.BattleType;
import events.battle.model.BattleConditions;
import events.battle.model.BattleGrp;
import events.battle.util.BattleUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleGvG1x1 extends BattleGvG {
   private static final Logger _log = LoggerFactory.getLogger(BattleGvG1x1.class);
   private static final BattleGvG1x1 _instance = new BattleGvG1x1();
   private final String[] _voiceCommandList = new String[]{"gvg1", "g1showteams", "g1removegroup", "g1showreg", "g1story", "g1expec", "g1watch", "g1addgroup"};

   public static final BattleGvG1x1 getInstance() {
      return _instance;
   }

   public void load() {
      AdminCommandHandler.getInstance().registerAdminCommandHandler(new BattleGvG1x1.BattleGvgAdm());
      VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new BattleGvG1x1.BattleGvgVoice());
      this.onLoad(BattleType.B1X1);
   }

   public void addGroup(Player player, String[] param) {
      if (param.length >= 1) {
         if (player != null) {
            if (BattleConditions.preCheckSingleAddGroup(player, param[0], this)) {
               Map<String, Pair<String, String>> errorMessages = BattleConditions.getSingleGroupCheckError();
               String rsn = BattleConditions.validatePlayerForBattle(this, player, false, true);
               String msgRu;
               String msgEn;
               if (rsn != null) {
                  Pair<String, String> messages = (Pair)errorMessages.get(rsn);
                  if (messages != null) {
                     msgRu = (String)messages.getLeft();
                     msgEn = (String)messages.getRight();
                     player.sendMessage(player.isLangRus() ? msgRu : msgEn);
                  }

                  player.setReg(false);
                  player.setInGvG(0);
                  if (this.getType().isRestrictIp()) {
                     this.getRestrictIp().remove(player.getIP());
                  }

                  if (this.getType().isRestrictHwid()) {
                     GameClient playerClient = player.getNetConnection();
                     if (playerClient != null) {
                        this.getRestrictHwid().remove(playerClient.getHwid());
                     }
                  }

               } else {
                  player.setReg(true);
                  if (this.getType().isRestrictIp()) {
                     this.getRestrictIp().add(player.getIP());
                  }

                  if (this.getType().isRestrictHwid()) {
                     GameClient playerClient = player.getNetConnection();
                     if (playerClient != null) {
                        this.getRestrictHwid().add(playerClient.getHwid());
                     }
                  }

                  List<HardReference<Player>> cms = new CopyOnWriteArrayList();
                  cms.add(player.getRef());
                  this.getCommands().put(player.getObjectId(), cms);
                  if (this.getType().isToArena()) {
                     this.getPointByCommand().put(player.getObjectId(), this.getPoints().remove(0));
                  }

                  this.getCommandNames().put(player.getObjectId(), param[0]);
                  this.getLeaderList().add(player.getRef());
                  player.addListener(this._exitListener);
                  msgRu = "Вы внесены в список ожидания GvG " + this.getType().getNameType() + " турнира. Пожалуйста, не регистрируйтесь в других ивентах и не участвуйте в дуэлях.";
                  msgEn = "You have been placed on the GvG " + this.getType().getNameType() + " tournament waitlist. Please do not register in other events and do not participate in duels.";
                  Iterator var8 = HardReferences.unwrap((Collection)this.getCommands().get(player.getObjectId())).iterator();

                  while(var8.hasNext()) {
                     Player member = (Player)var8.next();
                     member.sendMessage(member.isLangRus() ? msgRu : msgEn);
                  }

               }
            }
         }
      }
   }

   protected void checkPlayers() {
      List<HardReference<Player>> toRemove = new ArrayList();
      Map<String, Pair<String, String>> errorMessages = BattleConditions.getSingleCheckPlayerError();
      Iterator var3 = HardReferences.unwrap(this.getLeaderList()).iterator();

      while(var3.hasNext()) {
         Player player = (Player)var3.next();
         String rsn = BattleConditions.validatePlayerForBattle(this, player, true, true);
         if (rsn != null) {
            Pair<String, String> messages = (Pair)errorMessages.get(rsn);
            if (messages != null) {
               String messageRu = (String)messages.getLeft();
               String messageEn = (String)messages.getRight();
               player.sendMessage(player.isLangRus() ? messageRu : messageEn);
            }

            toRemove.add(player.getRef());
         } else if (!this.validateParticipants(player, true)) {
            toRemove.add(player.getRef());
         }
      }

      if (!toRemove.isEmpty()) {
         this.processRemovedPlayers(toRemove);
      }

   }

   protected boolean validateParticipants(Player player, boolean me) {
      if (player != null) {
         if (!BattleConditions.preCheckSingleValidate(player, this)) {
            return false;
         }

         Map<String, Pair<String, String>> errorMessages = BattleConditions.getSingleCheckDError();
         String rsn = BattleConditions.validatePlayerForBattle(this, player, false, true);
         if (rsn != null) {
            if (me) {
               List<HardReference<Player>> list = (List)this.getCommands().get(player.getObjectId());
               if (list != null) {
                  Pair<String, String> messages = (Pair)errorMessages.get(rsn);
                  if (messages != null) {
                     String messageRu = (String)messages.getLeft();
                     String messageEn = (String)messages.getRight();
                     Iterator var9 = HardReferences.unwrap(list).iterator();

                     while(var9.hasNext()) {
                        Player member = (Player)var9.next();
                        member.sendMessage(member.isLangRus() ? messageRu : messageEn);
                     }
                  }
               }
            }

            return false;
         }
      }

      return true;
   }

   public void addReg(Player admin, Player leader, String team) {
      if (BattleConditions.preCheckSingleAddReg(admin, leader, team, this)) {
         List<HardReference<Player>> cms = new CopyOnWriteArrayList();
         Map<String, Pair<String, String>> errorMessages = BattleConditions.getSingeRegError();
         String msgRu;
         if (leader != null) {
            msgRu = BattleConditions.validatePlayerForBattle(this, leader, false, true);
            if (msgRu != null) {
               Pair<String, String> messages = (Pair)errorMessages.get(msgRu);
               if (messages != null) {
                  String messageRu = String.format((String)messages.getLeft(), leader.getName());
                  String messageEn = String.format((String)messages.getRight(), leader.getName());
                  admin.sendMessage(admin.isLangRus() ? messageRu : messageEn);
               }

               leader.setReg(false);
               leader.setInGvG(0);
               if (this.getType().isRestrictIp()) {
                  this.getRestrictIp().remove(leader.getIP());
               }

               if (this.getType().isRestrictHwid()) {
                  GameClient leaderClient = leader.getNetConnection();
                  if (leaderClient != null) {
                     this.getRestrictHwid().remove(leaderClient.getHwid());
                  }
               }

               return;
            }

            leader.setReg(true);
            if (this.getType().isRestrictIp()) {
               this.getRestrictIp().add(leader.getIP());
            }

            if (this.getType().isRestrictHwid()) {
               GameClient leaderClient = leader.getNetConnection();
               if (leaderClient != null) {
                  this.getRestrictHwid().add(leaderClient.getHwid());
               }
            }

            cms.add(leader.getRef());
         }

         this.getCommands().put(leader.getObjectId(), cms);
         if (this.getType().isToArena()) {
            this.getPointByCommand().put(leader.getObjectId(), this.getPoints().remove(0));
         }

         this.getCommandNames().put(leader.getObjectId(), team);
         msgRu = "Гейммастер " + admin.getName() + " внёс Вас в список ожидания GvG " + this.getType().getNameType() + " турнира. Пожалуйста, не регистрируйтесь в других ивентах и не участвуйте в дуэлях.";
         String msgEn = "Gamemaster " + admin.getName() + " has put you on the waiting list for a GvG " + this.getType().getNameType() + " tournament. Please do not register in other events and do not participate in duels.";
         Iterator var8;
         Player member;
         if (this.isRegistrationActive()) {
            this.getLeaderList().add(leader.getRef());
            leader.addListener(this._exitListener);
            var8 = HardReferences.unwrap((Collection)this.getCommands().get(leader.getObjectId())).iterator();

            while(var8.hasNext()) {
               member = (Player)var8.next();
               member.sendMessage(member.isLangRus() ? msgRu : msgEn);
            }
         } else {
            var8 = HardReferences.unwrap((Collection)this.getCommands().get(leader.getObjectId())).iterator();

            while(var8.hasNext()) {
               member = (Player)var8.next();
               member.sendMessage(member.isLangRus() ? msgRu : msgEn);
               if (this.getType().isToArena()) {
                  Location point = (Location)this.getPointByCommand().get(leader.getObjectId());
                  if (point != null) {
                     BattleUtil.onArena(member, this.getReflection(), point);
                  } else {
                     _log.warn("BattleGvG1x1: For the member " + member.getName() + " with leader objId " + leader.getObjectId() + ", it was not possible to find a position for teleportation to the arena when registering a team.");
                  }
               }
            }

            this.getFirstList().add(new BattleGrp(leader, (String)this.getCommandNames().get(leader.getObjectId()), (List)this.getCommands().get(leader.getObjectId())));
         }

         if (admin.isLangRus()) {
            admin.sendMessage("Команда " + team + " добавлена в список участников GvG " + this.getType().getNameType() + " турнира.");
         } else {
            admin.sendMessage("The " + team + " team has been added to the list of participants in the GvG " + this.getType().getNameType() + " tournament.");
         }

      }
   }

   public String[] getVoiceCommandsByType() {
      return this._voiceCommandList;
   }

   public class BattleGvgAdm implements IAdminCommandHandler {
      public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
         if (!activeChar.getPlayerAccess().IsEventGm) {
            return false;
         } else {
            BattleGvG1x1.Commands command = (BattleGvG1x1.Commands)comm;
            switch(command) {
            case admin_gvg1_start:
               activeChar.sendMessage("Try to start GvG " + BattleGvG1x1.this.getType().getNameType() + " registration and stop global task.");
               BattleGvG1x1.this.now(activeChar);
               break;
            case admin_gvg1_stop:
               activeChar.sendMessage("Try to stop GvG " + BattleGvG1x1.this.getType().getNameType() + " event.");
               BattleGvG1x1.this.stop(activeChar);
               break;
            case admin_gvg1_auto:
               activeChar.sendMessage("Try to start GvG " + BattleGvG1x1.this.getType().getNameType() + " global task.");
               BattleGvG1x1.this.auto(activeChar);
               break;
            case admin_gvg1_reg:
               if (wordList.length > 2) {
                  String name = wordList[1];
                  String team = wordList[2];
                  Player partic = GameObjectsStorage.getPlayer(name);
                  if (partic == null) {
                     activeChar.sendMessage("Player " + name + " not found in game.");
                     return false;
                  }

                  BattleGvG1x1.this.addReg(activeChar, partic, team);
               } else {
                  activeChar.sendMessage("Usage: //gvg_reg1 <nick> <team_name>");
               }
               break;
            case admin_gvg1_fugas:
               activeChar.sendMessage("Try fugas all players on GvG " + BattleGvG1x1.this.getType().getNameType() + " arena.");
               BattleGvG1x1.this.fugas(activeChar);
            }

            return true;
         }
      }

      public Enum[] getAdminCommandEnum() {
         return BattleGvG1x1.Commands.values();
      }
   }

   private static enum Commands {
      admin_gvg1_start,
      admin_gvg1_stop,
      admin_gvg1_auto,
      admin_gvg1_reg,
      admin_gvg1_fugas;
   }

   public class BattleGvgVoice implements IVoicedCommandHandler {
      public boolean useVoicedCommand(String command, Player player, String args) {
         if (command.equalsIgnoreCase(BattleGvG1x1.this.getVoiceCommandsByType()[0])) {
            BattleGvG1x1.this.status(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG1x1.this.getVoiceCommandsByType()[1])) {
            BattleGvG1x1.this.showTeams(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG1x1.this.getVoiceCommandsByType()[2])) {
            BattleGvG1x1.this.removeGroup(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG1x1.this.getVoiceCommandsByType()[3])) {
            BattleGvG1x1.this.showReg(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG1x1.this.getVoiceCommandsByType()[4])) {
            BattleGvG1x1.this.story(player);
            return true;
         } else if (command.equalsIgnoreCase(BattleGvG1x1.this.getVoiceCommandsByType()[5])) {
            BattleGvG1x1.this.expec(player);
            return true;
         } else if (command.startsWith(BattleGvG1x1.this.getVoiceCommandsByType()[6])) {
            BattleGvG1x1.this.watch(player, args.split(" "));
            return true;
         } else if (command.startsWith(BattleGvG1x1.this.getVoiceCommandsByType()[7])) {
            BattleGvG1x1.this.addGroup(player, args.split(" "));
            return true;
         } else {
            return false;
         }
      }

      public String[] getVoicedCommandList() {
         return BattleGvG1x1.this._voiceCommandList;
      }
   }
}
