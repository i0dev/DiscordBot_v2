package com.i0dev.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class FindFromString {

    private final static FindFromString findUser = new FindFromString();

    public static FindFromString get() {
        return findUser;
    }

    public User getUser(String arg, Message message) {
        try {
            return message.getMentionedUsers().get(0);
        } catch (Exception ignored) {
        }
        try {
            Long.parseLong(arg);
            return InternalJDA.get().getJda().getUserById(Long.parseLong(arg));
        } catch (Exception ignored) {
        }
        try {
            return InternalJDA.get().getJda().getUserByTag(arg);

        } catch (Exception ignored) {
        }
        return null;
    }

    public Role getRole(String arg, Message message) {
        try {
            return message.getMentionedRoles().get(0);
        } catch (Exception ignored) {
        }
        try {
            Long.parseLong(arg);
            return GlobalConfig.GENERAL_MAIN_GUILD.getRoleById(Long.parseLong(arg));
        } catch (Exception ignored) {
        }
        try {
            return GlobalConfig.GENERAL_MAIN_GUILD.getRolesByName(arg, false).get(0);
        } catch (Exception ignored) {
        }
        try {
            return GlobalConfig.GENERAL_MAIN_GUILD.getRolesByName(arg, true).get(0);
        } catch (Exception ignored) {
        }
        return null;
    }

    public TextChannel getTextChannel(String arg, Message message) {
        try {
            return message.getMentionedChannels().get(0);
        } catch (Exception ignored) {
        }
        try {
            Long.parseLong(arg);
            return GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(Long.parseLong(arg));
        } catch (Exception ignored) {
        }
        try {
            return GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelsByName(arg, false).get(0);
        } catch (Exception ignored) {
        }
        try {
            return GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelsByName(arg, true).get(0);
        } catch (Exception ignored) {
        }
        return null;
    }

    public Message getMessage(String arg, TextChannel channel) {

        try {
            Long.parseLong(arg);
        } catch (Exception exception) {
            return null;
        }

        return channel.retrieveMessageById(arg).complete();


    }
}
