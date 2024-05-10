package eu.thelair.punishmentsystem.managers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;

public class PermissionManager {

    private static PermissionManager instance;

    public PermissionManager() {
        instance = this;
    }

    public String getGroupByPlayer(UUID uuid) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        return user.getPrimaryGroup();
    }

    public String getColorCode(UUID uuid) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "§3";
        }

        Group group = luckPerms.getGroupManager().getGroup(user.getPrimaryGroup());

        if (group == null) {
            return "§3";
        }

        String prefix = group.getCachedData().getMetaData().getPrefix();

        if (prefix == null) {
            return "§3";
        }

        if (prefix.length() == 2) {
            return ChatColor.translateAlternateColorCodes('&', prefix);
        } else {
            return ChatColor.translateAlternateColorCodes('&', prefix.substring(0, 2));
        }
    }

    public String getPrefix(UUID uuid) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "§3";
        }
        Group group = luckPerms.getGroupManager().getGroup(user.getPrimaryGroup());

        String prefix = group.getCachedData().getMetaData().getPrefix();
        if (prefix == null) {
            return "§3";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public int getWeight(UUID uuid) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return 6;
        }
        Group group = luckPerms.getGroupManager().getGroup(user.getPrimaryGroup());
        return group.getWeight().getAsInt();
    }

    public String getPrefix(String groupName) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        Group group = luckPerms.getGroupManager().getGroup(groupName);

        String prefix = group.getCachedData().getMetaData().getPrefix();
        if (prefix == null) {
            return "§8";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getSuffix(String groupName) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        Group group = luckPerms.getGroupManager().getGroup(groupName);

        String suffix = group.getCachedData().getMetaData().getSuffix();
        if (suffix == null) {
            return "§8";
        }
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }

    public static PermissionManager getInstance() {
        return instance;
    }
}