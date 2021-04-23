package com.i0dev.utility.util;

import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.Placeholders;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.List;

public class MessageUtil {

    public static void sendMessage(Long channelID, String message) {
        sendMessage(channelID, message, null, null, null);
    }

    public static void sendMessage(Long channelID, String message, User author) {
        sendMessage(channelID, message, null, author, null);
    }

    public static void sendMessage(Long channelID, String message, User author, User mentioned) {
        sendMessage(channelID, message, null, author, mentioned);
    }

    public static Message sendMessage(Long channelID, String message, String title, User author, User mentioned) {
        MessageChannel messageChannel = InternalJDA.get().getJda().getTextChannelById(channelID);
        return messageChannel.sendMessage(
                EmbedFactory.createEmbed(
                        Placeholders.convert(title, mentioned, author),
                        Placeholders.convert(message, mentioned, author)).build())
                .complete();

    }

    public static Message sendMessage(Long channelID, MessageEmbed messageEmbed) {
        MessageChannel messageChannel = InternalJDA.get().getJda().getTextChannelById(channelID);
        return messageChannel.sendMessage(messageEmbed).complete();
    }

    public static void sendMessagePrivateChannel(Long userID, String message, String title, User author, User mentioned) {
        PrivateChannel messageChannel = InternalJDA.get().getJda().getPrivateChannelById(userID);
        messageChannel.sendMessage(
                EmbedFactory.createEmbed(Placeholders.convert(title, mentioned, author), Placeholders.convert(message, mentioned, author)).build())
                .queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_CHANNEL));
    }


    public static void sendMessageIngame(org.bukkit.entity.Player player, String message) {
        try {
            player.sendMessage(FormatUtil.c(message));
        } catch (Exception ignored) {

        }
    }

    public static void sendMessageIngame(org.bukkit.entity.Player player, List<String> messages) {
        try {
            for (String message : messages) {
                player.sendMessage(FormatUtil.c(message));
            }
        } catch (Exception ignored) {

        }
    }

}
