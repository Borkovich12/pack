package services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;

public class ZoneEquipHolder extends AbstractHolder {
   private static ZoneEquipHolder instance = new ZoneEquipHolder();
   private static Map<String, Map<Integer, Integer>> _zoneItems = new HashMap();

   public static ZoneEquipHolder getInstance() {
      return instance;
   }

   public void addZoneItem(String zoneName, Map<Integer, Integer> slots) {
      _zoneItems.put(zoneName, slots);
   }

   public Map<Integer, Integer> getDisplayItem(String zoneName) {
      return (Map)_zoneItems.get(zoneName);
   }

   public Map<Integer, Integer> getDisplayItem(Player player) {
      Iterator var2 = player.getZones().iterator();

      Map result;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         Zone zone = (Zone)var2.next();
         result = (Map)_zoneItems.get(zone.getName());
      } while(result == null);

      return result;
   }

   public boolean hasZoneDisplayItem(String itemId) {
      return _zoneItems.containsKey(itemId);
   }

   public void log() {
      this.info("load " + _zoneItems.size() + " zone display item(s).");
   }

   public int size() {
      return _zoneItems.size();
   }

   public void clear() {
      _zoneItems.clear();
   }
}
