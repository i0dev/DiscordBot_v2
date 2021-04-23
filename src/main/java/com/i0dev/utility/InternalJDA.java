package com.i0dev.utility;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.commands.discord.CommandVersion;
import com.i0dev.commands.discord.basic.CommandHelp;
import com.i0dev.commands.discord.completedModules.giveaway.Create;
import com.i0dev.commands.discord.completedModules.suggestion.Accept;
import com.i0dev.commands.discord.completedModules.suggestion.Reject;
import com.i0dev.commands.discord.creators.PollCreator;
import com.i0dev.commands.discord.creators.ReactionRoles;
import com.i0dev.commands.discord.moderation.CommandExportData;
import com.i0dev.commands.discord.moderation.CommandMessageClear;
import com.i0dev.commands.discord.tempApplications.CommandApply;
import com.i0dev.engine.discord.automod.eventAutoMod;
import com.i0dev.engine.discord.eventReactVerify;
import com.i0dev.engine.discord.inviteTracking.eventInviteTracking;
import com.i0dev.engine.discord.reactionroles.onReactionRole;
import com.i0dev.engine.discord.ticket.eventCloseTicket;
import com.i0dev.engine.discord.ticket.eventReactAdminOnly;
import com.i0dev.engine.discord.ticket.eventTicketCreate;
import com.i0dev.engine.discord.welcome.eventWelcome;
import com.i0dev.pointSystem.EventHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class InternalJDA {
    private final static InternalJDA instance = new InternalJDA();

    public static InternalJDA get() {
        return instance;
    }

    public JDA jda = null;

    public JDA getJda() {
        return jda;
    }

    public void createJDA() {
        try {
            jda = JDABuilder.create(Configuration.getString("general.token"),
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.GUILD_BANS,
                    GatewayIntent.GUILD_EMOJIS,
                    GatewayIntent.GUILD_PRESENCES,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_INVITES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_MESSAGE_TYPING,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.DIRECT_MESSAGE_TYPING,
                    GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setContextEnabled(true)
                    //.setActivity(Activity.of(Activity.ActivityType.valueOf(Configuration.getString("general.activityType")), Configuration.getString("general.activity")))
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(
                            CacheFlag.ACTIVITY,
                            CacheFlag.VOICE_STATE,
                            CacheFlag.MEMBER_OVERRIDES,
                            CacheFlag.EMOTE,
                            CacheFlag.CLIENT_STATUS)
                    .build()
                    .awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void registerListeners() {

        jda.addEventListener(new CommandMessageClear());
        jda.addEventListener(new eventTicketCreate());
        jda.addEventListener(new eventReactAdminOnly());
        jda.addEventListener(new eventCloseTicket());
        jda.addEventListener(new CommandVersion());
        jda.addEventListener(new eventReactVerify());
        jda.addEventListener(new eventWelcome());
        jda.addEventListener(new CommandHelp());
        jda.addEventListener(new eventAutoMod());
        jda.addEventListener(new InviteTracking());
        jda.addEventListener(new eventInviteTracking());
        jda.addEventListener(new PollCreator());
        jda.addEventListener(new ReactionRoles());
        jda.addEventListener(new onReactionRole());
        jda.addEventListener(new CommandExportData());
        jda.addEventListener(new Accept());
        jda.addEventListener(new Reject());
        jda.addEventListener(new Create());
        jda.addEventListener(new CommandApply());


        jda.addEventListener(new DiscordCommandManager());

        jda.addEventListener(new EventHandler());
    }
}
