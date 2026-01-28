package events.FirstOnServer.data;

import events.FirstOnServer.template.FirstOnServerRewardTemplate;
import events.FirstOnServer.template.FirstOnServerTemplate;
import events.FirstOnServer.type.FirstOnServerRewardType;
import events.FirstOnServer.type.FirstOnServerType;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;
import org.dom4j.Element;

public class FirstOnServerRewardsParser extends AbstractFileParser<FirstOnServerRewardsHolder> {
   private static final FirstOnServerRewardsParser _instance = new FirstOnServerRewardsParser();

   public static FirstOnServerRewardsParser getInstance() {
      return _instance;
   }

   private FirstOnServerRewardsParser() {
      super(FirstOnServerRewardsHolder.getInstance());
   }

   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/first_on_server_rewards.xml");
   }

   public String getDTDFileName() {
      return "first_on_server_rewards.dtd";
   }

   protected void readData(Element rootElement) throws Exception {
      FirstOnServerRewardsHolder holder = (FirstOnServerRewardsHolder)this.getHolder();
      boolean enabled = Boolean.parseBoolean(rootElement.attributeValue("enabled", "false"));
      int managerId = Integer.parseInt(rootElement.attributeValue("manager_id", "0"));
      String[] managerSpawns = rootElement.attributeValue("manager_spawn", "83448,148375,-3425").split(";");
      List<Location> spawnList = new ArrayList();
      String[] var7 = managerSpawns;
      int var8 = managerSpawns.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         String spawn = var7[var9];
         spawnList.add(Location.parseLoc(spawn));
      }

      boolean enableVoiced = Boolean.parseBoolean(rootElement.attributeValue("enable_voiced", "false"));
      String voicedCommand = rootElement.attributeValue("voiced_command");
      holder.setEnable(enabled);
      holder.setManagerId(managerId);
      holder.setManagerSpawn(spawnList);
      holder.setEnableVoiced(enableVoiced);
      holder.setVoicedCommand(voicedCommand);
      Iterator iterator = rootElement.elementIterator();

      label77:
      while(true) {
         Element element;
         do {
            if (!iterator.hasNext()) {
               return;
            }

            element = (Element)iterator.next();
         } while(!"set".equalsIgnoreCase(element.getName()));

         int id = Integer.parseInt(element.attributeValue("id"));
         String name = element.attributeValue("name");
         int pageIndex = Integer.parseInt(element.attributeValue("page_index"));
         FirstOnServerType type = FirstOnServerType.valueOf(element.attributeValue("type"));
         String[] values = element.attributeValue("value").split(";");
         int[] val = new int[values.length];

         for(int i = 0; i < val.length; ++i) {
            val[i] = Integer.parseInt(values[i]);
         }

         String[] addValue = element.attributeValue("add_value") != null ? element.attributeValue("add_value").split(";") : new String[0];
         int[] addVal = new int[addValue.length];

         for(int i = 0; i < addVal.length; ++i) {
            addVal[i] = Integer.parseInt(addValue[i]);
         }

         String playerMessage = element.attributeValue("player_message");
         boolean broadcastAnnounce = Boolean.parseBoolean(element.attributeValue("broadcast_announce"));
         String announceMessage = element.attributeValue("announce_message");
         List<FirstOnServerRewardTemplate> rewardList = new ArrayList();
         Iterator rewardsElementIt = element.elementIterator();

         while(true) {
            Element rewardsElement;
            do {
               if (!rewardsElementIt.hasNext()) {
                  holder.addTemplate(new FirstOnServerTemplate(id, name, pageIndex, type, val, addVal, rewardList, playerMessage, broadcastAnnounce, announceMessage));
                  continue label77;
               }

               rewardsElement = (Element)rewardsElementIt.next();
            } while(!"rewards".equals(rewardsElement.getName()));

            Iterator rewardElementIt = rewardsElement.elementIterator();

            while(rewardElementIt.hasNext()) {
               Element rewardElement = (Element)rewardElementIt.next();
               if ("reward".equalsIgnoreCase(rewardElement.getName())) {
                  FirstOnServerRewardType rewardType = FirstOnServerRewardType.valueOf(rewardElement.attributeValue("reward_type"));
                  FirstOnServerRewardTemplate rewardTemplate = new FirstOnServerRewardTemplate(rewardType);
                  if (rewardTemplate.getRewardType() == FirstOnServerRewardType.ITEM) {
                     rewardTemplate.setItemId(Integer.parseInt(rewardElement.attributeValue("item_id")));
                     rewardTemplate.setItemCount(Integer.parseInt(rewardElement.attributeValue("item_count")));
                  } else if (rewardTemplate.getRewardType() == FirstOnServerRewardType.PREMIUM) {
                     rewardTemplate.setPremiumHours(Integer.parseInt(rewardElement.attributeValue("hours")));
                  } else if (rewardTemplate.getRewardType() == FirstOnServerRewardType.HERO) {
                     rewardTemplate.setHeroHours(Integer.parseInt(rewardElement.attributeValue("hours")));
                  }

                  rewardList.add(rewardTemplate);
               }
            }
         }
      }
   }
}
