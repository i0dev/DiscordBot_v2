package main.java.com.i0dev.util;

import main.java.com.i0dev.command.applications.applyResponses;
import main.java.com.i0dev.command.applications.cmdAccept;
import main.java.com.i0dev.command.applications.cmdApply;
import main.java.com.i0dev.command.applications.cmdReject;
import main.java.com.i0dev.command.basic.*;
import main.java.com.i0dev.command.cmdExportDiscordUsers;
import main.java.com.i0dev.command.cmdHelpPage;
import main.java.com.i0dev.command.cmdVersion;
import main.java.com.i0dev.command.fun.*;
import main.java.com.i0dev.command.gamemodeSpecific.*;
import main.java.com.i0dev.command.giveaways.cmdGiveawayCreator;
import main.java.com.i0dev.command.giveaways.giveawayCreatorResponses;
import main.java.com.i0dev.command.invite.cmdInviteLeaderboard;
import main.java.com.i0dev.command.invite.cmdInviteResetData;
import main.java.com.i0dev.command.invite.cmdInvites;
import main.java.com.i0dev.command.moderation.blacklist.cmdBlacklistAdd;
import main.java.com.i0dev.command.moderation.blacklist.cmdBlacklistClear;
import main.java.com.i0dev.command.moderation.blacklist.cmdBlacklistList;
import main.java.com.i0dev.command.moderation.blacklist.cmdBlacklistRemove;
import main.java.com.i0dev.command.moderation.*;
import main.java.com.i0dev.command.moderation.mute.cmdCreateMutedRole;
import main.java.com.i0dev.command.moderation.mute.cmdGetMuted;
import main.java.com.i0dev.command.moderation.mute.cmdMute;
import main.java.com.i0dev.command.moderation.mute.cmdUnmute;
import main.java.com.i0dev.command.movements.*;
import main.java.com.i0dev.command.polls.cmdPollCreator;
import main.java.com.i0dev.command.polls.pollCreatorResponses;
import main.java.com.i0dev.command.reactionroles.cmdReactionRoleCreator;
import main.java.com.i0dev.command.reactionroles.reactionRoleResponses;
import main.java.com.i0dev.command.ticket.*;
import main.java.com.i0dev.command.verify.cmdVerifyPanel;
import main.java.com.i0dev.command.verify.eventReactVerify;
import main.java.com.i0dev.event.automod.eventAutoMod;
import main.java.com.i0dev.event.inviteTracking.eventInviteTracking;
import main.java.com.i0dev.event.reactionroles.onReactionRole;
import main.java.com.i0dev.event.ticket.eventCloseTicket;
import main.java.com.i0dev.event.ticket.eventReactAdminOnly;
import main.java.com.i0dev.event.ticket.eventTicketCreate;
import main.java.com.i0dev.event.welcome.eventWelcome;
import main.java.com.i0dev.util.inviteutil.InviteTracking;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class initJDA {
    private final static initJDA instance = new initJDA();

    public static initJDA get() {
        return instance;
    }

    public JDA jda;

    public JDA getJda() {
        return jda;
    }

    public void createJDA() {
        try {
            jda = JDABuilder.create(getConfig.get().getString("general.token"),
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
                    .setActivity(Activity.watching(getConfig.get().getString("general.activity")))
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
        jda.addEventListener(new cmdAvatar());
        jda.addEventListener(new cmdReloadConfig());
        jda.addEventListener(new cmdRoles());
        jda.addEventListener(new cmdRoleInfo());
        jda.addEventListener(new cmdServerInfo());
        jda.addEventListener(new cmdUserInfo());
        jda.addEventListener(new cmdMemberCount());
        jda.addEventListener(new cmdSuggest());

        jda.addEventListener(new cmdBlacklistAdd());
        jda.addEventListener(new cmdBlacklistRemove());
        jda.addEventListener(new cmdBlacklistClear());
        jda.addEventListener(new cmdBlacklistList());

        jda.addEventListener(new cmdAnnounce());
        jda.addEventListener(new cmdBan());
        jda.addEventListener(new cmdUnban());
        jda.addEventListener(new cmdKick());
        jda.addEventListener(new cmdPrune());
        jda.addEventListener(new cmdMessageClear());

        jda.addEventListener(new cmdMute());
        jda.addEventListener(new cmdUnmute());
        jda.addEventListener(new cmdGetMuted());
        jda.addEventListener(new cmdCreateMutedRole());

        jda.addEventListener(new main.java.com.i0dev.command.ticket.cmdCreateTicketPanel());
        jda.addEventListener(new eventTicketCreate());
        jda.addEventListener(new cmdTicketRename());
        jda.addEventListener(new cmdTicketAdd());
        jda.addEventListener(new cmdTicketAdminOnly());
        jda.addEventListener(new cmdTicketInfo());
        jda.addEventListener(new cmdTicketRemove());
        jda.addEventListener(new eventReactAdminOnly());
        jda.addEventListener(new eventCloseTicket());
        jda.addEventListener(new cmdTicketClose());

        jda.addEventListener(new cmd8Ball());
        jda.addEventListener(new cmdCoinFlip());
        jda.addEventListener(new cmdHey());
        jda.addEventListener(new cmdPat());
        jda.addEventListener(new cmdSlap());

        jda.addEventListener(new cmdVersion());

        jda.addEventListener(new cmdPromote());
        jda.addEventListener(new cmdDemote());
        jda.addEventListener(new cmdAssign());
        jda.addEventListener(new cmdResign());
        jda.addEventListener(new cmdStaffClear());

        jda.addEventListener(new cmdVerifyPanel());
        jda.addEventListener(new eventReactVerify());

        jda.addEventListener(new eventWelcome());

        jda.addEventListener(new cmdFactionLeader());
        jda.addEventListener(new cmdConfirmFaction());
        jda.addEventListener(new cmdConfirmIsland());
        jda.addEventListener(new cmdIslandLeader());
        jda.addEventListener(new cmdConfirmCell());
        jda.addEventListener(new cmdCellLeader());

        jda.addEventListener(new cmdHelpPage());
        jda.addEventListener(new eventAutoMod());

        jda.addEventListener(new InviteTracking());
        jda.addEventListener(new eventInviteTracking());
        jda.addEventListener(new cmdInvites());
        jda.addEventListener(new cmdInviteResetData());
        jda.addEventListener(new cmdInviteLeaderboard());

        jda.addEventListener(new pollCreatorResponses());
        jda.addEventListener(new cmdPollCreator());

        jda.addEventListener(new giveawayCreatorResponses());
        jda.addEventListener(new cmdGiveawayCreator());

        jda.addEventListener(new reactionRoleResponses());
        jda.addEventListener(new cmdReactionRoleCreator());
        jda.addEventListener(new onReactionRole());

        jda.addEventListener(new applyResponses());
        jda.addEventListener(new cmdApply());
        jda.addEventListener(new cmdAccept());
        jda.addEventListener(new cmdReject());

        jda.addEventListener(new cmdExportDiscordUsers());

    }

}
