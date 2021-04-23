package com.i0dev.commands.discord.completedModules.tebex;


import com.i0dev.commands.discord.completedModules.tebex.giftcard.GiftcardMod;
import com.i0dev.commands.discord.completedModules.tebex.packageTebex.PackageMod;
import com.i0dev.commands.discord.completedModules.tebex.payment.PaymentMod;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TebexManager {

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:shopping_cart: Tebex Commands :shopping_cart:**").append("\n");
        builder.append(lookup).append("\n");
        builder.append(payment).append("\n");
        builder.append(Package).append("\n");
        builder.append(giftcard).append("\n");
        builder.append(info).append("\n");
        return builder.toString();
    }

    public static String lookup = "`{prefix}tebex Lookup <IGN>` *Does a player lookup for a user though tebex.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String payment = "`{prefix}tebex Payment <...>` *Goes into the payment module.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String Package = "`{prefix}tebex Package <...>` *Goes into the package module.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String giftcard = "`{prefix}tebex GiftCard <...>` *Goes into the GiftCard module.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String info = "`{prefix}tebex Info` *Gets basic information about the Tebex account.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[1].toLowerCase()) {
                case "lookup":
                    PlayerLookup.run(e);
                    break;
                case "payment":
                    PaymentMod.run(e);
                    break;
                case "package":
                    PackageMod.run(e);
                    break;
                case "giftcard":
                    GiftcardMod.run(e);
                    break;
                case "info":
                    Info.run(e);
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}
