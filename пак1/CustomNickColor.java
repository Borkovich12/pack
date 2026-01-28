package services;

import l2.gameserver.data.StringHolder;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.StringUtils;

public class CustomNickColor extends Functions {
   public void list() {
      Player player = this.getSelf();
      if (player != null) {
         if (!ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_ENABLED) {
            player.sendPacket((new NpcHtmlMessage(5)).setFile("scripts/services/service_disabled.htm"));
         } else {
            NpcHtmlMessage msg = (new NpcHtmlMessage(5)).setFile("scripts/services/change_custom_nick_color.htm");
            StringBuilder sb = new StringBuilder();

            for(int idx = 0; idx < ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_LIST.length; ++idx) {
               String color = ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_LIST[idx];
               String entryHtml = StringHolder.getInstance().getNotNull(player, "services.CustomNameColor.entryHtml");
               entryHtml = entryHtml.replaceAll("%color%", color);
               entryHtml = entryHtml.replaceAll("%fontColor%", color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2));
               entryHtml = entryHtml.replaceAll("%itemId%", String.valueOf(ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_ITEM[idx]));
               entryHtml = entryHtml.replaceAll("%itemCount%", String.valueOf(ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_PRICE[idx]));
               entryHtml = entryHtml.replaceAll("%playerName%", player.getName());
               sb.append(entryHtml);
            }

            msg.replace("%list%", sb.toString());
            player.sendPacket(msg);
         }
      }
   }

   public void change(String[] param) {
      Player player = this.getSelf();
      if (player != null && CheckPlayerConditions(player)) {
         if (param != null && param.length >= 1) {
            if (!ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_ENABLED) {
               player.sendPacket((new NpcHtmlMessage(5)).setFile("scripts/services/service_disabled.htm"));
            } else if (param[0].equalsIgnoreCase("FFFFFF")) {
               player.setNameColor(Integer.decode("0xFFFFFF"));
               player.broadcastUserInfo(true);
            } else {
               String colorText = StringUtils.trimToEmpty(param[0]);
               if (!colorText.isEmpty()) {
                  for(int idx = 0; idx < ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_LIST.length; ++idx) {
                     if (StringUtils.equalsIgnoreCase(ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_LIST[idx], colorText)) {
                        int requiredItemId = ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_ITEM[idx];
                        long requiredItemAmmount = (long)ConfigCustomServices.SERVICES_CHANGE_NICK_COLOR_PRICE[idx];
                        if (ItemFunctions.getItemCount(player, requiredItemId) < requiredItemAmmount) {
                           if (requiredItemId == 57) {
                              player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                           } else {
                              player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                           }

                           return;
                        }

                        if (ItemFunctions.removeItem(player, requiredItemId, requiredItemAmmount, true) >= requiredItemAmmount) {
                           player.setNameColor(Integer.decode("0x" + param[0]));
                           player.broadcastUserInfo(true);
                           Log.service("CustomNickColor", player, "change nick color on " + Integer.decode("0x" + param[0]) + " for " + requiredItemId + " amount " + requiredItemAmmount);
                        }
                     }
                  }

               }
            }
         }
      }
   }
}
