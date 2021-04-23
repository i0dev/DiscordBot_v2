package com.i0dev.commands.discord.completedModules.warn;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Remove {
    private static final String Identifier = "Warn Remove";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.warn.parts.remove.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.warn.parts.remove.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.warn.parts.remove.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.warn.parts.remove.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.warn.parts.remove.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.warn.parts.remove.message.logMessage");
    private static final String MESSAGE_NO_WARNS = Configuration.getString("modules.warn.parts.remove.message.noWarns");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.warn.parts.remove.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Warn Remove")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), WarnManager.remove, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }

        if (DPlayerEngine.getInstance().getObject(MentionedUser).getWarnCount() == 0) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_NO_WARNS, e.getAuthor(), MentionedUser);
            return;
        }

        DPlayerEngine.getInstance().decreaseWarn(MentionedUser);

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor(), MentionedUser);

        if (OPTION_LOG) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, MESSAGE_LOG_MESSAGE, e.getAuthor(), MentionedUser);
        }
    }
}