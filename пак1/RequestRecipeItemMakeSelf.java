package l2.gameserver.network.l2.c2s;

import java.util.Iterator;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.RecipeItemMakeInfo;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.tuple.Pair;

public class RequestRecipeItemMakeSelf extends L2GameClientPacket {
   private int _recipeId;

   protected void readImpl() {
      this._recipeId = this.readD();
   }

   protected void runImpl() {
      Player activeChar = ((GameClient)this.getClient()).getActiveChar();
      if (activeChar != null) {
         if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
         } else if (activeChar.isInStoreMode()) {
            activeChar.sendActionFailed();
         } else if (activeChar.isProcessingRequest()) {
            activeChar.sendActionFailed();
         } else if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
         } else if (activeChar.isInDuel()) {
            activeChar.sendActionFailed();
         } else {
            Recipe recipe = RecipeHolder.getInstance().getRecipeById(this._recipeId);
            if (recipe != null && !recipe.getMaterials().isEmpty() && !recipe.getProducts().isEmpty()) {
               if (activeChar.getCurrentMp() < (double)recipe.getMpConsume()) {
                  activeChar.sendPacket(new IStaticPacket[]{SystemMsg.NOT_ENOUGH_MP, new RecipeItemMakeInfo(activeChar, recipe, 0)});
               } else if (!activeChar.findRecipe(this._recipeId)) {
                  activeChar.sendPacket(new IStaticPacket[]{SystemMsg.PLEASE_REGISTER_A_RECIPE, ActionFail.STATIC});
               } else {
                  boolean succeed = false;
                  List<Pair<ItemTemplate, Long>> materials = recipe.getMaterials();
                  List<Pair<ItemTemplate, Long>> products = Util.calcProducts(recipe.getProducts());
                  activeChar.getInventory().writeLock();

                  Iterator var6;
                  Pair product;
                  long materialAmount;
                  try {
                     var6 = materials.iterator();

                     while(true) {
                        if (!var6.hasNext()) {
                           int totalWeight = 0;
                           long totalSlotCount = 0L;

                           Pair material;
                           Iterator var20;
                           for(var20 = products.iterator(); var20.hasNext(); totalSlotCount += ((ItemTemplate)material.getKey()).isStackable() ? 1L : (Long)material.getValue()) {
                              material = (Pair)var20.next();
                              totalWeight = (int)((long)totalWeight + (long)((ItemTemplate)material.getKey()).getWeight() * (Long)material.getValue());
                           }

                           if (!activeChar.getInventory().validateWeight((long)totalWeight) || !activeChar.getInventory().validateCapacity(totalSlotCount)) {
                              activeChar.sendPacket(new IStaticPacket[]{SystemMsg.WEIGHT_AND_VOLUME_LIMIT_HAS_BEEN_EXCEEDED_THAT_SKILL_IS_CURRENTLY_UNAVAILABLE, new RecipeItemMakeInfo(activeChar, recipe, 0)});
                              return;
                           }

                           var20 = materials.iterator();

                           while(var20.hasNext()) {
                              material = (Pair)var20.next();
                              ItemTemplate materialItem = (ItemTemplate)material.getKey();
                              long materialAmount = (Long)material.getValue();
                              if (materialAmount > 0L) {
                                 if (Config.ALT_GAME_UNREGISTER_RECIPE && materialItem.getItemType() == EtcItemType.RECIPE) {
                                    activeChar.unregisterRecipe(RecipeHolder.getInstance().getRecipeByItem(materialItem).getId());
                                 } else if (activeChar.getInventory().destroyItemByItemId(materialItem.getItemId(), materialAmount)) {
                                    activeChar.sendPacket(SystemMessage.removeItems(materialItem.getItemId(), materialAmount));
                                 }
                              }
                           }
                           break;
                        }

                        product = (Pair)var6.next();
                        ItemTemplate materialItem = (ItemTemplate)product.getKey();
                        materialAmount = (Long)product.getValue();
                        if (materialAmount > 0L) {
                           if (Config.ALT_GAME_UNREGISTER_RECIPE && materialItem.getItemType() == EtcItemType.RECIPE) {
                              Recipe recipe1 = RecipeHolder.getInstance().getRecipeByItem(materialItem);
                              if (!activeChar.hasRecipe(recipe1)) {
                                 activeChar.sendPacket(new IStaticPacket[]{SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeItemMakeInfo(activeChar, recipe, 0)});
                                 return;
                              }
                           } else {
                              ItemInstance item = activeChar.getInventory().getItemByItemId(materialItem.getItemId());
                              if (item == null || item.getCount() < materialAmount) {
                                 activeChar.sendPacket(new IStaticPacket[]{SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeItemMakeInfo(activeChar, recipe, 0)});
                                 return;
                              }
                           }
                        }
                     }
                  } finally {
                     activeChar.getInventory().writeUnlock();
                  }

                  activeChar.resetWaitSitTime();
                  activeChar.reduceCurrentMp((double)recipe.getMpConsume(), (Creature)null);
                  if (Rnd.chance(recipe.getSuccessRate())) {
                     var6 = products.iterator();

                     while(var6.hasNext()) {
                        product = (Pair)var6.next();
                        int itemId = ((ItemTemplate)product.getKey()).getItemId();
                        materialAmount = (Long)product.getValue();
                        ItemFunctions.addItem(activeChar, itemId, materialAmount, true);
                        activeChar.getListeners().onCraftItem(itemId, materialAmount);
                     }

                     succeed = true;
                  }

                  if (!succeed) {
                     var6 = products.iterator();

                     while(var6.hasNext()) {
                        product = (Pair)var6.next();
                        activeChar.sendPacket((new SystemMessage(SystemMsg.YOU_FAILED_TO_MANUFACTURE_S1)).addItemName(((ItemTemplate)product.getKey()).getItemId()));
                     }
                  }

                  activeChar.sendPacket(new RecipeItemMakeInfo(activeChar, recipe, succeed ? 1 : 0));
               }
            } else {
               activeChar.sendPacket(SystemMsg.THE_RECIPE_IS_INCORRECT);
            }
         }
      }
   }
}
