package com.i0dev.commands.discord.moderation;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandKick {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.kick.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.kick.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.kick.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.kick.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.kick.enabled");
    public static final boolean LOGS_ENABLED = Configuration.getBoolean("commands.kick.log");
    public static final String LOGS_MESSAGE = Configuration.getString("commands.kick.logMessage");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Kick")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.KICK_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }

        String reason = FormatUtil.remainingArgFormatter(message, 2);

        e.getGuild().kick(e.getGuild().getMember(MentionedUser)).queue();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{reason}", reason), MentionedUser, e.getAuthor())).build()).queue();

        if (LOGS_ENABLED) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(Placeholders.convert(LOGS_MESSAGE,MentionedUser,e.getAuthor())
                    .replace("{reason}", reason))
                    .build());
        }

    }
}
