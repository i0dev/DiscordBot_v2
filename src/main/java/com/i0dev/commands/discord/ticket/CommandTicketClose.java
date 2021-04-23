package com.i0dev.commands.discord.ticket;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Ticket;
import com.i0dev.object.engines.TicketEngine;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.LogsUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class CommandTicketClose {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ticketClose.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ticketClose.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.ticketClose.messageContent");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.ticketClose.enabled");
    public static final String MESSAGE_TICKET_LOG = Configuration.getString("commands.ticketClose.ticketLogsMessage");
    public static final String MESSAGE_TICKET_LOG_TITLE = Configuration.getString("commands.ticketClose.ticketLogsTitle");
    public static final String TICKET_LOGS_ID = Configuration.getString("channels.ticketLogsID");
    public static final String ADMIN_LOGS_ID = Configuration.getString("channels.ticketAdminLogsID");
    private static final long delayToCloseTicketMilis = Configuration.getLong("commands.ticketClose.delayToCloseTicketMilis");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Ticket Close")) {
            return;
        }
        if (!TicketEngine.getInstance().isOnList(e.getChannel())) return;

        String[] message = e.getMessage().getContentRaw().split(" ");
        Ticket ticketObject = TicketEngine.getInstance().getObject(e.getChannel());
        String reason = FormatUtil.ticketRemainingArgFormatter(message, 1);
        File file = LogsUtil.getLogsFile(e.getChannel());
        User TicketUser = e.getGuild().getJDA().getUserById(ticketObject.getTicketOwnerID());

        try {
            Files.write(Paths.get(file.getAbsolutePath()), LogsUtil.getLogContents(e.getChannel().getHistoryFromBeginning(100).complete(), ticketObject, e.getChannel(), e.getAuthor(), reason).getBytes());
        } catch (IOException ignored) {
        }

        TicketEngine.getInstance().remove(e.getChannel());
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{channelName}", e.getChannel().getName()), e.getAuthor()), null, null).build()).queue();

        if (TicketUser != null) {
            try {
                TicketUser.openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, TicketUser, e.getAuthor()), Placeholders.convert(MESSAGE_TICKET_LOG.replace("{channelName}", e.getChannel().getName()).replace("{reason}", reason), e.getAuthor()), TicketUser.getEffectiveAvatarUrl()).build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
                TicketUser.openPrivateChannel().complete().sendFile(file).queueAfter(delayToCloseTicketMilis + 1000, TimeUnit.MILLISECONDS);
            } catch (Exception ignored) {
            }
        }
        if (!ticketObject.isAdminOnlyMode()) {
            e.getGuild().getTextChannelById(TICKET_LOGS_ID).sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, TicketUser, e.getAuthor()), Placeholders.convert(MESSAGE_TICKET_LOG.replace("{channelName}", e.getChannel().getName()).replace("{reason}", reason), e.getAuthor()), TicketUser.getEffectiveAvatarUrl()).build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
            e.getGuild().getTextChannelById(TICKET_LOGS_ID).sendFile(file).queueAfter(delayToCloseTicketMilis + 1000, TimeUnit.MILLISECONDS);

        } else {
            e.getGuild().getTextChannelById(ADMIN_LOGS_ID).sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, TicketUser, e.getAuthor()), Placeholders.convert(MESSAGE_TICKET_LOG.replace("{channelName}", e.getChannel().getName()).replace("{reason}", reason), e.getAuthor()), TicketUser.getEffectiveAvatarUrl()).build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
            e.getGuild().getTextChannelById(ADMIN_LOGS_ID).sendFile(file).queueAfter(delayToCloseTicketMilis + 1000, TimeUnit.MILLISECONDS);

        }
        DPlayerEngine.getInstance().increaseTicketsClosed(e.getAuthor());

        e.getChannel().delete().queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
    }
}
