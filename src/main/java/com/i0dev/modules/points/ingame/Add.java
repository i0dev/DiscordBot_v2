package com.i0dev.modules.points.ingame;

import com.i0dev.Engine;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.objects.LogObject;
import com.i0dev.utility.util.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class Add {


    public static void run(CommandSender sender, String[] args) {
        if (!PointsManager.hasPermission(sender, "discordbot.points.add")) {
            //no permission
            sender.sendMessage("No Permission");
            return;
        }

        if (args.length != 3) {
            //usage
            sender.sendMessage("Usage: /points add <user> <amount>");

            return;
        }

        DPlayer dPlayer = DPlayerEngine.getObjectFromIGN(args[1]);
        String amount = args[2];

        if (dPlayer == null) {
            try {
                Bukkit.getPlayer(args[1]).sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', "&cSomeone attempted to give you &f" + amount + " points &c, but you we're not linked to discord so you did not receive them."));
                Bukkit.getPlayer(args[1]).sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', "&f&lScreenshot this message and send it in a ticket to get your points"));
            } catch (Exception ignored) {

            }
            sender.sendMessage("Invalid User");
            return;
        }
        if (!dPlayer.getLinkInfo().isLinked()) {
            sender.sendMessage("User needs to be linked in order to claim rewards");
            try {
                Bukkit.getPlayer(args[1]).sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', "&cSomeone attempted to give you &f" + amount + " points &c, but you we're not linked to discord so you did not receive them."));
            } catch (Exception ignored) {

            }
            return;
        }
        if (!FormatUtil.isInt(amount)) {
            sender.sendMessage("Not an valid number");
            return;
        }

        dPlayer.setPoints(dPlayer.getPoints() + Double.parseDouble(amount));

        sender.sendMessage("U sent " + amount + " points to " + dPlayer.getCachedData().getMinecraftIGN());
        try {
            Bukkit.getPlayer(dPlayer.getCachedData().getMinecraftIGN()).sendMessage(ChatColor.translateAlternateColorCodes(
                    '&', "&7You received &c" + amount + " points &7from &c" + sender.getName()));
        } catch (Exception ignored) {
            sender.sendMessage("error occured btw");
        }

        String message = "[{tag}] has received [{points}] points from [{staff}] ingame"
                .replace("{tag}", dPlayer.getCachedData().getDiscordTag())
                .replace("{points}", amount)
                .replace("{staff}", sender.getName());
        message = "" + FormatUtil.formatDate(System.currentTimeMillis()) + ": " + message;
        Engine.getToLog().add(new LogObject(message, new File(InitializeBot.get().getPointLogPath())));

    }
}
