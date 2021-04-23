package com.i0dev.commands.discord.completedModules.linking;

import com.i0dev.InitilizeBot;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class LinkManager {

    public static String usage() {

        StringBuilder builder = new StringBuilder();
        builder.append("**:link: Linking Commands :link:**").append("\n");
        builder.append(code).append("\n");
        builder.append(generate).append("\n");
        builder.append(info).append("\n");
        builder.append(force).append("\n");
        builder.append(remove).append("\n");
        return builder.toString();
    }

    public static String code = "`{prefix}link code <code>` *Links your discord account to the minecraft ign that generated that code.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String generate = "`{prefix}link generate` *Generate a code to then user /link <code> in game with*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String info = "`{prefix}link info <ign | user>` *Get link information about that user*.".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String force = "`{prefix}link force <user> <ign>` *Force a linkage onto a player.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String remove = "`{prefix}link remove <user | ign>` *Removes the link from that user.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);

    public static final String MESSAGE_ALREADY_LINKED = Configuration.getString("modules.link.message.alreadyLinked");
    public static final List<String> INGAME_MESSAGE_ALREADY_LINKED = Configuration.getStringList("modules.link.message.ingameAlreadyLinked");


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message[0].equalsIgnoreCase(GlobalConfig.GENERAL_BOT_PREFIX + "link")) {

            if (!InitilizeBot.isPluginMode()) {
                MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.NOT_PLUGIN_MODE, e.getAuthor());
                return;
            }

            if (message.length == 1) {
                MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
            } else {
                switch (message[1].toLowerCase()) {
                    case "code":
                        Code.run(e);
                        break;
                    case "generate":
                        Generate.run(e);
                        break;
                    case "info":
                        Info.run(e);
                        break;
                    case "force":
                        Force.run(e);
                        break;
                    case "remove":
                        Remove.run(e);
                        break;
                    default:
                        MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                        break;
                }
            }
        }
    }
}