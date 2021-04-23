package com.i0dev.commands.discord.completedModules.gamemode.skyblock;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Getter
public class SkyBlockModule extends ListenerAdapter {

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:deciduous_tree: GameMode SkyBlock Commands :deciduous_tree:**").append("\n");
        builder.append(leaderMod).append("\n");
        builder.append(confirmMod).append("\n");
        return builder.toString();
    }

    public static String leaderMod = "`{prefix}gamemode SkyBlock Leader <User>` *Give a user the Island Leader Role.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String confirmMod = "`{prefix}gamemode SkyBlock Confirm <Leader> <Island Name> <Roster Size>` *Confirm a Island as playing.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);

    public static final long CONFIRMED_ISLAND_CHANNEL = Configuration.getLong("channels.confirmedIslandChannelID");
    public static final long ISLAND_LEADER_ROLE = Configuration.getLong("roles.islandLeaderRoleID");

    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[1].toLowerCase()) {
                case "leader":
                    Leader.run(e);
                    break;
                case "confirm":
                    Confirm.run(e);
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }

        }
    }
}