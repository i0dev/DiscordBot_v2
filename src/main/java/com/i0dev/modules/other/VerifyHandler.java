package com.i0dev.modules.other;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.EmojiUtil;
import com.i0dev.utility.util.PermissionUtil;
import com.i0dev.utility.util.RoleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;

public class VerifyHandler extends ListenerAdapter {

    public static final String Identifier = "Verify";
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("events.event_verify.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("events.event_verify.permissionLiteMode");
    public static final boolean EVENT_ENABLED = Configuration.getBoolean("events.event_verify.enabled");
    public static final String VERIFY_CHANNEL_ID = Configuration.getString("channels.verifyChannelID");
    public static final String MESSAGE_DM_TITLE = Configuration.getString("events.event_verify.dmToVerifyTitle");
    public static final String MESSAGE_DM_DESCRIPTION = Configuration.getString("events.event_verify.dmToVerifyDesc");
    private final List<Long> ROLES_TO_GIVE = Configuration.getLongList("events.event_verify.rolesToGive");
    private final List<Long> ROLES_TO_REMOVE = Configuration.getLongList("events.event_verify.rolesToRemove");
    public static final String VERIFY_EMOJI = Configuration.getString("commands.createVerifyPanel.verifyEmoji");

    @Override
    public void onGuildMessageReactionAdd( GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!EVENT_ENABLED) return;
        if (!e.getChannel().getId().equals(VERIFY_CHANNEL_ID)) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        String Emoji = (VERIFY_EMOJI);


        if (!EmojiUtil.isEmojiValid(e.getReactionEmote(), Emoji)) return;

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
