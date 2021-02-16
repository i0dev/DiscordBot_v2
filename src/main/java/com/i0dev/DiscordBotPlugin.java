package com.i0dev;

import com.i0dev.entity.*;
import com.i0dev.util.conf;
import com.i0dev.util.getConfig;
import com.i0dev.util.initJDA;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordBotPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
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

        Timer createJDATimer = new Timer();
        createJDATimer.schedule(createJDALater, 1000);

        System.out.println("Started loaded DiscordBot. Please wait 4 seconds.");
    }

    public static TimerTask createJDALater = new TimerTask() {
        public void run() {
            getConfig.get().reloadConfig();
            initJDA.get().createJDA();

            Application.get().loadApplications();
            Blacklist.get().loadBlacklist();
            Warning.get().loadWarnings();
            Giveaway.get().loadGiveaways();
            Screenshare.get().loadScreenshare();
            Ticket.get().loadTickets();

            Timer runStartupLaterTimer = new Timer();
            runStartupLaterTimer.schedule(runStartupLater, 3000);
        }
    };

    public static TimerTask runStartupLater = new TimerTask() {
        public void run() {
            conf.initGlobalConfig();
            initJDA.get().registerListeners();

            System.out.println("Successfully loaded DiscordBot");
        }
    };

    @Override
    public void onDisable() {
        super.onEnable();
    }
}
