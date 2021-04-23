package com.i0dev.commands.discord.completedModules.warn;

import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WarnManager {

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:warning: Warning Commands :warning:**").append("\n");

        builder.append(add).append("\n");
        builder.append(remove).append("\n");
        builder.append(list).append("\n");
        builder.append(clear).append("\n");
        return builder.toString();
    }

    public static String add = "`{prefix}warn Add <User> [reason]` *Warns a user.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String remove = "`{prefix}warn Remove <User>` *Removes a warn from a user.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String list = "`{prefix}warn List` *Retrieve a ranked list of all warned users.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String clear = "`{prefix}warn Clear` *Clears the warned user list.* ".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


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
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}