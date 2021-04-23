package com.i0dev.commands.discord.basic;

import com.i0dev.InitilizeBot;
import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

public class CommandHeapDump {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.heapDumpToDiscord.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.heapDumpToDiscord.permissionLiteMode");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.heapDumpToDiscord.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.heapDumpToDiscord.enabled");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Heap Dump")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.HEAP_DUMP_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        StringBuilder desc = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();
        NumberFormat integerInstance = NumberFormat.getIntegerInstance();

        desc.append("**__RunTime Information:__**\n");
        desc.append("**Free RAM:** ").append("`" + integerInstance.format(runtime.freeMemory() / 1024L / 1024L) + " MB`").append("\n");
        desc.append("**Used RAM:** ").append("`" + integerInstance.format(((runtime.maxMemory() - runtime.freeMemory()) / 1024L / 1024L)) + " MB`").append("\n");
        desc.append("**Max RAM:** ").append("`" + integerInstance.format(runtime.maxMemory() / 1024L / 1024L) + " MB`").append("\n");
        desc.append("**Available Processors:** ").append("`" + integerInstance.format(runtime.availableProcessors()) + "`").append("\n");
        desc.append("**System Load Average:** ").append("`" + ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() + "%`").append("\n");

        if (InitilizeBot.get().isPluginMode()) {
            desc.append("\n**__Minecraft Server Information:__**\n");
            desc.append("**Version:** ").append("`" + org.bukkit.Bukkit.getVersion() + "`").append("\n");
            desc.append("**Ticks Per Second:** ").append("`" + Math.round(Lag.getTPS()) + "`").append("\n");
            desc.append("**Online Players:** ").append("`" + org.bukkit.Bukkit.getOnlinePlayers().size() + " / " + org.bukkit.Bukkit.getMaxPlayers() + "`").append("\n");
        }

        e.getChannel().sendMessage(EmbedFactory.createEmbed(desc.toString()).build()).queue();
    }

}