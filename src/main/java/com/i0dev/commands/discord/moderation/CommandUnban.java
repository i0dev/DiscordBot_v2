package com.i0dev.commands.discord.moderation;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandUnban {

    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.unban.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.unban.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.unban.messageContent");
    public static final String USER_NOT_BANNED = Configuration.getString("commands.unban.userNotBanned");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.unban.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.unban.enabled");
    public static final boolean LOGS_ENABLED = Configuration.getBoolean("commands.unban.log");
    public static final String LOGS_MESSAGE = Configuration.getString("commands.unban.logMessage");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Unban User")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.UNBAN_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }

        if (!e.getGuild().retrieveBanList().complete().contains(MentionedUser)) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(USER_NOT_BANNED, MentionedUser, e.getAuthor())).build()).queue();
            return;
        }

        e.getGuild().unban(MentionedUser).queue();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, MentionedUser)).build()).queue();

        if (LOGS_ENABLED) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(Placeholders.convert(LOGS_MESSAGE, MentionedUser, e.getAuthor()))
                    .build());
        }
    }

}
