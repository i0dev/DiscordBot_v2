package com.i0dev.cache;

import com.i0dev.utility.EmbedFactory;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimerTask;

public class GiveawayCache {

    private static GiveawayCache instance = new GiveawayCache();

    public static GiveawayCache get() {
        return instance;
    }

    public HashMap<User, Integer> Map = new HashMap<>();
    public LinkedHashMap<User, LinkedHashMap<String, String>> responseMap = new LinkedHashMap<>();
    public LinkedHashMap<User, ArrayList<String>> QuestionMap = new LinkedHashMap<>();
    public HashMap<User, Long> TimeoutMap = new HashMap<>();

    public HashMap getMap() {
        return Map;
    }

    public HashMap getTimeoutMap() {
        return TimeoutMap;
    }

    public LinkedHashMap getResponseMap() {
        return responseMap;
    }

    public LinkedHashMap getQuestionMap() {
        return QuestionMap;
    }

    public void removeUser(User user) {
        GiveawayCache.get().getResponseMap().remove(user);
        GiveawayCache.get().getMap().remove(user);
        GiveawayCache.get().getQuestionMap().remove(user);
        GiveawayCache.get().getTimeoutMap().remove(user);
    }

    public TimerTask TaskCheckTimeouts = new TimerTask() {
        public void run() {
            if (TimeoutMap.isEmpty()) return;
            TimeoutMap.forEach((user, timout) -> {
                if (System.currentTimeMillis() > timout) {
                    try {
                        user.openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed("Your giveaway creator has timed out due to inactivity.").build()).queue();
                    } catch (Exception ignored) {
                    }
                    GiveawayCache.get().removeUser(user);
                }
            });
        }
    };
}
