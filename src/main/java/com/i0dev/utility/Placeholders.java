package com.i0dev.utility;

import com.i0dev.object.Warning;
import com.i0dev.InitilizeBot;
import com.i0dev.object.Invites;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class Placeholders {

    public static String convert(String s) {
        return convert(s, null);
    }


    public static String convert(String initialMessage, User user) {
        JDA jda = InternalJDA.get().getJda();
        Invites inviteManager = Invites.get();
        Guild guild = GlobalConfig.GENERAL_MAIN_GUILD;


        //User
        if (user != null) {
            initialMessage = initialMessage
                    .replace("{userName}", user.getName())
                    .replace("{userTag}", user.getAsTag())
                    .replace("{userMention}", user.getAsMention())
                    .replace("{userID}", user.getId())
                    .replace("{UserAvatarUrl}", user.getEffectiveAvatarUrl())
                    .replace("{inviteCount}", inviteManager.getUserInviteCount(user) + "")
                    .replace("{userWarns}", Warning.get().getUserWarnCount(user) + "")
                    .replace("{memberRoleCount}", guild.getMember(user).getRoles().size() + "")
                    .replace("{memberIsAdministrator}", guild.getMember(user).getPermissions().contains(Permission.ADMINISTRATOR) + "")
                    .replace("{memberEffectiveName}", guild.getMember(user).getEffectiveName());
        }
        //Guild
        if (guild != null) {
            String GuildBannerURL;
            if (guild.getBannerUrl() == null) {
                GuildBannerURL = "No Banner";
            } else {
                GuildBannerURL = guild.getBannerUrl();
            }

            initialMessage = initialMessage
                    .replace("{guildName}", guild.getName())
                    .replace("{guildMemberCount}", guild.getMemberCount() + "")
                    .replace("{guildBoostTier}", guild.getBoostTier().getKey() + "")
                    .replace("{guildBannerUrl}", GuildBannerURL)
                    .replace("{guildOwnerTag}", guild.getOwner().getUser().getAsTag())
                    .replace("{guildOwnerMention}", guild.getOwner().getUser().getAsMention())
                    .replace("{guildOwnerID}", guild.getOwner().getUser().getId())
                    .replace("{guildOwnerAvatarUrl}", guild.getOwner().getUser().getEffectiveAvatarUrl())
                    .replace("{guildOwnerName}", guild.getOwner().getUser().getName());
        }

        //Self User
        initialMessage = initialMessage
                .replace("{BotUserTag}", jda.getSelfUser().getAsTag())
                .replace("{BotUserMention}", jda.getSelfUser().getAsMention())
                .replace("{BotUserAvatarUL}", jda.getSelfUser().getEffectiveAvatarUrl())
                .replace("{BotUserID}", jda.getSelfUser().getId())
                .replace("{BotUserName}", jda.getSelfUser().getName());


        //General
        initialMessage = initialMessage
                .replace("{DiscordBotAuthor}", "i0#0001")
                .replace("{}", "")
                .replace("{}", "")
                .replace("{DiscordBotPluginMode}", InitilizeBot.get().isPluginMode() ? "Yes" : "No")
                .replace("{DiscordBotPrefix}", GlobalConfig.GENERAL_BOT_PREFIX)
                .replace("{DiscordBotVersion}", "2.0.24");


        return initialMessage;
    }
}
