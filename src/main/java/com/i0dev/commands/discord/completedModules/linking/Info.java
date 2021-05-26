package com.i0dev.commands.discord.completedModules.linking;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Info {
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.link.parts.info.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.link.parts.info.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.link.parts.info.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.link.parts.info.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.link.parts.info.message.general");
    private static final String MESSAGE_NOT_LINKED = Configuration.getString("modules.link.parts.info.message.notLinked");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Link Info")) {
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
            if (DPlayerEngine.getObjectFromIGN(message[2]) == null) {
                MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_NOT_LINKED.replace("{arg}", message[2]), e.getAuthor());
                return;
            } else {
                dPlayer = DPlayerEngine.getObjectFromIGN(message[2]);
            }
        } else {
            dPlayer = DPlayerEngine.getObject(MentionedUser.getIdLong());
        }


        if (dPlayer == null || dPlayer.getLinkInfo().getLinkedTime() == 0) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_NOT_LINKED.replace("{arg}", message[2]), e.getAuthor());
            return;
        }

        String desc = MESSAGE_CONTENT
                .replace("{discordTag}", dPlayer.getCachedData().getDiscordTag())
                .replace("{discordID}", dPlayer.getDiscordID() + "")
                .replace("{linkedIGN}", dPlayer.getCachedData().getMinecraftIGN())
                .replace("{uuid}", dPlayer.getLinkInfo().getMinecraftUUID())
                .replace("{linkTime}", FormatUtil.formatDate(dPlayer.getLinkInfo().getLinkedTime()))
                .replace("{}", "");

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, MentionedUser, e.getAuthor());
    }
}