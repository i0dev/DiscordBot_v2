package com.i0dev.commands.discord.moderation;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandBan {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ban.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ban.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.ban.messageContent");
    public static final String USER_ALREADY_BANNED = Configuration.getString("commands.ban.userAlreadyBanned");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.ban.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.ban.enabled");
    public static final boolean LOGS_ENABLED = Configuration.getBoolean("commands.ban.log");
    public static final String LOGS_MESSAGE = Configuration.getString("commands.ban.logMessage");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Ban User")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.BAN_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }

        if (e.getGuild().retrieveBanList().complete().contains(MentionedUser)) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(USER_ALREADY_BANNED, e.getAuthor())).build()).queue();
            return;
        }

        String reason = FormatUtil.remainingArgFormatter(message, 2);

        e.getGuild().ban(MentionedUser, 0, reason).queue();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{reason}", reason), MentionedUser)).build()).queue();

        if (LOGS_ENABLED) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(LOGS_MESSAGE
                    .replace("{userTag}", MentionedUser.getAsTag())
                    .replace("{punisherTag}", e.getAuthor().getAsTag())
                    .replace("{reason}", reason))
                    .build());

        }
    }
}
