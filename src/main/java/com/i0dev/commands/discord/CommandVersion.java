package com.i0dev.commands.discord;

import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandVersion extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            if (e.getMessage().getMentionedUsers().size() > 0 && e.getMessage().getMentionedUsers().get(0).equals(e.getGuild().getJDA().getSelfUser())) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed(null, Placeholders.convert("**Prefix:** `{DiscordBotPrefix}`\n**Version:** `{DiscordBotVersion}`\n**Author:** `{DiscordBotAuthor}`\n**Plugin Mode:** `{DiscordBotPluginMode}`"), "Bot created by i0dev.com").build()).queue();
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            } else if (e.getMessage().getContentRaw().equals(GlobalConfig.GENERAL_BOT_PREFIX + "version")) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed(null, Placeholders.convert("**Prefix:** `{DiscordBotPrefix}`\n**Version:** `{DiscordBotVersion}`\n**Author:** `{DiscordBotAuthor}`\n**Plugin Mode:** `{DiscordBotPluginMode}`"), "Bot created by i0dev.com").build()).queue();
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            } else if (e.getMessage().getContentRaw().equals(GlobalConfig.GENERAL_BOT_PREFIX + "botVersion")) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed(null, Placeholders.convert("**Prefix:** `{DiscordBotPrefix}`\n**Version:** `{DiscordBotVersion}`\n**Author:** `{DiscordBotAuthor}`\n**Plugin Mode:** `{DiscordBotPluginMode}`"), "Bot created by i0dev.com").build()).queue();
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            }
        }
    }
}
