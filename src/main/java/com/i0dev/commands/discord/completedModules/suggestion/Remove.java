package com.i0dev.commands.discord.completedModules.suggestion;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Suggestion;
import com.i0dev.object.engines.SuggestionEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Remove {
    private static final String Identifier = "Suggestion Remove";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.suggestion.parts.remove.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.suggestion.parts.remove.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.suggestion.parts.remove.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.suggestion.parts.remove.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.suggestion.parts.remove.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.suggestion.parts.remove.message.logMessage");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.suggestion.parts.remove.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Suggestion Add")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), SuggestionManager.remove, e.getAuthor());
            return;
        }
        String suggestionID = message[2];
        Message suggestionMsg = FindFromString.get().getMessage(suggestionID, SuggestionManager.CHANNEL_SUGGESTION_CHANNEL_ID);
        if (suggestionMsg == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), SuggestionManager.MESSAGE_CANT_FIND, e.getAuthor());
            return;
        }
        Suggestion suggestion = SuggestionEngine.getInstance().getObject(suggestionMsg);
        SuggestionEngine.getInstance().remove(suggestion);
        suggestionMsg.delete().queue();

        String desc = MESSAGE_CONTENT
                .replace("{suggestionUserTag}", suggestion.getUserTag())
                .replace("{suggestion}", suggestion.getSuggestion());
        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());


        if (OPTION_LOG) {
            String logMsg = MESSAGE_LOG_MESSAGE
                    .replace("{suggestionUserTag}", suggestion.getUserTag())
                    .replace("{suggestion}", suggestion.getSuggestion());
            MessageUtil.sendMessage(e.getChannel().getIdLong(), logMsg, e.getAuthor());
        }
    }
}