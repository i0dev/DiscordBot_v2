package com.i0dev.modules.basic;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandServerInfo extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_TITLE;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.serverInfo.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.serverInfo.permissionLiteMode");
        MESSAGE_TITLE = Configuration.getString("commands.serverInfo.messageTitle");
        MESSAGE_CONTENT = Configuration.getString("commands.serverInfo.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.serverInfo.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.serverInfo.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Server Info")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.SERVER_INFO_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
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


        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_TITLE, e.getAuthor()), Placeholders.convert(description, e.getAuthor())).build()).queue();

    }
}
