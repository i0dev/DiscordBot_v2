package com.i0dev.commands.discord.completedModules.warn;

import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Add {

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.warn.parts.add.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.warn.parts.add.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.warn.parts.add.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.warn.parts.add.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.warn.parts.add.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.warn.parts.add.message.logMessage");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.warn.parts.add.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Warn Add")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), WarnManager.add, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }

        String reason = FormatUtil.remainingArgFormatter(message, 3);

        DPlayerEngine.increment(MentionedUser.getIdLong(), "warns");

        String desc = MESSAGE_CONTENT
                .replace("{reason}", reason);

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor(), MentionedUser);

        if (OPTION_LOG) {
            String logMsg = MESSAGE_LOG_MESSAGE
                    .replace("{reason}", reason);
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, logMsg, e.getAuthor(), MentionedUser);

        }
    }
}
