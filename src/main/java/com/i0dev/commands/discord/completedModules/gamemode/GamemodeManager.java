package com.i0dev.commands.discord.completedModules.gamemode;

import com.i0dev.commands.discord.completedModules.gamemode.factions.FactionsModule;
import com.i0dev.commands.discord.completedModules.gamemode.prison.PrisonModule;
import com.i0dev.commands.discord.completedModules.gamemode.skyblock.SkyBlockModule;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@Getter
public class GamemodeManager {

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:video_game: GameMode Commands :video_game:**").append("\n");

        builder.append(factionsMod).append("\n");
        builder.append(skyblockMod).append("\n");
        builder.append(prisonMod).append("\n");
        return builder.toString();
    }

    public static String factionsMod = "`{prefix}gamemode Factions <...>` *Goes to the Factions module.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String skyblockMod = "`{prefix}gamemode SkyBlock <...>` *Goes to the SkyBlock module.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String prisonMod = "`{prefix}gamemode Prison <...>` *Goes to the Prison module.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[1].toLowerCase()) {
                case "factions":
                    FactionsModule.run(e);
                    break;
                case "skyblock":
                    SkyBlockModule.run(e);
                    break;
                case "prison":
                    PrisonModule.run(e);
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}