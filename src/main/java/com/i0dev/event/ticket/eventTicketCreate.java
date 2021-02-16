package main.java.com.i0dev.command.ticket;

import main.java.com.i0dev.util.*;
import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.entity.Ticket;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

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
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;
        if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;

        List<JSONObject> TicketOptions = getConfig.get().getObjectList("commands.createTicketPanel.ticketOptions");
        for (JSONObject object : TicketOptions) {
            String Emoji = getSimpleEmoji(object.get("Emoji").toString());
            if (!e.getReactionEmote().getName().equals(Emoji)) continue;
            ArrayList<String> Questions = (ArrayList<String>) object.get("Questions");
            boolean AdminOnlyDefault = (boolean) object.get("AdminOnlyDefault");
            boolean PingStaffRoles = (boolean) object.get("PingStaffRoles");
            String ChannelName = object.get("ChannelName").toString();
            e.getChannel().removeReactionById(e.getMessageId(), getEmojiWithoutArrow(Emoji), e.getUser()).queue();

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
                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setThumbnail(e.getMember().getUser().getAvatarUrl())
                    .setTitle(Placeholders.convert(TICKET_CREATED_EMBED_TITLE, e.getMember().getUser()))
                    .addField(TICKET_CREATED_EMBED_FIELD_HEADER, Placeholders.convert(TICKET_CREATED_EMBED_FIELD_BASE, e.getMember().getUser()).replace("{Questions}", Prettify.FormatListString(Questions)), false)
                    .setTimestamp(ZonedDateTime.now())
                    .setFooter(conf.EMBED_FOOTER, e.getMember().getUser().getAvatarUrl());


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
                NewTicketCreated.sendMessage(Prettify.FormatList(rolesToPing) + ", " + e.getMember().getAsMention()).queue();
            } else {
                NewTicketCreated.sendMessage(e.getMember().getAsMention()).queue();
            }

            NewTicketCreated.sendMessage(Embed.build()).queue(message -> {
                message.addReaction(getEmojiWithoutArrow(getConfig.get().getString("commands.event_ticketCreate.closeTicketEmoji"))).queue();
                message.addReaction(getEmojiWithoutArrow(getConfig.get().getString("commands.event_ticketCreate.adminOnlyEmoji"))).queue();
            });
            Ticket.get().createTicket(NewTicketCreated, e.getUser(), AdminOnlyDefault, Integer.parseInt(CurrentTicketNumber));

            EmbedBuilder EmbedPM = new EmbedBuilder()
                    .setTitle(Placeholders.convert(MESSAGE_DM_TICKETCREATOR_TITLE, e.getUser()))
                    .setThumbnail(e.getUser().getAvatarUrl())
                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setDescription(Placeholders.convert(MESSAGE_DM_TICKETCREATOR_DESCRIPTION.replace("{ticketCreatedMention}", NewTicketCreated.getAsMention()), e.getUser()))
                    .setTimestamp(ZonedDateTime.now());
            try {
                e.getUser().openPrivateChannel().complete().sendMessage(EmbedPM.build()).complete();
            } catch (Exception ignored) {

            }

        }
    }

    private String getSimpleEmoji(String Emoji) {
        if (Emoji.length() < 3) {
            return Emoji;
        } else {
            return Emoji.substring(2, Emoji.length() - 20);
        }
    }

    private String getEmojiWithoutArrow(String Emoji) {
        if (Emoji.length() < 3) {
            return Emoji;
        } else {
            return Emoji.substring(0, Emoji.length() - 1);
        }
    }

    private String CurrentTicketNumber = "0";
    public static File CurrentTicketNumberFile = new File("DiscordBot/storage/tickets/currentTicketCount.txt");

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
