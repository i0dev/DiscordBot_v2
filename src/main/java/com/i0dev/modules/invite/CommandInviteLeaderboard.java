package com.i0dev.modules.invite;


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

public class CommandInviteLeaderboard extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static boolean COMMAND_ENABLED;
    public static String MESSAGE_FORMAT;

    public static String messageTitle;
    public static String leaderboardFormat;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.inviteLeaderboard.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.inviteLeaderboard.permissionLiteMode");
        messageTitle = Configuration.getString("commands.inviteLeaderboard.messageTitle");
        leaderboardFormat = Configuration.getString("commands.inviteLeaderboard.leaderboardFormat");
        MESSAGE_FORMAT = Configuration.getString("commands.inviteLeaderboard.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.inviteLeaderboard.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Invite Leaderboard")) {
            return;
        }

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.INVITE_LEADERBOARD_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        List<Object> list = DPlayerEngine.getCache();
        List<DPlayer> discPlayers = new ArrayList<>();
        for (Object o : list) {
            DPlayer dPlayer = ((DPlayer) o);
            discPlayers.add(dPlayer);
        }
        discPlayers.sort(Comparator.comparing(DPlayer::getInviteCount));
        Collections.reverse(discPlayers);
        StringBuilder desc = new StringBuilder();
        int maxEntries = 0;
        for (DPlayer obj : discPlayers) {
            if (maxEntries >= 25) {
                break;
            }
            User user = e.getJDA().getUserById(obj.getDiscordID());
            if (user == null) continue;
            desc.append(Placeholders.convert(leaderboardFormat
                    .replace("{place}", (discPlayers.indexOf(obj) + 1) + ""), null, user));
            maxEntries++;
        }
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(messageTitle, e.getAuthor()), Placeholders.convert(desc.toString(), e.getAuthor())).build()).queue();
    }

}
