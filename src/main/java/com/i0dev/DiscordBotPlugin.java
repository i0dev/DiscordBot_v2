package main.java.com.i0dev;

import main.java.com.i0dev.engine.TaskCheckActiveGiveaways;
import main.java.com.i0dev.engine.TaskCreatorTimeouts;
import main.java.com.i0dev.jframe.DiscordBotGUI;
import main.java.com.i0dev.util.conf;
import main.java.com.i0dev.util.getConfig;
import main.java.com.i0dev.util.initJDA;
import main.java.com.i0dev.util.inviteutil.InviteTracking;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordBotPlugin extends JavaPlugin {
    private static DiscordBotPlugin DiscordBotPlugin;
    private static boolean GUIenabled = false;

    public static DiscordBotPlugin get() {
        return DiscordBotPlugin;
    }

    public File getFile(String name) {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        File file = new File(getDataFolder(), name);
        try {
            if (!file.exists()) saveResource(name, false);
            return file;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        DiscordBotPlugin = this;

        getFile("storage/tickets");
        getFile("storage/tickets/logs");
        getFile("storage/tickets/logs");
        getFile("storage/tickets/currentTicketCount.txt");


        try {
            String CurrentTicketNumber = "0";
            Files.write(Paths.get(new File(getDataFolder(), "/storage/tickets/currentTicketCount.txt").getPath()),
                    CurrentTicketNumber.getBytes());
        } catch (IOException ignored) {
        }


        getConfig.get().getFile(main.java.com.i0dev.entity.Application.get().getFilePath());
        getConfig.get().getFile(main.java.com.i0dev.entity.Blacklist.get().getFilePath());
        getConfig.get().getFile(main.java.com.i0dev.entity.Giveaway.get().getFilePath());
        getConfig.get().getFile(main.java.com.i0dev.entity.Screenshare.get().getFilePath());
        getConfig.get().getFile(main.java.com.i0dev.entity.Warning.get().getFilePath());
        getConfig.get().getFile(getConfig.get().getFilePath());
        getConfig.get().getFile(main.java.com.i0dev.entity.Ticket.get().getFilePath());
        getConfig.get().getFile(main.java.com.i0dev.entity.Invites.get().getFilePath());
        getConfig.get().getFile(main.java.com.i0dev.entity.InviteMatcher.get().getFilePath());
        getConfig.get().getFile(main.java.com.i0dev.entity.ReactionRoles.get().getFilePath());

        Timer createJDATimer = new Timer();
        createJDATimer.schedule(createJDALater, 1000);
        Timer verify = new Timer();
        verify.schedule(verifyInitial, 4000);
        if (GUIenabled) {
            try {
                DiscordBotGUI.openGUI();
                DiscordBotGUI.jLabel7.setText("LOADING");
            } catch (Exception ignored) {
            }
        }
    }

    public static TimerTask createJDALater = new TimerTask() {
        public void run() {
            getConfig.get().reloadConfig();
            try {
                initJDA.get().createJDA();
            } catch (Exception ignored) {

            }
            if (initJDA.get().getJda() == null) {
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nConfiguration generated. Please enter your token in the config file!\n\n\n\n\n\n\n\n\n\n\n");
                System.exit(0);
            }
            main.java.com.i0dev.entity.Application.get().loadApplications();
            main.java.com.i0dev.entity.Blacklist.get().loadBlacklist();
            main.java.com.i0dev.entity.Warning.get().loadWarnings();
            main.java.com.i0dev.entity.Giveaway.get().loadGiveaways();
            main.java.com.i0dev.entity.Screenshare.get().loadScreenshare();
            main.java.com.i0dev.entity.Ticket.get().loadTickets();
            main.java.com.i0dev.entity.Invites.get().loadCacheFromFile();
            main.java.com.i0dev.entity.ReactionRoles.get().loadObject();
            main.java.com.i0dev.entity.InviteMatcher.get().loadCacheFromFile();
            if (GUIenabled) {
                try {
                    DiscordBotGUI.jLabel7.setText("Almost Done");
                } catch (Exception ignored) {
                }
            }
            Timer TaskTimer = new Timer();
            TaskTimer.scheduleAtFixedRate(TaskCreatorTimeouts.get().TaskPollTimeout, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskCreatorTimeouts.get().TaskGiveawayTimeout, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskCheckActiveGiveaways.get().TaskGiveawayTimeout, 5000, 10000);
            TaskTimer.schedule(runStartupLater, 1000);


        }
    };

    public static TimerTask runStartupLater = new TimerTask() {
        public void run() {
            conf.initGlobalConfig();
            initJDA.get().registerListeners();
            if (GUIenabled) {

                try {
                    DiscordBotGUI.jLabel7.setText("<html>Bot Prefix: \"" + conf.GENERAL_BOT_PREFIX
                            + "\"<br/>" + "Color Hex: \"" + conf.EMBED_COLOR_HEX_CODE
                            + "\"<br/>" + "Guild ID: \"" + conf.GENERAL_MAIN_GUILD.getId()
                            + "\"<br/>" + "Guild Name: \"" + conf.GENERAL_MAIN_GUILD.getName()
                            + "\"<br/>" + "Bot Activity: \"" + getConfig.get().getString("general.activity")
                            + "\"</html>"

                    );
                } catch (Exception ignored) {
                }
            }
            InviteTracking.attemptInviteCaching(conf.GENERAL_MAIN_GUILD);
            System.out.println("Successfully loaded DiscordBot");
        }
    };

    public static TimerTask verifyInitial = new TimerTask() {
        public void run() {
            try {
                if (initJDA.get().getJda().getGuildById("773035795023790131") == null) {
                    System.out.println("Failed to verify with authentication servers.");
                    initJDA.get().getJda().shutdownNow();
                    try {
                        System.exit(0);
                    } catch (Exception ignored) {

                    }
                } else {
                    System.out.println("Successfully verified with authentication servers.");
                }
            } catch (Exception ignored) {
                System.out.println("Failed to verify with authentication servers.");
                initJDA.get().getJda().shutdownNow();
                try {
                    System.exit(0);
                } catch (Exception ignored1) {

                }
            }
        }

    };


    @Override
    public void onDisable() {
        initJDA.get().getJda().shutdownNow();
    }
}