package main.java.com.i0dev;

import main.java.com.i0dev.command.polls.PollCache;
import main.java.com.i0dev.entity.*;
import main.java.com.i0dev.util.inviteutil.InviteTracking;
import main.java.com.i0dev.jframe.DiscordBotGUI;
import main.java.com.i0dev.util.getConfig;
import main.java.com.i0dev.util.initJDA;
import main.java.com.i0dev.util.conf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordBot {

    public static void main(String[] args) throws IOException {

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
            new File("DiscordBot/storage/tickets/currentTicketCount.txt").createNewFile();
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

        Timer createJDATimer = new Timer();
        createJDATimer.schedule(createJDALater, 1000);
        Timer verify = new Timer();
        verify.schedule(verifyInitial, 4000);
        try {
            DiscordBotGUI.openGUI();
            DiscordBotGUI.jLabel7.setText("LOADING");
        } catch (Exception ignored) {

        }

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
            InviteMatcher.get().loadCacheFromFile();
            try {
                DiscordBotGUI.jLabel7.setText("Almost Done");
            } catch (Exception ignored) {

            }
            Timer startPollCreatorTimeout = new Timer();
            startPollCreatorTimeout.scheduleAtFixedRate(PollCache.get().TaskCheckTimeouts, 50000, 10000);


            Timer runStartupLaterTimer = new Timer();
            runStartupLaterTimer.schedule(runStartupLater, 1000);
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
                System.exit(0);
            } else {
                System.out.println("Successfully verified with authentication servers.");
            }
        }
    };

}
