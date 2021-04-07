package com.i0dev.engine.minecraft;

import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.getConfig;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.MessageUtil;
import litebans.api.Entry;
import litebans.api.Events;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.awt.*;
import java.time.ZonedDateTime;

public class LitebansNotifications {
    private static final boolean enabled = getConfig.get().getBoolean("minecraftModules.litebansLogs.enabled");
    private static final String logMessage = getConfig.get().getString("minecraftModules.litebansLogs.logMessage");

    public static void registerEvents() {
        if (!Bukkit.getPluginManager().isPluginEnabled("LiteBans")) {
            System.out.println("LiteBans Integration not found. Disabling API");
            return;
        }
        if (!enabled) return;

        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry e) {
                APIUtil.refreshAPICache(e.getUuid());

                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle(GlobalConfig.EMBED_TITLE)
                        .setFooter(GlobalConfig.EMBED_FOOTER)
                        .setTimestamp(ZonedDateTime.now())
                        .setThumbnail("https://crafatar.com/renders/head/" + e.getUuid())
                        .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE));

                embedBuilder.setDescription(logMessage
                        .replace("{type}", StringUtils.capitalize(e.getType()))
                        .replace("{punisherName}", e.getExecutorName())
                        .replace("{punishedName}", APIUtil.getIGNFromUUID(e.getUuid()))
                        .replace("{reason}", e.getReason())
                        .replace("{duration}", e.getDurationString())
                        .replace("{uuid}", e.getUuid())
                        .replace("{}", "")
                );

                MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, embedBuilder.build());
            }
        });
    }
}
