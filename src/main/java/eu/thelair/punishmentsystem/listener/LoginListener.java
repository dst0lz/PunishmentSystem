package eu.thelair.punishmentsystem.listener;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.entites.Ban;
import eu.thelair.punishmentsystem.entites.Template;
import eu.thelair.punishmentsystem.entites.User;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoginListener implements Listener {
  private final static String SYSTEM_BANNER_UUID = "9bdc73ee-c9a9-4a8c-ac27-38a33615135b";
  private final static String SYSTEM_ACC_CHECKER_TEMPLATE = "ACCOUNT";

  public LoginListener() {
  }

  @EventHandler
  public void onLogin(LoginEvent e) {
    String uuid = e.getConnection().getUniqueId().toString();
    String name = e.getConnection().getName();
    String ip = e.getConnection().getAddress().getHostString();
    PunishmentSystem.getInstance().getUserManager().initUser(uuid, name, ip);
    PunishmentSystem.getInstance().getUserManager().updateUser(uuid, name, ip);
    if (PunishmentSystem.getInstance().getBanCache().containsKey(uuid)) {
      Ban ban = PunishmentSystem.getInstance().getBanCache().get(uuid);
      boolean checkExceeded = PunishmentSystem.getInstance().getUnbanManager().checkBanExceeded(ban);
      if (checkExceeded) {
        PunishmentSystem.getInstance().getUnbanManager().deleteBan(ban);
      } else {
        e.setCancelled(true);
        e.setCancelReason(PunishmentSystem.getInstance().getBanManager().getKickMessage(ban));
      }
    } else {
      if (PunishmentSystem.getInstance().getBanManager().checkIpBan(ip) != null) {
        Ban ban = PunishmentSystem.getInstance().getBanManager().checkIpBan(ip);

        if (ban.getTemplate().getAbbreviation().equals("ACCOUNT")) {
          return;
        }

        User bannedUser = PunishmentSystem.getInstance().getUserManager().findByUuid(uuid);
        User bannerUser = PunishmentSystem.getInstance().getUserManager().findByUuid(SYSTEM_BANNER_UUID);

        Template template = PunishmentSystem.getInstance().getTemplateManager().findTemplateByAbbreviation(SYSTEM_ACC_CHECKER_TEMPLATE);
        PunishmentSystem.getInstance().getBanManager().ban(bannedUser, bannerUser, template, "(IPACCRISK | " + ban.getBanned().getLastName() + ")");
        e.setCancelled(true);
        e.setCancelReason(PunishmentSystem.getInstance().getBanManager().getKickMessage(ban));
      }
    }
  }

}
