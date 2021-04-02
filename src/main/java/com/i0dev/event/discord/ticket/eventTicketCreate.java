package main.java.com.i0dev.event.discord.ticket;

import com.sun.istack.internal.NotNull;
import main.java.com.i0dev.InitilizeBot;
import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.object.Ticket;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.EmojiUtil;
import main.java.com.i0dev.utility.util.FormatUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class eventTicketCreate extends ListenerAdapter {

    private final String Identifier = "Create Ticket";
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("events.event_ticketCreate.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("events.event_ticketCreate.permissionLiteMode");
    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("events.event_ticketCreate.enabled");
    private final String TICKET_CREATE_CHANNEL_ID = getConfig.get().getString("channels.ticketCreateChannelID");
    private final String TICKET_CREATE_CATEGORY_ID = getConfig.get().getString("channels.ticketCreateCategoryID");
    private final String TICKET_CREATED_EMBED_TITLE = getConfig.get().getString("events.event_ticketCreate.ticketCreatedEmbedTitle");
    private final String TICKET_CREATED_EMBED_FIELD_HEADER = getConfig.get().getString("events.event_ticketCreate.ticketCreatedEmbedFieldHeader");
    private final String TICKET_CREATED_EMBED_FIELD_BASE = getConfig.get().getString("events.event_ticketCreate.ticketCreatedEmbedFieldBase");
    private final List<Long> ROLES_TO_PING_STAFF_PING = getConfig.get().getLongList("events.event_ticketCreate.RolesToPingIfPingEnabled");
    private final String MESSAGE_DM_TICKETCREATOR_TITLE = getConfig.get().getString("events.event_ticketCreate.dmToTicketCreatorTicket");
    private final String MESSAGE_DM_TICKETCREATOR_DESCRIPTION = getConfig.get().getString("events.event_ticketCreate.dmToTicketCreatorDescription");
    private final List<Long> ROLES_TO_GIVE_ALLOW_FOR_TICKET = getConfig.get().getLongList("events.event_ticketCreate.RolesToAllowSeeingTickets");
    private final List<Long> ROLES_ALLOWED_TO_SEE_ADMINONLY = getConfig.get().getLongList("events.event_ticketCreate.RolesToAllowSeeingAdminOnlyTickets");

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getChannel().getId().equals(TICKET_CREATE_CHANNEL_ID)) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;

        List<JSONObject> TicketOptions = getConfig.get().getObjectList("commands.createTicketPanel.ticketOptions");
        for (JSONObject object : TicketOptions) {
            String Emoji = EmojiUtil.getSimpleEmoji(object.get("Emoji").toString());

            if (e.getReactionEmote().isEmoji()) {
                if (!EmojiUtil.getUnicodeFromCodepoints(e.getReactionEmote().getAsCodepoints()).equalsIgnoreCase(Emoji))
                    continue;
            } else {
                if (!e.getReactionEmote().getName().equalsIgnoreCase(Emoji)) continue;
            }

            ArrayList<String> Questions = (ArrayList<String>) object.get("Questions");
            boolean AdminOnlyDefault = (boolean) object.get("AdminOnlyDefault");
            boolean PingStaffRoles = (boolean) object.get("PingStaffRoles");
            String ChannelName = object.get("ChannelName").toString();
            e.getChannel().removeReactionById(e.getMessageId(), EmojiUtil.getEmojiWithoutArrow(Emoji), e.getUser()).queue();

            Category NewTicketCreatedCategory = e.getGuild().getCategoryById(TICKET_CREATE_CATEGORY_ID);
            TextChannel NewTicketCreated;
            if (NewTicketCreatedCategory != null) {
                NewTicketCreated = NewTicketCreatedCategory.createTextChannel(ChannelName.replace("{ticketNumber}", CurrentTicketNumber + "")).complete();
            } else {
                NewTicketCreated = e.getGuild().createTextChannel(ChannelName.replace("{ticketNumber}", CurrentTicketNumber + "")).complete();
            }
            CurrentTicketNumber = Integer.parseInt(CurrentTicketNumber) + 1 + "";
            saveFile();
            EmbedBuilder Embed = new EmbedBuilder()
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                    .setThumbnail(e.getMember().getUser().getEffectiveAvatarUrl())
                    .setTitle(Placeholders.convert(TICKET_CREATED_EMBED_TITLE, e.getMember().getUser()))
                    .addField(TICKET_CREATED_EMBED_FIELD_HEADER, Placeholders.convert(TICKET_CREATED_EMBED_FIELD_BASE, e.getMember().getUser()).replace("{Questions}", FormatUtil.FormatListString(Questions)), false)
                    .setTimestamp(ZonedDateTime.now())
                    .setFooter(GlobalConfig.EMBED_FOOTER, e.getMember().getUser().getEffectiveAvatarUrl());


            try {
                NewTicketCreated.putPermissionOverride(e.getMember())
                        .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                                Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                        .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                                Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                        .queue();
            } catch (Exception ignored) {
            }
            try {
                NewTicketCreated.putPermissionOverride(e.getGuild().getPublicRole())
                        .setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                                Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE,
                                Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                                Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                        .queue();
            } catch (Exception ignored) {
            }

            if (!AdminOnlyDefault) {
                for (Long roleID : ROLES_TO_GIVE_ALLOW_FOR_TICKET) {
                    Role role = e.getGuild().getRoleById(roleID);
                    try {
                        NewTicketCreated.putPermissionOverride(role)
                                .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                                        Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                                        Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                                .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                                        Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                                .queue();
                    } catch (Exception ignored) {
                    }
                }
            }
            for (Long roleID : ROLES_ALLOWED_TO_SEE_ADMINONLY) {
                Role role = e.getGuild().getRoleById(roleID);
                try {
                    NewTicketCreated.putPermissionOverride(role)
                            .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                                    Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                                    Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                            .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                                    Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                            .queue();
                } catch (Exception ignored) {
                }
            }


            if (PingStaffRoles) {
                List<Role> rolesToPing = new ArrayList<>();
                for (Long roleID : ROLES_TO_PING_STAFF_PING) {
                    Role role = e.getGuild().getRoleById(roleID);
                    if (role == null) continue;
                    rolesToPing.add(role);
                }
                NewTicketCreated.sendMessage(FormatUtil.FormatList(rolesToPing) + ", " + e.getMember().getAsMention()).queue();
            } else {
                NewTicketCreated.sendMessage(e.getMember().getAsMention()).queue();
            }

            NewTicketCreated.sendMessage(Embed.build()).queue(message -> {
                message.addReaction(EmojiUtil.getEmojiWithoutArrow(getConfig.get().getString("events.event_ticketCreate.closeTicketEmoji"))).queue();
                message.addReaction(EmojiUtil.getEmojiWithoutArrow(getConfig.get().getString("events.event_ticketCreate.adminOnlyEmoji"))).queue();
            });
            Ticket.get().createTicket(NewTicketCreated, e.getUser(), AdminOnlyDefault, Integer.parseInt(CurrentTicketNumber));

            EmbedBuilder EmbedPM = new EmbedBuilder()
                    .setTitle(Placeholders.convert(MESSAGE_DM_TICKETCREATOR_TITLE, e.getUser()))
                    .setThumbnail(e.getUser().getEffectiveAvatarUrl())
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                    .setDescription(Placeholders.convert(MESSAGE_DM_TICKETCREATOR_DESCRIPTION.replace("{ticketCreatedMention}", NewTicketCreated.getAsMention()), e.getUser()))
                    .setTimestamp(ZonedDateTime.now());
            try {
                e.getUser().openPrivateChannel().complete().sendMessage(EmbedPM.build()).complete();
            } catch (Exception ignored) {

            }

        }
    }

    private String CurrentTicketNumber = "0";
    public static File CurrentTicketNumberFile = new File(InitilizeBot.get().getTicketCountPath());

    public void saveFile() {
        try {
            Files.write(Paths.get(CurrentTicketNumberFile.getPath()),
                    CurrentTicketNumber.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public eventTicketCreate() {
        try {
            Scanner reader = new Scanner(CurrentTicketNumberFile);
            CurrentTicketNumber = reader.nextLine();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
