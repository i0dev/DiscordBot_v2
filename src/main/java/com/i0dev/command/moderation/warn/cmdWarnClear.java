package main.java.com.i0dev.command.moderation.warn;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.entity.Screenshare;
import main.java.com.i0dev.entity.Warning;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdWarnClear extends ListenerAdapter {

    private final String Identifier = "Warns Clear";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.warns_clear.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.warns_clear.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.warns_clear.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.warns_clear.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.warns_clear.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.warns_clear.enabled");
    private final boolean LOGS_ENABLED = getConfig.get().getBoolean("commands.warns_clear.log");
    private final String LOGS_MESSAGE = getConfig.get().getString("commands.warns_clear.logMessage");


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
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            Warning.get().wipeCache();

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();
            if (LOGS_ENABLED) {
                MessageUtil.sendMessage(conf.GENERAL_MAIN_LOGS_CHANNEL,EmbedFactory.get().createSimpleEmbed(Placeholders.convert(LOGS_MESSAGE, e.getAuthor())).build());
            }
        }
    }
}
