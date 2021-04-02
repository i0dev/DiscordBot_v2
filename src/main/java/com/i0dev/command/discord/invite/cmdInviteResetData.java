package main.java.com.i0dev.command.discord.invite;

import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.object.InviteMatcher;
import main.java.com.i0dev.object.Invites;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.MessageUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdInviteResetData extends ListenerAdapter {


    private final String Identifier = "Wipe invite data";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.invite_resetData.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.invite_resetData.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.invite_resetData.permissionLiteMode");
    private final String messageContent = getConfig.get().getString("commands.invite_resetData.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.invite_resetData.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.invite_resetData.enabled");
    private final boolean LOGS_ENABLED = getConfig.get().getBoolean("commands.invite_resetData.log");
    private final String LOGS_MESSAGE = getConfig.get().getString("commands.invite_resetData.logMessage");

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
            Invites.get().wipeCache();
            InviteMatcher.get().wipeCache();
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(messageContent, e.getAuthor())).build()).queue();
            if (LOGS_ENABLED) {
                MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.get().createSimpleEmbed(Placeholders.convert(LOGS_MESSAGE, e.getAuthor())).build());
            }
        }
    }
}
