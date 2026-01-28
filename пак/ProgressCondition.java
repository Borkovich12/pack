package services.community.custom.progress;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.AggroList.AggroInfo;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;

public abstract class ProgressCondition {
   public static ProgressCondition makeCond(String condName, String condValue) {
      try {
         Class[] var2 = ProgressCondition.class.getClasses();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Class<?> clazz = var2[var4];
            if (ProgressCondition.class.isAssignableFrom(clazz)) {
               ProgressCondition.AchievementConditionName conditionName = (ProgressCondition.AchievementConditionName)clazz.getAnnotation(ProgressCondition.AchievementConditionName.class);
               if (conditionName != null && condName.equalsIgnoreCase(conditionName.value())) {
                  Constructor<? extends ProgressCondition> ctor = clazz.getConstructor(String.class);
                  if (ctor != null) {
                     return (ProgressCondition)ctor.newInstance(condValue);
                  }
               }
            }
         }

         return null;
      } catch (Exception var9) {
         throw new RuntimeException("Can't make condition " + condName + "(" + condValue + ")", var9);
      }
   }

   public abstract boolean test(Player var1, Object... var2);

   @ProgressCondition.AchievementConditionName("self_is_sub_count")
   public static class AchSelfIsSubCount extends ProgressCondition {
      private final int _subCount;

      public AchSelfIsSubCount(String value) {
         this._subCount = Integer.parseInt(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         return selfPlayer.getSubClasses().size() >= this._subCount;
      }
   }

   @ProgressCondition.AchievementConditionName("self_is_skill_enchant")
   public static class AchSelfIsSkillEnchant extends ProgressCondition {
      private final int _skillEnchant;

      public AchSelfIsSkillEnchant(String value) {
         this._skillEnchant = Integer.parseInt(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         Iterator var3 = selfPlayer.getAllSkills().iterator();

         while(var3.hasNext()) {
            Skill s = (Skill)var3.next();
            String lvl = String.valueOf(s.getLevel());
            if (lvl.length() > 2) {
               int sklvl = Integer.parseInt(lvl.substring(1));
               if (sklvl >= this._skillEnchant) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("min_clan_member_count")
   public static class AchMinClanMemberCount extends ProgressCondition {
      private final int _memberCount;

      public AchMinClanMemberCount(String value) {
         this._memberCount = Integer.parseInt(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         if (selfPlayer.getClan() != null) {
            return selfPlayer.getClan().getAllMembers().size() >= this._memberCount;
         } else {
            return false;
         }
      }
   }

   @ProgressCondition.AchievementConditionName("self_is_married")
   public static class AchSelfIsMarried extends ProgressCondition {
      private final boolean _isMarried;

      public AchSelfIsMarried(String value) {
         this._isMarried = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         return selfPlayer.isMaried() == this._isMarried;
      }
   }

   @ProgressCondition.AchievementConditionName("self_is_item_count")
   public static class AchSelfIsItemCount extends ProgressCondition {
      private final int _itemId;
      private final int _itemCount;

      public AchSelfIsItemCount(String value) {
         int delimIdx = value.indexOf(44);
         this._itemId = Integer.parseInt(value.substring(0, delimIdx).trim());
         this._itemCount = Integer.parseInt(value.substring(delimIdx + 1).trim());
      }

      public boolean test(Player selfPlayer, Object... args) {
         try {
            if (selfPlayer.getInventory().getCountOf(this._itemId) >= (long)this._itemCount) {
               return true;
            }
         } catch (NumberFormatException var4) {
            var4.printStackTrace();
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("self_clan_rep")
   public static class AchSelfClanRep extends ProgressCondition {
      private final int _clanRep;

      public AchSelfClanRep(String value) {
         this._clanRep = Integer.parseInt(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         if (selfPlayer.getClan() != null) {
            return selfPlayer.getClan().getReputationScore() >= this._clanRep;
         } else {
            return false;
         }
      }
   }

   @ProgressCondition.AchievementConditionName("self_clan_level")
   public static class AchSelfClanLevel extends ProgressCondition {
      private final int _clanLevel;

      public AchSelfClanLevel(String value) {
         this._clanLevel = Integer.parseInt(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         if (selfPlayer.getClan() != null) {
            return selfPlayer.getClan().getLevel() >= this._clanLevel;
         } else {
            return false;
         }
      }
   }

   @ProgressCondition.AchievementConditionName("self_is_clan_siege")
   public static class AchSelfIsCastleSiege extends ProgressCondition {
      private final boolean _isCastleSiege;

      public AchSelfIsCastleSiege(String value) {
         this._isCastleSiege = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         if (selfPlayer.getClan() == null) {
            return false;
         } else {
            return selfPlayer.isCastleLord(5) == this._isCastleSiege || selfPlayer.isCastleLord(3) == this._isCastleSiege || selfPlayer.isCastleLord(7) == this._isCastleSiege;
         }
      }
   }

   @ProgressCondition.AchievementConditionName("count_adena")
   public static class AchCountAdena extends ProgressCondition {
      private final int _countAdena;

      public AchCountAdena(String value) {
         this._countAdena = Integer.parseInt(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         return selfPlayer.getInventory().getAdena() >= (long)this._countAdena;
      }
   }

   @ProgressCondition.AchievementConditionName("self_quest_state_is")
   public static class AchSelfQuestStateIs extends ProgressCondition {
      private final int _questStateId;

      public AchSelfQuestStateIs(String value) {
         this._questStateId = Quest.getStateId(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null) {
               QuestState questState = null;
               if (obj instanceof QuestState) {
                  questState = (QuestState)obj;
               }

               if (obj instanceof Quest) {
                  questState = selfPlayer.getQuestState(((Quest)obj).getName());
               }

               if (questState != null) {
                  return questState.getState() == this._questStateId;
               }
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("self_quest_id_in")
   public static class AchSelfQuestId extends ProgressCondition {
      private final Set<Integer> _questIds = new HashSet();

      public AchSelfQuestId(String value) {
         StringTokenizer tok = new StringTokenizer(value, ";,");

         while(tok.hasMoreTokens()) {
            this._questIds.add(Integer.parseInt(tok.nextToken()));
         }

      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null) {
               Quest quest = null;
               if (obj instanceof Quest) {
                  quest = (Quest)obj;
               }

               if (obj instanceof QuestState) {
                  quest = ((QuestState)obj).getQuest();
               }

               if (quest != null) {
                  return this._questIds.contains(quest.getQuestIntId());
               }
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("target_npc_min_damage_to_me")
   public static class AchTargetAggroMinDamageToMe extends ProgressCondition {
      private final int _minDamage;

      public AchTargetAggroMinDamageToMe(String value) {
         this._minDamage = Integer.parseInt(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null && obj instanceof NpcInstance) {
               NpcInstance targetNpc = (NpcInstance)obj;
               AggroInfo aggroInfo = targetNpc.getAggroList().get(selfPlayer);
               if (aggroInfo == null) {
                  return false;
               }

               return aggroInfo.damage >= this._minDamage;
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("target_npc_min_hate_to_me")
   public static class AchTargetAggroMinHateToMe extends ProgressCondition {
      private final int _minHate;

      public AchTargetAggroMinHateToMe(String value) {
         this._minHate = Integer.parseInt(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null && obj instanceof NpcInstance) {
               NpcInstance targetNpc = (NpcInstance)obj;
               AggroInfo aggroInfo = targetNpc.getAggroList().get(selfPlayer);
               if (aggroInfo == null) {
                  return false;
               }

               return aggroInfo.hate >= this._minHate;
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("self_target_max_lvl_diff")
   public static class AchSelfTargetMaxLvlDiff extends ProgressCondition {
      private final int _maxLvlDiff;

      public AchSelfTargetMaxLvlDiff(String value) {
         this._maxLvlDiff = Integer.parseInt(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null && obj instanceof Creature) {
               Creature targetCreature = (Creature)obj;
               return Math.abs(targetCreature.getLevel() - selfPlayer.getLevel()) <= this._maxLvlDiff;
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("self_is_hero")
   public static class AchSelfIsHero extends ProgressCondition {
      private final boolean _isHero;

      public AchSelfIsHero(String value) {
         this._isHero = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         return selfPlayer.isHero() == this._isHero;
      }
   }

   @ProgressCondition.AchievementConditionName("self_is_noble")
   public static class AchSelfIsNoble extends ProgressCondition {
      private final boolean _isNoble;

      public AchSelfIsNoble(String value) {
         this._isNoble = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         return selfPlayer.isNoble() == this._isNoble;
      }
   }

   @ProgressCondition.AchievementConditionName("self_level_in_range")
   public static class AchSelfLevelInRange extends ProgressCondition {
      private final int _minLevel;
      private final int _maxLevel;

      public AchSelfLevelInRange(String value) {
         int delimIdx = value.indexOf(45);
         this._minLevel = Integer.parseInt(value.substring(0, delimIdx).trim());
         this._maxLevel = Integer.parseInt(value.substring(delimIdx + 1).trim());
      }

      public boolean test(Player selfPlayer, Object... args) {
         return selfPlayer.getLevel() >= this._minLevel && selfPlayer.getLevel() < this._maxLevel;
      }
   }

   @ProgressCondition.AchievementConditionName("diff_target_level")
   public static class AchDiffTargetLevel extends ProgressCondition {
      private final int _minLevel;
      private final int _maxLevel;

      public AchDiffTargetLevel(String value) {
         int delimIdx = value.indexOf(45);
         this._minLevel = Integer.parseInt(value.substring(0, delimIdx).trim());
         this._maxLevel = Integer.parseInt(value.substring(delimIdx + 1).trim());
      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null && obj instanceof NpcInstance) {
               NpcInstance npc = (NpcInstance)obj;
               return npc.getLevel() >= this._minLevel && npc.getLevel() <= this._maxLevel;
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("self_is_clan_leader")
   public static class AchSelfIsClanLeader extends ProgressCondition {
      private final boolean _isClanleader;

      public AchSelfIsClanLeader(String value) {
         this._isClanleader = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         return selfPlayer.isClanLeader() == this._isClanleader;
      }
   }

   @ProgressCondition.AchievementConditionName("self_is_subclass_active")
   public static class AchSelfIsSubclassActive extends ProgressCondition {
      private final boolean _isSubclassActive;

      public AchSelfIsSubclassActive(String value) {
         this._isSubclassActive = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         return selfPlayer.isSubClassActive() == this._isSubclassActive;
      }
   }

   @ProgressCondition.AchievementConditionName("is_target_player_class_id_in")
   public static class AchTargetPlayerIsActiveClass extends ProgressCondition {
      private final Set<Integer> _classIds = new HashSet();

      public AchTargetPlayerIsActiveClass(String value) {
         StringTokenizer st = new StringTokenizer(value, ";,");

         while(st.hasMoreTokens()) {
            this._classIds.add(Integer.parseInt(st.nextToken()));
         }

      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null && obj instanceof Player) {
               Player targetPlayer = (Player)obj;
               return this._classIds.contains(targetPlayer.getClassId().getId());
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("self_is_class_id_in")
   public static class AchSelfIsActiveClass extends ProgressCondition {
      private final Set<Integer> _classIds = new HashSet();

      public AchSelfIsActiveClass(String value) {
         StringTokenizer st = new StringTokenizer(value, ";,");

         while(st.hasMoreTokens()) {
            this._classIds.add(Integer.parseInt(st.nextToken()));
         }

      }

      public boolean test(Player selfPlayer, Object... args) {
         return this._classIds.contains(selfPlayer.getClassId().getId());
      }
   }

   @ProgressCondition.AchievementConditionName("is_oly_winner")
   public static class AchIsOlyWinner extends ProgressCondition {
      private final boolean _isWinner;

      public AchIsOlyWinner(String value) {
         this._isWinner = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         boolean haveComp = false;
         Object[] var4 = args;
         int var5 = args.length;

         int var6;
         Object obj;
         for(var6 = 0; var6 < var5; ++var6) {
            obj = var4[var6];
            if (obj instanceof Competition) {
               haveComp = true;
            }
         }

         if (haveComp) {
            var4 = args;
            var5 = args.length;

            for(var6 = 0; var6 < var5; ++var6) {
               obj = var4[var6];
               if (obj instanceof Boolean) {
                  return this._isWinner == (Boolean)obj;
               }
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("now_match_cron")
   public static class AchievementConditionIsNowMatchCron extends ProgressCondition {
      private final SchedulingPattern _pattern;

      public AchievementConditionIsNowMatchCron(String pattern) {
         this._pattern = new SchedulingPattern(pattern);
      }

      public boolean test(Player selfPlayer, Object... args) {
         return this._pattern.match(System.currentTimeMillis());
      }
   }

   @ProgressCondition.AchievementConditionName("npc_id_in_list")
   public static class AchievementConditionNpcIdInList extends ProgressCondition {
      private Set<Integer> _npcIds = new HashSet();

      public AchievementConditionNpcIdInList(String value) {
         StringTokenizer tok = new StringTokenizer(value, ";,");

         while(tok.hasMoreTokens()) {
            this._npcIds.add(Integer.parseInt(tok.nextToken()));
         }

      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null && obj instanceof NpcInstance) {
               NpcInstance npc = (NpcInstance)obj;
               if (this._npcIds.contains(npc.getNpcId())) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("is_karma_player")
   public static class AchievementConditionIsKarmaPlayer extends ProgressCondition {
      private final boolean _value;

      public AchievementConditionIsKarmaPlayer(String value) {
         this._value = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null) {
               if (this._value) {
                  if (obj instanceof Player && ((Player)obj).getKarma() > 0) {
                     return true;
                  }
               } else if (obj instanceof Player && ((Player)obj).getKarma() == 0) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("is_blue_champion")
   public static class AchievementConditionHaveChampion extends ProgressCondition {
      private final boolean _blue;

      public AchievementConditionHaveChampion(String value) {
         this._blue = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null && obj instanceof MonsterInstance) {
               MonsterInstance monster = (MonsterInstance)obj;
               if (monster.getChampion() == 1 && this._blue) {
                  return true;
               }

               if (monster.getChampion() == 2 && !this._blue) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @ProgressCondition.AchievementConditionName("is_raid_boss")
   public static class AchievementConditionHaveRaid extends ProgressCondition {
      private final boolean _value;

      public AchievementConditionHaveRaid(String value) {
         this._value = Boolean.parseBoolean(value);
      }

      public boolean test(Player selfPlayer, Object... args) {
         Object[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            if (obj != null) {
               if (this._value) {
                  if (obj instanceof RaidBossInstance) {
                     return true;
                  }
               } else if (!(obj instanceof RaidBossInstance)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Target({ElementType.TYPE})
   @Retention(RetentionPolicy.RUNTIME)
   public @interface AchievementConditionName {
      String value();
   }
}
