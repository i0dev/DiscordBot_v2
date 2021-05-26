package com.i0dev.modules.blacklist;

import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Clear extends DiscordCommand {

    private static boolean PERMISSION_STRICT;
    private static boolean PERMISSION_LITE;
    private static boolean PERMISSION_ADMIN;
    private static boolean ENABLED;
    private static String MESSAGE_CONTENT;
    private static String MESSAGE_LOG_MESSAGE;
    private static boolean OPTION_LOG;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.blacklist.parts.clear.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.blacklist.parts.clear.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.blacklist.parts.clear.permission.admin");
        ENABLED = Configuration.getBoolean("modules.blacklist.parts.clear.enabled");
        MESSAGE_CONTENT = Configuration.getString("modules.blacklist.parts.clear.message.general");
        MESSAGE_LOG_MESSAGE = Configuration.getString("modules.blacklist.parts.clear.message.logMessage");
        OPTION_LOG = Configuration.getBoolean("modules.blacklist.parts.clear.option.log");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Blacklist Clear")) {
            return;
        }
        DPlayerEngine.clear("blacklist");

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());
        if (OPTION_LOG) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, MESSAGE_LOG_MESSAGE, e.getAuthor());
        }

    }
}
