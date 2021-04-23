package com.i0dev.commands.discord.ticket;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Ticket;
import com.i0dev.object.engines.TicketEngine;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandTicketRename {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ticketRename.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ticketRename.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.ticketRename.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.ticketRename.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.ticketRename.enabled");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Ticket Rename")) {
            return;
        }
        if (!TicketEngine.getInstance().isOnList(e.getChannel())) return;

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.TICKET_RENAME_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        Ticket ticketObject = TicketEngine.getInstance().getObject(e.getChannel());
        String newTicketName = FormatUtil.remainingArgFormatter(message, 1).replace(" ", "-") + "-" + ticketObject.getTicketNumber();
        e.getChannel().getManager().setName(newTicketName).queue();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{newName}", newTicketName), e.getAuthor())).build()).queue();

    }
}
