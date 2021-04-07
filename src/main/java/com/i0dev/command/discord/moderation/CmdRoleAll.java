package com.i0dev.command.discord.moderation;

import com.i0dev.engine.discord.RoleQueue;
import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;
import com.i0dev.utility.util.PermissionUtil;
import com.i0dev.utility.util.RoleUtil;
import com.i0dev.utility.util.TimeUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class CmdRoleAll extends ListenerAdapter {

    private final String Identifier = "Role All";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.role_all.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.role_all.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.role_all.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.role_all.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.role_all.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.role_all.enabled");
    private final String noUsersToGive = getConfig.get().getString("commands.role_all.noUsersToGive");


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
            if (message.length != 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            Role MentionedRole = FindFromString.get().getRole(message[1], e.getMessage());

            if (MentionedRole == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_ROLE_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }
            if (RoleUtil.getUsersWhoNeedRole(MentionedRole) == 0) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(noUsersToGive.replace("{roleName}", MentionedRole.getName()), e.getAuthor())).build()).queue();
                return;
            }

            String description = MESSAGE_CONTENT
                    .replace("{roleName}", MentionedRole.getName())
                    .replace("{userCount}", RoleUtil.getUsersWhoNeedRole(MentionedRole) + "")
                    .replace("{time}", TimeUtil.formatPlayTime((2L * (RoleUtil.getUsersWhoNeedRole(MentionedRole) + RoleQueue.getQueue().size())) * 1000));

            RoleUtil.giveRolesUsersLongs(MentionedRole, RoleUtil.getUserListWhoNeedRole(MentionedRole));

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(description, e.getAuthor())).build()).queue();

        }
    }
}