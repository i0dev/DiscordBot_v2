package com.i0dev.modules.basic;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandRunCommand extends DiscordCommand {
    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;
    public static String messageContent;
    public static boolean log;
    public static String logMessage;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.runIngameCommand.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.runIngameCommand.permissionLiteMode");
        MESSAGE_FORMAT = Configuration.getString("commands.runIngameCommand.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.runIngameCommand.enabled");
        messageContent = Configuration.getString("commands.runIngameCommand.messageContent");
        log = Configuration.getBoolean("commands.runIngameCommand.log");
        logMessage = Configuration.getString("commands.runIngameCommand.logMessage");
    }

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
        String finalCommand = command;
        org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(com.i0dev.DiscordBot.get(), () -> com.i0dev.DiscordBot.get().runCommand(org.bukkit.Bukkit.getConsoleSender(), finalCommand));

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(messageContent
                .replace("{command}", "/" + command), e.getAuthor())).build()).queue();

        if (log) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, (EmbedFactory.createEmbed(Placeholders.convert(logMessage
                    .replace("{command}", "/" + command), e.getAuthor()))
                    .build()));
        }
    }

}