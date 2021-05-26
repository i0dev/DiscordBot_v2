package com.i0dev.modules.blacklist;

import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BlacklistManager  {

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:chains: Blacklist Commands :chains:**").append("\n");
        builder.append(add).append("\n");
        builder.append(remove).append("\n");
        builder.append(list).append("\n");
        builder.append(clear).append("\n");
        return builder.toString();
    }

    public static String add = "`{prefix}blacklist Add <user> [reason]` *Add a user to the blacklist. This will disallow them from using any commands on the bot.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String remove = "`{prefix}blacklist Remove <user>` *Remove a user from the blacklist. This will allow them to use bot commands again.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String list = "`{prefix}blacklist List` *Retrieve the blacklist.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String clear = "`{prefix}blacklist Clear` *Clears the entire blacklist.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


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
