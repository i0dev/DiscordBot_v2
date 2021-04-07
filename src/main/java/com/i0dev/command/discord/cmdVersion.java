package com.i0dev.command.discord;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.GlobalConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;

public class cmdVersion extends ListenerAdapter {

    private final String Identifier = "Version";
    private EmbedBuilder versionEmbed;

    public cmdVersion() {
        versionEmbed = new EmbedBuilder()
                .setTitle(GlobalConfig.EMBED_TITLE)
                .setThumbnail(GlobalConfig.EMBED_THUMBNAIL)
                .setDescription(Placeholders.convert("**Prefix:** `{DiscordBotPrefix}`\n**Version:** `{DiscordBotVersion}`\n**Author:** `{DiscordBotAuthor}`\n**Plugin Mode:** `{DiscordBotPluginMode}`"))
                .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                .setTimestamp(ZonedDateTime.now())
                .setFooter("Bot created by i0dev.com", "https://cdn.discordapp.com/attachments/763790150550683708/780593953824964628/i01.png");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            if (e.getMessage().getMentionedUsers().size() > 0 && e.getMessage().getMentionedUsers().get(0).equals(e.getGuild().getJDA().getSelfUser())) {
                e.getChannel().sendMessage(versionEmbed.build()).queue();
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            } else if (e.getMessage().getContentRaw().equals(GlobalConfig.GENERAL_BOT_PREFIX + "version")) {
                e.getChannel().sendMessage(versionEmbed.build()).queue();
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            } else if (e.getMessage().getContentRaw().equals(GlobalConfig.GENERAL_BOT_PREFIX + "botVersion")) {
                e.getChannel().sendMessage(versionEmbed.build()).queue();
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            }
        }
    }
}
