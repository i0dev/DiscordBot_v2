package com.i0dev.engine.discord.automod;

import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class eventAutoMod extends ListenerAdapter {

    public static final String Identifier = "Auto Mod";
    public static final boolean EVENT_ENABLED = Configuration.getBoolean("events.automod.enabled");
    private final List<Long> automodChannels = Configuration.getLongList("events.automod.automodChannels");
    private final List<String> blacklistedWords = Configuration.getStringList("events.automod.blacklistedWords");

    public static final boolean logEnabled = Configuration.getBoolean("events.automod.logs");
    public static final boolean channelWhitelistEnabled = Configuration.getBoolean("events.automod.channelWhitelistMode");
    public static final String logTitle = Configuration.getString("events.automod.logTitle");
    public static final String logContent = Configuration.getString("events.automod.logContent");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (!EVENT_ENABLED) return;
        if (channelWhitelistEnabled) {
            if (!MessageAliases.isChannelInList(e.getChannel(), automodChannels)) return;
        }
        String messageContent = e.getMessage().getContentRaw();
        for (String word : blacklistedWords) {
            if (messageContent.contains(word)) {
                e.getMessage().delete().queue();
                if (logEnabled) {
                    MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(Placeholders.convert(logTitle,
                            e.getAuthor()), Placeholders.convert(logContent.replace("{message}", messageContent),
                            e.getAuthor())).build());
                }
                break;
            }
        }
    }
}