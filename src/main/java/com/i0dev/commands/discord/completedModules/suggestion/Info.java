package com.i0dev.commands.discord.completedModules.suggestion;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Suggestion;
import com.i0dev.object.engines.SuggestionEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Info {
    private static final String Identifier = "Suggestion Info";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.suggestion.parts.info.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.suggestion.parts.info.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.suggestion.parts.info.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.suggestion.parts.info.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.suggestion.parts.info.message.general");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Suggestion Info")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), SuggestionManager.info, e.getAuthor());
            return;
        }
        String suggestionID = message[2];
        Message suggestionMsg = FindFromString.get().getMessage(suggestionID, SuggestionManager.CHANNEL_SUGGESTION_CHANNEL_ID);
        if (suggestionMsg == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), SuggestionManager.MESSAGE_CANT_FIND, e.getAuthor());
            return;
        }

        Suggestion suggestion = SuggestionEngine.getInstance().getObject(suggestionMsg);

        String desc = MESSAGE_CONTENT
                .replace("{suggestion}", suggestion.getSuggestion())
                .replace("{suggesterTag}", suggestion.getUserTag())
                .replace("{channelID}", suggestion.getChannelID().toString())
                .replace("{messageID}", suggestion.getMessageID().toString())
                .replace("{accepted}", suggestion.isAccepted() + "")
                .replace("{rejected}", suggestion.isRejected() + "")
                .replace("{suggestionLink}", ("https://discordapp.com/channels/" + GlobalConfig.GENERAL_MAIN_GUILD.getId() + "/" + suggestion.getChannelID() + "/" + suggestion.getMessageID()))
                .replace("{suggesterID}", suggestion.getUserID().toString());


        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());
    }
}
