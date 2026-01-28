package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.EnchantSkillHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.base.Experience;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExEnchantSkillList;
import l2.gameserver.network.l2.s2c.ShortCutRegister;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.SkillEnchant;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestExEnchantSkill extends L2GameClientPacket {
   private static final Logger LOG = LoggerFactory.getLogger(RequestExEnchantSkill.class);
   private int _skillId;
   private int _skillLvl;

   protected void readImpl() {
      this._skillId = this.readD();
      this._skillLvl = this.readD();
   }

   protected void runImpl() {
      Player player = ((GameClient)this.getClient()).getActiveChar();
      if (player != null) {
         if (player.getClassId().getLevel() >= 4 && player.getLevel() >= 76) {
            Skill currSkill = player.getKnownSkill(this._skillId);
            if (currSkill == null) {
               player.sendPacket(new SystemMessage(SystemMsg.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT));
            } else {
               int currSkillLevel = currSkill.getLevel();
               int currSkillBaseLevel = currSkill.getBaseLevel();
               Map<Integer, SkillEnchant> skillEnchLevels = EnchantSkillHolder.getInstance().getLevelsOf(this._skillId);
               if (skillEnchLevels != null && !skillEnchLevels.isEmpty()) {
                  SkillEnchant currSkillEnch = (SkillEnchant)skillEnchLevels.get(currSkillLevel);
                  SkillEnchant newSkillEnch = (SkillEnchant)skillEnchLevels.get(this._skillLvl);
                  if (newSkillEnch == null) {
                     player.sendPacket(new SystemMessage(SystemMsg.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT));
                  } else {
                     if (currSkillEnch != null) {
                        if (currSkillEnch.getRouteId() != newSkillEnch.getRouteId() || newSkillEnch.getEnchantLevel() != currSkillEnch.getEnchantLevel() + 1) {
                           player.sendPacket(new SystemMessage(SystemMsg.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT));
                           return;
                        }
                     } else if (newSkillEnch.getEnchantLevel() != 1 || currSkillLevel != currSkillBaseLevel) {
                        player.sendPacket(new SystemMessage(SystemMsg.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT));
                        LOG.warn("Player \"" + player.toString() + "\" trying to use enchant  exploit" + currSkill.toString() + " to " + this._skillLvl + "(enchant level " + newSkillEnch.getEnchantLevel() + ")");
                        return;
                     }

                     int[] chances = newSkillEnch.getChances();
                     int minPlayerLevel = Experience.LEVEL.length - chances.length - 1;
                     if (player.getLevel() < minPlayerLevel) {
                        player.sendPacket((new SystemMessage(SystemMsg.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN__COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1)).addNumber(minPlayerLevel));
                     } else if (player.getSp() < (long)newSkillEnch.getSp()) {
                        player.sendPacket(new SystemMessage(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL));
                     } else if (player.getExp() < newSkillEnch.getExp()) {
                        player.sendPacket(new SystemMessage(SystemMsg.EXP_REQUIRED_FOR_SKILL_ENCHANT_IS_INSUFFICIENT));
                     } else if (newSkillEnch.getItemId() > 0 && newSkillEnch.getItemCount() > 0L && Functions.removeItem(player, newSkillEnch.getItemId(), newSkillEnch.getItemCount()) < newSkillEnch.getItemCount()) {
                        player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
                     } else {
                        int chanceIdx = Math.max(0, Math.min(player.getLevel() - minPlayerLevel, chances.length - 1));
                        int chance = (int)Math.min(100L, Math.round((double)chances[chanceIdx] * player.getEnchantSkillBonusMul()));
                        player.addExpAndSp(-1L * newSkillEnch.getExp(), (long)(-1 * newSkillEnch.getSp()));
                        TimeStamp currSkillReuseTimeStamp = player.getSkillReuse(currSkill);
                        Skill newSkill = null;
                        if (Rnd.chance(chance)) {
                           newSkill = SkillTable.getInstance().getInfo(newSkillEnch.getSkillId(), newSkillEnch.getSkillLevel());
                           player.sendPacket((new SystemMessage(SystemMsg.SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED)).addSkillName(this._skillId, this._skillLvl));
                           player.getListeners().onEnchantSkill(newSkill, true);
                           Log.add(player.getName() + "|Successfully enchanted|" + this._skillId + "|to+" + this._skillLvl + "|" + chance, "enchant_skills");
                        } else {
                           newSkill = SkillTable.getInstance().getInfo(currSkill.getId(), currSkill.getBaseLevel());
                           player.sendPacket((new SystemMessage(SystemMsg.SKILL_ENCHANT_FAILED)).addSkillName(this._skillId, this._skillLvl));
                           player.getListeners().onEnchantSkill(newSkill, false);
                           Log.add(player.getName() + "|Failed to enchant|" + this._skillId + "|to+" + this._skillLvl + "|" + chance, "enchant_skills");
                        }

                        if (currSkillReuseTimeStamp != null && currSkillReuseTimeStamp.hasNotPassed()) {
                           player.disableSkill(newSkill, currSkillReuseTimeStamp.getReuseCurrent());
                        }

                        player.addSkill(newSkill, true);
                        player.sendPacket(new SkillList(player));
                        updateSkillShortcuts(player, this._skillId, this._skillLvl);
                        player.sendPacket(ExEnchantSkillList.packetFor(player));
                     }
                  }
               } else {
                  player.sendPacket(new SystemMessage(SystemMsg.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT));
               }
            }
         } else {
            player.sendPacket(new SystemMessage(SystemMsg.THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT));
         }
      }
   }

   protected static void updateSkillShortcuts(Player player, int skillId, int skillLevel) {
      Iterator var3 = player.getAllShortCuts().iterator();

      while(var3.hasNext()) {
         ShortCut sc = (ShortCut)var3.next();
         if (sc.getId() == skillId && sc.getType() == 2) {
            ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skillLevel, 1);
            player.sendPacket(new ShortCutRegister(player, newsc));
            player.registerShortCut(newsc);
         }
      }

   }
}
