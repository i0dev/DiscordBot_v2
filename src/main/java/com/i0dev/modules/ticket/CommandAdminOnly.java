package com.i0dev.modules.ticket;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.engines.TicketEngine;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.object.objects.Ticket;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class CommandAdminOnly extends DiscordCommand {
    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;
    public static String MESSAGE_ADMINALREADY;
    private static List<Long> ROLES_TO_GIVE_ALLOW_FOR_TICKET;
    private static List<Long> ROLES_ALLOWED_TO_SEE_ADMINONLY;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ticketAdminOnly.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ticketAdminOnly.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.ticketAdminOnly.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.ticketAdminOnly.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.ticketAdminOnly.enabled");
        MESSAGE_ADMINALREADY = Configuration.getString("commands.ticketAdminOnly.alreadyAdminOnly");
        ROLES_TO_GIVE_ALLOW_FOR_TICKET = Configuration.getLongList("events.event_ticketCreate.RolesToAllowSeeingTickets");
        ROLES_ALLOWED_TO_SEE_ADMINONLY = Configuration.getLongList("events.event_ticketCreate.RolesToAllowSeeingAdminOnlyTickets");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Admin Only")) {
            return;
        }
        if (!TicketEngine.getInstance().isOnList(e.getChannel().getIdLong())) return;

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.ADMIN_ONLY_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        Ticket ticketObject = TicketEngine.getInstance().getObject(e.getChannel().getIdLong());
        if (ticketObject.isAdminOnlyMode()) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_ADMINALREADY, e.getAuthor())).build()).queue();
            return;
        }


        TicketEngine.getInstance().setAdminOnly(ticketObject);

        for (Long roleID : ROLES_TO_GIVE_ALLOW_FOR_TICKET) {
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
        for (Long roleID : ROLES_ALLOWED_TO_SEE_ADMINONLY) {
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

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();


    }
}
