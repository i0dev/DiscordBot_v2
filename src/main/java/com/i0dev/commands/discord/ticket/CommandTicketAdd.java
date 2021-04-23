package com.i0dev.commands.discord.ticket;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.engines.TicketEngine;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class CommandTicketAdd extends ListenerAdapter {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ticketAdd.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ticketAdd.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.ticketAdd.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.ticketAdd.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.ticketAdd.enabled");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Ticket Add")) {
            return;
        }
        if (!TicketEngine.getInstance().isOnList(e.getChannel())) return;

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1 || message.length > 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.TICKET_ADD_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }

        e.getChannel().putPermissionOverride(Objects.requireNonNull(e.getGuild().getMember(MentionedUser)))
                .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                        Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                .queue();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, MentionedUser, e.getAuthor())).build()).queue();

    }
}