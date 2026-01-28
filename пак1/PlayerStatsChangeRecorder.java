package l2.gameserver.model.actor.recorder;

import l2.commons.collections.CollectionUtils;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.s2c.ExStorageMaxCount;

public final class PlayerStatsChangeRecorder extends CharStatsChangeRecorder<Player> {
   public static final int BROADCAST_KARMA = 8;
   public static final int SEND_STORAGE_INFO = 16;
   public static final int SEND_MAX_LOAD = 32;
   public static final int SEND_CUR_LOAD = 64;
   public static final int BROADCAST_CHAR_INFO2 = 128;
   private int _maxCp;
   private int _maxLoad;
   private int _curLoad;
   private int[] _attackElement = new int[6];
   private int[] _defenceElement = new int[6];
   private long _exp;
   private int _sp;
   private int _karma;
   private int _pk;
   private int _pvp;
   private int _fame;
   private int _inventory;
   private int _warehouse;
   private int _clan;
   private int _trade;
   private int _recipeDwarven;
   private int _recipeCommon;
   private int _partyRoom;
   private String _title = "";
   private int _cubicsHash;

   public PlayerStatsChangeRecorder(Player activeChar) {
      super(activeChar);
   }

   protected void refreshStats() {
      this._maxCp = this.set(4, this._maxCp, ((Player)this._activeChar).getMaxCp());
      super.refreshStats();
      this._maxLoad = this.set(34, this._maxLoad, ((Player)this._activeChar).getMaxLoad());
      this._curLoad = this.set(64, this._curLoad, ((Player)this._activeChar).getCurrentLoad());
      Element[] var1 = Element.VALUES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Element e = var1[var3];
         this._attackElement[e.getId()] = this.set(2, this._attackElement[e.getId()], ((Player)this._activeChar).getAttack(e));
         this._defenceElement[e.getId()] = this.set(2, this._defenceElement[e.getId()], ((Player)this._activeChar).getDefence(e));
      }

      this._exp = this.set(2, this._exp, ((Player)this._activeChar).getExp());
      this._sp = this.set(2, this._sp, ((Player)this._activeChar).getIntSp());
      this._pk = this.set(2, this._pk, ((Player)this._activeChar).getPkKills());
      this._pvp = this.set(2, this._pvp, ((Player)this._activeChar).getPvpKills());
      this._karma = this.set(8, this._karma, ((Player)this._activeChar).getKarma());
      this._inventory = this.set(16, this._inventory, ((Player)this._activeChar).getInventoryLimit());
      this._warehouse = this.set(16, this._warehouse, ((Player)this._activeChar).getWarehouseLimit());
      this._clan = this.set(16, this._clan, Config.WAREHOUSE_SLOTS_CLAN);
      this._trade = this.set(16, this._trade, ((Player)this._activeChar).getTradeLimit());
      this._recipeDwarven = this.set(16, this._recipeDwarven, ((Player)this._activeChar).getDwarvenRecipeLimit());
      this._recipeCommon = this.set(16, this._recipeCommon, ((Player)this._activeChar).getCommonRecipeLimit());
      this._cubicsHash = this.set(1, this._cubicsHash, CollectionUtils.hashCode(((Player)this._activeChar).getCubics()));
      this._partyRoom = this.set(1, this._partyRoom, ((Player)this._activeChar).getMatchingRoom() != null && ((Player)this._activeChar).getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && ((Player)this._activeChar).getMatchingRoom().getLeader() == this._activeChar ? ((Player)this._activeChar).getMatchingRoom().getId() : 0);
      this._team = (TeamType)this.set(128, this._team, ((Player)this._activeChar).getTeam());
      this._title = this.set(1, this._title, ((Player)this._activeChar).getTitle());
   }

   protected void onSendChanges() {
      super.onSendChanges();
      if ((this._changes & 128) == 128) {
         ((Player)this._activeChar).broadcastCharInfo();
         if (((Player)this._activeChar).getPet() != null) {
            ((Player)this._activeChar).getPet().broadcastCharInfo();
         }

         if (((Player)this._activeChar).getAgathion() != null) {
            ((Player)this._activeChar).getAgathion().broadcastCharInfo();
         }
      }

      if ((this._changes & 1) == 1) {
         ((Player)this._activeChar).broadcastCharInfo();
      } else if ((this._changes & 2) == 2) {
         ((Player)this._activeChar).sendUserInfo();
      }

      if ((this._changes & 64) == 64) {
         ((Player)this._activeChar).sendStatusUpdate(false, false, new int[]{14});
      }

      if ((this._changes & 32) == 32) {
         ((Player)this._activeChar).sendStatusUpdate(false, false, new int[]{15});
      }

      if ((this._changes & 8) == 8) {
         ((Player)this._activeChar).sendStatusUpdate(true, false, new int[]{27});
      }

      if ((this._changes & 16) == 16) {
         ((Player)this._activeChar).sendPacket(new ExStorageMaxCount((Player)this._activeChar));
      }

   }
}
