package com.i0dev.modules.ticket;


import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommandTicketTopLeaderboard extends DiscordCommand {
    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String messageTitle;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ticketTopLeaderboard.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ticketTopLeaderboard.permissionLiteMode");
        messageTitle = Configuration.getString("commands.ticketTopLeaderboard.messageTitle");
        MESSAGE_FORMAT = Configuration.getString("commands.ticketTopLeaderboard.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.ticketTopLeaderboard.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Ticket Top Leaderboard")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.TICKET_TOP_LEADERBOARD_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        List<Object> list = DPlayerEngine.getCache();
        List<DPlayer> discPlayers = new ArrayList<>();
        for (Object o : list) {
            DPlayer dPlayer = ((DPlayer) o);
            discPlayers.add(dPlayer);
        }
        discPlayers.sort(Comparator.comparing(DPlayer::getTicketsClosed));
        Collections.reverse(list);
        StringBuilder desc = new StringBuilder();
        for (DPlayer obj : discPlayers) {
            User user = e.getJDA().getUserById(obj.getDiscordID());
            if (user == null) continue;
            desc.append(Placeholders.convert(MESSAGE_FORMAT
                    .replace("{place}", (discPlayers.indexOf(obj) + 1) + ""), null, user));
        }


        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(messageTitle, e.getAuthor()), Placeholders.convert(desc.toString(), e.getAuthor())).build()).queue();
    }
}
