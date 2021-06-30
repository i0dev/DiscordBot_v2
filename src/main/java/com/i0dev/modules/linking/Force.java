package com.i0dev.modules.linking;

import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Force extends DiscordCommand {
    private static boolean PERMISSION_STRICT;
    private static boolean PERMISSION_LITE;
    private static boolean PERMISSION_ADMIN;
    private static boolean ENABLED;

    private static String MESSAGE_CONTENT;
    private static String MESSAGE_LOG_MESSAGE;

    private static boolean OPTION_LOG;


    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.link.parts.force.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.link.parts.force.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.link.parts.force.permission.admin");
        ENABLED = Configuration.getBoolean("modules.link.parts.force.enabled");

        MESSAGE_CONTENT = Configuration.getString("modules.link.parts.force.message.general");
        MESSAGE_LOG_MESSAGE = Configuration.getString("modules.link.parts.force.message.logMessage");

        OPTION_LOG = Configuration.getBoolean("modules.link.parts.force.option.log");
    }

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

        DPlayerEngine.setLinked(MentionedUser.getIdLong(), FormatUtil.GenerateRandomString(), ign, APIUtil.getUUIDFromIGN(ign).toString());

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor(), MentionedUser);

        if (OPTION_LOG) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, MESSAGE_LOG_MESSAGE, e.getAuthor(), MentionedUser);
        }
        RoleRefreshHandler.RefreshUserRank(DPlayerEngine.getObject(MentionedUser.getIdLong()));

    }
}