package com.i0dev.commands.discord.completedModules.gamemode.factions;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@Getter
public class FactionsModule   {

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:crossed_swords: GameMode Factions Commands :crossed_swords:**").append("\n");
        builder.append(leaderMod).append("\n");
        builder.append(confirmMod).append("\n");
        return builder.toString();
    }

    public static String leaderMod = "`{prefix}gamemode Factions Leader <User>` *Give a user the Faction Leader Role.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String confirmMod = "`{prefix}gamemode Factions Confirm <Leader> <Faction Name> <Roster Size>` *Confirm a faction as playing.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);

    public static final long CONFIRMED_FACTION_CHANNEL = Configuration.getLong("channels.confirmedFactionChannelID");
    public static final long FACTION_LEADER_ROLE = Configuration.getLong("roles.factionLeaderRoleID");

    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[2].toLowerCase()) {
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