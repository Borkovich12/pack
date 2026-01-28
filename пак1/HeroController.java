package l2.gameserver.model.entity.oly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.dbutils.DbUtils;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.entity.HeroDiary;
import l2.gameserver.model.entity.oly.CompetitionController.CompetitionResults;
import l2.gameserver.model.entity.oly.NoblesController.NobleRecord;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Strings;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeroController {
   private static final Logger _log = LoggerFactory.getLogger(HeroController.class);
   public static final int[] HERO_WEAPONS = new int[]{6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621};
   private static final String SQL_GET_HEROES = "SELECT  `oly_heroes`.`char_id` AS `char_id`, `oly_nobles`.`char_name` AS `name`, `oly_nobles`.`class_id` AS `class_id`, `oly_heroes`.`count` AS `count`, `oly_heroes`.`played` AS `played`, `oly_heroes`.`active` AS `active`, `oly_heroes`.`message` AS `message` FROM    `oly_heroes`,`oly_nobles` WHERE   `oly_heroes`.`char_id` = `oly_nobles`.`char_id`";
   private static final String SQL_SET_HEROES = "REPLACE INTO `oly_heroes` (`char_id`, `count`, `played`, `active`, `message`) VALUES (?, ?, ?, ?, ?)";
   private static final String SQL_DEL_HEROES = "DELETE FROM `oly_heroes` WHERE `char_id` = ?";
   private static HeroController _instance;
   private ArrayList<HeroController.HeroRecord> _currentHeroes = new ArrayList();
   private ArrayList<HeroController.HeroRecord> _allHeroes = new ArrayList();
   private static Map<Integer, List<HeroDiary>> _herodiary;
   private static Map<Integer, String> _heroMessage;

   public static HeroController getInstance() {
      if (_instance == null) {
         _instance = new HeroController();
      }

      return _instance;
   }

   private HeroController() {
      _herodiary = new ConcurrentHashMap();
      _heroMessage = new ConcurrentHashMap();
      this.loadHeroes();
   }

   private synchronized Collection<NobleRecord> CalcHeroesContenders() {
      _log.info("HeroController: Calculating heroes contenders.");
      HashMap<ClassId, NobleRecord> hero_contenders_map = new HashMap();
      Iterator var2 = NoblesController.getInstance().getNoblesRecords().iterator();

      NobleRecord nr;
      while(var2.hasNext()) {
         nr = (NobleRecord)var2.next();

         try {
            if (nr.comp_done >= Config.OLY_MIN_HERO_COMPS && nr.comp_win >= Config.OLY_MIN_HERO_WIN) {
               ClassId cid = null;
               ClassId[] var5 = ClassId.values();
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  ClassId cid2 = var5[var7];
                  if (cid2.getId() == nr.class_id && cid2.level() == 3) {
                     cid = cid2;
                  }
               }

               if (cid == null) {
                  _log.warn("HeroController: Not third or null ClassID for character '" + nr.char_name + "'");
               } else if (!hero_contenders_map.containsKey(cid)) {
                  hero_contenders_map.put(cid, nr);
               } else {
                  NobleRecord nr2 = (NobleRecord)hero_contenders_map.get(cid);
                  if (nr.points_current > nr2.points_current || nr.points_current == nr2.points_current && nr.comp_win > nr2.comp_win || nr.points_current == nr2.points_current && nr.comp_win == nr2.comp_win && nr.comp_done > nr2.comp_done) {
                     hero_contenders_map.put(cid, nr);
                  }
               }
            }
         } catch (Exception var9) {
            _log.warn("HeroController: Exception while claculating new heroes", var9);
         }
      }

      var2 = hero_contenders_map.values().iterator();

      while(var2.hasNext()) {
         nr = (NobleRecord)var2.next();
         Log.add(String.format("HeroController: %s(%d) pretended to be a hero. points_current = %d", nr.char_name, nr.char_id, nr.points_current), "olympiad");
      }

      ArrayList<NobleRecord> result = new ArrayList();
      result.addAll(hero_contenders_map.values());
      return result;
   }

   private void loadHeroes() {
      Connection conn = null;
      Statement stmt = null;
      ResultSet rset = null;

      try {
         conn = DatabaseFactory.getInstance().getConnection();
         stmt = conn.createStatement();

         HeroController.HeroRecord nr;
         for(rset = stmt.executeQuery("SELECT  `oly_heroes`.`char_id` AS `char_id`, `oly_nobles`.`char_name` AS `name`, `oly_nobles`.`class_id` AS `class_id`, `oly_heroes`.`count` AS `count`, `oly_heroes`.`played` AS `played`, `oly_heroes`.`active` AS `active`, `oly_heroes`.`message` AS `message` FROM    `oly_heroes`,`oly_nobles` WHERE   `oly_heroes`.`char_id` = `oly_nobles`.`char_id`"); rset.next(); this._allHeroes.add(nr)) {
            int char_id = rset.getInt("char_id");
            String name = rset.getString("name");
            int class_id = rset.getInt("class_id");
            int count = rset.getInt("count");
            boolean played = rset.getInt("played") != 0;
            boolean active = rset.getInt("active") != 0;
            String message = rset.getString("message");
            nr = new HeroController.HeroRecord(char_id, name, class_id, count, active, played, message);
            if (played) {
               this._currentHeroes.add(nr);
            }
         }

         this.applyClanAndAlly();
      } catch (Exception var15) {
         _log.warn("Exception while loading heroes", var15);
      } finally {
         DbUtils.closeQuietly(conn, stmt, rset);
      }

   }

   public void saveHeroes() {
      Connection conn = null;
      PreparedStatement pstmt = null;

      try {
         conn = DatabaseFactory.getInstance().getConnection();
         pstmt = conn.prepareStatement("REPLACE INTO `oly_heroes` (`char_id`, `count`, `played`, `active`, `message`) VALUES (?, ?, ?, ?, ?)");
         Iterator var3 = this._allHeroes.iterator();

         while(var3.hasNext()) {
            HeroController.HeroRecord nr = (HeroController.HeroRecord)var3.next();
            pstmt.setInt(1, nr.char_id);
            pstmt.setInt(2, nr.count);
            pstmt.setInt(3, nr.played ? 1 : 0);
            pstmt.setInt(4, nr.active ? 1 : 0);
            pstmt.setString(5, nr.message);
            pstmt.executeUpdate();
         }
      } catch (Exception var8) {
         _log.warn("Exception while saving heroes", var8);
      } finally {
         DbUtils.closeQuietly(conn, pstmt);
      }

   }

   public synchronized void removeHero(Player player) {
      HeroController.HeroRecord heroRecord = null;
      Iterator var3 = this._allHeroes.iterator();

      while(var3.hasNext()) {
         HeroController.HeroRecord nr = (HeroController.HeroRecord)var3.next();
         if (nr.char_id == player.getObjectId()) {
            heroRecord = nr;
            break;
         }
      }

      if (heroRecord != null) {
         Connection conn = null;
         PreparedStatement pstmt = null;
         Object rset = null;

         try {
            conn = DatabaseFactory.getInstance().getConnection();
            pstmt = conn.prepareStatement("DELETE FROM `oly_heroes` WHERE `char_id` = ?");
            pstmt.setInt(1, heroRecord.char_id);
            pstmt.executeUpdate();
            this._allHeroes.remove(heroRecord);
         } catch (SQLException var10) {
            _log.warn("HeroController: Can't remove Hero ", var10);
         } finally {
            DbUtils.closeQuietly(conn, pstmt, (ResultSet)rset);
         }

      }
   }

   public void renameHero(int object_id, String newname) {
      Iterator var3 = this._currentHeroes.iterator();

      while(var3.hasNext()) {
         HeroController.HeroRecord hr = (HeroController.HeroRecord)var3.next();
         if (hr != null && hr.char_id == object_id) {
            hr.name = newname;
         }
      }

   }

   private void clearHeroes() {
      _log.info("HeroController: Clearing previus season heroes.");
      mysql.set("UPDATE `oly_heroes` SET `played` = 0, `active` = 0");
      if (!this._currentHeroes.isEmpty()) {
         Iterator var1 = this._currentHeroes.iterator();

         label58:
         while(true) {
            HeroController.HeroRecord nr;
            do {
               if (!var1.hasNext()) {
                  break label58;
               }

               nr = (HeroController.HeroRecord)var1.next();
            } while(!nr.active);

            Player player = GameObjectsStorage.getPlayer(nr.char_id);
            if (player != null) {
               player.getInventory().unEquipItemInBodySlot(256);
               player.getInventory().unEquipItemInBodySlot(128);
               player.getInventory().unEquipItemInBodySlot(16384);
               player.getInventory().unEquipItemInBodySlot(65536);
               player.getInventory().unEquipItemInBodySlot(524288);
               player.getInventory().unEquipItemInBodySlot(262144);
               ItemInstance[] var4 = player.getInventory().getItems();
               int var5 = var4.length;

               int var6;
               ItemInstance item;
               for(var6 = 0; var6 < var5; ++var6) {
                  item = var4[var6];
                  if (item != null && item.isHeroWeapon()) {
                     player.getInventory().destroyItem(item);
                  }
               }

               var4 = player.getWarehouse().getItems();
               var5 = var4.length;

               for(var6 = 0; var6 < var5; ++var6) {
                  item = var4[var6];
                  if (item != null && !item.isEquipable() && item.isHeroWeapon()) {
                     player.getWarehouse().destroyItem(item);
                  }
               }

               player.unsetVar("CustomHeroEndTime");
               player.setHero(false);
               player.updatePledgeClass();
               player.broadcastUserInfo(true);
               removeAllHeroWeapons(player);
               removeSkills(player, true);
            }

            nr.played = false;
            nr.active = false;
         }
      }

      this.saveHeroes();
      this._currentHeroes.clear();
   }

   public synchronized void ComputeNewHeroNobleses() {
      _log.info("HeroController: Computing new heroes.");

      try {
         NoblesController.getInstance().SaveNobleses();
         this.saveHeroes();
         Collection<NobleRecord> hContenders = this.CalcHeroesContenders();
         this.clearHeroes();
         Iterator var2 = hContenders.iterator();

         while(var2.hasNext()) {
            NobleRecord hnr = (NobleRecord)var2.next();
            HeroController.HeroRecord hr = null;
            Iterator var5 = this._allHeroes.iterator();

            while(var5.hasNext()) {
               HeroController.HeroRecord hr2 = (HeroController.HeroRecord)var5.next();
               if (hnr.char_id == hr2.char_id) {
                  hr = hr2;
               }
            }

            if (hr == null) {
               hr = new HeroController.HeroRecord(hnr.char_id, hnr.char_name, hnr.class_id, 0, false, true, "");
               this._allHeroes.add(hr);
            }

            ++hr.count;
            hr.played = true;
            this._currentHeroes.add(hr);
         }

         this.saveHeroes();
         NoblesController.getInstance().TransactNewSeason();
         NoblesController.getInstance().ComputeRanks();
         NoblesController.getInstance().SaveNobleses();
         this.applyClanAndAlly();
      } catch (Exception var7) {
         _log.warn("HeroController: Can't compute heroes.", var7);
      }

   }

   private void applyClanAndAlly() {
      Iterator var1 = this._currentHeroes.iterator();

      while(var1.hasNext()) {
         HeroController.HeroRecord hr = (HeroController.HeroRecord)var1.next();
         if (hr != null) {
            Entry<Clan, Alliance> e = ClanTable.getInstance().getClanAndAllianceByCharId(hr.char_id);
            if (e.getKey() != null) {
               hr.clan_name = ((Clan)e.getKey()).getName();
               hr.clan_crest = ((Clan)e.getKey()).getCrestId();
            } else {
               hr.clan_name = "";
               hr.clan_crest = 0;
            }

            if (e.getValue() != null) {
               hr.ally_name = ((Alliance)e.getValue()).getAllyName();
               hr.ally_crest = ((Alliance)e.getValue()).getAllyCrestId();
            } else {
               hr.ally_name = "";
               hr.ally_crest = 0;
            }
         }
      }

   }

   public Collection<HeroController.HeroRecord> getCurrentHeroes() {
      return this._currentHeroes;
   }

   public boolean isCurrentHero(Player player) {
      if (player == null) {
         return false;
      } else {
         return this._currentHeroes.isEmpty() ? false : this.isCurrentHero(player.getObjectId());
      }
   }

   public boolean isInactiveHero(Player player) {
      return player == null ? false : this.isInactiveHero(player.getObjectId());
   }

   public void activateHero(Player player) {
      if (player != null) {
         if (!this._currentHeroes.isEmpty()) {
            Iterator var2 = this._currentHeroes.iterator();

            while(true) {
               HeroController.HeroRecord hr;
               do {
                  do {
                     if (!var2.hasNext()) {
                        return;
                     }

                     hr = (HeroController.HeroRecord)var2.next();
                  } while(hr.char_id != player.getObjectId());
               } while(!hr.played);

               hr.active = true;
               if (player.getBaseClassId() == player.getActiveClassId()) {
                  addSkills(player);
                  player.sendPacket(new SkillList(player));
               }

               player.setHero(true);
               player.unsetVar("CustomHeroEndTime");
               player.broadcastPacket(new L2GameServerPacket[]{new SocialAction(player.getObjectId(), 16)});
               player.updatePledgeClass();
               player.getPlayer().sendUserInfo(true);
               if (player.getClan() != null) {
                  Clan clan = player.getClan();
                  if (clan.getLevel() >= Config.MIN_CLAN_LEVEL_FOR_REPUTATION && Config.ADD_CLAN_REPUTATION_POINT_ON_HERO_GAIN) {
                     int incPoints = player.getClan().incReputation(Config.AMOUNT_CLAN_REPUTATION_POINT_ON_HERO_GAIN, true, "Hero:activateHero:" + player);
                     clan.broadcastToOtherOnlineMembers(((SystemMessage)(new SystemMessage(SystemMsg.CLAN_MEMBER_C1_WAS_NAMED_A_HERO_S2_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE)).addString(player.getName())).addNumber(incPoints), player);
                  }

                  clan.setCustomPoints(clan.getCustomPoints() + Config.EVENT_CLAN_HERO_POINTS);
                  if (Config.SERVICES_TOP_CLANS_STATISTIC_ANNOUNCE) {
                     Announcements.getInstance().announceByCustomMessage("scripts.services.CustomClanPointsHero", new String[]{String.valueOf(clan.getName()), String.valueOf(player.getName()), String.valueOf(Config.EVENT_CLAN_HERO_POINTS)});
                  }
               }

               player.broadcastUserInfo(true);
               this.saveHeroes();
               if (!ArrayUtils.isEmpty(Config.OLY_HERO_CUSTOM_REWARD_AMOUNT) && Config.OLY_HERO_CUSTOM_REWARD_AMOUNT.length == Config.OLY_HERO_CUSTOM_REWARD_ID.length && Config.OLY_HERO_CUSTOM_REWARD_CHANCE.length == Config.OLY_HERO_CUSTOM_REWARD_ID.length) {
                  for(int itemIdx = 0; itemIdx < Config.OLY_HERO_CUSTOM_REWARD_ID.length; ++itemIdx) {
                     if (Config.OLY_HERO_CUSTOM_REWARD_ID[itemIdx] != 0 && Config.OLY_HERO_CUSTOM_REWARD_AMOUNT[itemIdx] > 0 && Rnd.chance(Config.OLY_HERO_CUSTOM_REWARD_CHANCE[itemIdx])) {
                        ItemFunctions.addItem(player, Config.OLY_HERO_CUSTOM_REWARD_ID[itemIdx], (long)Config.OLY_HERO_CUSTOM_REWARD_AMOUNT[itemIdx], true);
                     }
                  }
               }

               player.getListeners().onBecomeHero(hr.class_id);
            }
         }
      }
   }

   public boolean isCurrentHero(int obj_id) {
      if (this._currentHeroes.isEmpty()) {
         return false;
      } else {
         Iterator var2 = this._currentHeroes.iterator();

         HeroController.HeroRecord hr;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            hr = (HeroController.HeroRecord)var2.next();
         } while(hr.char_id != obj_id);

         return hr.active && hr.played;
      }
   }

   public boolean isInactiveHero(int obj_id) {
      if (this._currentHeroes.isEmpty()) {
         return false;
      } else {
         Iterator var2 = this._currentHeroes.iterator();

         HeroController.HeroRecord hr;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            hr = (HeroController.HeroRecord)var2.next();
         } while(hr.char_id != obj_id);

         return hr.played && !hr.active;
      }
   }

   public static void addSkills(Player player) {
      addSkills(player, false);
   }

   public static void addSkills(Player player, boolean sendUpdate) {
      Iterator var2 = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.HERO).iterator();

      while(var2.hasNext()) {
         SkillLearn skillLearn = (SkillLearn)var2.next();
         Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
         if (skill != null && player.getSkillLevel(skill.getId()) < skill.getLevel()) {
            player.addSkill(skill, false);
         }
      }

      if (sendUpdate) {
         player.sendPacket(new SkillList(player));
      }

   }

   public static void removeSkills(Player player) {
      removeSkills(player, false);
   }

   public static void removeSkills(Player player, boolean sendUpdate) {
      Iterator var2 = SkillAcquireHolder.getInstance().getAllSkillLearn(player, player.getClassId(), AcquireType.HERO).iterator();

      while(var2.hasNext()) {
         SkillLearn skillLearn = (SkillLearn)var2.next();
         Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
         if (skill != null) {
            player.removeSkill(skill, false);
         }
      }

      if (sendUpdate) {
         player.sendPacket(new SkillList(player));
      }

   }

   public void showHistory(Player player, int targetClassId, int page) {
      int perpage = true;
      HeroController.HeroRecord hr = null;
      Iterator var6 = this.getCurrentHeroes().iterator();

      while(var6.hasNext()) {
         HeroController.HeroRecord hr2 = (HeroController.HeroRecord)var6.next();
         if (hr2.active && hr2.played && hr2.class_id == targetClassId) {
            hr = hr2;
         }
      }

      if (hr != null) {
         NpcHtmlMessage html = new NpcHtmlMessage(player, (NpcInstance)null);
         html.setFile("oly/monument_hero_history.htm");
         html.replace("%title%", StringHolder.getInstance().getNotNull(player, "hero.history"));
         Collection<CompetitionResults> crs_list = hr.getCompetitions();
         CompetitionResults[] crs = (CompetitionResults[])crs_list.toArray(new CompetitionResults[crs_list.size()]);
         int allStatWinner = 0;
         int allStatLoss = 0;
         int allStatTie = 0;
         CompetitionResults[] var12 = crs;
         int max = crs.length;

         for(int var14 = 0; var14 < max; ++var14) {
            CompetitionResults h = var12[var14];
            if (h.result > 0) {
               ++allStatWinner;
            } else if (h.result < 0) {
               ++allStatLoss;
            } else if (h.result == 0) {
               ++allStatTie;
            }
         }

         html.replace("%wins%", String.valueOf(allStatWinner));
         html.replace("%ties%", String.valueOf(allStatTie));
         html.replace("%losses%", String.valueOf(allStatLoss));
         int min = 15 * (page - 1);
         max = 15 * page;
         MutableInt currentWinner = new MutableInt(0);
         MutableInt currentLoss = new MutableInt(0);
         MutableInt currentTie = new MutableInt(0);
         StringBuilder b = new StringBuilder(500);

         for(int i = 0; i < crs.length; ++i) {
            CompetitionResults h = crs[i];
            if (h.result > 0) {
               currentWinner.increment();
            } else if (h.result < 0) {
               currentLoss.increment();
            } else if (h.result == 0) {
               currentTie.increment();
            }

            if (i >= min) {
               if (i >= max) {
                  break;
               }

               b.append("<tr><td>");
               b.append(h.toString(player, currentWinner, currentLoss, currentTie));
               b.append("</td></tr");
            }
         }

         if (min > 0) {
            html.replace("%buttprev%", "<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">");
            html.replace("%prev_bypass%", "_match?class=" + targetClassId + "&page=" + (page - 1));
         } else {
            html.replace("%buttprev%", "");
         }

         if (crs.length > max) {
            html.replace("%buttnext%", "<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">");
            html.replace("%prev_bypass%", "_match?class=" + targetClassId + "&page=" + (page + 1));
         } else {
            html.replace("%buttnext%", "");
         }

         html.replace("%list%", b.toString());
         player.sendPacket(html);
      }
   }

   public void loadDiary(int charId) {
      List<HeroDiary> diary = new ArrayList();
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rset = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("SELECT * FROM  `heroes_diary` WHERE `charId`=? ORDER BY `time` ASC");
         statement.setInt(1, charId);
         rset = statement.executeQuery();

         while(rset.next()) {
            long time = rset.getLong("time");
            int action = rset.getInt("action");
            int param = rset.getInt("param");
            HeroDiary d = new HeroDiary(action, time, param);
            diary.add(d);
         }

         _herodiary.put(charId, diary);
         if (Config.DEBUG) {
            _log.info("Hero System: Loaded " + diary.size() + " diary entries for Hero(object id: #" + charId + ")");
         }
      } catch (SQLException var14) {
         _log.warn("Hero System: Couldnt load Hero Diary for CharId: " + charId, var14);
      } finally {
         DbUtils.closeQuietly(con, statement, rset);
      }

   }

   public void showHeroDiary(Player activeChar, int heroclass, int page) {
      int perpage = true;
      HeroController.HeroRecord hr = null;
      Iterator var6 = this.getCurrentHeroes().iterator();

      while(var6.hasNext()) {
         HeroController.HeroRecord hr2 = (HeroController.HeroRecord)var6.next();
         if (hr2.active && hr2.played && hr2.class_id == heroclass) {
            hr = hr2;
         }
      }

      if (hr != null) {
         List<HeroDiary> mainlist = (List)_herodiary.get(hr.char_id);
         if (mainlist != null) {
            NpcHtmlMessage html = new NpcHtmlMessage(activeChar, (NpcInstance)null);
            html.setFile("oly/monument_hero_info.htm");
            html.replace("%title%", StringHolder.getInstance().getNotNull(activeChar, "hero.diary"));
            html.replace("%heroname%", hr.name);
            html.replace("%message%", hr.message);
            List<HeroDiary> list = new ArrayList(mainlist);
            Collections.reverse(list);
            boolean color = true;
            StringBuilder fList = new StringBuilder(500);
            int counter = 0;
            int breakat = 0;

            for(int i = (page - 1) * 10; i < list.size(); ++i) {
               breakat = i;
               HeroDiary diary = (HeroDiary)list.get(i);
               Entry<String, String> entry = diary.toString(activeChar);
               fList.append("<tr><td>");
               if (color) {
                  fList.append("<table width=270 bgcolor=\"131210\">");
               } else {
                  fList.append("<table width=270>");
               }

               fList.append("<tr><td width=270><font color=\"LEVEL\">" + (String)entry.getKey() + "</font></td></tr>");
               fList.append("<tr><td width=270>" + (String)entry.getValue() + "</td></tr>");
               fList.append("<tr><td>&nbsp;</td></tr></table>");
               fList.append("</td></tr>");
               color = !color;
               ++counter;
               if (counter >= 10) {
                  break;
               }
            }

            if (breakat < list.size() - 1) {
               html.replace("%buttprev%", "<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">");
               html.replace("%prev_bypass%", "_diary?class=" + heroclass + "&page=" + (page + 1));
            } else {
               html.replace("%buttprev%", "");
            }

            if (page > 1) {
               html.replace("%buttnext%", "<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=65 height=20 back=\"l2ui_ch3.smallbutton2_down\" fore=\"l2ui_ch3.smallbutton2\">");
               html.replace("%next_bypass%", "_diary?class=" + heroclass + "&page=" + (page - 1));
            } else {
               html.replace("%buttnext%", "");
            }

            html.replace("%list%", fList.toString());
            activeChar.sendPacket(html);
         }

      }
   }

   public void addHeroDiary(int playerId, int id, int param) {
      this.insertHeroDiary(playerId, id, param);
      List<HeroDiary> list = (List)_herodiary.get(playerId);
      if (list != null) {
         list.add(new HeroDiary(id, System.currentTimeMillis(), param));
      }

   }

   private void insertHeroDiary(int charId, int action, int param) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("INSERT INTO `heroes_diary` (`charId`, `time`, `action`, `param`) values(?,?,?,?)");
         statement.setInt(1, charId);
         statement.setLong(2, System.currentTimeMillis());
         statement.setInt(3, action);
         statement.setInt(4, param);
         statement.execute();
         statement.close();
      } catch (SQLException var10) {
         _log.error("SQL exception while saving DiaryData.", var10);
      } finally {
         DbUtils.closeQuietly(con, statement);
      }

   }

   public void setHeroMessage(int charId, String message) {
      HeroController.HeroRecord hr = null;
      Iterator var4 = this.getCurrentHeroes().iterator();

      while(var4.hasNext()) {
         HeroController.HeroRecord hr2 = (HeroController.HeroRecord)var4.next();
         if (hr2.active && hr2.played && hr2.char_id == charId) {
            hr = hr2;
         }
      }

      hr.message = Strings.stripSlashes(message);
   }

   public static boolean isHaveHeroWeapon(Player player) {
      int[] var1 = HERO_WEAPONS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int iid = var1[var3];
         if (player.getInventory().getCountOf(iid) > 0L) {
            return true;
         }
      }

      return false;
   }

   private static void removeAllHeroWeapons(Player player) {
      boolean removed = false;
      int[] var2 = HERO_WEAPONS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int itemId;
         for(itemId = var2[var4]; player.getInventory().destroyItemByItemId(itemId, 1L); removed = true) {
         }

         if (removed) {
            player.sendPacket((new SystemMessage(SystemMsg.S1_HAS_DISAPPEARED_BECAUSE_ITS_TIME_PERIOD_HAS_EXPIRED)).addItemName(itemId));
         }

         removed = false;
      }

   }

   public static void checkHeroWeaponary(Player player) {
      if (!player.isHero()) {
         removeAllHeroWeapons(player);
      }

   }

   public class HeroRecord {
      public int char_id;
      public int class_id;
      public int count;
      public boolean active;
      public boolean played;
      public String name;
      public String message;
      public String clan_name;
      public String ally_name;
      public int clan_crest;
      public int ally_crest;
      public Collection<CompetitionResults> competitions;

      private HeroRecord(int _char_id, String _name, int _class_id, int _count, boolean _active, boolean _played, String _message) {
         this.char_id = _char_id;
         this.name = _name;
         this.count = _count;
         this.class_id = _class_id;
         this.active = _active;
         this.played = _played;
         this.message = _message;
      }

      public Collection<CompetitionResults> getCompetitions() {
         if (this.competitions == null) {
            this.competitions = CompetitionController.getInstance().getCompetitionResults(this.char_id, OlyController.getInstance().getCurrentSeason() - 1);
         }

         return this.competitions;
      }

      // $FF: synthetic method
      HeroRecord(int x1, String x2, int x3, int x4, boolean x5, boolean x6, String x7, Object x8) {
         this(x1, x2, x3, x4, x5, x6, x7);
      }
   }
}
