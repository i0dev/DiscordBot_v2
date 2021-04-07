package com.i0dev.command.discord.moderation;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.PermissionUtil;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class cmdPrune extends ListenerAdapter {

    private final String Identifier = "Prune Messages";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.prune.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.prune.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.prune.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.prune.messageContent");
    private final String MESSAGE_CONTENT_USER = getConfig.get().getString("commands.prune.messageContentUser");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.prune.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.prune.enabled");
    private final boolean LOGS_ENABLED = getConfig.get().getBoolean("commands.prune.log");
    private final String LOGS_MESSAGE = getConfig.get().getString("commands.prune.logMessage");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (!COMMAND_ENABLED) {
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length > 3 || message.length == 1) {
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            if (message.length == 2) {
                try {
                    Integer.parseInt(message[1]);
                } catch (Exception ignored) {
                    e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                    return;
                }
                for (Message messageToDelete : e.getChannel().getHistory().retrievePast(Integer.parseInt(message[1]) + 1).complete()) {
                    if (messageToDelete == null) continue;
                    try {
                        messageToDelete.delete().queue();
                    } catch (Exception ignored) {

                    }
                }
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{messages}", message[1]), e.getAuthor())).build()).queue(self -> {

                    try {
                        self.delete().queueAfter(3, TimeUnit.SECONDS);
                    } catch (Exception ignored) {

                    }
                });


                if (LOGS_ENABLED) {
                    MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, (EmbedFactory.get().createSimpleEmbed(LOGS_MESSAGE
                            .replace("{messages}", message[1])
                            .replace("{userTag}", e.getAuthor().getAsTag())
                            .replace("{channel}", e.getChannel().getAsMention()))
                            .build()));
                }

            } else {
                try {
                    Integer.parseInt(message[1]);
                } catch (Exception ignored) {
                    e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                    return;
                }

                User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
                if (MentionedUser == null) {
                    e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
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
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT_USER.replace("{prunedTag}", MentionedUser.getAsTag()).replace("{messages}", message[1]), e.getAuthor())).build()).queue(self -> {

                    try {
                        self.delete().queueAfter(3, TimeUnit.SECONDS);
                    } catch (Exception ignored) {

                    }
                });
                if (LOGS_ENABLED) {
                    MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.get().createSimpleEmbed(LOGS_MESSAGE
                            .replace("{messages}", message[1])
                            .replace("{userTag}", e.getAuthor().getAsTag())
                            .replace("{prunedUserTag}", MentionedUser.getAsTag())
                            .replace("{channel}", e.getChannel().getAsMention()))
                            .build());
                }


            }
        }
    }
}
