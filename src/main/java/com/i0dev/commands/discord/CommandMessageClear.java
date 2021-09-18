package com.i0dev.commands.discord;

import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class CommandMessageClear extends ListenerAdapter {

    public static final String Identifier = "Clear Messages";
    private final List<String> COMMAND_ALIASES = Configuration.getStringList("commands.messageClear.aliases");
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.messageClear.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.messageClear.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.messageClear.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.messageClear.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.messageClear.enabled");
    public static final boolean LOGS_ENABLED = Configuration.getBoolean("commands.messageClear.log");
    public static final String LOGS_MESSAGE = Configuration.getString("commands.messageClear.logMessage");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!FormatUtil.isValidGuild(e.getGuild())) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (DPlayerEngine.getObject(e.getAuthor().getIdLong()).isBlacklisted()) return;

            if (!COMMAND_ENABLED) {
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            for (int i = 0; i < 20; i++) {
                List<Message> messages = e.getChannel().getHistory().retrievePast(100).complete();
                e.getChannel().purgeMessages(messages);
            }

            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();

            if (LOGS_ENABLED) {
                MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(LOGS_MESSAGE
                        .replace("{userTag}", e.getAuthor().getAsTag())
                        .replace("{channel}", e.getChannel().getAsMention()))
                        .build());
            }
        }
    }
}
