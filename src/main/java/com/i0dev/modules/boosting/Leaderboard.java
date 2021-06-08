package com.i0dev.modules.boosting;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
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

public class Leaderboard extends DiscordCommand {
    public static boolean PERMISSION_STRICT;
    public static boolean PERMISSION_LITE;
    public static boolean PERMISSION_ADMIN;
    public static boolean ENABLED;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;


    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.boosting.parts.leaderboard.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.boosting.parts.leaderboard.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.boosting.parts.leaderboard.permission.admin");
        ENABLED = Configuration.getBoolean("modules.boosting.parts.leaderboard.enabled");
        MESSAGE_CONTENT = Configuration.getString("modules.boosting.parts.leaderboard.message.general");
        MESSAGE_FORMAT = Configuration.getString("modules.boosting.parts.leaderboard.message.format");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Boosting Leaderboard")) {
            return;
        }
        List<Object> list = DPlayerEngine.getCache();
        List<DPlayer> discPlayers = new ArrayList<>();
        for (Object o : list) {
            DPlayer dPlayer = ((DPlayer) o);
            discPlayers.add(dPlayer);
        }
        discPlayers.sort(Comparator.comparing(DPlayer::getBoostCount));
        Collections.reverse(discPlayers);
        List<String> listString = new ArrayList<>();
        int count = 0;
        for (DPlayer obj : discPlayers) {
            User user = e.getJDA().getUserById(obj.getDiscordID());
            if (user == null) continue;
            if (DPlayerEngine.getObject(user.getIdLong()).getPoints() == 0) continue;
            count++;
            if (obj.getBoostCount() == 0) continue;
            listString.add(Placeholders.convert(MESSAGE_FORMAT
                    .replace("{place}", count + ""), user));
            if (count >= 30) {
                break;
            }
        }

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{list}", FormatUtil.FormatListString(listString)), e.getAuthor());
    }


}
