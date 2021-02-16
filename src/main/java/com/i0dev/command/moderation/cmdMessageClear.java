package com.i0dev.command.moderation;

import com.i0dev.entity.Blacklist;
import com.i0dev.util.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class cmdMessageClear extends ListenerAdapter {

    private final String Identifier = "Clear Messages";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.messageClear.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.messageClear.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.messageClear.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.messageClear.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.messageClear.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.messageClear.enabled");
    private final boolean LOGS_ENABLED = getConfig.get().getBoolean("commands.messageClear.log");
    private final String LOGS_MESSAGE = getConfig.get().getString("commands.messageClear.logMessage");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (!COMMAND_ENABLED) {
                if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            for (int i = 0; i < 20; i++) {
                List<Message> messages = e.getChannel().getHistory().retrievePast(100).complete();
                e.getChannel().purgeMessages(messages);
            }

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{messages}", message[1]), e.getAuthor())).build()).queue();

            if (LOGS_ENABLED) {
                conf.GENERAL_MAIN_LOGS_CHANNEL.sendMessage(EmbedFactory.get().createSimpleEmbed(LOGS_MESSAGE
                        .replace("{userTag}", e.getAuthor().getAsTag())
                        .replace("{channel}", e.getChannel().getAsMention()))
                        .build()).queue();
            }
        }
    }
}
