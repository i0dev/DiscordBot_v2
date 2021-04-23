package com.i0dev.commands.discord.completedModules.giveaway;

import com.i0dev.object.objects.Giveaway;
import com.i0dev.object.engines.GiveawayEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Retrieve {
    private static final String Identifier = "Giveaway End";
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.giveaway.parts.list.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.giveaway.parts.list.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.giveaway.parts.list.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.giveaway.parts.list.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.giveaway.parts.list.message.general");
    private static final String MESSAGE_LIST_FORAMT = Configuration.getString("modules.giveaway.parts.list.message.format");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Giveaway List")) {
            return;
        }

        List<String> giveaways = new ArrayList<>();
        for (Object singleton : GiveawayEngine.getInstance().getCache()) {
            Giveaway gw = ((Giveaway) singleton);
            if (gw.isEnded()) continue;
            giveaways.add(MESSAGE_LIST_FORAMT
                    .replace("{prize}", gw.getPrize())
                    .replace("{messageLink}", ("https://discordapp.com/channels/" + GlobalConfig.GENERAL_MAIN_GUILD.getId() + "/" + gw.getChannelID() + "/" + gw.getMessageID())));
        }

        String desc = MESSAGE_CONTENT
                .replace("{giveaways}", FormatUtil.FormatListString(giveaways));


        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());
    }
}
