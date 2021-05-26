package com.i0dev.modules.moderation;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandBan extends DiscordCommand {
    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static String USER_ALREADY_BANNED;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;
    public static boolean LOGS_ENABLED;
    public static String LOGS_MESSAGE;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ban.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ban.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.ban.messageContent");
        USER_ALREADY_BANNED = Configuration.getString("commands.ban.userAlreadyBanned");
        MESSAGE_FORMAT = Configuration.getString("commands.ban.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.ban.enabled");
        LOGS_ENABLED = Configuration.getBoolean("commands.ban.log");
        LOGS_MESSAGE = Configuration.getString("commands.ban.logMessage");
    }

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
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(USER_ALREADY_BANNED, MentionedUser, e.getAuthor())).build()).queue();
            return;
        }

        String reason = FormatUtil.remainingArgFormatter(message, 2);

        e.getGuild().ban(MentionedUser, 0, reason).queue();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{reason}", reason), MentionedUser, e.getAuthor())).build()).queue();

        if (LOGS_ENABLED) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(Placeholders.convert(LOGS_MESSAGE
                    .replace("{reason}", reason), MentionedUser, e.getAuthor()))
                    .build());

        }
    }
}
