package com.i0dev.command.basic;

import com.i0dev.entity.Blacklist;
import com.i0dev.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class cmdRoleInfo extends ListenerAdapter {

    private final String Identifier = "Role Info";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.roleInfo.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.roleInfo.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.roleInfo.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.roleInfo.messageTitle");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.roleInfo.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.roleInfo.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.roleInfo.enabled");


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
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX+COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            Role MentionedRole = FindFromString.get().getRole(message[1], e.getMessage());

            if (MentionedRole == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_ROLE_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
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

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TITLE.replace("{role}", MentionedRole.getName()), e.getAuthor()), Placeholders.convert(description, e.getAuthor())).build()).queue();

        }
    }
}