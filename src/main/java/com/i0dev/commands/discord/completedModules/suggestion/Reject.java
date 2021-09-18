package com.i0dev.commands.discord.completedModules.suggestion;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Suggestion;
import com.i0dev.object.engines.SuggestionEngine;
import com.i0dev.utility.*;
import com.i0dev.utility.util.EmojiUtil;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;

public class Reject extends ListenerAdapter {
    private static final String Identifier = "Suggestion Reject";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.suggestion.parts.reject.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.suggestion.parts.reject.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.suggestion.parts.reject.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.suggestion.parts.reject.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.suggestion.parts.reject.message.general");
    private static final String MESSAGE_REJECTED_MESSAGE = Configuration.getString("modules.suggestion.parts.reject.message.rejectedMessage");
    private static final String MESSAGE_REJECTED_TITLE = Configuration.getString("modules.suggestion.parts.reject.message.rejectedTitle");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Suggestion Reject")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), SuggestionManager.reject, e.getAuthor());
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

        String note = FormatUtil.remainingArgFormatter(message, 3);
        if ("".equalsIgnoreCase(note)) {
            note = "Nothing Provided";
        }

        String icon = GlobalConfig.EMBED_THUMBNAIL;
        if (Configuration.getBoolean("modules.suggestion.parts.reject.useSuggesterIcon")) {
            icon = suggestion.getUserAvatarUrl();
        }

        String rejectedDesc = MESSAGE_REJECTED_MESSAGE
                .replace("{suggestionUserTag}", suggestion.getUserTag())
                .replace("{suggestion}", suggestion.getSuggestion())
                .replace("{note}", note);

        EmbedBuilder embedFactory = new EmbedBuilder()
                .setTitle(Placeholders.convert(MESSAGE_REJECTED_TITLE, e.getAuthor()))
                .setTimestamp(ZonedDateTime.now())
                .setDescription(Placeholders.convert(rejectedDesc, e.getAuthor()))
                .setFooter(GlobalConfig.EMBED_FOOTER)
                .setThumbnail(icon)
                .setColor(Color.decode(SuggestionManager.REJECT_COLOR));

        MessageUtil.sendMessage(SuggestionManager.CHANNEL_REJECTED_SUGGESTION_CHANNEL_ID, embedFactory.build());

        String desc = MESSAGE_CONTENT
                .replace("{suggestionUserTag}", suggestion.getUserTag())
                .replace("{suggestion}", suggestion.getSuggestion())
                .replace("{note}", note);

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());
        SuggestionEngine.getInstance().setAccepted(suggestion);
    }


    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (!GlobalCheck.check(e, Identifier, ENABLED, PERMISSION_STRICT, PERMISSION_LITE, PERMISSION_ADMIN)) {
            return;
        }
        if (e.getChannel().getIdLong() != SuggestionManager.CHANNEL_SUGGESTION_CHANNEL_ID) return;
        String Emoji = EmojiUtil.getSimpleEmoji(SuggestionManager.REJECT_EMOJI);

        if (e.getReactionEmote().isEmoji()) {
            if (!EmojiUtil.getUnicodeFromCodepoints(e.getReactionEmote().getAsCodepoints()).equalsIgnoreCase(Emoji))
                return;
        } else {
            if (!e.getReactionEmote().getName().equalsIgnoreCase(SuggestionManager.REJECT_EMOJI)) return;
        }

        Message suggestionMsg = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
        Suggestion suggestion = SuggestionEngine.getInstance().getObject(suggestionMsg);
        SuggestionEngine.getInstance().remove(suggestion);
        suggestionMsg.delete().queue();

        String note = "Nothing Provided";

        String icon = GlobalConfig.EMBED_THUMBNAIL;
        if (Configuration.getBoolean("modules.suggestion.parts.reject.useSuggesterIcon")) {
            icon = suggestion.getUserAvatarUrl();
        }

        String Desc = MESSAGE_REJECTED_MESSAGE
                .replace("{suggestionUserTag}", suggestion.getUserTag())
                .replace("{suggestion}", suggestion.getSuggestion())
                .replace("{note}", note);

        EmbedBuilder embedFactory = new EmbedBuilder()
                .setTitle(Placeholders.convert(MESSAGE_REJECTED_TITLE, e.getUser()))
                .setTimestamp(ZonedDateTime.now())
                .setDescription(Placeholders.convert(Desc, e.getUser()))
                .setFooter(GlobalConfig.EMBED_FOOTER)
                .setThumbnail(icon)
                .setColor(Color.decode(SuggestionManager.REJECT_COLOR));

        MessageUtil.sendMessage(SuggestionManager.CHANNEL_REJECTED_SUGGESTION_CHANNEL_ID, embedFactory.build());

        SuggestionEngine.getInstance().setAccepted(suggestion);

    }
}