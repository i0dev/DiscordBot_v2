package com.i0dev.modules.points.ingame;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class PointsManager implements CommandExecutor {

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (sender.isOp()) return true;
        if (sender instanceof ConsoleCommandSender) return true;
        return sender.hasPermission(permission);
    }

    public static int getInventorySize(int number) {
        if (number >= 0 && number <= 9) {
            return 9;
        } else if (number >= 10 && number <= 18) {
            return 18;
        } else if (number >= 19 && number <= 27) {
            return 27;
        } else if (number >= 28 && number <= 36) {
            return 36;
        } else if (number >= 37 && number <= 45) {
            return 45;
        } else {
            return 54;
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0 || strings.length == 4) {
            commandSender.sendMessage("Usage: /points add <user> <amount>");
            commandSender.sendMessage("Usage: /points shop");
            return false;
        }

        switch (strings[0].toLowerCase()) {
            case "add":
                Add.run(commandSender, strings);
                break;
            case "remove":
                //   Remove.run(e);
                break;
            case "set":
                // Set.run(e);
                break;
            case "bal":
                //  Balance.run(e);
                break;
            case "balance":
                //   Balance.run(e);
                break;
            case "pay":
                //  Pay.run(e);
                break;
            case "leaderboard":
                // Leaderboard.run(e);
                break;
            case "shop":
                Shop.run(commandSender, strings);
                break;
            case "buy":
                // Buy.run(e);
                break;
            default:
                commandSender.sendMessage("Usage: /points add <user> <amount>");
                commandSender.sendMessage("Usage: /points shop");
                //   MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                break;

        }

        return false;
    }
}