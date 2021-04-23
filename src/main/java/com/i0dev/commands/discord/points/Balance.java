package com.i0dev.commands.discord.points;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Balance {

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.points.parts.balance.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.points.parts.balance.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.points.parts.balance.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.points.parts.balance.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.points.parts.balance.message.general");

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