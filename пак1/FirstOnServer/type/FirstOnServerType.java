package events.FirstOnServer.type;

public enum FirstOnServerType {
   LEVEL,
   KILL_MOBS,
   PVP,
   PK,
   QUEST,
   CRAFT,
   ENCHANT_ITEM,
   ENCHANT_SKILL,
   HERO_STATUS,
   LS,
   KILL_RAID_BOSS,
   DONATE,
   CLASS,
   VOTE;

   public static FirstOnServerType getTypeById(int id) {
      FirstOnServerType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         FirstOnServerType type = var1[var3];
         if (type.ordinal() == id) {
            return type;
         }
      }

      return null;
   }
}
