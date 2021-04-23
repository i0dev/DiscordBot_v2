package com.i0dev;

import com.i0dev.engine.discord.RoleQueue;
import com.i0dev.engine.discord.TaskCheckActiveGiveaways;
import com.i0dev.engine.discord.TaskCreatorTimeouts;
import com.i0dev.engine.discord.TaskMemberCount;
import com.i0dev.engine.minecraft.UpdateCaches;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.*;
import com.i0dev.object.objects.ReactionRoles;
import com.i0dev.pointSystem.EventHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FileUtil;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class InitilizeBot {

    @Getter
    public static boolean pluginMode = false;


    private static InitilizeBot instance = new InitilizeBot();

    public static InitilizeBot get() {
        return instance;
    }

    public static void main(String[] args) {
        get().startUp();
    }


    public void startUp() {
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
                        "0".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Timer TaskTimer = new Timer();
        TaskTimer.schedule(createJDA, 1000);
        TaskTimer.schedule(verifyInitial, 9000);

    }

    public static TimerTask createJDA = new TimerTask() {
        public void run() {
            Configuration.reloadConfig();

            try {
                InternalJDA.get().createJDA();
            } catch (Exception ignored) {
            }
            if (InternalJDA.get().getJda() == null) {
                System.out.println("\n\n\n\n\n\nConfiguration generated. Please enter your token in the config file!\n\n\n\n\n");
                if (!pluginMode) {
                    System.exit(0);
                }
            }


            ReactionRoles.get().loadObject();

            ApplicationEngine.getInstance().load(FileUtil.getJsonArray(get().getApplicationsPath()));
            GiveawayEngine.getInstance().load(FileUtil.getJsonArray(get().getGiveawaysPath()));
            ScreenshareEngine.getInstance().load(FileUtil.getJsonArray(get().getScreensharePath()));
            TicketEngine.getInstance().load(FileUtil.getJsonArray(get().getTicketsPath()));
            SuggestionEngine.getInstance().load(FileUtil.getJsonArray(get().getSuggestionPath()));

            DPlayerEngine.getInstance().load();


            Timer TaskTimer = new Timer();
            TaskTimer.scheduleAtFixedRate(RoleQueue.applyRoles, 5000, 2000);

            TaskTimer.scheduleAtFixedRate(TaskCreatorTimeouts.TaskPollTimeout, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskMemberCount.MemberCountTimer, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskCreatorTimeouts.TaskGiveawayTimeout, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskCheckActiveGiveaways.get().TaskGiveawayTimeout, 5000, 10000);
            TaskTimer.scheduleAtFixedRate(UpdateCaches.taskUpdateCacheData, 5000, 3600000);
            TaskTimer.scheduleAtFixedRate(DPlayerEngine.getInstance().taskUpdateUsers, 5000, 30000);
            TaskTimer.scheduleAtFixedRate(EventHandler.taskCheckVoice, 15000, 45000);
            LogsFile.initPoints();

            TaskTimer.schedule(runStartupLater, 1000);

        }
    };


    public static TimerTask runStartupLater = new TimerTask() {
        public void run() {
            GlobalConfig.initGlobalConfig();
            InternalJDA.get().registerListeners();
            InviteTracking.attemptInviteCaching(GlobalConfig.GENERAL_MAIN_GUILD);

            try {
                String activity = Placeholders.convert(GlobalConfig.DISCORD_ACTIVITY);
                switch (GlobalConfig.DISCORD_ACTIVITY_TYPE.toLowerCase()) {
                    case "watching":
                        InternalJDA.get().getJda().getPresence().setActivity(Activity.watching(activity));
                        break;
                    case "listening":
                        InternalJDA.get().getJda().getPresence().setActivity(Activity.listening(activity));
                        break;
                    case "playing":
                        InternalJDA.get().getJda().getPresence().setActivity(Activity.playing(activity));
                        break;
                }
            } catch (Exception ignored) {

            }

            System.out.println("Successfully loaded DiscordBot");

        }
    };

    public static TimerTask verifyInitial = new TimerTask() {
        public void run() {
            try {
                if (InternalJDA.get().getJda().getGuildById("773035795023790131") == null) {
                    System.out.println("Failed to verify with authentication servers.");
                    InternalJDA.get().getJda().shutdownNow();
                    try {
                        if (!InitilizeBot.get().isPluginMode()) {
                            System.exit(0);
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    System.out.println("Successfully verified with authentication servers.");
                }
            } catch (Exception ignored) {
                System.out.println("Failed to verify with authentication servers.");
                InternalJDA.get().getJda().shutdownNow();
                try {
                    if (!InitilizeBot.get().isPluginMode()) {
                        System.exit(0);
                    }
                } catch (Exception ignored1) {

                }
            }
        }

    };

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
