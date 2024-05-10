package eu.thelair.punishmentsystem.cache;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.database.MySQL;
import eu.thelair.punishmentsystem.entites.Template;
import eu.thelair.punishmentsystem.entites.enums.TemplateType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class TemplateCache implements Cache<Template> {
  private final Map<String, Template> templates = new ConcurrentHashMap<>();

  public TemplateCache() {
    loadTemplates();
  }

  public void loadTemplates() {
    try (ResultSet rs = sql().query("SELECT * FROM template ORDER BY template_id ASC")) {
      while (rs.next()) {
        long id = rs.getLong("template_id");
        String abbreviation = rs.getString("abbreviation");
        String reason = rs.getString("reason");
        boolean permanent = rs.getBoolean("permanent");
        Long[] times = null;
        if (!permanent) {
          String[] timesString = rs.getString("times").split(", ");
          times = Stream.of(timesString).map(Long::valueOf).toArray(Long[]::new);
        }

        TemplateType type = TemplateType.valueOf(rs.getString("type"));
        Template template = new Template(id, abbreviation, reason, times, type, permanent);
        templates.put(template.getAbbreviation(), template);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void add(Template template) {
    templates.put(template.getAbbreviation(), template);
  }

  @Override
  public void remove(Template template) {
    templates.remove(template.getAbbreviation());
  }

  @Override
  public boolean contains(Template template) {
    return templates.containsValue(template);
  }

  @Override
  public boolean containsKey(String key) {
    return templates.containsKey(key);
  }

  @Override
  public Map<String, Template> getMap() {
    return this.templates;
  }

  @Override
  public Template get(String key) {
    return templates.get(key);
  }

  private MySQL sql() {
    return PunishmentSystem.getInstance().getMySQL();
  }


}
