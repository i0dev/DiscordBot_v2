package com.i0dev.commands.discord.completedModules.suggestion;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SuggestionManager {

    public static final String UP_VOTE_EMOJI = Configuration.getString("modules.suggestion.upVoteEmoji");
    public static final String DOWN_VOTE_EMOJI = Configuration.getString("modules.suggestion.downVoteEmoji");
    public static final String MESSAGE_CANT_FIND = Configuration.getString("modules.suggestion.message.cantFind");

    public static final String ACCEPT_EMOJI = Configuration.getString("modules.suggestion.acceptEmoji");
    public static final String ACCEPT_COLOR = Configuration.getString("modules.suggestion.acceptColor");

    public static final String REJECT_EMOJI = Configuration.getString("modules.suggestion.rejectEmoji");
    public static final String REJECT_COLOR = Configuration.getString("modules.suggestion.rejectColor");

    public static final long CHANNEL_SUGGESTION_CHANNEL_ID = Configuration.getLong("channels.suggestionChannelID");
    public static final long CHANNEL_ACCEPTED_SUGGESTION_CHANNEL_ID = Configuration.getLong("channels.acceptedSuggestionChannelID");
    public static final long CHANNEL_REJECTED_SUGGESTION_CHANNEL_ID = Configuration.getLong("channels.rejectedSuggestionChannelID");

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:mailbox_with_mail: Suggestion Commands :mailbox_with_mail:**").append("\n");
        builder.append(add).append("\n");
        builder.append(remove).append("\n");
        builder.append(list).append("\n");
        builder.append(clear).append("\n");
        builder.append(info).append("\n");
        builder.append(accept).append("\n");
        builder.append(reject).append("\n");
        return builder.toString();
    }

    public static String add = "`{prefix}suggestion Add <Suggestion>` *Adds your suggestion to the suggestion channel.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String remove = "`{prefix}suggestion Remove <messageID>` *Deletes that suggestion.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String info = "`{prefix}suggestion Info <messageID>` *Sends information about that suggestion*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String list = "`{prefix}suggestion List` *Retrieves the list of suggestions.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String clear = "`{prefix}suggestion Clear` *Clears all the suggestions from storage. Doesn't delete the suggestion message.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String accept = "`{prefix}suggestion Accept <messageID> [note]` *Accepts that suggestion and moves it to a accepted channel.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String reject = "`{prefix}suggestion Reject <messageID> [note]` *Rejects that suggestion and moves it to a rejected channel.".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[1].toLowerCase()) {
                case "add":
                    Add.run(e);
                    break;
                case "remove":
                    Remove.run(e);
                    break;
                case "list":
                    Retrieve.run(e);
                    break;
                case "clear":
                    Clear.run(e);
                    break;
                case "info":
                    Info.run(e);
                    break;
                case "accept":
                    Accept.run(e);
                    break;
                case "reject":
                    Reject.run(e);
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;

            }
        }
    }
}
