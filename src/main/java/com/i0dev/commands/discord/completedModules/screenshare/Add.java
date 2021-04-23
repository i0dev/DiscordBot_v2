package com.i0dev.commands.discord.completedModules.screenshare;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Screenshare;
import com.i0dev.object.engines.ScreenshareEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Add {

    private static final String Identifier = "Screenshare Add";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.screenshare.parts.add.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.screenshare.parts.add.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.screenshare.parts.add.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.screenshare.parts.add.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.screenshare.parts.add.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.screenshare.parts.add.message.logMessage");
    private static final String MESSAGE_ALREADY_ON_LIST = Configuration.getString("modules.screenshare.parts.add.message.alreadyOnList");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.screenshare.parts.add.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Screenshare Add")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), ScreenshareManager.add, e.getAuthor());
            return;
        }
        String reason = FormatUtil.remainingArgFormatter(message, 3);

        String ign = message[2];

        if (ScreenshareEngine.getInstance().isOnList(ign)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_ALREADY_ON_LIST.replace("{IGN}", ign), e.getAuthor());
            return;
        }

        Screenshare screenshare = new Screenshare();
        screenshare.setIgn(ign);
        screenshare.setPunisherID(e.getAuthor().getIdLong());
        screenshare.setPunisherTag(e.getAuthor().getAsTag());
        screenshare.setReason(reason);
        screenshare.addToCache();


        String desc = MESSAGE_CONTENT
                .replace("{IGN}", ign)
                .replace("{reason}", reason);

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());

        if (OPTION_LOG) {
            String logMsg = MESSAGE_LOG_MESSAGE
                    .replace("{IGN}", ign)
                    .replace("{reason}", reason);
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, logMsg, e.getAuthor());

        }
    }
}
