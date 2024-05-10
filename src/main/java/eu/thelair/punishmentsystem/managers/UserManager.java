package eu.thelair.punishmentsystem.managers;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.cache.UserCache;
import eu.thelair.punishmentsystem.database.MySQL;
import eu.thelair.punishmentsystem.entites.User;
import eu.thelair.punishmentsystem.utils.UUIDFetcher;

import java.util.UUID;

public class UserManager {

  public void initUser(String uuid, String name, String ip) {
    if (cache().containsKey(uuid))
      return;
    User user = new User(uuid, name, ip);
    cache().add(user);
    sql().update("INSERT INTO user (uuid, last_name, last_ip) VALUES (?,?,?)", uuid, name, ip);
  }

  public void updateUser(String uuid, String name, String ip) {
    sql().update("UPDATE user SET last_ip=?, last_name=? WHERE uuid=?", ip, name, uuid);
  }

  public User findByUuid(String uuid) {
    if (cache().containsKey(uuid))
      return cache().get(uuid);
    String name = UUIDFetcher.getName(UUID.fromString(uuid));
    User user = new User(uuid, name, "");
    cache().add(user);
    sql().update("INSERT INTO user (uuid, last_name) VALUES (?, ?)", uuid, name);
    return user;
  }

  public User findByName(String name) {
    return cache().getMap().values().stream().filter(user -> user.getLastName().equalsIgnoreCase(name)).findFirst().orElse(null);
  }

  private MySQL sql() {
    return PunishmentSystem.getInstance().getMySQL();
  }

  private UserCache cache() {
    return PunishmentSystem.getInstance().getUserCache();
  }

}
