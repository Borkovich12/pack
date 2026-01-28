package l2.gameserver.network.l2.s2c;

import events.BossHunting.ConfigBossHunting;
import events.CaptureCastle.ConfigCaptureCastle;
import events.RoomOfPower.ConfigRoomOfPower;
import events.TreasureHunting.ConfigTreasureHunting;
import java.util.Iterator;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.skills.effects.EffectCubic;
import l2.gameserver.utils.Location;

public class UserInfo extends L2GameServerPacket {
   private boolean can_writeImpl = false;
   private boolean partyRoom;
   private int _runSpd;
   private int _walkSpd;
   private int _swimRunSpd;
   private int _swimWalkSpd;
   private int _flRunSpd;
   private int _flWalkSpd;
   private int _flyRunSpd;
   private int _flyWalkSpd;
   private int _relation;
   private double move_speed;
   private double attack_speed;
   private double col_radius;
   private double col_height;
   private int[][] _inv;
   private Location _loc;
   private Location _fishLoc;
   private int obj_id;
   private int vehicle_obj_id;
   private int _race;
   private int sex;
   private int base_class;
   private int level;
   private int curCp;
   private int maxCp;
   private int _enchant;
   private int _pAtkRange;
   private long _exp;
   private int curHp;
   private int maxHp;
   private int curMp;
   private int maxMp;
   private int curLoad;
   private int maxLoad;
   private int rec_left;
   private int rec_have;
   private int _str;
   private int _con;
   private int _dex;
   private int _int;
   private int _wit;
   private int _men;
   private int _sp;
   private int ClanPrivs;
   private int InventoryLimit;
   private int _patk;
   private int _patkspd;
   private int _pdef;
   private int evasion;
   private int accuracy;
   private int crit;
   private int _matk;
   private int _matkspd;
   private int _mdef;
   private int pvp_flag;
   private int karma;
   private int hair_style;
   private int hair_color;
   private int face;
   private int gm_commands;
   private int clan_id;
   private int clan_crest_id;
   private int ally_id;
   private int ally_crest_id;
   private int large_clan_crest_id;
   private int private_store;
   private int can_crystalize;
   private int pk_kills;
   private int pvp_kills;
   private int class_id;
   private int agathion;
   private int _abnormalEffect;
   private int _abnormalEffect2;
   private int noble;
   private int hero;
   private int mount_id;
   private int cw_level;
   private int name_color;
   private int running;
   private int pledge_class;
   private int pledge_type;
   private int title_color;
   private int transformation;
   private int mount_type;
   private String _name;
   private String title;
   private EffectCubic[] cubics;
   private boolean isFlying;
   private TeamType _team;

   public UserInfo(Player player) {
      if (player.getTransformationName() != null) {
         this._name = player.getTransformationName();
         this.title = player.getTransformationTitle() != null ? player.getTransformationTitle() : "";
         this.clan_crest_id = 0;
         this.ally_crest_id = 0;
         this.large_clan_crest_id = 0;
         this.cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
      } else {
         this._name = getVisibleName(player);
         Clan clan = player.getClan();
         Alliance alliance = clan == null ? null : clan.getAlliance();
         if (isHideClanAndAllyInfo(player)) {
            this.clan_id = 0;
            this.clan_crest_id = 0;
            this.large_clan_crest_id = 0;
            this.ally_id = 0;
            this.ally_crest_id = 0;
         } else {
            this.clan_id = clan == null ? 0 : clan.getClanId();
            this.clan_crest_id = clan == null ? 0 : clan.getCrestId();
            this.large_clan_crest_id = clan == null ? 0 : clan.getCrestLargeId();
            this.ally_id = alliance == null ? 0 : alliance.getAllyId();
            this.ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();
         }

         this.cw_level = 0;
         this.title = getVisibleTitle(player);
      }

      if (player.getPlayerAccess().GodMode && player.isInvisible()) {
         this.title = this.title + "(Invisible)";
      }

      if (player.isPolymorphed()) {
         if (NpcHolder.getInstance().getTemplate(player.getPolyId()) != null) {
            this.title = this.title + " - " + NpcHolder.getInstance().getTemplate(player.getPolyId()).name;
         } else {
            this.title = this.title + " - Polymorphed";
         }
      }

      if (player.isMounted()) {
         this._enchant = 0;
         this.mount_id = player.getMountNpcId() + 1000000;
         this.mount_type = player.getMountType();
      } else {
         this._enchant = player.getEnchantEffect();
         this.mount_id = 0;
         this.mount_type = 0;
      }

      this._pAtkRange = player.getPhysicalAttackRange();
      this.move_speed = player.getMovementSpeedMultiplier();
      this._runSpd = player.getTemplate().baseRunSpd;
      this._walkSpd = player.getTemplate().baseWalkSpd;
      this._flRunSpd = 0;
      this._flWalkSpd = 0;
      if (player.isFlying()) {
         this._flyRunSpd = this._runSpd;
         this._flyWalkSpd = this._walkSpd;
      } else {
         this._flyRunSpd = 0;
         this._flyWalkSpd = 0;
      }

      this._swimRunSpd = player.getSwimSpeed();
      this._swimWalkSpd = player.getSwimSpeed();
      this._inv = new int[17][3];
      int[] var6 = Inventory.PAPERDOLL_ORDER;
      int var8 = var6.length;

      for(int var4 = 0; var4 < var8; ++var4) {
         int PAPERDOLL_ID = var6[var4];
         this._inv[PAPERDOLL_ID][0] = player.getInventory().getPaperdollObjectId(PAPERDOLL_ID);
         this._inv[PAPERDOLL_ID][1] = player.getInventory().getPaperdollItemId(PAPERDOLL_ID);
         this._inv[PAPERDOLL_ID][2] = player.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
      }

      this._relation = player.isClanLeader() ? 64 : 0;

      GlobalEvent e;
      for(Iterator var7 = player.getEvents().iterator(); var7.hasNext(); this._relation = e.getUserRelation(player, this._relation)) {
         e = (GlobalEvent)var7.next();
      }

      this._loc = player.getLoc();
      this.obj_id = player.getObjectId();
      this.vehicle_obj_id = player.isInBoat() ? player.getBoat().getObjectId() : 0;
      this._race = player.getRace().ordinal();
      this.sex = player.getSex();
      this.base_class = player.getBaseClassId();
      this.level = player.getLevel();
      this._exp = player.getExp();
      this._str = player.getSTR();
      this._dex = player.getDEX();
      this._con = player.getCON();
      this._int = player.getINT();
      this._wit = player.getWIT();
      this._men = player.getMEN();
      this.curHp = (int)player.getCurrentHp();
      this.maxHp = player.getMaxHp();
      this.curMp = (int)player.getCurrentMp();
      this.maxMp = player.getMaxMp();
      this.curLoad = player.getCurrentLoad();
      this.maxLoad = player.getMaxLoad();
      this._sp = player.getIntSp();
      this._patk = player.getPAtk((Creature)null);
      this._patkspd = player.getPAtkSpd();
      this._pdef = player.getPDef((Creature)null);
      this.evasion = player.getEvasionRate((Creature)null);
      this.accuracy = player.getAccuracy();
      this.crit = player.getCriticalHit((Creature)null, (Skill)null);
      this._matk = player.getMAtk((Creature)null, (Skill)null);
      this._matkspd = player.getMAtkSpd();
      this._mdef = player.getMDef((Creature)null, (Skill)null);
      this.pvp_flag = player.getPvpFlag();
      this.karma = player.getKarma();
      this.attack_speed = player.getAttackSpeedMultiplier();
      this.col_radius = player.getColRadius();
      this.col_height = player.getColHeight();
      this.hair_style = player.getHairStyle();
      this.hair_color = player.getHairColor();
      this.face = player.getFace();
      this.gm_commands = !player.isGM() && !player.getPlayerAccess().CanUseGMCommand ? 0 : 1;
      this.clan_id = player.getClanId();
      this.ally_id = player.getAllyId();
      this.private_store = player.getPrivateStoreType();
      this.can_crystalize = player.getSkillLevel(248) > 0 ? 1 : 0;
      this.pk_kills = player.getPkKills();
      this.pvp_kills = player.getPvpKills();
      this.cubics = (EffectCubic[])player.getCubics().toArray(new EffectCubic[player.getCubics().size()]);
      this._abnormalEffect = player.getAbnormalEffect();
      this._abnormalEffect2 = player.getAbnormalEffect2();
      this.ClanPrivs = player.getClanPrivileges();
      this.rec_left = player.getGivableRec();
      this.rec_have = player.getReceivedRec();
      this.InventoryLimit = player.getInventoryLimit();
      this.class_id = player.getClassId().getId();
      this.maxCp = player.getMaxCp();
      this.curCp = (int)player.getCurrentCp();
      this._team = player.getTeam();
      this.noble = player.isNoble() || player.isGM() && Config.GM_HERO_AURA ? 1 : 0;
      this.hero = !player.isHero() && (!player.isGM() || !Config.GM_HERO_AURA) ? 0 : 1;
      this._fishLoc = player.getFishLoc();
      this.name_color = getVisibleNameColor(player);
      this.running = player.isRunning() ? 1 : 0;
      this.pledge_class = player.getPledgeClass();
      this.pledge_type = player.getPledgeType();
      this.title_color = getVisibleTitleColor(player);
      this.transformation = player.getTransformation();
      this.agathion = player.getAgathionId();
      this.partyRoom = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getLeader() == player;
      this.isFlying = player.isInFlyingTransform();
      this.can_writeImpl = true;
   }

   private static boolean isHideClanAndAllyInfo(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_CLAN_ALY_INFO) {
         return true;
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_CLAN_ALY_INFO) {
         return true;
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_CLAN_ALY_INFO) {
         return true;
      } else {
         return player.isInRop() && ConfigRoomOfPower.ROP_HIDE_CLAN_ALY_INFO;
      }
   }

   private static String getVisibleName(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_NAME) {
         return "Player";
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_NAME) {
         return "TreasureHunter";
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_NAME) {
         return "BossHunter";
      } else {
         return player.isInRop() && ConfigRoomOfPower.ROP_HIDE_NAME ? "BossHunter" : player.getName();
      }
   }

   private static String getVisibleTitle(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_TITLE) {
         return "";
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_TITLE) {
         return "";
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_TITLE) {
         return "";
      } else {
         return player.isInRop() && ConfigRoomOfPower.ROP_HIDE_TITLE ? "" : player.getTitle();
      }
   }

   private static int getVisibleTitleColor(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_TITLE_COLOR) {
         return 16777079;
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_TITLE_COLOR) {
         return 16777079;
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_TITLE_COLOR) {
         return 16777079;
      } else {
         return player.isInRop() && ConfigRoomOfPower.ROP_HIDE_TITLE_COLOR ? 16777079 : player.getTitleColor();
      }
   }

   private static int getVisibleNameColor(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_NAME_COLOR) {
         return 16777215;
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_NAME_COLOR) {
         return 16777215;
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_NAME_COLOR) {
         return 16777215;
      } else {
         return player.isInRop() && ConfigRoomOfPower.ROP_HIDE_NAME_COLOR ? 16777215 : player.getNameColor();
      }
   }

   protected final void writeImpl() {
      if (this.can_writeImpl) {
         this.writeC(4);
         this.writeD(this._loc.x);
         this.writeD(this._loc.y);
         this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
         this.writeD(this._loc.h);
         this.writeD(this.obj_id);
         this.writeS(this._name);
         this.writeD(this._race);
         this.writeD(this.sex);
         this.writeD(this.base_class);
         this.writeD(this.level);
         this.writeQ(this._exp);
         this.writeD(this._str);
         this.writeD(this._dex);
         this.writeD(this._con);
         this.writeD(this._int);
         this.writeD(this._wit);
         this.writeD(this._men);
         this.writeD(this.maxHp);
         this.writeD(this.curHp);
         this.writeD(this.maxMp);
         this.writeD(this.curMp);
         this.writeD(this._sp);
         this.writeD(this.curLoad);
         this.writeD(this.maxLoad);
         this.writeD(this._pAtkRange);
         int[] var1 = Inventory.PAPERDOLL_ORDER;
         int var2 = var1.length;

         int var3;
         int PAPERDOLL_ID;
         for(var3 = 0; var3 < var2; ++var3) {
            PAPERDOLL_ID = var1[var3];
            this.writeD(this._inv[PAPERDOLL_ID][0]);
         }

         var1 = Inventory.PAPERDOLL_ORDER;
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            PAPERDOLL_ID = var1[var3];
            this.writeD(this._inv[PAPERDOLL_ID][1]);
         }

         var1 = Inventory.PAPERDOLL_ORDER;
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            PAPERDOLL_ID = var1[var3];
            this.writeD(this._inv[PAPERDOLL_ID][2]);
         }

         this.writeD(this._patk);
         this.writeD(this._patkspd);
         this.writeD(this._pdef);
         this.writeD(this.evasion);
         this.writeD(this.accuracy);
         this.writeD(this.crit);
         this.writeD(this._matk);
         this.writeD(this._matkspd);
         this.writeD(this._patkspd);
         this.writeD(this._mdef);
         this.writeD(this.pvp_flag);
         this.writeD(this.karma);
         this.writeD(this._runSpd);
         this.writeD(this._walkSpd);
         this.writeD(this._swimRunSpd);
         this.writeD(this._swimWalkSpd);
         this.writeD(this._flRunSpd);
         this.writeD(this._flWalkSpd);
         this.writeD(this._flyRunSpd);
         this.writeD(this._flyWalkSpd);
         this.writeF(this.move_speed);
         this.writeF(this.attack_speed);
         this.writeF(this.col_radius);
         this.writeF(this.col_height);
         this.writeD(this.hair_style);
         this.writeD(this.hair_color);
         this.writeD(this.face);
         this.writeD(this.gm_commands);
         this.writeS(this.title);
         this.writeD(this.clan_id);
         this.writeD(this.clan_crest_id);
         this.writeD(this.ally_id);
         this.writeD(this.ally_crest_id);
         this.writeD(this._relation);
         this.writeC(this.mount_type);
         this.writeC(this.private_store);
         this.writeC(this.can_crystalize);
         this.writeD(this.pk_kills);
         this.writeD(this.pvp_kills);
         this.writeH(this.cubics.length);
         EffectCubic[] var5 = this.cubics;
         var2 = var5.length;

         for(var3 = 0; var3 < var2; ++var3) {
            EffectCubic cubic = var5[var3];
            this.writeH(cubic == null ? 0 : cubic.getId());
         }

         this.writeC(this.partyRoom ? 1 : 0);
         this.writeD(this._abnormalEffect);
         this.writeC(this.isFlying ? 2 : 0);
         this.writeD(this.ClanPrivs);
         this.writeH(this.rec_left);
         this.writeH(this.rec_have);
         this.writeD(this.mount_id);
         this.writeH(this.InventoryLimit);
         this.writeD(this.class_id);
         this.writeD(0);
         this.writeD(this.maxCp);
         this.writeD(this.curCp);
         this.writeC(this._enchant);
         this.writeC(this._team.ordinal());
         this.writeD(this.large_clan_crest_id);
         this.writeC(this.noble);
         this.writeC(this.hero);
         this.writeC(0);
         this.writeD(this._fishLoc.x);
         this.writeD(this._fishLoc.y);
         this.writeD(this._fishLoc.z);
         this.writeD(this.name_color);
         this.writeC(this.running);
         this.writeD(this.pledge_class);
         this.writeD(this.pledge_type);
         this.writeD(this.title_color);
         this.writeD(this.cw_level);
      }
   }
}
