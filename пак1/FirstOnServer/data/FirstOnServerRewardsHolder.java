package events.FirstOnServer.data;

import events.FirstOnServer.template.FirstOnServerTemplate;
import events.FirstOnServer.type.FirstOnServerType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.utils.Location;

public class FirstOnServerRewardsHolder extends AbstractHolder {
   private static FirstOnServerRewardsHolder instance = new FirstOnServerRewardsHolder();
   private boolean _enable;
   private boolean _enableVoiced;
   private String _voicedCommand;
   private int _managerId;
   private List<Location> _managerSpawn = new ArrayList();
   private Map<FirstOnServerType, List<FirstOnServerTemplate>> _templates = new HashMap();

   public static FirstOnServerRewardsHolder getInstance() {
      return instance;
   }

   public void addTemplate(FirstOnServerTemplate template) {
      List<FirstOnServerTemplate> list = (List)this._templates.get(template.getType());
      if (list == null) {
         ArrayList list;
         this._templates.put(template.getType(), list = new ArrayList());
         list.add(template);
      } else {
         list.add(template);
         this._templates.replace(template.getType(), list);
      }

   }

   public List<FirstOnServerTemplate> getAllRewardsByType(FirstOnServerType type) {
      return (List)this._templates.get(type);
   }

   public Map<FirstOnServerType, List<FirstOnServerTemplate>> getAllRewardsByIndexPage(int index) {
      Map<FirstOnServerType, List<FirstOnServerTemplate>> result = new HashMap();
      Iterator var3 = this._templates.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<FirstOnServerType, List<FirstOnServerTemplate>> entry = (Entry)var3.next();
         List<FirstOnServerTemplate> list = null;
         Iterator var6 = ((List)entry.getValue()).iterator();

         while(var6.hasNext()) {
            FirstOnServerTemplate t = (FirstOnServerTemplate)var6.next();
            if (t.getPageIndex() == index) {
               if (!result.containsKey(entry.getKey())) {
                  result.put(entry.getKey(), list = new ArrayList());
               }

               if (list != null) {
                  list.add(t);
               }
            }
         }
      }

      return result;
   }

   public FirstOnServerTemplate getRewardByType(FirstOnServerType type, int id) {
      List<FirstOnServerTemplate> list = (List)this._templates.get(type);
      if (list == null) {
         return null;
      } else {
         Iterator var4 = list.iterator();

         FirstOnServerTemplate template;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            template = (FirstOnServerTemplate)var4.next();
         } while(template.getId() != id);

         return template;
      }
   }

   public boolean isEnable() {
      return this._enable;
   }

   public void setEnable(boolean enable) {
      this._enable = enable;
   }

   public boolean isEnableVoiced() {
      return this._enableVoiced;
   }

   public void setEnableVoiced(boolean enable) {
      this._enableVoiced = enable;
   }

   public String getVoicedCommand() {
      return this._voicedCommand;
   }

   public void setVoicedCommand(String command) {
      this._voicedCommand = command;
   }

   public int getManagerId() {
      return this._managerId;
   }

   public void setManagerId(int managerId) {
      this._managerId = managerId;
   }

   public List<Location> getManagerSpawn() {
      return this._managerSpawn;
   }

   public void setManagerSpawn(List<Location> managerSpawn) {
      this._managerSpawn = managerSpawn;
   }

   public void log() {
      this.info("load " + this._templates.size() + " first on server data(s).");
   }

   public int size() {
      return this._templates.size();
   }

   public void clear() {
      this._templates.clear();
   }
}
