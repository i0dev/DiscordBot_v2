package com.i0dev.commands.discord.completedModules.blacklist;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Remove {

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.blacklist.parts.remove.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.blacklist.parts.remove.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.blacklist.parts.remove.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.blacklist.parts.remove.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.blacklist.parts.remove.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.blacklist.parts.remove.message.logMessage");
    private static final String MESSAGE_NOT_ON_LIST = Configuration.getString("modules.blacklist.parts.remove.message.notOnList");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.blacklist.parts.remove.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Blacklist Remove")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), BlacklistManager.remove, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }

        if (!DPlayerEngine.getInstance().isBlacklisted(MentionedUser)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_NOT_ON_LIST, e.getAuthor(), MentionedUser);
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getInstance().getObject(MentionedUser);

        dPlayer.setBlacklisted(false);
        dPlayer.save();

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());

        if (OPTION_LOG) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, MESSAGE_LOG_MESSAGE, e.getAuthor(), MentionedUser);

        }
    }
}