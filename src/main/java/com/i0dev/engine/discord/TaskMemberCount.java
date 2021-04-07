package com.i0dev.engine.discord;

import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.getConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.TimerTask;

public class TaskMemberCount {

    public static TimerTask MemberCountTimer = new TimerTask() {
        public void run() {
            getConfig configuration = getConfig.get();
            if (!configuration.getBoolean("events.memberCounter.memberCountEnabled")) return;
            VoiceChannel channel = GlobalConfig.GENERAL_MAIN_GUILD.getVoiceChannelById(configuration.getLong("events.memberCounter.memberCountChannelID"));
            if (channel == null) System.out.println("The member counting channel is invalid!");
            channel.getManager().setName(Placeholders.convert(configuration.getString("events.memberCounter.channelNameFormat"))).queue();
        }
    };
}
