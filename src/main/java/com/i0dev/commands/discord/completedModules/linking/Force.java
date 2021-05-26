package com.i0dev.commands.discord.completedModules.linking;

import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Force {
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.link.parts.force.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.link.parts.force.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.link.parts.force.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.link.parts.force.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.link.parts.force.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.link.parts.force.message.logMessage");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.link.parts.force.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Link Force")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length < 4) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), LinkManager.force, e.getAuthor());
            return;
        }

        User MentionedUser = FindFromString.get().getUser(message[2], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[2]), e.getAuthor())).build()).queue();
            return;
        }

        String ign = message[3];

        DPlayerEngine.setLinked(MentionedUser.getIdLong(), FormatUtil.GenerateRandomString(), ign, APIUtil.getUUIDFromIGN(ign));

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor(), MentionedUser);

        if (OPTION_LOG) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, MESSAGE_LOG_MESSAGE, e.getAuthor(), MentionedUser);
        }
        RoleRefreshHandler.RefreshUserRank(DPlayerEngine.getObject(MentionedUser.getIdLong()));

    }
}