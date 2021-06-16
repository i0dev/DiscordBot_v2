package com.i0dev.commands.discord.completedModules.linking;

import com.i0dev.DiscordBot;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.objects.RoleQueueObject;
import com.i0dev.object.objects.Type;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.RoleUtil;
import com.i0dev.utility.util.TempNicknameUtil;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.UUID;
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
        User discordUser = InternalJDA.getJda().getUserById(dPlayer.getDiscordID());
        if (discordUser == null) return;
        if (!dPlayer.getLinkInfo().isLinked()) return;
        if (dPlayer.getCachedData().getMinecraftIGN().equalsIgnoreCase("")) return;
        if (!InitializeBot.isPluginMode()) return;
        if (!DiscordBot.get().getServer().getPluginManager().isPluginEnabled("LuckPerms")) return;
        net.luckperms.api.model.user.User user = luckPerms.getUserManager().loadUser(UUID.fromString(dPlayer.getLinkInfo().getMinecraftUUID())).join();
        if (user == null) return;
        List<String> groups = user.getNodes(net.luckperms.api.node.NodeType.INHERITANCE).stream()
                .map(net.luckperms.api.node.types.InheritanceNode::getGroupName)
                .collect(Collectors.toList());
        if (!RoleUtil.hasRole(discordUser, Configuration.getLongList("modules.link.general.rolesThatBypassNicknameChange"))) {
            Player player = Bukkit.getPlayer(dPlayer.getCachedData().getMinecraftIGN());
            if (player == null || !player.hasPermission("discordbot.link.nickname.bypass")) {
                TempNicknameUtil.modifyNickname(discordUser, Configuration.getString("modules.link.general.nicknameFormat")
                        .replace("{faction}", FormatUtil.getFactionName(dPlayer))
                        .replace("{prefix}", FormatUtil.getPrefix(dPlayer))
                        .replace("{ign}", dPlayer.getCachedData().getMinecraftIGN()));
            }
        }


        List<Long> ranksAlways = Configuration.getLongList("modules.link.general.rolesToGiveAlways");
        for (Long roleID : ranksAlways) {
            new RoleQueueObject(dPlayer.getDiscordID(), roleID, Type.ADD_ROLE).add();
        }

        if (groups.isEmpty()) return;
        JSONObject ranksToGive = Configuration.getObject("modules.link.general.ranksToLink");
        for (Object key : ranksToGive.keySet()) {
            if (groups.contains(key.toString())) {
                long roleID = ((long) ranksToGive.get(key));
                new RoleQueueObject(dPlayer.getDiscordID(), roleID, Type.ADD_ROLE).add();
            } else {
                long roleID = ((long) ranksToGive.get(key));
                new RoleQueueObject(dPlayer.getDiscordID(), roleID, Type.REMOVE_ROLE).add();
            }
        }
    }

    static net.luckperms.api.LuckPerms luckPerms = net.luckperms.api.LuckPermsProvider.get();

}
