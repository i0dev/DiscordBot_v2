package com.i0dev.command.ticket;

import com.i0dev.entity.Blacklist;
import com.i0dev.entity.Ticket;
import com.i0dev.util.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.List;

public class cmdTicketAdminOnly extends ListenerAdapter {

    private final String Identifier = "Ticket Admin Only";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.ticketAdminOnly.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketAdminOnly.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketAdminOnly.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.ticketAdminOnly.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.ticketAdminOnly.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.ticketAdminOnly.enabled");
    private final String MESSAGE_ADMINALREADY = getConfig.get().getString("commands.ticketAdminOnly.alreadyAdminOnly");
    private final List<String> ROLES_TO_GIVE_ALLOW_FOR_TICKET = getConfig.get().getStringList("commands.event_ticketCreate.RolesToAllowSeeingTickets");
    private final List<String> ROLES_ALLOWED_TO_SEE_ADMINONLY = getConfig.get().getStringList("commands.event_ticketCreate.RolesToAllowSeeingAdminOnlyTickets");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (!Ticket.get().isTicket(e.getChannel())) return;

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
            if (message.length != 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            JSONObject ticketObject = Ticket.get().getTicket(e.getChannel());
            if ((boolean) ticketObject.get("adminOnlyMode")) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_ADMINALREADY, e.getAuthor())).build()).queue();
                return;
            }

            Ticket.get().ticketAdminOnly(e.getChannel(), true);

            for (String roleID : ROLES_TO_GIVE_ALLOW_FOR_TICKET) {
                Role role = e.getGuild().getRoleById(roleID);
                try {
                    e.getChannel().putPermissionOverride(role)
                            .setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                                    Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS,
                                    Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                                    Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_MENTION_EVERYONE,
                                    Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS, Permission.MANAGE_WEBHOOKS,
                                    Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                            .queue();
                } catch (Exception ignored) {
                }
            }
            for (String roleID : ROLES_ALLOWED_TO_SEE_ADMINONLY) {
                Role role = e.getGuild().getRoleById(roleID);
                try {
                    e.getChannel().putPermissionOverride(role)
                            .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                                    Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                                    Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                            .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                                    Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                            .queue();
                } catch (Exception ignored) {
                }
            }

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();


        }
    }
}
