package l2.gameserver.handler.items;

import java.util.Iterator;
import java.util.List;
import l2.commons.util.RandomUtils;
import l2.gameserver.data.xml.holder.VariationChanceHolder;
import l2.gameserver.data.xml.holder.VariationGroupHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.ExPutCommissionResultForVariationMake;
import l2.gameserver.network.l2.s2c.ExPutIntensiveResultForVariationMake;
import l2.gameserver.network.l2.s2c.ExPutItemResultForVariationCancel;
import l2.gameserver.network.l2.s2c.ExPutItemResultForVariationMake;
import l2.gameserver.network.l2.s2c.ExShowRefineryInterface;
import l2.gameserver.network.l2.s2c.ExShowVariationCancelWindow;
import l2.gameserver.network.l2.s2c.ExVariationCancelResult;
import l2.gameserver.network.l2.s2c.ExVariationResult;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.ShortCutRegister;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.support.VariationChanceData;
import l2.gameserver.templates.item.support.VariationGroupData;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefineryHandler implements IRefineryHandler {
   private static final RefineryHandler _instance = new RefineryHandler();
   protected static final Logger LOG = LoggerFactory.getLogger(RefineryHandler.class);

   public static RefineryHandler getInstance() {
      return _instance;
   }

   private RefineryHandler() {
   }

   public void onInitRefinery(Player player) {
      if (!Functions.CheckPlayerConditions(player)) {
         player.sendActionFailed();
      } else {
         player.sendPacket(new IStaticPacket[]{SystemMsg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowRefineryInterface.STATIC});
      }
   }

   public void onPutTargetItem(Player player, ItemInstance targetItem) {
      if (!Functions.CheckPlayerConditions(player)) {
         player.sendActionFailed();
      } else if (targetItem.isAugmented()) {
         player.sendPacket(new IStaticPacket[]{SystemMsg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.STATIC});
      } else {
         List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetItem.getItemId());
         if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
            player.sendPacket(new IStaticPacket[]{SystemMsg.SELECT_THE_CATALYST_FOR_AUGMENTATION, new ExPutItemResultForVariationMake(targetItem.getObjectId(), true)});
         } else {
            player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
         }
      }
   }

   public void onPutMineralItem(Player player, ItemInstance targetItem, ItemInstance mineralItem) {
      if (!Functions.CheckPlayerConditions(player)) {
         player.sendPacket(ActionFail.STATIC);
      } else if (targetItem.isAugmented()) {
         player.sendPacket(new IStaticPacket[]{SystemMsg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.STATIC});
      } else {
         List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetItem.getItemId());
         if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
            int mineralItemId = mineralItem.getItemId();
            VariationGroupData variationGroupDataOfMineral = null;
            Pair<VariationChanceData, VariationChanceData> variationChanceData = VariationChanceHolder.getInstance().getVariationChanceDataForMineral(mineralItemId);
            Iterator var8 = variationGroupDataList.iterator();

            while(var8.hasNext()) {
               VariationGroupData variationGroupData = (VariationGroupData)var8.next();
               if (variationGroupData.getMineralItemId() == mineralItemId) {
                  variationGroupDataOfMineral = variationGroupData;
                  break;
               }
            }

            if (null != variationGroupDataOfMineral && null != variationChanceData) {
               if (targetItem.getTemplate().isMageItem() && variationChanceData.getRight() == null) {
                  LOG.warn("No mage variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
                  player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
               } else if (!targetItem.getTemplate().isMageItem() && variationChanceData.getLeft() == null) {
                  LOG.warn("No warrior variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
                  player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
               } else if (!mineralItem.getTemplate().testCondition(player, mineralItem, true)) {
                  player.sendPacket(ActionFail.STATIC);
               } else {
                  player.sendPacket(new IStaticPacket[]{new ExPutIntensiveResultForVariationMake(mineralItem.getObjectId(), mineralItemId, variationGroupDataOfMineral.getGemstoneItemId(), variationGroupDataOfMineral.getGemstoneItemCnt(), true), ((SystemMessage)(new SystemMessage(SystemMsg.REQUIRES_S2_S1)).addNumber(variationGroupDataOfMineral.getGemstoneItemCnt())).addItemName(variationGroupDataOfMineral.getGemstoneItemId())});
               }
            } else {
               player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
            }
         } else {
            player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
         }
      }
   }

   public void onPutGemstoneItem(Player player, ItemInstance targetItem, ItemInstance mineralItem, ItemInstance gemstoneItem, long gemstoneItemCnt) {
      if (!Functions.CheckPlayerConditions(player)) {
         player.sendPacket(ActionFail.STATIC);
      } else if (targetItem.isAugmented()) {
         player.sendPacket(new IStaticPacket[]{SystemMsg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.STATIC});
      } else {
         List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetItem.getItemId());
         if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
            int mineralItemId = mineralItem.getItemId();
            int gemstoneItemId = gemstoneItem.getItemId();
            VariationGroupData variationGroupDataOfMineral = null;
            Pair<VariationChanceData, VariationChanceData> variationChanceData = VariationChanceHolder.getInstance().getVariationChanceDataForMineral(mineralItemId);
            Iterator var12 = variationGroupDataList.iterator();

            while(var12.hasNext()) {
               VariationGroupData variationGroupData = (VariationGroupData)var12.next();
               if (variationGroupData.getMineralItemId() == mineralItemId && variationGroupData.getGemstoneItemId() == gemstoneItemId) {
                  variationGroupDataOfMineral = variationGroupData;
                  break;
               }
            }

            if (null != variationGroupDataOfMineral && variationChanceData != null) {
               if ((variationChanceData.getLeft() == null || ((VariationChanceData)variationChanceData.getLeft()).getMineralItemId() == variationGroupDataOfMineral.getMineralItemId()) && (variationChanceData.getRight() == null || ((VariationChanceData)variationChanceData.getRight()).getMineralItemId() == variationGroupDataOfMineral.getMineralItemId())) {
                  if (targetItem.getTemplate().isMageItem() && variationChanceData.getRight() == null) {
                     LOG.warn("No mage variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
                     player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
                  } else if (!targetItem.getTemplate().isMageItem() && variationChanceData.getLeft() == null) {
                     LOG.warn("No warrior variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
                     player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
                  } else if (!mineralItem.getTemplate().testCondition(player, mineralItem, true)) {
                     player.sendPacket(ActionFail.STATIC);
                  } else if (variationGroupDataOfMineral.getGemstoneItemCnt() <= gemstoneItemCnt && player.getInventory().getCountOf(gemstoneItemId) >= variationGroupDataOfMineral.getGemstoneItemCnt()) {
                     player.sendPacket(new IStaticPacket[]{new ExPutCommissionResultForVariationMake(gemstoneItem.getObjectId(), variationGroupDataOfMineral.getGemstoneItemCnt()), SystemMsg.PRESS_THE_AUGMENT_BUTTON_TO_BEGIN});
                  } else {
                     player.sendPacket(new IStaticPacket[]{SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT, ActionFail.STATIC});
                  }
               } else {
                  player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
               }
            } else {
               player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
            }
         } else {
            player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
         }
      }
   }

   public void onRequestRefine(Player player, ItemInstance targetItem, ItemInstance mineralItem, ItemInstance gemstoneItem, long gemstoneItemCnt) {
      if (!Functions.CheckPlayerConditions(player)) {
         player.sendPacket(ActionFail.STATIC);
      } else if (targetItem.isAugmented()) {
         player.sendPacket(new IStaticPacket[]{SystemMsg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.STATIC});
      } else {
         List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetItem.getItemId());
         if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
            int mineralItemId = mineralItem.getItemId();
            int gemstoneItemId = gemstoneItem.getItemId();
            VariationGroupData variationGroupDataOfMineral = null;
            Pair<VariationChanceData, VariationChanceData> variationChanceData = VariationChanceHolder.getInstance().getVariationChanceDataForMineral(mineralItemId);
            Iterator variation1Groups = variationGroupDataList.iterator();

            VariationGroupData variation2Groups;
            while(variation1Groups.hasNext()) {
               variation2Groups = (VariationGroupData)variation1Groups.next();
               if (variation2Groups.getMineralItemId() == mineralItemId && variation2Groups.getGemstoneItemId() == gemstoneItemId) {
                  variationGroupDataOfMineral = variation2Groups;
                  break;
               }
            }

            if (null != variationGroupDataOfMineral && variationChanceData != null) {
               if ((variationChanceData.getLeft() == null || ((VariationChanceData)variationChanceData.getLeft()).getMineralItemId() == variationGroupDataOfMineral.getMineralItemId()) && (variationChanceData.getRight() == null || ((VariationChanceData)variationChanceData.getRight()).getMineralItemId() == variationGroupDataOfMineral.getMineralItemId())) {
                  if (targetItem.getTemplate().isMageItem() && variationChanceData.getRight() == null) {
                     LOG.warn("No mage variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
                     player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
                  } else if (!targetItem.getTemplate().isMageItem() && variationChanceData.getLeft() == null) {
                     LOG.warn("No warrior variation for item " + targetItem.getItemId() + " and mineral " + mineralItem.getItemId());
                     player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
                  } else if (!mineralItem.getTemplate().testCondition(player, mineralItem, true)) {
                     player.sendPacket(ActionFail.STATIC);
                  } else if (variationGroupDataOfMineral.getGemstoneItemCnt() <= gemstoneItemCnt && player.getInventory().getCountOf(gemstoneItemId) >= variationGroupDataOfMineral.getGemstoneItemCnt()) {
                     variation1Groups = null;
                     variation2Groups = null;
                     List variation1Groups;
                     List variation2Groups;
                     if (targetItem.getTemplate().isMageItem()) {
                        variation1Groups = ((VariationChanceData)variationChanceData.getRight()).getVariation1();
                        variation2Groups = ((VariationChanceData)variationChanceData.getRight()).getVariation2();
                     } else {
                        variation1Groups = ((VariationChanceData)variationChanceData.getLeft()).getVariation1();
                        variation2Groups = ((VariationChanceData)variationChanceData.getLeft()).getVariation2();
                     }

                     List<Pair<Integer, Double>> variation1 = (List)RandomUtils.pickRandomSortedGroup(variation1Groups, 100.0D);
                     List<Pair<Integer, Double>> variation2 = (List)RandomUtils.pickRandomSortedGroup(variation2Groups, 100.0D);
                     Integer option1 = variation1 != null ? (Integer)RandomUtils.pickRandomSortedGroup(variation1, 100.0D) : 0;
                     Integer option2 = variation2 != null ? (Integer)RandomUtils.pickRandomSortedGroup(variation2, 100.0D) : 0;
                     if (player.getInventory().destroyItem(gemstoneItem, variationGroupDataOfMineral.getGemstoneItemCnt())) {
                        if (player.getInventory().destroyItem(mineralItem, 1L)) {
                           boolean equipped;
                           if (equipped = targetItem.isEquipped()) {
                              player.getInventory().unEquipItem(targetItem);
                           }

                           targetItem.setVariationStat1(option1);
                           targetItem.setVariationStat2(option2);
                           if (equipped) {
                              player.getInventory().equipItem(targetItem);
                           }

                           player.sendPacket((new InventoryUpdate()).addModifiedItem(targetItem));
                           Iterator var19 = player.getAllShortCuts().iterator();

                           while(var19.hasNext()) {
                              ShortCut sc = (ShortCut)var19.next();
                              if (sc.getId() == targetItem.getObjectId() && sc.getType() == 1) {
                                 player.sendPacket(new ShortCutRegister(player, sc));
                              }
                           }

                           player.sendChanges();
                           player.sendPacket(new ExVariationResult(option1, option2, 1));
                           player.getListeners().onAugmentItem(targetItem, option1, option2);
                        }
                     }
                  } else {
                     player.sendPacket(new IStaticPacket[]{SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT, ActionFail.STATIC});
                  }
               } else {
                  player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
               }
            } else {
               player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
            }
         } else {
            player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
         }
      }
   }

   public void onInitRefineryCancel(Player player) {
      if (Functions.CheckPlayerConditions(player)) {
         player.sendPacket(new IStaticPacket[]{SystemMsg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC});
      }
   }

   public void onPutTargetCancelItem(Player player, ItemInstance targetCancelItem) {
      if (!Functions.CheckPlayerConditions(player)) {
         player.sendPacket(ActionFail.STATIC);
      } else {
         List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetCancelItem.getItemId());
         if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
            if (!targetCancelItem.isAugmented()) {
               player.sendPacket(new IStaticPacket[]{SystemMsg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM, ActionFail.STATIC});
            } else {
               VariationGroupData variationGroupData = (VariationGroupData)variationGroupDataList.get(0);
               if (variationGroupData == null) {
                  player.sendPacket(ActionFail.STATIC);
               } else {
                  player.sendPacket(new ExPutItemResultForVariationCancel(targetCancelItem, variationGroupData.getCancelPrice(), true));
               }
            }
         } else {
            player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
         }
      }
   }

   public void onRequestCancelRefine(Player player, ItemInstance targetCancelItem) {
      if (!Functions.CheckPlayerConditions(player)) {
         player.sendPacket(ActionFail.STATIC);
      } else {
         List<VariationGroupData> variationGroupDataList = VariationGroupHolder.getInstance().getDataForItemId(targetCancelItem.getItemId());
         if (variationGroupDataList != null && !variationGroupDataList.isEmpty()) {
            if (!targetCancelItem.isAugmented()) {
               player.sendPacket(new IStaticPacket[]{SystemMsg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM, ActionFail.STATIC});
            } else {
               VariationGroupData variationGroupData = (VariationGroupData)variationGroupDataList.get(0);
               if (variationGroupData == null) {
                  player.sendPacket(ActionFail.STATIC);
               } else {
                  long price = variationGroupData.getCancelPrice();
                  if (price < 0L) {
                     player.sendPacket(ExVariationCancelResult.FAIL_PACKET);
                  }

                  if (!player.reduceAdena(price, true)) {
                     player.sendPacket(new IStaticPacket[]{ActionFail.STATIC, SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA});
                  } else {
                     boolean equipped;
                     if (equipped = targetCancelItem.isEquipped()) {
                        player.getInventory().unEquipItem(targetCancelItem);
                     }

                     targetCancelItem.setVariationStat1(0);
                     targetCancelItem.setVariationStat2(0);
                     if (equipped) {
                        player.getInventory().equipItem(targetCancelItem);
                     }

                     InventoryUpdate iu = (new InventoryUpdate()).addModifiedItem(targetCancelItem);
                     SystemMessage sm = new SystemMessage(SystemMsg.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1);
                     sm.addItemName(targetCancelItem.getItemId());
                     player.sendPacket(new IStaticPacket[]{new ExVariationCancelResult(1), iu, sm});
                     Iterator var10 = player.getAllShortCuts().iterator();

                     while(var10.hasNext()) {
                        ShortCut sc = (ShortCut)var10.next();
                        if (sc.getId() == targetCancelItem.getObjectId() && sc.getType() == 1) {
                           player.sendPacket(new ShortCutRegister(player, sc));
                        }
                     }

                     player.sendChanges();
                     targetCancelItem.save();
                  }
               }
            }
         } else {
            player.sendPacket(new IStaticPacket[]{SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC});
         }
      }
   }
}
