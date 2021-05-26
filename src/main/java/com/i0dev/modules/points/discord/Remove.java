package com.i0dev.modules.points.discord;

import com.i0dev.Engine;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.object.objects.LogObject;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;

public class Remove extends DiscordCommand {
    public static boolean PERMISSION_STRICT;
    public static boolean PERMISSION_LITE;
    public static boolean PERMISSION_ADMIN;
    public static boolean ENABLED;
    public static String MESSAGE_CONTENT;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.points.parts.remove.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.points.parts.remove.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.points.parts.remove.permission.admin");
        ENABLED = Configuration.getBoolean("modules.points.parts.remove.enabled");
        MESSAGE_CONTENT = Configuration.getString("modules.points.parts.remove.message.general");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Points Remove")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length <= 3) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PointsManager.remove, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }
        String amount = message[3];
        if (!FormatUtil.isInt(amount) && Double.parseDouble(amount) > 0) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PointsManager.MESSAGE_IS_NOT_NUMBER.replace("{arg}", message[3]), e.getAuthor());
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getObject(MentionedUser.getIdLong());

        double newPoints = dPlayer.getPoints() - Double.parseDouble(amount);
        if (newPoints < 0) {
            newPoints = 0;
        }
        dPlayer.setPoints(newPoints);
        DPlayerEngine.save(MentionedUser.getIdLong());

        String desc = MESSAGE_CONTENT
                .replace("{amount}", PointsManager.decimalFormat.format(Double.parseDouble(amount)));

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor(), MentionedUser);
        String message1 = ("[{tag}] has lost [{points}] points by [{staff}]"
                .replace("{tag}", MentionedUser.getAsTag())
                .replace("{points}", amount)
                .replace("{staff}", e.getAuthor().getAsTag()));

        message1 = "" + FormatUtil.formatDate(System.currentTimeMillis()) + ": " + message1;
        Engine.getToLog().add(new LogObject(message1, new File(InitializeBot.get().getPointLogPath())));
    }
}
