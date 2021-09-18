package com.i0dev.utility;

import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class GlobalCheck {

    public static boolean checkBasic(GuildMessageReceivedEvent event, boolean enabled, PermissionHandler permissionHandler, String identifier) {

        if (event.getAuthor().isBot()) return false;
        if (!FormatUtil.isValidGuild(event.getGuild())) return false;
        if (DPlayerEngine.getObject(event.getAuthor().getIdLong()).isBlacklisted()) return false;

        if (!PermissionUtil.get().hasPermission(event, permissionHandler.isStrict(), permissionHandler.isLite(), permissionHandler.isAdmin())) {
            MessageUtil.sendMessage(event.getChannel().getIdLong(), GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", identifier), event.getAuthor(), null);
            return false;
        }
        if (!enabled) {
            MessageUtil.sendMessage(event.getChannel().getIdLong(), GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", identifier), event.getAuthor(), null);
            return false;
        }
        if (GlobalConfig.GENERAL_DELETE_COMMAND) {
            event.getMessage().delete().queue(null, new ErrorHandler()
                    .ignore(ErrorResponse.UNKNOWN_MESSAGE)
            );
        }
        return true;

    }


    public static boolean check(GuildMessageReactionAddEvent event, String identifier, boolean enabled, boolean PERMISSION_STRICT, boolean PERMISSION_LITE, boolean PERMISSION_ADMIN) {
        if (event.getUser().isBot()) return false;
        if (!FormatUtil.isValidGuild(event.getGuild())) return false;
        if (DPlayerEngine.getObject(event.getUser().getIdLong()).isBlacklisted()) return false;
        if (!enabled) {
            return false;
        }
        return PermissionUtil.get().hasPermission(event, PERMISSION_STRICT, PERMISSION_LITE, PERMISSION_ADMIN);
    }
}

