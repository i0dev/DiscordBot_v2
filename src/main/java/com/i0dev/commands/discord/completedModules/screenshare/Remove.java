package com.i0dev.commands.discord.completedModules.screenshare;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.engines.ScreenshareEngine;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Remove {
    private static final String Identifier = "Screenshare Remove";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.screenshare.parts.remove.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.screenshare.parts.remove.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.screenshare.parts.remove.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.screenshare.parts.remove.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.screenshare.parts.remove.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.screenshare.parts.remove.message.logMessage");
    private static final String MESSAGE_NOT_ON_LIST = Configuration.getString("modules.screenshare.parts.remove.message.notOnList");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.screenshare.parts.remove.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Screenshare Remove")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), ScreenshareManager.remove, e.getAuthor());
            return;
        }
        String ign = message[2];

        if (!ScreenshareEngine.getInstance().isOnList(ign)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_NOT_ON_LIST.replace("{IGN}", ign), e.getAuthor());
            return;
        }

        ScreenshareEngine.getInstance().remove(ign);


        String desc = MESSAGE_CONTENT
                .replace("{IGN}", ign);

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());

        if (OPTION_LOG) {
            String logMsg = MESSAGE_LOG_MESSAGE
                    .replace("{punisherTag}", e.getAuthor().getAsTag())
                    .replace("{IGN}", ign);
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, logMsg, e.getAuthor());

        }
    }
}