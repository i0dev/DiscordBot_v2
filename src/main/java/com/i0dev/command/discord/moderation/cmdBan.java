package com.i0dev.command.discord.moderation;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.PermissionUtil;


import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdBan extends ListenerAdapter {

    private final String Identifier = "Ban User";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.ban.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.ban.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.ban.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.ban.messageContent");
    private final String USER_ALREADY_BANNED = getConfig.get().getString("commands.ban.userAlreadyBanned");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.ban.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.ban.enabled");
    private final boolean LOGS_ENABLED = getConfig.get().getBoolean("commands.ban.log");
    private final String LOGS_MESSAGE = getConfig.get().getString("commands.ban.logMessage");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length == 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }

            if (e.getGuild().retrieveBanList().complete().contains(MentionedUser)) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(USER_ALREADY_BANNED, e.getAuthor())).build()).queue();
                return;
            }

            String reason = FormatUtil.remainingArgFormatter(message, 2);

            e.getGuild().ban(MentionedUser, 0, reason).queue();

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{reason}", reason), MentionedUser)).build()).queue();

            if (LOGS_ENABLED) {
                MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL,EmbedFactory.get().createSimpleEmbed(LOGS_MESSAGE
                        .replace("{userTag}", MentionedUser.getAsTag())
                        .replace("{punisherTag}", e.getAuthor().getAsTag())
                        .replace("{reason}", reason))
                        .build());
            }
        }
    }
}
