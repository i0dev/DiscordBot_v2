package com.i0dev.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;

public class Logs {


    public static String getLogContents(MessageHistory messageHistory, JSONObject ticketObj, TextChannel ticketChannel, User ticketCloser, String ticketCloseReason) {

        String Zone = ZonedDateTime.now().getZone().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String ticketOwnerID = ticketObj.get("ticketOwnerID").toString();
        String ticketOwnerAvatarURL = ticketObj.get("ticketOwnerAvatarURL").toString();
        String ticketOwnerTag = ticketObj.get("ticketOwnerTag").toString();
        String adminOnlyMode = ticketObj.get("adminOnlyMode").toString();

        StringBuilder toFile = new StringBuilder();
        toFile.append("Ticket information:").append("\n");
        toFile.append("     Channel ID: ").append(ticketChannel.getId()).append("\n");
        toFile.append("     Channel Name: ").append(ticketChannel.getName()).append("\n");
        toFile.append("     Ticket Owner ID: ").append(ticketOwnerID).append("\n");
        toFile.append("     Ticket Owner Tag: ").append(ticketOwnerTag).append("\n");
        toFile.append("     Ticket Owner Avatar: ").append(ticketOwnerAvatarURL).append("\n");
        toFile.append("     Admin Only Mode: ").append(adminOnlyMode).append("\n");
        toFile.append("     Ticket Closer Tag: ").append(ticketCloser.getAsTag()).append("\n");
        toFile.append("     Ticket Closer ID: ").append(ticketCloser.getId()).append("\n");
        toFile.append("     Ticket Close reason: ").append(ticketCloseReason).append("\n");
        toFile.append("\nTicket logs (TimeZone: ").append(Zone).append("):").append("\n");

        ArrayList<Message> messageHistoryList = new ArrayList<>(messageHistory.getRetrievedHistory());
        Collections.reverse(messageHistoryList);

        for (Message message : messageHistoryList) {
            String Month = message.getTimeCreated().toZonedDateTime().getMonth().getValue() + "";
            String Day = message.getTimeCreated().toZonedDateTime().getDayOfMonth() + "";
            String Year = message.getTimeCreated().toZonedDateTime().getYear() + "";
            String Hour = message.getTimeCreated().atZoneSameInstant(ZoneId.of("America/New_York")).getHour() + "";
            String Minute = message.getTimeCreated().toZonedDateTime().getMinute() + "";

            String time = "[" + Month + "/" + Day + "/" + Year + " " + Hour + ":" + Minute + "" + "EST" + "] ";
            try {
                toFile.append("     ").append(time).append(" [").append(message.getAuthor().getAsTag()).append("]: ").append(message.getContentDisplay()).append("\n");
            } catch (Exception ignored) {
                toFile.append("     ").append(time).append(" [").append("UnknownUser").append("]: ").append(message.getContentDisplay()).append("\n");
            }
        }
        return toFile.toString();
    }

    public static File getLogsFile(TextChannel channel) {
        return new File("DiscordBot/storage/tickets/logs/" + channel.getName() + ".txt");
    }
}
