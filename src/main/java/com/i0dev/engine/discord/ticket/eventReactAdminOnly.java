package com.i0dev.engine.discord.ticket;

import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.TicketEngine;
import com.i0dev.object.objects.Ticket;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.EmojiUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class eventReactAdminOnly extends ListenerAdapter {

    public static final String Identifier = "Ticket Admin Only React";
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.ticketAdminOnly.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.ticketAdminOnly.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.ticketAdminOnly.messageContent");
    public static final boolean EVENT_ENABLED = Configuration.getBoolean("commands.ticketAdminOnly.enabled");
    public static final String MESSAGE_ADMINALREADY = Configuration.getString("commands.ticketAdminOnly.alreadyAdminOnly");
    public static final String AdminOnlyEmoji = Configuration.getString("events.event_ticketCreate.adminOnlyEmoji");
    private final List<Long> ROLES_TO_GIVE_ALLOW_FOR_TICKET = Configuration.getLongList("events.event_ticketCreate.RolesToAllowSeeingTickets");
    private final List<Long> ROLES_ALLOWED_TO_SEE_ADMINONLY = Configuration.getLongList("events.event_ticketCreate.RolesToAllowSeeingAdminOnlyTickets");

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (DPlayerEngine.getInstance().isBlacklisted(e.getUser())) return;
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        if (!TicketEngine.getInstance().isOnList(e.getChannel())) return;

        if (!EmojiUtil.isEmojiValid(e.getReactionEmote(), AdminOnlyEmoji)) return;

        e.getChannel().removeReactionById(e.getMessageId(), EmojiUtil.getEmojiWithoutArrow(AdminOnlyEmoji), e.getUser()).queue();

        Ticket ticketObject = TicketEngine.getInstance().getObject(e.getChannel());
        if (ticketObject.isAdminOnlyMode()) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_ADMINALREADY, e.getUser())).build()).queue();
            return;
        }
        TicketEngine.getInstance().setAdminOnly(TicketEngine.getInstance().getObject(e.getChannel()));

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

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getUser())).build()).queue();


    }
}
