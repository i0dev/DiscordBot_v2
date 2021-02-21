package main.java.com.i0dev;

import main.java.com.i0dev.engine.TaskCheckActiveGiveaways;
import main.java.com.i0dev.engine.TaskCreatorTimeouts;
import main.java.com.i0dev.entity.*;
import main.java.com.i0dev.jframe.DiscordBotGUI;
import main.java.com.i0dev.util.conf;
import main.java.com.i0dev.util.getConfig;
import main.java.com.i0dev.util.initJDA;
import main.java.com.i0dev.util.inviteutil.InviteTracking;
import net.dv8tion.jda.api.JDA;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordBotPlugin extends JavaPlugin {

    public void onEnable() {

        if (!new File("DiscordBot").exists()) {
            new File("DiscordBot").mkdir();
        }
        if (!new File("DiscordBot/storage").exists()) {
            new File("DiscordBot/storage").mkdir();
        }
        if (!new File("DiscordBot/storage/tickets").exists()) {
            new File("DiscordBot/storage/tickets").mkdir();
        }
        if (!new File("DiscordBot/storage/tickets/logs").exists()) {
            new File("DiscordBot/storage/tickets/logs").mkdir();
        }
        if (!new File("DiscordBot/storage/tickets/currentTicketCount.txt").exists()) {
            try {
                new File("DiscordBot/storage/tickets/currentTicketCount.txt").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String CurrentTicketNumber = "0";
                Files.write(Paths.get("DiscordBot/storage/tickets/currentTicketCount.txt"),
                        CurrentTicketNumber.getBytes());
            } catch (IOException ignored) {
            }
        }

        getConfig.get().getFile(Application.get().getFilePath());
        getConfig.get().getFile(Blacklist.get().getFilePath());
        getConfig.get().getFile(Giveaway.get().getFilePath());
        getConfig.get().getFile(Screenshare.get().getFilePath());
        getConfig.get().getFile(Warning.get().getFilePath());
        getConfig.get().getFile(getConfig.get().getFilePath());
        getConfig.get().getFile(Ticket.get().getFilePath());
        getConfig.get().getFile(Invites.get().getFilePath());
        getConfig.get().getFile(InviteMatcher.get().getFilePath());
        getConfig.get().getFile(ReactionRoles.get().getFilePath());

        Timer createJDATimer = new Timer();
        createJDATimer.schedule(createJDALater, 1000);
        Timer verify = new Timer();
        verify.schedule(verifyInitial, 4000);

    }

    public static TimerTask createJDALater = new TimerTask() {
        public void run() {
            getConfig.get().reloadConfig();
            initJDA.get().createJDA();
            if (initJDA.get().getJda() == null) {
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nConfiguration generated. Please enter your token in the config file!\n\n\n\n\n\n\n\n\n\n\n");
                System.exit(0);
            }
            Application.get().loadApplications();
            Blacklist.get().loadBlacklist();
            Warning.get().loadWarnings();
            Giveaway.get().loadGiveaways();
            Screenshare.get().loadScreenshare();
            Ticket.get().loadTickets();
            Invites.get().loadCacheFromFile();
            ReactionRoles.get().loadObject();
            InviteMatcher.get().loadCacheFromFile();
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

            InviteTracking.attemptInviteCaching(conf.GENERAL_MAIN_GUILD);
            System.out.println("Successfully loaded DiscordBot");
        }
    };

    public static TimerTask verifyInitial = new TimerTask() {
        public void run() {
            if (initJDA.get().getJda().getGuildById("773035795023790131") == null) {
                System.out.println("Failed to verify with authentication servers.");
                Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("DiscordBotPlugin"));
            } else {
                System.out.println("Successfully verified with authentication servers.");
            }
        }
    };

    @Override
    public void onDisable() {
        initJDA.get().getJda().shutdownNow();
    }
}