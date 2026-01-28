package l2.gameserver.model.instances;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.AutoAttackStart;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcInfo;
import l2.gameserver.scripts.Events;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ConfigAgathion;

public class AgathionInstance extends Summon {
   private static final Logger _log = LoggerFactory.getLogger(AgathionInstance.class);
   private final int _controlItemObjId;
   private boolean _respawned;
   private Future<?> _petInfoTask;

   public static final AgathionInstance restore(ItemInstance control, NpcTemplate template, Player owner) {
      AgathionInstance agathion = null;
      Connection con = null;
      PreparedStatement statement = null;
      ResultSet rset = null;

      Object var8;
      try {
         con = DatabaseFactory.getInstance().getConnection();
         statement = con.prepareStatement("SELECT `objId` FROM `agathions` WHERE `item_obj_id`=?");
         statement.setInt(1, control.getObjectId());
         rset = statement.executeQuery();
         if (!rset.next()) {
            agathion = new AgathionInstance(IdFactory.getInstance().getNextId(), template, owner, control);
            AgathionInstance var7 = agathion;
            return var7;
         }

         agathion = new AgathionInstance(rset.getInt("objId"), template, owner, control);
         agathion.setRespawned(true);
         agathion.setName("");
         agathion.setCurrentHpMp((double)agathion.getMaxHp(), (double)agathion.getMaxMp(), true);
         agathion.setCurrentCp((double)agathion.getMaxCp());
         return agathion;
      } catch (Exception var12) {
         _log.error("Could not restore Agathion data from item: " + control + "!", var12);
         var8 = null;
      } finally {
         DbUtils.closeQuietly(con, statement, rset);
      }

      return (AgathionInstance)var8;
   }

   public AgathionInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control) {
      super(objectId, template, owner);
      this._controlItemObjId = control.getObjectId();
   }

   public boolean isAttackable(Creature attacker) {
      return false;
   }

   public boolean isAutoAttackable(Creature attacker) {
      return false;
   }

   protected void onSpawn() {
      super.onSpawn();
   }

   protected void onDespawn() {
      super.onSpawn();
   }

   public boolean consumeItem(int itemConsumeId, long itemCount) {
      return this.getPlayer().getInventory().destroyItemByItemId(itemConsumeId, itemCount);
   }

   public void onAction(Player player, boolean shift) {
      if (this.isFrozen()) {
         player.sendPacket(ActionFail.STATIC);
      } else if (Events.onAction(player, this, shift)) {
         player.sendPacket(ActionFail.STATIC);
      } else {
         player.sendActionFailed();
      }
   }

   protected void onDelete() {
      Player owner = this.getPlayer();
      owner.setAgathion(owner, (AgathionInstance)null);
      this.stopDecay();
      super.onDelete();
   }

   private void destroyControlItem() {
      Player owner = this.getPlayer();
      if (this.getControlItemObjId() != 0) {
         if (owner.getInventory().destroyItemByObjectId(this.getControlItemObjId(), 1L)) {
            Connection con = null;
            PreparedStatement statement = null;

            try {
               con = DatabaseFactory.getInstance().getConnection();
               statement = con.prepareStatement("DELETE FROM `agathions` WHERE `item_obj_id`=?");
               statement.setInt(1, this.getControlItemObjId());
               statement.execute();
            } catch (Exception var8) {
               _log.warn("could not delete agathion:" + var8);
            } finally {
               DbUtils.closeQuietly(con, statement);
            }

         }
      }
   }

   protected void onDeath(Creature killer) {
      super.onDeath(killer);
   }

   public void doRevive(double percent) {
      this.doRevive();
   }

   public void doRevive() {
      this.stopDecay();
      super.doRevive();
      this.setRunning();
   }

   public ItemInstance getActiveWeaponInstance() {
      return null;
   }

   public WeaponTemplate getActiveWeaponItem() {
      return null;
   }

   public ItemInstance getControlItem() {
      Player owner = this.getPlayer();
      if (owner == null) {
         return null;
      } else {
         int item_obj_id = this.getControlItemObjId();
         return item_obj_id == 0 ? null : owner.getInventory().getItemByObjectId(item_obj_id);
      }
   }

   public int getControlItemObjId() {
      return this._controlItemObjId;
   }

   public int getCurrentFed() {
      return 0;
   }

   public long getWearedMask() {
      return WeaponType.SWORD.mask();
   }

   public final int getLevel() {
      return 80;
   }

   public int getMaxFed() {
      return 0;
   }

   public int getRunSpeed() {
      Player player = this.getPlayer();
      if (player == null) {
         return super.getRunSpeed();
      } else {
         return ConfigAgathion.AGATHION_COPY_OWNER_RUN_SPD ? player.getRunSpeed() : super.getRunSpeed();
      }
   }

   public ItemInstance getSecondaryWeaponInstance() {
      return null;
   }

   public WeaponTemplate getSecondaryWeaponItem() {
      return null;
   }

   public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
   }

   public void displayReceiveDamageMessage(Creature attacker, int damage) {
   }

   public int getSummonType() {
      return 2;
   }

   public NpcTemplate getTemplate() {
      return (NpcTemplate)this._template;
   }

   public boolean isRespawned() {
      return this._respawned;
   }

   public void setRespawned(boolean respawned) {
      this._respawned = respawned;
   }

   public void store() {
      if (this.getControlItemObjId() != 0) {
         Connection con = null;
         PreparedStatement statement = null;

         try {
            con = DatabaseFactory.getInstance().getConnection();
            String req;
            if (!this.isRespawned()) {
               req = "INSERT INTO `agathions` (`objId`,`item_obj_id`) VALUES (?,?)";
            } else {
               req = "UPDATE `agathions` SET `objId`=? WHERE `item_obj_id` = ?";
            }

            statement = con.prepareStatement(req);
            statement.setInt(1, this.getObjectId());
            statement.setInt(2, this._controlItemObjId);
            statement.executeUpdate();
         } catch (Exception var7) {
            _log.error("Could not store agathion data!", var7);
         } finally {
            DbUtils.closeQuietly(con, statement);
         }

         this._respawned = true;
      }
   }

   protected void onDecay() {
      this.destroyControlItem();
      super.onDecay();
   }

   public void unSummon() {
      this.store();
      super.unSummon();
   }

   public void updateControlItem() {
      ItemInstance controlItem = this.getControlItem();
      if (controlItem != null) {
         Player owner = this.getPlayer();
         owner.sendPacket((new InventoryUpdate()).addModifiedItem(controlItem));
      }
   }

   public double getExpPenalty() {
      return 0.0D;
   }

   public boolean isAgathion() {
      return true;
   }

   public int getEffectIdentifier() {
      return 0;
   }

   public boolean isInvul() {
      return true;
   }

   public long getNonAggroTime() {
      return Long.MAX_VALUE;
   }

   public TeamType getTeam() {
      return TeamType.NONE;
   }

   public String toString() {
      return this.getClass().getSimpleName() + " " + (this.getName() == null ? "no name exist" : this.getName()) + " (" + this.getNpcId() + ":" + this.getControlItemObjId() + ") owner " + this.getPlayer();
   }

   public void broadcastCharInfoImpl() {
      Player owner = this.getPlayer();
      Iterator var2 = World.getAroundPlayers(this).iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         player.sendPacket((new NpcInfo(this, player)).update());
      }

   }

   private void sendPetInfoImpl() {
      Player owner = this.getPlayer();
      owner.sendPacket(new NpcInfo(this, owner));
   }

   public void sendPetInfo() {
      if (Config.USER_INFO_INTERVAL == 0L) {
         if (this._petInfoTask != null) {
            this._petInfoTask.cancel(false);
            this._petInfoTask = null;
         }

         this.sendPetInfoImpl();
      } else if (this._petInfoTask == null) {
         this._petInfoTask = ThreadPoolManager.getInstance().schedule(new AgathionInstance.PetInfoTask(), Config.USER_INFO_INTERVAL);
      }
   }

   public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
      List<L2GameServerPacket> list = new ArrayList();
      Player owner = this.getPlayer();
      list.add(new NpcInfo(this, forPlayer));
      if (this.isInCombat()) {
         list.add(new AutoAttackStart(this.getObjectId()));
      }

      if (this.isMoving() || this.isFollowing()) {
         list.add(this.movePacket());
      }

      return list;
   }

   private class PetInfoTask extends RunnableImpl {
      private PetInfoTask() {
      }

      public void runImpl() throws Exception {
         AgathionInstance.this.sendPetInfoImpl();
         AgathionInstance.this._petInfoTask = null;
      }

      // $FF: synthetic method
      PetInfoTask(Object x1) {
         this();
      }
   }
}
