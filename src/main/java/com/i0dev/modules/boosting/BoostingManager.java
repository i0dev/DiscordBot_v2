package com.i0dev.modules.boosting;

import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BoostingManager {
    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:rocket: Boost Commands :rocket:**").append("\n");
        builder.append(info).append("\n");
        builder.append(top).append("\n");
        builder.append(list).append("\n");
        builder.append(claim).append("\n");
        return builder.toString();
    }

    public static String info = "`{prefix}boosting info` *Sends a page of boosting rewards and information.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String top = "`{prefix}boosting leaderboard` *Sends the boosting leaderboard.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String list = "`{prefix}boosting list` *Sends a list of active boosters.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String claim = "`{prefix}boosting claim` *Claims boosting rewards.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[1].toLowerCase()) {
                case "info":
                    Info.run(e);
                    break;
                case "top":
                case "leaderboard":
                    Leaderboard.run(e);
                    break;
                case "list":
                    Retrieve.run(e);
                    break;
                case "claim":
                    Claim.run(e);
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}