package com.i0dev.modules.ticket;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.engines.TicketEngine;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandTicketClose extends DiscordCommand {
    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static boolean COMMAND_ENABLED;
    public static String MESSAGE_TICKET_LOG;
    public static String MESSAGE_TICKET_LOG_TITLE;
    public static String TICKET_LOGS_ID;
    public static String ADMIN_LOGS_ID;
    public static long delayToCloseTicketMilis;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ticketClose.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ticketClose.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.ticketClose.messageContent");
        COMMAND_ENABLED = Configuration.getBoolean("commands.ticketClose.enabled");
        MESSAGE_TICKET_LOG = Configuration.getString("commands.ticketClose.ticketLogsMessage");
        MESSAGE_TICKET_LOG_TITLE = Configuration.getString("commands.ticketClose.ticketLogsTitle");
        TICKET_LOGS_ID = Configuration.getString("channels.ticketLogsID");
        ADMIN_LOGS_ID = Configuration.getString("channels.ticketAdminLogsID");
        delayToCloseTicketMilis = Configuration.getLong("commands.ticketClose.delayToCloseTicketMilis");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Ticket Close")) {
            return;
        }
        if (!TicketEngine.getInstance().isOnList(e.getChannel().getIdLong())) return;
        String[] message = e.getMessage().getContentRaw().split(" ");
        TicketCloseHandler.closeTicket(TicketEngine.getInstance().getObject(e.getChannel().getIdLong()), FormatUtil.ticketRemainingArgFormatter(message, 1), e.getAuthor());
    }
}
