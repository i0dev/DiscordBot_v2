package com.i0dev.event.discord.suggestion;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.getConfig;
import com.i0dev.utility.util.EmojiUtil;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;

public class EventAcceptSuggestion extends ListenerAdapter {
    private final String Identifier = "Accept Suggestion";
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.acceptSuggestion.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.acceptSuggestion.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.acceptSuggestion.messageTitle");
    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("commands.acceptSuggestion.enabled");
    private final String suggestionChannelID = getConfig.get().getString("channels.suggestionChannelID");
    private final Long acceptedSuggestionChannelID = getConfig.get().getLong("channels.acceptedSuggestionChannelID");
    private final String acceptedSuggestionEmbedColor = getConfig.get().getString("commands.acceptSuggestion.acceptedSuggestionEmbedColor");
    private final String acceptSuggestionEmoji = getConfig.get().getString("commands.acceptSuggestion.acceptSuggestionEmoji");

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        if (!e.getChannel().getId().equalsIgnoreCase(suggestionChannelID)) return;
        String Emoji = EmojiUtil.getSimpleEmoji(acceptSuggestionEmoji);
        if (e.getReactionEmote().isEmoji()) {
            if (!EmojiUtil.getUnicodeFromCodepoints(e.getReactionEmote().getAsCodepoints()).equalsIgnoreCase(Emoji))
                return;
        } else {
            if (!e.getReactionEmote().getName().equalsIgnoreCase(acceptSuggestionEmoji)) return;
        }


        Message suggestionMsg = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
        String desc = suggestionMsg.getEmbeds().get(0).getDescription();
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(Placeholders.convert(MESSAGE_TITLE, e.getUser()))
                .setColor(Color.decode(acceptedSuggestionEmbedColor))
                .setFooter(GlobalConfig.EMBED_FOOTER)
                .setTimestamp(ZonedDateTime.now())
                .setThumbnail(e.getUser().getEffectiveAvatarUrl())
                .setDescription(desc);
        MessageUtil.sendMessage(acceptedSuggestionChannelID, builder.build());


    }

}
