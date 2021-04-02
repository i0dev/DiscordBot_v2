package main.java.com.i0dev.command.discord.verify;

import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.object.Blacklist;

import main.java.com.i0dev.utility.util.EmojiUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;

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
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            if (!COMMAND_ENABLED) {

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            EmbedBuilder Embed = new EmbedBuilder()
                    .setTitle(MESSAGE_TITLE)
                    .setThumbnail(GlobalConfig.EMBED_THUMBNAIL)
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
}
