package main.java.com.i0dev.util;

import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class MessageAliases {

    public static boolean isMessageACommand(Message message, List<String> aliases) {
        String[] splitMessage = message.getContentRaw().split(" ");
        for (String command : aliases) {
            if (splitMessage[0].equalsIgnoreCase(getConfig.get().getString("general.prefix") + command)) {
                return true;
            }
        }
        return false;
    }
}
