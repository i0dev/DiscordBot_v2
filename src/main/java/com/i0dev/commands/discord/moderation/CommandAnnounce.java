package com.i0dev.commands.discord.moderation;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandAnnounce {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.announce.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.announce.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.announce.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.announce.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.announce.enabled");
    public static final String announcementTitle = Configuration.getString("commands.announce.announcementTitle");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Announce")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length <= 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.ANNOUNCE_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        TextChannel Channel = FindFromString.get().getTextChannel(message[1], e.getMessage());
        if (Channel == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_CHANNEL_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }

        String content = FormatUtil.remainingArgFormatter(message, 2);
        Channel.sendMessage(EmbedFactory.createEmbed(Placeholders.convert(announcementTitle, e.getAuthor()), content).build()).queue();
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{channel}", Channel.getAsMention()), e.getAuthor())).build()).queue();

    }
}
