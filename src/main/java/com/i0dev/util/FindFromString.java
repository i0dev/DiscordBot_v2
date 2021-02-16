package main.java.com.i0dev.util;

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
            return initJDA.get().getJda().getUserById(Long.parseLong(arg));
        } catch (Exception ignored) {
        }
        try {
            return initJDA.get().getJda().getUserByTag(arg);

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
            return conf.GENERAL_MAIN_GUILD.getRoleById(Long.parseLong(arg));
        } catch (Exception ignored) {
        }
        try {
            return conf.GENERAL_MAIN_GUILD.getRolesByName(arg, false).get(0);
        } catch (Exception ignored) {
        }
        try {
            return conf.GENERAL_MAIN_GUILD.getRolesByName(arg, true).get(0);
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
            return conf.GENERAL_MAIN_GUILD.getTextChannelById(Long.parseLong(arg));
        } catch (Exception ignored) {
        }
        try {
            return conf.GENERAL_MAIN_GUILD.getTextChannelsByName(arg, false).get(0);
        } catch (Exception ignored) {
        }
        try {
            return conf.GENERAL_MAIN_GUILD.getTextChannelsByName(arg, true).get(0);
        } catch (Exception ignored) {
        }
        return null;
    }
}
