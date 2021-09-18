package com.i0dev.commands.discord;

import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.UUID;

public class CommandExportData extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getMessage().getContentRaw().equalsIgnoreCase(GlobalConfig.GENERAL_BOT_PREFIX + "exportData")) return;
        if (!e.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) return;
        File file = new File(InitializeBot.get().getStorageDirPath() + "/PlayerData_" + UUID.randomUUID() + ".txt");
        StringBuilder toFile = new StringBuilder();
        toFile.append("Total members: " + e.getGuild().getMemberCount()).append("\n");
        toFile.append("Created By: " + e.getAuthor().getAsTag() + " : " + e.getAuthor().getId()).append("\n");
        toFile.append("Created Time: " + FormatUtil.formatDate(ZonedDateTime.now()));

        toFile.append("\n\n\n");
        GlobalConfig.GENERAL_MAIN_GUILD.getMembers().forEach(member -> {
            toFile.append(member.getId() + " : " + member.getUser().getAsTag() + " : " + FormatUtil.formatDate(member.getTimeJoined().toZonedDateTime()) + "\n");
        });

        toFile.append("\n\n");

        DPlayerEngine.getCache().stream().filter(o -> ((DPlayer) o).getLinkInfo().isLinked()).forEach(o -> {
            toFile.append(".link force " + ((DPlayer) o).getDiscordID() + " " + ((DPlayer) o).getCachedData().getMinecraftIGN() + "\n");
        });

        try {
            Files.write(Paths.get(file.getAbsolutePath()), toFile.toString().getBytes());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        e.getAuthor().openPrivateChannel().complete().sendFile(file).queue();

    }
}
