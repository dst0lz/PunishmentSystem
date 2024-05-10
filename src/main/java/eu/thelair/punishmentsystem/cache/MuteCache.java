package eu.thelair.punishmentsystem.cache;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.database.MySQL;
import eu.thelair.punishmentsystem.entites.Ban;
import eu.thelair.punishmentsystem.entites.Template;
import eu.thelair.punishmentsystem.entites.User;
import eu.thelair.punishmentsystem.entites.enums.TemplateType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MuteCache implements Cache<Ban> {
  private final Map<String, Ban> mutes = new ConcurrentHashMap<>();

  public MuteCache() {
    loadMutes();
  }

  private void loadMutes() {
    String qry = "SELECT * FROM ban b JOIN template t ON b.template = t.template_id WHERE t.type = ? OR t.type = ?";
    try (ResultSet rs = sql().query(qry, TemplateType.MUTE.name(), TemplateType.UNMUTE.name())) {
      while (rs.next()) {
        long id = rs.getLong("b.ban_id");
        String symbol = rs.getString("b.symbol");
        User banned = PunishmentSystem.getInstance().getUserManager().findByUuid(rs.getString("b.banned"));
        User banner = PunishmentSystem.getInstance().getUserManager().findByUuid(rs.getString("b.banner"));
        Timestamp since = rs.getTimestamp("b.since");
        Timestamp until = rs.getTimestamp("b.until");
        Template template = PunishmentSystem.getInstance().getTemplateManager().findById(rs.getInt("b.template"));
        String addendum = rs.getString("b.addendum");

        Ban ban = new Ban(symbol, banned, banner, since, until, template, addendum);
        ban.setId(id);
        add(ban);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private MySQL sql() {
    return PunishmentSystem.getInstance().getMySQL();
  }

  @Override
  public void add(Ban ban) {
    mutes.put(ban.getBanned().getUuid(), ban);
  }

  @Override
  public void remove(Ban ban) {
    mutes.remove(ban.getBanned().getUuid());
  }

  @Override
  public boolean contains(Ban ban) {
    return mutes.containsValue(ban);
  }

  @Override
  public boolean containsKey(String key) {
    return mutes.containsKey(key);
  }

  @Override
  public Map<String, Ban> getMap() {
    return mutes;
  }

  @Override
  public Ban get(String key) {
    return mutes.get(key);
  }

  public Optional<Ban> findBySymbol(String symbol) {
    return getMap().values().stream().filter(mute -> mute.getSymbol().equals(symbol)).findFirst();
  }
}
