package com.i0dev.commands.discord.completedModules.movements;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.MovementUtil;
import com.i0dev.utility.util.TempNicknameUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Resign {

    private static final String Identifier = "Movement Resign";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.movement.parts.resign.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.movement.parts.resign.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.movement.parts.resign.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.movement.parts.resign.enabled");

    private static final String MESSAGE_MOVEMENT_MESSAGE = Configuration.getString("modules.movement.parts.resign.message.movementMessage");
    private static final String MESSAGE_CONTENT = Configuration.getString("modules.movement.parts.resign.message.general");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Movement Resign")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MovementManager.resign, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }
        Member MentionedMember = e.getGuild().getMember(MentionedUser);

        if (!MovementUtil.isAlreadyStaff(MentionedMember)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MovementManager.MESSAGE_NOT_STAFF, e.getAuthor());
            return;
        }

        Role currentParentRole = MovementUtil.getParentStaff(MentionedMember);
        MovementUtil.removeOldRoles(MentionedMember, Long.valueOf(currentParentRole.getId()));
        TempNicknameUtil.modifyNickname(MentionedUser, "");

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor(), MentionedUser);
        MessageUtil.sendMessage(GlobalConfig.MOVEMENT_CHANNEL_ID, MESSAGE_MOVEMENT_MESSAGE, e.getAuthor(), MentionedUser);
    }
}
