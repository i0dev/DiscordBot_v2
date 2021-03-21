package main.java.com.i0dev;

import main.java.com.i0dev.engine.TaskCheckActiveGiveaways;
import main.java.com.i0dev.engine.TaskCreatorTimeouts;
import main.java.com.i0dev.engine.TaskMemberCount;
import main.java.com.i0dev.jframe.DiscordBotGUI;
import main.java.com.i0dev.util.*;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordBot {

    private static boolean GUIenabled = false;
    private static boolean firstTimeConfig = false;

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
        if (!new File("DiscordBot/Config.json").exists()) {
            firstTimeConfig = true;
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
        verify.schedule(verifyInitial, 9000);
        if (GUIenabled) {
            DiscordBotGUI.openGUI();
        }
    }

    public static TimerTask createJDALater = new TimerTask() {
        public void run() {
            getConfig.get().reloadConfig();
            if (!firstTimeConfig) {
                try {
                    getConfig.get().putDefaultsIfAbsent();
                } catch (ParseException | IOException ignored) {
                }
            }
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
            main.java.com.i0dev.entity.Warning.get().loadCacheFromFile();
            main.java.com.i0dev.entity.Giveaway.get().loadGiveaways();
            main.java.com.i0dev.entity.Screenshare.get().loadScreenshare();
            main.java.com.i0dev.entity.Ticket.get().loadTickets();
            main.java.com.i0dev.entity.Invites.get().loadCacheFromFile();
            main.java.com.i0dev.entity.ReactionRoles.get().loadObject();
            main.java.com.i0dev.entity.InviteMatcher.get().loadCacheFromFile();
            Timer TaskTimer = new Timer();
            TaskTimer.scheduleAtFixedRate(TaskCreatorTimeouts.get().TaskPollTimeout, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskCreatorTimeouts.get().TaskGiveawayTimeout, 50000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskCheckActiveGiveaways.get().TaskGiveawayTimeout, 5000, 10000);
            TaskTimer.scheduleAtFixedRate(TaskMemberCount.MemberCountTimer, 5000, 30000);
            TaskTimer.schedule(runStartupLater, 1000);


        }
    };

    public static TimerTask runStartupLater = new TimerTask() {
        public void run() {
            conf.initGlobalConfig();
            initJDA.get().registerListeners();
            if (GUIenabled) {
                DiscordBotGUI.jLabel7.setText("<html>Bot Prefix: \"" + conf.GENERAL_BOT_PREFIX
                        + "\"<br/>" + "Color Hex: \"" + conf.EMBED_COLOR_HEX_CODE
                        + "\"<br/>" + "Guild ID: \"" + conf.GENERAL_MAIN_GUILD.getId()
                        + "\"<br/>" + "Guild Name: \"" + conf.GENERAL_MAIN_GUILD.getName()
                        + "\"<br/>" + "Bot Activity: \"" + getConfig.get().getString("general.activity")
                        + "\"</html>"

                );
            }
            InviteTracking.attemptInviteCaching(conf.GENERAL_MAIN_GUILD);
            System.out.println("Successfully loaded DiscordBot");
        }
    };

    public static TimerTask verifyInitial = new TimerTask() {
        public void run() {
            try {
                if (initJDA.get().getJda().getGuildById("773035795023790131") == null) {
                    System.exit(0);
                    throw new i0devException("Failed to verify with authentication servers. Please make sure your token is correct, and you have the bot verified.");

                } else {
                    System.out.println("Successfully verified with authentication servers.");
                }
            } catch (Exception ignored) {
                try {
                    System.exit(0);
                    throw new i0devException("Failed to verify with authentication servers. Please make sure your token is correct, and you have the bot verified.");
                } catch (i0devException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
