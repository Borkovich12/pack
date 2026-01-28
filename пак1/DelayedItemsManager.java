package l2.gameserver.taskmanager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.dbutils.DbUtils;
import l2.commons.listener.ListenerList;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.GameServer;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.listener.actor.player.OnDonateItemListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.items.ItemInstance.ItemLocation;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayedItemsManager extends RunnableImpl {
   private static final Logger _log = LoggerFactory.getLogger(DelayedItemsManager.class);
   private static DelayedItemsManager _instance;
   private static final String SQL_GET_DELAYED_ADD = "{CALL `lip_ItemsDelayedAdd`(?,?,?,?,?,?,?)}";
   private static final String SQL_LOAD_DELAYED_BY_OWNER_AND_STATUS = "{CALL `lip_LoadItemsDelayedByOwnerAndStatus`(?, ?)}";
   private static final String SQL_UPDATE_DELAYED_PAYMENT_STATUS = "{CALL `lip_UpdateItemsDelayedPaymentStatus`(?, ?)}";
   private static final String SQL_GET_MAX_DELAYED_PAYMENT_ID = "{CALL `lip_GetItemsDelayedMaxPaymentId`()}";
   private static final String SQL_LOAD_DELAYED_OWNERS = "{CALL `lip_LoadItemsDelayedOwners`(?)}";
   private static final Object _lock = new Object();
   private AtomicInteger last_payment_id = new AtomicInteger(0);
   private static final DelayedItemsManager.DonateItemListenerList _donateItemListenerList = new DelayedItemsManager.DonateItemListenerList();

   public static DelayedItemsManager getInstance() {
      if (_instance == null) {
         _instance = new DelayedItemsManager();
      }

      return _instance;
   }

   public DelayedItemsManager() {
      this.last_payment_id.set(this.loadLastPaymentId());
      ThreadPoolManager.getInstance().schedule(this, 10000L);
   }

   public static DelayedItemsManager.DonateItemListenerList getDonateItemListenerList() {
      return _donateItemListenerList;
   }

   private int loadLastPaymentId() {
      Connection con = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();
         int var2 = this.get_last_payment_id(con);
         return var2;
      } catch (Exception var6) {
         _log.error("", var6);
      } finally {
         DbUtils.closeQuietly(con);
      }

      return 0;
   }

   private int get_last_payment_id(Connection con) {
      ResultSet st = null;
      CallableStatement cstmt = null;
      int result = this.last_payment_id.get();

      try {
         cstmt = con.prepareCall("{CALL `lip_GetItemsDelayedMaxPaymentId`()}");
         st = cstmt.executeQuery();
         if (st.next()) {
            result = st.getInt("last");
         }
      } catch (Exception var9) {
         _log.error("", var9);
      } finally {
         DbUtils.closeQuietly(cstmt, st);
      }

      return result;
   }

   public void runImpl() throws Exception {
      Connection con = null;
      CallableStatement cstmt = null;
      ResultSet rset = null;

      try {
         con = DatabaseFactory.getInstance().getConnection();

         int last_id;
         int last_payment_id_temp;
         do {
            last_id = this.last_payment_id.get();
            last_payment_id_temp = this.get_last_payment_id(con);
            if (last_id == last_payment_id_temp) {
               break;
            }

            synchronized(_lock) {
               cstmt = con.prepareCall("{CALL `lip_LoadItemsDelayedOwners`(?)}");
               cstmt.setInt(1, this.last_payment_id.get());
               rset = cstmt.executeQuery();

               while(rset.next()) {
                  Player player;
                  if ((player = GameObjectsStorage.getPlayer(rset.getInt("owner_id"))) != null) {
                     this.loadDelayed(player, true);
                  }
               }
            }
         } while(!this.last_payment_id.compareAndSet(last_id, last_payment_id_temp));
      } catch (Exception var14) {
         _log.error("", var14);
      } finally {
         DbUtils.closeQuietly(con, cstmt, rset);
      }

      ThreadPoolManager.getInstance().schedule(this, 10000L);
   }

   public int loadDelayed(Player player, boolean notify) {
      if (player == null) {
         return 0;
      } else {
         int player_id = player.getObjectId();
         PcInventory inv = player.getInventory();
         if (inv == null) {
            return 0;
         } else {
            int restored_counter = 0;
            Connection con = null;
            CallableStatement cstmt = null;
            CallableStatement cstmt_delete = null;
            ResultSet rset = null;
            synchronized(_lock) {
               try {
                  con = DatabaseFactory.getInstance().getConnection();
                  cstmt = con.prepareCall("{CALL `lip_LoadItemsDelayedByOwnerAndStatus`(?, ?)}");
                  cstmt.setInt(1, player_id);
                  cstmt.setInt(2, 0);
                  rset = cstmt.executeQuery();
                  cstmt_delete = con.prepareCall("{CALL `lip_UpdateItemsDelayedPaymentStatus`(?, ?)}");

                  while(rset.next()) {
                     int ITEM_ID = rset.getInt("item_id");
                     long ITEM_COUNT = rset.getLong("count");
                     int ITEM_ENCHANT = rset.getInt("enchant_level");
                     int VARIATION_ID1 = rset.getInt("variationId1");
                     int VARIATION_ID2 = rset.getInt("variationId2");
                     int PAYMENT_ID = rset.getInt("payment_id");
                     int FLAGS = rset.getInt("flags");
                     String DESCRIPTION = rset.getString("description");
                     boolean stackable = ItemHolder.getInstance().getTemplate(ITEM_ID).isStackable();
                     boolean success = false;

                     for(int i = 0; (long)i < (stackable ? 1L : ITEM_COUNT); ++i) {
                        ItemInstance item = ItemFunctions.createItem(ITEM_ID);
                        if (item.isStackable()) {
                           item.setCount(ITEM_COUNT);
                        } else {
                           item.setEnchantLevel(ITEM_ENCHANT);
                           item.setVariationStat1(VARIATION_ID1);
                           item.setVariationStat2(VARIATION_ID2);
                        }

                        item.setLocation(ItemLocation.INVENTORY);
                        item.setCustomFlags(FLAGS);
                        if (ITEM_COUNT > 0L && inv.addItem(item) == null) {
                           _log.warn("Unable to delayed create item " + ITEM_ID + " request " + PAYMENT_ID);
                        } else {
                           success = true;
                           ++restored_counter;
                           if (notify && ITEM_COUNT > 0L) {
                              player.sendPacket(SystemMessage.obtainItems(ITEM_ID, stackable ? ITEM_COUNT : 1L, ITEM_ENCHANT));
                           }

                           player.sendMessage(new CustomMessage("l2.gameserver.taskmanager.DelayedItemsManager.ItemSendMessage", player, new Object[0]));
                           if (checkDonate(item, DESCRIPTION)) {
                              getDonateItemListenerList().onDonateItem(player_id, ITEM_ID, ITEM_COUNT);
                           }
                        }
                     }

                     if (success) {
                        Log.add("<add owner_id=" + player_id + " item_id=" + ITEM_ID + " count=" + ITEM_COUNT + " enchant_level=" + ITEM_ENCHANT + " variation_1=" + VARIATION_ID1 + " variation_2=" + VARIATION_ID2 + " payment_id=" + PAYMENT_ID + "/>", "delayed_add");
                        cstmt_delete.setInt(1, PAYMENT_ID);
                        cstmt_delete.setInt(2, 1);
                        cstmt_delete.execute();
                     }
                  }
               } catch (Exception var29) {
                  _log.error("Could not load delayed items for player " + player + "!", var29);
               } finally {
                  DbUtils.closeQuietly(new AutoCloseable[]{con, cstmt, cstmt_delete, rset});
               }

               return restored_counter;
            }
         }
      }
   }

   private static boolean checkDonate(ItemInstance item, String desc) {
      if (!item.isStackable()) {
         return false;
      } else if (desc.startsWith("Siege owner")) {
         return false;
      } else if (desc.startsWith("End siege")) {
         return false;
      } else if (desc.startsWith("L2Top")) {
         return false;
      } else if (desc.startsWith("MMOTop")) {
         return false;
      } else if (desc.startsWith("Reward for pawnshop")) {
         return false;
      } else if (desc.startsWith("Reward for battle")) {
         return false;
      } else if (desc.startsWith("Reward top killer")) {
         return false;
      } else if (desc.startsWith("Change zone")) {
         return false;
      } else {
         return !desc.equalsIgnoreCase("<CollectRew>");
      }
   }

   public void addDelayed(int ownerObjId, int itemTypeId, int amount, int enchant, int variation1, int variation2, String desc) {
      Connection con = null;
      CallableStatement cstmt = null;
      synchronized(_lock) {
         try {
            con = DatabaseFactory.getInstance().getConnection();
            cstmt = con.prepareCall("{CALL `lip_ItemsDelayedAdd`(?,?,?,?,?,?,?)}");
            cstmt.setInt(1, ownerObjId);
            cstmt.setInt(2, itemTypeId);
            cstmt.setInt(3, amount);
            cstmt.setInt(4, enchant);
            cstmt.setInt(5, variation1);
            cstmt.setInt(6, variation2);
            cstmt.setString(7, desc);
            cstmt.execute();
         } catch (Exception var17) {
            _log.error("Could not add delayed items " + itemTypeId + " " + amount + "(+" + enchant + ")( aug 1" + variation1 + ")( aug 2" + variation2 + ") + for objId " + ownerObjId + " desc \"" + desc + "\" !", var17);
         } finally {
            DbUtils.closeQuietly(con, cstmt);
         }

      }
   }

   public static class DonateItemListenerList extends ListenerList<GameServer> {
      public void onDonateItem(int objectId, int itemId, long itemCount) {
         this.forEachListener(OnDonateItemListener.class, (onDonateItemListener) -> {
            try {
               onDonateItemListener.onDonateItem(objectId, itemId, itemCount);
            } catch (Exception var6) {
               DelayedItemsManager._log.warn("Donate item listener", var6);
            }

         });
      }
   }
}
