package com.i0dev.command.minecraft;

import com.i0dev.utility.Placeholders;
import litebans.api.Entry;
import litebans.api.Events;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {

    String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
            commandSender.sendMessage(c("&f"));
            commandSender.sendMessage(c("&c&lDiscordBot Information"));
            commandSender.sendMessage(c(Placeholders.convert("&fHelp Command: &c/DiscordBot help")));
            commandSender.sendMessage(c(Placeholders.convert("&fVersion: &c{DiscordBotVersion}")));
            commandSender.sendMessage(c(Placeholders.convert("&fAuthor: &c{DiscordBotAuthor}")));
            commandSender.sendMessage(c("&f"));

            return true;
        }
        return false;
    }


}
