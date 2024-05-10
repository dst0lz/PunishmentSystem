package eu.thelair.punishmentsystem.commands;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.entites.LogEntry;
import eu.thelair.punishmentsystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.plugin.Command;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BanLogCommand extends Command {

  private final static String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

  public BanLogCommand() {
    super("banlog", "punishment.command.banlog", "bl");
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

      List<LogEntry> logEntries = PunishmentSystem.getInstance().getLogManager().getLogByUser(uuid);
      sender.sendMessage(PunishmentSystem.PREFIX + "§7Banlog §e" + name);
      sender.sendMessage("");

      if (logEntries.isEmpty()) {
        sender.sendMessage("§8» §cKeine Einträge :)");
        return;
      }

      for (LogEntry logEntry : logEntries) {
        TextComponent component = new TextComponent();
        if (logEntry.getTemplate() == null) {
          component.setText("§8» [§c#" + logEntry.getBanSymbol() + "§8] | §cKick");
        } else {
          component.setText("§8» [§c#" + logEntry.getBanSymbol() + "§8] | §c" + logEntry.getTemplate().getReason());
        }

        String duration = getDurationOfLog(logEntry);

        String display;
        if(logEntry.getTemplate() == null) {
          display = "§7Gebannt von §8| §e" + logEntry.getBannerName() + "\n"
                  + "§7Grund §8| §e" + logEntry.getAddendum() + "\n"
                  + "§7Dauer §8| §e" + duration + "\n"
                  + "§7Datum §8| §e" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logEntry.getSince());
        } else {
          display = "§7Gebannt von §8| §e" + logEntry.getBannerName() + "\n"
                  + "§7Grund §8| §e" + logEntry.getTemplate().getReason() + " " + logEntry.getAddendum() + "\n"
                  + "§7Dauer §8| §e" + duration + "\n"
                  + "§7Datum §8| §e" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(logEntry.getSince());
        }

        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(display)));
        sender.sendMessage(component);
        sender.sendMessage("");
      }
    } else {
      sender.sendMessage(PunishmentSystem.PREFIX + "§7Nutze §8• §c/banlog <Name>");
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

}
