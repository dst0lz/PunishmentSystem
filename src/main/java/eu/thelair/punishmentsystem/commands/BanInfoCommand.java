package eu.thelair.punishmentsystem.commands;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.entites.Ban;
import eu.thelair.punishmentsystem.entites.LogEntry;
import eu.thelair.punishmentsystem.entites.enums.TemplateType;
import eu.thelair.punishmentsystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BanInfoCommand extends Command {

  private final static String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

  public BanInfoCommand() {
    super("baninfo", "punishment.command.baninfo", "bans");
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (args.length == 1) {
      String target = args[0];

      String uuid = null;
      String name = target;
      if (target.matches(UUID_REGEX)) {
        uuid = target;
        try {
          name = UUIDFetcher.getName(UUID.fromString(uuid));
        } catch (Exception e) {
          sender.sendMessage(PunishmentSystem.PREFIX + "§cDiese UUID existiert nicht!");
          return;
        }
      }

      if (uuid == null) {
        try {
          uuid = UUIDFetcher.getUUID(target).toString();
        } catch (Exception e) {
          sender.sendMessage(PunishmentSystem.PREFIX + "§cDer Spieler existiert nicht!");
          return;
        }
      }

      sender.sendMessage(PunishmentSystem.PREFIX + "§7Ban Übersicht für §e" + name);
      sender.sendMessage("");
      sender.sendMessage("§7Ban:");
      if (PunishmentSystem.getInstance().getUnbanManager().isUserBanned(uuid)) {
        sendMessage(sender, uuid, true);
      } else {
        sender.sendMessage("  §8• §cKein Eintrag");
      }
      sender.sendMessage("");
      sender.sendMessage("§7Mute:");
      if (PunishmentSystem.getInstance().getUnbanManager().isUserMuted(uuid)) {
        sendMessage(sender, uuid, false);
      } else {
        sender.sendMessage("  §8• §cKein Eintrag");
      }
      sender.sendMessage("");

    } else {
      sender.sendMessage(PunishmentSystem.PREFIX + "§7Nutze §8• §c/bans <Name>");
    }
  }

  private String getDurationOfLog(LogEntry logEntry) {
    long days = TimeUnit.MILLISECONDS.toDays(logEntry.getDuration());

    String duration;
    if (logEntry.getDuration() == 0) {
      return "§cpermanent";
    } else if (logEntry.getDuration() == -1) {
      return "aufgehoben";
    }

    if (days == 0) {
      long hours = TimeUnit.MILLISECONDS.toHours(logEntry.getDuration());
      if(hours == 0) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(logEntry.getDuration());
        return minutes + (minutes == 1 ? " Minute" : " Minuten");
      }
      return hours + (hours == 1 ? " Stunde" : " Stunden");
    }

    return days + (days == 1 ? " Tag" : " Tage");
  }

  private String getRemainingDuration(LogEntry logEntry) {
    if (logEntry.getDuration() == -1 ||logEntry.getDuration() == 0) {
      return "" + logEntry.getDuration();
    }

    long to = logEntry.getSince().getTime() + logEntry.getDuration();
    long remainingDuration = to - System.currentTimeMillis();
    long days = TimeUnit.MILLISECONDS.toDays(remainingDuration);

    if (days == 0) {
      long hours = TimeUnit.MILLISECONDS.toHours(remainingDuration);
      if(hours == 0) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingDuration);
        return minutes + (minutes == 1 ? " Minute" : " Minuten");
      }
      return hours + (hours == 1 ? " Stunde" : " Stunden");
    }
    return days + (days == 1 ? " Tag" : " Tage");
  }

  private void sendMessage(CommandSender sender, String uuid, boolean ban) {

    List<LogEntry> logs = PunishmentSystem.getInstance().getLogManager().getLogByUser(uuid);
    LogEntry logEntry;

    if (ban) {
      Ban currentBan = PunishmentSystem.getInstance().getBanCache().get(uuid);

      if (currentBan == null || currentBan.getSymbol() == null) {
        sender.sendMessage("§cEs ist ein Fehler aufgetreten. Ban existiert nicht -> Admin melden!");
      }

      logEntry = logs.stream()
              .filter(Objects::nonNull)
              .filter(tmp -> tmp.getTemplate().getTemplateType() == TemplateType.BAN || tmp.getTemplate().getTemplateType() == TemplateType.UNBAN)
              .filter(tmp -> tmp.getBanSymbol().equals(currentBan.getSymbol()))
              .findFirst().get();
    } else {
      Ban currentBan = PunishmentSystem.getInstance().getMuteCache().get(uuid);
      logEntry = logs.stream()
              .filter(tmp -> tmp.getTemplate().getTemplateType() == TemplateType.MUTE || tmp.getTemplate().getTemplateType() == TemplateType.UNMUTE)
              .filter(tmp -> tmp.getBanSymbol().equals(currentBan.getSymbol()))
              .findFirst().orElse(null);
    }

    if (logEntry == null) {
      sender.sendMessage("§cEs ist ein Fehler aufgetreten. Log Fehler -> Admin melden!");
      return;
    }

    TextComponent textComponent = new TextComponent();
    textComponent.setText("  §8• §7BanID §8| §7[§c#" + logEntry.getBanSymbol() + "§7]");
    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, logEntry.getBanSymbol()));
    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aKlick zum kopieren")));

    sender.sendMessage("  §8• §7" + (ban ? "Gebannt" : "Gemutet") + " von §8| §e" + logEntry.getBannerName());
    sender.sendMessage("  §8• §7Grund §8| §e" + logEntry.getTemplate().getReason() + " " + logEntry.getAddendum());
    sender.sendMessage(textComponent);
    sender.sendMessage("  §8• §7Datum §8| §e" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logEntry.getSince()));
    sender.sendMessage("  §8• §7Dauer §8| §e" + getDurationOfLog(logEntry));
    if (getRemainingDuration(logEntry).equals("-1")) return;
    sender.sendMessage("  §8• §7Verbleibende Dauer §8| §e" + getRemainingDuration(logEntry));

  }

}
