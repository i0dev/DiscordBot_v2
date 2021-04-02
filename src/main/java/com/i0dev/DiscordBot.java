package main.java.com.i0dev;

import main.java.com.i0dev.command.minecraft.CmdVersion;
import main.java.com.i0dev.utility.InternalJDA;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Filter;

public class DiscordBot extends JavaPlugin {
    private static DiscordBot DiscordBotPlugin;

    public static DiscordBot get() {
        return DiscordBotPlugin;
    }

    @Override
    public void onEnable() {
        DiscordBotPlugin = this;
        InitilizeBot.pluginMode = true;
        InitilizeBot.get().startUp();
        Filter filter = getLogger().getFilter();
        getCommand("DiscordBot").setExecutor(new CmdVersion());

    }

    @Override
    public void onDisable() {
        InternalJDA.get().getJda().shutdownNow();
    }

    public File getDataFolderFromPlugin() {
        return getDataFolder();
    }


}