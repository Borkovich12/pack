package l2.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Skill;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.model.reward.RewardGroup;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.AbsorbInfo;
import l2.gameserver.templates.npc.Faction;
import l2.gameserver.templates.npc.MinionData;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.templates.npc.AbsorbInfo.AbsorbType;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public final class NpcParser extends AbstractDirParser<NpcHolder> {
   private static final NpcParser _instance = new NpcParser();

   public static NpcParser getInstance() {
      return _instance;
   }

   private NpcParser() {
      super(NpcHolder.getInstance());
   }

   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/npc/");
   }

   public boolean isIgnored(File f) {
      return false;
   }

   public String getDTDFileName() {
      return "npc.dtd";
   }

   protected void readData(Element rootElement) throws Exception {
      NpcTemplate template;
      label319:
      for(Iterator npcIterator = rootElement.elementIterator(); npcIterator.hasNext(); ((NpcHolder)this.getHolder()).addTemplate(template)) {
         Element npcElement = (Element)npcIterator.next();
         int npcId = Integer.parseInt(npcElement.attributeValue("id"));
         int templateId = npcElement.attributeValue("template_id") == null ? 0 : Integer.parseInt(npcElement.attributeValue("id"));
         String name = npcElement.attributeValue("name");
         String title = npcElement.attributeValue("title");
         StatsSet set = new StatsSet();
         set.set("npcId", npcId);
         set.set("displayId", templateId);
         set.set("name", name);
         set.set("title", title);
         set.set("baseCpReg", 0);
         set.set("baseCpMax", 0);
         Iterator firstIterator = npcElement.elementIterator();

         while(true) {
            Iterator eIterator;
            Element secondElement;
            while(firstIterator.hasNext()) {
               Element firstElement = (Element)firstIterator.next();
               if (firstElement.getName().equalsIgnoreCase("set")) {
                  set.set(firstElement.attributeValue("name"), firstElement.attributeValue("value"));
               } else {
                  int itemId;
                  if (firstElement.getName().equalsIgnoreCase("equip")) {
                     for(eIterator = firstElement.elementIterator(); eIterator.hasNext(); set.set(secondElement.getName(), String.valueOf(itemId))) {
                        secondElement = (Element)eIterator.next();
                        itemId = Integer.parseInt(secondElement.attributeValue("item_id"));
                        if (ItemHolder.getInstance().getTemplate(itemId) == null) {
                           this._log.error("Undefined item " + itemId + " used in slot " + secondElement.getName() + " of npc " + npcId);
                        }
                     }
                  } else if (firstElement.getName().equalsIgnoreCase("ai_params")) {
                     StatsSet ai = new StatsSet();
                     Iterator eIterator = firstElement.elementIterator();

                     while(eIterator.hasNext()) {
                        Element eElement = (Element)eIterator.next();
                        ai.set(eElement.attributeValue("name"), eElement.attributeValue("value"));
                     }

                     set.set("aiParams", ai);
                  } else if (firstElement.getName().equalsIgnoreCase("attributes")) {
                     int[] attributeAttack = new int[6];
                     int[] attributeDefence = new int[6];
                     Iterator eIterator = firstElement.elementIterator();

                     while(eIterator.hasNext()) {
                        Element eElement = (Element)eIterator.next();
                        l2.gameserver.model.base.Element element;
                        if (eElement.getName().equalsIgnoreCase("defence")) {
                           element = l2.gameserver.model.base.Element.getElementByName(eElement.attributeValue("attribute"));
                           attributeDefence[element.getId()] = Integer.parseInt(eElement.attributeValue("value"));
                        } else if (eElement.getName().equalsIgnoreCase("attack")) {
                           element = l2.gameserver.model.base.Element.getElementByName(eElement.attributeValue("attribute"));
                           attributeAttack[element.getId()] = Integer.parseInt(eElement.attributeValue("value"));
                        }
                     }

                     set.set("baseAttributeAttack", attributeAttack);
                     set.set("baseAttributeDefence", attributeDefence);
                  }
               }
            }

            template = new NpcTemplate(set);
            List<TeleportLocation> teleportLocations = new LinkedList();
            eIterator = npcElement.elementIterator();

            while(true) {
               while(true) {
                  if (!eIterator.hasNext()) {
                     continue label319;
                  }

                  secondElement = (Element)eIterator.next();
                  String nodeName = secondElement.getName();
                  int id;
                  Iterator nextIterator;
                  Element nextElement;
                  int maxLevel;
                  if (nodeName.equalsIgnoreCase("faction")) {
                     String factionId = secondElement.attributeValue("name");
                     Faction faction = new Faction(factionId);
                     id = Integer.parseInt(secondElement.attributeValue("range"));
                     faction.setRange(id);
                     nextIterator = secondElement.elementIterator();

                     while(nextIterator.hasNext()) {
                        nextElement = (Element)nextIterator.next();
                        maxLevel = Integer.parseInt(nextElement.attributeValue("npc_id"));
                        faction.addIgnoreNpcId(maxLevel);
                     }

                     template.setFaction(faction);
                  } else {
                     int validationResult;
                     if (nodeName.equalsIgnoreCase("rewardlist")) {
                        RewardType type = RewardType.valueOf(secondElement.attributeValue("type"));
                        boolean autoLoot = secondElement.attributeValue("auto_loot") != null && Boolean.parseBoolean(secondElement.attributeValue("auto_loot"));
                        RewardList list = new RewardList(type, autoLoot);
                        if (type != RewardType.DISABLED) {
                           nextIterator = secondElement.elementIterator();

                           while(true) {
                              label243:
                              while(nextIterator.hasNext()) {
                                 nextElement = (Element)nextIterator.next();
                                 String nextName = nextElement.getName();
                                 if (nextName.equalsIgnoreCase("group")) {
                                    double enterChance = nextElement.attributeValue("chance") == null ? 1000000.0D : Double.parseDouble(nextElement.attributeValue("chance")) * 10000.0D;
                                    double premiumChance = nextElement.attributeValue("premium_chance") == null ? enterChance : Double.parseDouble(nextElement.attributeValue("premium_chance")) * 10000.0D;
                                    RewardGroup group = type != RewardType.SWEEP && type != RewardType.NOT_RATED_NOT_GROUPED ? new RewardGroup(enterChance, premiumChance) : null;
                                    Iterator rewardIterator = nextElement.elementIterator();

                                    while(true) {
                                       while(true) {
                                          RewardData data;
                                          do {
                                             if (!rewardIterator.hasNext()) {
                                                if (group != null) {
                                                   list.add(group);
                                                }
                                                continue label243;
                                             }

                                             Element rewardElement = (Element)rewardIterator.next();
                                             data = this.parseReward(rewardElement, type);
                                          } while(data == null);

                                          if (type != RewardType.SWEEP && type != RewardType.NOT_RATED_NOT_GROUPED) {
                                             group.addData(data);
                                          } else {
                                             this.warn("Can't load rewardlist from group: " + npcId + "; type: " + type);
                                          }
                                       }
                                    }
                                 } else if (nextName.equalsIgnoreCase("reward")) {
                                    if (type != RewardType.SWEEP && type != RewardType.NOT_RATED_NOT_GROUPED) {
                                       this.warn("Reward can't be without group(and not grouped): " + npcId + "; type: " + type);
                                    } else {
                                       RewardData data = this.parseReward(nextElement, type);
                                       if (data != null) {
                                          RewardGroup g = new RewardGroup(1000000.0D, 1000000.0D);
                                          g.addData(data);
                                          list.add(g);
                                       }
                                    }
                                 }
                              }

                              if (type == RewardType.RATED_GROUPED || type == RewardType.NOT_RATED_GROUPED) {
                                 validationResult = list.validate();
                                 if (validationResult != 0) {
                                    String errorMessage = "Problems with rewardlist for npc: " + npcId + "; type: " + type;
                                    if ((validationResult & 1) != 0) {
                                       errorMessage = errorMessage + " - Issues with regular items";
                                    }

                                    if ((validationResult & 2) != 0) {
                                       errorMessage = errorMessage + " - Issues with premium items";
                                    }

                                    this.warn(errorMessage);
                                 }
                              }

                              template.putRewardList(type, list);
                              break;
                           }
                        }
                     } else {
                        Iterator sublistIterator;
                        Element subListElement;
                        if (nodeName.equalsIgnoreCase("skills")) {
                           sublistIterator = secondElement.elementIterator();

                           while(sublistIterator.hasNext()) {
                              subListElement = (Element)sublistIterator.next();
                              id = Integer.parseInt(subListElement.attributeValue("id"));
                              validationResult = Integer.parseInt(subListElement.attributeValue("level"));
                              if (SkillTable.getInstance().getInfo(id, validationResult) == null) {
                                 this._log.error("Undefined id " + id + " and level " + validationResult + " of npc " + npcId);
                              }

                              if (id == 4416) {
                                 template.setRace(validationResult);
                              }

                              Skill skill = SkillTable.getInstance().getInfo(id, validationResult);
                              if (skill != null) {
                                 template.addSkill(skill);
                              }
                           }
                        } else if (nodeName.equalsIgnoreCase("minions")) {
                           sublistIterator = secondElement.elementIterator();

                           while(sublistIterator.hasNext()) {
                              subListElement = (Element)sublistIterator.next();
                              id = Integer.parseInt(subListElement.attributeValue("npc_id"));
                              validationResult = Integer.parseInt(subListElement.attributeValue("count"));
                              template.addMinion(new MinionData(id, validationResult));
                           }
                        } else if (nodeName.equalsIgnoreCase("teach_classes")) {
                           sublistIterator = secondElement.elementIterator();

                           while(sublistIterator.hasNext()) {
                              subListElement = (Element)sublistIterator.next();
                              id = Integer.parseInt(subListElement.attributeValue("id"));
                              template.addTeachInfo(ClassId.VALUES[id]);
                           }
                        } else if (nodeName.equalsIgnoreCase("absorblist")) {
                           sublistIterator = secondElement.elementIterator();

                           while(sublistIterator.hasNext()) {
                              subListElement = (Element)sublistIterator.next();
                              id = Integer.parseInt(subListElement.attributeValue("chance"));
                              validationResult = subListElement.attributeValue("cursed_chance") == null ? 0 : Integer.parseInt(subListElement.attributeValue("cursed_chance"));
                              int minLevel = Integer.parseInt(subListElement.attributeValue("min_level"));
                              maxLevel = Integer.parseInt(subListElement.attributeValue("max_level"));
                              boolean skill = subListElement.attributeValue("skill") != null && Boolean.parseBoolean(subListElement.attributeValue("skill"));
                              AbsorbType absorbType = AbsorbType.valueOf(subListElement.attributeValue("type"));
                              template.addAbsorbInfo(new AbsorbInfo(skill, absorbType, id, validationResult, minLevel, maxLevel));
                           }
                        } else if (nodeName.equalsIgnoreCase("teleportlist")) {
                           sublistIterator = secondElement.elementIterator();

                           while(sublistIterator.hasNext()) {
                              subListElement = (Element)sublistIterator.next();
                              id = Integer.parseInt(subListElement.attributeValue("id"));
                              List<TeleportLocation> list = new ArrayList();
                              Iterator targetIterator = subListElement.elementIterator();

                              while(targetIterator.hasNext()) {
                                 Element targetElement = (Element)targetIterator.next();
                                 int itemId = Integer.parseInt(targetElement.attributeValue("item_id", "57"));
                                 long price = (long)Integer.parseInt(targetElement.attributeValue("price"));
                                 int minLevel = Integer.parseInt(targetElement.attributeValue("min_level", "0"));
                                 int maxLevel = Integer.parseInt(targetElement.attributeValue("max_level", "0"));
                                 String nameCustomStringAddr = targetElement.attributeValue("name").trim();
                                 int castleId = Integer.parseInt(targetElement.attributeValue("castle_id", "0"));
                                 int keyItemId = Integer.parseInt(targetElement.attributeValue("key_item_id", "-1"));
                                 TeleportLocation loc = new TeleportLocation(itemId, price, minLevel, maxLevel, nameCustomStringAddr, castleId, keyItemId);
                                 loc.set(Location.parseLoc(targetElement.attributeValue("loc")));
                                 if (minLevel > 0 || maxLevel > 0) {
                                    Iterator var29 = teleportLocations.iterator();

                                    while(var29.hasNext()) {
                                       TeleportLocation minMaxCheckLoc = (TeleportLocation)var29.next();
                                       if (minMaxCheckLoc.x == loc.x && minMaxCheckLoc.y == loc.y && minMaxCheckLoc.z == loc.z) {
                                          this._log.warn("Teleport location may intersect for " + targetElement.asXML());
                                       }
                                    }
                                 }

                                 teleportLocations.add(loc);
                                 list.add(loc);
                              }

                              template.addTeleportList(id, (TeleportLocation[])list.toArray(new TeleportLocation[list.size()]));
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private RewardData parseReward(Element rewardElement, RewardType rewardType) {
      int itemId = Integer.parseInt(rewardElement.attributeValue("item_id"));
      if (rewardType == RewardType.SWEEP) {
         if (ArrayUtils.contains(Config.NO_DROP_ITEMS_FOR_SWEEP, itemId)) {
            return null;
         }
      } else if (ArrayUtils.contains(Config.NO_DROP_ITEMS, itemId)) {
         return null;
      }

      if (Config.ALLOW_ONLY_THESE_DROP_ITEMS_ID != null && Config.ALLOW_ONLY_THESE_DROP_ITEMS_ID.length != 0 && !ArrayUtils.contains(Config.ALLOW_ONLY_THESE_DROP_ITEMS_ID, itemId)) {
         return null;
      } else {
         int min = Integer.parseInt(rewardElement.attributeValue("min"));
         int max = Integer.parseInt(rewardElement.attributeValue("max"));
         String enchantMin = rewardElement.attributeValue("enchant_min");
         String enchantMax = rewardElement.attributeValue("enchant_max");
         int chance = (int)(Double.parseDouble(rewardElement.attributeValue("chance")) * 10000.0D);
         if (rewardType == RewardType.SWEEP && (ArrayUtils.isEmpty(Config.SPOIL_ITEMS_ID_FOR_RATE) || ArrayUtils.contains(Config.SPOIL_ITEMS_ID_FOR_RATE, itemId))) {
            chance = (int)((double)chance * Config.SPOIL_ITEMS_CHANCE_RATE);
         }

         int premiumChance;
         if (rewardElement.attributeValue("premium_chance") != null) {
            premiumChance = (int)(Double.parseDouble(rewardElement.attributeValue("premium_chance")) * 10000.0D);
            if (rewardType == RewardType.SWEEP && (ArrayUtils.isEmpty(Config.SPOIL_ITEMS_ID_FOR_RATE) || ArrayUtils.contains(Config.SPOIL_ITEMS_ID_FOR_RATE, itemId))) {
               premiumChance = (int)((double)premiumChance * Config.SPOIL_ITEMS_CHANCE_RATE);
            }
         } else {
            premiumChance = chance;
         }

         RewardData data = new RewardData(itemId);
         if (data.getItem().isHerb()) {
            data.setChance((double)chance * Config.RATE_DROP_HERBS);
            data.setPremiumChance((double)premiumChance * Config.RATE_DROP_HERBS);
         } else {
            data.setChance((double)chance);
            data.setPremiumChance((double)premiumChance);
         }

         if (StringUtils.isNumeric(enchantMin) && StringUtils.isNumeric(enchantMax) && data.getItem().isEnchantable()) {
            data.setEnchantMin(Math.min(Integer.parseInt(enchantMin), 65535));
            data.setEnchantMax(Math.min(Integer.parseInt(enchantMax), 65535));
         }

         data.setMinDrop((long)min);
         data.setMaxDrop((long)max);
         return data;
      }
   }
}
