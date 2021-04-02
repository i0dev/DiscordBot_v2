package main.java.com.i0dev.command.discord.basic;

import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdServerInfo extends ListenerAdapter {

    private final String Identifier = "Server Info";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.serverInfo.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.serverInfo.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.serverInfo.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.serverInfo.messageTitle");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.serverInfo.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.serverInfo.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.serverInfo.enabled");


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
            Guild guild = GlobalConfig.GENERAL_MAIN_GUILD;

            String[] Created = e.getGuild().getTimeCreated().toString().split("-");
            String description = MESSAGE_CONTENT
                    .replace("{serverOwner}", guild.getOwner().getAsMention())
                    .replace("{serverRegion}", guild.getRegion().getName())
                    .replace("{serverCreationDate}", Created[1] + "/" + Created[2].substring(0, 2) + "/" + Created[0])
                    .replace("{memberCount}", guild.getMemberCount() + "")
                    .replace("{categoryCount}", guild.getCategories().size() + "")
                    .replace("{TextChannelCount}", guild.getTextChannels().size() + "")
                    .replace("{VoiceChannelCount}", guild.getVoiceChannels().size() + "")
                    .replace("{categoryCount}", guild.getCategories().size() + "")
                    .replace("{roleCount}", guild.getRoles().size() + "")
                    .replace("{BotPrefix}", GlobalConfig.GENERAL_BOT_PREFIX)
                    .replace("{BotCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getUser().isBot()).count())
                    .replace("{HumanCount}", "" + e.getGuild().getMembers().stream().filter(member -> !member.getUser().isBot()).count())
                    .replace("{onlineCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE)).count())
                    .replace("{DNDCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)).count())
                    .replace("{idleCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.IDLE)).count())
                    .replace("{offlineCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count())
                    .replace("{inviteLink}", e.getGuild().getDefaultChannel().createInvite().complete().getUrl());


            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TITLE, e.getAuthor()), Placeholders.convert(description, e.getAuthor())).build()).queue();

        }
    }
}