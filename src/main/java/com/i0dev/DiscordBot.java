package com.i0dev;

import com.i0dev.commands.discord.completedModules.linking.CommandLink;
import com.i0dev.commands.discord.completedModules.linking.RoleRefreshHandler;
import com.i0dev.modules.MinecraftCommandManager;
import com.i0dev.modules.other.FreezeLogHandler;
import com.i0dev.modules.other.InGameChatFormatter;
import com.i0dev.modules.other.LitebansNotifications;
import com.i0dev.modules.points.ingame.PointsManager;
import com.i0dev.modules.points.ingame.ShopEvents;
import com.i0dev.modules.twoFactor.Command2fa;
import com.i0dev.modules.twoFactor.TwoFactorAuthentication;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.Lag;
import com.i0dev.utility.PapiPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DiscordBot extends JavaPlugin {
    private static DiscordBot DiscordBotPlugin;

    public static DiscordBot get() {
        return DiscordBotPlugin;
    }

    @Override
    public void onEnable() {
        DiscordBotPlugin = this;
        InitializeBot.pluginMode = true;
        InitializeBot.get().startUp();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (getServer().getPluginManager().isPluginEnabled("litebans")) {
                LitebansNotifications.registerEvents();
            }
            getCommand("DiscordBot").setExecutor(new MinecraftCommandManager());
            getCommand("Link").setExecutor(new CommandLink());
            getCommand("2fa").setExecutor(new Command2fa());
            getCommand("points").setExecutor(new PointsManager());
            getServer().getPluginManager().registerEvents(new TwoFactorAuthentication(), this);
            getServer().getPluginManager().registerEvents(new ShopEvents(), this);
            getServer().getPluginManager().registerEvents(new InGameChatFormatter(), this);
            getServer().getPluginManager().registerEvents(new FreezeLogHandler(), this);
            getServer().getPluginManager().registerEvents(new RoleRefreshHandler(), this);
        }, 200L);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PapiPlaceholders().register();
        }
        System.out.println("Enabled DiscordBot");
    }

    @Override
    public void onDisable() {
        if (InternalJDA.getJda() != null) {
            InternalJDA.getJda().shutdownNow();
        }
        Bukkit.getScheduler().cancelTasks(this);
        DiscordBotPlugin = null;
        InitializeBot.getAsyncService().shutdown();
        DiscordBotPlugin = null;
        System.out.println("Disabled DiscordBot");

    }

    public void runCommand(CommandSender commandSender, String command) {
        Bukkit.dispatchCommand(commandSender, command);
    }

    public File getDataFolderFromPlugin() {
        return getDataFolder();
    }

}
