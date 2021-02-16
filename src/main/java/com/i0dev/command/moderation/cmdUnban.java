package com.i0dev.command.moderation;

import com.i0dev.entity.Blacklist;
import com.i0dev.util.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdUnban extends ListenerAdapter {

    private final String Identifier = "Unban User";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.unban.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.unban.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.unban.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.unban.messageContent");
    private final String USER_NOT_BANNED = getConfig.get().getString("commands.unban.userNotBanned");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.unban.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.unban.enabled");
    private final boolean LOGS_ENABLED = getConfig.get().getBoolean("commands.unban.log");
    private final String LOGS_MESSAGE = getConfig.get().getString("commands.unban.logMessage");


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
            if (message.length != 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }

            if (!e.getGuild().retrieveBanList().complete().contains(MentionedUser)) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(USER_NOT_BANNED, e.getAuthor())).build()).queue();
                return;
            }

            e.getGuild().unban(MentionedUser).queue();

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT, MentionedUser)).build()).queue();

            if (LOGS_ENABLED) {
                conf.GENERAL_MAIN_LOGS_CHANNEL.sendMessage(EmbedFactory.get().createSimpleEmbed(LOGS_MESSAGE
                        .replace("{userTag}", MentionedUser.getAsTag())
                        .replace("{punisherTag}", e.getAuthor().getAsTag()))
                        .build()).queue();
            }
        }
    }
}
