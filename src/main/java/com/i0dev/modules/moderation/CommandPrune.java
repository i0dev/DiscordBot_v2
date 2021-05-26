package com.i0dev.modules.moderation;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CommandPrune extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_CONTENT_USER;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;
    public static boolean LOGS_ENABLED;
    public static String LOGS_MESSAGE;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.prune.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.prune.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.prune.messageContent");
        MESSAGE_CONTENT_USER = Configuration.getString("commands.prune.messageContentUser");
        MESSAGE_FORMAT = Configuration.getString("commands.prune.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.prune.enabled");
        LOGS_ENABLED = Configuration.getBoolean("commands.prune.log");
        LOGS_MESSAGE = Configuration.getString("commands.prune.logMessage");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Prune Messages")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length > 3 || message.length == 1) {
            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.PRUNE_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        if (message.length == 2) {
            try {
                Integer.parseInt(message[1]);
            } catch (Exception ignored) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.PRUNE_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            for (Message messageToDelete : e.getChannel().getHistory().retrievePast(Integer.parseInt(message[1]) + 1).complete()) {
                if (messageToDelete == null) continue;
                try {
                    messageToDelete.delete().queue();
                } catch (Exception ignored) {

                }
            }
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{messages}", message[1]), e.getAuthor())).build()).queue(self -> {

                try {
                    self.delete().queueAfter(3, TimeUnit.SECONDS);
                } catch (Exception ignored) {

                }
            });


            if (LOGS_ENABLED) {
                MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, (EmbedFactory.createEmbed(Placeholders.convert(LOGS_MESSAGE
                        .replace("{messages}", message[1])
                        .replace("{channel}", e.getChannel().getAsMention()), e.getAuthor()))
                        .build()));
            }

        } else {
            try {
                Integer.parseInt(message[1]);
            } catch (Exception ignored) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.PRUNE_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }

            for (Message messageToDelete : e.getChannel().getHistory().retrievePast(Integer.parseInt(message[1]) + 1).complete()) {
                if (messageToDelete == null) continue;
                try {
                    if (messageToDelete.getAuthor().equals(MentionedUser)) {
                        messageToDelete.delete().queue();
                    }
                } catch (Exception ignored) {

                }
            }
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT_USER.replace("{messages}", message[1]), MentionedUser, e.getAuthor())).build()).queue(self -> {

                try {
                    self.delete().queueAfter(3, TimeUnit.SECONDS);
                } catch (Exception ignored) {

                }
            });
            if (LOGS_ENABLED) {
                MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(Placeholders.convert(LOGS_MESSAGE
                        .replace("{messages}", message[1])
                        .replace("{channel}", e.getChannel().getAsMention()), MentionedUser, e.getAuthor()))
                        .build());
            }


        }
    }
}
