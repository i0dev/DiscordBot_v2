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

public class Promote {

    private static final String Identifier = "Movement Promote";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.movement.parts.promote.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.movement.parts.promote.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.movement.parts.promote.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.movement.parts.promote.enabled");

    private static final String MESSAGE_MOVEMENT_MESSAGE = Configuration.getString("modules.movement.parts.promote.message.movementMessage");
    private static final String MESSAGE_CONTENT = Configuration.getString("modules.movement.parts.promote.message.general");
    private static final String MESSAGE_HIGHEST_POS = Configuration.getString("modules.movement.parts.promote.message.highestPos");


    public static void run(GuildMessageReceivedEvent e) {
        try {
            if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Movement Promote")) {
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length == 2) {
                MessageUtil.sendMessage(e.getChannel().getIdLong(), MovementManager.promote, e.getAuthor());
                return;
            }
            User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
            if (MentionedUser == null) {
                MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
                return;
            }

            Member MentionedMember = e.getGuild().getMember(MentionedUser);

            if (!MovementUtil.isAlreadyStaff(MentionedMember)) {
                JSONObject firstRoleObject = MovementUtil.getTracks().get(0);
                MovementUtil.giveNewRoles(MentionedMember, (Long) MovementUtil.getTracks().get(0).get("mainRole"));


                String desc = MESSAGE_CONTENT
                        .replace("{position}", firstRoleObject.get("displayName").toString());

                String mvtDesc = MESSAGE_MOVEMENT_MESSAGE
                        .replace("{position}", firstRoleObject.get("displayName").toString());

                MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor(), MentionedUser);
                MessageUtil.sendMessage(GlobalConfig.MOVEMENT_CHANNEL_ID, mvtDesc, e.getAuthor(), MentionedUser);
                TempNicknameUtil.modifyNickname(MentionedUser, MovementManager.NICKNAME_FORMAT.replace("{userName}", MentionedUser.getName()).replace("{displayName}", firstRoleObject.get("displayName").toString()));
                return;
            }
            if (MovementUtil.isHighestStaff(MentionedMember)) {
                MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_HIGHEST_POS, e.getAuthor(), MentionedUser);
                return;
            }

            Role currentParentRole = MovementUtil.getParentStaff(MentionedMember);
            Role nextRole = MovementUtil.getNextRole(currentParentRole);
            JSONObject nextRoleObject = MovementUtil.getNextRoleObject(currentParentRole);

            MovementUtil.removeOldRoles(MentionedMember, currentParentRole.getIdLong());
            MovementUtil.giveNewRoles(MentionedMember, nextRole.getIdLong());

            TempNicknameUtil.modifyNickname(MentionedUser, MovementManager.NICKNAME_FORMAT.replace("{userName}", MentionedUser.getName()).replace("{displayName}", nextRoleObject.get("displayName").toString()));


            String desc = MESSAGE_CONTENT
                    .replace("{position}", nextRoleObject.get("displayName").toString());

            String mvtDesc = MESSAGE_MOVEMENT_MESSAGE
                    .replace("{position}", nextRoleObject.get("displayName").toString())
                    .replace("{roleMention}", nextRole.getAsMention());

            MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor(), MentionedUser);
            MessageUtil.sendMessage(GlobalConfig.MOVEMENT_CHANNEL_ID, mvtDesc, e.getAuthor(), MentionedUser);
        } catch (Exception er) {
            er.printStackTrace();
        }
    }
}
