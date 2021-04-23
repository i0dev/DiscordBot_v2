package com.i0dev.commands.discord.completedModules.suggestion;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.engines.SuggestionEngine;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Clear {

    private static final String Identifier = "Suggestion Clear";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.suggestion.parts.clear.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.suggestion.parts.clear.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.suggestion.parts.clear.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.suggestion.parts.clear.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.suggestion.parts.clear.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.suggestion.parts.clear.message.logMessage");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.suggestion.parts.clear.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Suggestion Clear")) {
            return;
        }

        SuggestionEngine.getInstance().clear();

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());
        if (OPTION_LOG) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_LOG_MESSAGE, e.getAuthor());
        }

    }
}
