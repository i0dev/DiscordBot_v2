package com.i0dev.modules.points.ingame;

import com.i0dev.DiscordBot;
import com.i0dev.Engine;
import com.i0dev.InitializeBot;
import com.i0dev.modules.points.Option;
import com.i0dev.modules.points.discord.PointsManager;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.objects.LogObject;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.util.FormatUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.List;

public class ShopEvents implements Listener {


    private static final List<JSONObject> OPTIONS = Configuration.getObjectList("pointShop.options");

    @EventHandler
    public void onClickEvent(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getType() == null) return;
        if (!e.getClickedInventory().getType().equals(InventoryType.CHEST)) return;
        if (e.getClickedInventory().getName() == null || e.getClickedInventory().getName().equals("")) return;
        if (e.getClickedInventory().getName().equals(Shop.getShopGUI().getName())) {
            e.setCancelled(true);
            int slot = e.getSlot();
            if (slot <= -2 || slot > OPTIONS.size() - 1) {
                return;
            }

            Option option = PointsManager.makeObject(OPTIONS.get(e.getSlot()), true);
            long price = option.getPrice();

            DPlayer dPlayer = DPlayerEngine.getObjectFromIGN(e.getWhoClicked().getName());
            if (dPlayer == null || !dPlayer.getLinkInfo().isLinked()) {
                e.getWhoClicked().sendMessage(FormatUtil.c("&cYou have to be linked in order to buy an item from shop."));
                e.getWhoClicked().closeInventory();
                return;
            }

            if (dPlayer.getPoints() < price) {
                e.getWhoClicked().sendMessage(FormatUtil.c("&cYou do not have enough points to buy this item. You only have &f") + dPlayer.getPoints());
                e.getWhoClicked().closeInventory();
                return;
            }
            dPlayer.setPoints(dPlayer.getPoints() - price);
            DPlayerEngine.save(dPlayer.getDiscordID());

            e.getWhoClicked().sendMessage(FormatUtil.c("&7You bought 1x " + option.getIngameDisplayName() + "&7 for &c" + price + " points"));
            ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.NOTE_PLING, 1, 1);
            //   e.getWhoClicked().closeInventory();

            String message = ("[{tag}] has purchased [{amount}] [{displayName}] for [{points}] points [ingame]"
                    .replace("{tag}", dPlayer.getCachedData().getDiscordTag())
                    .replace("{points}", price + "")
                    .replace("{displayName}", option.getDiscordDisplayName())
                    .replace("{amount}", "1"));

            message = "" + FormatUtil.formatDate(System.currentTimeMillis()) + ": " + message;
            Engine.getToLog().add(new LogObject(message, new File(InitializeBot.get().getPointLogPath())));

            for (String s : option.getCommandsToRun()) {
                org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(DiscordBot.get(), () -> DiscordBot.get().runCommand(org.bukkit.Bukkit.getConsoleSender(), s.replace("{player}", dPlayer.getCachedData().getMinecraftIGN()).replace("{amount}", "1"))
                );
            }

        }
    }
}
