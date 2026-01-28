package services.community.custom.roulette.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import org.dom4j.Element;
import services.community.custom.roulette.model.RouletteItem;

public class RouletteRewardParser extends AbstractFileParser<RouletteRewardHolder> {
   private static final RouletteRewardParser _instance = new RouletteRewardParser();

   public static RouletteRewardParser getInstance() {
      return _instance;
   }

   private RouletteRewardParser() {
      super(RouletteRewardHolder.getInstance());
   }

   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/roulette_reward.xml");
   }

   public String getDTDFileName() {
      return "roulette_reward.dtd";
   }

   protected void readData(Element rootElement) throws Exception {
      Iterator iterator = rootElement.elementIterator();

      while(true) {
         Element element;
         do {
            if (!iterator.hasNext()) {
               return;
            }

            element = (Element)iterator.next();
         } while(!"group".equals(element.getName()));

         int groupId = Integer.parseInt(element.attributeValue("id"));
         int groupCount = Integer.parseInt(element.attributeValue("count"));
         List<RouletteItem> rouletteItems = new ArrayList();
         Iterator secondIterator = element.elementIterator();

         while(secondIterator.hasNext()) {
            Element secondElement = (Element)secondIterator.next();
            if ("roulette_reward".equals(secondElement.getName())) {
               int itemId = Integer.parseInt(secondElement.attributeValue("itemId"));
               long itemCount = Long.parseLong(secondElement.attributeValue("itemCount"));
               String itemName = secondElement.attributeValue("itemName");
               String itemIcon = secondElement.attributeValue("itemIcon");
               int enchant = secondElement.attributeValue("enchant") != null ? Integer.parseInt(secondElement.attributeValue("enchant")) : 0;
               double chance = Double.parseDouble(secondElement.attributeValue("chance"));
               boolean disabled = secondElement.attributeValue("disabled") != null ? Boolean.parseBoolean(secondElement.attributeValue("disabled")) : false;
               int dailyCount = secondElement.attributeValue("dailyCount") != null ? Integer.parseInt(secondElement.attributeValue("dailyCount")) : 0;
               String startTime = secondElement.attributeValue("startTime") != null ? secondElement.attributeValue("startTime") : null;
               String endTime = secondElement.attributeValue("endTime") != null ? secondElement.attributeValue("endTime") : null;
               boolean announce = secondElement.attributeValue("announce") != null ? Boolean.parseBoolean(secondElement.attributeValue("announce")) : false;
               boolean showEffect = secondElement.attributeValue("showEffect") != null ? Boolean.parseBoolean(secondElement.attributeValue("showEffect")) : false;
               rouletteItems.add(new RouletteItem(groupId, itemId, itemCount, itemName, itemIcon, enchant, chance, disabled, dailyCount, startTime, endTime, announce, showEffect));
            }
         }

         ((RouletteRewardHolder)this.getHolder()).addReward(groupId, groupCount, rouletteItems);
      }
   }
}
