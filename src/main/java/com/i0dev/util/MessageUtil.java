package main.java.com.i0dev.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class MessageUtil {

    public static void sendMessage(TextChannel channel, MessageEmbed message) {
        if (channel == null) {
            try {
                throw new i0devException("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");
            } catch (i0devException ignored) {
            }
            return;

        }
        channel.sendMessage(message).queue();
    }

    public static void sendMessage(Long ID, MessageEmbed message) {
        if (conf.GENERAL_MAIN_GUILD.getTextChannelById(ID) == null) {
            try {
                throw new i0devException("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");
            } catch (i0devException ignored) {
            }
            return;
        }
        conf.GENERAL_MAIN_GUILD.getTextChannelById(ID).sendMessage(message).queue();
    }

    public static void sendMessage(String ID, MessageEmbed message) {
        if (conf.GENERAL_MAIN_GUILD.getTextChannelById(ID) == null) {
            try {
                throw new i0devException("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");
            } catch (i0devException ignored) {
            }
            return;
        }
        conf.GENERAL_MAIN_GUILD.getTextChannelById(ID).sendMessage(message).queue();
    }


    public static Message sendMessageComplete(TextChannel channel, MessageEmbed message) {
        if (channel == null) {
            try {
                throw new i0devException("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");
            } catch (i0devException ignored) {
            }
            return null;
        }
        return channel.sendMessage(message).complete();
    }

    public static Message sendMessageComplete(Long ID, MessageEmbed message) {
        if (conf.GENERAL_MAIN_GUILD.getTextChannelById(ID) == null) {
            try {
                throw new i0devException("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");
            } catch (i0devException ignored) {
            }
            return null;

        }
        return conf.GENERAL_MAIN_GUILD.getTextChannelById(ID).sendMessage(message).complete();
    }

    public static Message sendMessageComplete(String ID, MessageEmbed message) {
        if (conf.GENERAL_MAIN_GUILD.getTextChannelById(ID) == null) {
            try {
                throw new i0devException("ERROR: There was no channel set in config to send this message. Please make sure the ID is correct!");
            } catch (i0devException ignored) {
            }
            return null;
        }
        return conf.GENERAL_MAIN_GUILD.getTextChannelById(ID).sendMessage(message).complete();
    }
}
