package main.java.com.i0dev.engine;

import main.java.com.i0dev.command.giveaways.GiveawayCache;
import main.java.com.i0dev.command.polls.PollCache;
import main.java.com.i0dev.entity.Giveaway;
import main.java.com.i0dev.util.EmbedFactory;
import net.dv8tion.jda.api.entities.User;

import java.util.TimerTask;

public class TaskCreatorTimeouts {
    private static TaskCreatorTimeouts instance = new TaskCreatorTimeouts();
    public static TaskCreatorTimeouts get() {
        return instance;
    }

    public TimerTask TaskPollTimeout = new TimerTask() {
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

    public TimerTask TaskGiveawayTimeout = new TimerTask() {
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
