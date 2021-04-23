package com.i0dev.commands.discord.completedModules.suggestion;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Suggestion;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.EmojiUtil;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Add {

    private static final String Identifier = "Suggestion Add";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.suggestion.parts.add.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.suggestion.parts.add.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.suggestion.parts.add.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.suggestion.parts.add.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.suggestion.parts.add.message.general");
    private static final String MESSAGE_SUGGESTION_MESSAGE = Configuration.getString("modules.suggestion.parts.add.message.suggestionMessage");
    private static final String MESSAGE_SUGGESTION_TITLE = Configuration.getString("modules.suggestion.parts.add.message.suggestionTitle");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Suggestion Add")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), SuggestionManager.accept, e.getAuthor());
            return;
        }
        String suggestion = FormatUtil.remainingArgFormatter(message, 2);

        Message suggestionMsg = MessageUtil.sendMessage(SuggestionManager.CHANNEL_SUGGESTION_CHANNEL_ID, MESSAGE_SUGGESTION_MESSAGE.replace("{suggestion}", suggestion), MESSAGE_SUGGESTION_TITLE, e.getAuthor(), null);
        suggestionMsg.addReaction(EmojiUtil.getEmojiWithoutArrow(SuggestionManager.UP_VOTE_EMOJI)).queue();
        suggestionMsg.addReaction(EmojiUtil.getEmojiWithoutArrow(SuggestionManager.DOWN_VOTE_EMOJI)).queue();

        Suggestion suggestionObject = new Suggestion();
        suggestionObject.setSuggestion(suggestion);
        suggestionObject.setUserID(e.getAuthor().getIdLong());
        suggestionObject.setMessageID(suggestionMsg.getIdLong());
        suggestionObject.setChannelID(SuggestionManager.CHANNEL_SUGGESTION_CHANNEL_ID);
        suggestionObject.setUserTag(e.getAuthor().getAsTag());
        suggestionObject.setUserAvatarUrl(e.getAuthor().getEffectiveAvatarUrl());
        suggestionObject.addToCache();

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{suggestion}", suggestion), e.getAuthor());

    }
}
