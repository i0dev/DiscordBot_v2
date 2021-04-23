package com.i0dev;

import com.i0dev.commands.MinecraftCommandManager;
import com.i0dev.commands.discord.completedModules.linking.CommandLink;
import com.i0dev.engine.discord.RoleQueue;
import com.i0dev.engine.discord.TaskCheckActiveGiveaways;
import com.i0dev.engine.discord.TaskCreatorTimeouts;
import com.i0dev.engine.discord.TaskMemberCount;
import com.i0dev.engine.minecraft.LitebansNotifications;
import com.i0dev.engine.minecraft.UpdateCaches;
import com.i0dev.engine.minecraft.twofa.Cmd2fa;
import com.i0dev.engine.minecraft.twofa.TwoFactorAuthentication;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.pointSystem.EventHandler;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.Lag;
import org.bukkit.Bukkit;
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
        Bukkit.getScheduler().runTaskLater(this, () -> {
            LitebansNotifications.registerEvents();
            getCommand("DiscordBot").setExecutor(new MinecraftCommandManager());
            getCommand("Link").setExecutor(new CommandLink());
            getCommand("2fa").setExecutor(new Cmd2fa());
            getServer().getPluginManager().registerEvents(new TwoFactorAuthentication(), this);
        }, 200L);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
    }

    @Override
    public void onDisable() {
        if (InternalJDA.get().getJda() != null) {
            InternalJDA.get().getJda().shutdownNow();
        }
        Bukkit.getScheduler().cancelTasks(this);
        DiscordBotPlugin = null;
        TaskCheckActiveGiveaways.get().TaskGiveawayTimeout.cancel();
        RoleQueue.applyRoles.cancel();
        TaskCreatorTimeouts.TaskPollTimeout.cancel();
        TaskMemberCount.MemberCountTimer.cancel();
        TaskCreatorTimeouts.TaskGiveawayTimeout.cancel();
        UpdateCaches.taskUpdateCacheData.cancel();
        DPlayerEngine.getInstance().taskUpdateUsers.cancel();
        EventHandler.taskCheckVoice.cancel();
    }

    public File getDataFolderFromPlugin() {
        return getDataFolder();
    }

}
