package com.i0dev.commands.discord.completedModules.blacklist;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Retrieve {


    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.blacklist.parts.list.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.blacklist.parts.list.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.blacklist.parts.list.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.blacklist.parts.list.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.blacklist.parts.list.message.general");
    private static final String MESSAGE_FORMAT = Configuration.getString("modules.blacklist.parts.list.message.format");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, false), "Blacklist List")) {
            return;
        }

        List<String> listString = new ArrayList<>();
        for (Object obj : DPlayerEngine.getInstance().getCache()) {
            DPlayer dPlayer = (DPlayer) obj;
            if (dPlayer.isBlacklisted()) {
                listString.add(MESSAGE_FORMAT
                        .replace("{authorTag}", dPlayer.getCachedData().getDiscordTag()));
            }
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{list}", FormatUtil.FormatListString(listString)), e.getAuthor());

    }
}
