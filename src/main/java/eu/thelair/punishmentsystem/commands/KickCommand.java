package eu.thelair.punishmentsystem.commands;

import eu.thelair.punishmentsystem.PunishmentSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class KickCommand extends Command {
  private final static String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

  public KickCommand() {
    super("kick", "punishment.command.kick");
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (args.length > 1) {
      String targetName = args[0];
      if (targetName.equalsIgnoreCase(sender.getName())) {
        sender.sendMessage(PunishmentSystem.PREFIX + "§7Du kannst dich §cnicht§7 selbst bannen!");
        return;
      }

      ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetName);
      if (targetPlayer == null) {
        sender.sendMessage(PunishmentSystem.PREFIX + "§7Dieser Spieler ist §cnicht §7online!");
        return;
      }
      String addendum;
      if (args.length > 2) {
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < args.length; i++)
          buf.append(args[i]).append(" ");
        addendum = buf.toString();
        addendum = addendum.substring(0, addendum.length() - 1);
      } else {
        addendum = args[1];
      }
      PunishmentSystem.getInstance().getLogManager().createKickLog(sender.getName(), targetPlayer.getUniqueId().toString(), addendum);
      sendNotify(sender, targetPlayer, addendum);
      targetPlayer.disconnect("§4§lThe§f§lLair.eu\n\n "
              + "§b§l•§8§m                                   §b§l•\n\n"
              + "§c§lDU WURDEST GEKICKT\n\n"
              + "§7Grund §8• §c§l" + addendum + "\n\n"
              + "§b§l•§8§m                                   §b§l•");
    } else {
      sender.sendMessage(PunishmentSystem.PREFIX + "§7Nutze /kick <Name> <Grund>");
    }
  }

  private void sendNotify(CommandSender sender, ProxiedPlayer target, String reason) {
    String color, kicker;
    String targetColor = PunishmentSystem.getInstance().getPermissionManager().getColorCode(target.getUniqueId());

    if (sender instanceof ProxiedPlayer) {
      ProxiedPlayer p = (ProxiedPlayer) sender;
      color = PunishmentSystem.getInstance().getPermissionManager().getColorCode(p.getUniqueId());
      kicker = sender.getName();
    } else {
      color = "";
      kicker = "§4Medusa";
    }

    ProxyServer.getInstance().getPlayers()
            .stream()
            .filter(p -> p.hasPermission("punishment.notify.ban"))
            .forEach(all -> {
              all.sendMessage(PunishmentSystem.PREFIX + "§7Der Spieler " + targetColor + target.getName() + " §7wurde von " + color + kicker + " §7gekickt!");
              all.sendMessage(PunishmentSystem.PREFIX + "§7Grund: §c" + reason);
            });
  }
}