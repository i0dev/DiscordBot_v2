package com.i0dev.modules;

import com.i0dev.commands.discord.completedModules.gamemode.GamemodeManager;
import com.i0dev.commands.discord.completedModules.giveaway.GiveawayManager;
import com.i0dev.commands.discord.completedModules.linking.LinkManager;
import com.i0dev.commands.discord.completedModules.movements.MovementManager;
import com.i0dev.commands.discord.completedModules.mute.MuteManager;
import com.i0dev.commands.discord.completedModules.screenshare.ScreenshareManager;
import com.i0dev.commands.discord.completedModules.suggestion.SuggestionManager;
import com.i0dev.commands.discord.completedModules.tebex.TebexManager;
import com.i0dev.commands.discord.completedModules.warn.WarnManager;
import com.i0dev.modules.applications.CommandAccept;
import com.i0dev.modules.applications.CommandApply;
import com.i0dev.modules.applications.CommandReject;
import com.i0dev.modules.basic.*;
import com.i0dev.modules.blacklist.BlacklistManager;
import com.i0dev.modules.boosting.BoostingManager;
import com.i0dev.modules.creators.PollCreator;
import com.i0dev.modules.creators.ReactionRoles;
import com.i0dev.modules.fun.*;
import com.i0dev.modules.invite.CommandInviteLeaderboard;
import com.i0dev.modules.invite.CommandInvites;
import com.i0dev.modules.invite.CommandInvitesResetData;
import com.i0dev.modules.mapPoints.MapPointsManager;
import com.i0dev.modules.moderation.*;
import com.i0dev.modules.points.discord.PointsManager;
import com.i0dev.modules.ticket.*;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommandManager extends ListenerAdapter {


    private boolean isCommand(List<String> aliases, Message message) {
        for (String alias : aliases) {
            if ((GlobalConfig.GENERAL_BOT_PREFIX + alias.toLowerCase()).equalsIgnoreCase(message.getContentRaw().split(" ")[0])) {
                return true;
            }
        }
        return false;
    }

    private boolean isCommand(String alias, Message message) {
        return (GlobalConfig.GENERAL_BOT_PREFIX + alias).equalsIgnoreCase(message.getContentRaw().split(" ")[0]);
    }

    public static boolean isGuild(Guild guild) {
        List<Long> temp = new ArrayList<>();
        temp.add(GlobalConfig.GENERAL_MAIN_GUILD.getIdLong());
        return temp.contains(guild.getIdLong());
    }

    //Basic Commands
    public static final List<String> AVATAR_COMMAND_ALIASES = Configuration.getStringList("commands.avatar.aliases");
    public static final List<String> MEMBER_COUNT_COMMAND_ALIASES = Configuration.getStringList("commands.memberCount.aliases");
    public static final List<String> RELOAD_CONFIG_COMMAND_ALIASES = Configuration.getStringList("commands.reloadConfig.aliases");
    public static final List<String> ROLE_INFO_COMMAND_ALIASES = Configuration.getStringList("commands.roleInfo.aliases");
    public static final List<String> ROLES_COMMAND_ALIASES = Configuration.getStringList("commands.roles.aliases");
    public static final List<String> SERVER_INFO_COMMAND_ALIASES = Configuration.getStringList("commands.serverInfo.aliases");
    public static final List<String> SERVER_IP_COMMAND_ALIASES = Configuration.getStringList("commands.serverIP.aliases");
    public static final List<String> USER_INFO_COMMAND_ALIASES = Configuration.getStringList("commands.userInfo.aliases");
    public static final List<String> HELP_ALIASES = Configuration.getStringList("commands.help.aliases");
    public static final List<String> PROFILE_ALIASES = Configuration.getStringList("commands.profile.aliases");
    public static final List<String> HEAP_DUMP_ALIASES = Configuration.getStringList("commands.heapDumpToDiscord.aliases");
    public static final List<String> EMBED_MAKER_ALIASES = Configuration.getStringList("commands.embedMaker.aliases");
    public static final List<String> REWARDS_ALIASES = Configuration.getStringList("commands.rewards.aliases");
    public static final List<String> RECLAIM_ALIASES = Configuration.getStringList("commands.reclaim.aliases");
    public static final List<String> RECLAIM_RESET_ALIASES = Configuration.getStringList("commands.reclaim_reset.aliases");

    //Fun Commands
    public static final List<String> EIGHTBALL_COMMAND_ALIASES = Configuration.getStringList("commands.fun_8ball.aliases");
    public static final List<String> COINFLIP_COMMAND_ALIASES = Configuration.getStringList("commands.fun_coinFlip.aliases");
    public static final List<String> DICE_COMMAND_ALIASES = Configuration.getStringList("commands.fun_dice.aliases");
    public static final List<String> HEY_COMMAND_ALIASES = Configuration.getStringList("commands.fun_hey.aliases");
    public static final List<String> PAT_COMMAND_ALIASES = Configuration.getStringList("commands.fun_pat.aliases");
    public static final List<String> SLAP_COMMAND_ALIASES = Configuration.getStringList("commands.fun_slap.aliases");

    //Invite Commands
    public static final List<String> INVITE_LEADERBOARD_ALIASES = Configuration.getStringList("commands.inviteLeaderboard.aliases");
    public static final List<String> INVITES_ALIASES = Configuration.getStringList("commands.invites.aliases");
    public static final List<String> INVITES_RESET_ALIASES = Configuration.getStringList("commands.invite_resetData.aliases");

    //Minecraft API
    public static final List<String> MC_SERVER_INFO_ALIASES = Configuration.getStringList("commands.MCServerInfo.aliases");
    public static final List<String> RUN_COMMAND_ALIASES = Configuration.getStringList("commands.runIngameCommand.aliases");

    //Creators
    public static final List<String> POLL_CREATOR_ALIASES = Configuration.getStringList("commands.pollCreator.aliases");
    public static final List<String> REACTION_ROLE_CREATOR_ALIASES = Configuration.getStringList("commands.reactionRoles.aliases");

    //Moderation
    public static final List<String> ANNOUNCE_ALIASES = Configuration.getStringList("commands.announce.aliases");
    public static final List<String> BAN_ALIASES = Configuration.getStringList("commands.ban.aliases");
    public static final List<String> CHANGELOG_ALIASES = Configuration.getStringList("commands.changelog.aliases");
    public static final List<String> KICK_ALIASES = Configuration.getStringList("commands.kick.aliases");
    public static final List<String> PRUNE_ALIASES = Configuration.getStringList("commands.prune.aliases");
    public static final List<String> ROLE_ALL_ALIASES = Configuration.getStringList("commands.role_all.aliases");
    public static final List<String> UNBAN_ALIASES = Configuration.getStringList("commands.unban.aliases");
    public static final List<String> VERIFY_PANEL_ALIASES = Configuration.getStringList("commands.createVerifyPanel.aliases");

    //Temporary Application
    public static final List<String> APPLY_ALIASES = Configuration.getStringList("commands.apply.aliases");
    public static final List<String> REJECT_ALIASES = Configuration.getStringList("commands.reject.aliases");
    public static final List<String> ACCEPT_ALIASES = Configuration.getStringList("commands.accept.aliases");

    //Ticket Commands
    public static final List<String> CREATE_PANEL_ALIASES = Configuration.getStringList("commands.createTicketPanel.aliases");
    public static final List<String> TICKET_ADD_ALIASES = Configuration.getStringList("commands.ticketAdd.aliases");
    public static final List<String> ADMIN_ONLY_ALIASES = Configuration.getStringList("commands.ticketAdminOnly.aliases");
    public static final List<String> TICKET_CLOSE_ALIASES = Configuration.getStringList("commands.ticketClose.aliases");
    public static final List<String> TICKET_INFO_ALIASES = Configuration.getStringList("commands.ticketInfo.aliases");
    public static final List<String> TICKET_REMOVE_ALIASES = Configuration.getStringList("commands.ticketRemove.aliases");
    public static final List<String> TICKET_RENAME_ALIASES = Configuration.getStringList("commands.ticketRename.aliases");
    public static final List<String> TICKET_TOP_LEADERBOARD_ALIASES = Configuration.getStringList("commands.ticketTopLeaderboard.aliases");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!isGuild(e.getGuild())) return;
        if (DPlayerEngine.getObject(e.getAuthor().getIdLong()).isBlacklisted()) return;

        if (e.getMember().hasPermission(Permission.ADMINISTRATOR) && e.getMessage().getContentRaw().equals(".panic")) {
            PointsManager.setPanic(true);
            e.getChannel().sendMessage(e.getAuthor().getAsTag() + " YOU PANICKED AND DISABLED DAH POINTS SYSTEM :x:").queue();
        }
        if (e.getMember().hasPermission(Permission.ADMINISTRATOR) && e.getMessage().getContentRaw().equals(".unpanic")) {
            PointsManager.setPanic(false);
            e.getChannel().sendMessage(e.getAuthor().getAsTag() + " YOU PANICKED AND ENABLED DAH POINTS SYSTEM :white_check_mark:").queue();
        }
        //Moderation
        if (isCommand(ANNOUNCE_ALIASES, e.getMessage())) CommandAnnounce.run(e);
        else if (isCommand(EMBED_MAKER_ALIASES, e.getMessage())) CommandEmbedMaker.run(e);
        else if (isCommand(BAN_ALIASES, e.getMessage())) CommandBan.run(e);
        else if (isCommand(CHANGELOG_ALIASES, e.getMessage())) CommandChangelog.run(e);
        else if (isCommand(KICK_ALIASES, e.getMessage())) CommandKick.run(e);
        else if (isCommand(PRUNE_ALIASES, e.getMessage())) CommandPrune.run(e);
        else if (isCommand(ROLE_ALL_ALIASES, e.getMessage())) CommandRoleAll.run(e);
        else if (isCommand(UNBAN_ALIASES, e.getMessage())) CommandUnban.run(e);

        else if (isCommand(VERIFY_PANEL_ALIASES, e.getMessage())) CommandVerifyPanel.run(e);
        else if (isCommand(RECLAIM_RESET_ALIASES, e.getMessage())) CommandReclaimReset.run(e);
        else if (isCommand(RECLAIM_ALIASES, e.getMessage())) CommandReclaim.run(e);
            //Basic
        else if (isCommand(AVATAR_COMMAND_ALIASES, e.getMessage())) CommandAvatar.run(e);
        else if (isCommand(MEMBER_COUNT_COMMAND_ALIASES, e.getMessage())) CommandMemberCount.run(e);
        else if (isCommand(RELOAD_CONFIG_COMMAND_ALIASES, e.getMessage())) CommandReloadConfig.run(e);
        else if (isCommand(ROLE_INFO_COMMAND_ALIASES, e.getMessage())) CommandRoleInfo.run(e);
        else if (isCommand(USER_INFO_COMMAND_ALIASES, e.getMessage())) CommandUserInfo.run(e);
        else if (isCommand(SERVER_IP_COMMAND_ALIASES, e.getMessage())) CommandServerIP.run(e);
        else if (isCommand(SERVER_INFO_COMMAND_ALIASES, e.getMessage())) CommandServerInfo.run(e);
        else if (isCommand(ROLES_COMMAND_ALIASES, e.getMessage())) CommandRoles.run(e);
        else if (isCommand(HELP_ALIASES, e.getMessage())) CommandHelp.run(e);
        else if (isCommand(PROFILE_ALIASES, e.getMessage())) CommandProfile.run(e);
        else if (isCommand(HEAP_DUMP_ALIASES, e.getMessage())) CommandHeapDump.run(e);
        else if (isCommand(REWARDS_ALIASES, e.getMessage())) CommandRewards.run(e);
            //Fun
        else if (isCommand(EIGHTBALL_COMMAND_ALIASES, e.getMessage())) CommandEightBall.run(e);
        else if (isCommand(COINFLIP_COMMAND_ALIASES, e.getMessage())) CommandCoinflip.run(e);
        else if (isCommand(DICE_COMMAND_ALIASES, e.getMessage())) CommandDice.run(e);
        else if (isCommand(HEY_COMMAND_ALIASES, e.getMessage())) CommandHey.run(e);
        else if (isCommand(PAT_COMMAND_ALIASES, e.getMessage())) CommandPat.run(e);
        else if (isCommand(SLAP_COMMAND_ALIASES, e.getMessage())) CommandSlap.run(e);
            //Invites
        else if (isCommand(INVITE_LEADERBOARD_ALIASES, e.getMessage())) CommandInviteLeaderboard.run(e);
        else if (isCommand(INVITES_ALIASES, e.getMessage())) CommandInvites.run(e);
        else if (isCommand(INVITES_RESET_ALIASES, e.getMessage())) CommandInvitesResetData.run(e);
            //Minecraft
        else if (isCommand(MC_SERVER_INFO_ALIASES, e.getMessage())) CommandMcServerInfo.run(e);
        else if (isCommand(RUN_COMMAND_ALIASES, e.getMessage())) CommandRunCommand.run(e);
            //Creators
        else if (isCommand(POLL_CREATOR_ALIASES, e.getMessage())) PollCreator.run(e);
        else if (isCommand(REACTION_ROLE_CREATOR_ALIASES, e.getMessage())) ReactionRoles.run(e);
            //Temporary Application
        else if (isCommand(APPLY_ALIASES, e.getMessage())) CommandApply.run(e);
        else if (isCommand(ACCEPT_ALIASES, e.getMessage())) CommandAccept.run(e);
        else if (isCommand(REJECT_ALIASES, e.getMessage())) CommandReject.run(e);
            //Ticket Commands
        else if (isCommand(CREATE_PANEL_ALIASES, e.getMessage())) CommandTicketPanel.run(e);
        else if (isCommand(TICKET_ADD_ALIASES, e.getMessage())) CommandTicketAdd.run(e);
        else if (isCommand(ADMIN_ONLY_ALIASES, e.getMessage())) CommandAdminOnly.run(e);
        else if (isCommand(TICKET_CLOSE_ALIASES, e.getMessage())) CommandTicketClose.run(e);
        else if (isCommand(TICKET_INFO_ALIASES, e.getMessage())) CommandTicketInfo.run(e);
        else if (isCommand(TICKET_REMOVE_ALIASES, e.getMessage())) CommandTicketRemove.run(e);
        else if (isCommand(TICKET_RENAME_ALIASES, e.getMessage())) CommandTicketRename.run(e);
        else if (isCommand(TICKET_TOP_LEADERBOARD_ALIASES, e.getMessage())) CommandTicketTopLeaderboard.run(e);
            //dev
        else if (isCommand("ftop", e.getMessage())) CommandFtop.run(e);
            //Modules
        else if (isCommand("blacklist", e.getMessage())) BlacklistManager.run(e);
        else if (isCommand("gamemode", e.getMessage())) GamemodeManager.run(e);
        else if (isCommand("giveaway", e.getMessage())) GiveawayManager.run(e);
        else if (isCommand("link", e.getMessage())) LinkManager.run(e);
        else if (isCommand("movement", e.getMessage())) MovementManager.run(e);
        else if (isCommand("mute", e.getMessage())) MuteManager.run(e);
        else if (isCommand("screenshare", e.getMessage())) ScreenshareManager.run(e);
        else if (isCommand("suggestion", e.getMessage())) SuggestionManager.run(e);
        else if (isCommand("tebex", e.getMessage())) TebexManager.run(e);
        else if (isCommand("warn", e.getMessage())) WarnManager.run(e);
        else if (isCommand("points", e.getMessage())) PointsManager.run(e);
        else if (isCommand("mapPoints", e.getMessage())) MapPointsManager.run(e);
        else if (isCommand("boosting", e.getMessage())) BoostingManager.run(e);
    }
}
