package com.i0dev.commands.discord.basic;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandProfile {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.profile.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.profile.permissionLiteMode");
    public static final String MESSAGE_TITLE = Configuration.getString("commands.profile.messageTitle");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.profile.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.profile.enabled");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Profile")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length > 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.PROFILE_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = null;
        if (message.length == 2) {
            MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }
        } else if (message.length == 1) {
            MentionedUser = e.getAuthor();
        }

        StringBuilder desc = new StringBuilder();
        desc.append("**User Information:** ").append("`{authorTag} ({authorID})`").append("\n");
        desc.append("**Total Tickets Closed:** ").append("`{authorTicketsClosed}`").append("\n");
        desc.append("**Total Invites:** ").append("`{authorInviteCount}`").append("\n");
        desc.append("**Total Warnings:** ").append("`{authorWarnCount}`").append("\n");
        desc.append("**Is Blacklisted:** ").append("`{authorIsBlacklisted}`").append("\n");
        desc.append("**Invited By:** ").append("`{authorInvitedByTag}`").append("\n");
        desc.append("**Linked Status:** ").append("`{authorLinkStatus}`").append("\n");
        desc.append("**Linked IGN:** ").append("`{authorLinkedIGN}`").append("\n");
        desc.append("**Boost Count:** ").append("`{authorBoostCount}`").append("\n");
        desc.append("**Points:** ").append("`{authorPointsCount}`").append("\n");

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_TITLE, null, MentionedUser), Placeholders.convert(desc.toString(), null, MentionedUser), null, MentionedUser.getEffectiveAvatarUrl()).build()).queue();

    }

}