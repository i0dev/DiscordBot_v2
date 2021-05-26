package com.i0dev.modules.blacklist;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Add extends DiscordCommand {
    private static boolean PERMISSION_STRICT;
    private static boolean PERMISSION_LITE;
    private static boolean PERMISSION_ADMIN;
    private static boolean ENABLED;
    private static String MESSAGE_CONTENT;
    private static String MESSAGE_LOG_MESSAGE;
    private static String MESSAGE_ALREADY_ON_LIST;
    private static boolean OPTION_LOG;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.blacklist.parts.add.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.blacklist.parts.add.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.blacklist.parts.add.permission.admin");
        ENABLED = Configuration.getBoolean("modules.blacklist.parts.add.enabled");
        MESSAGE_CONTENT = Configuration.getString("modules.blacklist.parts.add.message.general");
        MESSAGE_LOG_MESSAGE = Configuration.getString("modules.blacklist.parts.add.message.logMessage");
        MESSAGE_ALREADY_ON_LIST = Configuration.getString("modules.blacklist.parts.add.message.alreadyOnList");
        OPTION_LOG = Configuration.getBoolean("modules.blacklist.parts.add.option.log");
    }


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Blacklist Add")) {
            return;
        }

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), BlacklistManager.add, e.getAuthor());
            return;
        }

        String reason = FormatUtil.remainingArgFormatter(message, 3);

        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }

        if (DPlayerEngine.getObject(MentionedUser.getIdLong()).isBlacklisted()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_ALREADY_ON_LIST, e.getAuthor(), MentionedUser);
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getObject(MentionedUser.getIdLong());
        dPlayer.setBlacklisted(true);
        dPlayer.save();

        String desc = MESSAGE_CONTENT
                .replace("{reason}", reason);

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor(), MentionedUser);

        if (OPTION_LOG) {
            String logMsg = MESSAGE_LOG_MESSAGE
                    .replace("{punisherTag}", e.getAuthor().getAsTag())
                    .replace("{reason}", reason);
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, logMsg, e.getAuthor(), MentionedUser);

        }
    }
}
