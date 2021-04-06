package com.i0dev.event.discord.reactionroles;

import com.i0dev.engine.discord.RoleQueue;
import com.i0dev.object.Blacklist;
import com.i0dev.object.ReactionRoles;
import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.getConfig;
import com.i0dev.utility.util.PermissionUtil;
import com.sun.istack.internal.NotNull;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class onReactionRole extends ListenerAdapter {

    private final String Identifier = "Reaction Role Event";
    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("commands.reactionRoles.enabled");
    private final String onReactionDMTitle = getConfig.get().getString("commands.reactionRoles.onReactionDMTitle");
    private final String onReactionDMDescAdded = getConfig.get().getString("commands.reactionRoles.onReactionDMDescAdded");
    private final String onReactionDMDescRemoved = getConfig.get().getString("commands.reactionRoles.onReactionDMDescRemoved");

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
        if (e.getUser() == null) return;
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;

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
                Role role = GlobalConfig.GENERAL_MAIN_GUILD.getRoleById(Option.get("roleID").toString());
                if (role == null) continue;
                if (e.getReactionEmote().isEmoji()) {
                    String ReactedEmojiHexacode = String.format("%x", (int) e.getReactionEmote().getEmoji().charAt(0));
                    if (ReactedEmojiHexacode.equals(Emoji)) {
                        GlobalConfig.GENERAL_MAIN_GUILD.removeRoleFromMember(e.getMember(), role).queue();
                        e.getUser().openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(onReactionDMTitle, e.getUser()), Placeholders.convert(onReactionDMDescRemoved
                                        .replace("{roleName}", role.getName())
                                , e.getUser())).build()).queue();
                        return;

                    }
                } else {
                    if (e.getReactionEmote().getEmote().getAsMention().equals(Emoji)) {
                        GlobalConfig.GENERAL_MAIN_GUILD.removeRoleFromMember(e.getMember(), role).queue();
                        e.getUser().openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(onReactionDMTitle, e.getUser()), Placeholders.convert(onReactionDMDescRemoved
                                        .replace("{roleName}", role.getName())
                                , e.getUser())).build()).queue();
                        return;

                    }
                }
            }
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;

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
                Role role = GlobalConfig.GENERAL_MAIN_GUILD.getRoleById(Option.get("roleID").toString());
                if (role == null) continue;
                if (e.getReactionEmote().isEmoji()) {
                    String ReactedEmojiHexacode = String.format("%x", (int) e.getReactionEmote().getEmoji().charAt(0));
                    if (ReactedEmojiHexacode.equals(Emoji)) {
                        RoleQueue.addToQueue(e.getMember().getUser(), role);
                        e.getUser().openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(onReactionDMTitle, e.getUser()), Placeholders.convert(onReactionDMDescAdded
                                        .replace("{roleName}", role.getName())
                                , e.getUser())).build()).queue();
                        return;

                    }
                } else {
                    if (e.getReactionEmote().getEmote().getAsMention().equals(Emoji)) {
                        RoleQueue.addToQueue(e.getMember().getUser(), role);
                        e.getUser().openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(onReactionDMTitle, e.getUser()), Placeholders.convert(onReactionDMDescAdded
                                        .replace("{roleName}", role.getName())
                                , e.getUser())).build()).queue();
                        return;
                    }
                }
            }
        }
    }
}
