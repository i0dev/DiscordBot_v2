package main.java.com.i0dev.event.inviteTracking;

import main.java.com.i0dev.entity.InviteMatcher;
import main.java.com.i0dev.entity.Invites;
import main.java.com.i0dev.util.EmbedFactory;
import main.java.com.i0dev.util.Placeholders;
import main.java.com.i0dev.util.conf;
import main.java.com.i0dev.util.getConfig;
import main.java.com.i0dev.util.inviteutil.InviteData;
import main.java.com.i0dev.util.inviteutil.InviteTracking;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

public class eventInviteTracking extends ListenerAdapter {

    private final boolean EVENT_ENABLED = getConfig.get().getBoolean("events.event_inviteTracking.enabled");
    private final boolean LOGENABLED = getConfig.get().getBoolean("events.event_inviteTracking.logMessageEnabled");
    private final String trackingMessageContent = getConfig.get().getString("events.event_inviteTracking.trackingMessageContent");
    private final Long trackingChannelID = getConfig.get().getLong("channels.inviteTrackingChannelID");

    @Override
    public void onGuildMemberLeave(final GuildMemberLeaveEvent e) {
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        JSONObject object = InviteMatcher.get().getNewJoinObject(e.getUser());
        if (object == null) return;
        User inviter = e.getJDA().getUserById(object.get("invitedBy").toString());
        if (inviter == null) return;
        Invites.get().decreaseUser(inviter);
        System.out.println(1);
        InviteMatcher.get().removeNewJoin(e.getUser());

    }

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent e) {
        if (!EVENT_ENABLED) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;

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
                if (LOGENABLED) {
                    e.getGuild().getTextChannelById(trackingChannelID).sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(trackingMessageContent
                            .replace("{inviteCount}", Invites.get().getUserInviteCountAdd1(retrievedInvite.getInviter()) + "")
                            .replace("{inviterTag}", retrievedInvite.getInviter().getAsTag()), e.getUser())).build()).queue();
                }
                InviteMatcher.get().addNewUser(retrievedInvite.getInviter(), e.getUser());
                Invites.get().increaseUser(retrievedInvite.getInviter());
                break;
            }
        });
    }
}
