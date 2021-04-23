package com.i0dev.commands.discord.completedModules.linking;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.discordLinking.Linking;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Remove {

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.link.parts.remove.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.link.parts.remove.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.link.parts.remove.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.link.parts.remove.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.link.parts.remove.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.link.parts.remove.message.logMessage");
    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.link.parts.remove.option.log");

    private static final String MESSAGE_NOT_LINKED = Configuration.getString("modules.link.parts.remove.message.notLinked");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Link Remove")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), LinkManager.info, e.getAuthor());
            return;
        }

        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        DPlayer dPlayer;
        if (MentionedUser == null) {
            if (DPlayerEngine.getInstance().getObjectFromIGN(message[2]) == null) {
                MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_NOT_LINKED.replace("{arg}", message[2]), e.getAuthor());
                return;
            } else {
                dPlayer = DPlayerEngine.getInstance().getObjectFromIGN(message[2]);
            }
        } else {
            dPlayer = DPlayerEngine.getInstance().getObject(MentionedUser);
        }

        if (dPlayer == null || dPlayer.getLinkInfo().getLinkedTime() == 0) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_NOT_LINKED.replace("{arg}", message[2]), e.getAuthor());
            return;
        }


        dPlayer.setLinkInfo(new Linking());
        DPlayerEngine.getInstance().save(dPlayer.getDiscordID());


        String desc = MESSAGE_CONTENT
                .replace("{ign}", dPlayer.getCachedData().getMinecraftIGN())
                .replace("{discordTag}", dPlayer.getCachedData().getDiscordTag())
                .replace("{uuid}", dPlayer.getLinkInfo().getMinecraftUUID());
        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());

        if (OPTION_LOG) {
            String logMsg = MESSAGE_LOG_MESSAGE
                    .replace("{ign}", dPlayer.getCachedData().getMinecraftIGN())
                    .replace("{discordTag}", dPlayer.getCachedData().getDiscordTag())
                    .replace("{uuid}", dPlayer.getLinkInfo().getMinecraftUUID());
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, logMsg, e.getAuthor());
        }
    }
}