package com.i0dev.commands.discord.completedModules.gamemode.prison;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Confirm {
    private static final String Identifier = "Gamemode Cell Confirm";
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.gamemode.parts.prison.parts.confirm.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.gamemode.parts.prison.parts.confirm.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.gamemode.parts.prison.parts.confirm.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.gamemode.parts.prison.parts.confirm.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.gamemode.parts.prison.parts.confirm.message.general");
    private static final String MESSAGE_CONFIRM_MESSAGE = Configuration.getString("modules.gamemode.parts.prison.parts.confirm.message.confirmMessage");
    private static final String MESSAGE_CONFIRM_MESSAGE_TITLE = Configuration.getString("modules.gamemode.parts.prison.parts.confirm.message.confirmMessageTitle");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Gamemode Prison Confirm")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 3) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PrisonModule.confirmMod, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[3], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[3]), e.getAuthor());
            return;
        }
        String faction = message[4];
        String rosterSize = message[5];

        MessageUtil.sendMessage(PrisonModule.CONFIRMED_CELL_CHANNEL,
                MESSAGE_CONFIRM_MESSAGE
                        .replace("{cell}", faction)
                        .replace("{rosterSize}", rosterSize),
                MESSAGE_CONFIRM_MESSAGE_TITLE,
                e.getAuthor(), MentionedUser);

        MessageUtil.sendMessage(e.getChannel().getIdLong(),
                MESSAGE_CONTENT
                        .replace("{cell}", faction)
                        .replace("{senderTag}", e.getAuthor().getAsTag())
                        .replace("{rosterSize}", rosterSize),
                e.getAuthor(), MentionedUser);
    }
}