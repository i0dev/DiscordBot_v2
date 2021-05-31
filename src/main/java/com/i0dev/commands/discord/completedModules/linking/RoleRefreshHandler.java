package com.i0dev.commands.discord.completedModules.linking;

import com.i0dev.DiscordBot;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.objects.RoleQueueObject;
import com.i0dev.object.objects.Type;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.TempNicknameUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class RoleRefreshHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        DPlayer dPlayer = DPlayerEngine.getObjectFromIGN(e.getPlayer().getName());
        if (dPlayer == null) return;
        if (!dPlayer.getLinkInfo().isLinked()) return;
        if (dPlayer.getCachedData().getMinecraftIGN().equals("")) return;
        RefreshUserRank(dPlayer);
    }

    public static void RefreshUserRank(DPlayer dPlayer) {
        if (dPlayer == null) return;
        if (InternalJDA.getJda().getUserById(dPlayer.getDiscordID()) == null) return;
        if (!dPlayer.getLinkInfo().isLinked()) return;
        if (dPlayer.getCachedData().getMinecraftIGN().equalsIgnoreCase("")) return;
        if (!InitializeBot.isPluginMode()) return;
        if (!DiscordBot.get().getServer().getPluginManager().isPluginEnabled("LuckPerms")) return;
        try {
            Player player = Bukkit.getPlayer(dPlayer.getCachedData().getMinecraftIGN());
            if (player != null) {
                if (!player.hasPermission("discordbot.link.nickname.bypass")) {
                    User discordUser = InternalJDA.getJda().getUserById(dPlayer.getDiscordID());
                    if (discordUser != null) {
                        String format = Configuration.getString("modules.link.general.nicknameFormat");
                        TempNicknameUtil.modifyNickname(discordUser, format.replace("{ign}", dPlayer.getCachedData().getMinecraftIGN()));
                    }
                }
            }
        } catch (Exception ignored) {

        }

        net.luckperms.api.model.user.User user;
        try {
            user = net.luckperms.api.LuckPermsProvider.get().getUserManager().getUser(dPlayer.getCachedData().getMinecraftIGN());
        } catch (Exception ignored) {
            return;
        }
        if (user == null) return;

        List<String> groups = user.getNodes(net.luckperms.api.node.NodeType.INHERITANCE).stream()
                .map(net.luckperms.api.node.types.InheritanceNode::getGroupName)
                .collect(Collectors.toList());
        List<Long> ranksAlways = Configuration.getLongList("modules.link.general.rolesToGiveAlways");
        for (Long roleID : ranksAlways) {
            Role role = GlobalConfig.GENERAL_MAIN_GUILD.getRoleById(roleID);
            if (role == null) continue;
            if (GlobalConfig.GENERAL_MAIN_GUILD.getMemberById(dPlayer.getDiscordID()).getRoles().contains(role))
                continue;
            new RoleQueueObject(dPlayer.getDiscordID(), roleID, Type.ADD_ROLE).add();

        }
        if (groups.isEmpty()) return;
        JSONObject ranksToGive = Configuration.getObject("modules.link.general.ranksToLink");
        for (Object key : ranksToGive.keySet()) {
            if (groups.contains(key.toString())) {
                long roleID = ((long) ranksToGive.get(key));
                Role role = GlobalConfig.GENERAL_MAIN_GUILD.getRoleById(roleID);
                if (role == null) continue;
                if (GlobalConfig.GENERAL_MAIN_GUILD.getMemberById(dPlayer.getDiscordID()).getRoles().contains(role))
                    continue;
                new RoleQueueObject(dPlayer.getDiscordID(), roleID, Type.ADD_ROLE).add();
            }
        }
    }
}
