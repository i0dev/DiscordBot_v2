package main.java.com.i0dev.cache;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ApplicationCache {

    private static ApplicationCache instance = new ApplicationCache();

    public static ApplicationCache get() {
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
        ApplicationCache.get().getResponseMap().remove(user);
        ApplicationCache.get().getMap().remove(user);
        ApplicationCache.get().getQuestionMap().remove(user);
        ApplicationCache.get().getTimeoutMap().remove(user);
    }
}
