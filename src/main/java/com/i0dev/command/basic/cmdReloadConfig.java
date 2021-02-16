package main.java.com.i0dev.command.basic;

import main.java.com.i0dev.util.*;
import main.java.com.i0dev.entity.Blacklist;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdReloadConfig extends ListenerAdapter {

    private final String Identifier = "Reload Config";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.reloadConfig.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.reloadConfig.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.reloadConfig.permissionLiteMode");
    private final String MESSAGE_DESCRIPTION = getConfig.get().getString("commands.reloadConfig.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.reloadConfig.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.reloadConfig.enabled");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}",conf.GENERAL_BOT_PREFIX+COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            conf.initGlobalConfig();
            getConfig.get().reloadConfig();
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_DESCRIPTION.replace("{tag}", e.getAuthor().getAsTag()), e.getAuthor())).build()).queue();
        }
    }
}