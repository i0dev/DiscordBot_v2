package com.i0dev.commands.discord.moderation;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.engine.discord.RoleQueue;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.RoleUtil;
import com.i0dev.utility.util.TimeUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandRoleAll {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.role_all.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.role_all.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.role_all.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.role_all.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.role_all.enabled");
    public static final String noUsersToGive = Configuration.getString("commands.role_all.noUsersToGive");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Role All")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.ROLE_ALL_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        Role MentionedRole = FindFromString.get().getRole(message[1], e.getMessage());

        if (MentionedRole == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_ROLE_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }
        if (RoleUtil.getUsersWhoNeedRole(MentionedRole) == 0) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(noUsersToGive.replace("{roleName}", MentionedRole.getName()), e.getAuthor())).build()).queue();
            return;
        }

        String description = MESSAGE_CONTENT
                .replace("{roleName}", MentionedRole.getName())
                .replace("{userCount}", RoleUtil.getUsersWhoNeedRole(MentionedRole) + "")
                .replace("{time}", TimeUtil.formatPlayTime((2L * (RoleUtil.getUsersWhoNeedRole(MentionedRole) + RoleQueue.getQueue().size())) * 1000));

        RoleUtil.giveRolesUsersLongs(MentionedRole, RoleUtil.getUserListWhoNeedRole(MentionedRole));

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(description, e.getAuthor())).build()).queue();

    }
}
