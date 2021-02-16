package com.i0dev.command.verify;

import com.i0dev.entity.Blacklist;
import com.i0dev.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.FileReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class cmdVerifyPanel extends ListenerAdapter {

    private final String Identifier = "Send Verify Panel";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.createVerifyPanel.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.createVerifyPanel.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.createVerifyPanel.permissionLiteMode");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.createVerifyPanel.format");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.createVerifyPanel.messageTitle");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.createVerifyPanel.messageContent");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.createVerifyPanel.enabled");
    private final boolean PIN_MESSAGE = getConfig.get().getBoolean("commands.createVerifyPanel.pinVerifyPanel");
    private final String VERIFY_EMOJI = getConfig.get().getString("commands.createVerifyPanel.verifyEmoji");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
            if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            if (!COMMAND_ENABLED) {

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            EmbedBuilder Embed = new EmbedBuilder()
                    .setTitle(MESSAGE_TITLE)
                    .setThumbnail(conf.EMBED_THUMBNAIL)
                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setFooter(conf.EMBED_FOOTER)
                    .setDescription(MESSAGE_CONTENT)
                    .setTimestamp(ZonedDateTime.now());

            Message PanelMessage = e.getChannel().sendMessage(Embed.build()).complete();
            if (PIN_MESSAGE) PanelMessage.pin().queue();
            String Emoji = getEmojiWithoutArrow(VERIFY_EMOJI);
            PanelMessage.addReaction(Emoji).queue();

        }
    }

    private String getEmojiWithoutArrow(String emoji) {
        String EmojiWithoutArrow;
        if (emoji.length() < 3) {
            EmojiWithoutArrow = emoji;
        } else {
            EmojiWithoutArrow = emoji.substring(0, emoji.length() - 1);
        }
        return EmojiWithoutArrow;
    }
}
