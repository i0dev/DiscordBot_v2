package main.java.com.i0dev.command;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;

public class cmdHelpPage extends ListenerAdapter {


    private String page1() {
        StringBuilder desc = new StringBuilder();
        String page = "1";
        desc.append("`<>`: arg required.\n");
        desc.append("`[]`: arg optional.\n");
        desc.append(enabledEmoji).append(": command enabled.\n");
        desc.append(disabledEmoji).append(": command disabled.\n\n");
        desc.append("**Basic Commands:**\n");
        desc.append("{enabled}``{cmd} [user]`` - *Get the avatar of yourself or a user.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.avatar.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.avatar.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Get a list of all roles.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.roles.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.roles.aliases").get(0)));
        desc.append("{enabled}``{cmd} <role>`` - *Get information about a role.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.roleInfo.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.roleInfo.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Get information about the server.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.serverInfo.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.serverInfo.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Get information about a user.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.userInfo.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.userInfo.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Gets the member count of the server.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.memberCount.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.memberCount.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Sends the server's IP address.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.serverIP.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.serverIP.aliases").get(0)));
        desc.append("{enabled}``{cmd} <gamemode> <suggestion>`` - *Create a suggestion.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.suggest.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.suggest.aliases").get(0)));
        desc.append("\n**Fun Commands:**\n");
        desc.append("{enabled}``{cmd} <user>`` - *Slap that user.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.fun_slap.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.fun_slap.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Role a die.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.fun_dice.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.fun_dice.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Pat that user.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.fun_pat.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.fun_pat.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Say hello to the bot.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.fun_hey.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.fun_hey.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Flip a coin.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.fun_coinFlip.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.fun_coinFlip.aliases").get(0)));
        desc.append("{enabled}``{cmd} <Question>`` - *Ask the magic8ball a question.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.fun_8ball.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.fun_8ball.aliases").get(0)));
        desc.append("\n**Gamemode Specific:**\n");
        desc.append("{enabled}``{cmd} <user>`` - *Give them the Faction Leader Role.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.factionLeader.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.factionLeader.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Give them the Island Leader Role.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.islandLeader.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.islandLeader.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Give them the Cell Leader Role.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.cellLeader.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.cellLeader.aliases").get(0)));
        desc.append("{enabled}``{cmd} <leader> <faction> <rosterSize>``\n - *Confirm that Faction as playing.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.confirmFaction.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.confirmFaction.aliases").get(0)));
        desc.append("{enabled}``{cmd} <leader> <island> <rosterSize>``\n - *Confirm that Island as playing.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.confirmIsland.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.confirmIsland.aliases").get(0)));
        desc.append("{enabled}``{cmd} <leader> <cell> <rosterSize>``\n - *Confirm that Cell as playing.*".replace("{enabled}", (getConfig.get().getBoolean("commands.confirmCell.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.confirmCell.aliases").get(0)));
        desc.append("\n**End of page ").append(page).append(":**\n*Use ``").append(conf.GENERAL_BOT_PREFIX).append("help [page]`` to go to other pages!*");
        desc.append("\n\n**Page 1 contents:** ``Basic, Fun, GameMode Specific``");
        desc.append("\n**Page 2 contents:** ``Moderation``");
        desc.append("\n**Page 3 contents:** ``Movements, Ticket, Invite, Other``");
        return desc.toString();
    }

    private String page2() {
        StringBuilder desc = new StringBuilder();
        String page = "2";
        desc.append("`<>`: arg required.\n");
        desc.append("`[]`: arg optional.\n");
        desc.append(enabledEmoji).append(": command enabled.\n");
        desc.append(disabledEmoji).append(": command disabled.\n\n");
        desc.append("**Moderation Commands:**\n");
        desc.append("{enabled}``{cmd} <user> [reason]`` - *Ban a user from the server.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.ban.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.ban.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Unban a user from the server.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.unban.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.unban.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user> [reason]`` - *Kick a user from the server.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.kick.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.kick.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Clear channel messages.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.messageClear.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.messageClear.aliases").get(0)));
        desc.append("{enabled}``{cmd} <amt> [user]`` - *Delete that number of messages.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.prune.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.prune.aliases").get(0)));
        desc.append("\n");
        desc.append("{enabled}``{cmd} <user>`` - *Add a user to the blacklist of using bot commands.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.blacklist_add.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.blacklist_add.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Remove a user from the blacklist.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.blacklist_remove.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.blacklist_remove.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Clear list.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.blacklist_clear.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.blacklist_clear.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *List blacklisted users.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.blacklist_list.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.blacklist_list.aliases").get(0)));
        desc.append("\n");
        desc.append("{enabled}``{cmd} <user>`` - *Mute the user.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.mute.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.mute.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Unmute the user.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.unmute.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.unmute.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *List all the muted users.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.getMuted.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.getMuted.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Create a muted role.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.createMutedRole.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.createMutedRole.aliases").get(0)));
        desc.append("\n");
        desc.append("{enabled}``{cmd} <user>`` - *Add a user to the screenshare list.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.screenshare_add.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.screenshare_add.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Remove a user to the screenshare list.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.screenshare_remove.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.screenshare_remove.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Clear list.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.screenshare_clear.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.screenshare_clear.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Send list.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.screenshare_list.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.screenshare_list.aliases").get(0)));
        desc.append("\n");
        desc.append("{enabled}``{cmd} <user> [reason]`` - *Warn User*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.warn.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.warn.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Remove a warn from a user*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.removeWarn.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.removeWarn.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Clear list.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.warns_clear.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.warns_clear.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Send list.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.warns_list.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.warns_list.aliases").get(0)));
        desc.append("\n**End of page ").append(page).append(":**\n*Use ``").append(conf.GENERAL_BOT_PREFIX).append("help [page]`` to go to other pages!*");
        desc.append("\n\n**Page 1 contents:** ``Basic, Fun, GameMode Specific``");
        desc.append("\n**Page 2 contents:** ``Moderation``");
        desc.append("\n**Page 3 contents:** ``Movements, Ticket, Invite, Other``");
        return desc.toString();
    }

    private String page3() {
        StringBuilder desc = new StringBuilder();
        String page = "3";
        desc.append("`<>`: arg required.\n");
        desc.append("`[]`: arg optional.\n");
        desc.append(enabledEmoji).append(": command enabled.\n");
        desc.append(disabledEmoji).append(": command disabled.\n\n");
        desc.append("**Movement Commands:**\n");
        desc.append("{enabled}``{cmd} <user> <role>`` - *Assign a user to that role.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.assign.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.assign.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Demotes the user once.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.demote.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.demote.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user> `` - *Promotes the user once.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.promote.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.promote.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Resign that user.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.resign.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.resign.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Fully demote that user.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.staffClear.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.staffClear.aliases").get(0)));
        desc.append("\n**Ticket Commands:**\n");
        desc.append("{enabled}``{cmd}`` - *Create the ticket panel.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.createTicketPanel.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.createTicketPanel.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Adds a user to the ticket.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.ticketAdd.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.ticketAdd.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user>`` - *Removes a user to the ticket.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.ticketRemove.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.ticketRemove.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Switch the ticket to admin only mode.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.ticketAdminOnly.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.ticketAdminOnly.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Give ticket info.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.ticketInfo.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.ticketInfo.aliases").get(0)));
        desc.append("{enabled}``{cmd} <name>`` - *Renames the ticket.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.ticketRename.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.ticketRename.aliases").get(0)));
        desc.append("{enabled}``{cmd} [reason]`` - *Closes the ticket.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.ticketClose.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.ticketClose.aliases").get(0)));
        desc.append("\n**Invite Commands:**\n");
        desc.append("{enabled}``{cmd} [user]`` - *Gets the invites of a user.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.invites.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.invites.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Sends the invites leaderboard.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.inviteLeaderboard.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.inviteLeaderboard.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Resets all invite data.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.invite_resetData.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.invite_resetData.aliases").get(0)));
        desc.append("\n**Other Commands:**\n");
        desc.append("{enabled}``{cmd}`` - *Create the verify panel.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.createVerifyPanel.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.createVerifyPanel.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Starts a poll creator.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.pollCreator.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.pollCreator.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Starts a giveaway creator.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.gcreate.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.gcreate.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Starts a reaction role creato.r*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.reactionRoles.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.reactionRoles.aliases").get(0)));
        desc.append("{enabled}``{cmd}`` - *Create an Application.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.apply.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.apply.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user> <role> [response]`` - *Accept a users app.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.accept.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.accept.aliases").get(0)));
        desc.append("{enabled}``{cmd} <user> [response]`` - *Rejects a users app.*\n".replace("{enabled}", (getConfig.get().getBoolean("commands.reject.enabled") + "").replace("true", enabledEmoji).replace("false", disabledEmoji)).replace("{cmd}", conf.GENERAL_BOT_PREFIX + getConfig.get().getStringList("commands.reject.aliases").get(0)));
        desc.append(enabledEmoji).append("``").append(conf.GENERAL_BOT_PREFIX).append("version`` - *Sends the version of the bot.*\n");
        desc.append("\n**End of page ").append(page).append(":**\n*Use ``").append(conf.GENERAL_BOT_PREFIX).append("help [page]`` to go to other pages!*");
        desc.append("\n\n**Page 1 contents:** ``Basic, Fun, GameMode Specific``");
        desc.append("\n**Page 2 contents:** ``Moderation``");
        desc.append("\n**Page 3 contents:** ``Movements, Ticket, Invite, Other``");
        return desc.toString();
    }

    private final String Identifier = "Help";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.help.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.help.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.help.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.help.helpPageTitle");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.help.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.help.enabled");
    private final String enabledEmoji = getConfig.get().getString("commands.help.enabledEmoji");
    private final String disabledEmoji = getConfig.get().getString("commands.help.disabledEmoji");

    public boolean isHelpMessage(Message reactionMessage) {
        for (Message message : HelpPageCache.get().getList()) {
            try {
                if (reactionMessage.equals(message)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (Blacklist.get().isBlacklisted(e.getUser())) return;
        if (!COMMAND_ENABLED) return;
        if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        Message message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
        if (!isHelpMessage(message)) return;
        if (message.getReactions().size() > 1) {
            message.clearReactions().queue();
            if (e.getReactionEmote().getEmoji().equals("\u2B05")) {
                message.clearReactions().queue();
                message.editMessage(EmbedFactory.get().createSimpleEmbed(MESSAGE_TITLE.replace("{page}", "1"), page1()).build()).queue(message1 -> {
                            message1.addReaction("\u27A1").queue();
                        }
                );
            } else if (e.getReactionEmote().getEmoji().equals("\u27A1")) {
                message.editMessage(EmbedFactory.get().createSimpleEmbed(MESSAGE_TITLE.replace("{page}", "3"), page3()).build()).queue(message1 -> {
                            message1.addReaction("\u2B05").queue();
                        }
                );
            }
            return;
        }

        if (message.getReactions().size() == 1) {
            if (message.getReactions().get(0).getReactionEmote().getEmoji().equals("\u27A1")) {
                message.clearReactions().queue();
                message.editMessage(EmbedFactory.get().createSimpleEmbed(MESSAGE_TITLE.replace("{page}", "2"), page2()).build()).queue(message1 -> {
                            message1.addReaction("\u2B05").queue();
                            message1.addReaction("\u27A1").queue();

                        }

                );
            }
            if (message.getReactions().get(0).getReactionEmote().getEmoji().equals("\u2B05")) {
                message.clearReactions().queue();
                message.editMessage(EmbedFactory.get().createSimpleEmbed(MESSAGE_TITLE.replace("{page}", "2"), page2()).build()).queue(message1 -> {
                            message1.addReaction("\u2B05").queue();
                            message1.addReaction("\u27A1").queue();

                        }
                );
            }
        }

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
            if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length == 3) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            if (message.length == 1 || message[1].equalsIgnoreCase("1")) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(MESSAGE_TITLE.replace("{page}", "1"), page1()).build()).queue(message1 -> {
                            message1.addReaction("\u27A1").queue();
                            HelpPageCache.get().getList().add(message1);
                        }
                );

            } else if (message[1].equalsIgnoreCase("2")) {

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(MESSAGE_TITLE.replace("{page}", "2"), page2()).build()).queue(message1 -> {
                            message1.addReaction("\u2B05").queue();
                            message1.addReaction("\u27A1").queue();
                            HelpPageCache.get().getList().add(message1);

                        }
                );

            } else if (message[1].equalsIgnoreCase("3")) {

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(MESSAGE_TITLE.replace("{page}", "3"), page3()).build()).queue(message1 -> {
                            message1.addReaction("\u2B05").queue();
                            HelpPageCache.get().getList().add(message1);

                        }
                );
            }
        }
    }
}
