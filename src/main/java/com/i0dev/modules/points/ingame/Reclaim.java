package com.i0dev.modules.points.ingame;

import com.i0dev.modules.basic.CommandReclaim;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.objects.ReclaimOption;
import com.i0dev.utility.util.FormatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reclaim implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!PointsManager.hasPermission(sender, "discordbot.points.reclaim")) {
            //no permission
            sender.sendMessage("No Permission");
            return false;
        }

        if (args.length != 0) {
            //usage
            sender.sendMessage("Usage: /reclaim");
            return false;
        }

        DPlayer dPlayer = DPlayerEngine.getObjectFromIGN(sender.getName());
        if (dPlayer == null) {
            sender.sendMessage(FormatUtil.c("&cYou need to link your account in order to reclaim! Link now with &f/link"));
            return false;
        }

        if (dPlayer.isClaimedReclaim()) {
            sender.sendMessage(FormatUtil.c("&cYou've already claimed your reclaim for this map."));
            return false;
        }

        List<ReclaimOption> options = CommandReclaim.getUsersReclaim(dPlayer);
        if (options.size() == 0) {
            sender.sendMessage(FormatUtil.c("&cYou do not have a rank that has a reclaim reward or there was an issue with your account!"));
            return false;
        }

        dPlayer.setClaimedReclaim(true);
        dPlayer.save();

        for (ReclaimOption option : options) {
            for (String command : option.getCommands()) {
                org.bukkit.Bukkit.getScheduler().runTask(com.i0dev.DiscordBot.get(), () -> com.i0dev.DiscordBot.get().runCommand(org.bukkit.Bukkit.getConsoleSender(), command.replace("{ign}", dPlayer.getCachedData().getMinecraftIGN())));
            }
        }

        sender.sendMessage(FormatUtil.c("&aYou have received your reclaim rewards! You can reclaim again next map."));
        return true;
    }
}
