package com.i0dev.utility;

import com.i0dev.InitilizeBot;
import com.i0dev.command.discord.*;
import com.i0dev.command.discord.applications.applyResponses;
import com.i0dev.command.discord.applications.cmdAccept;
import com.i0dev.command.discord.applications.cmdApply;
import com.i0dev.command.discord.applications.cmdReject;
import com.i0dev.command.discord.basic.*;
import com.i0dev.command.discord.fun.*;
import com.i0dev.command.discord.gamemodeSpecific.*;
import com.i0dev.command.discord.giveaways.cmdGiveawayCreator;
import com.i0dev.command.discord.giveaways.cmdGiveawayReroll;
import com.i0dev.command.discord.giveaways.giveawayCreatorResponses;
import com.i0dev.command.discord.invite.cmdInviteLeaderboard;
import com.i0dev.command.discord.invite.cmdInviteResetData;
import com.i0dev.command.discord.invite.cmdInvites;
import com.i0dev.command.discord.minecraftAPI.CmdRunCmd;
import com.i0dev.command.discord.minecraftAPI.cmdMCServerInfo;
import com.i0dev.command.discord.moderation.blacklist.cmdBlacklistAdd;
import com.i0dev.command.discord.moderation.blacklist.cmdBlacklistClear;
import com.i0dev.command.discord.moderation.blacklist.cmdBlacklistList;
import com.i0dev.command.discord.moderation.blacklist.cmdBlacklistRemove;
import com.i0dev.command.discord.moderation.*;
import com.i0dev.command.discord.moderation.mute.cmdCreateMutedRole;
import com.i0dev.command.discord.moderation.mute.cmdGetMuted;
import com.i0dev.command.discord.moderation.mute.cmdMute;
import com.i0dev.command.discord.moderation.mute.cmdUnmute;
import com.i0dev.command.discord.moderation.screenshare.cmdScreenshareAdd;
import com.i0dev.command.discord.moderation.screenshare.cmdScreenshareClear;
import com.i0dev.command.discord.moderation.screenshare.cmdScreenshareList;
import com.i0dev.command.discord.moderation.screenshare.cmdScreenshareRemove;
import com.i0dev.command.discord.moderation.warn.cmdWarn;
import com.i0dev.command.discord.moderation.warn.cmdWarnClear;
import com.i0dev.command.discord.moderation.warn.cmdWarnList;
import com.i0dev.command.discord.moderation.warn.cmdWarnRemove;
import com.i0dev.command.discord.movements.*;
import com.i0dev.command.discord.polls.cmdPollCreator;
import com.i0dev.command.discord.polls.pollCreatorResponses;
import com.i0dev.command.discord.reactionroles.cmdReactionRoleCreator;
import com.i0dev.command.discord.reactionroles.reactionRoleResponses;
import com.i0dev.command.discord.tebex.*;
import com.i0dev.command.discord.ticket.*;
import com.i0dev.command.discord.verify.cmdVerifyPanel;
import com.i0dev.command.discord.verify.eventReactVerify;
import com.i0dev.event.discord.automod.eventAutoMod;
import com.i0dev.event.discord.inviteTracking.eventInviteTracking;
import com.i0dev.event.discord.reactionroles.onReactionRole;
import com.i0dev.event.discord.suggestion.EventAcceptSuggestion;
import com.i0dev.event.discord.ticket.eventCloseTicket;
import com.i0dev.event.discord.ticket.eventReactAdminOnly;
import com.i0dev.event.discord.ticket.eventTicketCreate;
import com.i0dev.event.discord.welcome.eventWelcome;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class InternalJDA {
    private final static InternalJDA instance = new InternalJDA();

    public static InternalJDA get() {
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

        jda.addEventListener(new cmdScreenshareAdd());
        jda.addEventListener(new cmdScreenshareRemove());
        jda.addEventListener(new cmdScreenshareClear());
        jda.addEventListener(new cmdScreenshareList());

        jda.addEventListener(new cmdWarn());
        jda.addEventListener(new cmdWarnRemove());
        jda.addEventListener(new cmdWarnClear());
        jda.addEventListener(new cmdWarnList());

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

        jda.addEventListener(new cmdCreateTicketPanel());
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
        jda.addEventListener(new cmdGiveawayReroll());

        jda.addEventListener(new reactionRoleResponses());
        jda.addEventListener(new cmdReactionRoleCreator());
        jda.addEventListener(new onReactionRole());

        jda.addEventListener(new applyResponses());
        jda.addEventListener(new cmdApply());
        jda.addEventListener(new cmdAccept());
        jda.addEventListener(new cmdReject());

        jda.addEventListener(new cmdExportDiscordUsers());

        jda.addEventListener(new tebexPlayerLookup());
        jda.addEventListener(new tebexTransactionLookup());
        jda.addEventListener(new tebexPackageLookup());
        jda.addEventListener(new tebexInfo());
        jda.addEventListener(new tebexGiftcardCreate());

        jda.addEventListener(new cmdMCServerInfo());
        jda.addEventListener(new CmdHeapDump());
        jda.addEventListener(new cmdChangelog());
        jda.addEventListener(new CmdProfile());
        jda.addEventListener(new CmdRoleAll());
        jda.addEventListener(new CmdAcceptSuggestion());
        jda.addEventListener(new EventAcceptSuggestion());
        jda.addEventListener(new CmdTicketTopLeaderboard());

        if (InitilizeBot.get().isPluginMode()) {
            jda.addEventListener(new CmdRunCmd());
        }

    }

}
