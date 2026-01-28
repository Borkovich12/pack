package l2.gameserver.network.l2.s2c;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.Zones;
import l2.gameserver.model.Zone.ZoneType;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.pledge.Clan;
import services.ConfigChangeZone;

public class Die extends L2GameServerPacket {
   private int _objectId;
   private boolean _fake;
   private boolean _sweepable;
   private Map<RestartType, Boolean> _types;

   public Die(Creature cha) {
      this._types = new HashMap(RestartType.VALUES.length);
      this._objectId = cha.getObjectId();
      this._fake = !cha.isDead();
      if (cha.isMonster()) {
         this._sweepable = ((MonsterInstance)cha).isSweepActive();
      } else if (cha.isPlayer()) {
         Player player = (Player)cha;
         if (!player.isOlyCompetitionStarted() && !player.isResurectProhibited()) {
            this.put(RestartType.FIXED, hasFixedWindow(player));
            this.put(RestartType.TO_VILLAGE, true);
            Clan clan = null;
            if (this.get(RestartType.TO_VILLAGE)) {
               clan = player.getClan();
            }

            if (clan != null) {
               this.put(RestartType.TO_CLANHALL, clan.getHasHideout() > 0);
               this.put(RestartType.TO_CASTLE, clan.getCastle() > 0);
            }

            Iterator var4 = cha.getEvents().iterator();

            while(var4.hasNext()) {
               GlobalEvent e = (GlobalEvent)var4.next();
               e.checkRestartLocs(player, this._types);
            }
         }
      }

   }

   private static boolean hasFixedWindow(Player player) {
      if (Config.ALT_REVIVE_WINDOW_TO_TOWN) {
         return true;
      } else if (player.getPlayerAccess().ResurectFixed) {
         return true;
      } else if (Config.SERVICE_FEATHER_REVIVE_ENABLE && (!Config.SERVICE_DISABLE_FEATHER_ON_SIEGES_AND_EPIC || !player.isOnSiegeField() && !player.isInZone(ZoneType.epic)) && player.getInventory().getCountOf(Config.SERVICE_FEATHER_ITEM_ID) > 0L && !player.getInventory().isLockedItem(Config.SERVICE_FEATHER_ITEM_ID)) {
         return true;
      } else {
         return ConfigChangeZone.FIXED_REVIVE_WINDOW_ENABLE && isInFixedZone(player);
      }
   }

   public static boolean isInFixedZone(Player player) {
      Zones zones = player.getZones();
      if (zones != null) {
         Iterator var2 = zones.iterator();

         while(var2.hasNext()) {
            Zone zone = (Zone)var2.next();
            if (zone != null) {
               boolean isChangeZone = zone.getParams().getBool("IsChangeZone", false);
               String pointStr = zone.getParams().getString("ChangeZonePoints", (String)null);
               if (isChangeZone && pointStr != null && !pointStr.isEmpty()) {
                  String activeChangeZone = ServerVariables.getString("active_change_zone", "");
                  if (zone.getName().equalsIgnoreCase(activeChangeZone)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected final void writeImpl() {
      if (!this._fake) {
         this.writeC(6);
         this.writeD(this._objectId);
         this.writeD(this.get(RestartType.TO_VILLAGE));
         this.writeD(this.get(RestartType.TO_CLANHALL));
         this.writeD(this.get(RestartType.TO_CASTLE));
         this.writeD(this.get(RestartType.TO_FLAG));
         this.writeD(this._sweepable ? 1 : 0);
         this.writeD(this.get(RestartType.FIXED));
      }
   }

   private void put(RestartType t, boolean b) {
      this._types.put(t, b);
   }

   private boolean get(RestartType t) {
      Boolean b = (Boolean)this._types.get(t);
      return b != null && b;
   }
}
