package com.i0dev.command.discord.gamemodeSpecific;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;
import com.i0dev.utility.util.PermissionUtil;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdConfirmIsland extends ListenerAdapter {

    private final String Identifier = "Confirm Island";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.confirmIsland.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.confirmIsland.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.confirmIsland.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.confirmIsland.messageContent");
    private final String MESSAGE_MESSAGE_TITLE = getConfig.get().getString("commands.confirmIsland.confirmMessageTitle");
    private final String MESSAGE_MESSAGE_DESC = getConfig.get().getString("commands.confirmIsland.confirmMessageDesc");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.confirmIsland.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.confirmIsland.enabled");
    private final String confirmedIslandChannel = getConfig.get().getString("channels.confirmedIslandChannelID");

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
            if (message.length != 4) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            }
            String island = message[2];
            String rosterSize = message[3];

            e.getGuild().getTextChannelById(confirmedIslandChannel).sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_MESSAGE_TITLE, MentionedUser), Placeholders.convert(MESSAGE_MESSAGE_DESC
                    .replace("{island}", island)
                    .replace("{rosterSize}", rosterSize
                    ), MentionedUser)).build()).queue();

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT
                    .replace("{island}", island)
                    .replace("{senderTag}", e.getAuthor().getAsTag())
                    .replace("{rosterSize}", rosterSize), MentionedUser)).build()).queue();
        }
    }
}