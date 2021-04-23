package com.i0dev.commands.discord.minecraft;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandRunCommand {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.runIngameCommand.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.runIngameCommand.permissionLiteMode");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.runIngameCommand.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.runIngameCommand.enabled");
    public static final String messageContent = Configuration.getString("commands.runIngameCommand.messageContent");
    public static final boolean log = Configuration.getBoolean("commands.runIngameCommand.log");
    public static final String logMessage = Configuration.getString("commands.runIngameCommand.logMessage");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Run InGame Command")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.RUN_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        String command = FormatUtil.remainingArgFormatter(message, 1);
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), command);

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(messageContent
                .replace("{command}", "/" + command), e.getAuthor())).build()).queue();

        if (log) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, (EmbedFactory.createEmbed(logMessage
                    .replace("{command}", "/" + command))
                    .build()));
        }
    }

}