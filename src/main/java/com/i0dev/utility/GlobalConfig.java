package main.java.com.i0dev.utility;

import main.java.com.i0dev.InitilizeBot;
import net.dv8tion.jda.api.entities.Guild;

public class GlobalConfig {

    public static String MESSAGE_USER_NOT_FOUND;
    public static String MESSAGE_ROLE_NOT_FOUND;
    public static String MESSAGE_CHANNEL_NOT_FOUND;
    public static String MESSAGE_COMMAND_NO_PERMISSION;
    public static String MESSAGE_COMMAND_NOT_ENABLED;
    public static Guild GENERAL_MAIN_GUILD;
    public static String GENERAL_MAIN_LOGS_CHANNEL;
    public static String APPLICATIONS_CHANNEL;
    public static String EMBED_COLOR_HEX_CODE;
    public static String EMBED_THUMBNAIL;
    public static String EMBED_TITLE;
    public static String EMBED_FOOTER;
    public static String TEBEX_SECRET;
    public static String GENERAL_BOT_PREFIX;
    public static boolean GENERAL_DELETE_COMMAND;


    public static void initGlobalConfig() {
        try {
            GENERAL_MAIN_GUILD = InternalJDA.get().getJda().getGuildById(getConfig.get().getLong("general.guildID"));
        } catch (Exception ignored) {
            System.out.println("The guild ID in the GENERAL section of config is invalid!");
            if (!InitilizeBot.get().isPluginMode()) {
                System.exit(0);
            }
        }

        MESSAGE_COMMAND_NOT_ENABLED = getConfig.get().getString("messages.commandNotEnabled");
        MESSAGE_COMMAND_NO_PERMISSION = getConfig.get().getString("messages.commandNoPermission");
        MESSAGE_USER_NOT_FOUND = getConfig.get().getString("messages.userNotFound");
        MESSAGE_CHANNEL_NOT_FOUND = getConfig.get().getString("messages.channelNotFound");
        EMBED_COLOR_HEX_CODE = getConfig.get().getString("messages.embeds.ColorHexCode");
        EMBED_THUMBNAIL = getConfig.get().getString("messages.embeds.Thumbnail");
        EMBED_TITLE = getConfig.get().getString("messages.embeds.Title");
        EMBED_FOOTER = getConfig.get().getString("messages.embeds.Footer");
        MESSAGE_ROLE_NOT_FOUND = getConfig.get().getString("messages.roleNotFound");
        GENERAL_BOT_PREFIX = getConfig.get().getString("general.prefix");
        GENERAL_DELETE_COMMAND = getConfig.get().getBoolean("general.deleteCommand");
        TEBEX_SECRET = getConfig.get().getString("general.tebexSecret");
        GENERAL_MAIN_LOGS_CHANNEL = getConfig.get().getLong("channels.logsChannelID").toString();
        APPLICATIONS_CHANNEL = getConfig.get().getLong("channels.incomingApplicationsChannel").toString();
    }
}
