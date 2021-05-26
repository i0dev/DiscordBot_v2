package com.i0dev.utility.util;

import com.i0dev.InitializeBot;
import com.i0dev.object.objects.Ticket;
import net.dv8tion.jda.api.entities.*;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class LogsUtil {


    public static String getLogContents(MessageHistory messageHistory, Ticket ticketObj, TextChannel ticketChannel, User ticketCloser, String ticketCloseReason) {

        String Zone = ZonedDateTime.now().getZone().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        Long ticketOwnerID = ticketObj.getTicketOwnerID();
        String ticketOwnerAvatarURL = ticketObj.getTicketOwnerAvatarURL();
        String ticketOwnerTag = ticketObj.getTicketOwnerTag();
        boolean adminOnlyMode = ticketObj.isAdminOnlyMode();

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
            String Second = message.getTimeCreated().toZonedDateTime().getSecond() + "";

            String time = "[" + Month + "/" + Day + "/" + Year + " " + Hour + ":" + Minute + ":" + Second + "]";
            if (message.getEmbeds().size() > 0) {
                MessageEmbed embed = message.getEmbeds().get(0);
                try {
                    toFile.append("     ").append(time).append(" [").append(message.getAuthor().getAsTag()).append("]: ").append("[EMBED]"
                            + "\n          Title: " + embed.getTitle()
                            + "\n          Desc: " + embed.getDescription()
                            + "\n          Footer: " + embed.getFooter().getText()
                    ).append("\n");
                } catch (Exception ignored) {
                    toFile.append("     ").append(time).append(" [").append("UnknownUser").append("]: ").append("[EMBED]"
                            + "\n          Title: " + embed.getTitle()
                            + "\n          Desc: " + embed.getDescription()
                    );
                    try {
                        toFile.append("\n          Footer: " + embed.getFooter().getText()
                        );
                    } catch (Exception ignored1) {

                    }

                    toFile.append("\n");
                }
            } else {
                try {
                    toFile.append("     ").append(time).append(" [").append(message.getAuthor().getAsTag()).append("]: ").append(message.getContentDisplay()).append("\n");
                } catch (Exception ignored) {
                    toFile.append("     ").append(time).append(" [").append("UnknownUser").append("]: ").append(message.getContentDisplay()).append("\n");
                }
            }
        }
        return toFile.toString();
    }

    public static File getLogsFile(TextChannel channel) {
        return new File(InitializeBot.get().getTicketLogsDirPath() + "/" + channel.getName() + ".txt");
    }
}
