package eu.thelair.punishmentsystem.managers;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.cache.TemplateCache;
import eu.thelair.punishmentsystem.database.MySQL;
import eu.thelair.punishmentsystem.entites.Template;

public class TemplateManager {

  public Template findTemplateByAbbreviation(String name) {
    for (String templateName : cache().getMap().keySet()) {
      if (templateName.equalsIgnoreCase(name))
        return cache().get(templateName);
    }
    return null;
  }

  public Template findById(long id) {
    return cache().getMap().values().stream().filter(template -> (template.getId() == id)).findFirst().orElse(null);
  }

  private MySQL sql() {
    return PunishmentSystem.getInstance().getMySQL();
  }

  private TemplateCache cache() {
    return PunishmentSystem.getInstance().getTemplateCache();
  }

}
