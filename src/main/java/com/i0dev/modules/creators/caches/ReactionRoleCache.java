package com.i0dev.modules.creators.caches;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ReactionRoleCache {

    private static ReactionRoleCache instance = new ReactionRoleCache();

    public static ReactionRoleCache get() {
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
        ReactionRoleCache.get().getResponseMap().remove(user);
        ReactionRoleCache.get().getMap().remove(user);
        ReactionRoleCache.get().getQuestionMap().remove(user);
        ReactionRoleCache.get().getTimeoutMap().remove(user);
    }
}
