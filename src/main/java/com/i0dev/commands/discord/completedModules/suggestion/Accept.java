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

public class Accept extends ListenerAdapter {
    private static final String Identifier = "Suggestion Accept";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.suggestion.parts.accept.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.suggestion.parts.accept.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.suggestion.parts.accept.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.suggestion.parts.accept.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.suggestion.parts.accept.message.general");
    private static final String MESSAGE_ACCEPTED_MESSAGE = Configuration.getString("modules.suggestion.parts.accept.message.acceptedMessage");
    private static final String MESSAGE_ACCEPTED_TITLE = Configuration.getString("modules.suggestion.parts.accept.message.acceptedTitle");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Suggestion Accept")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), SuggestionManager.accept, e.getAuthor());
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


        String acceptedDesc = MESSAGE_ACCEPTED_MESSAGE
                .replace("{suggestionUserTag}", suggestion.getUserTag())
                .replace("{suggestion}", suggestion.getSuggestion())
                .replace("{note}", note);

        String icon = GlobalConfig.EMBED_THUMBNAIL;
        if (Configuration.getBoolean("modules.suggestion.parts.accept.useSuggesterIcon")) {
            icon = suggestion.getUserAvatarUrl();
        }

        EmbedBuilder embedFactory = new EmbedBuilder()
                .setTitle(Placeholders.convert(MESSAGE_ACCEPTED_TITLE, e.getAuthor()))
                .setTimestamp(ZonedDateTime.now())
                .setDescription(Placeholders.convert(acceptedDesc, e.getAuthor()))
                .setFooter(GlobalConfig.EMBED_FOOTER)
                .setThumbnail(icon)
                .setColor(Color.decode(SuggestionManager.ACCEPT_COLOR));

        MessageUtil.sendMessage(SuggestionManager.CHANNEL_ACCEPTED_SUGGESTION_CHANNEL_ID, embedFactory.build());

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
        String Emoji = EmojiUtil.getSimpleEmoji(SuggestionManager.ACCEPT_EMOJI);

        if (e.getReactionEmote().isEmoji()) {
            if (!EmojiUtil.getUnicodeFromCodepoints(e.getReactionEmote().getAsCodepoints()).equalsIgnoreCase(Emoji))
                return;
        } else {
            if (!e.getReactionEmote().getName().equalsIgnoreCase(SuggestionManager.ACCEPT_EMOJI)) return;
        }

        Message suggestionMsg = e.getChannel().retrieveMessageById(e.getMessageId()).complete();


        Suggestion suggestion = SuggestionEngine.getInstance().getObject(suggestionMsg);
        SuggestionEngine.getInstance().remove(suggestion);
        suggestionMsg.delete().queue();

        String note = "Nothing Provided";

        String icon = GlobalConfig.EMBED_THUMBNAIL;
        if (Configuration.getBoolean("modules.suggestion.parts.accept.useSuggesterIcon")) {
            icon = suggestion.getUserAvatarUrl();
        }

        String acceptedDesc = MESSAGE_ACCEPTED_MESSAGE
                .replace("{suggestionUserTag}", suggestion.getUserTag())
                .replace("{suggestion}", suggestion.getSuggestion())
                .replace("{note}", note);

        EmbedBuilder embedFactory = new EmbedBuilder()
                .setTitle(Placeholders.convert(MESSAGE_ACCEPTED_TITLE, e.getUser()))
                .setTimestamp(ZonedDateTime.now())
                .setDescription(Placeholders.convert(acceptedDesc, e.getUser()))
                .setFooter(GlobalConfig.EMBED_FOOTER)
                .setThumbnail(icon)
                .setColor(Color.decode(SuggestionManager.ACCEPT_COLOR));

        MessageUtil.sendMessage(SuggestionManager.CHANNEL_ACCEPTED_SUGGESTION_CHANNEL_ID, embedFactory.build());

        SuggestionEngine.getInstance().setAccepted(suggestion);


    }

}