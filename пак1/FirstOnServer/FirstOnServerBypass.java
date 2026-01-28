package events.FirstOnServer;

import events.FirstOnServer.data.FirstOnServerRewardsHolder;
import events.FirstOnServer.template.FirstOnServerRecord;
import events.FirstOnServer.template.FirstOnServerRewardTemplate;
import events.FirstOnServer.template.FirstOnServerTemplate;
import events.FirstOnServer.type.FirstOnServerType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

public class FirstOnServerBypass extends Functions implements ScriptFile {
   private static final int SHOW_ITEM_PAGE = 10;
   private static final int SHOW_PAGINATOR_IN_LINE = 15;

   public void sendPage(String[] args) {
      Player player = this.getSelf();
      NpcInstance npc = this.getNpc();
      int index = Integer.parseInt(args[0]);
      int page = 1;
      if (args.length == 2) {
         page = Integer.parseInt(args[1]);
      }

      String filePath = getHtmRoot(index);
      sendPage(player, index, page, npc, filePath);
   }

   public static void sendPage(Player player, int index, int page, NpcInstance npc, String filePath) {
      NpcHtmlMessage packet = new NpcHtmlMessage(player, npc);
      packet.setFile(filePath);
      String taskTab = HtmCache.getInstance().getNotNull("mods/FirstOnServer/taskTab.htm", player);
      String taskSuccessTab = HtmCache.getInstance().getNotNull("mods/FirstOnServer/taskSuccessTab.htm", player);
      Iterator var8 = FirstOnServerRewardsHolder.getInstance().getAllRewardsByIndexPage(index).entrySet().iterator();

      while(var8.hasNext()) {
         Entry<FirstOnServerType, List<FirstOnServerTemplate>> entry = (Entry)var8.next();
         FirstOnServerType type = (FirstOnServerType)entry.getKey();
         List<FirstOnServerTemplate> templateList = (List)entry.getValue();
         int pageType = page;
         if (templateList.size() <= 10) {
            pageType = 1;
         }

         StringBuilder sbTab = getShowTab(player, taskTab, taskSuccessTab, pageType, templateList);
         packet.replace("%" + type.name().toLowerCase() + "_list%", sbTab.toString());
         StringBuilder sbPages = getShowPages(player, index, pageType, templateList);
         packet.replace("%" + type.name().toLowerCase() + "_pages%", sbPages.toString());
      }

      player.sendPacket(packet);
   }

   private static StringBuilder getShowTab(Player player, String taskTab, String taskSuccessTab, int page, List<FirstOnServerTemplate> templateList) {
      StringBuilder sb = new StringBuilder();

      for(int i = page * 10 - 10; i < page * 10 && i < templateList.size(); ++i) {
         FirstOnServerTemplate template = (FirstOnServerTemplate)templateList.get(i);
         if (template != null) {
            Map<Integer, FirstOnServerRecord> winnersByType = FirstOnServerEvent.getInstance().getWinnersByType(template.getType());
            if (winnersByType != null) {
               String temp;
               if (winnersByType.containsKey(template.getId())) {
                  temp = taskSuccessTab.replace("%task_name%", (new CustomMessage(template.getName(), player, new Object[0])).toString());
                  temp = temp.replace("%task_winner%", ((FirstOnServerRecord)winnersByType.get(template.getId())).getWinnerName());
                  sb.append(temp);
               } else {
                  temp = taskTab.replace("%task_name%", (new CustomMessage(template.getName(), player, new Object[0])).toString());
                  FirstOnServerRewardTemplate rewardTemplate = (FirstOnServerRewardTemplate)template.getRewardList().get(0);
                  if (rewardTemplate != null) {
                     if (template.getRewardList().size() > 1) {
                        temp = temp.replace("%reward%", rewardTemplate.getRewardString(player) + "...");
                     } else {
                        temp = temp.replace("%reward%", rewardTemplate.getRewardString(player));
                     }
                  } else {
                     temp = temp.replace("%reward%", "");
                  }

                  sb.append(temp);
               }
            }
         }
      }

      return sb;
   }

   private static StringBuilder getShowPages(Player player, int index, int page, List<FirstOnServerTemplate> templates) {
      StringBuilder pg = new StringBuilder();
      double augmentCounts = (double)templates.size();
      double maxItemPerPage = 10.0D;
      if (augmentCounts > maxItemPerPage) {
         double maxPages = Math.ceil(augmentCounts / maxItemPerPage);
         pg.append("<center><table width=25 border=0><tr>");
         int ButtonInLine = 1;

         for(int current = 1; (double)current <= maxPages; ++current) {
            if (page == current) {
               pg.append("<td width=25 align=center><button value=\"[").append(current).append("]\" width=15 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            } else {
               pg.append("<td width=25 align=center><button value=\"").append(current).append("\" action=\"bypass -h scripts_events.FirstOnServer.FirstOnServerBypass:sendPage ").append(index).append(" ").append(current).append("\" width=15 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
            }

            if (ButtonInLine == 15) {
               pg.append("</tr><tr>");
               ButtonInLine = 0;
            }

            ++ButtonInLine;
         }

         pg.append("</tr></table></center>");
      }

      return pg;
   }

   public static String getHtmRoot(int val) {
      String pom;
      if (val == 0) {
         pom = "";
      } else {
         pom = "-" + val;
      }

      return "mods/FirstOnServer/index" + pom + ".htm";
   }

   public void onLoad() {
   }

   public void onReload() {
   }

   public void onShutdown() {
   }
}
