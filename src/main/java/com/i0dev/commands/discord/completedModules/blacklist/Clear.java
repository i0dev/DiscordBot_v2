package com.i0dev.commands.discord.completedModules.blacklist;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Clear {

    private static final String Identifier = "Blacklist Clear";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.blacklist.parts.clear.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.blacklist.parts.clear.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.blacklist.parts.clear.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.blacklist.parts.clear.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.blacklist.parts.clear.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.blacklist.parts.clear.message.logMessage");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.blacklist.parts.clear.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Blacklist Clear")) {
            return;
        }
        DPlayerEngine.getInstance().clearBlacklist();

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());
        if (OPTION_LOG) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, MESSAGE_LOG_MESSAGE, e.getAuthor());
        }

    }
}
