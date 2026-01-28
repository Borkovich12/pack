package events.battle.util;

import events.EventUtils;
import events.battle.BattleGvG;
import events.battle.BattleGvG1x1;
import events.battle.BattleGvG2x2;
import events.battle.BattleGvG3x3;
import events.battle.BattleGvG4x4;
import events.battle.BattleGvG5x5;
import events.battle.BattleGvG6x6;
import events.battle.BattleGvG7x7;
import events.battle.BattleGvG8x8;
import events.battle.BattleGvG9x9;
import events.battle.BattleGvGCxC;
import events.battle.enums.BattleType;
import events.battle.model.BattleGrp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleUtil {
   private static final Logger _log = LoggerFactory.getLogger(BattleUtil.class);
   private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");
   public static final String defaultDelimiter = "[\\s,;:]+";
   private static final ClassId[] HEALERS;
   private static final ClassId[] TANKERS;
   private static final ClassId[] DAGGERS;
   private static final ClassId[] ARCHERS;

   public static void sayToAll(String address, String[] replacements) {
      Iterator var2 = GameObjectsStorage.getAllPlayersForIterate().iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         sayToPlayer(player, address, replacements);
      }

   }

   public static void sayToPlayer(Player player, String address, String[] replacements) {
      CustomMessage cm = new CustomMessage(address, player, new Object[0]);
      if (replacements != null) {
         String[] var4 = replacements;
         int var5 = replacements.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String s = var4[var6];
            cm.addString(s);
         }
      }

      player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "GvG", "GvG: " + cm.toString()));
   }

   public static void sayToAll(String address) {
      Iterator var1 = GameObjectsStorage.getAllPlayersForIterate().iterator();

      while(var1.hasNext()) {
         Player player = (Player)var1.next();
         player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "GvG", "GvG: " + (new CustomMessage(address, player, new Object[0])).toString()));
      }

   }

   public static void sayScreen(List<HardReference<Player>> all_players, String address, String[] replacements) {
      Player player;
      CustomMessage cm;
      for(Iterator var3 = HardReferences.unwrap(all_players).iterator(); var3.hasNext(); player.sendPacket(new ExShowScreenMessage(cm.toString(), 2000, ScreenMessageAlign.TOP_CENTER, true))) {
         player = (Player)var3.next();
         cm = new CustomMessage(address, player, new Object[0]);
         if (replacements != null) {
            String[] var6 = replacements;
            int var7 = replacements.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String s = var6[var8];
               cm.addString(s);
            }
         }
      }

   }

   public static void actions(List<HardReference<Player>> players, List<HardReference<Player>> spectators, int action) {
      Iterator var3;
      Player player;
      if (action > 16) {
         var3 = HardReferences.unwrap(players).iterator();

         while(var3.hasNext()) {
            player = (Player)var3.next();
            broadcastPacket(player, new MagicSkillUse(player, player, action, 1, 500, 0L), spectators);
         }
      } else {
         var3 = HardReferences.unwrap(players).iterator();

         while(var3.hasNext()) {
            player = (Player)var3.next();
            broadcastPacket(player, new SocialAction(player.getObjectId(), action), spectators);
         }
      }

   }

   public static void actions(Player player, int action) {
      if (action > 16) {
         player.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(player, player, action, 1, 500, 0L)});
      } else {
         player.broadcastPacket(new L2GameServerPacket[]{new SocialAction(player.getObjectId(), action)});
      }

   }

   public static void broadcastPacket(Player player, L2GameServerPacket packets, List<HardReference<Player>> spectators) {
      player.sendPacket(packets);
      broadcastPacketToOthers(player, packets, spectators);
   }

   public static void broadcastPacketToOthers(Player player, L2GameServerPacket packets, List<HardReference<Player>> spectatorsRef) {
      if (player.isVisible()) {
         List<Player> players = World.getAroundPlayers(player);

         for(int i = 0; i < players.size(); ++i) {
            Player target = (Player)players.get(i);
            if (!spectatorsRef.contains(target.getRef())) {
               target.sendPacket(packets);
            }
         }

      }
   }

   public static boolean isMatchingRegexp(String text, String template) {
      Pattern pattern = null;

      try {
         pattern = Pattern.compile(template);
      } catch (PatternSyntaxException var4) {
         var4.printStackTrace();
      }

      if (pattern == null) {
         return false;
      } else {
         Matcher regexp = pattern.matcher(text);
         return regexp.matches();
      }
   }

   public static void onArena(Player player, Reflection reflection, Location point) {
      player.block();
      player.setIsInvul(true);
      player.setInGvG(0);
      EventUtils.healPlayer(player);
      Location loc = Location.findAroundPosition(point, 0, 100);
      if (loc != null) {
         player.teleToLocation(loc, reflection != null ? reflection : ReflectionManager.DEFAULT);
      } else {
         _log.warn("BattleUtil: Position " + point.toXYZString() + " not found for teleporting to the arena.");
      }

      player.sitDown((StaticObjectInstance)null);
      player.broadcastCharInfo();
   }

   public static void sayToParticipants(List<HardReference<Player>> all_players, List<HardReference<Player>> spectators, BattleType type, String text, boolean spec) {
      Say2 cs = new Say2(0, ChatType.CRITICAL_ANNOUNCE, "GvG", "GvG: " + text);
      Iterator var6 = HardReferences.unwrap(all_players).iterator();

      Player player;
      while(var6.hasNext()) {
         player = (Player)var6.next();
         player.sendPacket(cs);
      }

      if (spec) {
         var6 = HardReferences.unwrap(spectators).iterator();

         while(var6.hasNext()) {
            player = (Player)var6.next();
            player.sendPacket(cs);
         }
      }

   }

   public static void sayToParticipants(List<HardReference<Player>> all_players, List<HardReference<Player>> spectators, boolean spec, String address, String[] replacements) {
      Iterator var5 = HardReferences.unwrap(all_players).iterator();

      Player spectator;
      while(var5.hasNext()) {
         spectator = (Player)var5.next();
         sayToParticipants(spectator, address, replacements);
      }

      if (spec) {
         var5 = HardReferences.unwrap(spectators).iterator();

         while(var5.hasNext()) {
            spectator = (Player)var5.next();
            sayToParticipants(spectator, address, replacements);
         }
      }

   }

   public static void sayToParticipants(Player player, String address, String[] replacements) {
      CustomMessage cm = new CustomMessage(address, player, new Object[0]);
      if (replacements != null) {
         String[] var4 = replacements;
         int var5 = replacements.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String s = var4[var6];
            cm.addString(s);
         }
      }

      player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "GvG", "GvG: " + cm.toString()));
   }

   public static void backPlayer(Player player, BattleGvG battle, boolean exit) {
      BattleType type = battle.getType();
      EventUtils.healPlayer(player);
      if (!exit) {
         player.getListeners().onGvGEvent(false);
      }

      player.setInGvG(0);
      player.setReg(false);
      if (type.isRestrictIp()) {
         battle.getRestrictIp().remove(player.getIP());
      }

      if (type.isRestrictHwid()) {
         GameClient playerClient = player.getNetConnection();
         if (playerClient != null) {
            battle.getRestrictHwid().remove(playerClient.getHwid());
         }
      }

      player.setResurectProhibited(false);
      player.resetReuse();
      player.standUp();
      player.broadcastCharInfo();
      if (type.isToArena()) {
         player.setIsInvul(false);
      }

      player.unblock();
      if (type.getReturnPoint().length < 3) {
         Location ClearLoc = Location.parseLoc(type.getClearLoc());

         try {
            String var = player.getVar("BattleGvG_backCoords");
            if (var == null) {
               if (player.isLogoutStarted()) {
                  player.setReflection(ReflectionManager.DEFAULT);
                  player.setXYZ(ClearLoc.x, ClearLoc.y, ClearLoc.z);
               } else {
                  player.teleToLocation(ClearLoc, ReflectionManager.DEFAULT);
               }

               return;
            }

            String[] coords = var.split(" ");
            player.unsetVar("BattleGvG_backCoords");
            if (coords.length < 3) {
               if (player.isLogoutStarted()) {
                  player.setReflection(ReflectionManager.DEFAULT);
                  player.setXYZ(ClearLoc.x, ClearLoc.y, ClearLoc.z);
               } else {
                  player.teleToLocation(ClearLoc, ReflectionManager.DEFAULT);
               }

               return;
            }

            if (player.isLogoutStarted()) {
               player.setReflection(ReflectionManager.DEFAULT);
               player.setXYZ(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
            } else {
               player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), ReflectionManager.DEFAULT);
            }
         } catch (Exception var7) {
            _log.error("on back", var7);
         }
      } else {
         player.teleToLocation(type.getReturnPoint()[0], type.getReturnPoint()[1], type.getReturnPoint()[2], ReflectionManager.DEFAULT);
      }

   }

   public static String toSimpleFormat(long cal) {
      return SIMPLE_FORMAT.format(cal);
   }

   public static NpcInstance spawnSingle(int npcId, Location spawnedLoc, Reflection reflection) {
      NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
      if (template == null) {
         throw new NullPointerException("Npc template id : " + npcId + " not found!");
      } else {
         NpcInstance newInstance = template.getNewInstance();
         newInstance.setHeading(spawnedLoc.h < 0 ? Rnd.get(65535) : spawnedLoc.h);
         newInstance.setSpawnedLoc(spawnedLoc);
         newInstance.setReflection(reflection);
         newInstance.setCurrentHpMp((double)newInstance.getMaxHp(), (double)newInstance.getMaxMp(), true);
         newInstance.spawnMe(newInstance.getSpawnedLoc());
         return newInstance;
      }
   }

   public static int[] parseCommaSeparatedIntegerArray(String s) {
      if (s.isEmpty()) {
         return new int[0];
      } else {
         String[] values = s.split("[\\s,;:]+");
         int[] val = new int[values.length];

         for(int i = 0; i < val.length; ++i) {
            val[i] = Integer.parseInt(values[i]);
         }

         return val;
      }
   }

   public static void removeSpec(HardReference<Player> ref, String typeName) {
      BattleType type = BattleType.getTypeByName(typeName);
      if (type == BattleType.B1X1) {
         BattleGvG1x1.getInstance().removeSpec(ref);
      } else if (type == BattleType.B2X2) {
         BattleGvG2x2.getInstance().removeSpec(ref);
      } else if (type == BattleType.B3X3) {
         BattleGvG3x3.getInstance().removeSpec(ref);
      } else if (type == BattleType.B4X4) {
         BattleGvG4x4.getInstance().removeSpec(ref);
      } else if (type == BattleType.B5X5) {
         BattleGvG5x5.getInstance().removeSpec(ref);
      } else if (type == BattleType.B6X6) {
         BattleGvG6x6.getInstance().removeSpec(ref);
      } else if (type == BattleType.B7X7) {
         BattleGvG7x7.getInstance().removeSpec(ref);
      } else if (type == BattleType.B8X8) {
         BattleGvG8x8.getInstance().removeSpec(ref);
      } else if (type == BattleType.B9X9) {
         BattleGvG9x9.getInstance().removeSpec(ref);
      } else if (type == BattleType.BCXC) {
         BattleGvGCxC.getInstance().removeSpec(ref);
      }

   }

   public static void giveWinnerReward(List<BattleGrp> winners, BattleType type) {
      label48:
      for(int i = 0; i < winners.size(); ++i) {
         if (winners.get(i) != null && type.getReward()[i].length > 1) {
            Iterator var3 = ((BattleGrp)winners.get(i)).getPlayerIds().iterator();

            while(true) {
               while(true) {
                  if (!var3.hasNext()) {
                     continue label48;
                  }

                  int objectId = (Integer)var3.next();
                  Player player = GameObjectsStorage.getPlayer(objectId);
                  int n;
                  int itemId;
                  int itemCount;
                  if (player != null) {
                     for(n = 0; n < type.getReward()[i].length; n += 2) {
                        itemId = type.getReward()[i][n];
                        itemCount = type.getReward()[i][n + 1];
                        Functions.addItem(player, itemId, (long)itemCount);
                     }

                     player.getListeners().onGvGEvent(true);
                  } else {
                     for(n = 0; n < type.getReward()[i].length; n += 2) {
                        itemId = type.getReward()[i][n];
                        itemCount = type.getReward()[i][n + 1];
                        DelayedItemsManager.getInstance().addDelayed(objectId, itemId, itemCount, 0, 0, 0, "Reward for battle itemId=" + itemId + " itemCount=" + itemCount + " bought by " + player);
                     }
                  }
               }
            }
         }
      }

   }

   public static boolean isHealer(ClassId classId) {
      return ArrayUtils.contains(HEALERS, classId);
   }

   public static boolean isDagger(ClassId classId) {
      return ArrayUtils.contains(DAGGERS, classId);
   }

   public static boolean isTanker(ClassId classId) {
      return ArrayUtils.contains(TANKERS, classId);
   }

   public static boolean isArcher(ClassId classId) {
      return ArrayUtils.contains(ARCHERS, classId);
   }

   public static int getCountOfClass(ClassId classId, Collection<Player> members) {
      int count = 0;
      Iterator var3 = members.iterator();

      while(var3.hasNext()) {
         Player player = (Player)var3.next();
         if (ClassId.getClassById(player.getActiveClassId()) == classId) {
            ++count;
         }
      }

      return count;
   }

   public static String getCheckProhibitedClass(int[] prohibitedClass, Collection<Player> members) {
      Iterator var2 = members.iterator();

      Player member;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         member = (Player)var2.next();
      } while(!ArrayUtils.contains(prohibitedClass, member.getActiveClassId()));

      return HtmlUtils.htmlClassName(member.getActiveClassId(), member);
   }

   public static int getHealersCount(Collection<Player> members) {
      int count = 0;
      Iterator var2 = members.iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         if (isHealer(ClassId.getClassById(player.getActiveClassId()))) {
            ++count;
         }
      }

      return count;
   }

   public static int getTankersCount(Collection<Player> members) {
      int count = 0;
      Iterator var2 = members.iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         if (isTanker(ClassId.getClassById(player.getActiveClassId()))) {
            ++count;
         }
      }

      return count;
   }

   public static int getDaggersCount(Collection<Player> members) {
      int count = 0;
      Iterator var2 = members.iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         if (isDagger(ClassId.getClassById(player.getActiveClassId()))) {
            ++count;
         }
      }

      return count;
   }

   public static int getArchersCount(Collection<Player> members) {
      int count = 0;
      Iterator var2 = members.iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         if (isArcher(ClassId.getClassById(player.getActiveClassId()))) {
            ++count;
         }
      }

      return count;
   }

   public static final int getLevelWithoutEnchant(Skill skill) {
      int level = skill.getLevel();
      if (level > 100) {
         int baseLevel = skill.getBaseLevel();
         return baseLevel;
      } else {
         return level;
      }
   }

   static {
      HEALERS = new ClassId[]{ClassId.bishop, ClassId.cardinal, ClassId.evas_saint, ClassId.elder, ClassId.shillien_saint, ClassId.shillien_elder};
      TANKERS = new ClassId[]{ClassId.hell_knight, ClassId.dark_avenger, ClassId.shillien_knight, ClassId.shillien_templar, ClassId.evas_templar, ClassId.temple_knight, ClassId.bladedancer, ClassId.spectral_dancer, ClassId.swordsinger, ClassId.sword_muse, ClassId.phoenix_knight, ClassId.paladin};
      DAGGERS = new ClassId[]{ClassId.adventurer, ClassId.treasure_hunter, ClassId.ghost_hunter, ClassId.abyss_walker, ClassId.plain_walker, ClassId.wind_rider, ClassId.fortune_seeker, ClassId.bounty_hunter};
      ARCHERS = new ClassId[]{ClassId.sagittarius, ClassId.hawkeye, ClassId.ghost_sentinel, ClassId.phantom_ranger, ClassId.moonlight_sentinel, ClassId.silver_ranger};
   }
}
