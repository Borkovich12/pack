package events.TopKiller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopKillerDAO {
   private static final Logger LOGGER = LoggerFactory.getLogger(TopKillerDAO.class);
   private static final TopKillerDAO _instance = new TopKillerDAO();

   public static TopKillerDAO getInstance() {
      return _instance;
   }

   public List<KillerData> loadTopKillers() {
      List<KillerData> result = new ArrayList();
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rset = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("SELECT * FROM `top_killer`");
         rset = statement.executeQuery();

         while(rset.next()) {
            int objectId = rset.getInt("object_id");
            int dinoPoint = rset.getInt("dino_point");
            int pvpPoint = rset.getInt("pvp_point");
            int pkPoint = rset.getInt("pk_point");
            result.add(new KillerData(objectId, dinoPoint, pvpPoint, pkPoint));
         }
      } catch (Exception var12) {
         LOGGER.warn("TopKillerDAO: Could not restore killers data!", var12);
      } finally {
         DbUtils.closeQuietly(con, statement, rset);
      }

      return result;
   }

   public void saveTopKillers(Collection<KillerData> topKillers) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("REPLACE INTO `top_killer` (object_id, dino_point, pvp_point, pk_point) VALUES (?, ?, ?, ?)");
         Iterator var4 = topKillers.iterator();

         while(var4.hasNext()) {
            KillerData data = (KillerData)var4.next();
            statement.setInt(1, data.getObjectId());
            statement.setInt(2, data.getDinoPoint());
            statement.setInt(3, data.getPkPoint());
            statement.setInt(4, data.getPkPoint());
            statement.addBatch();
         }

         statement.executeBatch();
      } catch (SQLException var9) {
         LOGGER.warn("TopKillerDAO: Could not save top killers!", var9);
      } finally {
         DbUtils.closeQuietly(con, statement);
      }

   }

   public void saveTopKiller(KillerData killerData) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("REPLACE INTO `top_killer` (object_id, dino_point, pvp_point, pk_point) VALUES (?, ?, ?, ?)");
         statement.setInt(1, killerData.getObjectId());
         statement.setInt(2, killerData.getDinoPoint());
         statement.setInt(3, killerData.getPvpPoint());
         statement.setInt(4, killerData.getPkPoint());
         statement.executeUpdate();
      } catch (SQLException var8) {
         LOGGER.warn("TopKillerDAO: Could not save top killer!", var8);
      } finally {
         DbUtils.closeQuietly(con, statement);
      }

   }

   public void updateTopKiller(KillerData killerData) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("UPDATE `top_killer` SET dino_point=?, pvp_point=?, pk_point=? WHERE object_id=?");
         statement.setInt(1, killerData.getDinoPoint());
         statement.setInt(2, killerData.getPvpPoint());
         statement.setInt(3, killerData.getPkPoint());
         statement.setInt(4, killerData.getObjectId());
         statement.executeUpdate();
      } catch (SQLException var8) {
         LOGGER.warn("TopKillerDAO: Could not update top killer!", var8);
      } finally {
         DbUtils.closeQuietly(con, statement);
      }

   }

   public void clearTopKillersData() {
      Connection connection = null;
      PreparedStatement statement = null;
      Object rset = null;

      try {
         connection = DatabaseFactory.getInstance().getConnection();
         statement = connection.prepareStatement("DELETE FROM `top_killer`");
         statement.executeUpdate();
      } catch (SQLException var8) {
         LOGGER.warn("TopKillerDAO: Can't remove definitions data ", var8);
      } finally {
         DbUtils.closeQuietly(connection, statement, (ResultSet)rset);
      }

   }
}
