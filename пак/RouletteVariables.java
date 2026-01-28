package services.community.custom.roulette;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.database.mysql;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouletteVariables {
   private static final Logger _log = LoggerFactory.getLogger(RouletteVariables.class);
   private static RouletteVariables _instance;
   private final Map<String, Pair<String, Long>> variables = new ConcurrentHashMap();

   private RouletteVariables() {
      this.loadVariables();
   }

   public static RouletteVariables getInstance() {
      if (_instance == null) {
         _instance = new RouletteVariables();
      }

      return _instance;
   }

   public void setVar(String name, String value, long expirationTime) {
      this.variables.put(name, Pair.of(value, expirationTime));
      mysql.set("REPLACE INTO roulette_variables (name, value, expire_time) VALUES (?,?,?)", new Object[]{name, value, expirationTime});
   }

   public void setVar(String name, String value) {
      this.variables.put(name, Pair.of(value, -1L));
      mysql.set("REPLACE INTO roulette_variables (name, value, expire_time) VALUES (?,?,-1)", new Object[]{name, value});
   }

   public void unsetVar(String name) {
      if (name != null) {
         if (this.variables.remove(name) != null) {
            mysql.set("DELETE FROM `roulette_variables` WHERE AND `name`=? LIMIT 1", new Object[]{name});
         }

      }
   }

   public String getVar(String name) {
      Pair<String, Long> pair = (Pair)this.variables.get(name);
      if (pair == null) {
         return null;
      } else if ((Long)pair.getRight() > 0L && (Long)pair.getRight() <= System.currentTimeMillis() / 1000L) {
         this.unsetVar(name);
         return null;
      } else {
         return (String)pair.getLeft();
      }
   }

   public boolean getVarB(String name, boolean defaultVal) {
      Pair<String, Long> pair = (Pair)this.variables.get(name);
      if (pair == null) {
         return defaultVal;
      } else if ((Long)pair.getRight() > 0L && (Long)pair.getRight() <= System.currentTimeMillis() / 1000L) {
         this.unsetVar(name);
         return defaultVal;
      } else {
         String var = (String)pair.getLeft();
         return !var.equals("0") && !var.equalsIgnoreCase("false");
      }
   }

   public boolean getVarB(String name) {
      Pair<String, Long> pair = (Pair)this.variables.get(name);
      if (pair == null) {
         return false;
      } else if ((Long)pair.getRight() > 0L && (Long)pair.getRight() <= System.currentTimeMillis() / 1000L) {
         this.unsetVar(name);
         return false;
      } else {
         String var = (String)pair.getLeft();
         return !var.equals("0") && !var.equalsIgnoreCase("false");
      }
   }

   public int getVarInt(String name, int defaultVal) {
      try {
         Pair<String, Long> pair = (Pair)this.variables.get(name);
         if (pair != null) {
            int var = Integer.parseInt((String)pair.getLeft());
            if ((Long)pair.getRight() > 0L && (Long)pair.getRight() <= System.currentTimeMillis() / 1000L) {
               this.unsetVar(name);
               return defaultVal;
            }

            return var;
         }
      } catch (Exception var5) {
      }

      return defaultVal;
   }

   public long getVarLong(String name, long defaultVal) {
      try {
         Pair<String, Long> pair = (Pair)this.variables.get(name);
         if (pair != null) {
            long var = Long.parseLong((String)pair.getLeft());
            if ((Long)pair.getRight() <= 0L || (Long)pair.getRight() > System.currentTimeMillis() / 1000L) {
               return var;
            }

            this.unsetVar(name);
         }
      } catch (Exception var7) {
      }

      return defaultVal;
   }

   public long getVarExpireTime(String name) {
      if (this.variables.containsKey(name)) {
         Pair<String, Long> var = (Pair)this.variables.get(name);
         if (var != null) {
            return (Long)var.getRight();
         }
      }

      return 0L;
   }

   private void loadVariables() {
      Connection con = null;
      PreparedStatement offline = null;
      ResultSet rs = null;
      ArrayList vars = new ArrayList();

      String name;
      try {
         con = DatabaseFactory.getInstance().getConnection();
         offline = con.prepareStatement("SELECT * FROM roulette_variables");
         rs = offline.executeQuery();

         while(rs.next()) {
            String name = rs.getString("name");
            name = rs.getString("value");
            long time = rs.getLong("expire_time");
            this.variables.put(name, Pair.of(name, time));
            if (time != -1L && time * 1000L < System.currentTimeMillis()) {
               vars.add(name);
            }
         }
      } catch (Exception var12) {
         _log.error("", var12);
      } finally {
         DbUtils.closeQuietly(con, offline, rs);
      }

      if (!vars.isEmpty()) {
         Iterator var14 = vars.iterator();

         while(var14.hasNext()) {
            name = (String)var14.next();
            this.unsetVar(name);
         }
      }

   }
}
