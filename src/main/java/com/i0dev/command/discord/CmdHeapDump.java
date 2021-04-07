package com.i0dev.command.discord;

import com.i0dev.InitilizeBot;
import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.List;

public class CmdHeapDump extends ListenerAdapter {

    private final String Identifier = "Minecraft Server Heap Dump";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.heapDumpToDiscord.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.heapDumpToDiscord.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.heapDumpToDiscord.permissionLiteMode");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.heapDumpToDiscord.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.heapDumpToDiscord.enabled");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
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
                double[] tpsArray = org.bukkit.Bukkit.spigot().getTPS();


                desc.append("\n**__Minecraft Server Information:__**\n");
                desc.append("**Version:** ").append("`" + org.bukkit.Bukkit.getVersion() + "`").append("\n");
                desc.append("**TPS from last 1m:** ").append("`" + Math.round(tpsArray[0]) + "`").append("\n");
                desc.append("**TPS from last 5m:** ").append("`" + Math.round(tpsArray[1]) + "`").append("\n");
                desc.append("**TPS from last 15m:** ").append("`" + Math.round(tpsArray[2]) + "`").append("\n");
                desc.append("**Online Players:** ").append("`" + org.bukkit.Bukkit.getOnlinePlayers().size() + " / " + org.bukkit.Bukkit.getMaxPlayers() + "`").append("\n");
            }

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(desc.toString()).build()).queue();
        }
    }
}