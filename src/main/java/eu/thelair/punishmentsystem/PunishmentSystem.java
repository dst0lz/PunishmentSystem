package eu.thelair.punishmentsystem;

import eu.thelair.punishmentsystem.cache.BanCache;
import eu.thelair.punishmentsystem.cache.MuteCache;
import eu.thelair.punishmentsystem.cache.TemplateCache;
import eu.thelair.punishmentsystem.cache.UserCache;
import eu.thelair.punishmentsystem.commands.*;
import eu.thelair.punishmentsystem.communication.MedusaPanelCommunication;
import eu.thelair.punishmentsystem.database.MySQL;
import eu.thelair.punishmentsystem.listener.ChatListener;
import eu.thelair.punishmentsystem.listener.LoginListener;
import eu.thelair.punishmentsystem.listener.PluginMessageListener;
import eu.thelair.punishmentsystem.managers.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class PunishmentSystem extends Plugin {
  public final static String PREFIX = "§8[§c⚒§8] §8• §r";
  public final static String THELAIR_PREFIX = "§4The§fLair §8• §r";

  private static PunishmentSystem instance;
  private MySQL mySQL;

  //Cache
  private TemplateCache templateCache;
  private UserCache userCache;
  private BanCache banCache;
  private MuteCache muteCache;

  private UserManager userManager;
  private BanManager banManager;
  private PermissionManager permissionManager;
  private TemplateManager templateManager;
  private LogManager logManager;
  private UnbanManager unbanManager;

  @Override
  public void onEnable() {
    instance = this;
    this.mySQL = new MySQL();
    registerManagers();
    initCache();
    execute();
    registerCommands();
    registerEvents();
    this.getProxy().registerChannel("AntiHack");
  }

  @Override
  public void onDisable() {
    mySQL.disconnect();
    this.getProxy().unregisterChannel("AntiHack");
  }

  private void initCache() {
    templateCache = new TemplateCache();
    userCache = new UserCache();
    banCache = new BanCache();
    muteCache = new MuteCache();
  }

  private void registerManagers() {
    this.userManager = new UserManager();
    this.templateManager = new TemplateManager();
    this.banManager = new BanManager();
    this.permissionManager = new PermissionManager();
    this.logManager = new LogManager();
    this.unbanManager = new UnbanManager();
  }

  private void execute() {
    MedusaPanelCommunication communication = new MedusaPanelCommunication();
    communication.startTask();
  }

  private void registerCommands() {
    PluginManager pm = ProxyServer.getInstance().getPluginManager();
    pm.registerCommand(this, new BanTemplateCommand());
    pm.registerCommand(this, new BanLogCommand());
    pm.registerCommand(this, new UnbanTemplateCommand());
    pm.registerCommand(this, new MuteTemplateCommand());
    pm.registerCommand(this, new UnmuteTemplateCommand());
    pm.registerCommand(this, new AddAdendumBanCommand());
    pm.registerCommand(this, new BanInfoCommand());
    pm.registerCommand(this, new KickCommand());
  }

  private void registerEvents() {
    PluginManager pm = ProxyServer.getInstance().getPluginManager();
    pm.registerListener(this, new LoginListener());
    pm.registerListener(this, new ChatListener());
    pm.registerListener(this, new PluginMessageListener());
  }

  public static PunishmentSystem getInstance() {
    return instance;
  }

  public TemplateCache getTemplateCache() {
    return templateCache;
  }

  public UserCache getUserCache() {
    return userCache;
  }

  public BanCache getBanCache() {
    return banCache;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public BanManager getBanManager() {
    return banManager;
  }

  public PermissionManager getPermissionManager() {
    return permissionManager;
  }

  public TemplateManager getTemplateManager() {
    return templateManager;
  }

  public LogManager getLogManager() {
    return logManager;
  }

  public UnbanManager getUnbanManager() {
    return unbanManager;
  }

  public MuteCache getMuteCache() {
    return muteCache;
  }

  public MySQL getMySQL() {
    return mySQL;
  }
}
