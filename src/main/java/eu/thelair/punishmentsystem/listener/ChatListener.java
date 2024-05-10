package eu.thelair.punishmentsystem.listener;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.ext.bridge.node.CloudNetBridgeModule;
import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.entites.Ban;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.text.SimpleDateFormat;

public class ChatListener implements Listener {

  @EventHandler
  public void onChat(ChatEvent e) {
    ProxiedPlayer p = (ProxiedPlayer) e.getSender();
    String uuid = p.getUniqueId().toString();
    if (PunishmentSystem.getInstance().getMuteCache().containsKey(uuid)) {
      Ban ban = PunishmentSystem.getInstance().getMuteCache().get(uuid);
      boolean checkExceeded = PunishmentSystem.getInstance().getUnbanManager().checkBanExceeded(ban);
      if (checkExceeded) {
        PunishmentSystem.getInstance().getUnbanManager().deleteBan(ban);
      } else {
        if (e.getMessage().startsWith("/")) return;

        String time;
        if (ban.getTemplate().isPermanent()) {
          time = "permanent";
        } else {
          time = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(ban.getTo()) + " Uhr";
        }

        p.sendMessage(PunishmentSystem.THELAIR_PREFIX + "§7Du wurdest aus dem Chat §cgebannt");
        p.sendMessage(PunishmentSystem.THELAIR_PREFIX + "§7Grund: §c" + ban.getTemplate().getReason() + " §7bis §e" + time + " §7[§c#" + ban.getSymbol() + "§7]");
        p.sendMessage(PunishmentSystem.THELAIR_PREFIX + "§7Du möchtest deine Strafe verkürzen / aufheben? §8➡ §ehttps://thelair.eu/goto/ea");
        e.setMessage(null);
        e.setCancelled(true);
      }

                  }

  }

}
