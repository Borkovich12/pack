package handler.items;

import gnu.trove.TIntHashSet;
import java.util.Iterator;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.AgathionInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.tables.SkillTable;
import services.ConfigAgathion;

public class AgathionSummon extends ScriptItemHandler {
   private static int[] _itemIds = new int[0];

   public AgathionSummon() {
      ConfigAgathion.load();
      TIntHashSet set = new TIntHashSet();
      Iterator var2 = ConfigAgathion.AGATHION_DATAS.keySet().iterator();

      while(var2.hasNext()) {
         int itemId = (Integer)var2.next();
         set.add(itemId);
      }

      _itemIds = set.toArray();
   }

   public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
      if (playable != null && playable.isPlayer()) {
         Player player = (Player)playable;
         AgathionInstance agathion = player.getAgathion();
         if (agathion != null) {
            agathion.unSummon();
            player.sendMessage("Unsummon Pet.");
            return true;
         } else {
            player.setAgathionControlItem(item);
            player.getAI().Cast(SkillTable.getInstance().getInfo(ConfigAgathion.AGATHION_SUMMON_SKILL_ID, 1), player, false, true);
            return true;
         }
      } else {
         return false;
      }
   }

   public final int[] getItemIds() {
      return _itemIds;
   }
}
