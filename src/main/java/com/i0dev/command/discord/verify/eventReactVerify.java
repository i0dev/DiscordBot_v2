package com.i0dev.command.discord.verify;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.getConfig;
import com.i0dev.utility.util.EmojiUtil;
import com.i0dev.utility.util.PermissionUtil;
import com.i0dev.utility.util.RoleUtil;
import com.sun.istack.internal.NotNull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;

public class eventReactVerify extends ListenerAdapter {

    private final String Identifier = "Verify";
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("events.event_verify.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("events.event_verify.permissionLiteMode");
    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("events.event_verify.enabled");
    private final String VERIFY_CHANNEL_ID = getConfig.get().getString("channels.verifyChannelID");
    private final String MESSAGE_DM_TITLE = getConfig.get().getString("events.event_verify.dmToVerifyTitle");
    private final String MESSAGE_DM_DESCRIPTION = getConfig.get().getString("events.event_verify.dmToVerifyDesc");
    private final List<Long> ROLES_TO_GIVE = getConfig.get().getLongList("events.event_verify.rolesToGive");
    private final List<Long> ROLES_TO_REMOVE = getConfig.get().getLongList("events.event_verify.rolesToRemove");
    private final String VERIFY_EMOJI = getConfig.get().getString("commands.createVerifyPanel.verifyEmoji");

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getChannel().getId().equals(VERIFY_CHANNEL_ID)) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        String Emoji = EmojiUtil.getSimpleEmoji(VERIFY_EMOJI);


        if (e.getReactionEmote().isEmoji()) {
            if (!EmojiUtil.getUnicodeFromCodepoints(e.getReactionEmote().getAsCodepoints()).equalsIgnoreCase(Emoji)) return;

        } else {
            if (!e.getReactionEmote().getName().equalsIgnoreCase(VERIFY_EMOJI)) return;
        }


        e.getChannel().removeReactionById(e.getMessageId(), EmojiUtil.getEmojiWithoutArrow(Emoji), e.getUser()).queue();

        RoleUtil.giveRolesLongs(ROLES_TO_GIVE, e.getMember());
        RoleUtil.removeRolesLongs(ROLES_TO_REMOVE, e.getMember());

        EmbedBuilder EmbedPM = new EmbedBuilder()
                .setTitle(Placeholders.convert(MESSAGE_DM_TITLE, e.getUser()))
                .setThumbnail(e.getUser().getEffectiveAvatarUrl())
                .setFooter(GlobalConfig.EMBED_FOOTER)
                .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                .setDescription(Placeholders.convert(MESSAGE_DM_DESCRIPTION, e.getUser()))
                .setTimestamp(ZonedDateTime.now());
        try {
            e.getUser().openPrivateChannel().complete().sendMessage(EmbedPM.build()).complete();
        } catch (Exception ignored) {

        }
    }

}
