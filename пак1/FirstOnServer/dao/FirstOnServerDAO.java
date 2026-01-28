package events.FirstOnServer.dao;

import events.FirstOnServer.template.FirstOnServerRecord;
import events.FirstOnServer.type.FirstOnServerType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstOnServerDAO {
   private static final Logger _log = LoggerFactory.getLogger(FirstOnServerDAO.class);
   private static final FirstOnServerDAO _instance = new FirstOnServerDAO();
   public static final String SELECT_SQL_QUERY = "SELECT * FROM first_on_server";
   public static final String INSERT_SQL_QUERY = "INSERT INTO first_on_server(type, id, winner_id, winner_name) VALUES (?, ?, ?, ?)";
   public static final String DELETE_SQL_QUERY = "DELETE FROM first_on_server WHERE type=? AND id=?";
   public static final String CLEAR_SQL = "TRUNCATE `first_on_server`";

   public static FirstOnServerDAO getInstance() {
      return _instance;
   }

   public List<FirstOnServerRecord> load() {
      List<FirstOnServerRecord> result = new ArrayList();
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rset = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("SELECT * FROM first_on_server");
         rset = statement.executeQuery();

         while(rset.next()) {
            FirstOnServerType type = FirstOnServerType.getTypeById(rset.getInt("type"));
            int id = rset.getInt("id");
            int winnerId = rset.getInt("winner_id");
            String winnerName = rset.getString("winner_name");
            result.add(new FirstOnServerRecord(type, id, winnerId, winnerName));
         }
      } catch (Exception var12) {
         _log.error("FirstOnServerDAO:load(): " + var12, var12);
      } finally {
         DbUtils.closeQuietly(con, statement, rset);
      }

      return result;
   }

   public void insert(FirstOnServerRecord record) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("INSERT INTO first_on_server(type, id, winner_id, winner_name) VALUES (?, ?, ?, ?)");
         statement.setInt(1, record.getType().ordinal());
         statement.setInt(2, record.getId());
         statement.setInt(3, record.getWinnerId());
         statement.setString(4, record.getWinnerName());
         statement.execute();
      } catch (Exception var8) {
         _log.error("FirstOnServerDAO:insert(FirstOnServerType, int, int, String): " + var8, var8);
      } finally {
         DbUtils.closeQuietly(con, statement);
      }

   }

   public void delete(FirstOnServerType type, int id) {
      Connection con = null;
      PreparedStatement statement = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("DELETE FROM first_on_server WHERE type=? AND id=?");
         statement.setInt(1, type.ordinal());
         statement.setInt(2, id);
         statement.execute();
      } catch (Exception var9) {
         _log.error("FirstOnServerDAO:delete(Residence): " + var9, var9);
      } finally {
         DbUtils.closeQuietly(con, statement);
      }

   }

   public void clear() {
      Connection con = null;
      PreparedStatement statement = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("TRUNCATE `first_on_server`");
         statement.execute();
      } catch (Exception var7) {
         _log.error("FirstOnServerDAO:delete(Residence): " + var7, var7);
      } finally {
         DbUtils.closeQuietly(con, statement);
      }

   }
}
