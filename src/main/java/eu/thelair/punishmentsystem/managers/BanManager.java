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
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class BanManager {

  public void ban(User userBanned, User userBanner, Template template, String addendum) {
    String symbol = HashGenerator.getRandomId(5);
    int numberBans = PunishmentSystem.getInstance().getLogManager().getLogCountByUserAndTemplate(userBanned.getUuid(), template);
    boolean permanent = false;
    long time = -1L;
    if ((template.getTimes() != null && numberBans >= template.getTimes().length) || template.isPermanent()) {
      permanent = true;
    } else {
      time = template.getTimes()[numberBans];
    }
    template.setPermanent(permanent);
    Timestamp from = new Timestamp(System.currentTimeMillis());
    Timestamp to = null;
    long duration = 0;
    if (!permanent) {
      to = new Timestamp(System.currentTimeMillis() + time);
      duration = time;
    }
    Ban ban = new Ban(symbol, userBanned, userBanner, from, to, template, addendum);
    if (template.getTemplateType() == TemplateType.MUTE || template.getTemplateType() == TemplateType.UNMUTE) {
      muteCache().add(ban);
    } else {
      banCache().add(ban);
    }

    if (!permanent) {
      sql().update("INSERT INTO ban(symbol, banned, banner, since, until, template, addendum) VALUES (?, ?, ?, ?, ?, ?, ?)",
              symbol,
              userBanned.getUuid(),
              userBanner.getUuid(),
              from, to,
              template.getId(), addendum
      );
    } else {
      sql().update("INSERT INTO ban(symbol, banned, banner, since, template, addendum) VALUES (?, ?, ?, ?, ?, ?)",
              symbol,
              userBanned.getUuid(),
              userBanner.getUuid(),
              from,
              template.getId(),
              addendum
      );
    }

    PunishmentSystem.getInstance().getLogManager().createLog(ban, duration);
    sendNotify(ban);
    ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(userBanned.getUuid()));
    if (p != null) {
      if (template.getTemplateType() == TemplateType.BAN) {
        p.disconnect(getKickMessage(ban));
      } else if (template.getTemplateType() == TemplateType.MUTE) {
        String displayTime;
        if (ban.getTemplate().isPermanent()) {
          displayTime = "permanent";
        } else {
          displayTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(ban.getTo()) + " Uhr";
        }
        p.sendMessage(PunishmentSystem.THELAIR_PREFIX + "§7Du wurdest aus dem Chat §cgebannt");
        p.sendMessage(PunishmentSystem.THELAIR_PREFIX + "§7Grund: §c" + ban.getTemplate().getReason() + " §7bis §e" + displayTime + " §7[§c#" + ban.getSymbol() + "§7]");
        p.sendMessage(PunishmentSystem.THELAIR_PREFIX + "§7Du möchtest deine Strafe verkürzen / aufheben? §8➡ §ehttps://thelair.eu/goto/ea");
      }
    }
  }

  private void sendNotify(Ban ban) {
    String targetColor = PunishmentSystem.getInstance().getPermissionManager().getColorCode(UUID.fromString(ban.getBanned().getUuid()));
    String color = PunishmentSystem.getInstance().getPermissionManager().getColorCode(UUID.fromString(ban.getBanner().getUuid()));
    if (ban.getBanner().getLastName().equals("Medusa") || ban.getBanner().getLastName().equals("Neptun"))
      color = "§4";

    String time;
    if (ban.getTemplate().isPermanent()) {
      time = "permanent";
    } else {
      time = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(ban.getTo()) + " Uhr";
    }

    String type = "gebannt";
    if (ban.getTemplate().getTemplateType() == TemplateType.MUTE || ban.getTemplate().getTemplateType() == TemplateType.UNBAN) {
      type = "gemutet";
    }

    String finalColor = color;
    String finalTime = time;
    String finalType = type;
    ProxyServer.getInstance().getPlayers()
            .stream()
            .filter(p -> p.hasPermission("punishment.notify.ban"))
            .forEach(all -> {
              all.sendMessage(PunishmentSystem.PREFIX + "§7Der Spieler " + targetColor + ban.getBanned().getLastName() + " §7wurde von " + finalColor + ban.getBanner().getLastName() + " §7" + finalType);
              all.sendMessage(PunishmentSystem.PREFIX + "§7Grund: §c" + ban.getTemplate().getReason() + " §7bis §e" + finalTime + " §7[§c#" + ban.getSymbol() + "§7] " + ban.getAddendum());
            });
  }

  public String getKickMessage(Ban ban) {
    String time;
    if (ban.getTemplate().isPermanent()) {
      time = "§c§lPERMANENT";
    } else {
      time = "§e§l" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(ban.getTo()) + " Uhr";
    }

    return "§4§lThe§f§lLair.eu\n\n "
            + "§b§l•§8§m                                   §b§l•\n\n"
            + "§c§lDU WURDEST GEBANNT\n\n"
            + "§7Grund §8• §c§l" + ban.getTemplate().getReason() + " §7§l[§c§l#" + ban.getSymbol() + "§7§l]\n\n"
            + "§7Gebannt bis §8• " + time + "\n\n"
            + "§7Stelle einen Entbannungsantrag: §ehttps://thelair.eu/ea\n\n"
            + "§b§l•§8§m                                   §b§l•";
  }

  public void addAddendum(CommandSender sender, String symbol, String addendum) {
    if (banCache().findBySymbol(symbol).isPresent()) {
      Ban ban = banCache().findBySymbol(symbol).get();
      ban.setAddendum(ban.getAddendum() + " " + addendum);
      sql().update("UPDATE ban SET addendum=? WHERE symbol=?", ban.getAddendum(), symbol);
      sql().update("UPDATE log_entry SET addendum=? WHERE banSymbol=?", ban.getAddendum(), symbol);
      sendAddendumMessage(sender, symbol, addendum);
    } else {
      if (muteCache().findBySymbol(symbol).isPresent()) {
        Ban ban = muteCache().findBySymbol(symbol).get();
        ban.setAddendum(ban.getAddendum() + " " + addendum);
        sql().update("UPDATE ban SET addendum=? WHERE symbol=?", ban.getAddendum(), symbol);
        sql().update("UPDATE log_entry SET addendum=? WHERE banSymbol=?", ban.getAddendum(), symbol);
        sendAddendumMessage(sender, symbol, addendum);
      } else {
        sender.sendMessage(PunishmentSystem.PREFIX + "§cEin Bann mit der BanID existiert nicht!");
      }
    }
  }

  private void sendAddendumMessage(CommandSender sender, String symbol, String addendum) {
    for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
      if (!all.hasPermission("punishment.notify.addendum")) return;
      all.sendMessage(PunishmentSystem.PREFIX + "§c" + sender.getName() + " §7hat die Strafe §7[§c#" + symbol + "§7] bearbeitet");
      all.sendMessage(PunishmentSystem.PREFIX + "§7Addendum angehangen: §e" + addendum);
    }
  }

  public Ban checkIpBan(String address) {
    for (Ban ban : banCache().getMap().values()) {
      if (ban.getBanned() == null) continue;
      if (ban.getBanned().getLastIp() == null) continue;
      if (ban.getBanned().getLastIp().equals(address)) {
        return ban;
      }
    }
    return null;
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
