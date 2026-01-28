package services.community.custom.roulette;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.community.custom.roulette.dao.RouletteDAO;
import services.community.custom.roulette.data.RouletteRewardHolder;
import services.community.custom.roulette.model.ItemPosition;
import services.community.custom.roulette.model.ItemPositionsHistory;
import services.community.custom.roulette.model.RouletteItem;
import services.community.custom.roulette.model.RouletteItemKey;
import services.community.custom.roulette.model.RouletteResult;
import services.community.custom.roulette.model.RouletteStat;

public class RouletteManager {
   private static final Logger _log = LoggerFactory.getLogger(RouletteManager.class);
   private final AtomicInteger gameCount = new AtomicInteger(0);
   private final Map<RouletteItemKey, RouletteStat> rouletteStats = new HashMap();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private final Lock readLock;
   private final Lock writeLock;
   private static RouletteManager _instance;

   public RouletteManager() {
      this.readLock = this.lock.readLock();
      this.writeLock = this.lock.writeLock();
   }

   public static RouletteManager getInstance() {
      if (_instance == null) {
         _instance = new RouletteManager();
      }

      return _instance;
   }

   public RouletteResult playRoulette(int spinCount) {
      RouletteResult result = new RouletteResult();
      RouletteRewardHolder holder = RouletteRewardHolder.getInstance();
      List<RouletteItem> rouletteItemsCopy = new ArrayList(holder.getGroupRewardsBySpin(spinCount));
      Collections.shuffle(rouletteItemsCopy);
      LocalTime currentTime = LocalTime.now();
      double totalChance = 0.0D;
      Iterator var8 = rouletteItemsCopy.iterator();

      while(true) {
         while(true) {
            RouletteItem item;
            String endTimeStr;
            LocalTime endTime;
            int rouletteLimitCount;
            do {
               while(true) {
                  do {
                     if (!var8.hasNext()) {
                        double random = Math.random();
                        double accumulatedChance = 0.0D;
                        Iterator var28 = rouletteItemsCopy.iterator();

                        while(var28.hasNext()) {
                           RouletteItem item = (RouletteItem)var28.next();
                           if (!item.isDisabled()) {
                              LocalTime startTime;
                              String startTimeStr;
                              String endTimeStr;
                              if (item.getDailyCount() > 0) {
                                 startTimeStr = item.getStartTime();
                                 endTimeStr = item.getEndTime();
                                 if (startTimeStr != null && endTimeStr != null) {
                                    int rouletteLimitCount = RouletteVariables.getInstance().getVarInt("roulette_limited_item_" + item.getItemId(), 0);
                                    if (rouletteLimitCount < item.getDailyCount()) {
                                       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                       startTime = LocalTime.parse(startTimeStr, formatter);
                                       LocalTime endTime = LocalTime.parse(endTimeStr, formatter);
                                       if (!currentTime.isBefore(startTime) && !currentTime.isAfter(endTime)) {
                                          long nextSpinTime = this.calculateNextSpinTime(startTimeStr);
                                          long rouletteLimitedExpire = RouletteVariables.getInstance().getVarExpireTime("roulette_limited_item_" + item.getItemId());
                                          accumulatedChance += item.getChance() / totalChance;
                                          if (random < accumulatedChance) {
                                             result.setWinItem(item);
                                             result.setPosition(holder.getIndexOfReward(item));
                                             RouletteVariables.getInstance().setVar("roulette_limited_item_" + item.getItemId(), String.valueOf(rouletteLimitCount + 1), rouletteLimitedExpire == 0L ? nextSpinTime : rouletteLimitedExpire);
                                             break;
                                          }
                                       }
                                    }
                                 } else {
                                    long nextTimeDrop = this.calculateNextFreeTime();
                                    long rouletteLimitedExpire = RouletteVariables.getInstance().getVarExpireTime("roulette_limited_item_" + item.getItemId());
                                    int rouletteLimitCount = RouletteVariables.getInstance().getVarInt("roulette_limited_item_" + item.getItemId(), 0);
                                    if (rouletteLimitedExpire <= 0L || rouletteLimitCount < item.getDailyCount()) {
                                       accumulatedChance += item.getChance() / totalChance;
                                       if (random < accumulatedChance) {
                                          result.setWinItem(item);
                                          result.setPosition(holder.getIndexOfReward(item));
                                          RouletteVariables.getInstance().setVar("roulette_limited_item_" + item.getItemId(), String.valueOf(rouletteLimitCount + 1), rouletteLimitedExpire == 0L ? nextTimeDrop : rouletteLimitedExpire);
                                          break;
                                       }
                                    }
                                 }
                              } else {
                                 startTimeStr = item.getStartTime();
                                 endTimeStr = item.getEndTime();
                                 if (startTimeStr != null && endTimeStr != null) {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                    LocalTime startLimitedTime = LocalTime.parse(startTimeStr, formatter);
                                    startTime = LocalTime.parse(endTimeStr, formatter);
                                    if (currentTime.isBefore(startLimitedTime) || currentTime.isAfter(startTime)) {
                                       continue;
                                    }
                                 }

                                 accumulatedChance += item.getChance() / totalChance;
                                 if (random < accumulatedChance) {
                                    result.setWinItem(item);
                                    result.setPosition(holder.getIndexOfReward(item));
                                    break;
                                 }
                              }
                           }
                        }

                        return result;
                     }

                     item = (RouletteItem)var8.next();
                  } while(item.isDisabled());

                  if (item.getDailyCount() > 0) {
                     rouletteLimitCount = RouletteVariables.getInstance().getVarInt("roulette_limited_item_" + item.getItemId(), 0);
                     break;
                  }

                  String startTimeStr = item.getStartTime();
                  endTimeStr = item.getEndTime();
                  if (startTimeStr != null && endTimeStr != null) {
                     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                     LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
                     endTime = LocalTime.parse(endTimeStr, formatter);
                     if (!currentTime.isBefore(startTime) && !currentTime.isAfter(endTime)) {
                        totalChance += item.getChance();
                     }
                  } else {
                     totalChance += item.getChance();
                  }
               }
            } while(rouletteLimitCount >= item.getDailyCount());

            endTimeStr = item.getStartTime();
            String endTimeStr = item.getEndTime();
            if (endTimeStr != null && endTimeStr != null) {
               DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
               endTime = LocalTime.parse(endTimeStr, formatter);
               LocalTime endTime = LocalTime.parse(endTimeStr, formatter);
               if (!currentTime.isBefore(endTime) && !currentTime.isAfter(endTime)) {
                  totalChance += item.getChance();
               }
            } else {
               totalChance += item.getChance();
            }
         }
      }
   }

   public long calculateNextFreeTime() {
      long currentTimeUnix = System.currentTimeMillis() / 1000L;
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(currentTimeUnix * 1000L);
      int currentHour = calendar.get(11);
      int currentMinute = calendar.get(12);
      if (currentHour > 6 || currentHour == 6 && currentMinute >= 30) {
         calendar.add(6, 1);
      }

      calendar.set(11, 6);
      calendar.set(12, 30);
      calendar.set(13, 0);
      return calendar.getTimeInMillis() / 1000L;
   }

   public long calculateNextSpinTime(String startLimitedTimeStr) {
      long currentTimeUnix = System.currentTimeMillis() / 1000L;
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(currentTimeUnix * 1000L);
      int currentHour = calendar.get(11);
      int currentMinute = calendar.get(12);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
      LocalTime startLimitedTime = LocalTime.parse(startLimitedTimeStr, formatter);
      int limitedHour = startLimitedTime.getHour();
      int limitedMinute = startLimitedTime.getMinute();
      if (currentHour > limitedHour || currentHour == limitedHour && currentMinute >= limitedMinute) {
         calendar.add(6, 1);
      }

      calendar.set(11, limitedHour);
      calendar.set(12, limitedMinute);
      calendar.set(13, 0);
      return calendar.getTimeInMillis() / 1000L;
   }

   public int getCountScrollPosition(Player player, RouletteResult rouletteResult) {
      int rewardCount = RouletteRewardHolder.getInstance().getRewardCount();
      int currentPosition = player.getRoulette().getCurrentPosition();
      int positionOfWinItem = rouletteResult.getPosition();
      int scrollCountToWinItem = (positionOfWinItem + rewardCount - currentPosition) % rewardCount;
      int finalScrollNumber = scrollCountToWinItem < 3 ? scrollCountToWinItem + rewardCount : scrollCountToWinItem;
      return finalScrollNumber;
   }

   public ItemPositionsHistory getItemPositionsHistory(Player player) {
      int rewardCount = RouletteRewardHolder.getInstance().getRewardCount();
      List<ItemPosition> positions = new ArrayList();
      int currentPosition = player.getRoulette().getCurrentPosition();
      int indentFromCurrentPosition = 2;
      if (ConfigRoulette.CUSTOM_ROULETTE_VISIBLE_ITEMS_COUNT == 3) {
         indentFromCurrentPosition = 1;
      } else if (ConfigRoulette.CUSTOM_ROULETTE_VISIBLE_ITEMS_COUNT == 5) {
         indentFromCurrentPosition = 2;
      } else if (ConfigRoulette.CUSTOM_ROULETTE_VISIBLE_ITEMS_COUNT == 7) {
         indentFromCurrentPosition = 3;
      }

      for(int i = currentPosition - indentFromCurrentPosition; i <= currentPosition + indentFromCurrentPosition; ++i) {
         int position = i < 0 ? rewardCount + i : i;
         if (position >= rewardCount) {
            position %= rewardCount;
         }

         positions.add(new ItemPosition(RouletteRewardHolder.getInstance().getRewardByIndex(position).getItemId(), position));
      }

      return new ItemPositionsHistory(positions);
   }

   public Map<RouletteItemKey, RouletteStat> getRouletteStats() {
      this.readLock.lock();

      Map var1;
      try {
         var1 = this.rouletteStats;
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public RouletteStat getRouletteStatById(RouletteItemKey key) {
      this.readLock.lock();

      RouletteStat var2;
      try {
         var2 = (RouletteStat)this.rouletteStats.getOrDefault(key, (Object)null);
      } finally {
         this.readLock.unlock();
      }

      return var2;
   }

   public void increaseRouletteStat(int itemId, int itemEnchant, String itemName, int value) {
      this.writeLock.lock();

      try {
         RouletteItemKey key = new RouletteItemKey(itemId, itemEnchant);
         RouletteStat rouletteStat = (RouletteStat)this.rouletteStats.get(key);
         if (rouletteStat == null) {
            rouletteStat = new RouletteStat(itemId, itemName, itemEnchant, (long)value, 1);
            this.rouletteStats.put(key, rouletteStat);
         } else {
            int haveCount = rouletteStat.getCount();
            long haveValue = rouletteStat.getValue();
            rouletteStat = new RouletteStat(itemId, itemName, itemEnchant, haveValue + (long)value, haveCount + 1);
            this.rouletteStats.replace(key, rouletteStat);
         }

         RouletteDAO.getInstance().updateStats(rouletteStat);
      } finally {
         this.writeLock.unlock();
      }

   }

   public int incrementGame() {
      return this.gameCount.incrementAndGet();
   }

   public int getGameCount() {
      return this.gameCount.get();
   }

   public void increaseGameCount(int value) {
      this.gameCount.addAndGet(value);
   }

   public void restore() {
      RouletteVariables.getInstance();
      if (ConfigRoulette.CUSTOM_ROULETTE_STORE_STATS_IN_DB) {
         List<RouletteStat> restoreStats = RouletteDAO.getInstance().restoreStats();
         Iterator var2 = restoreStats.iterator();

         while(var2.hasNext()) {
            RouletteStat stats = (RouletteStat)var2.next();
            this.rouletteStats.put(new RouletteItemKey(stats.getItemId(), stats.getItemEnchant()), stats);
            this.increaseGameCount(stats.getCount());
         }

         _log.info("Restored " + restoreStats.size() + " roulette item stat(s)");
      }
   }
}
