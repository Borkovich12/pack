package services;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.templates.item.ItemTemplate;
import org.dom4j.Element;

public class ZoneEquipParser extends AbstractFileParser<ZoneEquipHolder> {
   private static final ZoneEquipParser _instance = new ZoneEquipParser();

   public static ZoneEquipParser getInstance() {
      return _instance;
   }

   private ZoneEquipParser() {
      super(ZoneEquipHolder.getInstance());
   }

   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/zone_equip.xml");
   }

   public String getDTDFileName() {
      return "zone_equip.dtd";
   }

   protected void readData(Element rootElement) throws Exception {
      boolean enabled = Boolean.parseBoolean(rootElement.attributeValue("enabled", "false"));
      if (enabled) {
         Iterator iterator = rootElement.elementIterator();

         while(true) {
            Element element;
            do {
               if (!iterator.hasNext()) {
                  return;
               }

               element = (Element)iterator.next();
            } while(!"zone".equals(element.getName()));

            String zoneName = element.attributeValue("name");
            Map<Integer, Integer> slots = new HashMap();
            Iterator displayIterator = element.elementIterator();

            while(displayIterator.hasNext()) {
               Element displayElement = (Element)displayIterator.next();
               if ("display".equalsIgnoreCase(displayElement.getName())) {
                  int itemDisplayId = Integer.parseInt(displayElement.attributeValue("itemId"));
                  ItemTemplate template = ItemHolder.getInstance().getTemplate(itemDisplayId);
                  if (template == null) {
                     return;
                  }

                  int paperdoll = Inventory.getPaperdollIndex(template.getBodyPart());
                  slots.put(paperdoll, template.getItemId());
               }
            }

            ((ZoneEquipHolder)this.getHolder()).addZoneItem(zoneName, slots);
         }
      }
   }
}
