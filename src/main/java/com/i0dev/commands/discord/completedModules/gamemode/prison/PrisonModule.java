package com.i0dev.commands.discord.completedModules.gamemode.prison;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Getter
public class PrisonModule extends ListenerAdapter {

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:police_car: GameMode Prison Commands :police_car:**").append("\n");
        builder.append(leaderMod).append("\n");
        builder.append(confirmMod).append("\n");
        return builder.toString();
    }

    public static String leaderMod = "`{prefix}gamemode Prison Leader <User>` *Give a user the Cell/Gang Leader Role.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String confirmMod = "`{prefix}gamemode Prison Confirm <Leader> <Gang/Cell Name> <Roster Size>` *Confirm a Gang/Cell as playing.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);

    public static final long CONFIRMED_CELL_CHANNEL = Configuration.getLong("channels.confirmedCellChannelID");
    public static final long PRISON_LEADER_ROLE = Configuration.getLong("roles.cellLeaderRoleID");

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