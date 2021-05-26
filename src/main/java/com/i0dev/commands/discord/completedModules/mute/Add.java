package com.i0dev.commands.discord.completedModules.mute;

import com.i0dev.object.objects.Type;
import com.i0dev.object.objects.RoleQueueObject;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.FindFromString;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Add {

    private static final String Identifier = "Mute Add";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.mute.parts.add.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.mute.parts.add.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.mute.parts.add.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.mute.parts.add.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.mute.parts.add.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.mute.parts.add.message.logMessage");
    private static final String MESSAGE_ALREADY_MUTED = Configuration.getString("modules.mute.parts.add.message.alreadyMuted");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.mute.parts.add.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Mute Add")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MuteManager.add, e.getAuthor());
            return;
        }
        String reason = FormatUtil.remainingArgFormatter(message, 3);
        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor());
            return;
        }
        Member MentionedMember = e.getGuild().getMember(MentionedUser);
        if (MuteManager.ROLE_MUTED_ROLE == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MuteManager.MESSAGE_ROLE_NOT_FOUND, e.getAuthor());
            return;
        }

        if (MentionedMember.getRoles().contains(MuteManager.ROLE_MUTED_ROLE)) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_ALREADY_MUTED, e.getAuthor(), MentionedUser);
            return;
        }

        new RoleQueueObject(MentionedUser.getIdLong(), MuteManager.ROLE_MUTED_ROLE.getIdLong(), Type.ADD_ROLE).add();

        String desc = MESSAGE_CONTENT
                .replace("{reason}", reason);

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor(), MentionedUser);

        if (OPTION_LOG) {
            String logMsg = MESSAGE_LOG_MESSAGE
                    .replace("{reason}", reason);
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, logMsg, e.getAuthor(), MentionedUser);

        }
    }
}
