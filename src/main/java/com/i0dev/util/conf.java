package main.java.com.i0dev.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class conf {

    public static String MESSAGE_USER_NOT_FOUND;
    public static String MESSAGE_ROLE_NOT_FOUND;
    public static String MESSAGE_COMMAND_NO_PERMISSION;
    public static String MESSAGE_COMMAND_NOT_ENABLED;
    public static Guild GENERAL_MAIN_GUILD;
    public static TextChannel GENERAL_MAIN_LOGS_CHANNEL;
    public static TextChannel APPLICATIONS_CHANNEL;
    public static String EMBED_COLOR_HEX_CODE;
    public static String EMBED_THUMBNAIL;
    public static String EMBED_TITLE;
    public static String EMBED_FOOTER;
    public static String GENERAL_BOT_PREFIX;
    public static boolean GENERAL_DELETE_COMMAND;


    public static void initGlobalConfig() {
        GENERAL_MAIN_GUILD = initJDA.get().getJda().getGuildById(getConfig.get().getLong("general.guildID"));
        GENERAL_MAIN_LOGS_CHANNEL = initJDA.get().getJda().getGuildById(getConfig.get().getLong("general.guildID")).getTextChannelById(getConfig.get().getLong("channels.logsChannelID"));
        APPLICATIONS_CHANNEL = initJDA.get().getJda().getGuildById(getConfig.get().getLong("general.guildID")).getTextChannelById(getConfig.get().getLong("channels.incomingApplicationsChannel"));
        MESSAGE_COMMAND_NOT_ENABLED = getConfig.get().getString("messages.commandNotEnabled");
        MESSAGE_COMMAND_NO_PERMISSION = getConfig.get().getString("messages.commandNoPermission");
        MESSAGE_USER_NOT_FOUND = getConfig.get().getString("messages.userNotFound");
        EMBED_COLOR_HEX_CODE = getConfig.get().getString("messages.embeds.ColorHexCode");
        EMBED_THUMBNAIL = getConfig.get().getString("messages.embeds.Thumbnail");
        EMBED_TITLE = getConfig.get().getString("messages.embeds.Title");
        EMBED_FOOTER = getConfig.get().getString("messages.embeds.Footer");
        MESSAGE_ROLE_NOT_FOUND = getConfig.get().getString("messages.roleNotFound");
        GENERAL_BOT_PREFIX = getConfig.get().getString("general.prefix");
        GENERAL_DELETE_COMMAND = getConfig.get().getBoolean("general.deleteCommand");
    }
}
