package services.community.custom.progress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.listener.game.OnCharacterDeleteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProgressCounter {
   private static final Logger LOG = LoggerFactory.getLogger(ProgressCounter.class);
   private final int _objid;
   private final int _stageId;
   private final int _achId;

   public ProgressCounter(int objid, int stageId, int achId) {
      this._objid = objid;
      this._stageId = stageId;
      this._achId = achId;
   }

   public static ProgressCounter makeDBStorableCounter(int objid, int stageId, int achId) {
      return new ProgressCounter.AchievementCounterDb(objid, stageId, achId);
   }

   public int getObjid() {
      return this._objid;
   }

   public int getStageId() {
      return this._stageId;
   }

   public int getAchId() {
      return this._achId;
   }

   public abstract int getVal();

   public abstract void setVal(int var1);

   public boolean isStorable() {
      return false;
   }

   public abstract void store();

   public int incrementAndGetValue() {
      this.setVal(this.getVal() + 1);
      return this.getVal();
   }

   public static void deleteProgressAchievemntsFromDd(int charObjId) {
      Connection con = null;
      PreparedStatement pstmt = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         pstmt = con.prepareStatement("DELETE FROM `progress` WHERE `objId` = ?");
         pstmt.setInt(1, charObjId);
         pstmt.executeUpdate();
      } catch (SQLException var7) {
         LOG.error("Can't delete counter for " + charObjId);
      } finally {
         DbUtils.closeQuietly(con, pstmt);
      }

   }

   static {
      CharacterDAO.getInstance().getCharacterDeleteListenerList().add(new ProgressCounter.AchievementCounterOnCharacterDeleteListener());
   }

   private static final class AchievementCounterDb extends ProgressCounter {
      private volatile Integer _val = null;

      public AchievementCounterDb(int objid, int stageId, int achId) {
         super(objid, stageId, achId);
      }

      private int getVal0() {
         Connection con = null;
         PreparedStatement pstmt = null;
         ResultSet rset = null;

         int var4;
         try {
            con = DatabaseFactory.getInstance().getConnection();
            pstmt = con.prepareStatement("SELECT `value` AS `value` FROM `progress` WHERE  `objId` = ? AND `stageId` = ? AND `achId` = ?");
            pstmt.setInt(1, this.getObjid());
            pstmt.setInt(2, this.getStageId());
            pstmt.setInt(3, this.getAchId());
            rset = pstmt.executeQuery();
            if (!rset.next()) {
               return 0;
            }

            var4 = rset.getInt("value");
         } catch (SQLException var8) {
            ProgressCounter.LOG.error("Can't load counter for " + this.getObjid() + "(" + this.getAchId() + ")", var8);
            return 0;
         } finally {
            DbUtils.closeQuietly(con, pstmt, rset);
         }

         return var4;
      }

      public int getVal() {
         if (this._val == null) {
            this._val = this.getVal0();
         }

         return this._val;
      }

      public void setVal(int val) {
         this._val = val;
      }

      public boolean isStorable() {
         return true;
      }

      public void store() {
         Connection con = null;
         PreparedStatement pstmt = null;

         try {
            con = DatabaseFactory.getInstance().getConnection();
            pstmt = con.prepareStatement("REPLACE INTO `progress` (`objId`, `stageId`, `achId`, `value`) VALUES (?, ?, ?, ?)");
            pstmt.setInt(1, this.getObjid());
            pstmt.setInt(2, this.getStageId());
            pstmt.setInt(3, this.getAchId());
            pstmt.setInt(4, this.getVal());
            pstmt.executeUpdate();
         } catch (SQLException var7) {
            ProgressCounter.LOG.error("Can't store counter for " + this.getObjid() + "(" + this.getAchId() + ")");
         } finally {
            DbUtils.closeQuietly(con, pstmt);
         }

      }
   }

   private static class AchievementCounterOnCharacterDeleteListener implements OnCharacterDeleteListener {
      private AchievementCounterOnCharacterDeleteListener() {
      }

      public void onCharacterDelete(int charObjId) {
         ProgressCounter.deleteProgressAchievemntsFromDd(charObjId);
      }

      // $FF: synthetic method
      AchievementCounterOnCharacterDeleteListener(Object x0) {
         this();
      }
   }
}
