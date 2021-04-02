package main.java.com.i0dev.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class MessageAliases {

    public static boolean isMessageACommand(Message message, List<String> aliases) {
        String[] splitMessage = message.getContentRaw().split(" ");
        for (String command : aliases) {
            if (splitMessage[0].equalsIgnoreCase(GlobalConfig.GENERAL_BOT_PREFIX + command)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChannelInList(TextChannel channel, List<Long> ids) {
        for (Long ID : ids) {
            if (channel.equals(channel.getJDA().getTextChannelById(ID))) {
                return true;
            }
        }
        return false;
    }
}
