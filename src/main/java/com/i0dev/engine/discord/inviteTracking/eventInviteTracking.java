package com.i0dev.engine.discord.inviteTracking;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class eventInviteTracking extends ListenerAdapter {

    public static final boolean EVENT_ENABLED = Configuration.getBoolean("events.event_inviteTracking.enabled");
    public static final boolean LOGENABLED = Configuration.getBoolean("events.event_inviteTracking.logMessageEnabled");
    public static final String trackingMessageContent = Configuration.getString("events.event_inviteTracking.trackingMessageContent");
    private final Long trackingChannelID = Configuration.getLong("channels.inviteTrackingChannelID");

    @Override
    public void onGuildMemberLeave(final GuildMemberLeaveEvent e) {
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        DPlayer object = DPlayerEngine.getInstance().getObject(e.getUser());
        if (object == null) return;
        User inviter = e.getJDA().getUserById(object.getInvitedByDiscordID());
        if (inviter == null) return;
        DPlayerEngine.getInstance().decreaseInvites(inviter);

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

                DPlayer dpLayer = DPlayerEngine.getInstance().getObject(retrievedInvite.getInviter());
                dpLayer.setPoints(dpLayer.getPoints() + inviteUser);
                LogsFile.logPoints(retrievedInvite.getInviter().getAsTag() + " has received " + inviteUser + " points for inviting " + e.getUser().getAsTag());

                if (LOGENABLED) {
                    DPlayerEngine.getInstance().increaseInvites(retrievedInvite.getInviter());

                    e.getGuild().getTextChannelById(trackingChannelID).sendMessage(EmbedFactory.createEmbed(Placeholders.convert(trackingMessageContent
                            .replace("{inviteCount}", DPlayerEngine.getInstance().getObject(retrievedInvite.getInviter()).getInviteCount() + "")
                            .replace("{inviterTag}", retrievedInvite.getInviter().getAsTag()), e.getUser())).build()).
                            queue();
                }
                DPlayerEngine.getInstance().updateInviter(e.getUser(), retrievedInvite.getInviter());
                break;
            }
        });
    }
}
