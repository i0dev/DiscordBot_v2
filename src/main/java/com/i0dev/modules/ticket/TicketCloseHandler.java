package com.i0dev.modules.ticket;

import com.i0dev.Engine;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.TicketEngine;
import com.i0dev.object.objects.LogObject;
import com.i0dev.object.objects.Ticket;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TicketCloseHandler extends ListenerAdapter {

    public static String Identifier = "Ticket Admin Only React";
    public static boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ticketClose.requirePermission");
    public static boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ticketClose.permissionLiteMode");
    public static String MESSAGE_CONTENT = Configuration.getString("commands.ticketClose.messageContent");
    public static boolean EVENT_ENABLED = Configuration.getBoolean("commands.ticketClose.enabled");
    public static String MESSAGE_TICKET_LOG = Configuration.getString("commands.ticketClose.ticketLogsMessage");
    public static String MESSAGE_TICKET_LOG_TITLE = Configuration.getString("commands.ticketClose.ticketLogsTitle");
    public static String TICKET_LOGS_ID = Configuration.getString("channels.ticketLogsID");
    public static String ADMIN_LOGS_ID = Configuration.getString("channels.ticketAdminLogsID");
    public static long delayToCloseTicketMilis = Configuration.getLong("commands.ticketClose.delayToCloseTicketMilis");

    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (!e.getButton().getId().equalsIgnoreCase("BUTTON_TICKET_CLOSE")) return;
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!FormatUtil.isValidGuild(e.getGuild())) return;
        if (DPlayerEngine.getObject(e.getUser().getIdLong()).isBlacklisted()) return;
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        if (!TicketEngine.getInstance().isOnList(e.getChannel().getIdLong())) return;
        closeTicket(TicketEngine.getInstance().getObject(e.getChannel().getIdLong()), Configuration.getString("commands.ticketClose.defaultCloseReason"), e.getUser());
        e.deferEdit().queue();
    }


    static void closeTicket(Ticket ticket, String reason, User closer) {

        File ticketLogsFile = new File(InitializeBot.get().getTicketLogsDirPath() + "/" + ticket.getChannelID() + ".log");
        TextChannel ticketChannel = InternalJDA.getJda().getTextChannelById(ticket.getChannelID());
        String toFile = "\n\nClosed Ticket Information:\n " +
                "  Ticket Closer Tag: " + closer.getAsTag() + "\n" +
                "   Ticket Closer ID: " + closer.getId() + "\n" +
                "   Ticket Close reason: " + reason;
        Engine.getToLog().add(new LogObject(toFile, ticketLogsFile));
        Engine.taskAppendToFile.run();
        ticketLogsFile = new File(InitializeBot.get().getTicketLogsDirPath() + "/" + ticket.getChannelID() + ".log");
        User ticketOwner = InternalJDA.getJda().getUserById(ticket.getTicketOwnerID());
        TicketEngine.getInstance().remove(ticket);
        DPlayerEngine.increment(closer.getIdLong(), "ticketsclosed");
        ticketChannel.delete().queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
        ticketChannel.sendMessage(EmbedFactory.createEmbed(null, Placeholders.convert(MESSAGE_CONTENT.replace("{channelName}", ticketChannel.getName()), closer), null).build()).queue();

        EmbedBuilder embedBuilder = EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, ticketOwner, closer), Placeholders.convert(MESSAGE_TICKET_LOG
                        .replace("{reason}", reason)
                        .replace("{channelName}", ticketChannel.getName())
                , ticketOwner, closer), null, ticketOwner == null ? GlobalConfig.EMBED_THUMBNAIL : ticketOwner.getEffectiveAvatarUrl());

        try {
            if (ticket.isAdminOnlyMode()) {
                InternalJDA.getJda().getTextChannelById(ADMIN_LOGS_ID).sendMessage(embedBuilder.build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
                InternalJDA.getJda().getTextChannelById(ADMIN_LOGS_ID).sendFile(ticketLogsFile).queueAfter(delayToCloseTicketMilis + 1000, TimeUnit.MILLISECONDS);
            } else {
                InternalJDA.getJda().getTextChannelById(TICKET_LOGS_ID).sendMessage(embedBuilder.build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
                InternalJDA.getJda().getTextChannelById(TICKET_LOGS_ID).sendFile(ticketLogsFile).queueAfter(delayToCloseTicketMilis + 1000, TimeUnit.MILLISECONDS);
            }

            try {
                ticketOwner.openPrivateChannel().complete().sendMessage(embedBuilder.build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
                ticketOwner.openPrivateChannel().complete().sendFile(ticketLogsFile).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
            } catch (Exception ignored) {

            }
        } catch (Exception ignored) {

        }
    }
}
