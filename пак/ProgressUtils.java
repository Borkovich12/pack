package services.community.custom.progress;

public class ProgressUtils {
   public static String hourFormat(boolean ru, String n) {
      int i = Integer.parseInt(n.substring(n.length() - 1, n.length()));
      if (ru) {
         if (i == 1) {
            return "час";
         } else {
            return i > 1 && i < 5 ? "часа" : "часов";
         }
      } else {
         return i == 1 ? "hour" : "hours";
      }
   }
}
