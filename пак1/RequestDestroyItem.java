package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.dao.AgathionDAO;
import l2.gameserver.dao.PetDAO;
import l2.gameserver.data.xml.holder.PetDataHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Log.ItemLog;
import services.ConfigAgathion;

public class RequestDestroyItem extends L2GameClientPacket {
   private int _objectId;
   private long _count;

   protected void readImpl() {
      this._objectId = this.readD();
      this._count = (long)this.readD();
   }

   protected void runImpl() {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
         if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
         } else if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
         } else if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
         } else if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
         } else {
            long count = this._count;
            ItemInstance item = activeChar.getInventory().getItemByObjectId(this._objectId);
            if (item == null) {
               activeChar.sendActionFailed();
            } else if (count < 1L) {
               activeChar.sendPacket(SystemMsg.YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT);
            } else if (activeChar.getPet() != null && activeChar.getPet().getControlItemObjId() == item.getObjectId()) {
               activeChar.sendPacket(SystemMsg.AS_YOUR_PET_IS_CURRENTLY_OUT_ITS_SUMMONING_ITEM_CANNOT_BE_DESTROYED);
            } else if (activeChar.getAgathion() != null && activeChar.getAgathion().getControlItemObjId() == item.getObjectId()) {
               activeChar.sendPacket(SystemMsg.AS_YOUR_PET_IS_CURRENTLY_OUT_ITS_SUMMONING_ITEM_CANNOT_BE_DESTROYED);
            } else if (!activeChar.isGM() && !item.canBeDestroyed(activeChar)) {
               activeChar.sendPacket(SystemMsg.THIS_ITEM_CANNOT_BE_DISCARDED);
            } else {
               if (this._count > item.getCount()) {
                  count = item.getCount();
               }

               boolean crystallize = item.canBeCrystallized(activeChar);
               int crystalAmount = item.getTemplate().getCrystalCount();
               int crystalId = item.getTemplate().getCrystalItemId();
               if (crystallize) {
                  if (Config.DWARF_AUTOMATICALLY_CRYSTALLIZE_ON_ITEM_DELETE) {
                     int level = activeChar.getSkillLevel(248);
                     if (level < 1 || item.getTemplate().getCrystalType().externalOrdinal > level) {
                        crystallize = false;
                     }
                  } else {
                     crystallize = false;
                  }
               }

               Log.LogItem(activeChar, ItemLog.Delete, item, count);
               if (!activeChar.getInventory().destroyItemByObjectId(this._objectId, count)) {
                  activeChar.sendActionFailed();
               } else {
                  if (PetDataHolder.getInstance().getByControlItemId(item) != null) {
                     PetDAO.deletePet(item, activeChar);
                  }

                  if (ConfigAgathion.AGATHION_DATAS.containsKey(item.getItemId())) {
                     AgathionDAO.deleteAgathion(item, activeChar);
                  }

                  if (crystallize) {
                     activeChar.sendPacket(SystemMsg.THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED);
                     if (crystalId > 0) {
                        ItemFunctions.addItem(activeChar, crystalId, (long)crystalAmount, true);
                     }
                  } else {
                     activeChar.sendPacket(SystemMessage.removeItems(item.getItemId(), count));
                  }

                  activeChar.sendChanges();
               }
            }
         }
      }
   }
}
