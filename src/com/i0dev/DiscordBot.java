package com.i0dev;

import com.i0dev.command.minecraft.CommandManager;
import com.i0dev.utility.InternalJDA;
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
        InitilizeBot.pluginMode = true;
        InitilizeBot.get().startUp();
        getCommand("DiscordBot").setExecutor(new CommandManager());
    }

    @Override
    public void onDisable() {
        InternalJDA.get().getJda().shutdownNow();
    }

    public File getDataFolderFromPlugin() {
        return getDataFolder();
    }


}
