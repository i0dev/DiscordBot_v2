package com.i0dev.modules.basic;

import com.i0dev.InitializeBot;
import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandReloadConfig extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_DESCRIPTION;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.reloadConfig.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.reloadConfig.permissionLiteMode");
        MESSAGE_DESCRIPTION = Configuration.getString("commands.reloadConfig.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.reloadConfig.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.reloadConfig.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Reload Config")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.RELOAD_CONFIG_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        Configuration.reloadConfig();
        InitializeBot.initializeCommands();
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_DESCRIPTION.replace("{tag}", e.getAuthor().getAsTag()), e.getAuthor())).build()).queue();

    }

}