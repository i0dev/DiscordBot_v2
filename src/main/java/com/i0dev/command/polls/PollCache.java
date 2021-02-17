package main.java.com.i0dev.command.polls;

import main.java.com.i0dev.util.EmbedFactory;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimerTask;

public class PollCache {

    private static PollCache instance = new PollCache();

    public static PollCache get() {
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
        PollCache.get().getResponseMap().remove(user);
        PollCache.get().getMap().remove(user);
        PollCache.get().getQuestionMap().remove(user);
        PollCache.get().getTimeoutMap().remove(user);
    }
}
