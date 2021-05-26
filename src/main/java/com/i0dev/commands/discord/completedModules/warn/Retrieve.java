package com.i0dev.commands.discord.completedModules.warn;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
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

public class Retrieve {

    private static final String Identifier = "Warns List";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.warn.parts.list.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.warn.parts.list.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.warn.parts.list.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.warn.parts.list.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.warn.parts.list.message.general");
    private static final String MESSAGE_FORMAT = Configuration.getString("modules.warn.parts.list.message.format");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Warn List")) {
            return;
        }

        List<Object> list = DPlayerEngine.getCache();
        List<DPlayer> discPlayers = new ArrayList<>();
        for (Object o : list) {
            DPlayer dPlayer = ((DPlayer) o);
            discPlayers.add(dPlayer);
        }
        discPlayers.sort(Comparator.comparing(DPlayer::getWarnCount));
        Collections.reverse(list);
        List<String> listString = new ArrayList<>();
        int count = 0;
        for (DPlayer obj : discPlayers) {
            count++;
            User user = e.getJDA().getUserById(obj.getDiscordID());
            if (user == null) continue;
            listString.add(Placeholders.convert(MESSAGE_FORMAT
                    .replace("{place}", (discPlayers.indexOf(obj) + 1) + ""), null, user));
            if (count >= 10) {
                break;
            }
        }

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{list}", FormatUtil.FormatListString(listString)), e.getAuthor());

    }
}
