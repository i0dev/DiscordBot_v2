package com.i0dev.engine.discord.welcome;

import com.i0dev.utility.util.RoleUtil;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class eventWelcome extends ListenerAdapter {

    public static final String Identifier = "Welcome";
    public static final boolean  EVENT_ENABLED = Configuration.getBoolean("events.event_welcome.enabled");
    public static final String WELCOME_CHANNEL = Configuration.getString("channels.welcomeChannelID");
    private final List<Long> ROLES_TO_GIVE = Configuration.getLongList("events.event_welcome.rolesToGive");
    public static final boolean  welcomeMessageThumbnailUseNewMember = Configuration.getBoolean("events.event_welcome.welcomeMessageThumbnailUseNewMember");
    public static final boolean  pingUser = Configuration.getBoolean("events.event_welcome.pingUser");
    public static final String MESSAGE_TITLE = Configuration.getString("events.event_welcome.welcomeMessageTitle");
    public static final String MESSAGE_DESC = Configuration.getString("events.event_welcome.welcomeMessageDesc");


    @Override
    public void onGuildMemberJoin( GuildMemberJoinEvent e) {
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        RoleUtil.giveRolesLongs(ROLES_TO_GIVE, e.getMember());

        EmbedBuilder Embed = new EmbedBuilder()
                .setTitle(Placeholders.convert(MESSAGE_TITLE, e.getUser()))
                .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                .setFooter(GlobalConfig.EMBED_FOOTER)
                .setDescription(Placeholders.convert(MESSAGE_DESC, e.getUser()))
                .setTimestamp(ZonedDateTime.now());

        if (welcomeMessageThumbnailUseNewMember) {
            Embed.setThumbnail(e.getUser().getEffectiveAvatarUrl());
        } else {
            Embed.setThumbnail(GlobalConfig.EMBED_THUMBNAIL.equals("") ? null : GlobalConfig.EMBED_THUMBNAIL);
        }
        if (pingUser) {
            e.getGuild().getTextChannelById(WELCOME_CHANNEL).sendMessage(e.getMember().getAsMention()).complete();
        }
        e.getGuild().getTextChannelById(WELCOME_CHANNEL).sendMessage(Embed.build()).completeAfter(1, TimeUnit.SECONDS);
    }
}
