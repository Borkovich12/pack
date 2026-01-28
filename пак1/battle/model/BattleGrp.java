package events.battle.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.model.Player;

public class BattleGrp {
   private String _name;
   private int _id;
   private List<HardReference<Player>> _members = new ArrayList();
   private List<Integer> _players = new ArrayList();
   private boolean _endBattle;

   public BattleGrp(Player player, String name, List<HardReference<Player>> mem) {
      this._id = player.getObjectId();
      this._name = name;
      this._members = mem;
      Iterator var4 = mem.iterator();

      while(var4.hasNext()) {
         HardReference<Player> p = (HardReference)var4.next();
         this._players.add(((Player)p.get()).getObjectId());
      }

      this._endBattle = false;
   }

   public String getName() {
      return this._name;
   }

   public int getId() {
      return this._id;
   }

   public List<HardReference<Player>> getMembers() {
      return this._members;
   }

   public List<Integer> getPlayerIds() {
      return this._players;
   }

   public boolean isEndBattle() {
      return this._endBattle;
   }

   public void setEndBattle(boolean endBattle) {
      this._endBattle = endBattle;
   }
}
