package main.java.com.i0dev.engine;

import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.TimerTask;

public class TaskMemberCount {

    public static TimerTask MemberCountTimer = new TimerTask() {
        public void run() {
            getConfig configuration = getConfig.get();
            JDA jda = initJDA.get().getJda();
            if (!configuration.getBoolean("events.memberCounter.memberCountEnabled")) return;
            VoiceChannel channel = conf.GENERAL_MAIN_GUILD.getVoiceChannelById(configuration.getLong("events.memberCounter.memberCountChannelID"));
            if (channel == null) {
                try {
                    throw new i0devException("Thee member counting channel is invalid!");
                } catch (i0devException e) {
                }
            }
            channel.getManager().setName(Placeholders.convert(configuration.getString("events.memberCounter.channelNameFormat"), jda.getSelfUser())).queue();
        }
    };
}
