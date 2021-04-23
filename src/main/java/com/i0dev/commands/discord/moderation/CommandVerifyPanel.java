package com.i0dev.commands.discord.moderation;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.EmojiUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.time.ZonedDateTime;

public class CommandVerifyPanel {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.createVerifyPanel.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.createVerifyPanel.permissionLiteMode");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.createVerifyPanel.format");
    public static final String MESSAGE_TITLE = Configuration.getString("commands.createVerifyPanel.messageTitle");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.createVerifyPanel.messageContent");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.createVerifyPanel.enabled");
    public static final boolean PIN_MESSAGE = Configuration.getBoolean("commands.createVerifyPanel.pinVerifyPanel");
    public static final String VERIFY_EMOJI = Configuration.getString("commands.createVerifyPanel.verifyEmoji");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Create Verify Panel")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {

            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.VERIFY_PANEL_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        EmbedBuilder Embed = new EmbedBuilder()
                .setTitle(MESSAGE_TITLE)
                .setThumbnail(GlobalConfig.EMBED_THUMBNAIL.equals("") ? null : GlobalConfig.EMBED_THUMBNAIL)
                .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                .setFooter(GlobalConfig.EMBED_FOOTER)
                .setDescription(MESSAGE_CONTENT)
                .setTimestamp(ZonedDateTime.now());

        Message PanelMessage = e.getChannel().sendMessage(Embed.build()).complete();
        if (PIN_MESSAGE) PanelMessage.pin().queue();
        String Emoji = EmojiUtil.getEmojiWithoutArrow(VERIFY_EMOJI);
        PanelMessage.addReaction(Emoji).queue();


    }
}
