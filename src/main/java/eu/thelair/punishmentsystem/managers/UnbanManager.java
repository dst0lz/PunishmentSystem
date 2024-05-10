package eu.thelair.punishmentsystem.managers;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.cache.BanCache;
import eu.thelair.punishmentsystem.cache.MuteCache;
import eu.thelair.punishmentsystem.database.MySQL;
import eu.thelair.punishmentsystem.entites.Ban;
import eu.thelair.punishmentsystem.entites.Template;
import eu.thelair.punishmentsystem.entites.User;
import eu.thelair.punishmentsystem.entites.enums.TemplateType;
import eu.thelair.punishmentsystem.utils.HashGenerator;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class UnbanManager {

  /**
   * Checks a ban if it's duration is exceeded or not
   *
   * @param ban the ban to check
   * @return true if the ban is permanent or not exceeded
   */
  public boolean checkBanExceeded(Ban ban) {
    if (ban.getTemplate().isPermanent()) {
      return false;
    }
    return !(ban.getTo() != null && new Timestamp(System.currentTimeMillis()).before(ban.getTo()));
  }

  public void unban(User userBanned, User userBanner, Template template, String addendum) {
    String symbol = HashGenerator.getRandomId(5);

    if (template.isPermanent()) {
      unbanCompletely(symbol, userBanned, userBanner, template, addendum);
      return;
    }

    long time = template.getTimes()[0];
    Timestamp from = new Timestamp(System.currentTimeMillis());
    Timestamp to = new Timestamp(System.currentTimeMillis() + Math.abs(time));

    Ban ban = new Ban(symbol, userBanned, userBanner, from, to, template, addendum);
    if (template.getTemplateType() == TemplateType.MUTE || template.getTemplateType() == TemplateType.UNMUTE) {
      muteCache().add(ban);
    } else {
      banCache().add(ban);
    }

    sql().update("INSERT INTO ban(symbol, banned, banner, since, until, template, addendum) VALUES (?, ?, ?, ?, ?, ?, ?)",
            symbol,
            userBanned.getUuid(),
            userBanner.getUuid(),
            from, to,
            template.getId(), addendum
    );

    PunishmentSystem.getInstance().getLogManager().createLog(ban, time);
    sendNotify(ban, false);
    ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(userBanned.getUuid()));
    if (p != null && template.getTemplateType() != TemplateType.UNMUTE && template.getTemplateType() != TemplateType.MUTE)
      p.disconnect(PunishmentSystem.getInstance().getBanManager().getKickMessage(ban));
  }

  private void unbanCompletely(String symbol, User userBanned, User userBanner, Template template, String addendum) {
    Timestamp from = new Timestamp(System.currentTimeMillis());
    Timestamp to = new Timestamp(System.currentTimeMillis());
    Ban ban = new Ban(symbol, userBanned, userBanner, from, to, template, addendum);
    deleteBan(ban);
    PunishmentSystem.getInstance().getLogManager().createLog(ban, -1);
    sendNotify(ban, true);
  }

  private void sendNotify(Ban ban, boolean unban) {
    String targetColor = PunishmentSystem.getInstance().getPermissionManager().getColorCode(UUID.fromString(ban.getBanned().getUuid()));
    String color = PunishmentSystem.getInstance().getPermissionManager().getColorCode(UUID.fromString(ban.getBanner().getUuid()));
    if (ban.getBanner().getLastName().equals("Medusa"))
      color = "§4";

    String time;
    if (ban.getTemplate().isPermanent() || unban) {
      time = "permanent";
    } else {
      time = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(ban.getTo()) + " Uhr";
    }

    String finalColor = color;
    String finalTime = time;
    if (unban) {
      String unbanMsg;
      if (ban.getTemplate().getTemplateType() == TemplateType.UNMUTE) {
        unbanMsg = "§7Der Spieler " + targetColor + ban.getBanned().getLastName() + " §7wurde von " + finalColor + ban.getBanner().getLastName() + " §7entmuted";
      } else {
        unbanMsg = "§7Der Spieler " + targetColor + ban.getBanned().getLastName() + " §7wurde von " + finalColor + ban.getBanner().getLastName() + " §7entbannt";
      }

      ProxyServer.getInstance().getPlayers()
              .stream()
              .filter(p -> p.hasPermission("punishment.notify.unban"))
              .forEach(all -> {
                all.sendMessage(PunishmentSystem.PREFIX + unbanMsg);
                all.sendMessage(PunishmentSystem.PREFIX + "§7Grund: §c" + ban.getTemplate().getReason() + " §7[§c#" + ban.getSymbol() + "§7] " + ban.getAddendum());
              });
    } else {
      String unbanMsg;
      if (ban.getTemplate().getTemplateType() == TemplateType.UNMUTE) {
        unbanMsg = "§7Der Spieler " + targetColor + ban.getBanned().getLastName() + " §7wurde von " + finalColor + ban.getBanner().getLastName() + " §7gemuted";
      } else {
        unbanMsg = "§7Der Spieler " + targetColor + ban.getBanned().getLastName() + " §7wurde von " + finalColor + ban.getBanner().getLastName() + " §7gebannt";
      }

      ProxyServer.getInstance().getPlayers()
              .stream()
              .filter(p -> p.hasPermission("punishment.notify.ban"))
              .forEach(all -> {
                all.sendMessage(PunishmentSystem.PREFIX + unbanMsg);
                all.sendMessage(PunishmentSystem.PREFIX + "§7Grund: §c" + ban.getTemplate().getReason() + " §7bis §e" + finalTime + " §7[§c#" + ban.getSymbol() + "§7] " + ban.getAddendum());
              });
    }
  }

  public boolean isUserBanned(String uuid) {
    if (banCache().containsKey(uuid)) {
      return !(checkBanExceeded(banCache().get(uuid)));
    }
    return false;
  }

  public boolean isUserMuted(String uuid) {
    if (muteCache().containsKey(uuid)) {
      return !(checkBanExceeded(muteCache().get(uuid)));
    }
    return false;
  }

  public void deleteBan(Ban ban) {
    sql().update("DELETE FROM ban WHERE symbol=?", ban.getSymbol());
    banCache().remove(ban);
    muteCache().remove(ban);
  }

  private MySQL sql() {
    return PunishmentSystem.getInstance().getMySQL();
  }

  private BanCache banCache() {
    return PunishmentSystem.getInstance().getBanCache();
  }

  private MuteCache muteCache() {
    return PunishmentSystem.getInstance().getMuteCache();
  }

}
