package com.i0dev.modules.blacklist;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Retrieve extends DiscordCommand {

    private static boolean PERMISSION_STRICT;
    private static boolean PERMISSION_LITE;
    private static boolean PERMISSION_ADMIN;
    private static boolean ENABLED;
    private static String MESSAGE_CONTENT;
    private static String MESSAGE_FORMAT;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.blacklist.parts.list.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.blacklist.parts.list.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.blacklist.parts.list.permission.admin");
        ENABLED = Configuration.getBoolean("modules.blacklist.parts.list.enabled");
        MESSAGE_CONTENT = Configuration.getString("modules.blacklist.parts.list.message.general");
        MESSAGE_FORMAT = Configuration.getString("modules.blacklist.parts.list.message.format");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Blacklist List")) {
            return;
        }

        List<String> listString = new ArrayList<>();
        for (Object obj : DPlayerEngine.getCache()) {
            DPlayer dPlayer = (DPlayer) obj;
            if (dPlayer.isBlacklisted()) {
                listString.add(MESSAGE_FORMAT
                        .replace("{authorTag}", dPlayer.getCachedData().getDiscordTag()));
            }
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{list}", FormatUtil.FormatListString(listString)), e.getAuthor());

    }
}
