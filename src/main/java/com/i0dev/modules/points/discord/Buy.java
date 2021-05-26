package com.i0dev.modules.points.discord;

import com.i0dev.Engine;
import com.i0dev.InitializeBot;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.object.objects.LogObject;
import com.i0dev.utility.util.FormatUtil;

import java.io.File;

public class Buy extends DiscordCommand {

    public static boolean PERMISSION_STRICT;
    public static boolean PERMISSION_LITE;
    public static boolean PERMISSION_ADMIN;
    public static boolean ENABLED;
    public static String MESSAGE_CONTENT;
    private static String NOT_VALID_ID;
    private static String MAX_BUY_AMOUNT;
    private static java.util.List<org.json.simple.JSONObject> OPTIONS;
    private static long MAX_BUY_AMOUNT_LONG;

    @Override
    public void init() {

        PERMISSION_STRICT = com.i0dev.utility.Configuration.getBoolean("modules.points.parts.buy.permission.strict");
        PERMISSION_LITE = com.i0dev.utility.Configuration.getBoolean("modules.points.parts.buy.permission.lite");
        PERMISSION_ADMIN = com.i0dev.utility.Configuration.getBoolean("modules.points.parts.buy.permission.admin");
        ENABLED = com.i0dev.utility.Configuration.getBoolean("modules.points.parts.buy.enabled");

        NOT_VALID_ID = com.i0dev.utility.Configuration.getString("modules.points.parts.buy.message.notValidID");
        MAX_BUY_AMOUNT = com.i0dev.utility.Configuration.getString("modules.points.parts.buy.message.maxBuyAmount");
        OPTIONS = com.i0dev.utility.Configuration.getObjectList("pointShop.options");
        MAX_BUY_AMOUNT_LONG = com.i0dev.utility.Configuration.getLong("modules.points.parts.buy.option.maxBuyAmount");
        MESSAGE_CONTENT = com.i0dev.utility.Configuration.getString("modules.points.parts.buy.message.general");
    }


    public static void run(net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent e) {
        if (!com.i0dev.utility.GlobalCheck.checkBasic(e, ENABLED, new com.i0dev.object.engines.PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Points Buy")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length <= 2 || message.length > 4) {
            com.i0dev.utility.util.MessageUtil.sendMessage(e.getChannel().getIdLong(), PointsManager.buy, e.getAuthor());
            return;
        }

        String id = message[2];
        if (!com.i0dev.utility.util.FormatUtil.isInt(id)) {
            com.i0dev.utility.util.MessageUtil.sendMessage(e.getChannel().getIdLong(), NOT_VALID_ID.replace("{arg}", message[2]), e.getAuthor());
            return;
        }
        if (Integer.parseInt(id) <= 0 || Integer.parseInt(id) > OPTIONS.size() + 2) {
            com.i0dev.utility.util.MessageUtil.sendMessage(e.getChannel().getIdLong(), NOT_VALID_ID.replace("{arg}", message[2]), e.getAuthor());
            return;
        }
        com.i0dev.modules.points.Option option = PointsManager.makeObject(OPTIONS.get(Integer.parseInt(id) - 1), true);
        long optionCost = option.getPrice();
        String amount = "1";
        if (message.length == 4) {
            amount = message[3];
        }
        if (!com.i0dev.utility.util.FormatUtil.isDouble(amount) || Double.parseDouble(amount) < 0) {
            com.i0dev.utility.util.MessageUtil.sendMessage(e.getChannel().getIdLong(), PointsManager.MESSAGE_IS_NOT_NUMBER.replace("{arg}", message.length == 3 ? 1 + "" : message[3]), e.getAuthor());
            return;
        }

        if (Double.parseDouble(amount) > MAX_BUY_AMOUNT_LONG + 1) {
            com.i0dev.utility.util.MessageUtil.sendMessage(e.getChannel().getIdLong(), MAX_BUY_AMOUNT.replace("{max}", MAX_BUY_AMOUNT_LONG + "").replace("{arg}", message[3]), e.getAuthor());
            return;
        }

        com.i0dev.object.discordLinking.DPlayer dPlayer = com.i0dev.object.discordLinking.DPlayerEngine.getObject(e.getAuthor().getIdLong());
        if (!dPlayer.getLinkInfo().isLinked()) {
            com.i0dev.utility.util.MessageUtil.sendMessage(e.getChannel().getIdLong(), com.i0dev.utility.GlobalConfig.NOT_LINKED, e.getAuthor());
            return;
        }
        long totalCost = (long) (Double.parseDouble(amount) * optionCost);

        if (dPlayer.getPoints() < totalCost) {
            com.i0dev.utility.util.MessageUtil.sendMessage(e.getChannel().getIdLong(), PointsManager.MESSAGE_INSUFFICIENT_BALANCE, e.getAuthor());
            return;
        }
        dPlayer.setPoints(dPlayer.getPoints() - totalCost);
        com.i0dev.object.discordLinking.DPlayerEngine.save(e.getAuthor().getIdLong());

        String desc = MESSAGE_CONTENT
                .replace("{amount}", amount)
                .replace("{displayName}", option.getDiscordDisplayName())
                .replace("{totalCost}", totalCost + "");

        com.i0dev.utility.util.MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());

        String message1 = ("[{tag}] has purchased [{amount}] [{displayName}] for [{points}] points"
                .replace("{tag}", e.getAuthor().getAsTag())
                .replace("{points}", totalCost + "")
                .replace("{displayName}", option.getDiscordDisplayName())
                .replace("{amount}", amount));

        message1 = "" + FormatUtil.formatDate(System.currentTimeMillis()) + ": " + message1;
        Engine.getToLog().add(new LogObject(message1, new File(InitializeBot.get().getPointLogPath())));

        if (InitializeBot.isPluginMode()) {
            final String atomicAmount = amount;
            for (String s : option.getCommandsToRun()) {
                org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(com.i0dev.DiscordBot.get(), () -> com.i0dev.DiscordBot.get().runCommand(org.bukkit.Bukkit.getConsoleSender(), s.replace("{player}", dPlayer.getCachedData().getMinecraftIGN()).replace("{amount}", atomicAmount))
                );
            }
        }
    }
}