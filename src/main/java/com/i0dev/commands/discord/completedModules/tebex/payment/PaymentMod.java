package com.i0dev.commands.discord.completedModules.tebex.payment;

import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PaymentMod {
    public static final String MESSAGE_CANT_FIND = Configuration.getString("modules.tebex.parts.payment.message.cantFind");


    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:money_with_wings: Tebex Giftcard Commands :money_with_wings:**").append("\n\n");

        builder.append(get).append("\n");
        builder.append(list).append("\n");
        builder.append(update).append("\n");
        return builder.toString();
    }

    public static String get = "`{prefix}tebex Payment Get <transID>` *Returns information about that transaction.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String list = "`{prefix}tebex Payment List [amount]` *Retrieves a list of all transactions.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String update = "`{prefix}tebex Payment Update <transID> [username] [status]` *Updats a transaction with new information. Use `null` if you don't want to update a paramater.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);



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