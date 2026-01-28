package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.AgathionInstance;
import l2.gameserver.model.items.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgathionDAO {
   private static final Logger _log = LoggerFactory.getLogger(AgathionDAO.class);

   public static void deleteAgathion(ItemInstance item, Creature owner) {
      int objectId = 0;
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rset = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("SELECT `objId` FROM `agathions` WHERE `item_obj_id`=?");
         statement.setInt(1, item.getObjectId());

         for(rset = statement.executeQuery(); rset.next(); objectId = rset.getInt("objId")) {
         }

         DbUtils.close(statement, rset);
         AgathionInstance agathion = owner.getAgathion();
         if (agathion != null && agathion.getObjectId() == objectId) {
            agathion.unSummon();
         }

         statement = con.prepareStatement("DELETE FROM `agathions` WHERE `item_obj_id`=?");
         statement.setInt(1, item.getObjectId());
         statement.execute();
      } catch (Exception var10) {
         _log.error("could not restore agathion objectid:", var10);
      } finally {
         DbUtils.closeQuietly(con, statement, rset);
      }

   }

   public static void unSummonAgathion(ItemInstance oldItem, Creature owner) {
      int agathionObjectId = 0;
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rset = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("SELECT `objId` FROM `agathions` WHERE `item_obj_id`=?");
         statement.setInt(1, oldItem.getObjectId());

         for(rset = statement.executeQuery(); rset.next(); agathionObjectId = rset.getInt("objId")) {
         }

         if (owner == null) {
            return;
         }

         AgathionInstance agathion = owner.getAgathion();
         if (agathion != null && agathion.getObjectId() == agathionObjectId) {
            agathion.unSummon();
         }
      } catch (Exception var10) {
         _log.error("could not restore agathion objectid:", var10);
      } finally {
         DbUtils.closeQuietly(con, statement, rset);
      }

   }
}
