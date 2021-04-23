package com.i0dev.commands.discord.points;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Set {
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.points.parts.set.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.points.parts.set.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.points.parts.set.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.points.parts.set.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.points.parts.set.message.general");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Points Set")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length <= 3) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PointsManager.set, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }
        String amount = message[3];
        if (!FormatUtil.isInt(amount)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PointsManager.MESSAGE_IS_NOT_NUMBER.replace("{arg}", message[3]), e.getAuthor());
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getInstance().getObject(MentionedUser);
        dPlayer.setPoints(Double.parseDouble(amount));


        String desc = MESSAGE_CONTENT
                .replace("{amount}", PointsManager.decimalFormat.format(Double.parseDouble(amount)));

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor(), MentionedUser);
        LogsFile.logPoints(MentionedUser.getAsTag() + " has been set to " + amount + " points from " + e.getAuthor().getAsTag());

    }
}
