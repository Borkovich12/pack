package services.community.custom.roulette.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.community.custom.roulette.ConfigRoulette;
import services.community.custom.roulette.RouletteManager;
import services.community.custom.roulette.data.RouletteRewardHolder;

public class RouletteComponent {
   private static final Logger _log = LoggerFactory.getLogger(RouletteComponent.class);
   private int currentRoulettePosition = 0;
   private HardReference<Player> playerRef;
   private ScheduledFuture<?> rouletteTask = null;

   public RouletteComponent(Player player) {
      if (player != null) {
         this.playerRef = player.getRef();
         this.resetCurrentRoulettePosition();
      }
   }

   public Player getPlayer() {
      return (Player)this.playerRef.get();
   }

   public void resetCurrentRoulettePosition() {
      this.currentRoulettePosition = 0;
   }

   public int getCurrentPosition() {
      return this.currentRoulettePosition;
   }

   public void updateCurrentPosition() {
      this.currentRoulettePosition = (this.currentRoulettePosition + 1) % RouletteRewardHolder.getInstance().getRewardCount();
   }

   public void setCurrentPosition(int newCurrentPosition) {
      this.currentRoulettePosition = newCurrentPosition % RouletteRewardHolder.getInstance().getRewardCount();
   }

   public void startRoulette() {
      Player player = this.getPlayer();
      if (this.isCheckCond(player)) {
         if (this.isCheckItem(player)) {
            int gameNumber = RouletteManager.getInstance().incrementGame();
            String output = "#" + gameNumber + " Player " + player.getName() + "[" + player.getObjectId() + "] startRoulette and remove itemName: " + ItemHolder.getInstance().getTemplate(ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID).getName() + " itemId: " + ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID + " count: " + ConfigRoulette.CUSTOM_ROULETTE_ITEM_COUNT;
            Log.service("Roulette", player, output);
            int rouletteSpinCount = player.getVarInt("roulette_spin_count", 0);
            ++rouletteSpinCount;
            RouletteResult rouletteResult = RouletteManager.getInstance().playRoulette(rouletteSpinCount);
            int countScrollPosition = RouletteManager.getInstance().getCountScrollPosition(player, rouletteResult);
            player.setVar("roulette_spin_count", String.valueOf(rouletteSpinCount), -1L);
            player.block();
            this.rouletteTask = ThreadPoolManager.getInstance().schedule(new RouletteComponent.RouletteTask(gameNumber, rouletteResult, countScrollPosition, false), (long)ConfigRoulette.CUSTOM_ROULETTE_DELAY);
         }
      }
   }

   private boolean isCheckItem(Player player) {
      return this.handleSpinWithItem(player);
   }

   private boolean handleSpinWithItem(Player player) {
      if (Functions.getItemCount(player, ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID) < (long)ConfigRoulette.CUSTOM_ROULETTE_ITEM_COUNT) {
         player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
         this.showRouletteBoard();
         return false;
      } else {
         Functions.removeItem(player, ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID, (long)ConfigRoulette.CUSTOM_ROULETTE_ITEM_COUNT);
         return true;
      }
   }

   public void fastRoulette() {
      Player player = this.getPlayer();
      if (this.isCheckCond(player)) {
         if (this.isCheckItem(player)) {
            int gameNumber = RouletteManager.getInstance().incrementGame();
            String output = "#" + gameNumber + " Player " + player.getName() + "[" + player.getObjectId() + "] fastRoulette and remove itemName: " + ItemHolder.getInstance().getTemplate(ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID).getName() + " itemId: " + ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID + " count: " + ConfigRoulette.CUSTOM_ROULETTE_ITEM_COUNT;
            Log.service("Roulette", player, output);
            int rouletteSpinCount = player.getVarInt("roulette_spin_count", 0);
            ++rouletteSpinCount;
            RouletteResult rouletteResult = RouletteManager.getInstance().playRoulette(rouletteSpinCount);
            int countScrollPosition = RouletteManager.getInstance().getCountScrollPosition(player, rouletteResult);
            player.setVar("roulette_spin_count", String.valueOf(rouletteSpinCount), -1L);
            player.block();
            this.rouletteTask = ThreadPoolManager.getInstance().schedule(new RouletteComponent.RouletteTask(gameNumber, rouletteResult, countScrollPosition, true), (long)ConfigRoulette.CUSTOM_ROULETTE_DELAY);
         }
      }
   }

   private void endTask() {
      this.stopRoulette();
      this.getPlayer().unblock();
   }

   private void winnerReward(int gameNumber, RouletteItem rouletteItem) {
      int itemId = rouletteItem.getItemId();
      String itemName = rouletteItem.getItemName();
      long itemCount = rouletteItem.getItemCount();
      int enchant = rouletteItem.getEnchant();
      if (enchant > 0) {
         ItemInstance item = ItemFunctions.createItem(itemId);
         if (item.canBeEnchanted(false)) {
            item.setCount(itemCount);
            item.setEnchantLevel(enchant);
            this.getPlayer().getInventory().addItem(item);
            this.getPlayer().sendPacket(SystemMessage.obtainItems(item.getItemId(), item.getCount(), item.getEnchantLevel()));
         } else {
            Functions.addItem(this.getPlayer(), itemId, itemCount);
         }
      } else {
         Functions.addItem(this.getPlayer(), itemId, itemCount);
      }

      if (ConfigRoulette.CUSTOM_ROULETTE_STORE_STATS_IN_DB) {
         RouletteManager.getInstance().increaseRouletteStat(itemId, enchant, itemName, (int)itemCount);
      }

      if (rouletteItem.isAnnounce()) {
         if (enchant > 0) {
            Announcements.getInstance().announceByCustomMessage("custom_roulette.rewardEnchant", new String[]{this.getPlayer().getName(), String.valueOf(itemCount), String.valueOf(ItemHolder.getInstance().getTemplate(itemId).getName()), String.valueOf(enchant)});
         } else {
            Announcements.getInstance().announceByCustomMessage("custom_roulette.reward", new String[]{this.getPlayer().getName(), String.valueOf(itemCount), String.valueOf(ItemHolder.getInstance().getTemplate(itemId).getName())});
         }
      }

      if (rouletteItem.isShowEffect()) {
         this.getPlayer().broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(this.getPlayer(), this.getPlayer(), 2025, 1, 1000, 0L)});
      }

      String output = "#" + gameNumber + " Player " + this.getPlayer().getName() + "[" + this.getPlayer().getObjectId() + "] has win roulette and give itemName: " + ItemHolder.getInstance().getTemplate(itemId).getName() + " itemId: " + itemId + " count: " + itemCount + (enchant > 0 ? " enchant: " + enchant : "");
      Log.service("Roulette", this.getPlayer(), output);
   }

   public void showRouletteBoard() {
      try {
         Player player = this.getPlayer();
         String htm = HtmCache.getInstance().getNotNull("mods/roulette/index.htm", player);
         String itemsHtm = HtmCache.getInstance().getNotNull("mods/roulette/items_tab.htm", player);
         String examplesHtm = HtmCache.getInstance().getNotNull("mods/roulette/examples_tab.htm", player);
         StringBuilder sb = new StringBuilder();
         StringBuilder examplesSb = new StringBuilder();
         ItemPositionsHistory history = RouletteManager.getInstance().getItemPositionsHistory(player);
         List<ItemPosition> lastItemPositions = history.getPositions();

         int counter;
         RouletteItem rouletteItem;
         int itemId;
         long itemCount;
         int enchant;
         String itemName;
         String itemIcon;
         for(counter = 0; counter < ConfigRoulette.CUSTOM_ROULETTE_VISIBLE_ITEMS_COUNT; ++counter) {
            int position = lastItemPositions == null ? counter : ((ItemPosition)lastItemPositions.get(counter)).getPosition();
            rouletteItem = RouletteRewardHolder.getInstance().getRewardByIndex(position);
            int currentPosition = player.getRoulette().getCurrentPosition();
            boolean thisPosition = position == currentPosition;
            itemId = rouletteItem.getItemId();
            itemCount = rouletteItem.getItemCount();
            enchant = rouletteItem.getEnchant();
            itemName = rouletteItem.getItemName();
            itemIcon = rouletteItem.getItemIcon();
            String itemsTemp = this.getRouletteInfo(itemsHtm, itemId, itemCount, enchant, itemName, itemIcon, thisPosition);
            sb.append(itemsTemp);
         }

         counter = 0;
         List<Integer> haveExampleIds = new ArrayList();
         Iterator var24 = RouletteRewardHolder.getInstance().getRewards().iterator();

         while(var24.hasNext()) {
            rouletteItem = (RouletteItem)var24.next();
            if (!haveExampleIds.contains(rouletteItem.getItemId())) {
               String separator = "";
               if (counter != 0 && counter % ConfigRoulette.CUSTOM_ROULETTE_VISIBLE_EXAMPLES_ITEMS_COUNT == 0) {
                  separator = "</tr><tr>";
               }

               String examplesTemp = separator + examplesHtm;
               itemId = rouletteItem.getItemId();
               itemCount = rouletteItem.getItemCount();
               enchant = rouletteItem.getEnchant();
               itemName = rouletteItem.getItemName();
               itemIcon = rouletteItem.getItemIcon();
               examplesTemp = this.getRouletteExamples(examplesTemp, itemId, itemCount, enchant, itemName, itemIcon, false);
               examplesSb.append(examplesTemp);
               haveExampleIds.add(itemId);
               ++counter;
            }
         }

         htm = htm.replace("%items%", sb.toString());
         htm = htm.replace("%examples_awards%", examplesSb.toString());
         htm = htm.replace("%spin_item_id%", String.valueOf(ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID));
         htm = htm.replace("%spin_item_name%", ItemHolder.getInstance().getTemplate(ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID).getName());
         htm = htm.replace("%spin_item_icon%", ItemHolder.getInstance().getTemplate(ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID).getIcon());
         htm = htm.replace("%spin_item_count%", String.valueOf(ConfigRoulette.CUSTOM_ROULETTE_ITEM_COUNT));
         htm = htm.replace("%have_item_count%", String.valueOf(player.getInventory().getCountOf(ConfigRoulette.CUSTOM_ROULETTE_ITEM_ID)));
         htm = htm.replace("%spin_count%", String.valueOf(player.getVarInt("roulette_spin_count", 0)));
         ShowBoard.separateAndSend(htm, player);
      } catch (NullPointerException var21) {
         Player player = this.getPlayer();
         if (player != null) {
            this.getPlayer().sendActionFailed();
         }
      }

   }

   private String getRouletteInfo(String temp, int itemId, long itemCount, int enchant, String itemName, String itemIcon, boolean thisPosition) {
      if (!itemIcon.isEmpty()) {
         temp = temp.replace("%item_icon%", itemIcon);
      } else {
         temp = temp.replace("%item_icon%", ItemHolder.getInstance().getTemplate(itemId).getIcon());
      }

      temp = temp.replace("%item_enchant%", String.valueOf(enchant));
      temp = temp.replace("%item_count%", String.valueOf(itemCount));
      temp = temp.replace("%item_id%", String.valueOf(itemId));
      if (!itemName.isEmpty()) {
         temp = temp.replace("%item_name%", this.trimString(itemName));
      } else {
         temp = temp.replace("%item_name%", this.trimString(ItemHolder.getInstance().getTemplate(itemId).getName()));
      }

      if (thisPosition) {
         temp = temp.replace("%bgcolor%", "bgcolor=\"000000\"");
      } else {
         temp = temp.replace("%bgcolor%", "");
      }

      return temp;
   }

   private String getRouletteExamples(String temp, int itemId, long itemCount, int enchant, String itemName, String itemIcon, boolean thisPosition) {
      temp = temp.replace("%item_icon%", ItemHolder.getInstance().getTemplate(itemId).getIcon());
      temp = temp.replace("%item_enchant%", String.valueOf(enchant));
      temp = temp.replace("%item_count%", String.valueOf(itemCount));
      temp = temp.replace("%item_id%", String.valueOf(itemId));
      if (!itemName.isEmpty()) {
         temp = temp.replace("%item_name%", this.trimString(itemName));
      } else {
         temp = temp.replace("%item_name%", this.trimString(ItemHolder.getInstance().getTemplate(itemId).getName()));
      }

      if (thisPosition) {
         temp = temp.replace("%bgcolor%", "bgcolor=\"000000\"");
      } else {
         temp = temp.replace("%bgcolor%", "");
      }

      return temp;
   }

   private String trimString(String text) {
      if (text.length() > ConfigRoulette.CUSTOM_ROULETTE_MAX_LENGTH_NAME) {
         text = text.substring(0, ConfigRoulette.CUSTOM_ROULETTE_MAX_LENGTH_NAME);
      }

      return text;
   }

   public void stopRoulette() {
      if (this.rouletteTask != null) {
         this.rouletteTask.cancel(true);
         this.rouletteTask = null;
      }

   }

   private boolean isCheckCond(Player player) {
      if (this.rouletteTask != null) {
         if (player.isLangRus()) {
            player.sendMessage("Рулетка уже запущена.");
         } else {
            player.sendMessage("Roulette is already running.");
         }

         return false;
      } else if (player.isOutOfControl()) {
         player.sendActionFailed();
         return false;
      } else if (player.getPlayerAccess().UseTrade && !player.isTradeBannedByGM()) {
         if ((player.getPvpFlag() > 0 || player.isInCombat()) && !player.isGM()) {
            if (player.isLangRus()) {
               player.sendMessage("Вы не можете торговать в бою или с PvP-флагом.");
            } else {
               player.sendMessage("You can't trade in combat or PvP flag.");
            }

            player.sendActionFailed();
            return false;
         } else if (player.isDead()) {
            player.sendActionFailed();
            return false;
         } else {
            return true;
         }
      } else {
         if (player.isLangRus()) {
            player.sendMessage("Вы не можете использовать торговлю.");
         } else {
            player.sendMessage("You can't use trade.");
         }

         player.sendActionFailed();
         return false;
      }
   }

   private class RouletteTask extends RunnableImpl {
      private int gameNumber;
      private final RouletteResult rouletteResult;
      private int currentScrollCount = 0;
      private int winItemPosition = -1;
      private boolean fast = false;

      public RouletteTask(int gameNumber, RouletteResult rouletteResult, int winItemPosition, boolean fast) {
         this.gameNumber = gameNumber;
         this.rouletteResult = rouletteResult;
         this.winItemPosition = winItemPosition;
         this.fast = fast;
      }

      public void runImpl() throws Exception {
         if (this.fast) {
            RouletteComponent.this.setCurrentPosition(RouletteComponent.this.getCurrentPosition() + this.winItemPosition);
            RouletteComponent.this.showRouletteBoard();
            RouletteItem rouletteItemx = this.rouletteResult.getWinItem();
            if (rouletteItemx != null) {
               RouletteComponent.this.winnerReward(this.gameNumber, rouletteItemx);
            }

            RouletteComponent.this.endTask();
         } else {
            RouletteComponent.this.updateCurrentPosition();
            RouletteComponent.this.showRouletteBoard();
            ++this.currentScrollCount;
            int remainingScrolls = this.winItemPosition - this.currentScrollCount;
            int delay = ConfigRoulette.CUSTOM_ROULETTE_DELAY;
            if (remainingScrolls == 3) {
               delay = (int)((double)delay * 1.25D);
            } else if (remainingScrolls == 2) {
               delay = (int)((double)delay * 1.5D);
            } else if (remainingScrolls == 1) {
               delay *= 2;
            }

            if (this.currentScrollCount == this.winItemPosition) {
               RouletteItem rouletteItem = this.rouletteResult.getWinItem();
               if (rouletteItem != null) {
                  RouletteComponent.this.winnerReward(this.gameNumber, rouletteItem);
               } else {
                  RouletteComponent.this.getPlayer().sendMessage(new CustomMessage("custom_roulette.failedSpin", RouletteComponent.this.getPlayer(), new Object[0]));
               }

               RouletteComponent.this.endTask();
            } else if (this.currentScrollCount > this.winItemPosition) {
               RouletteComponent.this.endTask();
            } else {
               RouletteComponent.this.rouletteTask = ThreadPoolManager.getInstance().schedule(this, (long)delay);
            }
         }

      }
   }
}
