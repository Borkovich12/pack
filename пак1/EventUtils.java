package events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.tables.SkillTable;
import org.apache.commons.lang3.ArrayUtils;

public class EventUtils {
   public static void giveBuff(Creature cha, Skill buff, long time, int i) {
      if (buff != null) {
         buff.getEffects(cha, cha, false, false, time, 1.0D, false);
         if (cha.getPet() != null) {
            buff.getEffects(cha.getPet(), cha.getPet(), false, false, time, 1.0D, false);
         }

      }
   }

   public static void stopEffects(Player player, int[] ids) {
      if (!player.getEffectList().isEmpty()) {
         Iterator var2 = player.getEffectList().getAllEffects().iterator();

         while(var2.hasNext()) {
            Effect e = (Effect)var2.next();
            if (ArrayUtils.contains(ids, e.getSkill().getId())) {
               e.exit();
            }
         }
      }

   }

   public static void healPlayer(Player player) {
      if (player != null) {
         if (player.isDead()) {
            player.restoreExp();
            player.setCurrentHp((double)player.getMaxHp(), true);
            player.setCurrentMp((double)player.getMaxMp());
            player.setCurrentCp((double)player.getMaxCp());
            player.broadcastPacket(new L2GameServerPacket[]{new Revive(player)});
         } else {
            if (player.isFakeDeath()) {
               player.breakFakeDeath();
            }

            player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp(), false);
            player.setCurrentCp((double)player.getMaxCp());
         }

      }
   }

   public static void activateHeroSkills(Player player) {
      if (player.isHero()) {
         Iterator var1 = SkillAcquireHolder.getInstance().getAllSkillLearn(player, player.getClassId(), AcquireType.HERO).iterator();

         while(var1.hasNext()) {
            SkillLearn skillLearn = (SkillLearn)var1.next();
            Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
            if (skill != null) {
               player.removeUnActiveSkill(skill);
            }
         }

      }
   }

   public static void unActivateHeroSkills(Player player) {
      if (player.isHero()) {
         Iterator var1 = SkillAcquireHolder.getInstance().getAllSkillLearn(player, player.getClassId(), AcquireType.HERO).iterator();

         while(var1.hasNext()) {
            SkillLearn skillLearn = (SkillLearn)var1.next();
            Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
            if (skill != null) {
               player.addUnActiveSkill(skill);
            }
         }

      }
   }

   public static List<Player> getPlayers(List<Long> list) {
      List<Player> result = new ArrayList();
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         Long id = (Long)var2.next();
         Player player = GameObjectsStorage.getAsPlayer(id);
         if (player != null) {
            result.add(player);
         }
      }

      return result;
   }

   public static List<Player> getPlayers(List<Long> list1, List<Long> list2) {
      List<Player> result = new ArrayList();
      Iterator var3 = list1.iterator();

      Long id;
      Player player;
      while(var3.hasNext()) {
         id = (Long)var3.next();
         player = GameObjectsStorage.getAsPlayer(id);
         if (player != null) {
            result.add(player);
         }
      }

      var3 = list2.iterator();

      while(var3.hasNext()) {
         id = (Long)var3.next();
         player = GameObjectsStorage.getAsPlayer(id);
         if (player != null) {
            result.add(player);
         }
      }

      return result;
   }

   public static List<Player> getSpectators(Set<Integer> list) {
      List<Player> result = new ArrayList();
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         int id = (Integer)var2.next();
         Player player = GameObjectsStorage.getPlayer(id);
         if (player != null) {
            result.add(player);
         }
      }

      return result;
   }

   public static int incAndGetEventKills(Player playerKiller, Map<Integer, AtomicInteger> killMap, int incrementValue) {
      AtomicInteger count = (AtomicInteger)killMap.get(playerKiller.getObjectId());
      if (count == null) {
         killMap.put(playerKiller.getObjectId(), new AtomicInteger(0));
         count = (AtomicInteger)killMap.get(playerKiller.getObjectId());
      }

      int currentCount = count.addAndGet(incrementValue);
      return currentCount;
   }

   public static int getEventKills(Player playerKiller, Map<Integer, AtomicInteger> killMap) {
      AtomicInteger count = (AtomicInteger)killMap.get(playerKiller.getObjectId());
      if (count == null) {
         killMap.put(playerKiller.getObjectId(), new AtomicInteger(0));
         count = (AtomicInteger)killMap.get(playerKiller.getObjectId());
      }

      int currentCount = count.get();
      return currentCount;
   }
}
