package main.java.com.i0dev.util;

import main.java.com.i0dev.entity.Invites;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class Placeholders {


    public static String convert(String initialMessage, User user) {
        JDA jda = initJDA.get().getJda();
        Invites inviteManager = Invites.get();
        Guild guild = conf.GENERAL_MAIN_GUILD;


        String GuildBannerURL;
        if (guild.getBannerUrl() == null) {
            GuildBannerURL = "No Banner";
        } else {
            GuildBannerURL = guild.getBannerUrl();
        }

        return initialMessage
                .replace("{userName}", user.getName())
                .replace("{guildName}", guild.getName())
                .replace("{guildMemberCount}", guild.getMemberCount() + "")
                .replace("{guildBoostTier}", guild.getBoostTier().getKey() + "")
                .replace("{guildBannerUrl}", GuildBannerURL)
                .replace("{guildOwnerTag}", guild.getOwner().getUser().getAsTag())
                .replace("{guildOwnerMention}", guild.getOwner().getUser().getAsMention())
                .replace("{guildOwnerID}", guild.getOwner().getUser().getId())
                .replace("{guildOwnerAvatarUrl}", guild.getOwner().getUser().getEffectiveAvatarUrl())
                .replace("{guildOwnerName}", guild.getOwner().getUser().getName())
                .replace("{memberRoleCount}", guild.getMember(user).getRoles().size() + "")
                .replace("{memberIsAdministrator}", guild.getMember(user).getPermissions().contains(Permission.ADMINISTRATOR) + "")
                .replace("{memberEffectiveName}", guild.getMember(user).getEffectiveName())
                .replace("{BotUserTag}", jda.getSelfUser().getAsTag())
                .replace("{BotUserMention}", jda.getSelfUser().getAsMention())
                .replace("{BotUserID}", jda.getSelfUser().getId())
                .replace("{userTag}", user.getAsTag())
                .replace("{userMention}", user.getAsMention())
                .replace("{userID}", user.getId())
                .replace("{BotUserAvatarUL}", jda.getSelfUser().getEffectiveAvatarUrl())
                .replace("{UserAvatarUrl}", user.getEffectiveAvatarUrl())
                .replace("{inviteCount}", inviteManager.getUserInviteCount(user) + "")
                .replace("{BotUserName}", jda.getSelfUser().getName());
    }
}
