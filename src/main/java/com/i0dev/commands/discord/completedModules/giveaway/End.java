package com.i0dev.commands.discord.completedModules.giveaway;

import com.i0dev.engine.discord.TaskCheckActiveGiveaways;
import com.i0dev.object.objects.Giveaway;
import com.i0dev.object.engines.GiveawayEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class End {
    private static final String Identifier = "Giveaway End";
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.giveaway.parts.end.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.giveaway.parts.end.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.giveaway.parts.end.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.giveaway.parts.end.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.giveaway.parts.end.message.general");
    private static final String MESSAGE_GIVEAWAY_ENDED = Configuration.getString("modules.giveaway.parts.end.message.ended");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Giveaway End")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GiveawayManager.end, e.getAuthor());
            return;
        }
        Giveaway giveaway = GiveawayEngine.getInstance().getObject(message[2]);
        if (giveaway == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GiveawayManager.MESSAGE_CANT_FIND, e.getAuthor());
            return;
        }
        if (giveaway.isEnded()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_GIVEAWAY_ENDED, e.getAuthor());
            return;
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());

        TaskCheckActiveGiveaways.get().endGiveawayFull(giveaway, true, false, false, e.getAuthor());

    }


}
