package eu.thelair.punishmentsystem.cache;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.database.MySQL;
import eu.thelair.punishmentsystem.entites.User;
import net.md_5.bungee.api.ProxyServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserCache implements Cache<User> {
  private final Map<String, User> users = new ConcurrentHashMap<>();

  public UserCache() {
    loadUsers();
  }

  private void loadUsers() {
    String qry = "SELECT * FROM user";
    try (ResultSet rs = sql().query(qry)) {
      while (rs.next()) {
        String uuid = rs.getString("uuid");
        String lastName = rs.getString("last_name");
        String lastIp = rs.getString("last_ip");
        User user = new User(uuid, lastName, lastIp);
        users.put(uuid, user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Optional<Integer> gteSize() {
    return Optional.ofNullable(ProxyServer.getInstance().getOnlineCount());
  }

  @Override
  public void add(User user) {
    users.put(user.getUuid(), user);
  }

  @Override
  public void remove(User user) {
    users.remove(user.getUuid());
  }

  @Override
  public boolean contains(User user) {
    return users.containsValue(user);
  }

  @Override
  public boolean containsKey(String key) {
    return users.containsKey(key);
  }

  @Override
  public Map<String, User> getMap() {
    return users;
  }

  @Override
  public User get(String key) {
    return users.get(key);
  }

  private MySQL sql() {
    return PunishmentSystem.getInstance().getMySQL();
  }

}
