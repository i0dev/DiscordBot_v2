package com.i0dev.command.discord.moderation;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.PermissionUtil;


import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdChangelog extends ListenerAdapter {

    private final String Identifier = "Changelog";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.changelog.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.changelog.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.changelog.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.changelog.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.changelog.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.changelog.enabled");
    private final boolean showThumbnail = getConfig.get().getBoolean("commands.changelog.showThumbnail");
    private final String changelogTitle = getConfig.get().getString("commands.changelog.changelogTitle");


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
            if (message.length <= 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            TextChannel Channel = e.getJDA().getTextChannelById(GlobalConfig.CHANGELOG_CHANNEL);
            String content = FormatUtil.remainingArgFormatter(message, 1);
            Channel.sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(changelogTitle, e.getAuthor()), content, showThumbnail ? GlobalConfig.EMBED_THUMBNAIL : null).build()).queue();
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{channel}", Channel.getAsMention()), e.getAuthor())).build()).queue();


        }
    }
}