package com.i0dev.commands.discord.completedModules.mute;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Retrieve {

    private static final String Identifier = "Mute List";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.mute.parts.list.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.mute.parts.list.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.mute.parts.list.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.mute.parts.list.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.mute.parts.list.message.general");
    private static final String MESSAGE_FORMAT = Configuration.getString("modules.mute.parts.list.message.format");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Mute List")) {
            return;
        }
        if (MuteManager.ROLE_MUTED_ROLE == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MuteManager.MESSAGE_ROLE_NOT_FOUND, e.getAuthor());
            return;
        }


        List<String> mutedUserTags = new ArrayList<>();
        for (Member member : e.getGuild().getMembers()) {
            if (member.getRoles().contains(MuteManager.ROLE_MUTED_ROLE)) {
                mutedUserTags.add(Placeholders.convert(MESSAGE_FORMAT, null, member.getUser()));
            }
        }


        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{list}", FormatUtil.FormatListString(mutedUserTags)), e.getAuthor());

    }
}
