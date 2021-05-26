package com.i0dev.modules.blacklist;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Remove extends DiscordCommand {

    private static boolean PERMISSION_STRICT;
    private static boolean PERMISSION_LITE;
    private static boolean PERMISSION_ADMIN;
    private static boolean ENABLED;
    private static String MESSAGE_CONTENT;
    private static String MESSAGE_LOG_MESSAGE;
    private static String MESSAGE_NOT_ON_LIST;
    private static boolean OPTION_LOG;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.blacklist.parts.remove.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.blacklist.parts.remove.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.blacklist.parts.remove.permission.admin");
        ENABLED = Configuration.getBoolean("modules.blacklist.parts.remove.enabled");
        MESSAGE_CONTENT = Configuration.getString("modules.blacklist.parts.remove.message.general");
        MESSAGE_LOG_MESSAGE = Configuration.getString("modules.blacklist.parts.remove.message.logMessage");
        MESSAGE_NOT_ON_LIST = Configuration.getString("modules.blacklist.parts.remove.message.notOnList");
        OPTION_LOG = Configuration.getBoolean("modules.blacklist.parts.remove.option.log");
    }

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

        if (!DPlayerEngine.getObject(MentionedUser.getIdLong()).isBlacklisted()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_NOT_ON_LIST, e.getAuthor(), MentionedUser);
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getObject(MentionedUser.getIdLong());

        dPlayer.setBlacklisted(false);
        dPlayer.save();

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());

        if (OPTION_LOG) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, MESSAGE_LOG_MESSAGE, e.getAuthor(), MentionedUser);

        }
    }
}