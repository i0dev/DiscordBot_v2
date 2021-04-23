package com.i0dev.commands.discord.basic;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandMemberCount {

    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.memberCount.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.memberCount.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.memberCount.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.memberCount.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.memberCount.enabled");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Member Count")) {
            return;
        }

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.MEMBER_COUNT_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        String description = MESSAGE_CONTENT
                .replace("{BotCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getUser().isBot()).count())
                .replace("{memberCount}", "" + e.getGuild().getMemberCount())
                .replace("{HumanCount}", "" + e.getGuild().getMembers().stream().filter(member -> !member.getUser().isBot()).count())
                .replace("{onlineCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE)).count())
                .replace("{DNDCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)).count())
                .replace("{idleCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.IDLE)).count())
                .replace("{offlineCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count());

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(description, e.getAuthor())).build()).queue();
    }
}