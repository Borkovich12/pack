package events.PiratesTreasure.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.utils.Location;
import org.dom4j.Element;

public final class PirateSearchParser extends AbstractFileParser<PirateSearchHolder> {
   private static final PirateSearchParser _instance = new PirateSearchParser();

   public static PirateSearchParser getInstance() {
      return _instance;
   }

   private PirateSearchParser() {
      super(PirateSearchHolder.getInstance());
   }

   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/pirate_search.xml");
   }

   public String getDTDFileName() {
      return "pirate_search.dtd";
   }

   protected void readData(Element rootElement) throws Exception {
      Iterator iterator = rootElement.elementIterator();

      while(iterator.hasNext()) {
         Element element = (Element)iterator.next();
         String address = element.attributeValue("address");
         List<Location> points = new ArrayList();
         Iterator pointiterator = element.elementIterator();

         while(pointiterator.hasNext()) {
            Element pointElement = (Element)pointiterator.next();
            String value = pointElement.attributeValue("value");
            points.add(Location.parseLoc(value));
         }

         ((PirateSearchHolder)this.getHolder()).add(address, points);
      }

   }
}
