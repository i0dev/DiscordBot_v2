package com.i0dev.utility.util;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.InternalJDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Objects;


public class PermissionUtil {
    private final static PermissionUtil instance = new PermissionUtil();

    public static PermissionUtil get() {
        return instance;
    }

    public boolean hasStrictPermission(User user, Guild guild) {
        if (Objects.requireNonNull(guild.getMember(user)).getPermissions().contains(Permission.ADMINISTRATOR))
            return true;
        for (long RoleID : Configuration.getLongList("roles.StrictAllowedRoles")) {
            if (guild.getMember(user) == null) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().isEmpty()) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().contains(guild.getRoleById(RoleID))) {
                return true;
            }
        }
        for (long userID : Configuration.getLongList("roles.StrictAllowedUsers")) {
            if (user.equals(InternalJDA.get().getJda().getUserById(userID))) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLitePermission(User user, Guild guild) {

        if (hasStrictPermission(user, guild)) return true;
        for (long RoleID : Configuration.getLongList("roles.LiteAllowedRoles")) {
            if (guild.getMember(user) == null) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().isEmpty()) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().contains(guild.getRoleById(RoleID))) {
                return true;
            }
        }
        for (long userID : Configuration.getLongList("roles.LiteAllowedUsers")) {
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

    public boolean hasPermission(GuildMessageReceivedEvent e, boolean strict, boolean lite, boolean admin) {
        if (admin) {
            return e.getMember().hasPermission(Permission.ADMINISTRATOR);
        } else if (lite) {
            return hasLitePermission(e.getAuthor(), e.getGuild());
        } else if (strict) {
            return hasStrictPermission(e.getAuthor(), e.getGuild());
        }
        return true;
    }

    public boolean hasPermission(GuildMessageReactionAddEvent e, boolean strict, boolean lite, boolean admin) {
        if (admin) {
            return e.getMember().hasPermission(Permission.ADMINISTRATOR);
        } else if (lite) {
            return hasLitePermission(e.getUser(), e.getGuild());
        } else if (strict) {
            return hasStrictPermission(e.getUser(), e.getGuild());
        }
        return true;
    }

    public boolean hasPermission(GuildMessageReceivedEvent e, boolean strict, boolean lite) {
        return hasPermission(e, strict, lite, false);
    }
}
