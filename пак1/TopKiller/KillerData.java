package events.TopKiller;

public class KillerData {
   private final int objectId;
   private int dinoPoint;
   private int pvpPoint;
   private int pkPoint;

   public KillerData(int objectId) {
      this.objectId = objectId;
      this.dinoPoint = 0;
      this.pvpPoint = 0;
      this.pkPoint = 0;
   }

   public KillerData(int objectId, int dinoPoint, int pvpPoint, int pkPoint) {
      this.objectId = objectId;
      this.dinoPoint = dinoPoint;
      this.pvpPoint = pvpPoint;
      this.pkPoint = pkPoint;
   }

   public int getObjectId() {
      return this.objectId;
   }

   public int getDinoPoint() {
      return this.dinoPoint;
   }

   public void setDinoPoint(int dinoPoint) {
      this.dinoPoint = dinoPoint;
   }

   public int getPvpPoint() {
      return this.pvpPoint;
   }

   public void setPvpPoint(int pvpPoint) {
      this.pvpPoint = pvpPoint;
   }

   public int getPkPoint() {
      return this.pkPoint;
   }

   public void setPkPoint(int pkPoint) {
      this.pkPoint = pkPoint;
   }

   public boolean isNew() {
      return this.getPvpPoint() == 0 && this.getDinoPoint() == 0 && this.getPkPoint() == 0;
   }
}
