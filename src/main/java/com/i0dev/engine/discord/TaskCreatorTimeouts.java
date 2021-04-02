package main.java.com.i0dev.engine.discord;

import main.java.com.i0dev.cache.GiveawayCache;
import main.java.com.i0dev.cache.PollCache;
import main.java.com.i0dev.utility.EmbedFactory;
import net.dv8tion.jda.api.entities.User;

import java.util.TimerTask;

public class TaskCreatorTimeouts {

    public static TimerTask TaskPollTimeout = new TimerTask() {
        public void run() {
            if (PollCache.get().getTimeoutMap().isEmpty()) return;
            PollCache.get().getTimeoutMap().forEach((user, timout) -> {
                if (System.currentTimeMillis() > (long) timout) {
                    try {
                        ((User) user).openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed("Your poll creator has timed out due to inactivity.").build()).queue();
                    } catch (Exception ignored) {
                    }
                    PollCache.get().removeUser((User) user);
                }
            });
        }
    };

    public static TimerTask TaskGiveawayTimeout = new TimerTask() {
        public void run() {
            if (GiveawayCache.get().getTimeoutMap().isEmpty()) return;
            GiveawayCache.get().getTimeoutMap().forEach((user, timout) -> {
                if (System.currentTimeMillis() > (long) timout) {
                    try {
                        ((User) user).openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed("Your giveaway creator has timed out due to inactivity.").build()).queue();
                    } catch (Exception ignored) {
                    }
                    GiveawayCache.get().removeUser((User) user);
                }
            });
        }
    };
}
