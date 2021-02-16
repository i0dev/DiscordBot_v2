package main.java.com.i0dev.command.ticket;

import main.java.com.i0dev.util.*;
import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.entity.Ticket;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class cmdTicketClose extends ListenerAdapter {

    private final String Identifier = "Ticket Close";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.ticketClose.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketClose.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketClose.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.ticketClose.messageContent");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.ticketClose.enabled");
    private final String MESSAGE_TICKET_LOG = getConfig.get().getString("commands.ticketClose.ticketLogsMessage");
    private final String MESSAGE_TICKET_LOG_TITLE = getConfig.get().getString("commands.ticketClose.ticketLogsTitle");
    private final String TICKET_LOGS_ID = getConfig.get().getString("channels.ticketLogsID");
    private final String ADMIN_LOGS_ID = getConfig.get().getString("channels.ticketAdminLogsID");
    private final long delayToCloseTicketMilis = getConfig.get().getLong("commands.ticketClose.delayToCloseTicketMilis");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (!Ticket.get().isTicket(e.getChannel())) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
            if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            JSONObject ticketObject = Ticket.get().getTicket(e.getChannel());
            String reason = Prettify.ticketRemainingArgFormatter(message, 1);
            File file = Logs.getLogsFile(e.getChannel());
            User TicketUser = e.getGuild().getJDA().getUserById(ticketObject.get("ticketOwnerID").toString());

            try {
                Files.write(Paths.get(file.getAbsolutePath()), Logs.getLogContents(e.getChannel().getHistoryFromBeginning(100).complete(), ticketObject, e.getChannel(), e.getAuthor(), reason).getBytes());
            } catch (IOException ignored) {
            }

            Ticket.get().deleteTicket(e.getChannel().getId());
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{channelName}", e.getChannel().getName()), e.getAuthor()), null, null).build()).queue();

            if (TicketUser != null) {
                try {
                    TicketUser.openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, TicketUser), Placeholders.convert(MESSAGE_TICKET_LOG.replace("{channelName}", e.getChannel().getName()).replace("{reason}", reason), TicketUser), TicketUser.getAvatarUrl()).build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
                    TicketUser.openPrivateChannel().complete().sendFile(file).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
                } catch (Exception ignored) {
                }
            }
            if (!(boolean) ticketObject.get("adminOnlyMode")) {
                e.getGuild().getTextChannelById(TICKET_LOGS_ID).sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, TicketUser), Placeholders.convert(MESSAGE_TICKET_LOG.replace("{channelName}", e.getChannel().getName()).replace("{reason}", reason), TicketUser), TicketUser.getAvatarUrl()).build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
            } else {
                e.getGuild().getTextChannelById(ADMIN_LOGS_ID).sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, TicketUser), Placeholders.convert(MESSAGE_TICKET_LOG.replace("{channelName}", e.getChannel().getName()).replace("{reason}", reason), TicketUser), TicketUser.getAvatarUrl()).build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
            }
            e.getChannel().delete().queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
        }
    }
}
