package com.i0dev.modules.invite;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandInvites extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static boolean COMMAND_ENABLED;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.invites.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.invites.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.invites.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.invites.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.invites.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Invites")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length > 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.INVITES_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        if (message.length == 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, null, MentionedUser)).build()).queue();

    }

}
