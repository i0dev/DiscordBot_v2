package com.i0dev.commands.discord.basic;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandReloadConfig {

    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.reloadConfig.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.reloadConfig.permissionLiteMode");
    public static final String MESSAGE_DESCRIPTION = Configuration.getString("commands.reloadConfig.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.reloadConfig.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.reloadConfig.enabled");

    public static void run(GuildMessageReceivedEvent e) {
        if (! GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Reload Config")){
        return;
    }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.RELOAD_CONFIG_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_DESCRIPTION.replace("{tag}", e.getAuthor().getAsTag()), e.getAuthor())).build()).queue();
        Configuration.reload();

    }

}