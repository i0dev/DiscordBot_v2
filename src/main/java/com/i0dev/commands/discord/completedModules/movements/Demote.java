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
import org.json.simple.JSONObject;

public class Demote {

    private static final String Identifier = "Movement Demote";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.movement.parts.demote.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.movement.parts.demote.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.movement.parts.demote.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.movement.parts.demote.enabled");

    private static final String MESSAGE_MOVEMENT_MESSAGE = Configuration.getString("modules.movement.parts.demote.message.movementMessage");
    private static final String MESSAGE_CONTENT = Configuration.getString("modules.movement.parts.demote.message.general");
    private static final String MESSAGE_LOWEST_POS = Configuration.getString("modules.movement.parts.demote.message.lowestPos");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Movement Demote")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MovementManager.demote, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }

        Member MentionedMember = e.getGuild().getMember(MentionedUser);

        if (!MovementUtil.isAlreadyStaff(MentionedMember)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MovementManager.MESSAGE_NOT_STAFF, e.getAuthor(), MentionedUser);
            return;
        }

        if (MovementUtil.isLowestStaff(MentionedMember)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_LOWEST_POS, e.getAuthor(), MentionedUser);
            return;
        }

        Role currentParentRole = MovementUtil.getParentStaff(MentionedMember);
        Role previousRole = MovementUtil.getPreviousRole(currentParentRole);
        JSONObject previousRoleObject = MovementUtil.getPreviousRoleObject(currentParentRole);

        MovementUtil.removeOldRoles(MentionedMember, Long.valueOf(currentParentRole.getId()));
        MovementUtil.giveNewRoles(MentionedMember, Long.valueOf(previousRole.getId()));

        TempNicknameUtil.modifyNickname(MentionedUser, MovementManager.NICKNAME_FORMAT.replace("{userName}", MentionedUser.getName()).replace("{displayName}", previousRoleObject.get("displayName").toString()));

        String desc = MESSAGE_CONTENT
                .replace("{position}", previousRoleObject.get("displayName").toString());

        String mvtDesc = MESSAGE_MOVEMENT_MESSAGE
                .replace("{position}", previousRoleObject.get("displayName").toString())
                .replace("{roleMention}", previousRole.getAsMention());

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor(), MentionedUser);
        MessageUtil.sendMessage(GlobalConfig.MOVEMENT_CHANNEL_ID, mvtDesc, e.getAuthor(), MentionedUser);

    }
}
