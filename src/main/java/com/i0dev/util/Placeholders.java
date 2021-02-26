package main.java.com.i0dev.util;

import main.java.com.i0dev.entity.Invites;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class Placeholders {


    public static String convert(String initialMessage, User user) {
        JDA jda = initJDA.get().getJda();
        Invites inviteManager = Invites.get();
        Guild guild = conf.GENERAL_MAIN_GUILD;


        String AvatarURL;
        if (user.getAvatarUrl() == null) {
            AvatarURL = user.getDefaultAvatarUrl();
        } else {
            AvatarURL = user.getAvatarUrl();
        }
        String BotAvatarURL;
        if (jda.getSelfUser().getAvatarUrl() == null) {
            BotAvatarURL = jda.getSelfUser().getDefaultAvatarUrl();
        } else {
            BotAvatarURL = jda.getSelfUser().getAvatarUrl();
        }


        String OwnerAvatarURL;
        if (guild.getOwner().getUser().getAvatarUrl() != null) {
            OwnerAvatarURL = jda.getSelfUser().getDefaultAvatarUrl();
        } else {
            OwnerAvatarURL = guild.getOwner().getUser().getDefaultAvatarUrl();
        }
        String GuildBannerURL;
        if (guild.getBannerUrl() == null) {
            GuildBannerURL = "No Banner";
        } else {
            GuildBannerURL = guild.getBannerUrl();
        }


        return initialMessage
                .replace("{userAvatarURL}", AvatarURL)
                .replace("{userName}", user.getName())
                .replace("{guildName}", guild.getName())
                .replace("{guildMemberCount}", guild.getMemberCount() + "")
                .replace("{guildBoostTier}", guild.getBoostTier().getKey() + "")
                .replace("{guildBannerUrl}", GuildBannerURL)
                .replace("{guildOwnerTag}", guild.getOwner().getUser().getAsTag())
                .replace("{guildOwnerMention}", guild.getOwner().getUser().getAsMention())
                .replace("{guildOwnerID}", guild.getOwner().getUser().getId())
                .replace("{guildOwnerAvatarUrl}", OwnerAvatarURL)
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
                .replace("{BotUserAvatarUL}", BotAvatarURL)
                .replace("{UserAvatarUrl}", AvatarURL)
                .replace("{inviteCount}", inviteManager.getUserInviteCount(user) + "")
                .replace("{BotUserName}", jda.getSelfUser().getName());
    }
}
