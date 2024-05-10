package eu.thelair.punishmentsystem.managers;

import com.google.common.collect.Lists;
import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.database.MySQL;
import eu.thelair.punishmentsystem.entites.Ban;
import eu.thelair.punishmentsystem.entites.LogEntry;
import eu.thelair.punishmentsystem.entites.Template;
import eu.thelair.punishmentsystem.utils.HashGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class LogManager {

  public void createLog(Ban ban, long duration) {
    sql().update("INSERT INTO log_entry (banSymbol, template_id, since, bannerName, logUserUUID, duration, addendum) VALUES (?, ?, ?, ?, ?, ?, ?)",
            ban.getSymbol(),
            ban.getTemplate().getId(),
            ban.getFrom(),
            ban.getBanner().getLastName(),
            ban.getBanned().getUuid(),
            duration,
            ban.getAddendum()
    );
  }

  public void createKickLog(String kicker, String targetUUID, String addendum) {
    sql().update("INSERT INTO log_entry (banSymbol, template_id, since, bannerName, logUserUUID, duration, addendum) VALUES (?, ?, ?, ?, ?, ?, ?)",
            HashGenerator.getRandomId(5),
            0,
            new Timestamp(System.currentTimeMillis()),
            kicker,
            targetUUID,
            -1,
            addendum
    );
  }

  public int getLogCountByUserAndTemplate(String logUserUUID, Template template) {
    String qry = "SELECT count(*) as count FROM log_entry WHERE logUserUUID=? AND template_id=?";
    try (ResultSet rs = sql().query(qry, logUserUUID, template.getId())) {
      if (rs.next()) {
        return rs.getInt("count");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }

  public List<LogEntry> getLogByUser(String uuid) {
    List<LogEntry> logEntries = Lists.newArrayList();

    String qry = "SELECT * FROM log_entry WHERE logUserUUID=?";
    try (ResultSet rs = sql().query(qry, uuid)) {
      while (rs.next()) {
        long logId = rs.getLong("log_id");
        String banSymbol = rs.getString("banSymbol");
        Template template = PunishmentSystem.getInstance().getTemplateManager().findById(rs.getInt("template_id"));
        if (template == null) {
          deleteLog(logId);
          continue;
        }
        Timestamp since = rs.getTimestamp("since");
        String bannerName = rs.getString("bannerName");
        String logUserUUID = rs.getString("logUserUUID");
        Long duration = rs.getLong("duration");
        String addendum = rs.getString("addendum");
        logEntries.add(new LogEntry(banSymbol, template, since, bannerName, logUserUUID, duration, addendum));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return logEntries;
  }

  public void deleteLog(long logId) {
    String update = "DELETE FROM log_entry WHERE log_id=?";
    sql().update(update, logId);
  }


  private MySQL sql() {
    return PunishmentSystem.getInstance().getMySQL();
  }


}
