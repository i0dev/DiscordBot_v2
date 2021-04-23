package com.i0dev.commands.discord.completedModules.mute;

import com.i0dev.engine.discord.RoleQueue;
import com.i0dev.engine.discord.Type;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Clear {

    private static final String Identifier = "Mute Clear";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.mute.parts.clear.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.mute.parts.clear.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.mute.parts.clear.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.mute.parts.clear.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.mute.parts.clear.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.mute.parts.clear.message.logMessage");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.mute.parts.clear.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Mute Clear")) {
            return;
        }
        if (MuteManager.ROLE_MUTED_ROLE == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MuteManager.MESSAGE_ROLE_NOT_FOUND, e.getAuthor());
            return;
        }

        for (Member member : e.getGuild().getMembers()) {
            if (member.getRoles().contains(MuteManager.ROLE_MUTED_ROLE)) {
                RoleQueue.addToQueue(member, MuteManager.ROLE_MUTED_ROLE, Type.REMOVE_ROLE);
            }
        }


        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());
        if (OPTION_LOG) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_LOG_MESSAGE, e.getAuthor());
        }

    }
}
