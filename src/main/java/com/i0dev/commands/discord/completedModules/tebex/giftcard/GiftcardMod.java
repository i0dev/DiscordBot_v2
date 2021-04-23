package com.i0dev.commands.discord.completedModules.tebex.giftcard;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GiftcardMod {
    public static final String MESSAGE_NO_PERMISSION = Configuration.getString("modules.tebex.parts.giftcard.message.noPermission");

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:credit_card: Tebex Giftcard Commands :credit_card:**").append("\n\n");

        builder.append(get).append("\n");
        builder.append(create).append("\n");
        builder.append(delete).append("\n");
        builder.append(topup).append("\n");
        builder.append(list).append("\n");
        return builder.toString();
    }

    public static String get = "`{prefix}tebex GiftCard Get <code>` *Gets information about that GiftCard.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String create = "`{prefix}tebex GiftCard Create <amount> [note]` *Creates a GiftCard with the specified amount.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String delete = "`{prefix}tebex GiftCard Delete <code>` *Deletes that giftcard.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String topup = "`{prefix}tebex GiftCard TopUp <code> <amount>` *Adds the amount to that giftcards balance.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String list = "`{prefix}tebex GiftCard List` *Returns a list of all giftcards on the server.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[2].toLowerCase()) {
                case "get":
                    break;
                case "create":
                    Create.run(e);
                    break;
                case "delete":
                    break;
                case "topup":
                    break;
                case "list":
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}