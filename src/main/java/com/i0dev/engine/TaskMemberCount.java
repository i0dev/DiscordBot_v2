package main.java.com.i0dev.engine;

import main.java.com.i0dev.util.Placeholders;
import main.java.com.i0dev.util.conf;
import main.java.com.i0dev.util.getConfig;
import main.java.com.i0dev.util.initJDA;
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
                System.out.println("The member counter channel is null!");
                return;
            }
            channel.getManager().setName(Placeholders.convert(configuration.getString("events.memberCounter.channelNameFormat"), jda.getSelfUser())).queue();
        }
    };
}
