package com.i0dev.modules.points.discord;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Balance extends DiscordCommand {

    public static boolean PERMISSION_STRICT;
    public static boolean PERMISSION_LITE;
    public static boolean PERMISSION_ADMIN;
    public static boolean ENABLED;
    public static String MESSAGE_CONTENT;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.points.parts.balance.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.points.parts.balance.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.points.parts.balance.permission.admin");
        MESSAGE_CONTENT = Configuration.getString("modules.points.parts.balance.message.general");
        ENABLED = Configuration.getBoolean("modules.points.parts.balance.enabled");
    }


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Points Balance")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length > 3) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PointsManager.balance, e.getAuthor());
            return;
        }

        if (message.length == 3) {
            User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
            if (MentionedUser == null) {
                MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
                return;
            }
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, MentionedUser);
        } else {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());
        }
    }

}