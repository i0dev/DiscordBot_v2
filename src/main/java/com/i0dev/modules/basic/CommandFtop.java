package com.i0dev.modules.basic;

import com.i0dev.modules.other.FactionsTopHandler;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandFtop {

    public static void run(GuildMessageReceivedEvent e) {
        MessageUtil.sendMessage(e.getChannel().getIdLong(), FactionsTopHandler.getFTOPEmbed());
    }

}
