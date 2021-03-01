package main.java.com.i0dev.event.welcome;

import com.sun.istack.internal.NotNull;
import main.java.com.i0dev.util.Placeholders;
import main.java.com.i0dev.util.RoleUtil;
import main.java.com.i0dev.util.conf;
import main.java.com.i0dev.util.getConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class eventWelcome extends ListenerAdapter {

    private final String Identifier = "Welcome";
    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("events.event_welcome.enabled");
    private final String WELCOME_CHANNEL = getConfig.get().getString("channels.welcomeChannelID");
    private final List<Long> ROLES_TO_GIVE = getConfig.get().getLongList("events.event_welcome.rolesToGive");
    private final boolean welcomeMessageThumbnailUseNewMember = getConfig.get().getBoolean("events.event_welcome.welcomeMessageThumbnailUseNewMember");
    private final boolean pingUser = getConfig.get().getBoolean("events.event_welcome.pingUser");
    private final String MESSAGE_TITLE = getConfig.get().getString("events.event_welcome.welcomeMessageTitle");
    private final String MESSAGE_DESC = getConfig.get().getString("events.event_welcome.welcomeMessageDesc");


    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        RoleUtil.giveRolesLongs(ROLES_TO_GIVE, e.getMember());

        EmbedBuilder Embed = new EmbedBuilder()
                .setTitle(Placeholders.convert(MESSAGE_TITLE, e.getUser()))
                .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                .setFooter(conf.EMBED_FOOTER)
                .setDescription(Placeholders.convert(MESSAGE_DESC, e.getUser()))
                .setTimestamp(ZonedDateTime.now());
        if (welcomeMessageThumbnailUseNewMember) {
            Embed.setThumbnail(e.getUser().getEffectiveAvatarUrl());
        } else {
            Embed.setThumbnail(conf.EMBED_THUMBNAIL);
        }
        if (pingUser) {
            e.getGuild().getTextChannelById(WELCOME_CHANNEL).sendMessage(e.getMember().getAsMention()).complete();
        }
        e.getGuild().getTextChannelById(WELCOME_CHANNEL).sendMessage(Embed.build()).completeAfter(1, TimeUnit.SECONDS);
    }
}
