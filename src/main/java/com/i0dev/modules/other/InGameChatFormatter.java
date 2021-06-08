package com.i0dev.modules.other;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class InGameChatFormatter implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void asyncChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String format = event.getFormat();
        DPlayer dPlayer = DPlayerEngine.getObjectFromIGN(player.getName());

        format = format.replace("{discordbot_mapPoints}",
                dPlayer == null ? "0" : dPlayer.getMapPoints() + "");

        event.setFormat(format);
    }

}
