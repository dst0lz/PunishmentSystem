package eu.thelair.punishmentsystem.commands;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.entites.Template;
import eu.thelair.punishmentsystem.entites.User;
import eu.thelair.punishmentsystem.entites.enums.TemplateType;
import eu.thelair.punishmentsystem.utils.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Comparator;
import java.util.UUID;

public class BanTemplateCommand extends Command {

  private final static String SYSTEM_BANNER_UUID = "9bdc73ee-c9a9-4a8c-ac27-38a33615135b";
  private final static String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

  public BanTemplateCommand() {
    super("bantemplate", "punishment.command.bt", "bt");
  }

  public void execute(CommandSender sender, String[] args) {
    boolean system = !(sender instanceof ProxiedPlayer);

    if (args.length >= 2) {
      String targetName = args[0];
      String templateName = args[1];
      String targetUuid;

      if (targetName.equalsIgnoreCase(sender.getName())) {
        sender.sendMessage(PunishmentSystem.PREFIX + "§7Du kannst dich §cnicht§7 selbst bannen!");
        return;
      }

      targetUuid = checkUser(sender, targetName);
      if (targetUuid == null) return;

      if (PunishmentSystem.getInstance().getUnbanManager().isUserBanned(targetUuid)) {
        sender.sendMessage(PunishmentSystem.PREFIX + "§7Der Spieler ist bereits gebannt!");
        return;
      }

      Template template = checkTemplate(sender, templateName);
      if (template == null) return;

      String banner = system ? SYSTEM_BANNER_UUID : ((ProxiedPlayer) sender).getUniqueId().toString();

      User bannedUser = PunishmentSystem.getInstance().getUserManager().findByUuid(targetUuid);
      User bannerUser = PunishmentSystem.getInstance().getUserManager().findByUuid(banner);

      //final ban
      if (args.length == 2) {
        PunishmentSystem.getInstance().getBanManager().ban(bannedUser, bannerUser, template, "");
      } else {
        StringBuilder buf = new StringBuilder();
        for (int i = 2; i < args.length; i++)
          buf.append(args[i]).append(" ");
        String addendum = buf.toString();
        addendum = addendum.substring(0, addendum.length() - 1);
        PunishmentSystem.getInstance().getBanManager().ban(bannedUser, bannerUser, template, addendum);
      }
    } else {
      sender.sendMessage(PunishmentSystem.PREFIX + "§7Nutze §8• §c/bt <Spieler> <Template> <Addendum>");
    }
  }

  /**
   * @param sender     the sender of the command
   * @param targetName of the player
   * @return null or the correct uuid who identifies the targetName
   */
  private String checkUser(CommandSender sender, String targetName) {
    if (targetName.matches(UUID_REGEX)) {
      return targetName;
    }

    ProxiedPlayer p = ProxyServer.getInstance().getPlayer(targetName);

    if (p == null) {
      UUID uuid = UUIDFetcher.getUUID(targetName);
      if (uuid == null) {
        sender.sendMessage(PunishmentSystem.PREFIX + "§7Dieser Spieler existiert §cnicht§7!");
        return null;
      }
      return uuid.toString();
    } else {
      return p.getUniqueId().toString();
    }
  }


  /**
   * Send all templates by {@link TemplateType BAN} if the templateName not matches with any {@link Template}
   *
   * @param sender       the sender of the command
   * @param templateName the name of the given template "args[1]"
   * @return null or the correct template identifies by name
   */
  private Template checkTemplate(CommandSender sender, String templateName) {
    Template template = PunishmentSystem.getInstance().getTemplateManager().findTemplateByAbbreviation(templateName);
    if (template == null) {
      sender.sendMessage(PunishmentSystem.PREFIX + "§7Das Template §e" + templateName + " §7existiert §cnicht§7!");
      StringBuffer buf = new StringBuffer("§8[§c⚒§8] §8• §r§7Templates: §c");
      PunishmentSystem.getInstance().getTemplateCache().getMap().values()
              .stream()
              .filter(te -> (te.getTemplateType() == TemplateType.BAN))
              .sorted(Comparator.comparing(Template::getId))
              .map(Template::getAbbreviation)
              .forEachOrdered(s -> buf.append(s).append(", "));
      String temp = buf.toString();
      sender.sendMessage(temp.substring(0, temp.length() - 2));
      return null;
    }

    if (template.getTemplateType() != TemplateType.BAN) {
      sender.sendMessage(PunishmentSystem.PREFIX + "§7Du darfst das Template nicht zum bannen verwenden!");
      return null;
    }

    return template;
  }

}
