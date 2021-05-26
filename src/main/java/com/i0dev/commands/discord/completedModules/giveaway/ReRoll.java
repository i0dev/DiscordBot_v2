package com.i0dev.commands.discord.completedModules.giveaway;

import com.i0dev.modules.giveaway.giveawayHandler;
import com.i0dev.object.engines.GiveawayEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.Giveaway;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ReRoll {
    private static final String Identifier = "Giveaway ReRoll";
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.giveaway.parts.reroll.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.giveaway.parts.reroll.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.giveaway.parts.reroll.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.giveaway.parts.reroll.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.giveaway.parts.reroll.message.general");
    private static final String MESSAGE_RUNNING = Configuration.getString("modules.giveaway.parts.reroll.message.running");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Giveaway Reroll")) {
            return;
        }

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GiveawayManager.reroll, e.getAuthor());
            return;
        }
        Giveaway giveaway = GiveawayEngine.getInstance().getObject(message[2]);
        if (giveaway == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GiveawayManager.MESSAGE_CANT_FIND, e.getAuthor());
            return;
        }
        if (!giveaway.isEnded()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_RUNNING, e.getAuthor());
            return;
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());

        giveawayHandler.endGiveawayFull(giveaway, true, true, true, e.getAuthor());

    }
}
