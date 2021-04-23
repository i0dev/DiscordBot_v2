package com.i0dev.commands.discord.completedModules.tebex.packageTebex;

import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PackageMod {

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:package: Tebex Package Commands :package:**").append("\n\n");

        builder.append(get).append("\n");
        builder.append(list).append("\n");
        builder.append(update).append("\n");
        return builder.toString();
    }

    public static String get = "`{prefix}tebex Package Get <packageID>` *Returns information about that package.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String list = "`{prefix}tebex List [verbose]` *Returns a list of all packages, If verbose true, will send descriptions*Package ".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String update = "`{prefix}tebex Package Update <packageID> [disabled] [name] [price]` *Updates a package, If you don't want to use a parameter you can use `null`*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[2].toLowerCase()) {
                case "get":
                    Get.run(e);
                    break;
                case "list":
                    break;
                case "update":
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}