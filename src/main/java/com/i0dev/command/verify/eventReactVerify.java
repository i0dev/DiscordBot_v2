package com.i0dev.command.verify;

import com.i0dev.entity.Blacklist;
import com.i0dev.util.InternalPermission;
import com.i0dev.util.Placeholders;
import com.i0dev.util.conf;
import com.i0dev.util.getConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;

public class eventReactVerify extends ListenerAdapter {

    private final String Identifier = "Verify";
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.event_verify.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.event_verify.permissionLiteMode");
    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("commands.event_verify.enabled");
    private final String VERIFY_CHANNEL_ID = getConfig.get().getString("channels.verifyChannelID");
    private final String MESSAGE_DM_TITLE = getConfig.get().getString("commands.event_verify.dmToVerifyTitle");
    private final String MESSAGE_DM_DESCRIPTION = getConfig.get().getString("commands.event_verify.dmToVerifyDesc");
    private final List<Long> ROLES_TO_GIVE = getConfig.get().getLongList("commands.event_verify.rolesToGive");
    private final List<Long> ROLES_TO_REMOVE = getConfig.get().getLongList("commands.event_verify.rolesToRemove");
    private final String VERIFY_EMOJI = getConfig.get().getString("commands.createVerifyPanel.verifyEmoji");


    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getChannel().getId().equals(VERIFY_CHANNEL_ID)) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;
        if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        String Emoji = getSimpleEmoji(VERIFY_EMOJI);
        if (!e.getReactionEmote().getName().equals(Emoji)) return;
        e.getChannel().removeReactionById(e.getMessageId(), getEmojiWithoutArrow(Emoji), e.getUser()).queue();

        for (long roleID : ROLES_TO_GIVE) {
            Role role = e.getGuild().getRoleById(roleID);
            if (role == null) continue;
            if (e.getMember().getRoles().contains(role)) continue;
            e.getGuild().addRoleToMember(e.getMember(), role).queue();
        }
        for (long roleID : ROLES_TO_REMOVE) {
            Role role = e.getGuild().getRoleById(roleID);
            if (role == null) continue;
            if (!e.getMember().getRoles().contains(role)) continue;
            e.getGuild().removeRoleFromMember(e.getMember(), role).queue();
        }

        EmbedBuilder EmbedPM = new EmbedBuilder()
                .setTitle(Placeholders.convert(MESSAGE_DM_TITLE, e.getUser()))
                .setThumbnail(e.getUser().getAvatarUrl())
                .setFooter(conf.EMBED_FOOTER)
                .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                .setDescription(Placeholders.convert(MESSAGE_DM_DESCRIPTION, e.getUser()))
                .setTimestamp(ZonedDateTime.now());
        try {
            e.getUser().openPrivateChannel().complete().sendMessage(EmbedPM.build()).complete();
        } catch (Exception ignored) {

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
}
