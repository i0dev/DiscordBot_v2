package com.i0dev.modules.basic;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandRoles extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_TITLE;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.roles.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.roles.permissionLiteMode");
        MESSAGE_TITLE = Configuration.getString("commands.roles.messageTitle");
        MESSAGE_FORMAT = Configuration.getString("commands.roles.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.roles.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "List Roles")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.ROLES_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        StringBuilder description = new StringBuilder();
        for (Role role : e.getGuild().getRoles()) {
            description.append(role.getAsMention()).append("\n");
        }

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_TITLE, e.getAuthor()), Placeholders.convert(description.toString(), e.getAuthor())).build()).queue();

    }

}