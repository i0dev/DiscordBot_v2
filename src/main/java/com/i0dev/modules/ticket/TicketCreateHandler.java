package com.i0dev.modules.ticket;

import com.i0dev.Engine;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.TicketEngine;
import com.i0dev.object.objects.LogObject;
import com.i0dev.object.objects.Ticket;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.EmojiUtil;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class TicketCreateHandler extends ListenerAdapter {

    public static final String Identifier = "Create Ticket";
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("events.event_ticketCreate.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("events.event_ticketCreate.permissionLiteMode");
    public static final boolean EVENT_ENABLED = Configuration.getBoolean("events.event_ticketCreate.enabled");
    public static final long TICKET_CREATE_CHANNEL_ID = Configuration.getLong("channels.ticketCreateChannelID");
    public static final long TICKET_CREATE_CATEGORY_ID = Configuration.getLong("channels.ticketCreateCategoryID");
    public static final String TICKET_CREATED_EMBED_TITLE = Configuration.getString("events.event_ticketCreate.ticketCreatedEmbedTitle");
    public static final String TICKET_CREATED_EMBED_FIELD_HEADER = Configuration.getString("events.event_ticketCreate.ticketCreatedEmbedFieldHeader");
    public static final String TICKET_CREATED_EMBED_FIELD_BASE = Configuration.getString("events.event_ticketCreate.ticketCreatedEmbedFieldBase");
    private final List<Long> ROLES_TO_PING_STAFF_PING = Configuration.getLongList("events.event_ticketCreate.RolesToPingIfPingEnabled");
    public static final String MESSAGE_DM_TICKETCREATOR_TITLE = Configuration.getString("events.event_ticketCreate.dmToTicketCreatorTicket");
    public static final String MESSAGE_DM_TICKETCREATOR_DESCRIPTION = Configuration.getString("events.event_ticketCreate.dmToTicketCreatorDescription");
    private final List<Long> ROLES_TO_GIVE_ALLOW_FOR_TICKET = Configuration.getLongList("events.event_ticketCreate.RolesToAllowSeeingTickets");
    private final List<Long> ROLES_ALLOWED_TO_SEE_ADMINONLY = Configuration.getLongList("events.event_ticketCreate.RolesToAllowSeeingAdminOnlyTickets");
    public static final long MAX_TICKETS_PER_USER = Configuration.getLong("events.event_ticketCreate.maxTicketsPerUser");
    public static final String MAX_TICKETS_MESSAGE = Configuration.getString("events.event_ticketCreate.maxTicketsMessage");


    public int getUsersTicketCount(User user) {
        int count = 0;
        for (Object o : TicketEngine.getInstance().getCache()) {
            Ticket ticket = ((Ticket) o);

            if (ticket.getTicketOwnerID().equals(user.getIdLong())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (e.getChannel().getIdLong() != (TICKET_CREATE_CHANNEL_ID)) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (DPlayerEngine.getObject(e.getUser().getIdLong()).isBlacklisted()) return;
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;

        List<JSONObject> TicketOptions = Configuration.getObjectList("commands.createTicketPanel.ticketOptions");
        for (JSONObject object : TicketOptions) {
            String Emoji = object.get("Emoji").toString();
            if (!EmojiUtil.isEmojiValid(e.getReactionEmote(), Emoji)) continue;
            e.getChannel().removeReactionById(e.getMessageId(), EmojiUtil.getEmojiWithoutArrow(Emoji), e.getUser()).queue();

            if (getUsersTicketCount(e.getUser()) >= MAX_TICKETS_PER_USER) {
                MessageUtil.sendMessagePrivateChannel(e.getUser().getIdLong(), MAX_TICKETS_MESSAGE.replace("max", MAX_TICKETS_PER_USER + ""), null, e.getUser(), null);
                return;
            }

            ArrayList<String> Questions = (ArrayList<String>) object.get("Questions");
            boolean AdminOnlyDefault = (boolean) object.get("AdminOnlyDefault");
            boolean PingStaffRoles = (boolean) object.get("PingStaffRoles");
            String ChannelName = object.get("ChannelName").toString();
            long categoryID = object.containsKey("CategoryID") ? ((long) object.get("CategoryID")) : TICKET_CREATE_CATEGORY_ID;

            Category NewTicketCreatedCategory = e.getGuild().getCategoryById(categoryID);
            TextChannel NewTicketCreated;
            if (NewTicketCreatedCategory != null) {
                NewTicketCreated = NewTicketCreatedCategory.createTextChannel(ChannelName.replace("{ticketNumber}", CurrentTicketNumber + "")).complete();
            } else {
                NewTicketCreated = e.getGuild().createTextChannel(ChannelName.replace("{ticketNumber}", CurrentTicketNumber + "")).complete();
            }
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
                message.addReaction(EmojiUtil.getEmojiWithoutArrow(Configuration.getString("events.event_ticketCreate.closeTicketEmoji"))).queue();
                message.addReaction(EmojiUtil.getEmojiWithoutArrow(Configuration.getString("events.event_ticketCreate.adminOnlyEmoji"))).queue();
            });
            Ticket ticket = new Ticket();
            ticket.setAdminOnlyMode(AdminOnlyDefault);
            ticket.setTicketOwnerID(e.getUser().getIdLong());
            ticket.setTicketOwnerTag(e.getUser().getAsTag());
            ticket.setTicketOwnerAvatarURL(e.getUser().getEffectiveAvatarUrl());
            ticket.setChannelID(NewTicketCreated.getIdLong());
            ticket.setTicketNumber(Long.parseLong(CurrentTicketNumber));
            ticket.addToCache();


            File ticketLogsFile = new File(InitializeBot.get().getTicketLogsDirPath() + "/" + NewTicketCreated.getId() + ".log");


            String Zone = ZonedDateTime.now().getZone().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            Long ticketOwnerID = ticket.getTicketOwnerID();
            String ticketOwnerAvatarURL = ticket.getTicketOwnerAvatarURL();
            String ticketOwnerTag = ticket.getTicketOwnerTag();
            boolean adminOnlyMode = ticket.isAdminOnlyMode();

            StringBuilder toFile = new StringBuilder();
            toFile.append("Ticket information:").append("\n");
            toFile.append("     Channel ID: ").append(NewTicketCreated.getId()).append("\n");
            toFile.append("     Channel Name: ").append(NewTicketCreated.getName()).append("\n");
            toFile.append("     Ticket Owner ID: ").append(ticketOwnerID).append("\n");
            toFile.append("     Ticket Owner Tag: ").append(ticketOwnerTag).append("\n");
            toFile.append("     Ticket Owner Avatar: ").append(ticketOwnerAvatarURL).append("\n");
            toFile.append("     Admin Only Mode: ").append(adminOnlyMode).append("\n");
            toFile.append("Ticket logs (TimeZone: ").append(Zone).append("):");

            Engine.getToLog().add(new LogObject(toFile.toString(), ticketLogsFile));

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
            CurrentTicketNumber = Integer.parseInt(CurrentTicketNumber) + 1 + "";
            saveFile();
        }
    }

    private static String CurrentTicketNumber = "0";
    public static File CurrentTicketNumberFile = new File(InitializeBot.get().getTicketCountPath());

    public static void saveFile() {
        try {
            Files.write(Paths.get(CurrentTicketNumberFile.getPath()),
                    CurrentTicketNumber.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void init() {
        try {
            Scanner reader = new Scanner(CurrentTicketNumberFile);
            CurrentTicketNumber = reader.nextLine();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
