package events.battle.model;

import events.battle.BattleGvG;
import events.battle.util.BattleUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import org.apache.commons.lang3.tuple.Pair;

public class BattleConditions {
   public static Map<String, Pair<String, String>> getCheckPlayersError() {
      Map<String, Pair<String, String>> errorMessages = new HashMap();
      errorMessages.put("noplayer", Pair.of("Ваша группа удалена с турнира, поскольку Вы не найдены.", "Your group has been removed from the tournament because you are was not found."));
      errorMessages.put("ip", Pair.of("Ваша группа удалена с турнира, поскольку Вы уже зарегистрированы.", "Your group has been removed from the tournament because you are already registered."));
      errorMessages.put("hwid", Pair.of("Ваша группа удалена с турнира, поскольку Вы уже зарегистрированы.", "Your group has been removed from the tournament because you are already registered."));
      errorMessages.put("inparty", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в пати.", "Your group has been removed from the tournament because you are in a party."));
      errorMessages.put("noparty", Pair.of("Ваша группа удалена с турнира, поскольку Вы не находитесь в пати.", "Your group has been removed from the tournament because you are not in a party."));
      errorMessages.put("noleader", Pair.of("Ваша группа удалена с турнира, поскольку Вы не являетесь лидером группы.", "Your group has been removed from the tournament because you are not the leader of the group."));
      errorMessages.put("levels", Pair.of("Ваша группа удалена с турнира, поскольку Вы не соответствуете требованиям уровней для турнира.", "Your group has been removed from the tournament because you do not meet the level requirements for the tournament."));
      errorMessages.put("minmembers", Pair.of("Ваша группа удалена с турнира, поскольку Вы не соответствует требованиям количества игроков в группе для турнира.", "Your group has been removed from the tournament because you do not meet the group requirement for the tournament."));
      errorMessages.put("maxmembers", Pair.of("Ваша группа удалена с турнира, поскольку Вы не соответствует требованиям количества игроков в группе для турнира.", "Your group has been removed from the tournament because you do not meet the group requirement for the tournament."));
      errorMessages.put("mount", Pair.of("Ваша группа удалена с турнира, поскольку Вы используете ездовое животное.", "Your group has been removed from the tournament because you are using a mount."));
      errorMessages.put("duel", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в дуэли.", "Your group has been removed from the tournament because you are in a duel."));
      errorMessages.put("event", Pair.of("Ваша группа удалена с турнира, поскольку Вы принимаете участие в другом ивенте.", "Your group has been removed from the tournament because you are participating in another event."));
      errorMessages.put("olympiad", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в списке ожидания Олимпиады или принимает участие в ней.", "Your group has been removed from the tournament because you are on the waiting list for the Olympiad or are taking part in it."));
      errorMessages.put("teleport", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в состоянии телепортации.", "Your group has been removed from the tournament because you are in a state of teleportation."));
      errorMessages.put("rift", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в Dimensional Rift.", "Your group has been removed from the tournament because you are in the Dimensional Rift."));
      errorMessages.put("cursed", Pair.of("Ваша группа удалена с турнира, поскольку Вы обладаете Проклятым Оружием.", "Your party has been removed from the tournament because you are in possession of the Cursed Weapon."));
      errorMessages.put("nopeace", Pair.of("Ваша группа удалена с турнира, поскольку Вы не находитесь в мирной зоне.", "Your group has been removed from the tournament because you are not in a peaceful zone."));
      errorMessages.put("observer", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в режиме обозревания.", "Your group has been removed from the tournament because you are in observer mode."));
      errorMessages.put("instance", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в инстансе.", "Your group has been removed from the tournament because you are in an instance."));
      errorMessages.put("jail", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в тюрьме.", "Your group has been removed from the tournament because you are in jail."));
      return errorMessages;
   }

   public static Map<String, Pair<String, String>> getSingeRegError() {
      Map<String, Pair<String, String>> errorMessages = new HashMap();
      errorMessages.put("noplayer", Pair.of("Нельзя зарегистрировать команду, поскольку %s не найден.", "The command cannot be registered because %s was not found."));
      errorMessages.put("ip", Pair.of("Нельзя зарегистрировать команду, поскольку %s уже зарегистрирован.", "Command cannot be registered because %s is already registered."));
      errorMessages.put("hwid", Pair.of("Нельзя зарегистрировать команду, поскольку %s уже зарегистрирован.", "Command cannot be registered because %s is already registered."));
      errorMessages.put("inparty", Pair.of("Нельзя зарегистрировать команду, поскольку %s находитесь в пати.", "Cannot register command because %s is in a party."));
      errorMessages.put("noparty", Pair.of("Нельзя зарегистрировать команду, поскольку %s не находитесь в пати.", "Cannot register command because %s is not in the party."));
      errorMessages.put("noleader", Pair.of("Нельзя зарегистрировать команду, поскольку %s не являетесь лидером группы.", "The command cannot be registered because %s is not a group leader."));
      errorMessages.put("levels", Pair.of("Нельзя зарегистрировать команду, поскольку %s не соответствуете требованиям уровней для турнира.", "The team cannot be registered because %s does not meet the level requirement for the tournament."));
      errorMessages.put("mount", Pair.of("Нельзя зарегистрировать команду, поскольку %s используете ездовое животное.", "Command cannot be registered because %s is using a mount."));
      errorMessages.put("duel", Pair.of("Нельзя зарегистрировать команду, поскольку %s находитесь в дуэли.", "Cannot register command because %s is in a duel."));
      errorMessages.put("event", Pair.of("Нельзя зарегистрировать команду, поскольку %s принимаете участие в другом ивенте.", "Cannot register a team because %s is participating in another event."));
      errorMessages.put("olympiad", Pair.of("Нельзя зарегистрировать команду, поскольку %s находитесь в списке ожидания Олимпиады или принимает участие в ней.", "Cannot register a team because %s is on or participating in an Olympiad waiting list."));
      errorMessages.put("teleport", Pair.of("Нельзя зарегистрировать команду, поскольку %s находитесь в состоянии телепортации.", "Command cannot be registered because %s is in a teleportation state."));
      errorMessages.put("rift", Pair.of("Нельзя зарегистрировать команду, поскольку %s находитесь в Dimensional Rift.", "Cannot register command because %s is in Dimensional Rift."));
      errorMessages.put("cursed", Pair.of("Нельзя зарегистрировать команду, поскольку %s обладаете Проклятым Оружием.", "You can't register a command because %s is in possession of a Cursed Weapon."));
      errorMessages.put("nopeace", Pair.of("Нельзя зарегистрировать команду, поскольку %s не находитесь в мирной зоне.", "Cannot register command because %s is not in a peaceful zone."));
      errorMessages.put("observer", Pair.of("Нельзя зарегистрировать команду, поскольку %s находитесь в режиме обозревания.", "Command cannot be registered because %s is in browse mode."));
      errorMessages.put("instance", Pair.of("Нельзя зарегистрировать команду, поскольку %s находитесь в инстансе.", "Command cannot be registered because %s is in an instance."));
      errorMessages.put("jail", Pair.of("Нельзя зарегистрировать команду, поскольку %s находитесь в тюрьме.", "Cannot register command because %s is in jail."));
      return errorMessages;
   }

   public static Map<String, Pair<String, String>> getCheckDError() {
      Map<String, Pair<String, String>> errorMessages = new HashMap();
      errorMessages.put("noplayer", Pair.of("Ваша группа удалена с турнира, поскольку %s не найден.", "Your party has been removed from the tournament because %s was not found."));
      errorMessages.put("ip", Pair.of("Ваша группа удалена с турнира, поскольку %s уже зарегистрирован.", "Your group has been removed from the tournament because %s is already registered."));
      errorMessages.put("hwid", Pair.of("Ваша группа удалена с турнира, поскольку %s уже зарегистрирован.", "Your group has been removed from the tournament because %s is already registered."));
      errorMessages.put("inparty", Pair.of("Ваша группа удалена с турнира, поскольку %s находится в пати.", "Your party has been removed from the tournament because %s is in the party."));
      errorMessages.put("noparty", Pair.of("Ваша группа удалена с турнира, поскольку %s не находится в пати.", "Your party has been removed from the tournament because %s is not in the party."));
      errorMessages.put("noleader", Pair.of("Ваша группа удалена с турнира, поскольку %s не является лидером группы.", "Your party has been removed from the tournament because %s is not a party leader."));
      errorMessages.put("levels", Pair.of("Ваша группа удалена с турнира, поскольку %s не соответствует требованиям уровней для турнира.", "Your party has been removed from the tournament because %s does not meet the tournament level requirements."));
      errorMessages.put("minmembers", Pair.of("Ваша группа удалена с турнира, поскольку %s не соответствует требованиям количества игроков в группе для турнира.", "Your party has been removed from the tournament because %s does not meet the tournament party requirement."));
      errorMessages.put("maxmembers", Pair.of("Ваша группа удалена с турнира, поскольку %s не соответствует требованиям количества игроков в группе для турнира.", "Your party has been removed from the tournament because %s does not meet the tournament party requirement."));
      errorMessages.put("mount", Pair.of("Ваша группа удалена с турнира, поскольку %s использует ездовое животное.", "Your party has been removed from the tournament because %s is using a mount."));
      errorMessages.put("duel", Pair.of("Ваша группа удалена с турнира, поскольку %s находится в дуэли.", "Your party has been removed from the tournament because %s is in a duel."));
      errorMessages.put("event", Pair.of("Ваша группа удалена с турнира, поскольку %s принимает участие в другом ивенте.", "Your party has been removed from the tournament because %s is participating in another event."));
      errorMessages.put("olympiad", Pair.of("Ваша группа удалена с турнира, поскольку %s находится в списке ожидания Олимпиады или принимает участие в ней.", "Your group has been removed from the tournament because %s is on or participating in an Olympiad waitlist."));
      errorMessages.put("teleport", Pair.of("Ваша группа удалена с турнира, поскольку %s находится в состоянии телепортации.", "Your party has been removed from the tournament because %s is in a teleportation state."));
      errorMessages.put("rift", Pair.of("Ваша группа удалена с турнира, поскольку %s находится в Dimensional Rift.", "Your party has been removed from the tournament because %s is in the Dimensional Rift."));
      errorMessages.put("cursed", Pair.of("Ваша группа удалена с турнира, поскольку %s обладает Проклятым Оружием.", "Your party has been removed from the tournament because %s has a Cursed Weapon."));
      errorMessages.put("nopeace", Pair.of("Ваша группа удалена с турнира, поскольку %s не находится в мирной зоне.", "Your party has been removed from the tournament because %s is not in a peaceful zone."));
      errorMessages.put("observer", Pair.of("Ваша группа удалена с турнира, поскольку %s находится в режиме обозревания.", "Your party has been removed from the tournament because %s is in observer mode."));
      errorMessages.put("instance", Pair.of("Ваша группа удалена с турнира, поскольку %s находится в инстансе.", "Your party has been removed from the tournament because %s is in the instance."));
      errorMessages.put("jail", Pair.of("Ваша группа удалена с турнира, поскольку %s находится в тюрьме.", "Your party has been removed from the tournament because %s is in jail."));
      return errorMessages;
   }

   public static Map<String, Pair<String, String>> getSingleCheckDError() {
      Map<String, Pair<String, String>> errorMessages = new HashMap();
      errorMessages.put("inparty", Pair.of("Ваша группа удалена с турнира, поскольку Вы находится в пати.", "Your group has been removed from the tournament because you are in a party."));
      errorMessages.put("ip", Pair.of("Ваша группа удалена с турнира, поскольку Вы уже зарегистрированы.", "Your group has been removed from the tournament because you are already registered."));
      errorMessages.put("hwid", Pair.of("Ваша группа удалена с турнира, поскольку Вы уже зарегистрированы.", "Your group has been removed from the tournament because you are already registered."));
      errorMessages.put("noleader", Pair.of("Ваша группа удалена с турнира, поскольку Вы не являетесь лидером группы.", "Your group has been removed from the tournament because you are not the leader of the group."));
      errorMessages.put("levels", Pair.of("Ваша группа удалена с турнира, поскольку Вы не соответствуете требованиям уровней для турнира.", "Your group has been removed from the tournament because you do not meet the level requirements for the tournament."));
      errorMessages.put("minmembers", Pair.of("Ваша группа удалена с турнира, поскольку Вы не соответствуете требованиям количества игроков в группе для турнира.", "Your group has been removed from the tournament because you do not meet the group size requirements for the tournament."));
      errorMessages.put("maxmembers", Pair.of("Ваша группа удалена с турнира, поскольку Вы не соответствует требованиям количества игроков в группе для турнира.", "Your group has been removed from the tournament because you do not meet the group requirement for the tournament."));
      errorMessages.put("mount", Pair.of("Ваша группа удалена с турнира, поскольку Вы используете ездовое животное.", "Your group has been removed from the tournament because you are using a mount."));
      errorMessages.put("duel", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в дуэли.", "Your group has been removed from the tournament because you are in a duel."));
      errorMessages.put("event", Pair.of("Ваша группа удалена с турнира, поскольку Вы принимаете участие в другом ивенте.", "Your group has been removed from the tournament because you are participating in another event."));
      errorMessages.put("olympiad", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в списке ожидания Олимпиады или принимает участие в ней.", "Your group has been removed from the tournament because you are on the waiting list for the Olympiad or are taking part in it."));
      errorMessages.put("teleport", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в состоянии телепортации.", "Your group has been removed from the tournament because you are in a state of teleportation."));
      errorMessages.put("rift", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в Dimensional Rift.", "Your group has been removed from the tournament because you are in the Dimensional Rift."));
      errorMessages.put("cursed", Pair.of("Ваша группа удалена с турнира, поскольку Вы обладаете Проклятым Оружием.", "Your party has been removed from the tournament because you are in possession of the Cursed Weapon."));
      errorMessages.put("nopeace", Pair.of("Ваша группа удалена с турнира, поскольку Вы не находитесь в мирной зоне.", "Your group has been removed from the tournament because you are not in a peaceful zone."));
      errorMessages.put("observer", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в режиме обозревания.", "Your group has been removed from the tournament because you are in observer mode."));
      errorMessages.put("instance", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в инстансе.", "Your group has been removed from the tournament because you are in an instance."));
      errorMessages.put("jail", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в тюрьме.", "Your group has been removed from the tournament because you are in jail."));
      return errorMessages;
   }

   public static Map<String, Pair<String, String>> getRegCheckError() {
      Map<String, Pair<String, String>> errorMessages = new HashMap();
      errorMessages.put("noplayer", Pair.of("Игрок %s не найден.", "Player %s not found."));
      errorMessages.put("ip", Pair.of("Игрок %s уже зарегистрирован.", "Player %s is already registered."));
      errorMessages.put("hwid", Pair.of("Игрок %s уже зарегистрирован.", "Player %s is already registered."));
      errorMessages.put("inparty", Pair.of("Игрок %s находится в пати.", "Player %s is in the party."));
      errorMessages.put("noparty", Pair.of("Игрок %s не находится в пати.", "Player %s is not in the party."));
      errorMessages.put("noleader", Pair.of("Игрок %s не является лидером группы.", "Player %s is not a group leader."));
      errorMessages.put("levels", Pair.of("Игрок %s не соответствует требованиям уровней для турнира.", "Player %s does not meet the level requirement for the tournament."));
      errorMessages.put("minmembers", Pair.of("Игрок %s не соответствует требованиям количества игроков в группе для турнира.", "Player %s does not meet the tournament group requirement"));
      errorMessages.put("maxmembers", Pair.of("Игрок %s не соответствует требованиям количества игроков в группе для турнира.", "Player %s does not meet the tournament group requirement"));
      errorMessages.put("mount", Pair.of("Игрок %s использует ездовое животное.", "Player %s is using a mount."));
      errorMessages.put("duel", Pair.of("Игрок %s находится в дуэли.", "Player %s is in a duel."));
      errorMessages.put("event", Pair.of("Игрок %s принимает участие в другом ивенте.", "Player %s is participating in another event."));
      errorMessages.put("olympiad", Pair.of("Игрок %s находится в списке ожидания Олимпиады или принимает участие в ней.", "Player %s is on or participating in the Olympiad waiting list."));
      errorMessages.put("teleport", Pair.of("Игрок %s находится в состоянии телепортации.", "Player %s is in a state of teleportation."));
      errorMessages.put("rift", Pair.of("Игрок %s находится в Dimensional Rift.", "Player %s is in the Dimensional Rift."));
      errorMessages.put("cursed", Pair.of("Игрок %s обладает Проклятым Оружием.", "Player %s has a Cursed Weapon."));
      errorMessages.put("nopeace", Pair.of("Игрок %s не находится в мирной зоне.", "Player %s is not in a peaceful zone."));
      errorMessages.put("observer", Pair.of("Игрок %s находится в режиме обозревания.", "Player %s is in observer mode."));
      errorMessages.put("instance", Pair.of("Игрок %s находится в инстансе.", "Player %s is in the instance."));
      errorMessages.put("jail", Pair.of("Игрок %s находится в тюрьме.", "Player %s is in jail."));
      return errorMessages;
   }

   public static Map<String, Pair<String, String>> getGroupCheckError() {
      Map<String, Pair<String, String>> errorMessages = new HashMap();
      errorMessages.put("noplayer", Pair.of("Игрок %s не найден.", "Player %s not found."));
      errorMessages.put("ip", Pair.of("Игрок %s уже зарегистрирован.", "Player %s is already registered."));
      errorMessages.put("hwid", Pair.of("Игрок %s уже зарегистрирован.", "Player %s is already registered."));
      errorMessages.put("inparty", Pair.of("Игрок %s находится в пати.", "Player %s is in the party."));
      errorMessages.put("noparty", Pair.of("Игрок %s не находится в пати.", "Player %s is not in the party."));
      errorMessages.put("noleader", Pair.of("Игрок %s не является лидером группы.", "Player %s is not a group leader."));
      errorMessages.put("levels", Pair.of("Игрок %s не соответствует требованиям уровней для турнира.", "Player %s does not meet the level requirement for the tournament."));
      errorMessages.put("minmembers", Pair.of("Игрок %s не соответствует требованиям количества игроков в группе для турнира.", "Player %s does not meet the tournament group requirement"));
      errorMessages.put("maxmembers", Pair.of("Игрок %s не соответствует требованиям количества игроков в группе для турнира.", "Player %s does not meet the tournament group requirement"));
      errorMessages.put("mount", Pair.of("Игрок %s использует ездовое животное.", "Player %s is using a mount."));
      errorMessages.put("duel", Pair.of("Игрок %s находится в дуэли.", "Player %s is in a duel."));
      errorMessages.put("event", Pair.of("Игрок %s принимает участие в другом ивенте.", "Player %s is participating in another event."));
      errorMessages.put("olympiad", Pair.of("Игрок %s находится в списке ожидания Олимпиады или принимает участие в ней.", "Player %s is on or participating in the Olympiad waiting list."));
      errorMessages.put("teleport", Pair.of("Игрок %s находится в состоянии телепортации.", "Player %s is in a state of teleportation."));
      errorMessages.put("rift", Pair.of("Игрок %s находится в Dimensional Rift.", "Player %s is in the Dimensional Rift."));
      errorMessages.put("cursed", Pair.of("Игрок %s обладает Проклятым Оружием.", "Player %s has a Cursed Weapon."));
      errorMessages.put("nopeace", Pair.of("Игрок %s не находится в мирной зоне.", "Player %s is not in a peaceful zone."));
      errorMessages.put("observer", Pair.of("Игрок %s находится в режиме обозревания.", "Player %s is in observer mode."));
      errorMessages.put("instance", Pair.of("Игрок %s находится в инстансе.", "Player %s is in the instance."));
      errorMessages.put("jail", Pair.of("Игрок %s находится в тюрьме.", "Player %s is in jail."));
      return errorMessages;
   }

   public static Map<String, Pair<String, String>> getSingleGroupCheckError() {
      Map<String, Pair<String, String>> errorMessages = new HashMap();
      errorMessages.put("inparty", Pair.of("Вы находитесь в пати.", "You are in a party."));
      errorMessages.put("ip", Pair.of("Вы уже зарегистрированы.", "You are already registered."));
      errorMessages.put("hwid", Pair.of("Вы уже зарегистрированы.", "You are already registered."));
      errorMessages.put("levels", Pair.of("Вы не соответствуете требованиям уровней для турнира.", "You do not meet the level requirements for the tournament."));
      errorMessages.put("minmembers", Pair.of("Вы не соответствуете требованиям количества игроков в группе для турнира.", "You do not meet the tournament group size requirements."));
      errorMessages.put("maxmembers", Pair.of("Вы не соответствуете требованиям количества игроков в группе для турнира.", "You do not meet the tournament group size requirements."));
      errorMessages.put("mount", Pair.of("Вы используете ездовое животное.", "You are using a mount."));
      errorMessages.put("duel", Pair.of("Вы находитесь в дуэли.", "You are in a duel."));
      errorMessages.put("event", Pair.of("Вы принимаете участие в другом ивенте.", "You are participating in another event."));
      errorMessages.put("olympiad", Pair.of("Вы находитесь в списке ожидания Олимпиады или принимает участие в ней.", "You are on the waiting list for or participating in the Olympics."));
      errorMessages.put("teleport", Pair.of("Вы находитесь в состоянии телепортации.", "You are in a state of teleportation."));
      errorMessages.put("rift", Pair.of("Вы находитесь в Dimensional Rift.", "You are in Dimensional Rift."));
      errorMessages.put("cursed", Pair.of("Вы обладаете Проклятым Оружием.", "You are in possession of the Cursed Weapon."));
      errorMessages.put("nopeace", Pair.of("Вы не находитесь в мирной зоне.", "You are not in a peaceful zone."));
      errorMessages.put("observer", Pair.of("Вы находитесь в режиме обозревания.", "You are in observer mode."));
      errorMessages.put("instance", Pair.of("Вы находитесь в инстансе.", "You are in an instance."));
      errorMessages.put("jail", Pair.of("Вы находитесь в тюрьме.", "You are in jail."));
      return errorMessages;
   }

   public static Map<String, Pair<String, String>> getSingleCheckPlayerError() {
      Map<String, Pair<String, String>> errorMessages = new HashMap();
      errorMessages.put("inparty", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в пати.", "Your group has been removed from the tournament because you are in a party."));
      errorMessages.put("ip", Pair.of("Ваша группа удалена с турнира, поскольку Вы уже зарегистрированы.", "Your group has been removed from the tournament because you are already registered."));
      errorMessages.put("hwid", Pair.of("Ваша группа удалена с турнира, поскольку Вы уже зарегистрированы.", "Your group has been removed from the tournament because you are already registered."));
      errorMessages.put("noleader", Pair.of("Ваша группа удалена с турнира, поскольку Вы не являетесь лидером группы.", "Your group has been removed from the tournament because you are not the leader of the group."));
      errorMessages.put("levels", Pair.of("Ваша группа удалена с турнира, поскольку Вы не соответствуете требованиям уровней для турнира.", "Your group has been removed from the tournament because you do not meet the level requirements for the tournament."));
      errorMessages.put("minmembers", Pair.of("Ваша группа удалена с турнира, поскольку Вы не соответствует требованиям количества игроков в группе для турнира.", "Your group has been removed from the tournament because you do not meet the group requirement for the tournament."));
      errorMessages.put("maxmembers", Pair.of("Ваша группа удалена с турнира, поскольку Вы не соответствует требованиям количества игроков в группе для турнира.", "Your group has been removed from the tournament because you do not meet the group requirement for the tournament."));
      errorMessages.put("mount", Pair.of("Ваша группа удалена с турнира, поскольку Вы используете ездовое животное.", "Your group has been removed from the tournament because you are using a mount."));
      errorMessages.put("duel", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в дуэли.", "Your group has been removed from the tournament because you are in a duel."));
      errorMessages.put("event", Pair.of("Ваша группа удалена с турнира, поскольку Вы принимаете участие в другом ивенте.", "Your group has been removed from the tournament because you are participating in another event."));
      errorMessages.put("olympiad", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в списке ожидания Олимпиады или принимает участие в ней.", "Your group has been removed from the tournament because you are on the waiting list for the Olympiad or are taking part in it."));
      errorMessages.put("teleport", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в состоянии телепортации.", "Your group has been removed from the tournament because you are in a state of teleportation."));
      errorMessages.put("rift", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в Dimensional Rift.", "Your group has been removed from the tournament because you are in the Dimensional Rift."));
      errorMessages.put("cursed", Pair.of("Ваша группа удалена с турнира, поскольку Вы обладаете Проклятым Оружием.", "Your party has been removed from the tournament because you are in possession of the Cursed Weapon."));
      errorMessages.put("nopeace", Pair.of("Ваша группа удалена с турнира, поскольку Вы не находитесь в мирной зоне.", "Your group has been removed from the tournament because you are not in a peaceful zone."));
      errorMessages.put("observer", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в режиме обозревания.", "Your group has been removed from the tournament because you are in observer mode."));
      errorMessages.put("instance", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в инстансе.", "Your group has been removed from the tournament because you are in an instance."));
      errorMessages.put("jail", Pair.of("Ваша группа удалена с турнира, поскольку Вы находитесь в тюрьме.", "Your group has been removed from the tournament because you are in jail."));
      return errorMessages;
   }

   public static boolean preCheckAddGroup(Player player, String commandName, BattleGvG battle) {
      if (!battle.isRegistrationActive()) {
         if (player.isLangRus()) {
            player.sendMessage("Регистрация на GvG " + battle.getType().getNameType() + " турнир неактивна.");
         } else {
            player.sendMessage("GvG registration " + battle.getType().getNameType() + " tournament inactive.");
         }

         return false;
      } else if (!BattleUtil.isMatchingRegexp(commandName, battle.getType().getCNameTemplate())) {
         player.sendMessage(player.isLangRus() ? "Недопустимое имя команды." : "Invalid command name.");
         return false;
      } else if (battle.getLeaderList().contains(player.getRef())) {
         if (player.isLangRus()) {
            player.sendMessage("Вы уже зарегистрировались на GvG " + battle.getType().getNameType() + " турнир.");
         } else {
            player.sendMessage("You have already registered for the GvG " + battle.getType().getNameType() + " tournament.");
         }

         return false;
      } else {
         Iterator var3 = battle.getCommandNames().values().iterator();

         String prohibitedClass;
         do {
            if (!var3.hasNext()) {
               if (battle.getType().isToArena() && battle.getPoints().isEmpty()) {
                  player.sendMessage(player.isLangRus() ? "Параметры турнира не позволяют принять участие." : "Tournament parameters do not allow participation.");
                  return false;
               }

               if (!player.isInParty()) {
                  player.sendMessage(player.isLangRus() ? "Вы не состоите в группе и не можете подать заявку." : "You are not a member of the group and cannot apply.");
                  return false;
               }

               if (battle.isCCType()) {
                  if (!player.getParty().isInCommandChannel()) {
                     player.sendMessage(player.isLangRus() ? "Участвовать можно только в Командном Канале." : "You can only participate in the Command Channel.");
                     return false;
                  }

                  if (player.getParty().getCommandChannel().getChannelLeader() != player) {
                     player.sendMessage(player.isLangRus() ? "Только лидер Командного Канала может подать заявку." : "Only the leader of the Command Channel can apply.");
                     return false;
                  }

                  if (player.getParty().getCommandChannel().getParties().size() > battle.getCCMax()) {
                     if (player.isLangRus()) {
                        player.sendMessage("Превышен лимит групп Командного Канала. Максимум " + battle.getCCMax() + " групп" + (battle.getCCMax() < 5 ? "ы" : "") + ".");
                     } else {
                        player.sendMessage("Command channel group limit exceeded. Maximum " + battle.getCCMax() + " group" + (battle.getCCMax() < 5 ? "s" : "") + ".");
                     }

                     return false;
                  }
               } else {
                  if (!player.getParty().isLeader(player)) {
                     player.sendMessage(player.isLangRus() ? "Только лидер группы может подать заявку." : "Only the group leader can apply.");
                     return false;
                  }

                  if (player.getParty().isInCommandChannel()) {
                     player.sendMessage(player.isLangRus() ? "Чтобы участвовать в турнире, Вы должны покинуть Командный Канал." : "To participate in the tournament, you must leave the Command Channel.");
                     return false;
                  }

                  int memberCount = player.getParty().getMemberCount();
                  if (memberCount < battle.getType().getMembers()) {
                     if (player.isLangRus()) {
                        player.sendMessage("Вы состоите в неполной группе. Минимальное кол-во членов группы - " + battle.getType().getMembers() + ".");
                     } else {
                        player.sendMessage("You are in an incomplete group. The minimum number of group members is " + battle.getType().getMembers() + ".");
                     }

                     return false;
                  }

                  if (memberCount > battle.getType().getMembers()) {
                     if (player.isLangRus()) {
                        player.sendMessage("Вы состоите в группе превышающей лимит. Максимальное кол-во членов группы - " + battle.getType().getMembers() + ".");
                     } else {
                        player.sendMessage("You are in a group that exceeds the limit. The maximum number of group members is " + battle.getType().getMembers() + ".");
                     }

                     return false;
                  }
               }

               if (battle.getLeaderList().size() >= battle.getType().getCommandsMax()) {
                  player.sendMessage(player.isLangRus() ? "Достигнут лимит количества групп для участия в турнире. Заявка отклонена." : "The limit of the number of groups for participation in the tournament has been reached. Application rejected.");
                  return false;
               }

               List<Player> party = battle.isCCType() ? player.getParty().getCommandChannel().getMembers() : player.getParty().getPartyMembers();
               prohibitedClass = BattleUtil.getCheckProhibitedClass(battle.getType().getProhibitedClassIds(), party);
               if (prohibitedClass != null) {
                  if (player.isLangRus()) {
                     player.sendMessage("Класс " + prohibitedClass + " запрещен в этом режиме.");
                  } else {
                     player.sendMessage("The class " + prohibitedClass + " is prohibited in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.dreadnought, party) > battle.getType().getDreadnoughtAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.duelist, party) > battle.getType().getDuelistAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDuelistAllowed() + " Duelist(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDuelistAllowed() + " Duelist(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.archmage, party) > battle.getType().getArchmageAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getArchmageAllowed() + " Archmage(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getArchmageAllowed() + " Archmage(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.soultaker, party) > battle.getType().getSoultakerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getSoultakerAllowed() + " Soultaker(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getSoultakerAllowed() + " Soultaker(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.storm_screamer, party) > battle.getType().getStormScreamerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.mystic_muse, party) > battle.getType().getMysticMouseAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.titan, party) > battle.getType().getTitanAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getTitanAllowed() + " Titan(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getTitanAllowed() + " Titan(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.dominator, party) > battle.getType().getDominatorAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDominatorAllowed() + " Dominator(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDominatorAllowed() + " Dominator(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.doomcryer, party) > battle.getType().getDoomcryerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getArchersCount(party) > battle.getType().getArcherAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getArcherAllowed() + " Archer(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getArcherAllowed() + " Archer(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getTankersCount(party) > battle.getType().getTankerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getTankerAllowed() + " Tanker(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getTankerAllowed() + " Tanker(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getHealersCount(party) > battle.getType().getHealerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getHealerAllowed() + " Healer(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getHealerAllowed() + " Healer(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getDaggersCount(party) > battle.getType().getDaggerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDaggerAllowed() + " Dagger(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDaggerAllowed() + " Dagger(s) are allowed in this mode.");
                  }

                  return false;
               }

               Iterator var5 = party.iterator();

               Player member;
               do {
                  if (!var5.hasNext()) {
                     return true;
                  }

                  member = (Player)var5.next();
                  if (battle.getType().isRestrictIp() && battle.getRestrictIp().contains(member.getIP())) {
                     if (player.isLangRus()) {
                        player.sendMessage("Игрок " + member.getName() + " уже зарегистрирован.");
                     } else {
                        player.sendMessage("Player " + member.getName() + " is already registered.");
                     }

                     return false;
                  }
               } while(!battle.getType().isRestrictHwid() || !battle.getRestrictHwid().contains(member.getNetConnection().getHwid()));

               if (player.isLangRus()) {
                  player.sendMessage("Игрок " + member.getName() + " уже зарегистрирован.");
               } else {
                  player.sendMessage("Player " + member.getName() + " is already registered.");
               }

               return false;
            }

            prohibitedClass = (String)var3.next();
         } while(!commandName.equalsIgnoreCase(prohibitedClass));

         player.sendMessage(player.isLangRus() ? "Введенное имя команды уже занято." : "The command name you entered is already taken.");
         return false;
      }
   }

   public static boolean preCheckSingleAddGroup(Player player, String commandName, BattleGvG battle) {
      if (!battle.isRegistrationActive()) {
         if (player.isLangRus()) {
            player.sendMessage("Регистрация на GvG " + battle.getType().getNameType() + " турнир неактивна.");
         } else {
            player.sendMessage("GvG " + battle.getType().getNameType() + " registration tournament inactive.");
         }

         return false;
      } else if (!BattleUtil.isMatchingRegexp(commandName, battle.getType().getCNameTemplate())) {
         player.sendMessage(player.isLangRus() ? "Недопустимое имя команды." : "Invalid command name.");
         return false;
      } else if (battle.getLeaderList().contains(player.getRef())) {
         if (player.isLangRus()) {
            player.sendMessage("Вы уже зарегистрировались на GvG " + battle.getType().getNameType() + " турнир.");
         } else {
            player.sendMessage("You have already registered for the GvG " + battle.getType().getNameType() + " tournament.");
         }

         return false;
      } else {
         Iterator var3 = battle.getCommandNames().values().iterator();

         String prohibitedClass;
         do {
            if (!var3.hasNext()) {
               if (battle.getType().isToArena() && battle.getPoints().isEmpty()) {
                  player.sendMessage(player.isLangRus() ? "Параметры турнира не позволяют принять участие." : "Tournament parameters do not allow participation.");
                  return false;
               }

               if (player.isInParty()) {
                  player.sendMessage(player.isLangRus() ? "Вы состоите в группе и не можете подать заявку." : "You are in a group and cannot apply.");
                  return false;
               }

               if (battle.getLeaderList().size() >= battle.getType().getCommandsMax()) {
                  player.sendMessage(player.isLangRus() ? "Достигнут лимит количества групп для участия в турнире. Заявка отклонена." : "The limit of the number of groups for participation in the tournament has been reached. Application rejected.");
                  return false;
               }

               List<Player> party = Arrays.asList(player);
               prohibitedClass = BattleUtil.getCheckProhibitedClass(battle.getType().getProhibitedClassIds(), party);
               if (prohibitedClass != null) {
                  if (player.isLangRus()) {
                     player.sendMessage("Класс " + prohibitedClass + " запрещен в этом режиме.");
                  } else {
                     player.sendMessage("The class " + prohibitedClass + " is prohibited in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.dreadnought, party) > battle.getType().getDreadnoughtAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.duelist, party) > battle.getType().getDuelistAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDuelistAllowed() + " Duelist(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDuelistAllowed() + " Duelist(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.archmage, party) > battle.getType().getArchmageAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getArchmageAllowed() + " Archmage(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getArchmageAllowed() + " Archmage(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.soultaker, party) > battle.getType().getSoultakerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getSoultakerAllowed() + " Soultaker(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getSoultakerAllowed() + " Soultaker(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.storm_screamer, party) > battle.getType().getStormScreamerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.mystic_muse, party) > battle.getType().getMysticMouseAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.titan, party) > battle.getType().getTitanAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getTitanAllowed() + " Titan(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getTitanAllowed() + " Titan(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.dominator, party) > battle.getType().getDominatorAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDominatorAllowed() + " Dominator(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDominatorAllowed() + " Dominator(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getCountOfClass(ClassId.doomcryer, party) > battle.getType().getDoomcryerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getArchersCount(party) > battle.getType().getArcherAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getArcherAllowed() + " Archer(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getArcherAllowed() + " Archer(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getTankersCount(party) > battle.getType().getTankerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getTankerAllowed() + " Tanker(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getTankerAllowed() + " Tanker(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getHealersCount(party) > battle.getType().getHealerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getHealerAllowed() + " Healer(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getHealerAllowed() + " Healer(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (BattleUtil.getDaggersCount(party) > battle.getType().getDaggerAllowed()) {
                  if (player.isLangRus()) {
                     player.sendMessage("Только " + battle.getType().getDaggerAllowed() + " Dagger(s) разрешено в этом режиме.");
                  } else {
                     player.sendMessage("Only " + battle.getType().getDaggerAllowed() + " Dagger(s) are allowed in this mode.");
                  }

                  return false;
               }

               if (battle.getType().isRestrictIp() && battle.getRestrictIp().contains(player.getIP())) {
                  if (player.isLangRus()) {
                     player.sendMessage("Вы уже зарегистрированы.");
                  } else {
                     player.sendMessage("You are already registered.");
                  }

                  return false;
               }

               if (battle.getType().isRestrictHwid() && battle.getRestrictHwid().contains(player.getNetConnection().getHwid())) {
                  if (player.isLangRus()) {
                     player.sendMessage("Вы уже зарегистрированы.");
                  } else {
                     player.sendMessage("You are already registered.");
                  }

                  return false;
               }

               return true;
            }

            prohibitedClass = (String)var3.next();
         } while(!commandName.equalsIgnoreCase(prohibitedClass));

         player.sendMessage(player.isLangRus() ? "Введенное имя команды уже занято." : "The command name you entered is already taken.");
         return false;
      }
   }

   public static boolean preCheckValidate(Player player, BattleGvG battle) {
      if (battle.isCCType() && player.getParty().getCommandChannel().getParties().size() > battle.getCCMax()) {
         player.getParty().getCommandChannel().broadCast(new IStaticPacket[]{(new SystemMessage(SystemMsg.S1)).addString("Your group has been removed from the GvG tournament because the Command Channel group limit has been exceeded. Maximum " + battle.getCCMax() + " group" + (battle.getCCMax() < 5 ? "(s)" : "") + ".")});
         return false;
      } else {
         List<Player> party = battle.isCCType() ? player.getParty().getCommandChannel().getMembers() : player.getParty().getPartyMembers();
         String prohibitedClass = BattleUtil.getCheckProhibitedClass(battle.getType().getProhibitedClassIds(), party);
         Iterator var4;
         Player member;
         if (prohibitedClass != null) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку класс " + prohibitedClass + " запрещен в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because the class " + prohibitedClass + " is prohibited in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.dreadnought, party) > battle.getType().getDreadnoughtAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.duelist, party) > battle.getType().getDuelistAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getDuelistAllowed() + " Duelist(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getDuelistAllowed() + " Duelist(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.archmage, party) > battle.getType().getArchmageAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getArchmageAllowed() + " Archmage(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getArchmageAllowed() + " Archmage(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.soultaker, party) > battle.getType().getSoultakerAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getSoultakerAllowed() + " Soultaker(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getSoultakerAllowed() + " Soultaker(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.storm_screamer, party) > battle.getType().getStormScreamerAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.mystic_muse, party) > battle.getType().getMysticMouseAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.titan, party) > battle.getType().getTitanAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getTitanAllowed() + " Titan(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getTitanAllowed() + " Titan(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.dominator, party) > battle.getType().getDominatorAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getDominatorAllowed() + " Dominator(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getDominatorAllowed() + " Dominator(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.doomcryer, party) > battle.getType().getDoomcryerAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getArchersCount(party) > battle.getType().getArcherAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getArcherAllowed() + " Archer(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getArcherAllowed() + " Archer(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getTankersCount(party) > battle.getType().getTankerAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getTankerAllowed() + " Tanker(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getTankerAllowed() + " Tanker(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getHealersCount(party) > battle.getType().getHealerAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getHealerAllowed() + " Healer(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getHealerAllowed() + " Healer(s) are allowed in this mode.");
               }
            }

            return false;
         } else if (BattleUtil.getDaggersCount(party) > battle.getType().getDaggerAllowed()) {
            var4 = party.iterator();

            while(var4.hasNext()) {
               member = (Player)var4.next();
               if (member.isLangRus()) {
                  member.sendMessage("Ваша группа удалена с турнира, поскольку только " + battle.getType().getDaggerAllowed() + " Dagger(s) разрешено в этом режиме.");
               } else {
                  member.sendMessage("Your group has been removed from the tournament because only " + battle.getType().getDaggerAllowed() + " Dagger(s) are allowed in this mode.");
               }
            }

            return false;
         } else {
            return true;
         }
      }
   }

   public static boolean preCheckSingleValidate(Player player, BattleGvG battle) {
      List<Player> party = Arrays.asList(player);
      String prohibitedClass = BattleUtil.getCheckProhibitedClass(battle.getType().getProhibitedClassIds(), party);
      if (prohibitedClass != null) {
         if (player.isLangRus()) {
            player.sendMessage("Класс " + prohibitedClass + " запрещен в этом режиме.");
         } else {
            player.sendMessage("The class " + prohibitedClass + " is prohibited in this mode.");
         }

         return false;
      } else if (BattleUtil.getCountOfClass(ClassId.dreadnought, party) > battle.getType().getDreadnoughtAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getCountOfClass(ClassId.duelist, party) > battle.getType().getDuelistAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getDuelistAllowed() + " Duelist(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getDuelistAllowed() + " Duelist(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getCountOfClass(ClassId.archmage, party) > battle.getType().getArchmageAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getArchmageAllowed() + " Archmage(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getArchmageAllowed() + " Archmage(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getCountOfClass(ClassId.soultaker, party) > battle.getType().getSoultakerAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getSoultakerAllowed() + " Soultaker(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getSoultakerAllowed() + " Soultaker(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getCountOfClass(ClassId.storm_screamer, party) > battle.getType().getStormScreamerAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getCountOfClass(ClassId.mystic_muse, party) > battle.getType().getMysticMouseAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getCountOfClass(ClassId.titan, party) > battle.getType().getTitanAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getTitanAllowed() + " Titan(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getTitanAllowed() + " Titan(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getCountOfClass(ClassId.dominator, party) > battle.getType().getDominatorAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getDominatorAllowed() + " Dominator(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getDominatorAllowed() + " Dominator(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getCountOfClass(ClassId.doomcryer, party) > battle.getType().getDoomcryerAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getArchersCount(party) > battle.getType().getArcherAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getArcherAllowed() + " Archer(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getArcherAllowed() + " Archer(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getTankersCount(party) > battle.getType().getTankerAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getTankerAllowed() + " Tanker(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getTankerAllowed() + " Tanker(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getHealersCount(party) > battle.getType().getHealerAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getHealerAllowed() + " Healer(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getHealerAllowed() + " Healer(s) are allowed in this mode.");
         }

         return false;
      } else if (BattleUtil.getDaggersCount(party) > battle.getType().getDaggerAllowed()) {
         if (player.isLangRus()) {
            player.sendMessage("Только " + battle.getType().getDaggerAllowed() + " Dagger(s) разрешено в этом режиме.");
         } else {
            player.sendMessage("Only " + battle.getType().getDaggerAllowed() + " Dagger(s) are allowed in this mode.");
         }

         return false;
      } else {
         return true;
      }
   }

   public static boolean preCheckAddReg(Player player, Player partic, String team, BattleGvG battle) {
      if (!player.getPlayerAccess().IsEventGm) {
         return false;
      } else if (!BattleUtil.isMatchingRegexp(team, battle.getType().getCNameTemplate())) {
         player.sendMessage(player.isLangRus() ? "Введено недопустимое имя команды." : "Invalid command name entered.");
         return false;
      } else if (!battle.isActive()) {
         if (player.isLangRus()) {
            player.sendMessage("GvG " + battle.getType().getNameType() + " не запущен.");
         } else {
            player.sendMessage("GvG " + battle.getType().getNameType() + " не запущен.");
         }

         return false;
      } else if (battle.getType().isToArena() && battle.getPoints().isEmpty()) {
         player.sendMessage(player.isLangRus() ? "Недостаточно точек для рассадки участников за ареной." : "Not enough seating points outside the arena.");
         return false;
      } else if (!partic.isInParty()) {
         if (player.isLangRus()) {
            player.sendMessage(partic.getName() + " не в пати.");
         } else {
            player.sendMessage(partic.getName() + " is not in the party.");
         }

         return false;
      } else if (battle.isCCType() && !partic.getParty().isInCommandChannel()) {
         if (player.isLangRus()) {
            player.sendMessage(partic.getName() + " не в Командном Канале.");
         } else {
            player.sendMessage(partic.getName() + " is not in the Command Channel.");
         }

         return false;
      } else {
         Player leader = battle.isCCType() ? partic.getParty().getCommandChannel().getChannelLeader() : partic.getParty().getPartyLeader();
         if (leader == null) {
            player.sendMessage(player.isLangRus() ? "Лидер не найден." : "Leader not found.");
            return false;
         } else if (battle.getLeaderList().contains(leader.getRef())) {
            if (player.isLangRus()) {
               player.sendMessage(leader.getName() + " уже зарегистрировал свою команду.");
            } else {
               player.sendMessage(leader.getName() + " has already registered its team.");
            }

            return false;
         } else if (battle.isCCType() && leader.getParty().getCommandChannel().getParties().size() > battle.getCCMax()) {
            if (player.isLangRus()) {
               player.sendMessage("Превышен лимит групп Командного Канала. Максимум " + battle.getCCMax() + " групп" + (battle.getCCMax() < 5 ? "ы" : "") + ".");
            } else {
               player.sendMessage("Command channel group limit exceeded. Maximum " + battle.getCCMax() + " group" + (battle.getCCMax() < 5 ? "s" : "") + ".");
            }

            return false;
         } else {
            List<Player> party = battle.isCCType() ? leader.getParty().getCommandChannel().getMembers() : leader.getParty().getPartyMembers();
            String prohibitedClass = BattleUtil.getCheckProhibitedClass(battle.getType().getProhibitedClassIds(), party);
            if (prohibitedClass != null) {
               if (player.isLangRus()) {
                  player.sendMessage("Класс " + prohibitedClass + " запрещен в этом режиме.");
               } else {
                  player.sendMessage("The class " + prohibitedClass + " is prohibited in this mode.");
               }

               return false;
            } else if (BattleUtil.getCountOfClass(ClassId.dreadnought, party) > battle.getType().getDreadnoughtAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getCountOfClass(ClassId.duelist, party) > battle.getType().getDuelistAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getDuelistAllowed() + " Duelist(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getDuelistAllowed() + " Duelist(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getCountOfClass(ClassId.archmage, party) > battle.getType().getArchmageAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getArchmageAllowed() + " Archmage(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getArchmageAllowed() + " Archmage(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getCountOfClass(ClassId.soultaker, party) > battle.getType().getSoultakerAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getSoultakerAllowed() + " Soultaker(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getSoultakerAllowed() + " Soultaker(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getCountOfClass(ClassId.storm_screamer, party) > battle.getType().getStormScreamerAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getCountOfClass(ClassId.mystic_muse, party) > battle.getType().getMysticMouseAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getCountOfClass(ClassId.titan, party) > battle.getType().getTitanAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getTitanAllowed() + " Titan(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getTitanAllowed() + " Titan(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getCountOfClass(ClassId.dominator, party) > battle.getType().getDominatorAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getDominatorAllowed() + " Dominator(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getDominatorAllowed() + " Dominator(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getCountOfClass(ClassId.doomcryer, party) > battle.getType().getDoomcryerAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getArchersCount(party) > battle.getType().getArcherAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getArcherAllowed() + " Archer(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getArcherAllowed() + " Archer(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getTankersCount(party) > battle.getType().getTankerAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getTankerAllowed() + " Tanker(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getTankerAllowed() + " Tanker(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getHealersCount(party) > battle.getType().getHealerAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getHealerAllowed() + " Healer(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getHealerAllowed() + " Healer(s) are allowed in this mode.");
               }

               return false;
            } else if (BattleUtil.getDaggersCount(party) > battle.getType().getDaggerAllowed()) {
               if (player.isLangRus()) {
                  player.sendMessage("Только " + battle.getType().getDaggerAllowed() + " Dagger(s) разрешено в этом режиме.");
               } else {
                  player.sendMessage("Only " + battle.getType().getDaggerAllowed() + " Dagger(s) are allowed in this mode.");
               }

               return false;
            } else {
               Iterator var7 = party.iterator();

               Player member;
               do {
                  if (!var7.hasNext()) {
                     return true;
                  }

                  member = (Player)var7.next();
                  if (battle.getType().isRestrictIp() && battle.getRestrictIp().contains(member.getIP())) {
                     if (player.isLangRus()) {
                        player.sendMessage("Игрок " + member.getName() + " уже зарегистрирован.");
                     } else {
                        player.sendMessage("Player " + member.getName() + " is already registered.");
                     }

                     return false;
                  }
               } while(!battle.getType().isRestrictHwid() || !battle.getRestrictHwid().contains(member.getNetConnection().getHwid()));

               if (player.isLangRus()) {
                  player.sendMessage("Игрок " + member.getName() + " уже зарегистрирован.");
               } else {
                  player.sendMessage("Player " + member.getName() + " is already registered.");
               }

               return false;
            }
         }
      }
   }

   public static boolean preCheckSingleAddReg(Player admin, Player leader, String team, BattleGvG battle) {
      if (!admin.getPlayerAccess().IsEventGm) {
         return false;
      } else if (!BattleUtil.isMatchingRegexp(team, battle.getType().getCNameTemplate())) {
         admin.sendMessage(admin.isLangRus() ? "Введено недопустимое имя команды." : "An invalid command name was entered.");
         return false;
      } else if (!battle.isActive()) {
         if (admin.isLangRus()) {
            admin.sendMessage("GvG " + battle.getType().getNameType() + " не запущен.");
         } else {
            admin.sendMessage("GvG " + battle.getType().getNameType() + " is not running.");
         }

         return false;
      } else if (battle.getType().isToArena() && battle.getPoints().isEmpty()) {
         admin.sendMessage(admin.isLangRus() ? "Недостаточно точек для рассадки участников за ареной." : "Not enough seating points outside the arena.");
         return false;
      } else if (leader.isInParty()) {
         if (admin.isLangRus()) {
            admin.sendMessage(leader.getName() + " не в пати.");
         } else {
            admin.sendMessage(leader.getName() + " is not in the party.");
         }

         return false;
      } else if (battle.getLeaderList().contains(leader.getRef())) {
         if (admin.isLangRus()) {
            admin.sendMessage(leader.getName() + " уже зарегистрировал свою команду.");
         } else {
            admin.sendMessage(leader.getName() + " has already registered its team.");
         }

         return false;
      } else {
         List<Player> party = Arrays.asList(leader);
         String prohibitedClass = BattleUtil.getCheckProhibitedClass(battle.getType().getProhibitedClassIds(), party);
         if (prohibitedClass != null) {
            if (admin.isLangRus()) {
               admin.sendMessage("Класс " + prohibitedClass + " запрещен в этом режиме.");
            } else {
               admin.sendMessage("The class " + prohibitedClass + " is prohibited in this mode.");
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.dreadnought, party) > battle.getType().getDreadnoughtAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getDreadnoughtAllowed() + " Dreadnought(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.duelist, party) > battle.getType().getDuelistAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getDuelistAllowed() + " Duelist(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getDuelistAllowed() + " Duelist(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.archmage, party) > battle.getType().getArchmageAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getArchmageAllowed() + " Archmage(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getArchmageAllowed() + " Archmage(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.soultaker, party) > battle.getType().getSoultakerAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getSoultakerAllowed() + " Soultaker(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getSoultakerAllowed() + " Soultaker(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.storm_screamer, party) > battle.getType().getStormScreamerAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getStormScreamerAllowed() + " Storm Screamer(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.mystic_muse, party) > battle.getType().getMysticMouseAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getMysticMouseAllowed() + " Mystic Muse(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.titan, party) > battle.getType().getTitanAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getTitanAllowed() + " Titan(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getTitanAllowed() + " Titan(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.dominator, party) > battle.getType().getDominatorAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getDominatorAllowed() + " Dominator(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getDominatorAllowed() + " Dominator(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getCountOfClass(ClassId.doomcryer, party) > battle.getType().getDoomcryerAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getDoomcryerAllowed() + " Doomcryer(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getArchersCount(party) > battle.getType().getArcherAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getArcherAllowed() + " Archer(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getArcherAllowed() + " Archer(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getTankersCount(party) > battle.getType().getTankerAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getTankerAllowed() + " Tanker(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getTankerAllowed() + " Tanker(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getHealersCount(party) > battle.getType().getHealerAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getHealerAllowed() + " Healer(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getHealerAllowed() + " Healer(s) are allowed in this mode.");
            }

            return false;
         } else if (BattleUtil.getDaggersCount(party) > battle.getType().getDaggerAllowed()) {
            if (admin.isLangRus()) {
               admin.sendMessage("Только " + battle.getType().getDaggerAllowed() + " Dagger(s) разрешено в этом режиме.");
            } else {
               admin.sendMessage("Only " + battle.getType().getDaggerAllowed() + " Dagger(s) are allowed in this mode.");
            }

            return false;
         } else if (battle.getType().isRestrictIp() && battle.getRestrictIp().contains(leader.getIP())) {
            if (admin.isLangRus()) {
               admin.sendMessage(leader.getName() + " уже зарегистрирован.");
            } else {
               admin.sendMessage(leader.getName() + " already registered.");
            }

            return false;
         } else if (battle.getType().isRestrictHwid() && battle.getRestrictHwid().contains(leader.getNetConnection().getHwid())) {
            if (admin.isLangRus()) {
               admin.sendMessage(leader.getName() + " уже зарегистрирован.");
            } else {
               admin.sendMessage(leader.getName() + " already registered.");
            }

            return false;
         } else {
            return true;
         }
      }
   }

   public static String validatePlayerForBattle(BattleGvG battle, Player player, boolean lead, boolean single) {
      if (player == null) {
         return "noplayer";
      } else {
         if (single) {
            if (player.isInParty()) {
               return "inparty";
            }
         } else {
            if (!player.isInParty()) {
               return "noparty";
            }

            if (lead) {
               label115: {
                  if (battle.isCCType()) {
                     if (player.getParty().isInCommandChannel() && player.getParty().getCommandChannel().getChannelLeader() == player) {
                        break label115;
                     }
                  } else if (player.getParty().isLeader(player)) {
                     break label115;
                  }

                  return "noleader";
               }

               if (battle.isCCType()) {
                  Iterator var4 = player.getParty().getCommandChannel().getParties().iterator();

                  while(var4.hasNext()) {
                     Party party = (Party)var4.next();
                     int memberCount = party.getMemberCount();
                     if (memberCount < battle.getType().getMembers()) {
                        return "minmembers";
                     }

                     if (memberCount > battle.getType().getMembers()) {
                        return "maxmembers";
                     }
                  }
               } else {
                  int memberCount = player.getParty().getMemberCount();
                  if (memberCount < battle.getType().getMembers()) {
                     return "minmembers";
                  }

                  if (memberCount > battle.getType().getMembers()) {
                     return "maxmembers";
                  }
               }
            }
         }

         if (player.getLevel() >= battle.getType().getMinLevel() && player.getLevel() <= battle.getType().getMaxLevel()) {
            if (player.isMounted()) {
               return "mount";
            } else if (player.isInDuel()) {
               return "duel";
            } else if (player.getTeam() != TeamType.NONE) {
               return "event";
            } else if (!player.isOlyParticipant() && !ParticipantPool.getInstance().isRegistred(player)) {
               if (player.isTeleporting()) {
                  return "teleport";
               } else if (!single && player.getParty().isInDimensionalRift()) {
                  return "rift";
               } else if (player.isCursedWeaponEquipped()) {
                  return "cursed";
               } else if (battle.getType().isInPeace() && !player.isInZonePeace()) {
                  return "nopeace";
               } else if (player.isInObserverMode() && !player.isOlyParticipant()) {
                  return "observer";
               } else if (player.getReflectionId() != 0) {
                  return "instance";
               } else {
                  return player.getVar("jailed") != null ? "jail" : null;
               }
            } else {
               return "olympiad";
            }
         } else {
            return "levels";
         }
      }
   }
}
