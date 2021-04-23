package com.i0dev.commands.discord.points;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Leaderboard {

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.points.parts.leaderboard.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.points.parts.leaderboard.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.points.parts.leaderboard.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.points.parts.leaderboard.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.points.parts.leaderboard.message.general");
    private static final String MESSAGE_FORMAT = Configuration.getString("modules.points.parts.leaderboard.message.format");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Points Leaderboard")) {
            return;
        }
        List<Object> list = DPlayerEngine.getInstance().getCache();
        List<DPlayer> discPlayers = new ArrayList<>();
        for (Object o : list) {
            DPlayer dPlayer = ((DPlayer) o);
            discPlayers.add(dPlayer);
        }
        discPlayers.sort(Comparator.comparing(DPlayer::getPoints));
        Collections.reverse(discPlayers);
        List<String> listString = new ArrayList<>();
        int count = 0;
        for (DPlayer obj : discPlayers) {
            User user = e.getJDA().getUserById(obj.getDiscordID());
            if (user == null) continue;
            if (DPlayerEngine.getInstance().getObject(user).getPoints() == 0) continue;
            count++;
            listString.add(Placeholders.convert(MESSAGE_FORMAT
                    .replace("{place}", count + ""), user));
            if (count >= 30) {
                break;
            }
        }

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{list}", FormatUtil.FormatListString(listString)), e.getAuthor());
    }
}
