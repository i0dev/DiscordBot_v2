package main.java.com.i0dev.event.inviteTracking;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InviteTracking extends ListenerAdapter {
    public static final Map<String, InviteData> inviteCache = new ConcurrentHashMap<>();                    // initialize a thread safe Map for invites; key - a String, invite's code; value - InviteData object to prevent storing jda entities

    // keep in mind that invite events will only fire for channels which your bot has MANAGE_CHANNEL perm in
    @Override
    public void onGuildInviteCreate(final GuildInviteCreateEvent event)                               // gets fired when an invite is created, lets cache it
    {
        final String code = event.getCode();                                                          // get invite's code
        final InviteData inviteData = new InviteData(event.getInvite());                              // create an InviteData object for the invite
        inviteCache.put(code, inviteData);                                                            // put code as a key and InviteData object as a value into the map; cache
    }

    @Override
    public void onGuildInviteDelete(final GuildInviteDeleteEvent event)                               // gets fired when an invite is deleted, lets uncache it
    {
        final String code = event.getCode();                                                          // get invite's code
        inviteCache.remove(code);                                                                     // remove map entry based on deleted invite's code; uncache
    }

    @Override
    public void onGuildReady(final GuildReadyEvent event)                                             // gets fired when a guild has finished setting up upon booting the bot, lets try to cache its invites
    {
        final Guild guild = event.getGuild();                                                         // get the guild that has finished setting up
        attemptInviteCaching(guild);                                                                  // attempt to store guild's invites
    }



    @Override
    public void onGuildLeave(final GuildLeaveEvent event)                                             // gets fired when your bot has left a guild, uncache all invites for it
    {
        final long guildId = event.getGuild().getIdLong();                                            // get the id of the guild your bot has left
        inviteCache.entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);           // remove entry from the map if its value's guild id is the one your bot has left
    }

    public static void attemptInviteCaching(final Guild guild)                                              // helper method to prevent duplicate code for GuildReadyEvent and GuildJoinEvent
    {
        final Member selfMember = guild.getSelfMember();                                              // get your bot's member object for this guild

        if (!selfMember.hasPermission(Permission.MANAGE_SERVER))                                      // check if your bot doesn't have MANAGE_SERVER permission to retrieve the invites, if true, return
            return;

        guild.retrieveInvites().queue(retrievedInvites ->                                             // retrieve all guild's invites
        {
            retrievedInvites.forEach(retrievedInvite ->                                               // iterate over invites..
                    inviteCache.put(retrievedInvite.getCode(), new InviteData(retrievedInvite)));     // and store them
        });
    }
}