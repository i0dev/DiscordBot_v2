package com.i0dev.commands.discord.completedModules.screenshare;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Screenshare;
import com.i0dev.object.engines.ScreenshareEngine;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Retrieve {

    private static final String Identifier = "Screenshare List";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.screenshare.parts.list.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.screenshare.parts.list.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.screenshare.parts.list.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.screenshare.parts.list.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.screenshare.parts.list.message.general");
    private static final String MESSAGE_FORMAT = Configuration.getString("modules.screenshare.parts.list.message.format");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Screenshare List")) {
            return;
        }

        List<String> listString = new ArrayList<>();
        for (Object obj : ScreenshareEngine.getInstance().getCache()) {
            Screenshare screenshare = (Screenshare) obj;
            listString.add(MESSAGE_FORMAT
                    .replace("{IGN}", screenshare.getIgn())
                    .replace("{authorTag}", screenshare.getPunisherTag())
                    .replace("{reason}", screenshare.getReason()));
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{list}", FormatUtil.FormatListString(listString)), e.getAuthor());

    }
}
