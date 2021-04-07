package com.i0dev.command.discord.minecraftAPI;

import com.i0dev.InitilizeBot;
import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class CmdRunCmd extends ListenerAdapter {

    private final String Identifier = "Run InGame Command";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.runIngameCommand.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.runIngameCommand.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.runIngameCommand.permissionLiteMode");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.runIngameCommand.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.runIngameCommand.enabled");
    private final String messageContent = getConfig.get().getString("commands.runIngameCommand.messageContent");
    private final boolean log = getConfig.get().getBoolean("commands.runIngameCommand.log");
    private final String logMessage = getConfig.get().getString("commands.runIngameCommand.logMessage");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!InitilizeBot.get().isPluginMode()) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(GlobalConfig.NOT_PLUGIN_MODE).build()).queue();
            return;
        }
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
            if (message.length == 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            String command = FormatUtil.remainingArgFormatter(message, 1);
            if (command.startsWith("/")) {
                command = command.substring(1);
            }
            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), command);

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(messageContent
                    .replace("{command}", "/" + command), e.getAuthor())).build()).queue();

            if (log) {
                MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, (EmbedFactory.get().createSimpleEmbed(logMessage
                        .replace("{command}", "/" + command))
                        .build()));
            }
        }
    }
}