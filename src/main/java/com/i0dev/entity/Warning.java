package com.i0dev.entity;

import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Warning {

    private static String FILEPATH = "DiscordBot/storage/Warnings.json";
    private static File FILE = new File(FILEPATH);
    private static String KEY = "warnings";

    private static Warning instance = new Warning();

    public static Warning get() {
        return instance;
    }
    public String getFilePath(){
        return FILEPATH;
    }

    ArrayList<JSONObject> WarningCache = new ArrayList<>();

    public void addUser(User user, String reason, User punisher) {
        JSONObject object = new JSONObject();
        object.put("userID", user.getId());
        object.put("reason", reason);
        object.put("punisherID", punisher.getId());
        object.put("punisherTag", punisher.getAsTag());
        WarningCache.add(object);
        saveWarnings();
    }

    public void addUser(User user, User punisher) {
        JSONObject object = new JSONObject();
        object.put("userID", user.getId());
        object.put("reason", "No Reason");
        object.put("punisherID", punisher.getId());
        object.put("punisherTag", punisher.getAsTag());

        WarningCache.add(object);
        saveWarnings();
    }

    public JSONObject getWarningObject(User user) {
        if (WarningCache.isEmpty()) return null;

        for (JSONObject object : WarningCache) {
            if (object.get("userID").equals(user.getId())) {
                return object;
            }
        }
        return null;
    }

    public void removeUser(User user) {
        for (JSONObject object : WarningCache) {
            if (object.get("userID").equals(user.getId())) {
                WarningCache.remove(object);
                saveWarnings();
                break;
            }
        }
    }

    public boolean isOnWarnList(User user) {
        if (WarningCache.isEmpty()) return false;

        for (JSONObject object : WarningCache) {
            if (object.get("userID").equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void wipeCache() {
        WarningCache.clear();
        saveWarnings();
    }

    public ArrayList<JSONObject> getBlacklisted() {
        return WarningCache;
    }

    public void saveWarnings() {
        JSONObject all = new JSONObject();
        all.put(KEY, WarningCache);
        try {
            Files.write(Paths.get(com.i0dev.util.getConfig.get().getFile(FILEPATH).getPath()), all.toJSONString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadWarnings() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(FILE));
            WarningCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}