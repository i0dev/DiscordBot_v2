package com.i0dev.modules.moderation;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandDm extends DiscordCommand {
    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;
    public static String announcementTitle;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.directMessage.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.directMessage.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.directMessage.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.directMessage.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.directMessage.enabled");
        announcementTitle = Configuration.getString("commands.directMessage.dmTitle");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Direct Message")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length <= 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.DIRECT_MESSAGE_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        User user = FindFromString.get().getUser(message[1], e.getMessage());
        if (user == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_CHANNEL_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }

        String content = FormatUtil.remainingArgFormatter(message, 2);

        if (content.endsWith(" -normal")) {
            content = content.substring(0, content.length() - " -normal".length());
            user.openPrivateChannel().complete().sendMessage(Placeholders.convert(content, user, e.getAuthor())).queue();
        } else {
            MessageUtil.sendMessagePrivateChannel(user.getIdLong(), content, announcementTitle, e.getAuthor(), user);
        }
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, user, e.getAuthor())).build()).queue();
    }
}