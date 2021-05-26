package com.i0dev.modules.mapPoints;

import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class MapPointsManager extends DiscordCommand {

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:map: MapPoints Commands :map:**").append("\n");
        builder.append(add).append("\n");
        return builder.toString();
    }

    public static String add = "`{prefix}mapPoints add <User> <server> <amount>` *Gives map points to that user*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);

    public static String MESSAGE_IS_NOT_NUMBER;
    public static String MESSAGE_SERVER_NOT_VALID;
    public static List<String> GENERAL_SERVER_LIST;

    @Override
    public void init() {
        MESSAGE_IS_NOT_NUMBER = Configuration.getString("modules.mapPoints.message.isNotNumber");
        MESSAGE_SERVER_NOT_VALID = Configuration.getString("modules.mapPoints.message.notValidServer");
        GENERAL_SERVER_LIST = Configuration.getStringList("modules.mapPoints.general.servers");
    }

    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[1].toLowerCase()) {
                case "add":
                    Add.run(e);
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}