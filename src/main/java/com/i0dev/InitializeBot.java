package com.i0dev;

import com.i0dev.modules.boosting.BoostHandler;
import com.i0dev.modules.invite.InviteTracking;
import com.i0dev.modules.points.ingame.Shop;
import com.i0dev.modules.ticket.TicketCreateHandler;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.*;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.object.objects.ReactionRoles;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.SQLManager;
import com.i0dev.utility.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class InitializeBot {

    @Getter
    @Setter
    public static boolean pluginMode = false;

    private static final InitializeBot instance = new InitializeBot();

    public static InitializeBot get() {
        return instance;
    }

    public static void main(String[] args) {
        get().startUp();
    }

    @Getter
    public static ScheduledExecutorService asyncService = Executors.newScheduledThreadPool(Math.max(Runtime.getRuntime().availableProcessors() / 2, 8));

    public void startUp() {
        try {
            FileUtil.createDirectory(getStorageDirPath());
            FileUtil.createDirectory(getTicketsDirPath());
            FileUtil.createDirectory(getTicketLogsDirPath());
            FileUtil.createDirectory(getDPlayerDir());

            FileUtil.createFile(getConfigPath(), "Config.json");
            FileUtil.createFile(getReactionRolesPath(), "StorageFiles/ReactionRoles.json");
            FileUtil.createFile(getApplicationsPath(), "");
            FileUtil.createFile(getGiveawaysPath(), "");
            FileUtil.createFile(getScreensharePath(), "");
            FileUtil.createFile(getTicketsPath(), "");
            FileUtil.createFile(getSuggestionPath(), "");
            FileUtil.createFile(getPointLogPath());

            if (!new File(getTicketCountPath()).exists()) {
                FileUtil.createFile(getTicketCountPath());
                try {
                    Files.write(Paths.get(getTicketCountPath()),
                            "1".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Timer taskTimer = new Timer();
            taskTimer.schedule(createJDA, 1000);
            taskTimer.schedule(runStartupLater, 3000);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    static TimerTask createJDA = new TimerTask() {
        @Override
        public void run() {
            try {
                Configuration.reloadConfig();

                try {
                    InternalJDA.createJDA();
                } catch (Exception ignored) {
                    System.out.println("\n\nCould not load previous settings. If this is the first time you launched the bot, it will generate a folder with config and storage.\n");
                    return;
                }

                ReactionRoles.get().loadObject();

                ApplicationEngine.getInstance().load(FileUtil.getJsonArray(get().getApplicationsPath()));
                GiveawayEngine.getInstance().load(FileUtil.getJsonArray(get().getGiveawaysPath()));
                ScreenshareEngine.getInstance().load(FileUtil.getJsonArray(get().getScreensharePath()));
                TicketEngine.getInstance().load(FileUtil.getJsonArray(get().getTicketsPath()));
                SuggestionEngine.getInstance().load(FileUtil.getJsonArray(get().getSuggestionPath()));


            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    };

    static TimerTask runStartupLater = new TimerTask() {
        @Override
        public void run() {
            try {
                GlobalConfig.initGlobalConfig();

                if (GlobalConfig.USING_DATABASE) {
                    SQLManager.init();
                    SQLManager.connect();
                    SQLManager.migrateData();
                    SQLManager.absenceCheck(DPlayer.class);

                }
                DPlayerEngine.load();

                TicketCreateHandler.init();
                InviteTracking.attemptInviteCaching(GlobalConfig.GENERAL_MAIN_GUILD);
                BoostHandler.setBoostCountCache(GlobalConfig.GENERAL_MAIN_GUILD.getBoostCount());

                initializeCommands();
                InternalJDA.registerListeners();
                Engine.run();
                if (isPluginMode()) {
                    Shop.loadInventory();
                }


                System.out.println("Successfully loaded DiscordBot");
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    };

    public static void initializeCommands() {
        Reflections reflections = new Reflections("com.i0dev");
        int count = 0;
        for (Class<? extends DiscordCommand> command : reflections.getSubTypesOf(DiscordCommand.class)) {
            try {
                if (!isPluginMode()) {
                    if (command.getName().equals("com.i0dev.modules.points.discord.Buy")) continue;
                    if (command.getName().equals("com.i0dev.modules.basic.CommandRunCommand")) continue;
                    if (command.getName().equals("com.i0dev.modules.basic.CommandReclaim")) continue;
                    if (command.getName().equals("com.i0dev.modules.basic.CommandReclaimReset")) continue;
                    if (command.getName().equals("com.i0dev.modules.boosting.Claim")) continue;
                }
                // System.out.println(command.getName());
                Method init = command.getMethod("init");
                init.invoke(command.newInstance());
                count++;
            } catch
            (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Registered [" + count + "] total commands.");
    }

    private static File getDataFolder() {
        return pluginMode ? DiscordBot.get().getDataFolderFromPlugin() : null;
    }

    public String getApplicationsPath() {
        return pluginMode ? getDataFolder() + "/storage/Applications.json" : "DiscordBot/storage/Applications.json";
    }

    public String getGiveawaysPath() {
        return pluginMode ? getDataFolder() + "/storage/Giveaways.json" : "DiscordBot/storage/Giveaways.json";
    }

    public String getScreensharePath() {
        return pluginMode ? getDataFolder() + "/storage/Screenshare.json" : "DiscordBot/storage/Screenshare.json";
    }

    public String getSuggestionPath() {
        return pluginMode ? getDataFolder() + "/storage/Suggestion.json" : "DiscordBot/storage/Suggestion.json";
    }

    public String getTicketsPath() {
        return pluginMode ? getDataFolder() + "/storage/Tickets.json" : "DiscordBot/storage/Tickets.json";
    }

    public String getReactionRolesPath() {
        return pluginMode ? getDataFolder() + "/storage/ReactionRoles.json" : "DiscordBot/storage/ReactionRoles.json";
    }

    public String getStorageDirPath() {
        return pluginMode ? getDataFolder() + "/storage" : "DiscordBot/storage";
    }

    public String getTicketsDirPath() {
        return pluginMode ? getDataFolder() + "/storage/tickets" : "DiscordBot/storage/tickets";
    }

    public String getTicketLogsDirPath() {
        return pluginMode ? getDataFolder() + "/storage/tickets/logs" : "DiscordBot/storage/tickets/logs";
    }

    public String getDPlayerDir() {
        return pluginMode ? getDataFolder() + "/storage/dPlayerStorage" : "DiscordBot/storage/dPlayerStorage";
    }

    public String getTicketCountPath() {
        return pluginMode ? getDataFolder() + "/storage/tickets/currentTicketCount.txt" : "DiscordBot/storage/tickets/currentTicketCount.txt";
    }

    public String getPointLogPath() {
        return pluginMode ? getDataFolder() + "/storage/logs/pointLogs.txt" : "DiscordBot/storage/logs/pointLogs.txt";
    }

    public String getConfigPath() {
        return pluginMode ? getDataFolder() + "/Config.json" : "DiscordBot/Config.json";
    }
}