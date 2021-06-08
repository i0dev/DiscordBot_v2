package com.i0dev.modules.points.discord;

import com.i0dev.modules.points.Option;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PointsManager extends DiscordCommand {
    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:coin: Points Commands :coin:**").append("\n");
        builder.append(add).append("\n");
        builder.append(remove).append("\n");
        builder.append(set).append("\n");
        builder.append(balance).append("\n");
        builder.append(pay).append("\n");
        builder.append(leaderboard).append("\n");
        builder.append(shop).append("\n");
        builder.append(buy).append("\n");
        builder.append(info).append("\n");
        return builder.toString();
    }

    public static String add = "`{prefix}points add <User> <amount>` *Gives points to that user*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String info = "`{prefix}points info` *Sends information about points.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String remove = "`{prefix}points remove <User> <amount>` *Removes points from that user*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String set = "`{prefix}points set <User> <amount>` *Sets points count to that user*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);

    public static String balance = "`{prefix}points balance [user]` *Checks the balance of that user.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String leaderboard = "`{prefix}points leaderboard` *Gets the leaderboard of points*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String pay = "`{prefix}points pay <User> <amount>` *Pays that user some of your points*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);

    public static String shop = "`{prefix}points shop [page]` *Opens the shop menu*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String buy = "`{prefix}points buy <ID> [amount]` *Buys that item from shop.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);

    public static String MESSAGE_IS_NOT_NUMBER;
    public static String MESSAGE_INSUFFICIENT_BALANCE;

    @Override
    public void init() {
        MESSAGE_IS_NOT_NUMBER = Configuration.getString("modules.points.message.isNotNumber");
        MESSAGE_INSUFFICIENT_BALANCE = Configuration.getString("modules.points.message.insufficientBalance");
    }

    public static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Setter
    @Getter
    public static boolean panic = false;

    public static Option makeObject(JSONObject object, boolean full) {
        Option option = new Option();
        option.setPrice(((long) object.get("price")));
        option.setDiscordDisplayName(object.get("discordDisplayName").toString());
        option.setDiscordDescription(object.get("discordDescription").toString());
        if (full) {
            option.setIngameDisplayName(object.get("ingameDisplayName").toString());
            option.setCommandsToRun(((ArrayList<String>) object.get("commandsToRun")));
            option.setIngameDescription(((ArrayList<String>) object.get("ingameDescription")));
            option.setItemMaterial(object.get("itemMaterial").toString());
            option.setItemAmount(((long) object.get("itemAmount")));
            option.setItemData(((long) object.get("itemData")));
            option.setGlow(((boolean) object.get("glow")));
        }
        return option;
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (panic) return;
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
                case "set":
                    Set.run(e);
                    break;
                case "bal":
                    Balance.run(e);
                    break;
                case "balance":
                    Balance.run(e);
                    break;
                case "pay":
                    Pay.run(e);
                    break;
                case "leaderboard":
                    Leaderboard.run(e);
                    break;
                case "shop":
                    Shop.run(e);
                    break;
                case "buy":
                    Buy.run(e);
                    break;
                case "info":
                    Info.run(e);
                    break;
                case "howto":
                    Info.run(e);
                    break;
                case "help":
                    Info.run(e);
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}