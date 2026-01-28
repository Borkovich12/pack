package l2.gameserver.skills.skillclasses;

import java.util.Iterator;
import java.util.List;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Skill.SkillTargetType;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.BaseStats;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;
import org.apache.commons.lang3.tuple.Pair;

public class Resurrect extends Skill {
   private final boolean _canPet;
   private final int _expireResurrectTime;

   public Resurrect(StatsSet set) {
      super(set);
      this._canPet = set.getBool("canPet", false);
      this._expireResurrectTime = set.getInteger("expireResurrectTime", 0);
   }

   private boolean siegeCheck(Player player, Creature target, boolean forceUse) {
      boolean result = true;
      Iterator var5 = player.getEvents().iterator();

      while(var5.hasNext()) {
         GlobalEvent e = (GlobalEvent)var5.next();
         if (!e.canResurrect(player, target, forceUse)) {
            result = false;
         }
      }

      if (result) {
         SiegeEvent playerEvent = (SiegeEvent)player.getEvent(SiegeEvent.class);
         SiegeEvent targetEvent = (SiegeEvent)target.getEvent(SiegeEvent.class);
         boolean playerInZone = player.isInZone(ZoneType.SIEGE);
         boolean targetInZone = target.isInZone(ZoneType.SIEGE);
         if (playerEvent == null && playerInZone || targetEvent == null && targetInZone) {
            result = false;
         }
      }

      if (!result) {
         player.sendPacket((new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addSkillName(this));
         return false;
      } else {
         return true;
      }
   }

   public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
      if (!activeChar.isPlayer()) {
         return false;
      } else if (target != null && (target == activeChar || target.isDead())) {
         Player player = (Player)activeChar;
         Player pcTarget = target.getPlayer();
         if (pcTarget == null) {
            player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
         } else if (!player.isOlyParticipant() && !pcTarget.isOlyParticipant()) {
            if (player.getTeam() == TeamType.NONE && pcTarget.getTeam() == TeamType.NONE) {
               if (!this.siegeCheck(player, target, forceUse)) {
                  return false;
               } else {
                  if (this.oneTarget()) {
                     Pair ask;
                     ReviveAnswerListener reviveAsk;
                     if (target.isPet()) {
                        ask = pcTarget.getAskListener(false);
                        reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener)ask.getValue() : null;
                        if (reviveAsk != null) {
                           if (reviveAsk.isForPet()) {
                              activeChar.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
                           } else {
                              activeChar.sendPacket(SystemMsg.A_PET_CANNOT_BE_RESURRECTED_WHILE_ITS_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING);
                           }

                           return false;
                        }

                        if (!this._canPet && this._targetType != SkillTargetType.TARGET_PET) {
                           player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                           return false;
                        }
                     } else if (target.isPlayer()) {
                        ask = pcTarget.getAskListener(false);
                        reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener)ask.getValue() : null;
                        if (reviveAsk != null) {
                           if (reviveAsk.isForPet()) {
                              activeChar.sendPacket(SystemMsg.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
                           } else {
                              activeChar.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
                           }

                           return false;
                        }

                        if (this._targetType == SkillTargetType.TARGET_PET) {
                           player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                           return false;
                        }

                        if (pcTarget.isFestivalParticipant()) {
                           player.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Resurrect", player, new Object[0]));
                           return false;
                        }
                     }
                  }

                  return super.checkCondition(activeChar, target, forceUse, dontMove, first);
               }
            } else {
               player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
               return false;
            }
         } else {
            player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
         }
      } else {
         activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
         return false;
      }
   }

   public void useSkill(Creature activeChar, List<Creature> targets) {
      double percent = this._power;
      if (percent < 100.0D && !this.isHandler()) {
         double wit_bonus = this._power * (BaseStats.WIT.calcBonus(activeChar) - 1.0D);
         percent += wit_bonus > 20.0D ? 20.0D : wit_bonus;
         if (percent > 90.0D) {
            percent = 90.0D;
         }
      }

      Iterator var10 = targets.iterator();

      while(true) {
         Creature target;
         label92:
         while(true) {
            label73:
            while(true) {
               do {
                  do {
                     if (!var10.hasNext()) {
                        if (this.isSSPossible()) {
                           activeChar.unChargeShots(this.isMagic());
                        }

                        return;
                     }

                     target = (Creature)var10.next();
                  } while(target == null);
               } while(target.getPlayer() == null);

               Iterator var7 = target.getEvents().iterator();

               while(var7.hasNext()) {
                  GlobalEvent e = (GlobalEvent)var7.next();
                  if (!e.canResurrect((Player)activeChar, target, true)) {
                     continue label73;
                  }
               }

               if (target.isPet() && this._canPet) {
                  if (target.getPlayer() == activeChar) {
                     ((PetInstance)target).doRevive(percent);
                  } else {
                     target.getPlayer().reviveRequest((Player)activeChar, percent, true, this._expireResurrectTime);
                  }
                  break label92;
               }

               if (target.isPlayer() && this._targetType != SkillTargetType.TARGET_PET) {
                  Player targetPlayer = (Player)target;
                  Pair<Integer, OnAnswerListener> ask = targetPlayer.getAskListener(false);
                  ReviveAnswerListener reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener)ask.getValue() : null;
                  if (reviveAsk == null && !targetPlayer.isFestivalParticipant()) {
                     targetPlayer.reviveRequest((Player)activeChar, percent, false, this._expireResurrectTime);
                     break label92;
                  }
               }
            }
         }

         this.getEffects(activeChar, target, this.getActivateRate() > 0, false);
      }
   }
}
