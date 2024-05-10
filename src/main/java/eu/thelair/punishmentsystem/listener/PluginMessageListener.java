package eu.thelair.punishmentsystem.listener;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.entites.Template;
import eu.thelair.punishmentsystem.entites.User;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PluginMessageListener implements Listener {
  private final static String SYSTEM_BANNER_UUID = "6f818677-f8c1-41f0-857b-0099b2e58728";

  @EventHandler
  public void onPluginMessage(PluginMessageEvent ev) {
    if (!ev.getTag().equals("AntiHack")) {
      return;
    }

    if (!(ev.getSender() instanceof Server)) {
      return;
    }

    ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
    DataInputStream in = new DataInputStream(stream);
    String s = "";
    String player = "";
    String uuid = "";
    String reason = "";
    try {
      s = in.readUTF();
      player = s.split(":")[0];
      uuid = s.split(":")[1];
      reason = s.split(":")[2];

      User bannedUser = PunishmentSystem.getInstance().getUserManager().findByUuid(uuid);
      User bannerUser = PunishmentSystem.getInstance().getUserManager().findByUuid(SYSTEM_BANNER_UUID);
      Template template = PunishmentSystem.getInstance().getTemplateManager().findTemplateByAbbreviation("HACKING");

      reason = "(" + reason + ")";
      PunishmentSystem.getInstance().getBanManager().ban(bannedUser, bannerUser, template, reason);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
