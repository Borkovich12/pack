package actions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.model.reward.RewardGroup;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.stats.Stats;
import l2.gameserver.utils.HtmlUtils;
import org.apache.commons.lang3.StringUtils;

public class RewardListInfo extends Functions {
   private static final RewardType[] ITEMS_REWARD_ORDER;
   private static final NumberFormat pf;
   private static final NumberFormat df;

   private static boolean canBypassCheck(Player player, NpcInstance npc) {
      if (npc == null) {
         player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
         player.sendActionFailed();
         return false;
      } else if (!Config.ALLOW_TALK_WHILE_SITTING && player.isSitting()) {
         player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
         player.sendActionFailed();
         return false;
      } else if (!npc.isInRange(player, 2500L)) {
         player.sendPacket(new IStaticPacket[]{SystemMsg.POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT, ActionFail.STATIC});
         player.sendActionFailed();
         return false;
      } else {
         return true;
      }
   }

   public static void showRewardHtml(Player player, NpcInstance npc) {
      showRewardHtml(player, npc, 0);
   }

   public static void showRewardHtml(Player player, NpcInstance npc, int pageNum) {
      if (canBypassCheck(player, npc)) {
         int diff = npc.calculateLevelDiffForDrop(player.isInParty() ? player.getParty().getLevel() : player.getLevel());
         double mod = npc.calcStat(Stats.ITEM_REWARD_MULTIPLIER, 1.0D, player, (Skill)null);
         mod *= Experience.penaltyModifier((long)diff, 9.0D);
         NpcHtmlMessage htmlMessage = new NpcHtmlMessage(player, npc);
         htmlMessage.replace("%npc_name%", HtmlUtils.htmlNpcName(npc.getNpcId()));
         if (mod <= 0.0D && !player.isGM()) {
            htmlMessage.setFile("actions/rewardlist_to_weak.htm");
            player.sendPacket(htmlMessage);
         } else if (npc instanceof ChestInstance && !player.isGM() && !Config.ALT_GAME_SHOW_DROPLIST_TREASURE_CHEST) {
            htmlMessage.setFile("actions/rewardlist_no_for_chest.htm");
            player.sendPacket(htmlMessage);
            player.sendActionFailed();
         } else if (npc.getTemplate().getRewards().isEmpty()) {
            htmlMessage.setFile("actions/rewardlist_empty.htm");
            player.sendPacket(htmlMessage);
         } else {
            htmlMessage.setFile("actions/rewardlist_info.htm");
            Map<RewardType, RewardList> rewards = npc.getTemplate().getRewards();
            List<String> tmp = new ArrayList();
            RewardType[] var9 = ITEMS_REWARD_ORDER;
            int pages = var9.length;

            int firstIdx;
            for(firstIdx = 0; firstIdx < pages; ++firstIdx) {
               RewardType rewardType = var9[firstIdx];
               RewardList rewardList = (RewardList)rewards.get(rewardType);
               if (rewardList != null && !rewardList.isEmpty()) {
                  switch(rewardType) {
                  case RATED_GROUPED:
                     tmp.add(StringHolder.getInstance().getNotNull(player, "drop.rated_grouped"));
                     ratedGroupedRewardList(tmp, npc, rewardList, player, mod);
                     break;
                  case NOT_RATED_GROUPED:
                     tmp.add(StringHolder.getInstance().getNotNull(player, "drop.not_rated_grouped"));
                     notRatedGroupedRewardList(tmp, rewardList, mod, player);
                     break;
                  case NOT_RATED_NOT_GROUPED:
                     tmp.add(StringHolder.getInstance().getNotNull(player, "drop.not_rated_not_grouped"));
                     notGroupedRewardList(tmp, rewardList, 1.0D, mod, player);
                     break;
                  case SWEEP:
                     tmp.add(StringHolder.getInstance().getNotNull(player, "drop.sweep"));
                     notGroupedRewardList(tmp, rewardList, Config.RATE_DROP_SPOIL * player.getRateSpoil(), mod, player);
                  }
               }
            }

            StringBuilder builder = new StringBuilder();
            pages = tmp.size() / Config.ALT_NPC_SHIFTCLICK_ITEM_COUNT;
            pageNum = Math.min(pageNum, pages);
            firstIdx = pageNum * Config.ALT_NPC_SHIFTCLICK_ITEM_COUNT;
            int lastIdx = Math.max(0, Math.min((pageNum + 1) * Config.ALT_NPC_SHIFTCLICK_ITEM_COUNT - 1, tmp.size() - 1));

            int p;
            for(p = firstIdx; p <= lastIdx; ++p) {
               builder.append((String)tmp.get(p));
            }

            htmlMessage.replace("%info%", builder.toString());
            builder.setLength(0);
            builder.append("<table><tr>");

            for(p = 0; p <= pages; ++p) {
               builder.append("<td>");
               if (p == pageNum) {
                  builder.append(p + 1);
               } else {
                  builder.append("<a action=\"bypass -h scripts_actions.RewardListInfo:showReward ").append(p).append("\">").append(p + 1).append("</a>");
               }

               builder.append("</td>");
            }

            builder.append("</tr></table>");
            htmlMessage.replace("%paging%", builder.toString());
            player.sendPacket(htmlMessage);
         }
      }
   }

   public static void ratedGroupedRewardList(List<String> tmp, NpcInstance npc, RewardList list, Player player, double mod) {
      Iterator var6 = list.iterator();

      while(true) {
         RewardGroup g;
         List items;
         double grate;
         while(true) {
            if (!var6.hasNext()) {
               return;
            }

            g = (RewardGroup)var6.next();
            items = g.getItems();
            double gchance = player.hasBonus() ? g.getPremiumChance() : g.getChance();
            double gmod = mod;
            double rateDrop = npc instanceof RaidBossInstance ? Config.RATE_DROP_RAIDBOSS * (double)player.getBonus().getDropRaidItems() : (npc.isSiegeGuard() ? Config.RATE_DROP_SIEGE_GUARD : Config.RATE_DROP_ITEMS * player.getRateItems());
            double rateAdena = Config.RATE_DROP_ADENA * player.getRateAdena();
            double rateSealStone = Config.RATE_DROP_SEAL_STONES * player.getRateSealStones();
            if (g.isAdena()) {
               if (rateAdena != 0.0D) {
                  if (mod > 10.0D) {
                     gmod = mod * (gchance / 1000000.0D);
                  }

                  grate = rateAdena * gmod;
                  break;
               }
            } else if (g.isSealStone()) {
               if (rateSealStone != 0.0D) {
                  if (g.notRate()) {
                     grate = Math.min(mod, 1.0D);
                  } else {
                     grate = rateSealStone * mod;
                  }
                  break;
               }
            } else if (rateDrop != 0.0D) {
               if (g.notRate()) {
                  grate = Math.min(mod, 1.0D);
               } else {
                  grate = rateDrop * mod;
               }
               break;
            }
         }

         double gmult = grate;
         tmp.add(formatRewardGroupHtml(g, player));
         Iterator var23 = items.iterator();

         while(var23.hasNext()) {
            RewardData d = (RewardData)var23.next();
            tmp.add(formatRewardDataHtml(d, gmult, player));
         }
      }
   }

   public static void notRatedGroupedRewardList(List<String> tmp, RewardList list, double mod, Player player) {
      Iterator var5 = list.iterator();

      while(var5.hasNext()) {
         RewardGroup g = (RewardGroup)var5.next();
         List<RewardData> items = g.getItems();
         tmp.add(formatRewardGroupHtml(g, player));
         Iterator var8 = items.iterator();

         while(var8.hasNext()) {
            RewardData d = (RewardData)var8.next();
            tmp.add(formatRewardDataHtml(d, 1.0D, player));
         }
      }

   }

   public static void notGroupedRewardList(List<String> tmp, RewardList list, double rate, double mod, Player player) {
      Iterator var7 = list.iterator();

      while(true) {
         RewardGroup g;
         List items;
         do {
            if (!var7.hasNext()) {
               return;
            }

            g = (RewardGroup)var7.next();
            items = g.getItems();
         } while(rate == 0.0D);

         double grate;
         if (g.notRate()) {
            grate = Math.min(mod, 1.0D);
         } else {
            grate = rate * mod;
         }

         double gmult = Math.ceil(grate);
         Iterator var16 = items.iterator();

         while(var16.hasNext()) {
            RewardData d = (RewardData)var16.next();
            tmp.add(formatRewardDataHtml(d, gmult, player));
         }
      }
   }

   private static String formatRewardGroupHtml(RewardGroup g, Player player) {
      double chance = player.hasBonus() ? g.getPremiumChance() : g.getChance();
      return StringUtils.replace(StringHolder.getInstance().getNotNull(player, "drop.group.html"), "%group_chance%", pf.format(chance / 1000000.0D));
   }

   private static String formatRewardDataHtml(RewardData d, double gmult, Player player) {
      String icon = d.getItem().getIcon();
      if (icon == null || icon.equals("")) {
         icon = "icon.etc_question_mark_i00";
      }

      double chance = player.hasBonus() ? d.getPremiumChance() : d.getChance();
      return StringUtils.replaceEach(StringHolder.getInstance().getNotNull(player, "drop.rewardData.html"), new String[]{"%icon%", "%item%", "%drop_min%", "%drop_max%", "%chance%"}, new String[]{icon, HtmlUtils.htmlItemName(d.getItemId()), String.valueOf(d.getMinDrop()), String.valueOf(Math.round((double)d.getMaxDrop() * (d.notRate() ? 1.0D : gmult))), pf.format(Math.min(1.0D, chance / 1000000.0D))});
   }

   public void showReward(String[] param) {
      Player player = this.getSelf();
      NpcInstance npc = this.getNpc();
      if (player != null && npc != null) {
         int pageNum = 0;
         if (param.length > 0) {
            try {
               pageNum = Integer.parseInt(param[0]);
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         }

         showRewardHtml(player, npc, pageNum);
      }
   }

   static {
      ITEMS_REWARD_ORDER = new RewardType[]{RewardType.RATED_GROUPED, RewardType.SWEEP, RewardType.NOT_RATED_GROUPED, RewardType.NOT_RATED_NOT_GROUPED};
      pf = NumberFormat.getPercentInstance(Locale.ENGLISH);
      df = NumberFormat.getInstance(Locale.ENGLISH);
      pf.setMaximumFractionDigits(4);
      df.setMinimumFractionDigits(2);
   }
}
