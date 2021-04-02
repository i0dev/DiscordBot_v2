package main.java.com.i0dev.command.minecraft;

import main.java.com.i0dev.utility.Placeholders;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdVersion implements CommandExecutor {

    String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length != 1) return false;

        if (!args[0].equalsIgnoreCase("version")) return false;
        commandSender.sendMessage(c("&f"));
        commandSender.sendMessage(c("&c&lDiscordBot Information & Version"));
        commandSender.sendMessage(c(Placeholders.convert("&7Discord Prefix: &9{DiscordBotPrefix}")));
        commandSender.sendMessage(c(Placeholders.convert("&7Version: &9{DiscordBotVersion}")));
        commandSender.sendMessage(c(Placeholders.convert("&7Author: &9{DiscordBotAuthor}")));
        commandSender.sendMessage(c(Placeholders.convert("&7Plugin Mode: &9{DiscordBotPluginMode}")));
        commandSender.sendMessage(c("&f"));

        return true;


    }
}
