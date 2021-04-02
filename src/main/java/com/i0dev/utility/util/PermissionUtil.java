package main.java.com.i0dev.utility.util;

import main.java.com.i0dev.utility.getConfig;
import main.java.com.i0dev.utility.InternalJDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;


public class PermissionUtil {
    private  final static PermissionUtil instance = new PermissionUtil();
    public static PermissionUtil get() {
        return instance;
    }

    public boolean hasStrictPermission(User user, Guild guild) {
        if (Objects.requireNonNull(guild.getMember(user)).getPermissions().contains(Permission.ADMINISTRATOR))
            return true;
        for (long RoleID : getConfig.get().getLongList("roles.StrictAllowedRoles")) {
            if (guild.getMember(user) == null) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().isEmpty()) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().contains(guild.getRoleById(RoleID))) {
                return true;
            }
        }
        for (long userID : getConfig.get().getLongList("roles.StrictAllowedUsers")) {
            if (user.equals(InternalJDA.get().getJda().getUserById(userID))) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLitePermission(User user, Guild guild) {

        if (hasStrictPermission(user, guild)) return true;
        for (long RoleID : getConfig.get().getLongList("roles.LiteAllowedRoles")) {
            if (guild.getMember(user) == null) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().isEmpty()) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().contains(guild.getRoleById(RoleID))) {
                return true;
            }
        }
        for (long userID : getConfig.get().getLongList("roles.LiteAllowedUsers")) {
            if (user.equals(InternalJDA.get().getJda().getUserById(userID))) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(boolean requirePermission, boolean permissionIsLiteMode, Guild guild, User user) {
        if (requirePermission && permissionIsLiteMode) {
            if (!hasLitePermission(user, guild)) {
                return false;
            }
        }
        if (requirePermission && !permissionIsLiteMode) {
            return hasStrictPermission(user, guild);
        }
        return true;
    }
}
