package com.i0dev.commands.discord.completedModules.giveaway;

import com.i0dev.object.objects.Giveaway;
import com.i0dev.object.engines.GiveawayEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Info {

    private static final String Identifier = "Giveaway Info";
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.giveaway.parts.info.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.giveaway.parts.info.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.giveaway.parts.info.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.giveaway.parts.info.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.giveaway.parts.info.message.general");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Giveaway Info")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GiveawayManager.info, e.getAuthor());
            return;
        }
        Giveaway giveaway = GiveawayEngine.getInstance().getObject(message[2]);
        if (giveaway == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GiveawayManager.MESSAGE_CANT_FIND, e.getAuthor());
            return;
        }
        String description = MESSAGE_CONTENT
                .replace("{channelMention}", giveaway.getChannelID().toString())
                .replace("{messageID}", giveaway.getMessageID().toString())
                .replace("{winnerCount}", giveaway.getHostID().toString())
                .replace("{hostTag}", giveaway.getPrize())
                .replace("{endTime}", FormatUtil.formatDate(giveaway.getEndTime()))
                .replace("{ended}", giveaway.isEnded() ? "Yes" : "No");

        MessageUtil.sendMessage(e.getChannel().getIdLong(), description, e.getAuthor());
    }
}
