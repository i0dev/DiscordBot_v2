package com.i0dev.modules.other;

import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.objects.ReactionRoles;
import com.i0dev.object.objects.RoleQueueObject;
import com.i0dev.object.objects.Type;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ReactionRolesHandler extends ListenerAdapter {

    public static final String Identifier = "Reaction Role Event";
    public static final boolean EVENT_ENABLED = Configuration.getBoolean("commands.reactionRoles.enabled");
    public static final String onReactionDMTitle = Configuration.getString("commands.reactionRoles.onReactionDMTitle");
    public static final String onReactionDMDescAdded = Configuration.getString("commands.reactionRoles.onReactionDMDescAdded");
    public static final String onReactionDMDescRemoved = Configuration.getString("commands.reactionRoles.onReactionDMDescRemoved");

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
        if (e.getUser() == null) return;
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!FormatUtil.isValidGuild(e.getGuild())) return;
        if (DPlayerEngine.getObject(e.getUser().getIdLong()).isBlacklisted()) return;
        for (JSONObject object : ReactionRoles.get().getCache()) {
            String ChannelID = object.get("channelID").toString();
            String MessageID = object.get("messageID").toString();
            if (!e.getChannel().getId().equals(ChannelID)) continue;
            try {
                if (!e.getChannel().retrieveMessageById(e.getMessageId()).complete().getId().equals(MessageID))
                    continue;
            } catch (Exception ignored) {
            }

            for (JSONObject Option : (ArrayList<JSONObject>) object.get("options")) {
                String Emoji = Option.get("Emoji").toString();
                Role role =  InternalJDA.getJda().getRoleById(Option.get("roleID").toString());
                if (role == null) continue;
                if (e.getReactionEmote().isEmoji()) {
                    String ReactedEmojiHexacode = String.format("%x", (int) e.getReactionEmote().getEmoji().charAt(0));
                    if (ReactedEmojiHexacode.equals(Emoji)) {
                        GlobalConfig.GENERAL_MAIN_GUILD.removeRoleFromMember(e.getMember(), role).queue();
                        e.getUser().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(onReactionDMTitle, e.getUser()), Placeholders.convert(onReactionDMDescRemoved
                                        .replace("{roleName}", role.getName())
                                , e.getUser())).build()).queue();
                        return;

                    }
                } else {
                    if (e.getReactionEmote().getEmote().getAsMention().equals(Emoji)) {
                        GlobalConfig.GENERAL_MAIN_GUILD.removeRoleFromMember(e.getMember(), role).queue();
                        e.getUser().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(onReactionDMTitle, e.getUser()), Placeholders.convert(onReactionDMDescRemoved
                                        .replace("{roleName}", role.getName())
                                , e.getUser())).build()).queue();
                        return;

                    }
                }
            }
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {


        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!FormatUtil.isValidGuild(e.getGuild())) return;
        if (DPlayerEngine.getObject(e.getUser().getIdLong()).isBlacklisted()) return;

        for (JSONObject object : ReactionRoles.get().getCache()) {
            String ChannelID = object.get("channelID").toString();
            String MessageID = object.get("messageID").toString();
            if (!e.getChannel().getId().equals(ChannelID)) continue;
            try {
                if (!e.getChannel().retrieveMessageById(e.getMessageId()).complete().getId().equals(MessageID))
                    continue;
            } catch (Exception ignored) {
            }

            for (JSONObject Option : (ArrayList<JSONObject>) object.get("options")) {
                String Emoji = Option.get("Emoji").toString();
                Role role =  InternalJDA.getJda().getRoleById(Option.get("roleID").toString());
                if (role == null) continue;
                if (e.getReactionEmote().isEmoji()) {
                    String ReactedEmojiHexacode = String.format("%x", (int) e.getReactionEmote().getEmoji().charAt(0));
                    if (ReactedEmojiHexacode.equals(Emoji)) {
                        new RoleQueueObject(e.getMember().getIdLong(), role.getIdLong(), Type.ADD_ROLE).add();

                        e.getUser().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(onReactionDMTitle, e.getUser()), Placeholders.convert(onReactionDMDescAdded
                                        .replace("{roleName}", role.getName())
                                , e.getUser())).build()).queue();
                        return;

                    }
                } else {
                    if (e.getReactionEmote().getEmote().getAsMention().equals(Emoji)) {
                        new RoleQueueObject(e.getMember().getIdLong(), role.getIdLong(), Type.ADD_ROLE).add();
                        e.getUser().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(onReactionDMTitle, e.getUser()), Placeholders.convert(onReactionDMDescAdded
                                        .replace("{roleName}", role.getName())
                                , e.getUser())).build()).queue();
                        return;
                    }
                }
            }
        }
    }
}
