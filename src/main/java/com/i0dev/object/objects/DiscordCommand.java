package com.i0dev.object.objects;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@Getter
@Setter
public abstract class DiscordCommand {

    public abstract void init();

    public static void run(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("An error occurred.").queue();
    }

}
