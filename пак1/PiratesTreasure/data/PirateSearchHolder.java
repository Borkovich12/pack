package events.PiratesTreasure.data;

import java.util.ArrayList;
import java.util.List;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Pair;

public final class PirateSearchHolder extends AbstractHolder {
   private static final PirateSearchHolder _instance = new PirateSearchHolder();
   private final List<Pair<String, List<Location>>> _searchList = new ArrayList();

   public static PirateSearchHolder getInstance() {
      return _instance;
   }

   public void add(String address, List<Location> points) {
      this._searchList.add(Pair.of(address, points));
   }

   public List<Pair<String, List<Location>>> getSearchList() {
      return this._searchList;
   }

   public int size() {
      return this._searchList.size();
   }

   public void clear() {
      this._searchList.clear();
   }
}
