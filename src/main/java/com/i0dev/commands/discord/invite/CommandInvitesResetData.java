package com.i0dev.commands.discord.invite;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandInvitesResetData {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.invite_resetData.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.invite_resetData.permissionLiteMode");
    public static final String messageContent = Configuration.getString("commands.invite_resetData.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.invite_resetData.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.invite_resetData.enabled");
    public static final boolean LOGS_ENABLED = Configuration.getBoolean("commands.invite_resetData.log");
    public static final String LOGS_MESSAGE = Configuration.getString("commands.invite_resetData.logMessage");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Wipe Invite Data")) {
            return;
        }

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.INVITES_RESET_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        DPlayerEngine.getInstance().clearInvites();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(messageContent, e.getAuthor())).build()).queue();
        if (LOGS_ENABLED) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(Placeholders.convert(LOGS_MESSAGE, e.getAuthor())).build());
        }
    }

}
