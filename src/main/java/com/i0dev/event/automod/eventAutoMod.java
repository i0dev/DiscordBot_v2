package main.java.com.i0dev.event.automod;

import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class eventAutoMod extends ListenerAdapter {

    private final String Identifier = "Auto Mod";
    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("events.automod.enabled");
    private final List<Long> automodChannels = getConfig.get().getLongList("events.automod.automodChannels");
    private final List<String> blacklistedWords = getConfig.get().getStringList("events.automod.blacklistedWords");

    private final boolean logEnabled = getConfig.get().getBoolean("events.automod.logs");
    private final boolean channelWhitelistEnabled = getConfig.get().getBoolean("events.automod.channelWhitelistMode");
    private final String logTitle = getConfig.get().getString("events.automod.logTitle");
    private final String logContent = getConfig.get().getString("events.automod.logContent");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (!EVENT_ENABLED) return;
        if (channelWhitelistEnabled) {
            if (!MessageAliases.isChannelInList(e.getChannel(), automodChannels)) return;
        }
        String messageContent = e.getMessage().getContentRaw();
        for (String word : blacklistedWords) {
            if (messageContent.contains(word)) {
                e.getMessage().delete().queue();
                if (logEnabled) {
                    MessageUtil.sendMessage(conf.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.get()
                            .createSimpleEmbed(Placeholders.convert(logTitle,
                                    e.getAuthor()), Placeholders.convert(logContent.replace("{message}", messageContent),
                                    e.getAuthor())).build());
                }
                break;
            }
        }
    }
}