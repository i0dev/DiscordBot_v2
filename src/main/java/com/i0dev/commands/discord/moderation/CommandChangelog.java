package com.i0dev.commands.discord.moderation;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandChangelog {

    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.changelog.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.changelog.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.changelog.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.changelog.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.changelog.enabled");
    public static final boolean showThumbnail = Configuration.getBoolean("commands.changelog.showThumbnail");
    public static final String changelogTitle = Configuration.getString("commands.changelog.changelogTitle");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Changelog")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length < 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.CHANGELOG_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        TextChannel Channel = e.getJDA().getTextChannelById(GlobalConfig.CHANGELOG_CHANNEL);
        String content = FormatUtil.remainingArgFormatter(message, 1);
        Channel.sendMessage(EmbedFactory.createEmbed(Placeholders.convert(changelogTitle, e.getAuthor()), content, showThumbnail ? GlobalConfig.EMBED_THUMBNAIL.equals("") ? null : GlobalConfig.EMBED_THUMBNAIL : null).build()).queue();
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{channel}", Channel.getAsMention()), e.getAuthor())).build()).queue();


    }
}
