package com.i0dev.commands.discord.completedModules.screenshare;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.engines.ScreenshareEngine;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Clear {

    private static final String Identifier = "Screenshare Clear";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.screenshare.parts.clear.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.screenshare.parts.clear.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.screenshare.parts.clear.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.screenshare.parts.clear.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.screenshare.parts.clear.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.screenshare.parts.clear.message.logMessage");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.screenshare.parts.clear.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Screenshare Clear")) {
            return;
        }

        ScreenshareEngine.getInstance().clear();

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());
        if (OPTION_LOG) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_LOG_MESSAGE, e.getAuthor());
        }

    }
}
