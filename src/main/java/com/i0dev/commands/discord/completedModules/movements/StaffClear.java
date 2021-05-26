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

public class StaffClear {

    private static final String Identifier = "Movement StaffClear";
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.movement.parts.staffClear.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.movement.parts.staffClear.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.movement.parts.staffClear.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.movement.parts.staffClear.enabled");

    private static final String MESSAGE_MOVEMENT_MESSAGE = Configuration.getString("modules.movement.parts.staffClear.message.movementMessage");
    private static final String MESSAGE_CONTENT = Configuration.getString("modules.movement.parts.staffClear.message.general");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Movement Clear")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MovementManager.clear, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }
        Member MentionedMember = e.getGuild().getMember(MentionedUser);

        if (!MovementUtil.isAlreadyStaff(MentionedMember)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MovementManager.MESSAGE_NOT_STAFF, e.getAuthor(),MentionedUser);
            return;
        }

        Role currentParentRole = MovementUtil.getParentStaff(MentionedMember);
        MovementUtil.removeOldRoles(MentionedMember, Long.valueOf(currentParentRole.getId()));
        TempNicknameUtil.modifyNickname(MentionedUser, "");


        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor(), MentionedUser);
        MessageUtil.sendMessage(GlobalConfig.MOVEMENT_CHANNEL_ID, MESSAGE_MOVEMENT_MESSAGE, e.getAuthor(), MentionedUser);

    }
}
