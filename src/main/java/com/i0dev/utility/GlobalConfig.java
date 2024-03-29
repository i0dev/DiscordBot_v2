package com.i0dev.utility;

import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

public class GlobalConfig {

    public static String MESSAGE_USER_NOT_FOUND;
    public static String MESSAGE_ROLE_NOT_FOUND;
    public static String MESSAGE_CHANNEL_NOT_FOUND;
    public static String MESSAGE_COMMAND_NO_PERMISSION;
    public static String MESSAGE_COMMAND_NOT_ENABLED;
    public static Guild GENERAL_MAIN_GUILD;
    public static long GENERAL_MAIN_LOGS_CHANNEL;
    public static long APPLICATIONS_CHANNEL;
    public static String EMBED_COLOR_HEX_CODE;
    public static String EMBED_THUMBNAIL;
    public static String EMBED_TITLE;
    public static String EMBED_FOOTER;
    public static String TEBEX_SECRET;
    public static String GENERAL_BOT_PREFIX;
    public static boolean GENERAL_DELETE_COMMAND;
    public static long CHANGELOG_CHANNEL;
    public static String NOT_PLUGIN_MODE;
    public static long MOVEMENT_CHANNEL_ID;
    public static String DISCORD_ACTIVITY;
    public static String DISCORD_ACTIVITY_TYPE;
    public static String NOT_LINKED;
    public static long F_TOP_LOGS_CHANNEL_ID;
    public static boolean USING_DATABASE;
    public static List<Guild> GUILDS = new ArrayList<>();

    public static void initGlobalConfig() {
        try {
            GENERAL_MAIN_GUILD = InternalJDA.getJda().getGuildById(Configuration.getLong("general.guildID"));
        } catch (Exception ignored) {
            System.out.println("The guild ID in the GENERAL section of config is invalid!");
            InternalJDA.getJda().shutdown();
        }

        MESSAGE_COMMAND_NOT_ENABLED = Configuration.getString("messages.commandNotEnabled");
        MESSAGE_COMMAND_NO_PERMISSION = Configuration.getString("messages.commandNoPermission");
        MESSAGE_USER_NOT_FOUND = Configuration.getString("messages.userNotFound");
        MESSAGE_CHANNEL_NOT_FOUND = Configuration.getString("messages.channelNotFound");
        EMBED_COLOR_HEX_CODE = Configuration.getString("messages.embeds.ColorHexCode");
        EMBED_THUMBNAIL = Configuration.getString("messages.embeds.Thumbnail");
        EMBED_TITLE = Configuration.getString("messages.embeds.Title");
        EMBED_FOOTER = Configuration.getString("messages.embeds.Footer");
        MESSAGE_ROLE_NOT_FOUND = Configuration.getString("messages.roleNotFound");
        GENERAL_BOT_PREFIX = Configuration.getString("general.prefix");
        GENERAL_DELETE_COMMAND = Configuration.getBoolean("general.deleteCommand");
        TEBEX_SECRET = Configuration.getString("general.tebexSecret");
        GENERAL_MAIN_LOGS_CHANNEL = Configuration.getLong("channels.logsChannelID");
        APPLICATIONS_CHANNEL = Configuration.getLong("channels.incomingApplicationsChannel");
        CHANGELOG_CHANNEL = Configuration.getLong("channels.changelogChannelID");
        NOT_PLUGIN_MODE = Configuration.getString("messages.botNotInPluginMode");
        MOVEMENT_CHANNEL_ID = Configuration.getLong("channels.staffMovementsChannel");
        DISCORD_ACTIVITY = Configuration.getString("general.activity");
        DISCORD_ACTIVITY_TYPE = Configuration.getString("general.activityType");
        NOT_LINKED = Configuration.getString("messages.notLinked");
        F_TOP_LOGS_CHANNEL_ID = Configuration.getLong("channels.ftopLogsChannelID");
        //USING_DATABASE = Configuration.getBoolean("database.useDatabase");
        USING_DATABASE = false;
        GUILDS.add(GENERAL_MAIN_GUILD);
        Configuration.getLongList("general.secondaryGuildIDs").forEach(aLong -> {
            Guild guild = InternalJDA.getJda().getGuildById(aLong);
            if (guild != null)
                GUILDS.add(guild);
        });
    }
}
