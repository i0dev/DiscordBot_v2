package com.i0dev.engine.discord;

import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.Configuration;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.TimerTask;

public class TaskMemberCount {

    public static TimerTask MemberCountTimer = new TimerTask() {
        public void run() {
            if (!Configuration.getBoolean("events.memberCounter.memberCountEnabled")) return;
            VoiceChannel channel = GlobalConfig.GENERAL_MAIN_GUILD.getVoiceChannelById(Configuration.getLong("events.memberCounter.memberCountChannelID"));
            if (channel == null) System.out.println("The member counting channel is invalid!");
            channel.getManager().setName(Placeholders.convert(Configuration.getString("events.memberCounter.channelNameFormat"))).queue();
        }
    };
}
