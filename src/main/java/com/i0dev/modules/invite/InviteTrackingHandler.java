package com.i0dev.modules.invite;

import com.i0dev.Engine;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.objects.LogObject;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

public class InviteTrackingHandler extends ListenerAdapter {

    public static final boolean EVENT_ENABLED = Configuration.getBoolean("events.event_inviteTracking.enabled");
    public static final boolean LOGENABLED = Configuration.getBoolean("events.event_inviteTracking.logMessageEnabled");
    public static final String trackingMessageContent = Configuration.getString("events.event_inviteTracking.trackingMessageContent");
    private final Long trackingChannelID = Configuration.getLong("channels.inviteTrackingChannelID");

    @Override
    public void onGuildMemberLeave(final GuildMemberLeaveEvent e) {
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        DPlayer object = DPlayerEngine.getObject(e.getUser().getIdLong());
        if (object == null) return;
        User inviter = e.getJDA().getUserById(object.getInvitedByDiscordID());
        if (inviter == null) return;
        DPlayerEngine.decrease(inviter.getIdLong(), "invites");
        DPlayer dpLayer = DPlayerEngine.getObject(inviter.getIdLong());
        dpLayer.setPoints(dpLayer.getPoints() - inviteUser);

        String message = ("[{tag}] has lost [{points}] points due to {left} leaving the discord."
                .replace("{tag}", inviter.getAsTag())
                .replace("{points}", inviteUser + "")
                .replace("{left}", e.getUser().getAsTag()));

        message = "" + FormatUtil.formatDate(System.currentTimeMillis()) + ": " + message;
        Engine.getToLog().add(new LogObject(message, new File(InitializeBot.get().getPointLogPath())));

    }

    private static final double inviteUser = Configuration.getDouble("events.pointEvents.giveAmounts.inviteUser");

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent e) {
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;

        e.getGuild().retrieveInvites().queue(retrievedInvites -> {
            for (final Invite retrievedInvite : retrievedInvites) {
                final String code = retrievedInvite.getCode();
                final InviteData cachedInvite = InviteTracking.inviteCache.get(code);
                if (cachedInvite == null) {
                    continue;
                }
                if (retrievedInvite.getUses() == cachedInvite.getUses())
                    continue;
                cachedInvite.incrementUses();

                DPlayer dpLayer = DPlayerEngine.getObject(retrievedInvite.getInviter().getIdLong());
                dpLayer.setPoints(dpLayer.getPoints() + inviteUser);

                String message = ("[{tag}] has received [{points}] points due to {join} joining the discord."
                        .replace("{tag}", retrievedInvite.getInviter().getAsTag())
                        .replace("{points}", inviteUser + "")
                        .replace("{join}", e.getUser().getAsTag()));

                message = "" + FormatUtil.formatDate(System.currentTimeMillis()) + ": " + message;
                Engine.getToLog().add(new LogObject(message, new File(InitializeBot.get().getPointLogPath())));
                if (LOGENABLED) {
                    DPlayerEngine.increment(retrievedInvite.getInviter().getIdLong(), "invites");

                    e.getGuild().getTextChannelById(trackingChannelID).sendMessage(EmbedFactory.createEmbed(Placeholders.convert(trackingMessageContent
                            .replace("{inviteCount}", DPlayerEngine.getObject(retrievedInvite.getInviter().getIdLong()).getInviteCount() + "")
                            .replace("{inviterTag}", retrievedInvite.getInviter().getAsTag()), e.getUser())).build()).
                            queue();
                }
                DPlayerEngine.updateInviter(e.getUser().getIdLong() , retrievedInvite.getInviter());
                break;
            }
        });
    }
}
