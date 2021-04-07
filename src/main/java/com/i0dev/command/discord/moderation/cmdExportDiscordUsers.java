package com.i0dev.command.discord.moderation;

import com.i0dev.utility.util.FormatUtil;
import com.i0dev.InitilizeBot;
import com.i0dev.object.Blacklist;
import com.i0dev.utility.GlobalConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.UUID;

public class cmdExportDiscordUsers extends ListenerAdapter {

    private final String Identifier = "Export User Data";

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
        if (!e.getMessage().getContentRaw().equals(".exportData")) return;
        if (!e.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) return;
        File file = new File(InitilizeBot.get().getStorageDirPath() + "/PlayerData_" + UUID.randomUUID() + ".txt");
        StringBuilder toFile = new StringBuilder();
        toFile.append("Total members: " + e.getGuild().getMemberCount()).append("\n");
        toFile.append("Created By: " + e.getAuthor().getAsTag() + " : " + e.getAuthor().getId()).append("\n");
        toFile.append("Created Time: " + FormatUtil.formatDate(ZonedDateTime.now()));

        toFile.append("\n\n\n");
        GlobalConfig.GENERAL_MAIN_GUILD.getMembers().forEach(member -> {
            toFile.append(member.getId() + " : " + member.getUser().getAsTag() + " : " + FormatUtil.formatDate(member.getTimeJoined().toZonedDateTime()) + "\n");
        });

        try {
            Files.write(Paths.get(file.getAbsolutePath()), toFile.toString().getBytes());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        e.getAuthor().openPrivateChannel().complete().sendFile(file).queue();

    }
}
