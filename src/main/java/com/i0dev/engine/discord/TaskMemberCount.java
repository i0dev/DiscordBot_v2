package main.java.com.i0dev.engine.discord;

import main.java.com.i0dev.utility.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.TimerTask;

public class TaskMemberCount {

    public static TimerTask MemberCountTimer = new TimerTask() {
        public void run() {
            getConfig configuration = getConfig.get();
            JDA jda = InternalJDA.get().getJda();
            if (!configuration.getBoolean("events.memberCounter.memberCountEnabled")) return;
            VoiceChannel channel = GlobalConfig.GENERAL_MAIN_GUILD.getVoiceChannelById(configuration.getLong("events.memberCounter.memberCountChannelID"));
            if (channel == null) System.out.println("Thee member counting channel is invalid!");
            channel.getManager().setName(Placeholders.convert(configuration.getString("events.memberCounter.channelNameFormat"), jda.getSelfUser())).queue();
        }
    };
}
