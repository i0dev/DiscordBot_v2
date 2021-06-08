package com.i0dev.modules.boosting;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.util.List;

public class Claim extends DiscordCommand {

    public static boolean PERMISSION_STRICT;
    public static boolean PERMISSION_LITE;
    public static boolean PERMISSION_ADMIN;
    public static boolean ENABLED;
    public static String MESSAGE_CONTENT;
    public static String NO_POINTS;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.boosting.parts.claim.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.boosting.parts.claim.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.boosting.parts.claim.permission.admin");
        ENABLED = Configuration.getBoolean("modules.boosting.parts.claim.enabled");

        MESSAGE_CONTENT = Configuration.getString("modules.boosting.parts.claim.message.content");
        NO_POINTS = Configuration.getString("modules.boosting.parts.claim.message.noPoints");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Boosting Claim")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), BoostingManager.claim, e.getAuthor());
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getObject(e.getAuthor().getIdLong());
        if (!dPlayer.getLinkInfo().isLinked()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.NOT_LINKED, e.getAuthor());
            return;
        }

        if (dPlayer.getBoostCredits() <= 0) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), NO_POINTS, e.getAuthor());
            return;
        }

        dPlayer.setBoostCredits(dPlayer.getBoostCredits() - 1);
        dPlayer.save();
        List<JSONObject> obj = Configuration.getObjectList("modules.boosting.general.rewardOptions");
        for (JSONObject ob : obj) {
            String s = ob.get("command").toString();
            org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(com.i0dev.DiscordBot.get(), () -> com.i0dev.DiscordBot.get().runCommand(org.bukkit.Bukkit.getConsoleSender(), s.replace("{player}", dPlayer.getCachedData().getMinecraftIGN()))
            );
        }

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());
    }
}