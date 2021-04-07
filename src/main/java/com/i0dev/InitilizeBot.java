package com.i0dev;

import com.i0dev.engine.discord.RoleQueue;
import com.i0dev.engine.discord.TaskCheckActiveGiveaways;
import com.i0dev.engine.discord.TaskCreatorTimeouts;
import com.i0dev.engine.discord.TaskMemberCount;
import com.i0dev.object.*;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.InviteTracking;
import com.i0dev.utility.getConfig;
import com.i0dev.utility.util.FileUtil;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class InitilizeBot {

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
        FileUtil.createFile(getConfigPath(), "Config.json");

        FileUtil.createFile(getApplicationsPath(), "StorageFiles/Applications.json");
        FileUtil.createFile(getBlacklistedPath(), "StorageFiles/Blacklisted.json");
        FileUtil.createFile(getGiveawaysPath(), "StorageFiles/Giveaways.json");
        FileUtil.createFile(getScreensharePath(), "StorageFiles/Screenshare.json");
        FileUtil.createFile(getWarningsPath(), "StorageFiles/Warnings.json");
        FileUtil.createFile(getTicketsPath(), "StorageFiles/Tickets.json");
        FileUtil.createFile(getInvitesPath(), "StorageFiles/Invites.json");
        FileUtil.createFile(getReactionRolesPath(), "StorageFiles/ReactionRoles.json");
        FileUtil.createFile(getInviteMatcherPath(), "StorageFiles/InviteMatcher.json");
        FileUtil.createFile(getTicketTopPath(), "StorageFiles/TicketTop.json");

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
            getConfig.get().reloadConfig();

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
            try {
                getConfig.get().putDefaultsIfAbsent();
            } catch (ParseException | IOException ignored) {
            }

            Application.get().loadApplications();
            Blacklist.get().loadBlacklist();
            Warning.get().loadCacheFromFile();
            Giveaway.get().loadGiveaways();
            Screenshare.get().loadScreenshare();
            Ticket.get().loadTickets();
            Invites.get().loadCacheFromFile();
            ReactionRoles.get().loadObject();
            InviteMatcher.get().loadCacheFromFile();
            TicketTop.get().loadCacheFromFile();

            Timer TaskTimer = new Timer();
            TaskTimer.scheduleAtFixedRate(TaskCreatorTimeouts.TaskPollTimeout, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskMemberCount.MemberCountTimer, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskCreatorTimeouts.TaskGiveawayTimeout, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskCheckActiveGiveaways.get().TaskGiveawayTimeout, 5000, 10000);
            TaskTimer.scheduleAtFixedRate(RoleQueue.applyRoles, 5000, 2000);
            TaskTimer.schedule(runStartupLater, 1000);

        }
    };


    public static TimerTask runStartupLater = new TimerTask() {
        public void run() {
            GlobalConfig.initGlobalConfig();
            InternalJDA.get().registerListeners();
            InviteTracking.attemptInviteCaching(GlobalConfig.GENERAL_MAIN_GUILD);
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

    public String getBlacklistedPath() {
        return pluginMode ? getDataFolder() + "/storage/Blacklisted.json" : "DiscordBot/storage/Blacklisted.json";
    }

    public String getGiveawaysPath() {
        return pluginMode ? getDataFolder() + "/storage/Giveaways.json" : "DiscordBot/storage/Giveaways.json";
    }

    public String getScreensharePath() {
        return pluginMode ? getDataFolder() + "/storage/Screenshare.json" : "DiscordBot/storage/Screenshare.json";
    }

    public String getWarningsPath() {
        return pluginMode ? getDataFolder() + "/storage/Warnings.json" : "DiscordBot/storage/Warnings.json";
    }

    public String getTicketsPath() {
        return pluginMode ? getDataFolder() + "/storage/Tickets.json" : "DiscordBot/storage/Tickets.json";
    }

    public String getInvitesPath() {
        return pluginMode ? getDataFolder() + "/storage/Invites.json" : "DiscordBot/storage/Invites.json";
    }

    public String getInviteMatcherPath() {
        return pluginMode ? getDataFolder() + "/storage/InviteMatcher.json" : "DiscordBot/storage/InviteMatcher.json";
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

    public String getTicketCountPath() {
        return pluginMode ? getDataFolder() + "/storage/tickets/currentTicketCount.txt" : "DiscordBot/storage/tickets/currentTicketCount.txt";
    }

    public String getTicketTopPath() {
        return pluginMode ? getDataFolder() + "/storage/tickets/TicketTop.json" : "DiscordBot/storage/tickets/TicketTop.json";
    }

    public String getConfigPath() {
        return pluginMode ? getDataFolder() + "/Config.json" : "DiscordBot/Config.json";
    }

    public boolean isPluginMode() {
        return pluginMode;
    }

}
