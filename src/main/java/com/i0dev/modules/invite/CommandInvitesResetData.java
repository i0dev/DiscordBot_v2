package com.i0dev.modules.invite;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandInvitesResetData extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static boolean COMMAND_ENABLED;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static boolean LOGS_ENABLED;
    public static String LOGS_MESSAGE;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.invite_resetData.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.invite_resetData.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.invite_resetData.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.invite_resetData.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.invite_resetData.enabled");
        LOGS_ENABLED = Configuration.getBoolean("commands.invite_resetData.log");
        LOGS_MESSAGE = Configuration.getString("commands.invite_resetData.logMessage");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Wipe Invite Data")) {
            return;
        }

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.INVITES_RESET_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        DPlayerEngine.clear("invites");

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();
        if (LOGS_ENABLED) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(Placeholders.convert(LOGS_MESSAGE, e.getAuthor())).build());
        }
    }

}
