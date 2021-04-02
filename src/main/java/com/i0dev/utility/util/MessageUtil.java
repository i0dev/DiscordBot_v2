package main.java.com.i0dev.utility.util;

import main.java.com.i0dev.utility.GlobalConfig;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class MessageUtil {

    public static void sendMessage(TextChannel channel, MessageEmbed message) {
        if (channel == null) {
            System.out.println("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");
            return;

        }
        channel.sendMessage(message).queue();
    }

    public static void sendMessage(Long ID, MessageEmbed message) {
        if (GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(ID) == null) {
            System.out.println("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");

            return;
        }
        GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(ID).sendMessage(message).queue();
    }

    public static void sendMessage(String ID, MessageEmbed message) {
        if (GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(ID) == null) {
            System.out.println("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");

            return;
        }
        GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(ID).sendMessage(message).queue();
    }


    public static Message sendMessageComplete(TextChannel channel, MessageEmbed message) {
        if (channel == null) {
            System.out.println("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");

            return null;
        }
        return channel.sendMessage(message).complete();
    }

    public static Message sendMessageComplete(Long ID, MessageEmbed message) {
        if (GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(ID) == null) {
            System.out.println("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");

            return null;

        }
        return GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(ID).sendMessage(message).complete();
    }

    public static Message sendMessageComplete(String ID, MessageEmbed message) {
        if (GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(ID) == null) {
            System.out.println("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");

            return null;
        }
        return GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(ID).sendMessage(message).complete();
    }
}
