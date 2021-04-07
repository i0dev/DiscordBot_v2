package com.i0dev.command.discord.moderation;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;

import com.i0dev.utility.util.EmojiUtil;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;

public class CmdAcceptSuggestion extends ListenerAdapter {

    private final String Identifier = "Accept Suggest";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.acceptSuggestion.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.acceptSuggestion.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.acceptSuggestion.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.acceptSuggestion.messageTitle");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.acceptSuggestion.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.acceptSuggestion.enabled");
    private final String suggestionChannelID = getConfig.get().getString("channels.suggestionChannelID");
    private final String cantFindSuggestion = getConfig.get().getString("commands.acceptSuggestion.cantFindSuggestion");
    private final Long acceptedSuggestionChannelID = getConfig.get().getLong("channels.acceptedSuggestionChannelID");
    private final String acceptedSuggestionEmbedColor = getConfig.get().getString("commands.acceptSuggestion.acceptedSuggestionEmbedColor");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length == 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            Message suggestionMsg = FindFromString.get().getMessage(message[1], e.getJDA().getTextChannelById(suggestionChannelID));
            if (suggestionMsg == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(cantFindSuggestion.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }
            String note = FormatUtil.remainingArgFormatter(message, 2);
            String desc = suggestionMsg.getEmbeds().get(0).getDescription();
            if (!"".equals(note)) {
                desc = desc + "\n\n**Note:** `" + note + "`";
            }
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(Placeholders.convert(MESSAGE_TITLE, e.getAuthor()))
                    .setColor(Color.decode(acceptedSuggestionEmbedColor))
                    .setFooter(GlobalConfig.EMBED_FOOTER)
                    .setTimestamp(ZonedDateTime.now())
                    .setThumbnail(e.getAuthor().getEffectiveAvatarUrl())
                    .setDescription(desc);
            MessageUtil.sendMessage(acceptedSuggestionChannelID, builder.build());

        }
    }
}