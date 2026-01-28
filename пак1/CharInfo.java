package l2.gameserver.network.l2.s2c;

import events.BossHunting.ConfigBossHunting;
import events.CaptureCastle.ConfigCaptureCastle;
import events.RoomOfPower.ConfigRoomOfPower;
import events.TreasureHunting.ConfigTreasureHunting;
import java.util.Iterator;
import java.util.Map;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zones;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.skills.effects.EffectCubic;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ConfigChangeZone;
import services.ZoneEquipHolder;

public class CharInfo extends L2GameServerPacket {
   private static final Logger _log = LoggerFactory.getLogger(CharInfo.class);
   private Player _player;
   private int[][] _inv;
   private int _mAtkSpd;
   private int _pAtkSpd;
   private int _runSpd;
   private int _walkSpd;
   private int _swimSpd;
   private int _flRunSpd;
   private int _flWalkSpd;
   private int _flyRunSpd;
   private int _flyWalkSpd;
   private Location _loc;
   private Location _fishLoc;
   private String _name;
   private String _title;
   private int _objId;
   private int _race;
   private int _sex;
   private int base_class;
   private int pvp_flag;
   private int karma;
   private int rec_have;
   private double moveAnimMod;
   private double atkAnimMod;
   private double col_radius;
   private double col_height;
   private int hair_style;
   private int hair_color;
   private int face;
   private int _abnormalEffect;
   private int _abnormalEffect2;
   private int clan_id;
   private int clan_crest_id;
   private int large_clan_crest_id;
   private int ally_id;
   private int ally_crest_id;
   private int class_id;
   private int _sit;
   private int _run;
   private int _combat;
   private int _dead;
   private int private_store;
   private int _enchant;
   private int _noble;
   private int _hero;
   private int _fishing;
   private int mount_type;
   private int plg_class;
   private int pledge_type;
   private int clan_rep_score;
   private int cw_level;
   private int mount_id;
   private int _nameColor;
   private int _title_color;
   private int _transform;
   private int _agathion;
   private int _clanBoatObjectId;
   private EffectCubic[] cubics;
   private boolean _isPartyRoomLeader;
   private boolean _isFlying;
   private TeamType _team;
   public static final int[] PAPERDOLL_ORDER = new int[]{0, 6, 7, 8, 9, 10, 11, 12, 13, 7, 15, 16};

   public CharInfo(Player cha) {
      this((Creature)cha);
   }

   public CharInfo(Creature cha) {
      if (cha == null) {
         System.out.println("CharInfo: cha is null!");
         Thread.dumpStack();
      } else if (!cha.isInvisible()) {
         if (!cha.isDeleted()) {
            Player player = cha.getPlayer();
            if (player != null) {
               this._player = player;
               if (this._loc == null) {
                  this._loc = cha.getLoc();
               }

               this._objId = cha.getObjectId();
               if (player.getTransformationName() != null || player.getReflection() == ReflectionManager.GIRAN_HARBOR && player.getPrivateStoreType() != 0) {
                  this._name = player.getTransformationName() != null ? player.getTransformationName() : player.getName();
                  this._title = player.getTransformationTitle() != null ? player.getTransformationTitle() : "";
                  this.clan_id = 0;
                  this.clan_crest_id = 0;
                  this.ally_id = 0;
                  this.ally_crest_id = 0;
                  this.large_clan_crest_id = 0;
                  if (player.isCursedWeaponEquipped()) {
                     this.cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
                  }
               } else {
                  this._name = this.getVisibleName(player);
                  if (player.getPrivateStoreType() != 0) {
                     this._title = "";
                  } else if (!player.isConnected()) {
                     this._title = player.getDisconnectedTitle();
                     this._title_color = player.getDisconnectedTitleColor();
                  } else {
                     this._title = getVisibleTitle(player);
                     this._title_color = getVisibleTitleColor(player);
                  }

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

               this._inv = new int[17][2];
               int[] var7 = PAPERDOLL_ORDER;
               int var8 = var7.length;

               for(int var5 = 0; var5 < var8; ++var5) {
                  int PAPERDOLL_ID = var7[var5];
                  this._inv[PAPERDOLL_ID][0] = player.getInventory().getPaperdollItemId(PAPERDOLL_ID);
                  this._inv[PAPERDOLL_ID][1] = player.getInventory().getPaperdollAugmentationId(PAPERDOLL_ID);
               }

               this._mAtkSpd = player.getMAtkSpd();
               this._pAtkSpd = player.getPAtkSpd();
               this.moveAnimMod = player.getMovementSpeedMultiplier();
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

               this._swimSpd = player.getSwimSpeed();
               this._race = player.getBaseTemplate().race.ordinal();
               this._sex = player.getSex();
               this.base_class = player.getBaseClassId();
               this.pvp_flag = player.getPvpFlag();
               this.karma = player.getKarma();
               this.atkAnimMod = player.getAttackSpeedMultiplier();
               this.col_radius = player.getColRadius();
               this.col_height = player.getColHeight();
               this.hair_style = player.getHairStyle();
               this.hair_color = player.getHairColor();
               this.face = player.getFace();
               if (this.clan_id > 0 && player.getClan() != null) {
                  this.clan_rep_score = player.getClan().getReputationScore();
               } else {
                  this.clan_rep_score = 0;
               }

               this._sit = player.isSitting() ? 0 : 1;
               this._run = player.isRunning() ? 1 : 0;
               this._combat = player.isInCombat() ? 1 : 0;
               this._dead = player.isAlikeDead() ? 1 : 0;
               this.private_store = player.isInObserverMode() ? 7 : player.getPrivateStoreType();
               this.cubics = (EffectCubic[])player.getCubics().toArray(new EffectCubic[player.getCubics().size()]);
               this._abnormalEffect = player.getAbnormalEffect();
               this._abnormalEffect2 = player.getAbnormalEffect2();
               this.rec_have = player.getReceivedRec();
               this.class_id = player.getClassId().getId();
               this._team = player.getTeam();
               this._noble = player.isNoble() ? 1 : 0;
               this._hero = !player.isHero() && (!player.isGM() || !Config.GM_HERO_AURA) ? 0 : 1;
               this._fishing = player.isFishing() ? 1 : 0;
               this._fishLoc = player.getFishLoc();
               this._nameColor = getVisibleNameColor(player);
               this.plg_class = player.getPledgeClass();
               this.pledge_type = player.getPledgeType();
               this._transform = player.getTransformation();
               this._agathion = player.getAgathionId();
               this._isPartyRoomLeader = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getLeader() == player;
               this._isFlying = player.isInFlyingTransform();
            }
         }
      }
   }

   private static int getPaperdollItemId(int PAPERDOLL_ID, Player player, Map<Integer, Integer> displayZoneItemList) {
      int haveItemId = player.getInventory().getPaperdollItemId(PAPERDOLL_ID);
      if (displayZoneItemList == null) {
         return haveItemId;
      } else {
         Integer displayId = (Integer)displayZoneItemList.get(PAPERDOLL_ID);
         if (displayId == null) {
            return haveItemId;
         } else {
            ItemInstance item = player.getInventory().getPaperdollItem(PAPERDOLL_ID);
            if (item != null) {
               if (displayId > 0) {
                  return displayId;
               }
            } else if (PAPERDOLL_ID == 15) {
               item = player.getInventory().getPaperdollItem(16);
               if (item != null) {
                  displayId = (Integer)displayZoneItemList.get(16);
                  if (displayId > 0) {
                     return displayId;
                  }
               }
            }

            return displayId > 0 ? displayId : haveItemId;
         }
      }
   }

   public static int getVisualBaseClassId(Player player, int visibleRaceId) {
      if (visibleRaceId != -1) {
         if (visibleRaceId == player.getBaseTemplate().race.ordinal()) {
            return player.getBaseClassId();
         }

         if (visibleRaceId == 0) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 0;
               }

               return 0;
            }

            if (player.getSex() == 0) {
               return 10;
            }

            return 10;
         }

         if (visibleRaceId == 1) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 18;
               }

               return 18;
            }

            if (player.getSex() == 0) {
               return 25;
            }

            return 25;
         }

         if (visibleRaceId == 2) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 31;
               }

               return 31;
            }

            if (player.getSex() == 0) {
               return 31;
            }

            return 31;
         }

         if (visibleRaceId == 3) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 44;
               }

               return 44;
            }

            if (player.getSex() == 0) {
               return 49;
            }

            return 49;
         }

         if (visibleRaceId == 4) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 53;
               }

               return 53;
            }

            if (player.getSex() == 0) {
               return 53;
            }

            return 53;
         }
      }

      return player.getBaseClassId();
   }

   public static double getVisibleColHeight(Player player, int visibleRaceId) {
      if (visibleRaceId != -1) {
         if (visibleRaceId == 0) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 23.0D;
               }

               return 23.5D;
            }

            if (player.getSex() == 0) {
               return 22.8D;
            }

            return 22.5D;
         }

         if (visibleRaceId == 1) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 24.0D;
               }

               return 23.0D;
            }

            if (player.getSex() == 0) {
               return 24.0D;
            }

            return 23.0D;
         }

         if (visibleRaceId == 2) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 24.0D;
               }

               return 23.5D;
            }

            if (player.getSex() == 0) {
               return 24.0D;
            }

            return 23.5D;
         }

         if (visibleRaceId == 3) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 28.0D;
               }

               return 27.0D;
            }

            if (player.getSex() == 0) {
               return 27.5D;
            }

            return 25.5D;
         }

         if (visibleRaceId == 4) {
            if (player.getSex() == 0) {
               return 18.0D;
            }

            return 19.0D;
         }
      }

      return player.getColHeight();
   }

   public static double getVisibleColRadius(Player player, int visibleRaceId) {
      if (visibleRaceId != -1) {
         if (visibleRaceId == 0) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 9.0D;
               }

               return 8.0D;
            }

            if (player.getSex() == 0) {
               return 7.5D;
            }

            return 6.5D;
         }

         if (visibleRaceId == 1) {
            return 7.5D;
         }

         if (visibleRaceId == 2) {
            if (player.getSex() == 0) {
               return 7.5D;
            }

            return 7.0D;
         }

         if (visibleRaceId == 3) {
            if (!player.isMageClass()) {
               if (player.getSex() == 0) {
                  return 11.0D;
               }

               return 7.0D;
            }

            if (player.getSex() == 0) {
               return 7.0D;
            }

            return 8.0D;
         }

         if (visibleRaceId == 4) {
            if (player.getSex() == 0) {
               return 9.0D;
            }

            return 5.0D;
         }
      }

      return player.getColRadius();
   }

   public static int getVisibleRace(Player player, int visibleRaceId) {
      return visibleRaceId != -1 ? visibleRaceId : player.getBaseTemplate().race.ordinal();
   }

   public static int getZoneHideRace(Player player) {
      Zones zones = player.getZones();
      if (zones != null) {
         Iterator var2 = zones.iterator();

         while(var2.hasNext()) {
            Zone zone = (Zone)var2.next();
            if (zone != null) {
               boolean isChangeZone = zone.getParams().getBool("IsChangeZone", false);
               if (isChangeZone) {
                  String activeChangeZone = ServerVariables.getString("active_change_zone", "");
                  int raceId = zone.getParams().getInteger("ChangeZoneRaceId", -1);
                  if (zone.getName().equalsIgnoreCase(activeChangeZone) && raceId != -1) {
                     return raceId;
                  }
               }
            }
         }
      }

      return -1;
   }

   private static int getVisibleNameColor(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_NAME_COLOR) {
         return 16777215;
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_NAME_COLOR) {
         return 16777215;
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_NAME_COLOR) {
         return 16777215;
      } else if (player.isInRop() && ConfigRoomOfPower.ROP_HIDE_NAME_COLOR) {
         return 16777215;
      } else {
         return ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_NAME_COLOR_ENABLE && isInZoneHideInfo(player) ? ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_NAME_COLOR : player.getNameColor();
      }
   }

   private static int getVisibleTitleColor(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_TITLE_COLOR) {
         return 16777079;
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_TITLE_COLOR) {
         return 16777079;
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_TITLE_COLOR) {
         return 16777079;
      } else if (player.isInRop() && ConfigRoomOfPower.ROP_HIDE_TITLE_COLOR) {
         return 16777079;
      } else {
         return ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_TITLE_COLOR_ENABLE && isInZoneHideInfo(player) ? ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_TITLE_COLOR : player.getTitleColor();
      }
   }

   private static String getVisibleTitle(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_TITLE) {
         return "";
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_TITLE) {
         return "";
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_TITLE) {
         return "";
      } else if (player.isInRop() && ConfigRoomOfPower.ROP_HIDE_TITLE) {
         return "";
      } else {
         return ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_TITLE_ENABLE && isInZoneHideInfo(player) ? ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_TITLE : player.getTitle();
      }
   }

   private String getVisibleName(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_NAME) {
         return "Player";
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_NAME) {
         return "TreasureHunter";
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_NAME) {
         return "BossHunter";
      } else if (player.isInRop() && ConfigRoomOfPower.ROP_HIDE_NAME) {
         return "RoomOfPower";
      } else {
         return ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_NAME_ENABLE && isInZoneHideInfo(player) ? ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_NAME : player.getName();
      }
   }

   private static boolean isHideClanAndAllyInfo(Player player) {
      if (player.isInCaptureCastleEvent() && ConfigCaptureCastle.CAPTURE_CASTLE_HIDE_CLAN_ALY_INFO) {
         return true;
      } else if (player.isInTreasureHunting() && ConfigTreasureHunting.TREASURE_HUNTING_HIDE_CLAN_ALY_INFO) {
         return true;
      } else if (player.isInBossHunting() && ConfigBossHunting.BOSS_HUNTING_HIDE_CLAN_ALY_INFO) {
         return true;
      } else if (player.isInRop() && ConfigRoomOfPower.ROP_HIDE_CLAN_ALY_INFO) {
         return true;
      } else {
         return ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_CLAN_ALY_INFO_ENABLE && isInZoneHideInfo(player);
      }
   }

   public static boolean isInZoneHideInfo(Player player) {
      Zones zones = player.getZones();
      if (zones != null) {
         Iterator var2 = zones.iterator();

         while(var2.hasNext()) {
            Zone zone = (Zone)var2.next();
            if (zone != null) {
               boolean isChangeZone = zone.getParams().getBool("IsChangeZone", false);
               if (isChangeZone) {
                  String activeChangeZone = ServerVariables.getString("active_change_zone", "");
                  if (zone.getName().equalsIgnoreCase(activeChangeZone)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected final void writeImpl() {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
         if (this._objId != 0) {
            if (activeChar.getObjectId() == this._objId) {
               _log.error("You cant send CharInfo about his character to active user!!!");
            } else {
               this.writeC(3);
               this.writeD(this._loc.x);
               this.writeD(this._loc.y);
               this.writeD(this._loc.z + Config.CLIENT_Z_SHIFT);
               this.writeD(this._loc.h);
               this.writeD(this._objId);
               this.writeS(this._name);
               int visibleRaceId = -1;
               if (ConfigChangeZone.ACTIVE_CHANGE_ZONE_HIDE_RACE_ENABLE) {
                  visibleRaceId = getZoneHideRace(this._player);
               }

               if (visibleRaceId != -1) {
                  this.writeD(visibleRaceId);
               } else {
                  this.writeD(this._race);
               }

               this.writeD(this._sex);
               int baseVisualClass = getVisualBaseClassId(this._player, visibleRaceId);
               if (visibleRaceId != -1) {
                  this.writeD(baseVisualClass);
               } else {
                  this.writeD(this.base_class);
               }

               int[] var4 = PAPERDOLL_ORDER;
               int var5 = var4.length;

               int var6;
               int PAPERDOLL_ID;
               int displayId;
               for(var6 = 0; var6 < var5; ++var6) {
                  PAPERDOLL_ID = var4[var6];
                  Map<Integer, Integer> displayZoneItemList = ZoneEquipHolder.getInstance().getDisplayItem(this._player);
                  displayId = getPaperdollItemId(PAPERDOLL_ID, this._player, displayZoneItemList);
                  if (displayId > 0) {
                     this.writeD(displayId);
                  } else {
                     this.writeD(this._inv[PAPERDOLL_ID][0]);
                  }
               }

               var4 = PAPERDOLL_ORDER;
               var5 = var4.length;

               for(var6 = 0; var6 < var5; ++var6) {
                  PAPERDOLL_ID = var4[var6];
                  this.writeD(this._inv[PAPERDOLL_ID][1]);
               }

               this.writeD(this.pvp_flag);
               this.writeD(this.karma);
               this.writeD(this._mAtkSpd);
               this.writeD(this._pAtkSpd);
               this.writeD(this.pvp_flag);
               this.writeD(this.karma);
               this.writeD(this._runSpd);
               this.writeD(this._walkSpd);
               this.writeD(this._swimSpd);
               this.writeD(this._swimSpd);
               this.writeD(this._flRunSpd);
               this.writeD(this._flWalkSpd);
               this.writeD(this._flyRunSpd);
               this.writeD(this._flyWalkSpd);
               this.writeF(this.moveAnimMod);
               this.writeF(this.atkAnimMod);
               double visualColRadius = getVisibleColRadius(this._player, visibleRaceId);
               if (visibleRaceId != -1) {
                  this.writeF(visualColRadius);
               } else {
                  this.writeF(this.col_radius);
               }

               double visualColHeight = getVisibleColHeight(this._player, visibleRaceId);
               if (visibleRaceId != -1) {
                  this.writeF(visualColHeight);
               } else {
                  this.writeF(this.col_height);
               }

               this.writeD(this.hair_style);
               this.writeD(this.hair_color);
               this.writeD(this.face);
               this.writeS(this._title);
               this.writeD(this.clan_id);
               this.writeD(this.clan_crest_id);
               this.writeD(this.ally_id);
               this.writeD(this.ally_crest_id);
               this.writeD(0);
               this.writeC(this._sit);
               this.writeC(this._run);
               this.writeC(this._combat);
               this.writeC(this._dead);
               this.writeC(0);
               this.writeC(this.mount_type);
               this.writeC(this.private_store);
               this.writeH(this.cubics.length);
               EffectCubic[] var14 = this.cubics;
               displayId = var14.length;

               for(int var10 = 0; var10 < displayId; ++var10) {
                  EffectCubic cubic = var14[var10];
                  this.writeH(cubic == null ? 0 : cubic.getId());
               }

               this.writeC(this._isPartyRoomLeader ? 1 : 0);
               this.writeD(this._abnormalEffect);
               this.writeC(this._isFlying ? 2 : 0);
               this.writeH(this.rec_have);
               this.writeD(this.mount_id);
               this.writeD(this.class_id);
               this.writeD(0);
               this.writeC(this._enchant);
               this.writeC(this._team.ordinal());
               this.writeD(this.large_clan_crest_id);
               this.writeC(this._noble);
               this.writeC(this._hero);
               this.writeC(this._fishing);
               this.writeD(this._fishLoc.x);
               this.writeD(this._fishLoc.y);
               this.writeD(this._fishLoc.z);
               this.writeD(this._nameColor);
               this.writeD(this._loc.h);
               this.writeD(this.plg_class);
               this.writeD(this.pledge_type);
               this.writeD(this._title_color);
               this.writeD(this.cw_level);
               this.writeD(this.clan_rep_score);
               this.writeD(this._transform);
               this.writeD(this._agathion);
               this.writeD(1);
               this.writeD(this._abnormalEffect2);
            }
         }
      }
   }
}
