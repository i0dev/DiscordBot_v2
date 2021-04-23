package com.i0dev.commands.discord.completedModules.suggestion;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Suggestion;
import com.i0dev.object.engines.SuggestionEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Retrieve {


    private static final String Identifier = "Suggestion List";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.suggestion.parts.list.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.suggestion.parts.list.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.suggestion.parts.list.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.suggestion.parts.list.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.suggestion.parts.list.message.general");
    private static final String MESSAGE_FORMAT = Configuration.getString("modules.suggestion.parts.list.message.format");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Suggestion List")) {
            return;
        }

        List<String> stringList = new ArrayList<>();

        for (Object singleton : SuggestionEngine.getInstance().getCache()) {
            Suggestion suggestion = (Suggestion) singleton;
            stringList.add(MESSAGE_FORMAT
                    .replace("{messageID}", suggestion.getMessageID().toString())
            );
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{list}", FormatUtil.FormatListString(stringList)), e.getAuthor());
    }
}
