package main.java.com.i0dev.command;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.Logs;
import main.java.com.i0dev.util.Prettify;
import main.java.com.i0dev.util.conf;
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
        File file = new File("DiscordBot/storage/PlayerData_" + UUID.randomUUID() + ".txt");
        StringBuilder toFile = new StringBuilder();
        toFile.append("Total members: " + e.getGuild().getMemberCount()).append("\n");
        toFile.append("Created By: " + e.getAuthor().getAsTag() + " : " + e.getAuthor().getId()).append("\n");
        toFile.append("Created Time: " + Prettify.formatDate(ZonedDateTime.now()));

        toFile.append("\n\n\n");
        conf.GENERAL_MAIN_GUILD.getMembers().forEach(member -> {
            toFile.append(member.getId() + " : " + member.getUser().getAsTag() + " : " + Prettify.formatDate(member.getTimeJoined().toZonedDateTime()) + "\n");
        });

        try {
            Files.write(Paths.get(file.getAbsolutePath()), toFile.toString().getBytes());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        e.getAuthor().openPrivateChannel().complete().sendFile(file).queue();

    }
}
