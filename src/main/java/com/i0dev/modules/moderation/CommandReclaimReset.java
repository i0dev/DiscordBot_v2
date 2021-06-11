package com.i0dev.modules.moderation;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandReclaimReset extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.reclaim_reset.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.reclaim_reset.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.reclaim_reset.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.reclaim_reset.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.reclaim_reset.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Reclaim Reset")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.RECLAIM_RESET_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        for (Object o : DPlayerEngine.getCache()) {
            DPlayer dPlayer = ((DPlayer) o);
            dPlayer.setClaimedReclaim(false);
            dPlayer.save();
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());

    }
}

