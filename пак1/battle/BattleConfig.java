package events.battle;

import java.util.ArrayList;
import java.util.List;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleConfig {
   private static final Logger _log = LoggerFactory.getLogger(BattleConfig.class);
   public static final String BATTLE_GVG1X1_FILE = "config/events/gvg/battlegvg1x1.properties";
   public static final String BATTLE_GVG2X2_FILE = "config/events/gvg/battlegvg2x2.properties";
   public static final String BATTLE_GVG3X3_FILE = "config/events/gvg/battlegvg3x3.properties";
   public static final String BATTLE_GVG4X4_FILE = "config/events/gvg/battlegvg4x4.properties";
   public static final String BATTLE_GVG5X5_FILE = "config/events/gvg/battlegvg5x5.properties";
   public static final String BATTLE_GVG6X6_FILE = "config/events/gvg/battlegvg6x6.properties";
   public static final String BATTLE_GVG7X7_FILE = "config/events/gvg/battlegvg7x7.properties";
   public static final String BATTLE_GVG8X8_FILE = "config/events/gvg/battlegvg8x8.properties";
   public static final String BATTLE_GVG9X9_FILE = "config/events/gvg/battlegvg9x9.properties";
   public static final String BATTLE_GVGCXC_FILE = "config/events/gvg/battlegvgCxC.properties";
   public static boolean GVG_1X1_ENABLE;
   public static String GVG_1X1_START_TIME;
   public static int GVG_1X1_INSTANCE_ID;
   public static int GVG_1X1_MIN_LVL;
   public static int GVG_1X1_MAX_LVL;
   public static int GVG_1X1_COMMANDS_MIN;
   public static int GVG_1X1_COMMANDS_MAX;
   public static boolean GVG_1X1_RESTRICT_IP;
   public static boolean GVG_1X1_RESTRICT_HWID;
   public static boolean GVG_1X1_TO_ARENA;
   public static String[] GVG_1X1_ARENA_POINTS;
   public static boolean GVG_1X1_IN_PEACE;
   public static boolean GVG_1X1_PRE_CHECK;
   public static boolean GVG_1X1_BLOCK_MOVE;
   public static boolean GVG_1X1_SHOW_STATUS;
   public static int GVG_1X1_ROUNDS_WIN;
   public static int GVG_1X1_REG_TIME;
   public static int[] GVG_1X1_ANNOUNCE_REG_TIMES;
   public static int GVG_1X1_TIME_BATTLE;
   public static int GVG_1X1_HEAL_TP_BACK_TIME;
   public static boolean GVG_1X1_ALLOW_BATTLE_TIMER;
   public static int[] GVG_1X1_RETURN_POINT;
   public static int GVG_1X1_MANAGER_ID;
   public static byte GVG_1X1_MANAGER_SPAWN_TYPE;
   public static int[] GVG_1X1_MANAGER_COORDS;
   public static int[] GVG_1X1_RESTRICTED_SKILLS;
   public static int[] GVG_1X1_RESTRICTED_ITEMS;
   public static int[] GVG_1X1_RESTRICTED_SUMMONS;
   public static int GVG_1X1_BUFFER_ID;
   public static int GVG_1X1_TP_RANGE;
   public static String[] GVG_1X1_BUFFER1_COORDS;
   public static String[] GVG_1X1_BUFFER2_COORDS;
   public static boolean GVG_1X1_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_1X1_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_1X1_MAGE_BUFF;
   public static boolean GVG_1X1_CUSTOM_ITEMS_ENABLE;
   public static String GVG_1X1_CUSTOM_ITEMS_PATH;
   public static int GVG_1X1_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_1X1_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_1X1_ENCHANT_LIMIT;
   public static int GVG_1X1_ENCHANT_LIMIT_WEAPON;
   public static int GVG_1X1_ENCHANT_LIMIT_ARMOR;
   public static String GVG_1X1_CNAME_TEMPLATE;
   public static boolean GVG_1X1_ALLOW_SPEC;
   public static boolean GVG_1X1_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_1X1_REWARD;
   public static int[][] GVG_1X1_REWARD_PER_KILL;
   public static List<Location> GVG_1X1_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_1X1_RED_TEAM_LOC = new ArrayList();
   public static String GVG_1X1_CLEAR_LOC;
   public static int[] GVG_1X1_COORDS_SPECTATORS;
   public static boolean GVG_1X1_NO_AN;
   public static boolean GVG_1X1_NO_ENCHANT_SKILLS;
   public static int[] GVG_1X1_PROHIBITED_CLASS_IDS;
   public static int GVG_1X1_DUELIST_ALLOWED;
   public static int GVG_1X1_DREADNOUGHT_ALLOWED;
   public static int GVG_1X1_TANKER_ALLOWED;
   public static int GVG_1X1_DAGGER_ALLOWED;
   public static int GVG_1X1_ARCHER_ALLOWED;
   public static int GVG_1X1_HEALER_ALLOWED;
   public static int GVG_1X1_ARCHMAGE_ALLOWED;
   public static int GVG_1X1_SOULTAKER_ALLOWED;
   public static int GVG_1X1_MYSTICMOUSE_ALLOWED;
   public static int GVG_1X1_STORMSCREAMER_ALLOWED;
   public static int GVG_1X1_TITAN_ALLOWED;
   public static int GVG_1X1_DOMINATOR_ALLOWED;
   public static int GVG_1X1_DOOMCRYER_ALLOWED;
   public static boolean GVG_2X2_ENABLE;
   public static String GVG_2X2_START_TIME;
   public static int GVG_2X2_INSTANCE_ID;
   public static int GVG_2X2_MIN_LVL;
   public static int GVG_2X2_MAX_LVL;
   public static int GVG_2X2_COMMANDS_MIN;
   public static int GVG_2X2_COMMANDS_MAX;
   public static boolean GVG_2X2_RESTRICT_IP;
   public static boolean GVG_2X2_RESTRICT_HWID;
   public static boolean GVG_2X2_TO_ARENA;
   public static String[] GVG_2X2_ARENA_POINTS;
   public static boolean GVG_2X2_IN_PEACE;
   public static boolean GVG_2X2_PRE_CHECK;
   public static boolean GVG_2X2_BLOCK_MOVE;
   public static boolean GVG_2X2_SHOW_STATUS;
   public static int GVG_2X2_ROUNDS_WIN;
   public static int GVG_2X2_REG_TIME;
   public static int[] GVG_2X2_ANNOUNCE_REG_TIMES;
   public static int GVG_2X2_TIME_BATTLE;
   public static int GVG_2X2_HEAL_TP_BACK_TIME;
   public static boolean GVG_2X2_ALLOW_BATTLE_TIMER;
   public static int[] GVG_2X2_RETURN_POINT;
   public static int GVG_2X2_MANAGER_ID;
   public static byte GVG_2X2_MANAGER_SPAWN_TYPE;
   public static int[] GVG_2X2_MANAGER_COORDS;
   public static int[] GVG_2X2_RESTRICTED_SKILLS;
   public static int[] GVG_2X2_RESTRICTED_ITEMS;
   public static int[] GVG_2X2_RESTRICTED_SUMMONS;
   public static int GVG_2X2_BUFFER_ID;
   public static int GVG_2X2_TP_RANGE;
   public static String[] GVG_2X2_BUFFER1_COORDS;
   public static String[] GVG_2X2_BUFFER2_COORDS;
   public static boolean GVG_2X2_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_2X2_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_2X2_MAGE_BUFF;
   public static boolean GVG_2X2_CUSTOM_ITEMS_ENABLE;
   public static String GVG_2X2_CUSTOM_ITEMS_PATH;
   public static int GVG_2X2_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_2X2_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_2X2_ENCHANT_LIMIT;
   public static int GVG_2X2_ENCHANT_LIMIT_WEAPON;
   public static int GVG_2X2_ENCHANT_LIMIT_ARMOR;
   public static String GVG_2X2_CNAME_TEMPLATE;
   public static boolean GVG_2X2_ALLOW_SPEC;
   public static boolean GVG_2X2_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_2X2_REWARD;
   public static int[][] GVG_2X2_REWARD_PER_KILL;
   public static List<Location> GVG_2X2_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_2X2_RED_TEAM_LOC = new ArrayList();
   public static String GVG_2X2_CLEAR_LOC;
   public static int[] GVG_2X2_COORDS_SPECTATORS;
   public static boolean GVG_2X2_NO_AN;
   public static boolean GVG_2X2_NO_ENCHANT_SKILLS;
   public static int[] GVG_2X2_PROHIBITED_CLASS_IDS;
   public static int GVG_2X2_DUELIST_ALLOWED;
   public static int GVG_2X2_DREADNOUGHT_ALLOWED;
   public static int GVG_2X2_TANKER_ALLOWED;
   public static int GVG_2X2_DAGGER_ALLOWED;
   public static int GVG_2X2_ARCHER_ALLOWED;
   public static int GVG_2X2_HEALER_ALLOWED;
   public static int GVG_2X2_ARCHMAGE_ALLOWED;
   public static int GVG_2X2_SOULTAKER_ALLOWED;
   public static int GVG_2X2_MYSTICMOUSE_ALLOWED;
   public static int GVG_2X2_STORMSCREAMER_ALLOWED;
   public static int GVG_2X2_TITAN_ALLOWED;
   public static int GVG_2X2_DOMINATOR_ALLOWED;
   public static int GVG_2X2_DOOMCRYER_ALLOWED;
   public static boolean GVG_3X3_ENABLE;
   public static String GVG_3X3_START_TIME;
   public static int GVG_3X3_INSTANCE_ID;
   public static int GVG_3X3_MIN_LVL;
   public static int GVG_3X3_MAX_LVL;
   public static int GVG_3X3_COMMANDS_MIN;
   public static int GVG_3X3_COMMANDS_MAX;
   public static boolean GVG_3X3_RESTRICT_IP;
   public static boolean GVG_3X3_RESTRICT_HWID;
   public static boolean GVG_3X3_TO_ARENA;
   public static String[] GVG_3X3_ARENA_POINTS;
   public static boolean GVG_3X3_IN_PEACE;
   public static boolean GVG_3X3_PRE_CHECK;
   public static boolean GVG_3X3_BLOCK_MOVE;
   public static boolean GVG_3X3_SHOW_STATUS;
   public static int GVG_3X3_ROUNDS_WIN;
   public static int GVG_3X3_REG_TIME;
   public static int[] GVG_3X3_ANNOUNCE_REG_TIMES;
   public static int GVG_3X3_TIME_BATTLE;
   public static int GVG_3X3_HEAL_TP_BACK_TIME;
   public static boolean GVG_3X3_ALLOW_BATTLE_TIMER;
   public static int[] GVG_3X3_RETURN_POINT;
   public static int GVG_3X3_MANAGER_ID;
   public static byte GVG_3X3_MANAGER_SPAWN_TYPE;
   public static int[] GVG_3X3_MANAGER_COORDS;
   public static int[] GVG_3X3_RESTRICTED_SKILLS;
   public static int[] GVG_3X3_RESTRICTED_ITEMS;
   public static int[] GVG_3X3_RESTRICTED_SUMMONS;
   public static int GVG_3X3_BUFFER_ID;
   public static int GVG_3X3_TP_RANGE;
   public static String[] GVG_3X3_BUFFER1_COORDS;
   public static String[] GVG_3X3_BUFFER2_COORDS;
   public static boolean GVG_3X3_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_3X3_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_3X3_MAGE_BUFF;
   public static boolean GVG_3X3_CUSTOM_ITEMS_ENABLE;
   public static String GVG_3X3_CUSTOM_ITEMS_PATH;
   public static int GVG_3X3_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_3X3_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_3X3_ENCHANT_LIMIT;
   public static int GVG_3X3_ENCHANT_LIMIT_WEAPON;
   public static int GVG_3X3_ENCHANT_LIMIT_ARMOR;
   public static String GVG_3X3_CNAME_TEMPLATE;
   public static boolean GVG_3X3_ALLOW_SPEC;
   public static boolean GVG_3X3_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_3X3_REWARD;
   public static int[][] GVG_3X3_REWARD_PER_KILL;
   public static List<Location> GVG_3X3_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_3X3_RED_TEAM_LOC = new ArrayList();
   public static String GVG_3X3_CLEAR_LOC;
   public static int[] GVG_3X3_COORDS_SPECTATORS;
   public static boolean GVG_3X3_NO_AN;
   public static boolean GVG_3X3_NO_ENCHANT_SKILLS;
   public static int[] GVG_3X3_PROHIBITED_CLASS_IDS;
   public static int GVG_3X3_DUELIST_ALLOWED;
   public static int GVG_3X3_DREADNOUGHT_ALLOWED;
   public static int GVG_3X3_TANKER_ALLOWED;
   public static int GVG_3X3_DAGGER_ALLOWED;
   public static int GVG_3X3_ARCHER_ALLOWED;
   public static int GVG_3X3_HEALER_ALLOWED;
   public static int GVG_3X3_ARCHMAGE_ALLOWED;
   public static int GVG_3X3_SOULTAKER_ALLOWED;
   public static int GVG_3X3_MYSTICMOUSE_ALLOWED;
   public static int GVG_3X3_STORMSCREAMER_ALLOWED;
   public static int GVG_3X3_TITAN_ALLOWED;
   public static int GVG_3X3_DOMINATOR_ALLOWED;
   public static int GVG_3X3_DOOMCRYER_ALLOWED;
   public static boolean GVG_4X4_ENABLE;
   public static String GVG_4X4_START_TIME;
   public static int GVG_4X4_INSTANCE_ID;
   public static int GVG_4X4_MIN_LVL;
   public static int GVG_4X4_MAX_LVL;
   public static int GVG_4X4_COMMANDS_MIN;
   public static int GVG_4X4_COMMANDS_MAX;
   public static boolean GVG_4X4_RESTRICT_IP;
   public static boolean GVG_4X4_RESTRICT_HWID;
   public static boolean GVG_4X4_TO_ARENA;
   public static String[] GVG_4X4_ARENA_POINTS;
   public static boolean GVG_4X4_IN_PEACE;
   public static boolean GVG_4X4_PRE_CHECK;
   public static boolean GVG_4X4_BLOCK_MOVE;
   public static boolean GVG_4X4_SHOW_STATUS;
   public static int GVG_4X4_ROUNDS_WIN;
   public static int GVG_4X4_REG_TIME;
   public static int[] GVG_4X4_ANNOUNCE_REG_TIMES;
   public static int GVG_4X4_TIME_BATTLE;
   public static int GVG_4X4_HEAL_TP_BACK_TIME;
   public static boolean GVG_4X4_ALLOW_BATTLE_TIMER;
   public static int[] GVG_4X4_RETURN_POINT;
   public static int GVG_4X4_MANAGER_ID;
   public static byte GVG_4X4_MANAGER_SPAWN_TYPE;
   public static int[] GVG_4X4_MANAGER_COORDS;
   public static int[] GVG_4X4_RESTRICTED_SKILLS;
   public static int[] GVG_4X4_RESTRICTED_ITEMS;
   public static int[] GVG_4X4_RESTRICTED_SUMMONS;
   public static int GVG_4X4_BUFFER_ID;
   public static int GVG_4X4_TP_RANGE;
   public static String[] GVG_4X4_BUFFER1_COORDS;
   public static String[] GVG_4X4_BUFFER2_COORDS;
   public static boolean GVG_4X4_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_4X4_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_4X4_MAGE_BUFF;
   public static boolean GVG_4X4_CUSTOM_ITEMS_ENABLE;
   public static String GVG_4X4_CUSTOM_ITEMS_PATH;
   public static int GVG_4X4_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_4X4_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_4X4_ENCHANT_LIMIT;
   public static int GVG_4X4_ENCHANT_LIMIT_WEAPON;
   public static int GVG_4X4_ENCHANT_LIMIT_ARMOR;
   public static String GVG_4X4_CNAME_TEMPLATE;
   public static boolean GVG_4X4_ALLOW_SPEC;
   public static boolean GVG_4X4_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_4X4_REWARD;
   public static int[][] GVG_4X4_REWARD_PER_KILL;
   public static List<Location> GVG_4X4_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_4X4_RED_TEAM_LOC = new ArrayList();
   public static String GVG_4X4_CLEAR_LOC;
   public static int[] GVG_4X4_COORDS_SPECTATORS;
   public static boolean GVG_4X4_NO_AN;
   public static boolean GVG_4X4_NO_ENCHANT_SKILLS;
   public static int[] GVG_4X4_PROHIBITED_CLASS_IDS;
   public static int GVG_4X4_DUELIST_ALLOWED;
   public static int GVG_4X4_DREADNOUGHT_ALLOWED;
   public static int GVG_4X4_TANKER_ALLOWED;
   public static int GVG_4X4_DAGGER_ALLOWED;
   public static int GVG_4X4_ARCHER_ALLOWED;
   public static int GVG_4X4_HEALER_ALLOWED;
   public static int GVG_4X4_ARCHMAGE_ALLOWED;
   public static int GVG_4X4_SOULTAKER_ALLOWED;
   public static int GVG_4X4_MYSTICMOUSE_ALLOWED;
   public static int GVG_4X4_STORMSCREAMER_ALLOWED;
   public static int GVG_4X4_TITAN_ALLOWED;
   public static int GVG_4X4_DOMINATOR_ALLOWED;
   public static int GVG_4X4_DOOMCRYER_ALLOWED;
   public static boolean GVG_5X5_ENABLE;
   public static String GVG_5X5_START_TIME;
   public static int GVG_5X5_INSTANCE_ID;
   public static int GVG_5X5_MIN_LVL;
   public static int GVG_5X5_MAX_LVL;
   public static int GVG_5X5_COMMANDS_MIN;
   public static int GVG_5X5_COMMANDS_MAX;
   public static boolean GVG_5X5_RESTRICT_IP;
   public static boolean GVG_5X5_RESTRICT_HWID;
   public static boolean GVG_5X5_TO_ARENA;
   public static String[] GVG_5X5_ARENA_POINTS;
   public static boolean GVG_5X5_IN_PEACE;
   public static boolean GVG_5X5_PRE_CHECK;
   public static boolean GVG_5X5_BLOCK_MOVE;
   public static boolean GVG_5X5_SHOW_STATUS;
   public static int GVG_5X5_ROUNDS_WIN;
   public static int GVG_5X5_REG_TIME;
   public static int[] GVG_5X5_ANNOUNCE_REG_TIMES;
   public static int GVG_5X5_TIME_BATTLE;
   public static int GVG_5X5_HEAL_TP_BACK_TIME;
   public static boolean GVG_5X5_ALLOW_BATTLE_TIMER;
   public static int[] GVG_5X5_RETURN_POINT;
   public static int GVG_5X5_MANAGER_ID;
   public static byte GVG_5X5_MANAGER_SPAWN_TYPE;
   public static int[] GVG_5X5_MANAGER_COORDS;
   public static int[] GVG_5X5_RESTRICTED_SKILLS;
   public static int[] GVG_5X5_RESTRICTED_ITEMS;
   public static int[] GVG_5X5_RESTRICTED_SUMMONS;
   public static int GVG_5X5_BUFFER_ID;
   public static int GVG_5X5_TP_RANGE;
   public static String[] GVG_5X5_BUFFER1_COORDS;
   public static String[] GVG_5X5_BUFFER2_COORDS;
   public static boolean GVG_5X5_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_5X5_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_5X5_MAGE_BUFF;
   public static boolean GVG_5X5_CUSTOM_ITEMS_ENABLE;
   public static String GVG_5X5_CUSTOM_ITEMS_PATH;
   public static int GVG_5X5_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_5X5_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_5X5_ENCHANT_LIMIT;
   public static int GVG_5X5_ENCHANT_LIMIT_WEAPON;
   public static int GVG_5X5_ENCHANT_LIMIT_ARMOR;
   public static String GVG_5X5_CNAME_TEMPLATE;
   public static boolean GVG_5X5_ALLOW_SPEC;
   public static boolean GVG_5X5_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_5X5_REWARD;
   public static int[][] GVG_5X5_REWARD_PER_KILL;
   public static List<Location> GVG_5X5_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_5X5_RED_TEAM_LOC = new ArrayList();
   public static String GVG_5X5_CLEAR_LOC;
   public static int[] GVG_5X5_COORDS_SPECTATORS;
   public static boolean GVG_5X5_NO_AN;
   public static boolean GVG_5X5_NO_ENCHANT_SKILLS;
   public static int[] GVG_5X5_PROHIBITED_CLASS_IDS;
   public static int GVG_5X5_DUELIST_ALLOWED;
   public static int GVG_5X5_DREADNOUGHT_ALLOWED;
   public static int GVG_5X5_TANKER_ALLOWED;
   public static int GVG_5X5_DAGGER_ALLOWED;
   public static int GVG_5X5_ARCHER_ALLOWED;
   public static int GVG_5X5_HEALER_ALLOWED;
   public static int GVG_5X5_ARCHMAGE_ALLOWED;
   public static int GVG_5X5_SOULTAKER_ALLOWED;
   public static int GVG_5X5_MYSTICMOUSE_ALLOWED;
   public static int GVG_5X5_STORMSCREAMER_ALLOWED;
   public static int GVG_5X5_TITAN_ALLOWED;
   public static int GVG_5X5_DOMINATOR_ALLOWED;
   public static int GVG_5X5_DOOMCRYER_ALLOWED;
   public static boolean GVG_6X6_ENABLE;
   public static String GVG_6X6_START_TIME;
   public static int GVG_6X6_INSTANCE_ID;
   public static int GVG_6X6_MIN_LVL;
   public static int GVG_6X6_MAX_LVL;
   public static int GVG_6X6_COMMANDS_MIN;
   public static int GVG_6X6_COMMANDS_MAX;
   public static boolean GVG_6X6_RESTRICT_IP;
   public static boolean GVG_6X6_RESTRICT_HWID;
   public static boolean GVG_6X6_TO_ARENA;
   public static String[] GVG_6X6_ARENA_POINTS;
   public static boolean GVG_6X6_IN_PEACE;
   public static boolean GVG_6X6_PRE_CHECK;
   public static boolean GVG_6X6_BLOCK_MOVE;
   public static boolean GVG_6X6_SHOW_STATUS;
   public static int GVG_6X6_ROUNDS_WIN;
   public static int GVG_6X6_REG_TIME;
   public static int[] GVG_6X6_ANNOUNCE_REG_TIMES;
   public static int GVG_6X6_TIME_BATTLE;
   public static int GVG_6X6_HEAL_TP_BACK_TIME;
   public static boolean GVG_6X6_ALLOW_BATTLE_TIMER;
   public static int[] GVG_6X6_RETURN_POINT;
   public static int GVG_6X6_MANAGER_ID;
   public static byte GVG_6X6_MANAGER_SPAWN_TYPE;
   public static int[] GVG_6X6_MANAGER_COORDS;
   public static int[] GVG_6X6_RESTRICTED_SKILLS;
   public static int[] GVG_6X6_RESTRICTED_ITEMS;
   public static int[] GVG_6X6_RESTRICTED_SUMMONS;
   public static int GVG_6X6_BUFFER_ID;
   public static int GVG_6X6_TP_RANGE;
   public static String[] GVG_6X6_BUFFER1_COORDS;
   public static String[] GVG_6X6_BUFFER2_COORDS;
   public static boolean GVG_6X6_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_6X6_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_6X6_MAGE_BUFF;
   public static boolean GVG_6X6_CUSTOM_ITEMS_ENABLE;
   public static String GVG_6X6_CUSTOM_ITEMS_PATH;
   public static int GVG_6X6_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_6X6_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_6X6_ENCHANT_LIMIT;
   public static int GVG_6X6_ENCHANT_LIMIT_WEAPON;
   public static int GVG_6X6_ENCHANT_LIMIT_ARMOR;
   public static String GVG_6X6_CNAME_TEMPLATE;
   public static boolean GVG_6X6_ALLOW_SPEC;
   public static boolean GVG_6X6_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_6X6_REWARD;
   public static int[][] GVG_6X6_REWARD_PER_KILL;
   public static List<Location> GVG_6X6_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_6X6_RED_TEAM_LOC = new ArrayList();
   public static String GVG_6X6_CLEAR_LOC;
   public static int[] GVG_6X6_COORDS_SPECTATORS;
   public static boolean GVG_6X6_NO_AN;
   public static boolean GVG_6X6_NO_ENCHANT_SKILLS;
   public static int[] GVG_6X6_PROHIBITED_CLASS_IDS;
   public static int GVG_6X6_DUELIST_ALLOWED;
   public static int GVG_6X6_DREADNOUGHT_ALLOWED;
   public static int GVG_6X6_TANKER_ALLOWED;
   public static int GVG_6X6_DAGGER_ALLOWED;
   public static int GVG_6X6_ARCHER_ALLOWED;
   public static int GVG_6X6_HEALER_ALLOWED;
   public static int GVG_6X6_ARCHMAGE_ALLOWED;
   public static int GVG_6X6_SOULTAKER_ALLOWED;
   public static int GVG_6X6_MYSTICMOUSE_ALLOWED;
   public static int GVG_6X6_STORMSCREAMER_ALLOWED;
   public static int GVG_6X6_TITAN_ALLOWED;
   public static int GVG_6X6_DOMINATOR_ALLOWED;
   public static int GVG_6X6_DOOMCRYER_ALLOWED;
   public static boolean GVG_7X7_ENABLE;
   public static String GVG_7X7_START_TIME;
   public static int GVG_7X7_INSTANCE_ID;
   public static int GVG_7X7_MIN_LVL;
   public static int GVG_7X7_MAX_LVL;
   public static int GVG_7X7_COMMANDS_MIN;
   public static int GVG_7X7_COMMANDS_MAX;
   public static boolean GVG_7X7_RESTRICT_IP;
   public static boolean GVG_7X7_RESTRICT_HWID;
   public static boolean GVG_7X7_TO_ARENA;
   public static String[] GVG_7X7_ARENA_POINTS;
   public static boolean GVG_7X7_IN_PEACE;
   public static boolean GVG_7X7_PRE_CHECK;
   public static boolean GVG_7X7_BLOCK_MOVE;
   public static boolean GVG_7X7_SHOW_STATUS;
   public static int GVG_7X7_ROUNDS_WIN;
   public static int GVG_7X7_REG_TIME;
   public static int[] GVG_7X7_ANNOUNCE_REG_TIMES;
   public static int GVG_7X7_TIME_BATTLE;
   public static int GVG_7X7_HEAL_TP_BACK_TIME;
   public static boolean GVG_7X7_ALLOW_BATTLE_TIMER;
   public static int[] GVG_7X7_RETURN_POINT;
   public static int GVG_7X7_MANAGER_ID;
   public static byte GVG_7X7_MANAGER_SPAWN_TYPE;
   public static int[] GVG_7X7_MANAGER_COORDS;
   public static int[] GVG_7X7_RESTRICTED_SKILLS;
   public static int[] GVG_7X7_RESTRICTED_ITEMS;
   public static int[] GVG_7X7_RESTRICTED_SUMMONS;
   public static int GVG_7X7_BUFFER_ID;
   public static int GVG_7X7_TP_RANGE;
   public static String[] GVG_7X7_BUFFER1_COORDS;
   public static String[] GVG_7X7_BUFFER2_COORDS;
   public static boolean GVG_7X7_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_7X7_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_7X7_MAGE_BUFF;
   public static boolean GVG_7X7_CUSTOM_ITEMS_ENABLE;
   public static String GVG_7X7_CUSTOM_ITEMS_PATH;
   public static int GVG_7X7_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_7X7_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_7X7_ENCHANT_LIMIT;
   public static int GVG_7X7_ENCHANT_LIMIT_WEAPON;
   public static int GVG_7X7_ENCHANT_LIMIT_ARMOR;
   public static String GVG_7X7_CNAME_TEMPLATE;
   public static boolean GVG_7X7_ALLOW_SPEC;
   public static boolean GVG_7X7_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_7X7_REWARD;
   public static int[][] GVG_7X7_REWARD_PER_KILL;
   public static List<Location> GVG_7X7_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_7X7_RED_TEAM_LOC = new ArrayList();
   public static String GVG_7X7_CLEAR_LOC;
   public static int[] GVG_7X7_COORDS_SPECTATORS;
   public static boolean GVG_7X7_NO_AN;
   public static boolean GVG_7X7_NO_ENCHANT_SKILLS;
   public static int[] GVG_7X7_PROHIBITED_CLASS_IDS;
   public static int GVG_7X7_DUELIST_ALLOWED;
   public static int GVG_7X7_DREADNOUGHT_ALLOWED;
   public static int GVG_7X7_TANKER_ALLOWED;
   public static int GVG_7X7_DAGGER_ALLOWED;
   public static int GVG_7X7_ARCHER_ALLOWED;
   public static int GVG_7X7_HEALER_ALLOWED;
   public static int GVG_7X7_ARCHMAGE_ALLOWED;
   public static int GVG_7X7_SOULTAKER_ALLOWED;
   public static int GVG_7X7_MYSTICMOUSE_ALLOWED;
   public static int GVG_7X7_STORMSCREAMER_ALLOWED;
   public static int GVG_7X7_TITAN_ALLOWED;
   public static int GVG_7X7_DOMINATOR_ALLOWED;
   public static int GVG_7X7_DOOMCRYER_ALLOWED;
   public static boolean GVG_8X8_ENABLE;
   public static String GVG_8X8_START_TIME;
   public static int GVG_8X8_INSTANCE_ID;
   public static int GVG_8X8_MIN_LVL;
   public static int GVG_8X8_MAX_LVL;
   public static int GVG_8X8_COMMANDS_MIN;
   public static int GVG_8X8_COMMANDS_MAX;
   public static boolean GVG_8X8_RESTRICT_IP;
   public static boolean GVG_8X8_RESTRICT_HWID;
   public static boolean GVG_8X8_TO_ARENA;
   public static String[] GVG_8X8_ARENA_POINTS;
   public static boolean GVG_8X8_IN_PEACE;
   public static boolean GVG_8X8_PRE_CHECK;
   public static boolean GVG_8X8_BLOCK_MOVE;
   public static boolean GVG_8X8_SHOW_STATUS;
   public static int GVG_8X8_ROUNDS_WIN;
   public static int GVG_8X8_REG_TIME;
   public static int[] GVG_8X8_ANNOUNCE_REG_TIMES;
   public static int GVG_8X8_TIME_BATTLE;
   public static int GVG_8X8_HEAL_TP_BACK_TIME;
   public static boolean GVG_8X8_ALLOW_BATTLE_TIMER;
   public static int[] GVG_8X8_RETURN_POINT;
   public static int GVG_8X8_MANAGER_ID;
   public static byte GVG_8X8_MANAGER_SPAWN_TYPE;
   public static int[] GVG_8X8_MANAGER_COORDS;
   public static int[] GVG_8X8_RESTRICTED_SKILLS;
   public static int[] GVG_8X8_RESTRICTED_ITEMS;
   public static int[] GVG_8X8_RESTRICTED_SUMMONS;
   public static int GVG_8X8_BUFFER_ID;
   public static int GVG_8X8_TP_RANGE;
   public static String[] GVG_8X8_BUFFER1_COORDS;
   public static String[] GVG_8X8_BUFFER2_COORDS;
   public static boolean GVG_8X8_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_8X8_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_8X8_MAGE_BUFF;
   public static boolean GVG_8X8_CUSTOM_ITEMS_ENABLE;
   public static String GVG_8X8_CUSTOM_ITEMS_PATH;
   public static int GVG_8X8_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_8X8_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_8X8_ENCHANT_LIMIT;
   public static int GVG_8X8_ENCHANT_LIMIT_WEAPON;
   public static int GVG_8X8_ENCHANT_LIMIT_ARMOR;
   public static String GVG_8X8_CNAME_TEMPLATE;
   public static boolean GVG_8X8_ALLOW_SPEC;
   public static boolean GVG_8X8_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_8X8_REWARD;
   public static int[][] GVG_8X8_REWARD_PER_KILL;
   public static List<Location> GVG_8X8_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_8X8_RED_TEAM_LOC = new ArrayList();
   public static String GVG_8X8_CLEAR_LOC;
   public static int[] GVG_8X8_COORDS_SPECTATORS;
   public static boolean GVG_8X8_NO_AN;
   public static boolean GVG_8X8_NO_ENCHANT_SKILLS;
   public static int[] GVG_8X8_PROHIBITED_CLASS_IDS;
   public static int GVG_8X8_DUELIST_ALLOWED;
   public static int GVG_8X8_DREADNOUGHT_ALLOWED;
   public static int GVG_8X8_TANKER_ALLOWED;
   public static int GVG_8X8_DAGGER_ALLOWED;
   public static int GVG_8X8_ARCHER_ALLOWED;
   public static int GVG_8X8_HEALER_ALLOWED;
   public static int GVG_8X8_ARCHMAGE_ALLOWED;
   public static int GVG_8X8_SOULTAKER_ALLOWED;
   public static int GVG_8X8_MYSTICMOUSE_ALLOWED;
   public static int GVG_8X8_STORMSCREAMER_ALLOWED;
   public static int GVG_8X8_TITAN_ALLOWED;
   public static int GVG_8X8_DOMINATOR_ALLOWED;
   public static int GVG_8X8_DOOMCRYER_ALLOWED;
   public static boolean GVG_9X9_ENABLE;
   public static String GVG_9X9_START_TIME;
   public static int GVG_9X9_INSTANCE_ID;
   public static int GVG_9X9_MIN_LVL;
   public static int GVG_9X9_MAX_LVL;
   public static int GVG_9X9_COMMANDS_MIN;
   public static int GVG_9X9_COMMANDS_MAX;
   public static boolean GVG_9X9_RESTRICT_IP;
   public static boolean GVG_9X9_RESTRICT_HWID;
   public static boolean GVG_9X9_TO_ARENA;
   public static String[] GVG_9X9_ARENA_POINTS;
   public static boolean GVG_9X9_IN_PEACE;
   public static boolean GVG_9X9_PRE_CHECK;
   public static boolean GVG_9X9_BLOCK_MOVE;
   public static boolean GVG_9X9_SHOW_STATUS;
   public static int GVG_9X9_ROUNDS_WIN;
   public static int GVG_9X9_REG_TIME;
   public static int[] GVG_9X9_ANNOUNCE_REG_TIMES;
   public static int GVG_9X9_TIME_BATTLE;
   public static int GVG_9X9_HEAL_TP_BACK_TIME;
   public static boolean GVG_9X9_ALLOW_BATTLE_TIMER;
   public static int[] GVG_9X9_RETURN_POINT;
   public static int GVG_9X9_MANAGER_ID;
   public static byte GVG_9X9_MANAGER_SPAWN_TYPE;
   public static int[] GVG_9X9_MANAGER_COORDS;
   public static int[] GVG_9X9_RESTRICTED_SKILLS;
   public static int[] GVG_9X9_RESTRICTED_ITEMS;
   public static int[] GVG_9X9_RESTRICTED_SUMMONS;
   public static int GVG_9X9_BUFFER_ID;
   public static int GVG_9X9_TP_RANGE;
   public static String[] GVG_9X9_BUFFER1_COORDS;
   public static String[] GVG_9X9_BUFFER2_COORDS;
   public static boolean GVG_9X9_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_9X9_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_9X9_MAGE_BUFF;
   public static boolean GVG_9X9_CUSTOM_ITEMS_ENABLE;
   public static String GVG_9X9_CUSTOM_ITEMS_PATH;
   public static int GVG_9X9_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_9X9_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_9X9_ENCHANT_LIMIT;
   public static int GVG_9X9_ENCHANT_LIMIT_WEAPON;
   public static int GVG_9X9_ENCHANT_LIMIT_ARMOR;
   public static String GVG_9X9_CNAME_TEMPLATE;
   public static boolean GVG_9X9_ALLOW_SPEC;
   public static boolean GVG_9X9_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_9X9_REWARD;
   public static int[][] GVG_9X9_REWARD_PER_KILL;
   public static List<Location> GVG_9X9_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_9X9_RED_TEAM_LOC = new ArrayList();
   public static String GVG_9X9_CLEAR_LOC;
   public static int[] GVG_9X9_COORDS_SPECTATORS;
   public static boolean GVG_9X9_NO_AN;
   public static boolean GVG_9X9_NO_ENCHANT_SKILLS;
   public static int[] GVG_9X9_PROHIBITED_CLASS_IDS;
   public static int GVG_9X9_DUELIST_ALLOWED;
   public static int GVG_9X9_DREADNOUGHT_ALLOWED;
   public static int GVG_9X9_TANKER_ALLOWED;
   public static int GVG_9X9_DAGGER_ALLOWED;
   public static int GVG_9X9_ARCHER_ALLOWED;
   public static int GVG_9X9_HEALER_ALLOWED;
   public static int GVG_9X9_ARCHMAGE_ALLOWED;
   public static int GVG_9X9_SOULTAKER_ALLOWED;
   public static int GVG_9X9_MYSTICMOUSE_ALLOWED;
   public static int GVG_9X9_STORMSCREAMER_ALLOWED;
   public static int GVG_9X9_TITAN_ALLOWED;
   public static int GVG_9X9_DOMINATOR_ALLOWED;
   public static int GVG_9X9_DOOMCRYER_ALLOWED;
   public static boolean GVG_CXC_ENABLE;
   public static String GVG_CXC_START_TIME;
   public static int GVG_CXC_INSTANCE_ID;
   public static int GVG_CXC_MIN_LVL;
   public static int GVG_CXC_MAX_LVL;
   public static int GVG_CXC_COMMANDS_MIN;
   public static int GVG_CXC_COMMANDS_MAX;
   public static boolean GVG_CXC_RESTRICT_IP;
   public static boolean GVG_CXC_RESTRICT_HWID;
   public static boolean GVG_CXC_TO_ARENA;
   public static String[] GVG_CXC_ARENA_POINTS;
   public static boolean GVG_CXC_IN_PEACE;
   public static boolean GVG_CXC_PRE_CHECK;
   public static boolean GVG_CXC_BLOCK_MOVE;
   public static boolean GVG_CXC_SHOW_STATUS;
   public static int GVG_CXC_ROUNDS_WIN;
   public static int GVG_CXC_REG_TIME;
   public static int[] GVG_CXC_ANNOUNCE_REG_TIMES;
   public static int GVG_CXC_TIME_BATTLE;
   public static int GVG_CXC_HEAL_TP_BACK_TIME;
   public static boolean GVG_CXC_ALLOW_BATTLE_TIMER;
   public static int[] GVG_CXC_RETURN_POINT;
   public static int GVG_CXC_MANAGER_ID;
   public static byte GVG_CXC_MANAGER_SPAWN_TYPE;
   public static int[] GVG_CXC_MANAGER_COORDS;
   public static int[] GVG_CXC_RESTRICTED_SKILLS;
   public static int[] GVG_CXC_RESTRICTED_ITEMS;
   public static int[] GVG_CXC_RESTRICTED_SUMMONS;
   public static int GVG_CXC_BUFFER_ID;
   public static int GVG_CXC_TP_RANGE;
   public static String[] GVG_CXC_BUFFER1_COORDS;
   public static String[] GVG_CXC_BUFFER2_COORDS;
   public static boolean GVG_CXC_ENABLE_BUFFS;
   public static List<Pair<Integer, Integer>> GVG_CXC_WARRIOR_BUFF;
   public static List<Pair<Integer, Integer>> GVG_CXC_MAGE_BUFF;
   public static boolean GVG_CXC_CUSTOM_ITEMS_ENABLE;
   public static String GVG_CXC_CUSTOM_ITEMS_PATH;
   public static int GVG_CXC_CUSTOM_ITEMS_ENCHANT_WEAPON;
   public static int GVG_CXC_CUSTOM_ITEMS_ENCHANT_ARMOR;
   public static boolean GVG_CXC_ENCHANT_LIMIT;
   public static int GVG_CXC_ENCHANT_LIMIT_WEAPON;
   public static int GVG_CXC_ENCHANT_LIMIT_ARMOR;
   public static String GVG_CXC_CNAME_TEMPLATE;
   public static boolean GVG_CXC_ALLOW_SPEC;
   public static boolean GVG_CXC_PROHIBIT_PARTICIPANTS_SPEC;
   public static int[][] GVG_CXC_REWARD;
   public static int[][] GVG_CXC_REWARD_PER_KILL;
   public static List<Location> GVG_CXC_BLUE_TEAM_LOC = new ArrayList();
   public static List<Location> GVG_CXC_RED_TEAM_LOC = new ArrayList();
   public static String GVG_CXC_CLEAR_LOC;
   public static int[] GVG_CXC_COORDS_SPECTATORS;
   public static boolean GVG_CXC_NO_AN;
   public static boolean GVG_CXC_NO_ENCHANT_SKILLS;
   public static int[] GVG_CXC_PROHIBITED_CLASS_IDS;
   public static int GVG_CXC_DUELIST_ALLOWED;
   public static int GVG_CXC_DREADNOUGHT_ALLOWED;
   public static int GVG_CXC_TANKER_ALLOWED;
   public static int GVG_CXC_DAGGER_ALLOWED;
   public static int GVG_CXC_ARCHER_ALLOWED;
   public static int GVG_CXC_HEALER_ALLOWED;
   public static int GVG_CXC_ARCHMAGE_ALLOWED;
   public static int GVG_CXC_SOULTAKER_ALLOWED;
   public static int GVG_CXC_MYSTICMOUSE_ALLOWED;
   public static int GVG_CXC_STORMSCREAMER_ALLOWED;
   public static int GVG_CXC_TITAN_ALLOWED;
   public static int GVG_CXC_DOMINATOR_ALLOWED;
   public static int GVG_CXC_DOOMCRYER_ALLOWED;

   public static void load() {
      load1x1();
      load2x2();
      load3x3();
      load4x4();
      load5x5();
      load6x6();
      load7x7();
      load8x8();
      load9x9();
      loadCxC();
   }

   public static void load1x1() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvg1x1.properties");
      _log.info("Loading: config/events/gvg/battlegvg1x1.properties.");

      try {
         GVG_1X1_ENABLE = gvgSettings.getProperty("Gvg1x1Enable", true);
         GVG_1X1_START_TIME = gvgSettings.getProperty("Gvg1x1StartTime", "00 21 * * 3");
         GVG_1X1_INSTANCE_ID = gvgSettings.getProperty("Gvg1x1InstanceId", 401);
         GVG_1X1_MIN_LVL = gvgSettings.getProperty("Gvg1x1MinLvl", 76);
         GVG_1X1_MAX_LVL = gvgSettings.getProperty("Gvg1x1MaxLvl", 80);
         GVG_1X1_COMMANDS_MIN = gvgSettings.getProperty("Gvg1x1CommandsMin", 2);
         GVG_1X1_COMMANDS_MAX = gvgSettings.getProperty("Gvg1x1CommandsMax", 35);
         GVG_1X1_RESTRICT_IP = gvgSettings.getProperty("Gvg1x1RestrictIp", false);
         GVG_1X1_RESTRICT_HWID = gvgSettings.getProperty("Gvg1x1RestrictHwid", false);
         GVG_1X1_TO_ARENA = gvgSettings.getProperty("Gvg1x1ToArena", false);
         GVG_1X1_ARENA_POINTS = gvgSettings.getProperty("Gvg1x1ArenaPoints", "").split(";");
         GVG_1X1_IN_PEACE = gvgSettings.getProperty("Gvg1x1InPeace", false);
         GVG_1X1_PRE_CHECK = gvgSettings.getProperty("Gvg1x1PreCheck", true);
         GVG_1X1_BLOCK_MOVE = gvgSettings.getProperty("Gvg1x1BlockMove", true);
         GVG_1X1_SHOW_STATUS = gvgSettings.getProperty("Gvg1x1ShowStatus", true);
         GVG_1X1_ROUNDS_WIN = gvgSettings.getProperty("Gvg1x1RoundsWin", 2);
         GVG_1X1_REG_TIME = gvgSettings.getProperty("Gvg1x1RegTime", 15);
         GVG_1X1_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("Gvg1x1AnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_1X1_TIME_BATTLE = gvgSettings.getProperty("Gvg1x1TimeBattle", 5);
         GVG_1X1_HEAL_TP_BACK_TIME = gvgSettings.getProperty("Gvg1x1HealTpBackTime", 10);
         GVG_1X1_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("Gvg1x1AllowBattleTimer", false);
         GVG_1X1_RETURN_POINT = gvgSettings.getProperty("Gvg1x1ReturnPoint", new int[0]);
         GVG_1X1_MANAGER_ID = gvgSettings.getProperty("Gvg1x1ManagerId", 0);
         GVG_1X1_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("Gvg1x1ManagerSpawnType", 0);
         GVG_1X1_MANAGER_COORDS = gvgSettings.getProperty("Gvg1x1ManagerCoords", new int[0]);
         GVG_1X1_RESTRICTED_SKILLS = gvgSettings.getProperty("Gvg1x1RestrictedSkills", new int[0]);
         GVG_1X1_RESTRICTED_ITEMS = gvgSettings.getProperty("Gvg1x1RestrictedItems", new int[0]);
         GVG_1X1_RESTRICTED_SUMMONS = gvgSettings.getProperty("Gvg1x1RestrictedSummons", new int[0]);
         GVG_1X1_BUFFER_ID = gvgSettings.getProperty("Gvg1x1BufferId", 0);
         GVG_1X1_TP_RANGE = gvgSettings.getProperty("Gvg1x1TpRange", 148);
         GVG_1X1_BUFFER1_COORDS = gvgSettings.getProperty("Gvg1x1Buffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_1X1_BUFFER2_COORDS = gvgSettings.getProperty("Gvg1x1Buffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_1X1_ENABLE_BUFFS = gvgSettings.getProperty("Gvg1x1EnableBuffs", false);
         GVG_1X1_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("Gvg1x1MageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_1X1_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_1X1_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("Gvg1x1WarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_1X1_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_1X1_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("Gvg1x1CustomItemsEnable", false);
         GVG_1X1_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("Gvg1x1CustomItemsPath", "config/events/gvg/battlegvg_items1x1.xml");
         GVG_1X1_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("Gvg1x1CustomItemsEnchantWeapon", 6);
         GVG_1X1_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("Gvg1x1CustomItemsEnchantArmor", 6);
         GVG_1X1_ENCHANT_LIMIT = gvgSettings.getProperty("Gvg1x1EnchantLimit", false);
         GVG_1X1_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("Gvg1x1EnchantLimitWeapon", 6);
         GVG_1X1_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("Gvg1x1EnchantLimitArmor", 6);
         GVG_1X1_CNAME_TEMPLATE = gvgSettings.getProperty("Gvg1x1CNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_1X1_ALLOW_SPEC = gvgSettings.getProperty("Gvg1x1AllowSpec", false);
         GVG_1X1_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("Gvg1x1ProhibitParticipantsSpec", true);
         GVG_1X1_REWARD = stringToIntArray2X(gvgSettings.getProperty("Gvg1x1Reward", ""), ";", ",");
         GVG_1X1_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("Gvg1x1RewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("Gvg1x1BlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_1X1_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("Gvg1x1RedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_1X1_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_1X1_CLEAR_LOC = gvgSettings.getProperty("Gvg1x1ClearLoc", "82698,148638,-3473");
         GVG_1X1_COORDS_SPECTATORS = gvgSettings.getProperty("Gvg1x1CoordsSpectators", new int[0]);
         GVG_1X1_NO_AN = gvgSettings.getProperty("Gvg1x1NoAn", false);
         GVG_1X1_NO_ENCHANT_SKILLS = gvgSettings.getProperty("Gvg1x1NoEnchantSkills", false);
         GVG_1X1_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("Gvg1x1ProhibitedClassIds", new int[0]);
         GVG_1X1_DUELIST_ALLOWED = gvgSettings.getProperty("Gvg1x1DuelistAllowed", 1);
         GVG_1X1_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("Gvg1x1DreadnoughtAllowed", 1);
         GVG_1X1_TANKER_ALLOWED = gvgSettings.getProperty("Gvg1x1TankerAllowed", 1);
         GVG_1X1_DAGGER_ALLOWED = gvgSettings.getProperty("Gvg1x1DaggerAllowed", 1);
         GVG_1X1_ARCHER_ALLOWED = gvgSettings.getProperty("Gvg1x1ArcherAllowed", 1);
         GVG_1X1_HEALER_ALLOWED = gvgSettings.getProperty("Gvg1x1HealerAllowed", 1);
         GVG_1X1_ARCHMAGE_ALLOWED = gvgSettings.getProperty("Gvg1x1ArchmageAllowed", 1);
         GVG_1X1_SOULTAKER_ALLOWED = gvgSettings.getProperty("Gvg1x1SoultakerAllowed", 1);
         GVG_1X1_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("Gvg1x1MysticMouseAllowed", 1);
         GVG_1X1_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("Gvg1x1StormScreamerAllowed", 1);
         GVG_1X1_TITAN_ALLOWED = gvgSettings.getProperty("Gvg1x1TitanAllowed", 1);
         GVG_1X1_DOMINATOR_ALLOWED = gvgSettings.getProperty("Gvg1x1DominatorAllowed", 1);
         GVG_1X1_DOOMCRYER_ALLOWED = gvgSettings.getProperty("Gvg1x1DoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvg1x1.properties File.");
      }
   }

   public static void load2x2() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvg2x2.properties");
      _log.info("Loading: config/events/gvg/battlegvg2x2.properties.");

      try {
         GVG_2X2_ENABLE = gvgSettings.getProperty("Gvg2x2Enable", true);
         GVG_2X2_START_TIME = gvgSettings.getProperty("Gvg2x2StartTime", "00 21 * * 3");
         GVG_2X2_INSTANCE_ID = gvgSettings.getProperty("Gvg2x2InstanceId", 401);
         GVG_2X2_MIN_LVL = gvgSettings.getProperty("Gvg2x2MinLvl", 76);
         GVG_2X2_MAX_LVL = gvgSettings.getProperty("Gvg2x2MaxLvl", 80);
         GVG_2X2_COMMANDS_MIN = gvgSettings.getProperty("Gvg2x2CommandsMin", 2);
         GVG_2X2_COMMANDS_MAX = gvgSettings.getProperty("Gvg2x2CommandsMax", 35);
         GVG_2X2_RESTRICT_IP = gvgSettings.getProperty("Gvg2x2RestrictIp", false);
         GVG_2X2_RESTRICT_HWID = gvgSettings.getProperty("Gvg2x2RestrictHwid", false);
         GVG_2X2_TO_ARENA = gvgSettings.getProperty("Gvg2x2ToArena", false);
         GVG_2X2_ARENA_POINTS = gvgSettings.getProperty("Gvg2x2ArenaPoints", "").split(";");
         GVG_2X2_IN_PEACE = gvgSettings.getProperty("Gvg2x2InPeace", false);
         GVG_2X2_PRE_CHECK = gvgSettings.getProperty("Gvg2x2PreCheck", true);
         GVG_2X2_BLOCK_MOVE = gvgSettings.getProperty("Gvg2x2BlockMove", true);
         GVG_2X2_SHOW_STATUS = gvgSettings.getProperty("Gvg2x2ShowStatus", true);
         GVG_2X2_ROUNDS_WIN = gvgSettings.getProperty("Gvg2x2RoundsWin", 2);
         GVG_2X2_REG_TIME = gvgSettings.getProperty("Gvg2x2RegTime", 15);
         GVG_2X2_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("Gvg2x2AnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_2X2_TIME_BATTLE = gvgSettings.getProperty("Gvg2x2TimeBattle", 5);
         GVG_2X2_HEAL_TP_BACK_TIME = gvgSettings.getProperty("Gvg2x2HealTpBackTime", 10);
         GVG_2X2_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("Gvg2x2AllowBattleTimer", false);
         GVG_2X2_RETURN_POINT = gvgSettings.getProperty("Gvg2x2ReturnPoint", new int[0]);
         GVG_2X2_MANAGER_ID = gvgSettings.getProperty("Gvg2x2ManagerId", 0);
         GVG_2X2_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("Gvg2x2ManagerSpawnType", 0);
         GVG_2X2_MANAGER_COORDS = gvgSettings.getProperty("Gvg2x2ManagerCoords", new int[0]);
         GVG_2X2_RESTRICTED_SKILLS = gvgSettings.getProperty("Gvg2x2RestrictedSkills", new int[0]);
         GVG_2X2_RESTRICTED_ITEMS = gvgSettings.getProperty("Gvg2x2RestrictedItems", new int[0]);
         GVG_2X2_RESTRICTED_SUMMONS = gvgSettings.getProperty("Gvg2x2RestrictedSummons", new int[0]);
         GVG_2X2_BUFFER_ID = gvgSettings.getProperty("Gvg2x2BufferId", 0);
         GVG_2X2_TP_RANGE = gvgSettings.getProperty("Gvg2x2TpRange", 148);
         GVG_2X2_BUFFER1_COORDS = gvgSettings.getProperty("Gvg2x2Buffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_2X2_BUFFER2_COORDS = gvgSettings.getProperty("Gvg2x2Buffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_2X2_ENABLE_BUFFS = gvgSettings.getProperty("Gvg2x2EnableBuffs", false);
         GVG_2X2_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("Gvg2x2MageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_2X2_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_2X2_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("Gvg2x2WarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_2X2_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_2X2_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("Gvg2x2CustomItemsEnable", false);
         GVG_2X2_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("Gvg2x2CustomItemsPath", "config/events/gvg/battlegvg_items2x2.xml");
         GVG_2X2_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("Gvg2x2CustomItemsEnchantWeapon", 6);
         GVG_2X2_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("Gvg2x2CustomItemsEnchantArmor", 6);
         GVG_2X2_ENCHANT_LIMIT = gvgSettings.getProperty("Gvg2x2EnchantLimit", false);
         GVG_2X2_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("Gvg2x2EnchantLimitWeapon", 6);
         GVG_2X2_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("Gvg2x2EnchantLimitArmor", 6);
         GVG_2X2_CNAME_TEMPLATE = gvgSettings.getProperty("Gvg2x2CNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_2X2_ALLOW_SPEC = gvgSettings.getProperty("Gvg2x2AllowSpec", false);
         GVG_2X2_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("Gvg2x2ProhibitParticipantsSpec", true);
         GVG_2X2_REWARD = stringToIntArray2X(gvgSettings.getProperty("Gvg2x2Reward", ""), ";", ",");
         GVG_2X2_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("Gvg2x2RewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("Gvg2x2BlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_2X2_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("Gvg2x2RedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_2X2_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_2X2_CLEAR_LOC = gvgSettings.getProperty("Gvg2x2ClearLoc", "82698,148638,-3473");
         GVG_2X2_COORDS_SPECTATORS = gvgSettings.getProperty("Gvg2x2CoordsSpectators", new int[0]);
         GVG_2X2_NO_AN = gvgSettings.getProperty("Gvg2x2NoAn", false);
         GVG_2X2_NO_ENCHANT_SKILLS = gvgSettings.getProperty("Gvg2x2NoEnchantSkills", false);
         GVG_2X2_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("Gvg2x2ProhibitedClassIds", new int[0]);
         GVG_2X2_DUELIST_ALLOWED = gvgSettings.getProperty("Gvg2x2DuelistAllowed", 1);
         GVG_2X2_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("Gvg2x2DreadnoughtAllowed", 1);
         GVG_2X2_TANKER_ALLOWED = gvgSettings.getProperty("Gvg2x2TankerAllowed", 1);
         GVG_2X2_DAGGER_ALLOWED = gvgSettings.getProperty("Gvg2x2DaggerAllowed", 1);
         GVG_2X2_ARCHER_ALLOWED = gvgSettings.getProperty("Gvg2x2ArcherAllowed", 1);
         GVG_2X2_HEALER_ALLOWED = gvgSettings.getProperty("Gvg2x2HealerAllowed", 1);
         GVG_2X2_ARCHMAGE_ALLOWED = gvgSettings.getProperty("Gvg2x2ArchmageAllowed", 1);
         GVG_2X2_SOULTAKER_ALLOWED = gvgSettings.getProperty("Gvg2x2SoultakerAllowed", 1);
         GVG_2X2_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("Gvg2x2MysticMouseAllowed", 1);
         GVG_2X2_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("Gvg2x2StormScreamerAllowed", 1);
         GVG_2X2_TITAN_ALLOWED = gvgSettings.getProperty("Gvg2x2TitanAllowed", 1);
         GVG_2X2_DOMINATOR_ALLOWED = gvgSettings.getProperty("Gvg2x2DominatorAllowed", 1);
         GVG_2X2_DOOMCRYER_ALLOWED = gvgSettings.getProperty("Gvg2x2DoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvg2x2.properties File.");
      }
   }

   public static void load3x3() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvg3x3.properties");
      _log.info("Loading: config/events/gvg/battlegvg3x3.properties.");

      try {
         GVG_3X3_ENABLE = gvgSettings.getProperty("Gvg3x3Enable", true);
         GVG_3X3_START_TIME = gvgSettings.getProperty("Gvg3x3StartTime", "00 21 * * 3");
         GVG_3X3_INSTANCE_ID = gvgSettings.getProperty("Gvg3x3InstanceId", 401);
         GVG_3X3_MIN_LVL = gvgSettings.getProperty("Gvg3x3MinLvl", 76);
         GVG_3X3_MAX_LVL = gvgSettings.getProperty("Gvg3x3MaxLvl", 80);
         GVG_3X3_COMMANDS_MIN = gvgSettings.getProperty("Gvg3x3CommandsMin", 2);
         GVG_3X3_COMMANDS_MAX = gvgSettings.getProperty("Gvg3x3CommandsMax", 35);
         GVG_3X3_RESTRICT_IP = gvgSettings.getProperty("Gvg3x3RestrictIp", false);
         GVG_3X3_RESTRICT_HWID = gvgSettings.getProperty("Gvg3x3RestrictHwid", false);
         GVG_3X3_TO_ARENA = gvgSettings.getProperty("Gvg3x3ToArena", false);
         GVG_3X3_ARENA_POINTS = gvgSettings.getProperty("Gvg3x3ArenaPoints", "").split(";");
         GVG_3X3_IN_PEACE = gvgSettings.getProperty("Gvg3x3InPeace", false);
         GVG_3X3_PRE_CHECK = gvgSettings.getProperty("Gvg3x3PreCheck", true);
         GVG_3X3_BLOCK_MOVE = gvgSettings.getProperty("Gvg3x3BlockMove", true);
         GVG_3X3_SHOW_STATUS = gvgSettings.getProperty("Gvg3x3ShowStatus", true);
         GVG_3X3_ROUNDS_WIN = gvgSettings.getProperty("Gvg3x3RoundsWin", 2);
         GVG_3X3_REG_TIME = gvgSettings.getProperty("Gvg3x3RegTime", 15);
         GVG_3X3_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("Gvg3x3AnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_3X3_TIME_BATTLE = gvgSettings.getProperty("Gvg3x3TimeBattle", 5);
         GVG_3X3_HEAL_TP_BACK_TIME = gvgSettings.getProperty("Gvg3x3HealTpBackTime", 10);
         GVG_3X3_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("Gvg3x3AllowBattleTimer", false);
         GVG_3X3_RETURN_POINT = gvgSettings.getProperty("Gvg3x3ReturnPoint", new int[0]);
         GVG_3X3_MANAGER_ID = gvgSettings.getProperty("Gvg3x3ManagerId", 0);
         GVG_3X3_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("Gvg3x3ManagerSpawnType", 0);
         GVG_3X3_MANAGER_COORDS = gvgSettings.getProperty("Gvg3x3ManagerCoords", new int[0]);
         GVG_3X3_RESTRICTED_SKILLS = gvgSettings.getProperty("Gvg3x3RestrictedSkills", new int[0]);
         GVG_3X3_RESTRICTED_ITEMS = gvgSettings.getProperty("Gvg3x3RestrictedItems", new int[0]);
         GVG_3X3_RESTRICTED_SUMMONS = gvgSettings.getProperty("Gvg3x3RestrictedSummons", new int[0]);
         GVG_3X3_BUFFER_ID = gvgSettings.getProperty("Gvg3x3BufferId", 0);
         GVG_3X3_TP_RANGE = gvgSettings.getProperty("Gvg3x3TpRange", 148);
         GVG_3X3_BUFFER1_COORDS = gvgSettings.getProperty("Gvg3x3Buffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_3X3_BUFFER2_COORDS = gvgSettings.getProperty("Gvg3x3Buffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_3X3_ENABLE_BUFFS = gvgSettings.getProperty("Gvg3x3EnableBuffs", false);
         GVG_3X3_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("Gvg3x3MageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_3X3_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_3X3_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("Gvg3x3WarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_3X3_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_3X3_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("Gvg3x3CustomItemsEnable", false);
         GVG_3X3_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("Gvg3x3CustomItemsPath", "config/events/gvg/battlegvg_items3x3.xml");
         GVG_3X3_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("Gvg3x3CustomItemsEnchantWeapon", 6);
         GVG_3X3_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("Gvg3x3CustomItemsEnchantArmor", 6);
         GVG_3X3_ENCHANT_LIMIT = gvgSettings.getProperty("Gvg3x3EnchantLimit", false);
         GVG_3X3_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("Gvg3x3EnchantLimitWeapon", 6);
         GVG_3X3_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("Gvg3x3EnchantLimitArmor", 6);
         GVG_3X3_CNAME_TEMPLATE = gvgSettings.getProperty("Gvg3x3CNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_3X3_ALLOW_SPEC = gvgSettings.getProperty("Gvg3x3AllowSpec", false);
         GVG_3X3_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("Gvg3x3ProhibitParticipantsSpec", true);
         GVG_3X3_REWARD = stringToIntArray2X(gvgSettings.getProperty("Gvg3x3Reward", ""), ";", ",");
         GVG_3X3_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("Gvg3x3RewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("Gvg3x3BlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_3X3_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("Gvg3x3RedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_3X3_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_3X3_CLEAR_LOC = gvgSettings.getProperty("Gvg3x3ClearLoc", "82698,148638,-3473");
         GVG_3X3_COORDS_SPECTATORS = gvgSettings.getProperty("Gvg3x3CoordsSpectators", new int[0]);
         GVG_3X3_NO_AN = gvgSettings.getProperty("Gvg3x3NoAn", false);
         GVG_3X3_NO_ENCHANT_SKILLS = gvgSettings.getProperty("Gvg3x3NoEnchantSkills", false);
         GVG_3X3_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("Gvg3x3ProhibitedClassIds", new int[0]);
         GVG_3X3_DUELIST_ALLOWED = gvgSettings.getProperty("Gvg3x3DuelistAllowed", 1);
         GVG_3X3_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("Gvg3x3DreadnoughtAllowed", 1);
         GVG_3X3_TANKER_ALLOWED = gvgSettings.getProperty("Gvg3x3TankerAllowed", 1);
         GVG_3X3_DAGGER_ALLOWED = gvgSettings.getProperty("Gvg3x3DaggerAllowed", 1);
         GVG_3X3_ARCHER_ALLOWED = gvgSettings.getProperty("Gvg3x3ArcherAllowed", 1);
         GVG_3X3_HEALER_ALLOWED = gvgSettings.getProperty("Gvg3x3HealerAllowed", 1);
         GVG_3X3_ARCHMAGE_ALLOWED = gvgSettings.getProperty("Gvg3x3ArchmageAllowed", 1);
         GVG_3X3_SOULTAKER_ALLOWED = gvgSettings.getProperty("Gvg3x3SoultakerAllowed", 1);
         GVG_3X3_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("Gvg3x3MysticMouseAllowed", 1);
         GVG_3X3_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("Gvg3x3StormScreamerAllowed", 1);
         GVG_3X3_TITAN_ALLOWED = gvgSettings.getProperty("Gvg3x3TitanAllowed", 1);
         GVG_3X3_DOMINATOR_ALLOWED = gvgSettings.getProperty("Gvg3x3DominatorAllowed", 1);
         GVG_3X3_DOOMCRYER_ALLOWED = gvgSettings.getProperty("Gvg3x3DoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvg3x3.properties File.");
      }
   }

   public static void load4x4() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvg4x4.properties");
      _log.info("Loading: config/events/gvg/battlegvg4x4.properties.");

      try {
         GVG_4X4_ENABLE = gvgSettings.getProperty("Gvg4x4Enable", true);
         GVG_4X4_START_TIME = gvgSettings.getProperty("Gvg4x4StartTime", "00 21 * * 3");
         GVG_4X4_INSTANCE_ID = gvgSettings.getProperty("Gvg4x4InstanceId", 401);
         GVG_4X4_MIN_LVL = gvgSettings.getProperty("Gvg4x4MinLvl", 76);
         GVG_4X4_MAX_LVL = gvgSettings.getProperty("Gvg4x4MaxLvl", 80);
         GVG_4X4_COMMANDS_MIN = gvgSettings.getProperty("Gvg4x4CommandsMin", 2);
         GVG_4X4_COMMANDS_MAX = gvgSettings.getProperty("Gvg4x4CommandsMax", 35);
         GVG_4X4_RESTRICT_IP = gvgSettings.getProperty("Gvg4x4RestrictIp", false);
         GVG_4X4_RESTRICT_HWID = gvgSettings.getProperty("Gvg4x4RestrictHwid", false);
         GVG_4X4_TO_ARENA = gvgSettings.getProperty("Gvg4x4ToArena", false);
         GVG_4X4_ARENA_POINTS = gvgSettings.getProperty("Gvg4x4ArenaPoints", "").split(";");
         GVG_4X4_IN_PEACE = gvgSettings.getProperty("Gvg4x4InPeace", false);
         GVG_4X4_PRE_CHECK = gvgSettings.getProperty("Gvg4x4PreCheck", true);
         GVG_4X4_BLOCK_MOVE = gvgSettings.getProperty("Gvg4x4BlockMove", true);
         GVG_4X4_SHOW_STATUS = gvgSettings.getProperty("Gvg4x4ShowStatus", true);
         GVG_4X4_ROUNDS_WIN = gvgSettings.getProperty("Gvg4x4RoundsWin", 2);
         GVG_4X4_REG_TIME = gvgSettings.getProperty("Gvg4x4RegTime", 15);
         GVG_4X4_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("Gvg4x4AnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_4X4_TIME_BATTLE = gvgSettings.getProperty("Gvg4x4TimeBattle", 5);
         GVG_4X4_HEAL_TP_BACK_TIME = gvgSettings.getProperty("Gvg4x4HealTpBackTime", 10);
         GVG_4X4_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("Gvg4x4AllowBattleTimer", false);
         GVG_4X4_RETURN_POINT = gvgSettings.getProperty("Gvg4x4ReturnPoint", new int[0]);
         GVG_4X4_MANAGER_ID = gvgSettings.getProperty("Gvg4x4ManagerId", 0);
         GVG_4X4_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("Gvg4x4ManagerSpawnType", 0);
         GVG_4X4_MANAGER_COORDS = gvgSettings.getProperty("Gvg4x4ManagerCoords", new int[0]);
         GVG_4X4_RESTRICTED_SKILLS = gvgSettings.getProperty("Gvg4x4RestrictedSkills", new int[0]);
         GVG_4X4_RESTRICTED_ITEMS = gvgSettings.getProperty("Gvg4x4RestrictedItems", new int[0]);
         GVG_4X4_RESTRICTED_SUMMONS = gvgSettings.getProperty("Gvg4x4RestrictedSummons", new int[0]);
         GVG_4X4_BUFFER_ID = gvgSettings.getProperty("Gvg4x4BufferId", 0);
         GVG_4X4_TP_RANGE = gvgSettings.getProperty("Gvg4x4TpRange", 148);
         GVG_4X4_BUFFER1_COORDS = gvgSettings.getProperty("Gvg4x4Buffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_4X4_BUFFER2_COORDS = gvgSettings.getProperty("Gvg4x4Buffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_4X4_ENABLE_BUFFS = gvgSettings.getProperty("Gvg4x4EnableBuffs", false);
         GVG_4X4_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("Gvg4x4MageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_4X4_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_4X4_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("Gvg4x4WarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_4X4_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_4X4_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("Gvg4x4CustomItemsEnable", false);
         GVG_4X4_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("Gvg4x4CustomItemsPath", "config/events/gvg/battlegvg_items4x4.xml");
         GVG_4X4_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("Gvg4x4CustomItemsEnchantWeapon", 6);
         GVG_4X4_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("Gvg4x4CustomItemsEnchantArmor", 6);
         GVG_4X4_ENCHANT_LIMIT = gvgSettings.getProperty("Gvg4x4EnchantLimit", false);
         GVG_4X4_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("Gvg4x4EnchantLimitWeapon", 6);
         GVG_4X4_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("Gvg4x4EnchantLimitArmor", 6);
         GVG_4X4_CNAME_TEMPLATE = gvgSettings.getProperty("Gvg4x4CNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_4X4_ALLOW_SPEC = gvgSettings.getProperty("Gvg4x4AllowSpec", false);
         GVG_4X4_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("Gvg4x4ProhibitParticipantsSpec", true);
         GVG_4X4_REWARD = stringToIntArray2X(gvgSettings.getProperty("Gvg4x4Reward", ""), ";", ",");
         GVG_4X4_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("Gvg4x4RewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("Gvg4x4BlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_4X4_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("Gvg4x4RedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_4X4_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_4X4_CLEAR_LOC = gvgSettings.getProperty("Gvg4x4ClearLoc", "82698,148638,-3473");
         GVG_4X4_COORDS_SPECTATORS = gvgSettings.getProperty("Gvg4x4CoordsSpectators", new int[0]);
         GVG_4X4_NO_AN = gvgSettings.getProperty("Gvg4x4NoAn", false);
         GVG_4X4_NO_ENCHANT_SKILLS = gvgSettings.getProperty("Gvg4x4NoEnchantSkills", false);
         GVG_4X4_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("Gvg4x4ProhibitedClassIds", new int[0]);
         GVG_4X4_DUELIST_ALLOWED = gvgSettings.getProperty("Gvg4x4DuelistAllowed", 1);
         GVG_4X4_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("Gvg4x4DreadnoughtAllowed", 1);
         GVG_4X4_TANKER_ALLOWED = gvgSettings.getProperty("Gvg4x4TankerAllowed", 1);
         GVG_4X4_DAGGER_ALLOWED = gvgSettings.getProperty("Gvg4x4DaggerAllowed", 1);
         GVG_4X4_ARCHER_ALLOWED = gvgSettings.getProperty("Gvg4x4ArcherAllowed", 1);
         GVG_4X4_HEALER_ALLOWED = gvgSettings.getProperty("Gvg4x4HealerAllowed", 1);
         GVG_4X4_ARCHMAGE_ALLOWED = gvgSettings.getProperty("Gvg4x4ArchmageAllowed", 1);
         GVG_4X4_SOULTAKER_ALLOWED = gvgSettings.getProperty("Gvg4x4SoultakerAllowed", 1);
         GVG_4X4_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("Gvg4x4MysticMouseAllowed", 1);
         GVG_4X4_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("Gvg4x4StormScreamerAllowed", 1);
         GVG_4X4_TITAN_ALLOWED = gvgSettings.getProperty("Gvg4x4TitanAllowed", 1);
         GVG_4X4_DOMINATOR_ALLOWED = gvgSettings.getProperty("Gvg4x4DominatorAllowed", 1);
         GVG_4X4_DOOMCRYER_ALLOWED = gvgSettings.getProperty("Gvg4x4DoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvg4x4.properties File.");
      }
   }

   public static void load5x5() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvg5x5.properties");
      _log.info("Loading: config/events/gvg/battlegvg5x5.properties.");

      try {
         GVG_5X5_ENABLE = gvgSettings.getProperty("Gvg5x5Enable", true);
         GVG_5X5_START_TIME = gvgSettings.getProperty("Gvg5x5StartTime", "00 21 * * 3");
         GVG_5X5_INSTANCE_ID = gvgSettings.getProperty("Gvg5x5InstanceId", 401);
         GVG_5X5_MIN_LVL = gvgSettings.getProperty("Gvg5x5MinLvl", 76);
         GVG_5X5_MAX_LVL = gvgSettings.getProperty("Gvg5x5MaxLvl", 80);
         GVG_5X5_COMMANDS_MIN = gvgSettings.getProperty("Gvg5x5CommandsMin", 2);
         GVG_5X5_COMMANDS_MAX = gvgSettings.getProperty("Gvg5x5CommandsMax", 35);
         GVG_5X5_RESTRICT_IP = gvgSettings.getProperty("Gvg5x5RestrictIp", false);
         GVG_5X5_RESTRICT_HWID = gvgSettings.getProperty("Gvg5x5RestrictHwid", false);
         GVG_5X5_TO_ARENA = gvgSettings.getProperty("Gvg5x5ToArena", false);
         GVG_5X5_ARENA_POINTS = gvgSettings.getProperty("Gvg5x5ArenaPoints", "").split(";");
         GVG_5X5_IN_PEACE = gvgSettings.getProperty("Gvg5x5InPeace", false);
         GVG_5X5_PRE_CHECK = gvgSettings.getProperty("Gvg5x5PreCheck", true);
         GVG_5X5_BLOCK_MOVE = gvgSettings.getProperty("Gvg5x5BlockMove", true);
         GVG_5X5_SHOW_STATUS = gvgSettings.getProperty("Gvg5x5ShowStatus", true);
         GVG_5X5_ROUNDS_WIN = gvgSettings.getProperty("Gvg5x5RoundsWin", 2);
         GVG_5X5_REG_TIME = gvgSettings.getProperty("Gvg5x5RegTime", 15);
         GVG_5X5_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("Gvg5x5AnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_5X5_TIME_BATTLE = gvgSettings.getProperty("Gvg5x5TimeBattle", 5);
         GVG_5X5_HEAL_TP_BACK_TIME = gvgSettings.getProperty("Gvg5x5HealTpBackTime", 10);
         GVG_5X5_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("Gvg5x5AllowBattleTimer", false);
         GVG_5X5_RETURN_POINT = gvgSettings.getProperty("Gvg5x5ReturnPoint", new int[0]);
         GVG_5X5_MANAGER_ID = gvgSettings.getProperty("Gvg5x5ManagerId", 0);
         GVG_5X5_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("Gvg5x5ManagerSpawnType", 0);
         GVG_5X5_MANAGER_COORDS = gvgSettings.getProperty("Gvg5x5ManagerCoords", new int[0]);
         GVG_5X5_RESTRICTED_SKILLS = gvgSettings.getProperty("Gvg5x5RestrictedSkills", new int[0]);
         GVG_5X5_RESTRICTED_ITEMS = gvgSettings.getProperty("Gvg5x5RestrictedItems", new int[0]);
         GVG_5X5_RESTRICTED_SUMMONS = gvgSettings.getProperty("Gvg5x5RestrictedSummons", new int[0]);
         GVG_5X5_BUFFER_ID = gvgSettings.getProperty("Gvg5x5BufferId", 0);
         GVG_5X5_TP_RANGE = gvgSettings.getProperty("Gvg5x5TpRange", 148);
         GVG_5X5_BUFFER1_COORDS = gvgSettings.getProperty("Gvg5x5Buffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_5X5_BUFFER2_COORDS = gvgSettings.getProperty("Gvg5x5Buffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_5X5_ENABLE_BUFFS = gvgSettings.getProperty("Gvg5x5EnableBuffs", false);
         GVG_5X5_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("Gvg5x5MageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_5X5_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_5X5_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("Gvg5x5WarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_5X5_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_5X5_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("Gvg5x5CustomItemsEnable", false);
         GVG_5X5_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("Gvg5x5CustomItemsPath", "config/events/gvg/battlegvg_items5x5.xml");
         GVG_5X5_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("Gvg5x5CustomItemsEnchantWeapon", 6);
         GVG_5X5_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("Gvg5x5CustomItemsEnchantArmor", 6);
         GVG_5X5_ENCHANT_LIMIT = gvgSettings.getProperty("Gvg5x5EnchantLimit", false);
         GVG_5X5_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("Gvg5x5EnchantLimitWeapon", 6);
         GVG_5X5_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("Gvg5x5EnchantLimitArmor", 6);
         GVG_5X5_CNAME_TEMPLATE = gvgSettings.getProperty("Gvg5x5CNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_5X5_ALLOW_SPEC = gvgSettings.getProperty("Gvg5x5AllowSpec", false);
         GVG_5X5_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("Gvg5x5ProhibitParticipantsSpec", true);
         GVG_5X5_REWARD = stringToIntArray2X(gvgSettings.getProperty("Gvg5x5Reward", ""), ";", ",");
         GVG_5X5_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("Gvg5x5RewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("Gvg5x5BlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_5X5_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("Gvg5x5RedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_5X5_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_5X5_CLEAR_LOC = gvgSettings.getProperty("Gvg5x5ClearLoc", "82698,148638,-3473");
         GVG_5X5_COORDS_SPECTATORS = gvgSettings.getProperty("Gvg5x5CoordsSpectators", new int[0]);
         GVG_5X5_NO_AN = gvgSettings.getProperty("Gvg5x5NoAn", false);
         GVG_5X5_NO_ENCHANT_SKILLS = gvgSettings.getProperty("Gvg5x5NoEnchantSkills", false);
         GVG_5X5_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("Gvg5x5ProhibitedClassIds", new int[0]);
         GVG_5X5_DUELIST_ALLOWED = gvgSettings.getProperty("Gvg5x5DuelistAllowed", 1);
         GVG_5X5_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("Gvg5x5DreadnoughtAllowed", 1);
         GVG_5X5_TANKER_ALLOWED = gvgSettings.getProperty("Gvg5x5TankerAllowed", 1);
         GVG_5X5_DAGGER_ALLOWED = gvgSettings.getProperty("Gvg5x5DaggerAllowed", 1);
         GVG_5X5_ARCHER_ALLOWED = gvgSettings.getProperty("Gvg5x5ArcherAllowed", 1);
         GVG_5X5_HEALER_ALLOWED = gvgSettings.getProperty("Gvg5x5HealerAllowed", 1);
         GVG_5X5_ARCHMAGE_ALLOWED = gvgSettings.getProperty("Gvg5x5ArchmageAllowed", 1);
         GVG_5X5_SOULTAKER_ALLOWED = gvgSettings.getProperty("Gvg5x5SoultakerAllowed", 1);
         GVG_5X5_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("Gvg5x5MysticMouseAllowed", 1);
         GVG_5X5_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("Gvg5x5StormScreamerAllowed", 1);
         GVG_5X5_TITAN_ALLOWED = gvgSettings.getProperty("Gvg5x5TitanAllowed", 1);
         GVG_5X5_DOMINATOR_ALLOWED = gvgSettings.getProperty("Gvg5x5DominatorAllowed", 1);
         GVG_5X5_DOOMCRYER_ALLOWED = gvgSettings.getProperty("Gvg5x5DoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvg5x5.properties File.");
      }
   }

   public static void load6x6() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvg6x6.properties");
      _log.info("Loading: config/events/gvg/battlegvg6x6.properties.");

      try {
         GVG_6X6_ENABLE = gvgSettings.getProperty("Gvg6x6Enable", true);
         GVG_6X6_START_TIME = gvgSettings.getProperty("Gvg6x6StartTime", "00 21 * * 3");
         GVG_6X6_INSTANCE_ID = gvgSettings.getProperty("Gvg6x6InstanceId", 401);
         GVG_6X6_MIN_LVL = gvgSettings.getProperty("Gvg6x6MinLvl", 76);
         GVG_6X6_MAX_LVL = gvgSettings.getProperty("Gvg6x6MaxLvl", 80);
         GVG_6X6_COMMANDS_MIN = gvgSettings.getProperty("Gvg6x6CommandsMin", 2);
         GVG_6X6_COMMANDS_MAX = gvgSettings.getProperty("Gvg6x6CommandsMax", 35);
         GVG_6X6_RESTRICT_IP = gvgSettings.getProperty("Gvg6x6RestrictIp", false);
         GVG_6X6_RESTRICT_HWID = gvgSettings.getProperty("Gvg6x6RestrictHwid", false);
         GVG_6X6_TO_ARENA = gvgSettings.getProperty("Gvg6x6ToArena", false);
         GVG_6X6_ARENA_POINTS = gvgSettings.getProperty("Gvg6x6ArenaPoints", "").split(";");
         GVG_6X6_IN_PEACE = gvgSettings.getProperty("Gvg6x6InPeace", false);
         GVG_6X6_PRE_CHECK = gvgSettings.getProperty("Gvg6x6PreCheck", true);
         GVG_6X6_BLOCK_MOVE = gvgSettings.getProperty("Gvg6x6BlockMove", true);
         GVG_6X6_SHOW_STATUS = gvgSettings.getProperty("Gvg6x6ShowStatus", true);
         GVG_6X6_ROUNDS_WIN = gvgSettings.getProperty("Gvg6x6RoundsWin", 2);
         GVG_6X6_REG_TIME = gvgSettings.getProperty("Gvg6x6RegTime", 15);
         GVG_6X6_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("Gvg6x6AnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_6X6_TIME_BATTLE = gvgSettings.getProperty("Gvg6x6TimeBattle", 5);
         GVG_6X6_HEAL_TP_BACK_TIME = gvgSettings.getProperty("Gvg6x6HealTpBackTime", 10);
         GVG_6X6_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("Gvg6x6AllowBattleTimer", false);
         GVG_6X6_RETURN_POINT = gvgSettings.getProperty("Gvg6x6ReturnPoint", new int[0]);
         GVG_6X6_MANAGER_ID = gvgSettings.getProperty("Gvg6x6ManagerId", 0);
         GVG_6X6_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("Gvg6x6ManagerSpawnType", 0);
         GVG_6X6_MANAGER_COORDS = gvgSettings.getProperty("Gvg6x6ManagerCoords", new int[0]);
         GVG_6X6_RESTRICTED_SKILLS = gvgSettings.getProperty("Gvg6x6RestrictedSkills", new int[0]);
         GVG_6X6_RESTRICTED_ITEMS = gvgSettings.getProperty("Gvg6x6RestrictedItems", new int[0]);
         GVG_6X6_RESTRICTED_SUMMONS = gvgSettings.getProperty("Gvg6x6RestrictedSummons", new int[0]);
         GVG_6X6_BUFFER_ID = gvgSettings.getProperty("Gvg6x6BufferId", 0);
         GVG_6X6_TP_RANGE = gvgSettings.getProperty("Gvg6x6TpRange", 148);
         GVG_6X6_BUFFER1_COORDS = gvgSettings.getProperty("Gvg6x6Buffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_6X6_BUFFER2_COORDS = gvgSettings.getProperty("Gvg6x6Buffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_6X6_ENABLE_BUFFS = gvgSettings.getProperty("Gvg6x6EnableBuffs", false);
         GVG_6X6_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("Gvg6x6MageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_6X6_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_6X6_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("Gvg6x6WarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_6X6_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_6X6_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("Gvg6x6CustomItemsEnable", false);
         GVG_6X6_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("Gvg6x6CustomItemsPath", "config/events/gvg/battlegvg_items6x6.xml");
         GVG_6X6_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("Gvg6x6CustomItemsEnchantWeapon", 6);
         GVG_6X6_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("Gvg6x6CustomItemsEnchantArmor", 6);
         GVG_6X6_ENCHANT_LIMIT = gvgSettings.getProperty("Gvg6x6EnchantLimit", false);
         GVG_6X6_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("Gvg6x6EnchantLimitWeapon", 6);
         GVG_6X6_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("Gvg6x6EnchantLimitArmor", 6);
         GVG_6X6_CNAME_TEMPLATE = gvgSettings.getProperty("Gvg6x6CNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_6X6_ALLOW_SPEC = gvgSettings.getProperty("Gvg6x6AllowSpec", false);
         GVG_6X6_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("Gvg6x6ProhibitParticipantsSpec", true);
         GVG_6X6_REWARD = stringToIntArray2X(gvgSettings.getProperty("Gvg6x6Reward", ""), ";", ",");
         GVG_6X6_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("Gvg6x6RewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("Gvg6x6BlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_6X6_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("Gvg6x6RedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_6X6_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_6X6_CLEAR_LOC = gvgSettings.getProperty("Gvg6x6ClearLoc", "82698,148638,-3473");
         GVG_6X6_COORDS_SPECTATORS = gvgSettings.getProperty("Gvg6x6CoordsSpectators", new int[0]);
         GVG_6X6_NO_AN = gvgSettings.getProperty("Gvg6x6NoAn", false);
         GVG_6X6_NO_ENCHANT_SKILLS = gvgSettings.getProperty("Gvg6x6NoEnchantSkills", false);
         GVG_6X6_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("Gvg6x6ProhibitedClassIds", new int[0]);
         GVG_6X6_DUELIST_ALLOWED = gvgSettings.getProperty("Gvg6x6DuelistAllowed", 1);
         GVG_6X6_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("Gvg6x6DreadnoughtAllowed", 1);
         GVG_6X6_TANKER_ALLOWED = gvgSettings.getProperty("Gvg6x6TankerAllowed", 1);
         GVG_6X6_DAGGER_ALLOWED = gvgSettings.getProperty("Gvg6x6DaggerAllowed", 1);
         GVG_6X6_ARCHER_ALLOWED = gvgSettings.getProperty("Gvg6x6ArcherAllowed", 1);
         GVG_6X6_HEALER_ALLOWED = gvgSettings.getProperty("Gvg6x6HealerAllowed", 1);
         GVG_6X6_ARCHMAGE_ALLOWED = gvgSettings.getProperty("Gvg6x6ArchmageAllowed", 1);
         GVG_6X6_SOULTAKER_ALLOWED = gvgSettings.getProperty("Gvg6x6SoultakerAllowed", 1);
         GVG_6X6_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("Gvg6x6MysticMouseAllowed", 1);
         GVG_6X6_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("Gvg6x6StormScreamerAllowed", 1);
         GVG_6X6_TITAN_ALLOWED = gvgSettings.getProperty("Gvg6x6TitanAllowed", 1);
         GVG_6X6_DOMINATOR_ALLOWED = gvgSettings.getProperty("Gvg6x6DominatorAllowed", 1);
         GVG_6X6_DOOMCRYER_ALLOWED = gvgSettings.getProperty("Gvg6x6DoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvg6x6.properties File.");
      }
   }

   public static void load7x7() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvg7x7.properties");
      _log.info("Loading: config/events/gvg/battlegvg7x7.properties.");

      try {
         GVG_7X7_ENABLE = gvgSettings.getProperty("Gvg7x7Enable", true);
         GVG_7X7_START_TIME = gvgSettings.getProperty("Gvg7x7StartTime", "00 21 * * 3");
         GVG_7X7_INSTANCE_ID = gvgSettings.getProperty("Gvg7x7InstanceId", 401);
         GVG_7X7_MIN_LVL = gvgSettings.getProperty("Gvg7x7MinLvl", 76);
         GVG_7X7_MAX_LVL = gvgSettings.getProperty("Gvg7x7MaxLvl", 80);
         GVG_7X7_COMMANDS_MIN = gvgSettings.getProperty("Gvg7x7CommandsMin", 2);
         GVG_7X7_COMMANDS_MAX = gvgSettings.getProperty("Gvg7x7CommandsMax", 35);
         GVG_7X7_RESTRICT_IP = gvgSettings.getProperty("Gvg7x7RestrictIp", false);
         GVG_7X7_RESTRICT_HWID = gvgSettings.getProperty("Gvg7x7RestrictHwid", false);
         GVG_7X7_TO_ARENA = gvgSettings.getProperty("Gvg7x7ToArena", false);
         GVG_7X7_ARENA_POINTS = gvgSettings.getProperty("Gvg7x7ArenaPoints", "").split(";");
         GVG_7X7_IN_PEACE = gvgSettings.getProperty("Gvg7x7InPeace", false);
         GVG_7X7_PRE_CHECK = gvgSettings.getProperty("Gvg7x7PreCheck", true);
         GVG_7X7_BLOCK_MOVE = gvgSettings.getProperty("Gvg7x7BlockMove", true);
         GVG_7X7_SHOW_STATUS = gvgSettings.getProperty("Gvg7x7ShowStatus", true);
         GVG_7X7_ROUNDS_WIN = gvgSettings.getProperty("Gvg7x7RoundsWin", 2);
         GVG_7X7_REG_TIME = gvgSettings.getProperty("Gvg7x7RegTime", 15);
         GVG_7X7_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("Gvg7x7AnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_7X7_TIME_BATTLE = gvgSettings.getProperty("Gvg7x7TimeBattle", 5);
         GVG_7X7_HEAL_TP_BACK_TIME = gvgSettings.getProperty("Gvg7x7HealTpBackTime", 10);
         GVG_7X7_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("Gvg7x7AllowBattleTimer", false);
         GVG_7X7_RETURN_POINT = gvgSettings.getProperty("Gvg7x7ReturnPoint", new int[0]);
         GVG_7X7_MANAGER_ID = gvgSettings.getProperty("Gvg7x7ManagerId", 0);
         GVG_7X7_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("Gvg7x7ManagerSpawnType", 0);
         GVG_7X7_MANAGER_COORDS = gvgSettings.getProperty("Gvg7x7ManagerCoords", new int[0]);
         GVG_7X7_RESTRICTED_SKILLS = gvgSettings.getProperty("Gvg7x7RestrictedSkills", new int[0]);
         GVG_7X7_RESTRICTED_ITEMS = gvgSettings.getProperty("Gvg7x7RestrictedItems", new int[0]);
         GVG_7X7_RESTRICTED_SUMMONS = gvgSettings.getProperty("Gvg7x7RestrictedSummons", new int[0]);
         GVG_7X7_BUFFER_ID = gvgSettings.getProperty("Gvg7x7BufferId", 0);
         GVG_7X7_TP_RANGE = gvgSettings.getProperty("Gvg7x7TpRange", 148);
         GVG_7X7_BUFFER1_COORDS = gvgSettings.getProperty("Gvg7x7Buffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_7X7_BUFFER2_COORDS = gvgSettings.getProperty("Gvg7x7Buffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_7X7_ENABLE_BUFFS = gvgSettings.getProperty("Gvg7x7EnableBuffs", false);
         GVG_7X7_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("Gvg7x7MageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_7X7_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_7X7_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("Gvg7x7WarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_7X7_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_7X7_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("Gvg7x7CustomItemsEnable", false);
         GVG_7X7_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("Gvg7x7CustomItemsPath", "config/events/gvg/battlegvg_items7x7.xml");
         GVG_7X7_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("Gvg7x7CustomItemsEnchantWeapon", 6);
         GVG_7X7_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("Gvg7x7CustomItemsEnchantArmor", 6);
         GVG_7X7_ENCHANT_LIMIT = gvgSettings.getProperty("Gvg7x7EnchantLimit", false);
         GVG_7X7_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("Gvg7x7EnchantLimitWeapon", 6);
         GVG_7X7_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("Gvg7x7EnchantLimitArmor", 6);
         GVG_7X7_CNAME_TEMPLATE = gvgSettings.getProperty("Gvg7x7CNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_7X7_ALLOW_SPEC = gvgSettings.getProperty("Gvg7x7AllowSpec", false);
         GVG_7X7_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("Gvg7x7ProhibitParticipantsSpec", true);
         GVG_7X7_REWARD = stringToIntArray2X(gvgSettings.getProperty("Gvg7x7Reward", ""), ";", ",");
         GVG_7X7_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("Gvg7x7RewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("Gvg7x7BlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_7X7_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("Gvg7x7RedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_7X7_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_7X7_CLEAR_LOC = gvgSettings.getProperty("Gvg7x7ClearLoc", "82698,148638,-3473");
         GVG_7X7_COORDS_SPECTATORS = gvgSettings.getProperty("Gvg7x7CoordsSpectators", new int[0]);
         GVG_7X7_NO_AN = gvgSettings.getProperty("Gvg7x7NoAn", false);
         GVG_7X7_NO_ENCHANT_SKILLS = gvgSettings.getProperty("Gvg7x7NoEnchantSkills", false);
         GVG_7X7_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("Gvg7x7ProhibitedClassIds", new int[0]);
         GVG_7X7_DUELIST_ALLOWED = gvgSettings.getProperty("Gvg7x7DuelistAllowed", 1);
         GVG_7X7_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("Gvg7x7DreadnoughtAllowed", 1);
         GVG_7X7_TANKER_ALLOWED = gvgSettings.getProperty("Gvg7x7TankerAllowed", 1);
         GVG_7X7_DAGGER_ALLOWED = gvgSettings.getProperty("Gvg7x7DaggerAllowed", 1);
         GVG_7X7_ARCHER_ALLOWED = gvgSettings.getProperty("Gvg7x7ArcherAllowed", 1);
         GVG_7X7_HEALER_ALLOWED = gvgSettings.getProperty("Gvg7x7HealerAllowed", 1);
         GVG_7X7_ARCHMAGE_ALLOWED = gvgSettings.getProperty("Gvg7x7ArchmageAllowed", 1);
         GVG_7X7_SOULTAKER_ALLOWED = gvgSettings.getProperty("Gvg7x7SoultakerAllowed", 1);
         GVG_7X7_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("Gvg7x7MysticMouseAllowed", 1);
         GVG_7X7_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("Gvg7x7StormScreamerAllowed", 1);
         GVG_7X7_TITAN_ALLOWED = gvgSettings.getProperty("Gvg7x7TitanAllowed", 1);
         GVG_7X7_DOMINATOR_ALLOWED = gvgSettings.getProperty("Gvg7x7DominatorAllowed", 1);
         GVG_7X7_DOOMCRYER_ALLOWED = gvgSettings.getProperty("Gvg7x7DoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvg7x7.properties File.");
      }
   }

   public static void load8x8() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvg8x8.properties");
      _log.info("Loading: config/events/gvg/battlegvg8x8.properties.");

      try {
         GVG_8X8_ENABLE = gvgSettings.getProperty("Gvg8x8Enable", true);
         GVG_8X8_START_TIME = gvgSettings.getProperty("Gvg8x8StartTime", "00 21 * * 3");
         GVG_8X8_INSTANCE_ID = gvgSettings.getProperty("Gvg8x8InstanceId", 401);
         GVG_8X8_MIN_LVL = gvgSettings.getProperty("Gvg8x8MinLvl", 76);
         GVG_8X8_MAX_LVL = gvgSettings.getProperty("Gvg8x8MaxLvl", 80);
         GVG_8X8_COMMANDS_MIN = gvgSettings.getProperty("Gvg8x8CommandsMin", 2);
         GVG_8X8_COMMANDS_MAX = gvgSettings.getProperty("Gvg8x8CommandsMax", 35);
         GVG_8X8_RESTRICT_IP = gvgSettings.getProperty("Gvg8x8RestrictIp", false);
         GVG_8X8_RESTRICT_HWID = gvgSettings.getProperty("Gvg8x8RestrictHwid", false);
         GVG_8X8_TO_ARENA = gvgSettings.getProperty("Gvg8x8ToArena", false);
         GVG_8X8_ARENA_POINTS = gvgSettings.getProperty("Gvg8x8ArenaPoints", "").split(";");
         GVG_8X8_IN_PEACE = gvgSettings.getProperty("Gvg8x8InPeace", false);
         GVG_8X8_PRE_CHECK = gvgSettings.getProperty("Gvg8x8PreCheck", true);
         GVG_8X8_BLOCK_MOVE = gvgSettings.getProperty("Gvg8x8BlockMove", true);
         GVG_8X8_SHOW_STATUS = gvgSettings.getProperty("Gvg8x8ShowStatus", true);
         GVG_8X8_ROUNDS_WIN = gvgSettings.getProperty("Gvg8x8RoundsWin", 2);
         GVG_8X8_REG_TIME = gvgSettings.getProperty("Gvg8x8RegTime", 15);
         GVG_8X8_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("Gvg8x8AnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_8X8_TIME_BATTLE = gvgSettings.getProperty("Gvg8x8TimeBattle", 5);
         GVG_8X8_HEAL_TP_BACK_TIME = gvgSettings.getProperty("Gvg8x8HealTpBackTime", 10);
         GVG_8X8_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("Gvg8x8AllowBattleTimer", false);
         GVG_8X8_RETURN_POINT = gvgSettings.getProperty("Gvg8x8ReturnPoint", new int[0]);
         GVG_8X8_MANAGER_ID = gvgSettings.getProperty("Gvg8x8ManagerId", 0);
         GVG_8X8_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("Gvg8x8ManagerSpawnType", 0);
         GVG_8X8_MANAGER_COORDS = gvgSettings.getProperty("Gvg8x8ManagerCoords", new int[0]);
         GVG_8X8_RESTRICTED_SKILLS = gvgSettings.getProperty("Gvg8x8RestrictedSkills", new int[0]);
         GVG_8X8_RESTRICTED_ITEMS = gvgSettings.getProperty("Gvg8x8RestrictedItems", new int[0]);
         GVG_8X8_RESTRICTED_SUMMONS = gvgSettings.getProperty("Gvg8x8RestrictedSummons", new int[0]);
         GVG_8X8_BUFFER_ID = gvgSettings.getProperty("Gvg8x8BufferId", 0);
         GVG_8X8_TP_RANGE = gvgSettings.getProperty("Gvg8x8TpRange", 148);
         GVG_8X8_BUFFER1_COORDS = gvgSettings.getProperty("Gvg8x8Buffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_8X8_BUFFER2_COORDS = gvgSettings.getProperty("Gvg8x8Buffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_8X8_ENABLE_BUFFS = gvgSettings.getProperty("Gvg8x8EnableBuffs", false);
         GVG_8X8_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("Gvg8x8MageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_8X8_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_8X8_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("Gvg8x8WarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_8X8_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_8X8_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("Gvg8x8CustomItemsEnable", false);
         GVG_8X8_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("Gvg8x8CustomItemsPath", "config/events/gvg/battlegvg_items8x8.xml");
         GVG_8X8_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("Gvg8x8CustomItemsEnchantWeapon", 6);
         GVG_8X8_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("Gvg8x8CustomItemsEnchantArmor", 6);
         GVG_8X8_ENCHANT_LIMIT = gvgSettings.getProperty("Gvg8x8EnchantLimit", false);
         GVG_8X8_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("Gvg8x8EnchantLimitWeapon", 6);
         GVG_8X8_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("Gvg8x8EnchantLimitArmor", 6);
         GVG_8X8_CNAME_TEMPLATE = gvgSettings.getProperty("Gvg8x8CNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_8X8_ALLOW_SPEC = gvgSettings.getProperty("Gvg8x8AllowSpec", false);
         GVG_8X8_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("Gvg8x8ProhibitParticipantsSpec", true);
         GVG_8X8_REWARD = stringToIntArray2X(gvgSettings.getProperty("Gvg8x8Reward", ""), ";", ",");
         GVG_8X8_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("Gvg8x8RewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("Gvg8x8BlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_8X8_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("Gvg8x8RedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_8X8_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_8X8_CLEAR_LOC = gvgSettings.getProperty("Gvg8x8ClearLoc", "82698,148638,-3473");
         GVG_8X8_COORDS_SPECTATORS = gvgSettings.getProperty("Gvg8x8CoordsSpectators", new int[0]);
         GVG_8X8_NO_AN = gvgSettings.getProperty("Gvg8x8NoAn", false);
         GVG_8X8_NO_ENCHANT_SKILLS = gvgSettings.getProperty("Gvg8x8NoEnchantSkills", false);
         GVG_8X8_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("Gvg8x8ProhibitedClassIds", new int[0]);
         GVG_8X8_DUELIST_ALLOWED = gvgSettings.getProperty("Gvg8x8DuelistAllowed", 1);
         GVG_8X8_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("Gvg8x8DreadnoughtAllowed", 1);
         GVG_8X8_TANKER_ALLOWED = gvgSettings.getProperty("Gvg8x8TankerAllowed", 1);
         GVG_8X8_DAGGER_ALLOWED = gvgSettings.getProperty("Gvg8x8DaggerAllowed", 1);
         GVG_8X8_ARCHER_ALLOWED = gvgSettings.getProperty("Gvg8x8ArcherAllowed", 1);
         GVG_8X8_HEALER_ALLOWED = gvgSettings.getProperty("Gvg8x8HealerAllowed", 1);
         GVG_8X8_ARCHMAGE_ALLOWED = gvgSettings.getProperty("Gvg8x8ArchmageAllowed", 1);
         GVG_8X8_SOULTAKER_ALLOWED = gvgSettings.getProperty("Gvg8x8SoultakerAllowed", 1);
         GVG_8X8_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("Gvg8x8MysticMouseAllowed", 1);
         GVG_8X8_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("Gvg8x8StormScreamerAllowed", 1);
         GVG_8X8_TITAN_ALLOWED = gvgSettings.getProperty("Gvg8x8TitanAllowed", 1);
         GVG_8X8_DOMINATOR_ALLOWED = gvgSettings.getProperty("Gvg8x8DominatorAllowed", 1);
         GVG_8X8_DOOMCRYER_ALLOWED = gvgSettings.getProperty("Gvg8x8DoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvg8x8.properties File.");
      }
   }

   public static void load9x9() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvg9x9.properties");
      _log.info("Loading: config/events/gvg/battlegvg9x9.properties.");

      try {
         GVG_9X9_ENABLE = gvgSettings.getProperty("Gvg9x9Enable", true);
         GVG_9X9_START_TIME = gvgSettings.getProperty("Gvg9x9StartTime", "00 21 * * 3");
         GVG_9X9_INSTANCE_ID = gvgSettings.getProperty("Gvg9x9InstanceId", 401);
         GVG_9X9_MIN_LVL = gvgSettings.getProperty("Gvg9x9MinLvl", 76);
         GVG_9X9_MAX_LVL = gvgSettings.getProperty("Gvg9x9MaxLvl", 80);
         GVG_9X9_COMMANDS_MIN = gvgSettings.getProperty("Gvg9x9CommandsMin", 2);
         GVG_9X9_COMMANDS_MAX = gvgSettings.getProperty("Gvg9x9CommandsMax", 35);
         GVG_9X9_RESTRICT_IP = gvgSettings.getProperty("Gvg9x9RestrictIp", false);
         GVG_9X9_RESTRICT_HWID = gvgSettings.getProperty("Gvg9x9RestrictHwid", false);
         GVG_9X9_TO_ARENA = gvgSettings.getProperty("Gvg9x9ToArena", false);
         GVG_9X9_ARENA_POINTS = gvgSettings.getProperty("Gvg9x9ArenaPoints", "").split(";");
         GVG_9X9_IN_PEACE = gvgSettings.getProperty("Gvg9x9InPeace", false);
         GVG_9X9_PRE_CHECK = gvgSettings.getProperty("Gvg9x9PreCheck", true);
         GVG_9X9_BLOCK_MOVE = gvgSettings.getProperty("Gvg9x9BlockMove", true);
         GVG_9X9_SHOW_STATUS = gvgSettings.getProperty("Gvg9x9ShowStatus", true);
         GVG_9X9_ROUNDS_WIN = gvgSettings.getProperty("Gvg9x9RoundsWin", 2);
         GVG_9X9_REG_TIME = gvgSettings.getProperty("Gvg9x9RegTime", 15);
         GVG_9X9_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("Gvg9x9AnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_9X9_TIME_BATTLE = gvgSettings.getProperty("Gvg9x9TimeBattle", 5);
         GVG_9X9_HEAL_TP_BACK_TIME = gvgSettings.getProperty("Gvg9x9HealTpBackTime", 10);
         GVG_9X9_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("Gvg9x9AllowBattleTimer", false);
         GVG_9X9_RETURN_POINT = gvgSettings.getProperty("Gvg9x9ReturnPoint", new int[0]);
         GVG_9X9_MANAGER_ID = gvgSettings.getProperty("Gvg9x9ManagerId", 0);
         GVG_9X9_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("Gvg9x9ManagerSpawnType", 0);
         GVG_9X9_MANAGER_COORDS = gvgSettings.getProperty("Gvg9x9ManagerCoords", new int[0]);
         GVG_9X9_RESTRICTED_SKILLS = gvgSettings.getProperty("Gvg9x9RestrictedSkills", new int[0]);
         GVG_9X9_RESTRICTED_ITEMS = gvgSettings.getProperty("Gvg9x9RestrictedItems", new int[0]);
         GVG_9X9_RESTRICTED_SUMMONS = gvgSettings.getProperty("Gvg9x9RestrictedSummons", new int[0]);
         GVG_9X9_BUFFER_ID = gvgSettings.getProperty("Gvg9x9BufferId", 0);
         GVG_9X9_TP_RANGE = gvgSettings.getProperty("Gvg9x9TpRange", 148);
         GVG_9X9_BUFFER1_COORDS = gvgSettings.getProperty("Gvg9x9Buffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_9X9_BUFFER2_COORDS = gvgSettings.getProperty("Gvg9x9Buffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_9X9_ENABLE_BUFFS = gvgSettings.getProperty("Gvg9x9EnableBuffs", false);
         GVG_9X9_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("Gvg9x9MageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_9X9_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_9X9_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("Gvg9x9WarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_9X9_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_9X9_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("Gvg9x9CustomItemsEnable", false);
         GVG_9X9_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("Gvg9x9CustomItemsPath", "config/events/gvg/battlegvg_items9x9.xml");
         GVG_9X9_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("Gvg9x9CustomItemsEnchantWeapon", 6);
         GVG_9X9_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("Gvg9x9CustomItemsEnchantArmor", 6);
         GVG_9X9_ENCHANT_LIMIT = gvgSettings.getProperty("Gvg9x9EnchantLimit", false);
         GVG_9X9_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("Gvg9x9EnchantLimitWeapon", 6);
         GVG_9X9_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("Gvg9x9EnchantLimitArmor", 6);
         GVG_9X9_CNAME_TEMPLATE = gvgSettings.getProperty("Gvg9x9CNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_9X9_ALLOW_SPEC = gvgSettings.getProperty("Gvg9x9AllowSpec", false);
         GVG_9X9_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("Gvg9x9ProhibitParticipantsSpec", true);
         GVG_9X9_REWARD = stringToIntArray2X(gvgSettings.getProperty("Gvg9x9Reward", ""), ";", ",");
         GVG_9X9_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("Gvg9x9RewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("Gvg9x9BlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_9X9_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("Gvg9x9RedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_9X9_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_9X9_CLEAR_LOC = gvgSettings.getProperty("Gvg9x9ClearLoc", "82698,148638,-3473");
         GVG_9X9_COORDS_SPECTATORS = gvgSettings.getProperty("Gvg9x9CoordsSpectators", new int[0]);
         GVG_9X9_NO_AN = gvgSettings.getProperty("Gvg9x9NoAn", false);
         GVG_9X9_NO_ENCHANT_SKILLS = gvgSettings.getProperty("Gvg9x9NoEnchantSkills", false);
         GVG_9X9_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("Gvg9x9ProhibitedClassIds", new int[0]);
         GVG_9X9_DUELIST_ALLOWED = gvgSettings.getProperty("Gvg9x9DuelistAllowed", 1);
         GVG_9X9_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("Gvg9x9DreadnoughtAllowed", 1);
         GVG_9X9_TANKER_ALLOWED = gvgSettings.getProperty("Gvg9x9TankerAllowed", 1);
         GVG_9X9_DAGGER_ALLOWED = gvgSettings.getProperty("Gvg9x9DaggerAllowed", 1);
         GVG_9X9_ARCHER_ALLOWED = gvgSettings.getProperty("Gvg9x9ArcherAllowed", 1);
         GVG_9X9_HEALER_ALLOWED = gvgSettings.getProperty("Gvg9x9HealerAllowed", 1);
         GVG_9X9_ARCHMAGE_ALLOWED = gvgSettings.getProperty("Gvg9x9ArchmageAllowed", 1);
         GVG_9X9_SOULTAKER_ALLOWED = gvgSettings.getProperty("Gvg9x9SoultakerAllowed", 1);
         GVG_9X9_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("Gvg9x9MysticMouseAllowed", 1);
         GVG_9X9_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("Gvg9x9StormScreamerAllowed", 1);
         GVG_9X9_TITAN_ALLOWED = gvgSettings.getProperty("Gvg9x9TitanAllowed", 1);
         GVG_9X9_DOMINATOR_ALLOWED = gvgSettings.getProperty("Gvg9x9DominatorAllowed", 1);
         GVG_9X9_DOOMCRYER_ALLOWED = gvgSettings.getProperty("Gvg9x9DoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvg9x9.properties File.");
      }
   }

   public static void loadCxC() {
      ExProperties gvgSettings = Config.load("config/events/gvg/battlegvgCxC.properties");
      _log.info("Loading: config/events/gvg/battlegvgCxC.properties.");

      try {
         GVG_CXC_ENABLE = gvgSettings.getProperty("GvgCxCEnable", true);
         GVG_CXC_START_TIME = gvgSettings.getProperty("GvgCxCStartTime", "00 21 * * 3");
         GVG_CXC_INSTANCE_ID = gvgSettings.getProperty("GvgCxCInstanceId", 401);
         GVG_CXC_MIN_LVL = gvgSettings.getProperty("GvgCxCMinLvl", 76);
         GVG_CXC_MAX_LVL = gvgSettings.getProperty("GvgCxCMaxLvl", 80);
         GVG_CXC_COMMANDS_MIN = gvgSettings.getProperty("GvgCxCCommandsMin", 2);
         GVG_CXC_COMMANDS_MAX = gvgSettings.getProperty("GvgCxCCommandsMax", 35);
         GVG_CXC_RESTRICT_IP = gvgSettings.getProperty("GvgCxCRestrictIp", false);
         GVG_CXC_RESTRICT_HWID = gvgSettings.getProperty("GvgCxCRestrictHwid", false);
         GVG_CXC_TO_ARENA = gvgSettings.getProperty("GvgCxCToArena", false);
         GVG_CXC_ARENA_POINTS = gvgSettings.getProperty("GvgCxCArenaPoints", "").split(";");
         GVG_CXC_IN_PEACE = gvgSettings.getProperty("GvgCxCInPeace", false);
         GVG_CXC_PRE_CHECK = gvgSettings.getProperty("GvgCxCPreCheck", true);
         GVG_CXC_BLOCK_MOVE = gvgSettings.getProperty("GvgCxCBlockMove", true);
         GVG_CXC_SHOW_STATUS = gvgSettings.getProperty("GvgCxCShowStatus", true);
         GVG_CXC_ROUNDS_WIN = gvgSettings.getProperty("GvgCxCRoundsWin", 2);
         GVG_CXC_REG_TIME = gvgSettings.getProperty("GvgCxCRegTime", 15);
         GVG_CXC_ANNOUNCE_REG_TIMES = gvgSettings.getProperty("GvgCxCAnnounceRegTimes", new int[]{5, 4, 3, 2, 1});
         GVG_CXC_TIME_BATTLE = gvgSettings.getProperty("GvgCxCTimeBattle", 5);
         GVG_CXC_HEAL_TP_BACK_TIME = gvgSettings.getProperty("GvgCxCHealTpBackTime", 10);
         GVG_CXC_ALLOW_BATTLE_TIMER = gvgSettings.getProperty("GvgCxCAllowBattleTimer", false);
         GVG_CXC_RETURN_POINT = gvgSettings.getProperty("GvgCxCReturnPoint", new int[0]);
         GVG_CXC_MANAGER_ID = gvgSettings.getProperty("GvgCxCManagerId", 0);
         GVG_CXC_MANAGER_SPAWN_TYPE = (byte)gvgSettings.getProperty("GvgCxCManagerSpawnType", 0);
         GVG_CXC_MANAGER_COORDS = gvgSettings.getProperty("GvgCxCManagerCoords", new int[0]);
         GVG_CXC_RESTRICTED_SKILLS = gvgSettings.getProperty("GvgCxCRestrictedSkills", new int[0]);
         GVG_CXC_RESTRICTED_ITEMS = gvgSettings.getProperty("GvgCxCRestrictedItems", new int[0]);
         GVG_CXC_RESTRICTED_SUMMONS = gvgSettings.getProperty("GvgCxCRestrictedSummons", new int[0]);
         GVG_CXC_BUFFER_ID = gvgSettings.getProperty("GvgCxCBufferId", 0);
         GVG_CXC_TP_RANGE = gvgSettings.getProperty("GvgCxCTpRange", 148);
         GVG_CXC_BUFFER1_COORDS = gvgSettings.getProperty("GvgCxCBuffer1Coords", "-70392,-209137,-3328").split(";");
         GVG_CXC_BUFFER2_COORDS = gvgSettings.getProperty("GvgCxCBuffer2Coords", "-69176,-209137,-3328").split(";");
         GVG_CXC_ENABLE_BUFFS = gvgSettings.getProperty("GvgCxCEnableBuffs", false);
         GVG_CXC_MAGE_BUFF = new ArrayList();
         String[] blueTeamLocs = gvgSettings.getProperty("GvgCxCMageBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         int var2 = blueTeamLocs.length;

         int var3;
         String str;
         String[] params;
         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_CXC_MAGE_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_CXC_WARRIOR_BUFF = new ArrayList();
         blueTeamLocs = gvgSettings.getProperty("GvgCxCWarriorBuff", ArrayUtils.EMPTY_STRING_ARRAY);
         var2 = blueTeamLocs.length;

         for(var3 = 0; var3 < var2; ++var3) {
            str = blueTeamLocs[var3];
            if (!str.trim().isEmpty()) {
               params = StringUtils.split(str, "-:");
               GVG_CXC_WARRIOR_BUFF.add(Pair.of(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
            }
         }

         GVG_CXC_CUSTOM_ITEMS_ENABLE = gvgSettings.getProperty("GvgCxCCustomItemsEnable", false);
         GVG_CXC_CUSTOM_ITEMS_PATH = gvgSettings.getProperty("GvgCxCCustomItemsPath", "config/events/gvg/battlegvg_itemsCxC.xml");
         GVG_CXC_CUSTOM_ITEMS_ENCHANT_WEAPON = gvgSettings.getProperty("GvgCxCCustomItemsEnchantWeapon", 6);
         GVG_CXC_CUSTOM_ITEMS_ENCHANT_ARMOR = gvgSettings.getProperty("GvgCxCCustomItemsEnchantArmor", 6);
         GVG_CXC_ENCHANT_LIMIT = gvgSettings.getProperty("GvgCxCEnchantLimit", false);
         GVG_CXC_ENCHANT_LIMIT_WEAPON = gvgSettings.getProperty("GvgCxCEnchantLimitWeapon", 6);
         GVG_CXC_ENCHANT_LIMIT_ARMOR = gvgSettings.getProperty("GvgCxCEnchantLimitArmor", 6);
         GVG_CXC_CNAME_TEMPLATE = gvgSettings.getProperty("GvgCxCCNameTemplate", "[A-Za-z0-9]{4,11}");
         GVG_CXC_ALLOW_SPEC = gvgSettings.getProperty("GvgCxCAllowSpec", false);
         GVG_CXC_PROHIBIT_PARTICIPANTS_SPEC = gvgSettings.getProperty("GvgCxCProhibitParticipantsSpec", true);
         GVG_CXC_REWARD = stringToIntArray2X(gvgSettings.getProperty("GvgCxCReward", ""), ";", ",");
         GVG_CXC_REWARD_PER_KILL = stringToIntArray2X(gvgSettings.getProperty("GvgCxCRewardPerKill", ""), ";", ",");
         blueTeamLocs = gvgSettings.getProperty("GvgCxCBlueTeamLoc", "-70392,-209137,-3328").split(";");
         String[] redTeamLocs = blueTeamLocs;
         var3 = blueTeamLocs.length;

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            String loc = redTeamLocs[var9];
            GVG_CXC_BLUE_TEAM_LOC.add(Location.parseLoc(loc));
         }

         redTeamLocs = gvgSettings.getProperty("GvgCxCRedTeamLoc", "-69176,-209137,-3328").split(";");
         String[] var10 = redTeamLocs;
         var9 = redTeamLocs.length;

         for(int var12 = 0; var12 < var9; ++var12) {
            String loc = var10[var12];
            GVG_CXC_RED_TEAM_LOC.add(Location.parseLoc(loc));
         }

         GVG_CXC_CLEAR_LOC = gvgSettings.getProperty("GvgCxCClearLoc", "82698,148638,-3473");
         GVG_CXC_COORDS_SPECTATORS = gvgSettings.getProperty("GvgCxCCoordsSpectators", new int[0]);
         GVG_CXC_NO_AN = gvgSettings.getProperty("GvgCxCNoAn", false);
         GVG_CXC_NO_ENCHANT_SKILLS = gvgSettings.getProperty("GvgCxCNoEnchantSkills", false);
         GVG_CXC_PROHIBITED_CLASS_IDS = gvgSettings.getProperty("GvgCxCProhibitedClassIds", new int[0]);
         GVG_CXC_DUELIST_ALLOWED = gvgSettings.getProperty("GvgCxCDuelistAllowed", 1);
         GVG_CXC_DREADNOUGHT_ALLOWED = gvgSettings.getProperty("GvgCxCDreadnoughtAllowed", 1);
         GVG_CXC_TANKER_ALLOWED = gvgSettings.getProperty("GvgCxCTankerAllowed", 1);
         GVG_CXC_DAGGER_ALLOWED = gvgSettings.getProperty("GvgCxCDaggerAllowed", 1);
         GVG_CXC_ARCHER_ALLOWED = gvgSettings.getProperty("GvgCxCArcherAllowed", 1);
         GVG_CXC_HEALER_ALLOWED = gvgSettings.getProperty("GvgCxCHealerAllowed", 1);
         GVG_CXC_ARCHMAGE_ALLOWED = gvgSettings.getProperty("GvgCxCArchmageAllowed", 1);
         GVG_CXC_SOULTAKER_ALLOWED = gvgSettings.getProperty("GvgCxCSoultakerAllowed", 1);
         GVG_CXC_MYSTICMOUSE_ALLOWED = gvgSettings.getProperty("GvgCxCMysticMouseAllowed", 1);
         GVG_CXC_STORMSCREAMER_ALLOWED = gvgSettings.getProperty("GvgCxCStormScreamerAllowed", 1);
         GVG_CXC_TITAN_ALLOWED = gvgSettings.getProperty("GvgCxCTitanAllowed", 1);
         GVG_CXC_DOMINATOR_ALLOWED = gvgSettings.getProperty("GvgCxCDominatorAllowed", 1);
         GVG_CXC_DOOMCRYER_ALLOWED = gvgSettings.getProperty("GvgCxCDoomcryerAllowed", 1);
      } catch (Exception var7) {
         var7.printStackTrace();
         throw new Error("Failed to Load config/events/gvg/battlegvgCxC.properties File.");
      }
   }

   public static int[][] stringToIntArray2X(String text, String separator1, String separator2) {
      if (text != null && !text.isEmpty()) {
         String[] separatedText = text.split(separator1);
         int[][] result = new int[separatedText.length][];

         for(int i = 0; i < separatedText.length; ++i) {
            result[i] = stringToIntArray(separatedText[i], separator2);
         }

         return result;
      } else {
         return new int[0][];
      }
   }

   public static int[] stringToIntArray(String text, String separator) {
      if (text != null && !text.isEmpty()) {
         String[] separatedText = text.split(separator);
         int[] result = new int[separatedText.length];

         try {
            for(int i = 0; i < separatedText.length; ++i) {
               result[i] = Integer.parseInt(separatedText[i]);
            }

            return result;
         } catch (NumberFormatException var5) {
            _log.error("StringArrayUtils: Error while convert string to int array.", var5);
            return new int[0];
         }
      } else {
         return new int[0];
      }
   }
}
