package main.java.com.i0dev.event.ticket;

import com.sun.istack.internal.NotNull;
import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.entity.Ticket;

import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.List;

public class eventReactAdminOnly extends ListenerAdapter {

    private final String Identifier = "Ticket Admin Only React";
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketAdminOnly.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketAdminOnly.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.ticketAdminOnly.messageContent");
    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("commands.ticketAdminOnly.enabled");
    private final String MESSAGE_ADMINALREADY = getConfig.get().getString("commands.ticketAdminOnly.alreadyAdminOnly");
    private final String AdminOnlyEmoji = getConfig.get().getString("events.event_ticketCreate.adminOnlyEmoji");
    private final List<Long> ROLES_TO_GIVE_ALLOW_FOR_TICKET = getConfig.get().getLongList("events.event_ticketCreate.RolesToAllowSeeingTickets");
    private final List<Long> ROLES_ALLOWED_TO_SEE_ADMINONLY = getConfig.get().getLongList("events.event_ticketCreate.RolesToAllowSeeingAdminOnlyTickets");

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;
        if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        if (!Ticket.get().isTicket(e.getChannel())) return;
        String Emoji = EmojiUtil.getSimpleEmoji(AdminOnlyEmoji);

        if (e.getReactionEmote().isEmoji()) {
            if (!EmojiUtil.getUnicodeFromCodepoints(e.getReactionEmote().getAsCodepoints()).equalsIgnoreCase(Emoji)) return;
        } else {
            if (!e.getReactionEmote().getName().equalsIgnoreCase(AdminOnlyEmoji)) return;
        }



        e.getChannel().removeReactionById(e.getMessageId(), EmojiUtil.getEmojiWithoutArrow(Emoji), e.getUser()).queue();

        JSONObject ticketObject = Ticket.get().getTicket(e.getChannel());
        if ((boolean) ticketObject.get("adminOnlyMode")) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_ADMINALREADY, e.getUser())).build()).queue();
            return;
        }

        Ticket.get().ticketAdminOnly(e.getChannel(), true);

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

        e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getUser())).build()).queue();


    }
}
