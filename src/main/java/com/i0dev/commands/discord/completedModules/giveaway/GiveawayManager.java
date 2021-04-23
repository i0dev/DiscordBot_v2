package com.i0dev.commands.discord.completedModules.giveaway;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GiveawayManager {


    protected static final String MESSAGE_CANT_FIND = Configuration.getString("modules.giveaway.message.cantFind");

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:tada: Giveaway Commands :tada:**").append("\n");
        builder.append(create).append("\n");
        builder.append(end).append("\n");
        builder.append(info).append("\n");
        builder.append(reroll).append("\n");
        builder.append(list).append("\n");
        return builder.toString();
    }

    public static String create = "`{prefix}giveaway Create` *Starts a giveaway creator in your direct messages.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String end = "`{prefix}giveaway End <messageID>` *Ends a giveaway immediately*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String info = "`{prefix}giveaway Info <messageID>` *Gets information about a giveaway*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String reroll = "`{prefix}giveaway ReRoll <messageID>` *ReRolls the specified giveaway for new winners.".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String list = "`{prefix}giveaway List` *Retrieves a list of all active giveaways.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[1].toLowerCase()) {
                case "create":
                    Create.run(e);
                    break;
                case "reroll":
                    ReRoll.run(e);
                    break;
                case "end":
                    End.run(e);
                    break;
                case "info":
                    Info.run(e);
                    break;
                case "list":
                    Retrieve.run(e);
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}