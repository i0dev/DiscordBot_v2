package main.java.com.i0dev.command;

import main.java.com.i0dev.DiscordBotPlugin;
import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.conf;
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
                .setTitle(conf.EMBED_TITLE)
                .setThumbnail(conf.EMBED_THUMBNAIL)
                .setDescription("**Prefix:** `" + conf.GENERAL_BOT_PREFIX + "`\n" + "**Version:** " + "`2.0.18`" + "\n**Author:** `i0#0001`")
                .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                .setTimestamp(ZonedDateTime.now())
                .setFooter("Bot created by i0dev.com", "https://cdn.discordapp.com/attachments/763790150550683708/780593953824964628/i01.png");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            if (e.getMessage().getMentionedUsers().size() > 0 && e.getMessage().getMentionedUsers().get(0).equals(e.getGuild().getJDA().getSelfUser())) {
                e.getChannel().sendMessage(versionEmbed.build()).queue();
                if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            } else if (e.getMessage().getContentRaw().equals(conf.GENERAL_BOT_PREFIX + "version")) {
                e.getChannel().sendMessage(versionEmbed.build()).queue();
                if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            } else if (e.getMessage().getContentRaw().equals(conf.GENERAL_BOT_PREFIX + "botVersion")) {
                e.getChannel().sendMessage(versionEmbed.build()).queue();
                if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            }
        }
    }
}
