package com.i0dev.commands.discord.completedModules.mute;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CreateRole {
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.mute.parts.createRole.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.mute.parts.createRole.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.mute.parts.createRole.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.mute.parts.createRole.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.mute.parts.createRole.message.general");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Mute Create")) {
            return;
        }

        Role role = e.getGuild().createRole().setName("Muted").setColor(java.awt.Color.darkGray).complete();
        for (TextChannel channel : e.getGuild().getTextChannels()) {
            channel.putPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE).queueAfter(5, TimeUnit.SECONDS);
        }
        for (VoiceChannel channel : e.getGuild().getVoiceChannels()) {
            channel.putPermissionOverride(role).setDeny(Permission.VOICE_SPEAK).queueAfter(5, TimeUnit.SECONDS);
        }

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT.replace("{ID}", role.getId()), e.getAuthor());

    }
}
