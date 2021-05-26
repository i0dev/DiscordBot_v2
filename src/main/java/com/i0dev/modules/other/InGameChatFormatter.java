package com.i0dev.modules.other;

import com.i0dev.modules.mapPoints.MapPointsManager;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class InGameChatFormatter implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void asyncChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String format = event.getFormat();
        if (!format.contains("{discordbot_mapPoints_allegiance}")) return;
        DPlayer dPlayer = DPlayerEngine.getObjectFromIGN(player.getName());

        List<String> servers = MapPointsManager.GENERAL_SERVER_LIST;
        for (String server : servers) {
            format = format.replace("{discordbot_mapPoints_" + server + "}",
                    dPlayer == null ? "0" : dPlayer.getMapPointsMap().has(server) ? dPlayer.getMapPointsMap().get(server).getAsString() : "0");
        }
        event.setFormat(format);
    }

}
