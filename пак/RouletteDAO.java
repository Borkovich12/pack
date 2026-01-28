package services.community.custom.roulette.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.community.custom.roulette.model.RouletteStat;

public class RouletteDAO {
   private static final Logger _log = LoggerFactory.getLogger(RouletteDAO.class);
   private static final RouletteDAO INSTANCE = new RouletteDAO();

   private RouletteDAO() {
   }

   public static RouletteDAO getInstance() {
      return INSTANCE;
   }

   public void updateStats(RouletteStat stats) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("REPLACE INTO roulette_stats (item_id, item_name, item_enchant, value, count) VALUES (?,?,?,?,?)");
         statement.setInt(1, stats.getItemId());
         statement.setString(2, stats.getItemName());
         statement.setInt(3, stats.getItemEnchant());
         statement.setLong(4, stats.getValue());
         statement.setInt(5, stats.getCount());
         statement.execute();
      } catch (Exception var8) {
         _log.error("RouletteDAO:updateStats(int, long): " + var8, var8);
      } finally {
         DbUtils.closeQuietly(con, statement);
      }

   }

   public List<RouletteStat> restoreStats() {
      List<RouletteStat> result = new ArrayList();
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rset = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("SELECT * FROM roulette_stats");
         rset = statement.executeQuery();

         while(rset.next()) {
            int itemId = rset.getInt("item_id");
            String itemName = rset.getString("item_name");
            int itemEnchant = rset.getInt("item_enchant");
            long value = rset.getLong("value");
            int count = rset.getInt("count");
            result.add(new RouletteStat(itemId, itemName, itemEnchant, value, count));
         }
      } catch (Exception var14) {
         _log.error("RouletteDAO:restoreStats(): " + var14, var14);
      } finally {
         DbUtils.closeQuietly(con, statement, rset);
      }

      return result;
   }
}
