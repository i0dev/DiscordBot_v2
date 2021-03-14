package main.java.com.i0dev.event.ticket;

import com.sun.istack.internal.NotNull;
import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.entity.Ticket;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class eventCloseTicket extends ListenerAdapter {

    private final String Identifier = "Ticket Admin Only React";
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketClose.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketClose.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.ticketClose.messageContent");
    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("commands.ticketClose.enabled");
    private final String closeTicketEmoji = getConfig.get().getString("events.event_ticketCreate.closeTicketEmoji");
    private final String MESSAGE_TICKET_LOG = getConfig.get().getString("commands.ticketClose.ticketLogsMessage");
    private final String MESSAGE_TICKET_LOG_TITLE = getConfig.get().getString("commands.ticketClose.ticketLogsTitle");
    private final String TICKET_LOGS_ID = getConfig.get().getString("channels.ticketLogsID");
    private final String ADMIN_LOGS_ID = getConfig.get().getString("channels.ticketAdminLogsID");
    private final long delayToCloseTicketMilis = getConfig.get().getLong("commands.ticketClose.delayToCloseTicketMilis");


    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;
        if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        if (!Ticket.get().isTicket(e.getChannel())) return;
        String Emoji = EmojiUtil.getSimpleEmoji(closeTicketEmoji);
        if (e.getReactionEmote().isEmoji()) {
            if (!EmojiUtil.getUnicodeFromCodepoints(e.getReactionEmote().getAsCodepoints()).equalsIgnoreCase(Emoji))
                return;

        } else {
            if (!e.getReactionEmote().getName().equalsIgnoreCase(closeTicketEmoji)) return;
        }
        e.getChannel().removeReactionById(e.getMessageId(), EmojiUtil.getEmojiWithoutArrow(Emoji), e.getUser()).queue();

        JSONObject ticketObject = Ticket.get().getTicket(e.getChannel());
        String reason = Prettify.ticketRemainingArgFormatter(null, 4);
        File file = Logs.getLogsFile(e.getChannel());
        User TicketUser = e.getGuild().getJDA().getUserById(ticketObject.get("ticketOwnerID").toString());

        try {
            Files.write(Paths.get(file.getAbsolutePath()), Logs.getLogContents(e.getChannel().getHistoryFromBeginning(100).complete(), ticketObject, e.getChannel(), e.getUser(), reason).getBytes());
        } catch (IOException ignored) {
        }

        Ticket.get().deleteTicket(e.getChannel().getId());
        e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(null, Placeholders.convert(MESSAGE_CONTENT.replace("{channelName}", e.getChannel().getName()), e.getUser()), null).build()).queue();

        if (TicketUser != null) {
            try {
                TicketUser.openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, TicketUser), Placeholders.convert(MESSAGE_TICKET_LOG.replace("{channelName}", e.getChannel().getName()).replace("{reason}", reason), TicketUser), TicketUser.getEffectiveAvatarUrl()).build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
                TicketUser.openPrivateChannel().complete().sendFile(file).queueAfter(delayToCloseTicketMilis+ 1000, TimeUnit.MILLISECONDS);
            } catch (Exception ignored) {
            }
        }
        if (!(boolean) ticketObject.get("adminOnlyMode")) {
            e.getGuild().getTextChannelById(TICKET_LOGS_ID).sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, TicketUser), Placeholders.convert(MESSAGE_TICKET_LOG.replace("{channelName}", e.getChannel().getName()).replace("{reason}", reason), e.getUser()), TicketUser.getEffectiveAvatarUrl()).build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
            e.getGuild().getTextChannelById(TICKET_LOGS_ID).sendFile(file).queueAfter(delayToCloseTicketMilis + 1000, TimeUnit.MILLISECONDS);

        } else {
            e.getGuild().getTextChannelById(ADMIN_LOGS_ID).sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TICKET_LOG_TITLE, TicketUser), Placeholders.convert(MESSAGE_TICKET_LOG.replace("{channelName}", e.getChannel().getName()).replace("{reason}", reason), e.getUser()), TicketUser.getEffectiveAvatarUrl()).build()).queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);
            e.getGuild().getTextChannelById(ADMIN_LOGS_ID).sendFile(file).queueAfter(delayToCloseTicketMilis + 1000, TimeUnit.MILLISECONDS);

        }

        e.getChannel().delete().queueAfter(delayToCloseTicketMilis, TimeUnit.MILLISECONDS);


    }


}
