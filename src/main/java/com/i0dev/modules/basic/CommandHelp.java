package com.i0dev.modules.basic;

import com.i0dev.commands.discord.completedModules.gamemode.GamemodeManager;
import com.i0dev.commands.discord.completedModules.giveaway.GiveawayManager;
import com.i0dev.commands.discord.completedModules.linking.LinkManager;
import com.i0dev.commands.discord.completedModules.movements.MovementManager;
import com.i0dev.commands.discord.completedModules.mute.MuteManager;
import com.i0dev.commands.discord.completedModules.screenshare.ScreenshareManager;
import com.i0dev.commands.discord.completedModules.suggestion.SuggestionManager;
import com.i0dev.commands.discord.completedModules.tebex.TebexManager;
import com.i0dev.commands.discord.completedModules.warn.WarnManager;
import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.modules.basic.cache.HelpCmdCache;
import com.i0dev.modules.blacklist.BlacklistManager;
import com.i0dev.modules.boosting.BoostingManager;
import com.i0dev.modules.mapPoints.MapPointsManager;
import com.i0dev.modules.points.discord.PointsManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandHelp extends ListenerAdapter {
    private static String page1() {
        StringBuilder desc = new StringBuilder();
        desc.append("`<>`: required **|** `[]`: optional.\n\n");
        desc.append("**:large_blue_diamond: Basic Commands :large_blue_diamond:**\n");
        desc.append("``{cmd} [user]`` - *Get the avatar of yourself or a user.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.avatar.aliases").get(0)));
        desc.append("``{cmd}`` - *Get a list of all roles.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.roles.aliases").get(0)));
        desc.append("``{cmd} <role>`` - *Get information about a role.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.roleInfo.aliases").get(0)));
        desc.append("``{cmd}`` - *Get information about the server.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.serverInfo.aliases").get(0)));
        desc.append("``{cmd} <user>`` - *Get information about a user.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.userInfo.aliases").get(0)));
        desc.append("``{cmd}`` - *Gets the member count of the server.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.memberCount.aliases").get(0)));
        desc.append("``{cmd}`` - *Sends the server's IP address.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.serverIP.aliases").get(0)));
        desc.append("``{cmd}`` - *Claims your daily rewards.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.rewards.aliases").get(0)));
        desc.append("``{cmd}`` - *Reclaim rewards from your rank.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.reclaim.aliases").get(0)));
        desc.append("\n**:rofl: Fun Commands :rofl:**\n");
        desc.append("``{cmd} <user>`` - *Slap that user.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.fun_slap.aliases").get(0)));
        desc.append("``{cmd}`` - *Role a die.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.fun_dice.aliases").get(0)));
        desc.append("``{cmd} <user>`` - *Pat that user.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.fun_pat.aliases").get(0)));
        desc.append("``{cmd}`` - *Say hello to the bot.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.fun_hey.aliases").get(0)));
        desc.append("``{cmd}`` - *Flip a coin.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.fun_coinFlip.aliases").get(0)));
        desc.append("``{cmd} <Question>`` - *Ask the magic8ball a question.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.fun_8ball.aliases").get(0)));
        desc.append("\n");
        desc.append(GamemodeManager.usage());
        desc.append("\n");
        desc.append(MovementManager.usage());
        desc.append("\n");
        desc.append(LinkManager.usage());
        return desc.toString();
    }

    private static String page2() {
        StringBuilder desc = new StringBuilder();
        desc.append("`<>`: required **|** `[]`: optional.\n\n");
        desc.append("**:hammer: Moderation Commands :hammer:**\n");
        desc.append("``{cmd} <user> [reason]`` - *Ban a user from the server.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.ban.aliases").get(0)));
        desc.append("``{cmd} <user>`` - *Unban a user from the server.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.unban.aliases").get(0)));
        desc.append("``{cmd} <user> [reason]`` - *Kick a user from the server.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.kick.aliases").get(0)));
        desc.append("``{cmd}`` - *Clear channel messages.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.messageClear.aliases").get(0)));
        desc.append("``{cmd} <amt> [user]`` - *Delete that number of messages.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.prune.aliases").get(0)));
        desc.append("``{cmd}`` - *Resets all reclaim data.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.reclaim_reset.aliases").get(0)));
        desc.append("\n");
        desc.append(ScreenshareManager.usage());
        desc.append("\n");
        desc.append(WarnManager.usage());
        desc.append("\n");
        desc.append(BlacklistManager.usage());
        desc.append("\n");
        desc.append(MuteManager.usage());
        return desc.toString();
    }

    private static String page3() {
        StringBuilder desc = new StringBuilder();
        desc.append("`<>`: required **|** `[]`: optional.\n\n");
        desc.append("\n**:tickets: Ticket Commands :tickets:**\n");
        desc.append("``{cmd}`` - *Create the ticket panel.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.createTicketPanel.aliases").get(0)));
        desc.append("``{cmd} <user>`` - *Adds a user to the ticket.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.ticketAdd.aliases").get(0)));
        desc.append("``{cmd} <user>`` - *Removes a user to the ticket.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.ticketRemove.aliases").get(0)));
        desc.append("``{cmd}`` - *Switch the ticket to admin only mode.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.ticketAdminOnly.aliases").get(0)));
        desc.append("``{cmd}`` - *Give ticket info.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.ticketInfo.aliases").get(0)));
        desc.append("``{cmd} <name>`` - *Renames the ticket.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.ticketRename.aliases").get(0)));
        desc.append("``{cmd} [reason]`` - *Closes the ticket.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.ticketClose.aliases").get(0)));
        desc.append("\n**:new: Invite Commands :new:**\n");
        desc.append("``{cmd} [user]`` - *Gets the invites of a user.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.invites.aliases").get(0)));
        desc.append("``{cmd}`` - *Sends the invites leaderboard.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.inviteLeaderboard.aliases").get(0)));
        desc.append("``{cmd}`` - *Resets all invite data.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.invite_resetData.aliases").get(0)));
        desc.append("\n**:star: Other Commands :star:**\n");
        desc.append("``{cmd}`` - *Create the verify panel.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.createVerifyPanel.aliases").get(0)));
        desc.append("``{cmd}`` - *Starts a poll creator.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.pollCreator.aliases").get(0)));
        desc.append("``{cmd}`` - *Create an Application.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.apply.aliases").get(0)));
        desc.append("``{cmd} <user> <role> [response]`` - *Accept a users app.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.accept.aliases").get(0)));
        desc.append("``{cmd} <user> [response]`` - *Rejects a users app.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.reject.aliases").get(0)));
        desc.append("``{cmd} <Server IP>`` - *Get general information about the Minecraft server.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.MCServerInfo.aliases").get(0)));
        desc.append("``{cmd}`` - *Starts a reaction role creator.*\n".replace("{cmd}", GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList("commands.reactionRoles.aliases").get(0)));
        desc.append(String.format("``%1$s <content>`` - *Create a changelog post.*\n", getAlias("changelog")));
        desc.append(String.format("``%1$s`` - *Sends basic hardware information and usage. If connected as a plugin, will display TPS and online players.*\n", getAlias("heapDumpToDiscord")));
        desc.append(String.format("``%1$s [user]`` - *Sends profile information about a specified user. If connected as a plugin, will display TPS and online players.*\n", getAlias("profile")));
        desc.append(String.format("``%1$s <role>`` - *Gives everyone that role on the server.*\n", getAlias("role_all")));
        desc.append(String.format("``%1$s <command>`` - *Run a command in game though console.*\n", getAlias("runIngameCommand")));
        desc.append("``").append(GlobalConfig.GENERAL_BOT_PREFIX).append("version`` - *Sends the version of the bot.*\n");
        return desc.toString();
    }

    private static String page4() {
        StringBuilder desc = new StringBuilder();
        desc.append("`<>`: required **|** `[]`: optional.\n\n");
        desc.append(SuggestionManager.usage());
        desc.append("\n");
        desc.append(TebexManager.usage());
        desc.append("\n");
        desc.append(GiveawayManager.usage());
        return desc.toString();
    }

    private static String page5() {
        StringBuilder desc = new StringBuilder();
        desc.append("`<>`: required **|** `[]`: optional.\n\n");
        desc.append(PointsManager.usage());
        desc.append("\n");
        desc.append(MapPointsManager.usage());
        desc.append("\n");
        desc.append(BoostingManager.usage());
        return desc.toString();
    }

    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.help.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.help.permissionLiteMode");
    public static final String MESSAGE_TITLE = Configuration.getString("commands.help.helpPageTitle");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.help.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.help.enabled");

    private static String getAlias(String commandIdentifier) {
        return GlobalConfig.GENERAL_BOT_PREFIX + Configuration.getStringList(String.format("commands.%s.aliases", commandIdentifier)).get(0);
    }

    private static void addReactions(Message msg) {
        msg.addReaction("1️⃣").queue();
        msg.addReaction("2️⃣").queue();
        msg.addReaction("3️⃣").queue();
        msg.addReaction("4️⃣").queue();
        msg.addReaction("5️⃣").queue();

        //   msg.addReaction("6️⃣").queue();
        //   msg.addReaction("7️⃣").queue();
        //   msg.addReaction("8️⃣").queue();
        //   msg.addReaction("9️⃣").queue();

    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Help")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 3) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.HELP_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        if (message.length == 1 || message[1].equalsIgnoreCase("1")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "1"), page1()).build()).queue(message1 -> {
                addReactions(message1);
                HelpCmdCache.get().getList().add(message1);
            });
        } else if (message[1].equalsIgnoreCase("2")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "2"), page2()).build()).queue(message1 -> {
                addReactions(message1);
                HelpCmdCache.get().getList().add(message1);
            });
        } else if (message[1].equalsIgnoreCase("3")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "3"), page3()).build()).queue(message1 -> {
                addReactions(message1);
                HelpCmdCache.get().getList().add(message1);
            });
        } else if (message[1].equalsIgnoreCase("4")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "4"), page4()).build()).queue(message1 -> {
                addReactions(message1);
                HelpCmdCache.get().getList().add(message1);
            });
        } else if (message[1].equalsIgnoreCase("5")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "5"), page5()).build()).queue(message1 -> {
                addReactions(message1);
                HelpCmdCache.get().getList().add(message1);
            });
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (!COMMAND_ENABLED) return;
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getUser()))
            return;
        Message message;
        try {
            message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
        } catch (Exception ignored) {
            return;
        }
        if (!isHelpMessage(message)) return;

        if (e.getReaction().getReactionEmote().getEmoji().equals("1️⃣")) {
            message.editMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "1"), page1()).build()).queue(message1 -> message1.removeReaction("1️⃣", e.getUser()).queue());
        } else if (e.getReaction().getReactionEmote().getEmoji().equals("2️⃣")) {
            message.editMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "2"), page2()).build()).queue(message1 -> message1.removeReaction("2️⃣", e.getUser()).queue());
        } else if (e.getReaction().getReactionEmote().getEmoji().equals("3️⃣")) {
            message.editMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "3"), page3()).build()).queue(message1 -> message1.removeReaction("3️⃣", e.getUser()).queue());
        } else if (e.getReaction().getReactionEmote().getEmoji().equals("4️⃣")) {
            message.editMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "4"), page4()).build()).queue(message1 -> message1.removeReaction("4️⃣", e.getUser()).queue());
        } else if (e.getReaction().getReactionEmote().getEmoji().equals("5️⃣")) {
            message.editMessage(EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", "5"), page5()).build()).queue(message1 -> message1.removeReaction("5️⃣", e.getUser()).queue());
        }


    }

    public boolean isHelpMessage(Message reactionMessage) {
        for (Message message : HelpCmdCache.get().getList()) {
            try {
                if (reactionMessage.equals(message)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
