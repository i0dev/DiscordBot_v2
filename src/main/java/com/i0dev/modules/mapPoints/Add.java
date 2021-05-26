package com.i0dev.modules.mapPoints;

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

    public static boolean PERMISSION_STRICT;
    public static boolean PERMISSION_LITE;
    public static boolean PERMISSION_ADMIN;
    public static boolean ENABLED;
    public static String MESSAGE_CONTENT;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.mapPoints.parts.add.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.mapPoints.parts.add.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.mapPoints.parts.add.permission.admin");
        ENABLED = Configuration.getBoolean("modules.mapPoints.parts.add.enabled");
        MESSAGE_CONTENT = Configuration.getString("modules.mapPoints.parts.add.message.general");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "MapPoints Add")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length <= 4) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MapPointsManager.add, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }
        String server = message[3];
        if (!MapPointsManager.GENERAL_SERVER_LIST.contains(server.toLowerCase())) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MapPointsManager.MESSAGE_SERVER_NOT_VALID.replace("{list}", FormatUtil.FormatListString(MapPointsManager.GENERAL_SERVER_LIST)).replace("{arg}", message[3]), e.getAuthor());
            return;
        }

        String amount = message[4];
        if (!FormatUtil.isLong(amount)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MapPointsManager.MESSAGE_IS_NOT_NUMBER.replace("{arg}", message[4]), e.getAuthor());
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getObject(MentionedUser.getIdLong());

        long alreadyAmount = dPlayer.getMapPointsMap().has(server.toLowerCase()) ? (dPlayer.getMapPointsMap().get(server.toLowerCase()).getAsLong()) : 0;
        dPlayer.getMapPointsMap().addProperty(server.toLowerCase(), alreadyAmount + Long.parseLong(amount));
        DPlayerEngine.save(MentionedUser.getIdLong());
        String desc = MESSAGE_CONTENT
                .replace("{amount}", amount);

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor(), MentionedUser);


    }
}
