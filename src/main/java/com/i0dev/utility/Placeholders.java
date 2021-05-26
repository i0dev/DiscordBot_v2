package com.i0dev.utility;

import com.i0dev.DiscordBot;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.text.DecimalFormat;

public class Placeholders {

    public static String convert(String s) {
        return convert(s, null);
    }

    public static String convert(String message, User author) {
        return convert(message, null, author);
    }

    public static String convert(String message, User mentioned, User author) {
        JDA jda = InternalJDA.getJda();
        Guild guild = GlobalConfig.GENERAL_MAIN_GUILD;
        if (message == null) return null;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        //Mentioned User
        if (mentioned != null) {
            DPlayer dPlayer = DPlayerEngine.getObject(mentioned.getIdLong());
            message = message
                    .replace("{mentionedUserName}", mentioned.getName())
                    .replace("{mentionedUserTag}", mentioned.getAsTag())
                    .replace("{mentionedUserTagBold}", "**" + mentioned.getAsTag() + "**")
                    .replace("{mentionedUserMention}", mentioned.getAsMention())
                    .replace("{mentionedUserID}", mentioned.getId())
                    .replace("{mentionedUserAvatarUrl}", mentioned.getEffectiveAvatarUrl())

                    .replace("{mentionedUserLinkStatus}", dPlayer.getLinkInfo().isLinked() ? "Linked" : "Not Linked")
                    .replace("{mentionedUserInvitedByTag}", dPlayer.getCachedData().getInvitedByDiscordTag().equals("") ? "No Data" : dPlayer.getCachedData().getInvitedByDiscordTag())
                    .replace("{mentionedUserLinkedIGN}", dPlayer.getCachedData().getMinecraftIGN().equals("") ? "Not Linked" : dPlayer.getCachedData().getMinecraftIGN())
                    .replace("{mentionedUserMinecraftUUID}", dPlayer.getLinkInfo().getMinecraftUUID().equals("") ? "Not Linked" : dPlayer.getLinkInfo().getMinecraftUUID())
                    .replace("{mentionedUserIsBlacklisted}", dPlayer.isBlacklisted() ? "Yes" : "No")
                    .replace("{mentionedUserPointsCount}", decimalFormat.format(dPlayer.getPoints()))
                    .replace("{mentionedUserBoostCount}", dPlayer.getBoostCount() + "")

                    .replace("{mentionedUserInviteCount}", DPlayerEngine.getObject(mentioned.getIdLong()).getInviteCount() + "")
                    .replace("{mentionedUserTicketsClosed}", DPlayerEngine.getObject(mentioned.getIdLong()).getTicketsClosed() + "")
                    .replace("{mentionedUserWarnCount}", DPlayerEngine.getObject(mentioned.getIdLong()).getWarnCount() + "")
                    .replace("{mentionedUserRoleCount}", guild.getMember(mentioned).getRoles().size() + "")
                    .replace("{mentionedUserIsAdministrator}", guild.getMember(mentioned).getPermissions().contains(Permission.ADMINISTRATOR) + "")
                    .replace("{mentionedUserEffectiveName}", guild.getMember(mentioned).getEffectiveName());
            dPlayer = null;
        }

        //Author
        if (author != null) {
            DPlayer dPlayer = DPlayerEngine.getObject(author.getIdLong());
            message = message
                    .replace("{authorName}", author.getName())
                    .replace("{authorTag}", author.getAsTag())
                    .replace("{authorTagBold}", "**" + author.getAsTag() + "**")
                    .replace("{authorMention}", author.getAsMention())
                    .replace("{authorID}", author.getId())
                    .replace("{authorAvatarUrl}", author.getEffectiveAvatarUrl())

                    .replace("{authorLinkStatus}", dPlayer.getLinkInfo().isLinked() ? "Linked" : "Not Linked")
                    .replace("{authorInvitedByTag}", dPlayer.getCachedData().getInvitedByDiscordTag().equals("") ? "No Data" : dPlayer.getCachedData().getInvitedByDiscordTag())
                    .replace("{authorLinkedIGN}", dPlayer.getCachedData().getMinecraftIGN().equals("") ? "Not Linked" : dPlayer.getCachedData().getMinecraftIGN())
                    .replace("{authorIsBlacklisted}", dPlayer.isBlacklisted() ? "Yes" : "No")
                    .replace("{authorPointsCount}", decimalFormat.format(dPlayer.getPoints()))
                    .replace("{authorBoostCount}", dPlayer.getBoostCount() + "")


                    .replace("{authorInviteCount}", DPlayerEngine.getObject(author.getIdLong()).getInviteCount() + "")
                    .replace("{authorTicketsClosed}", DPlayerEngine.getObject(author.getIdLong()).getTicketsClosed() + "")
                    .replace("{authorWarnCount}", DPlayerEngine.getObject(author.getIdLong()).getWarnCount() + "")
                    .replace("{authorMemberRoleCount}", guild.getMember(author).getRoles().size() + "")
                    .replace("{authorIsAdministrator}", guild.getMember(author).getPermissions().contains(Permission.ADMINISTRATOR) + "")
                    .replace("{authorEffectiveName}", guild.getMember(author).getEffectiveName());
            dPlayer = null;
        }


        //Guild
        if (guild != null) {
            message = message
                    .replace("{guildName}", guild.getName())
                    .replace("{guildMemberCount}", guild.getMemberCount() + "")
                    .replace("{guildBoostTier}", guild.getBoostTier().getKey() + "")
                    .replace("{guildBannerUrl}", guild.getBannerUrl() == null ? "No Banner" : guild.getBannerUrl())
                    .replace("{guildOwnerTag}", guild.getOwner().getUser().getAsTag())
                    .replace("{guildOwnerMention}", guild.getOwner().getUser().getAsMention())
                    .replace("{guildOwnerID}", guild.getOwner().getUser().getId())
                    .replace("{guildOwnerAvatarUrl}", guild.getOwner().getUser().getEffectiveAvatarUrl())
                    .replace("{guildOwnerName}", guild.getOwner().getUser().getName());
        }

        //Self User
        message = message
                .replace("{botTag}", jda.getSelfUser().getAsTag())
                .replace("{botMention}", jda.getSelfUser().getAsMention())
                .replace("{botAvatarUL}", jda.getSelfUser().getEffectiveAvatarUrl())
                .replace("{botID}", jda.getSelfUser().getId())
                .replace("{botName}", jda.getSelfUser().getName());

        //General
        message = message
                .replace("{DiscordBotAuthor}", "i0#0001")
                .replace("{DiscordBotPluginMode}", InitializeBot.isPluginMode() ? "Yes" : "No")
                .replace("{DiscordBotPrefix}", GlobalConfig.GENERAL_BOT_PREFIX)
                .replace("{DiscordBotVersion}", "2.1.3");

        //plugin mode
        if (InitializeBot.isPluginMode()) {
            message = message
                    .replace("{serverTPS}", Lag.getTPS() + "")
                    .replace("{serverOnlineCount}", DiscordBot.get().getServer().getOnlinePlayers().size() + "");
        }

        return message;
    }
}
