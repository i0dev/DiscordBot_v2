package com.i0dev.modules.basic;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandRoleInfo extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_TITLE;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.roleInfo.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.roleInfo.permissionLiteMode");
        MESSAGE_TITLE = Configuration.getString("commands.roleInfo.messageTitle");
        MESSAGE_CONTENT = Configuration.getString("commands.roleInfo.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.roleInfo.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.roleInfo.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Role Info")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.ROLE_INFO_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        Role MentionedRole = FindFromString.get().getRole(message[1], e.getMessage());

        if (MentionedRole == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_ROLE_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }

        String ColorFormatted = "Default Color";

        if (MentionedRole.getColor() != null) {
            int Red = MentionedRole.getColor().getRed();
            int Blue = MentionedRole.getColor().getBlue();
            int Green = MentionedRole.getColor().getGreen();
            ColorFormatted = String.format("#%02x%02x%02x", Red, Green, Blue);
        }

        String description = MESSAGE_CONTENT.replace("{roleID}", MentionedRole.getId())
                .replace("{roleName}", MentionedRole.getName())
                .replace("{roleColor}", ColorFormatted)
                .replace("{roleMention}", MentionedRole.getAsMention())
                .replace("{rolePos}", MentionedRole.getPosition() + "")
                .replace("{roleMentionable}", MentionedRole.isMentionable() + "")
                .replace("{roleHoisted}", MentionedRole.isHoisted() + "");

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_TITLE.replace("{role}", MentionedRole.getName()), e.getAuthor()), Placeholders.convert(description, e.getAuthor())).build()).queue();

    }
}
