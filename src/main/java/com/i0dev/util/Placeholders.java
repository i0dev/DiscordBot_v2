package com.i0dev.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class Placeholders {


    public static String convert(String initialMessage, User user) {
        String message = initialMessage;
        Guild guild = conf.GENERAL_MAIN_GUILD;

        if (message.contains("{userTag}")) {
            message = message.replace("{userTag}", user.getAsTag());
        }
        if (message.contains("{userMention}")) {
            message = message.replace("{userMention}", user.getAsMention());
        }
        if (message.contains("{userID}")) {
            message = message.replace("{userID}", user.getId());
        }
        String AvatarURL;
        if (user.getAvatarUrl() == null) {
            AvatarURL = user.getDefaultAvatarUrl();
        } else {
            AvatarURL = user.getAvatarUrl();
        }
        if (message.contains("{userAvatarURL}")) {
            message = message.replace("{userAvatarURL}", AvatarURL);
        }
        if (message.contains("{userName}")) {
            message = message.replace("{userName}", user.getName());
        }
        if (message.contains("{guildName}")) {
            message = message.replace("{guildName}", guild.getName());
        }
        if (message.contains("{guildMemberCount}")) {
            message = message.replace("{guildMemberCount}", guild.getMemberCount() + "");
        }
        if (message.contains("{guildBoostTier}")) {
            message = message.replace("{guildBoostTier}", guild.getBoostTier().getKey() + "");
        }
        if (message.contains("{guildBannerUrl}")) {
            message = message.replace("{guildBannerUrl}", guild.getBannerUrl());
        }
        if (message.contains("{guildOwnerTag}")) {
            message = message.replace("{guildOwnerTag}", guild.getOwner().getUser().getAsTag());
        }
        if (message.contains("{guildOwnerMention}")) {
            message = message.replace("{guildOwnerMention}", guild.getOwner().getUser().getAsMention());
        }
        if (message.contains("{guildOwnerID}")) {
            message = message.replace("{guildOwnerID}", guild.getOwner().getUser().getId());
        }
        if (message.contains("{guildOwnerAvatarUrl}")) {
            message = message.replace("{guildOwnerAvatarUrl}", guild.getOwner().getUser().getAvatarUrl());
        }
        if (message.contains("{guildOwnerName}")) {
            message = message.replace("{guildOwnerName}", guild.getOwner().getUser().getName());
        }
        if (message.contains("{memberRoleCount}")) {
            message = message.replace("{memberRoleCount}", guild.getMember(user).getRoles().size() + "");
        }
        if (message.contains("{memberIsAdministrator}")) {
            message = message.replace("{memberIsAdministrator}", guild.getMember(user).getPermissions().contains(Permission.ADMINISTRATOR) + "");
        }
        if (message.contains("{memberEffectiveName}")) {
            message = message.replace("{memberEffectiveName}", guild.getMember(user).getEffectiveName());
        }
        if (message.contains("{BotUserTag}")) {
            message = message.replace("{BotUserTag}", initJDA.get().getJda().getSelfUser().getAsTag());
        }
        if (message.contains("{BotUserMention}")) {
            message = message.replace("{BotUserMention}", initJDA.get().getJda().getSelfUser().getAsMention());
        }
        if (message.contains("{BotUserID}")) {
            message = message.replace("{BotUserID}", initJDA.get().getJda().getSelfUser().getId());
        }
        String BotAvatarURL;
        if (initJDA.get().getJda().getSelfUser().getAvatarUrl() == null) {
            BotAvatarURL = initJDA.get().getJda().getSelfUser().getDefaultAvatarUrl();
        } else {
            BotAvatarURL = initJDA.get().getJda().getSelfUser().getAvatarUrl();
        }
        if (message.contains("{BotUserAvatarURL}")) {
            message = message.replace("{BotUser}", BotAvatarURL);
        }
        if (message.contains("{BotUserName}")) {
            message = message.replace("{BotUserName}", initJDA.get().getJda().getSelfUser().getName());
        }

        return message;
    }

}
