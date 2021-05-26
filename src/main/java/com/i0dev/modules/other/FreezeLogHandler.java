package com.i0dev.modules.other;

import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.awt.*;
import java.time.ZonedDateTime;

public class FreezeLogHandler implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled()) return;
        if (e.getMessage().startsWith("/freeze") && e.getMessage().split(" ").length == 2) {
            String user = e.getMessage().split(" ")[1];
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(GlobalConfig.EMBED_TITLE.equals("") ? null : GlobalConfig.EMBED_TITLE)
                    .setFooter(GlobalConfig.EMBED_FOOTER)
                    .setTimestamp(ZonedDateTime.now())
                    .setThumbnail("https://crafatar.com/renders/head/" + APIUtil.getUUIDFromIGN(user))
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE));

            embedBuilder.setDescription("**Type:** `{type}`\n**Staff Member:** `{punisherName}`\n**Punished:** `{punishedName}`\n**UUID:** `{uuid}`"
                    .replace("{type}", "Freeze")
                    .replace("{punisherName}", e.getPlayer().getName())
                    .replace("{punishedName}", user)
                    .replace("{uuid}", APIUtil.getUUIDFromIGN(user))
            );
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, embedBuilder.build());
        }
    }

    @EventHandler
    public void onCommandUn(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled()) return;

        if (e.getMessage().startsWith("/unfreeze") && e.getMessage().split(" ").length == 2) {
            String user = e.getMessage().split(" ")[1];
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle(GlobalConfig.EMBED_TITLE.equals("") ? null : GlobalConfig.EMBED_TITLE)
                    .setFooter(GlobalConfig.EMBED_FOOTER)
                    .setTimestamp(ZonedDateTime.now())
                    .setThumbnail("https://crafatar.com/renders/head/" + APIUtil.getUUIDFromIGN(user))
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE));

            embedBuilder.setDescription("**Type:** `{type}`\n**Staff Member:** `{punisherName}`\n**Punished:** `{punishedName}`\n**UUID:** `{uuid}`"
                    .replace("{type}", "UnFreeze")
                    .replace("{punisherName}", e.getPlayer().getName())
                    .replace("{punishedName}", user)
                    .replace("{uuid}", APIUtil.getUUIDFromIGN(user))
            );
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, embedBuilder.build());
        }
    }
}
