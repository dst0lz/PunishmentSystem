package eu.thelair.punishmentsystem.commands;

import eu.thelair.punishmentsystem.PunishmentSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class AddAdendumBanCommand extends Command {

  public AddAdendumBanCommand() {
    super("addadendumban", "punishment.command.aab", "aab");
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (args.length >= 2) {
      String symbol = args[0];
      if (args.length == 2) {
        PunishmentSystem.getInstance().getBanManager().addAddendum(sender, symbol, args[1]);
      } else {
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < args.length; i++)
          buf.append(args[i]).append(" ");
        String addendum = buf.toString();
        addendum = addendum.substring(0, addendum.length() - 1);
        PunishmentSystem.getInstance().getBanManager().addAddendum(sender, symbol, addendum);
      }
    } else {
      sender.sendMessage(PunishmentSystem.PREFIX + "§7Nutze §8• §c/aab <BanID> <Addendum>");
    }
  }
}
