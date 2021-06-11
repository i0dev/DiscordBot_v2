package com.i0dev.modules.ticket;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.engines.TicketEngine;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.object.objects.Ticket;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandTicketInfo extends DiscordCommand {
    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ticketInfo.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ticketInfo.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.ticketInfo.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.ticketInfo.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.ticketInfo.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Ticket Info")) {
            return;
        }
        if (!TicketEngine.getInstance().isOnList(e.getChannel().getIdLong())) return;

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.TICKET_INFO_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        Ticket ticketObject = TicketEngine.getInstance().getObject(e.getChannel().getIdLong());

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT
                .replace("{channelName}", e.getChannel().getName())
                .replace("{ownersTag}", ticketObject.getTicketOwnerTag())
                .replace("{channelID}", ticketObject.getChannelID().toString())
                .replace("{adminOnly}", ticketObject.isAdminOnlyMode() + ""), e.getAuthor())).build()).queue();

    }
}
