package com.i0dev.object;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.i0dev.InitilizeBot;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Warning {

    private static String KEY = "warnings";

    private static Warning instance = new Warning();

    public static Warning get() {
        return instance;
    }


    ArrayList<JSONObject> WarningsCache = new ArrayList<>();

    public void increaseUser(User user) {
        if (isAlreadyOn(user)) {
            JSONObject object = getUserObject(user);
            String Warnings = (String) object.get("warnings");
            long WarnLong = Long.parseLong(Warnings);
            WarnLong++;
            removeUserObject(user);
            addNewUser(user, WarnLong);

        } else {
            addNewUser(user);
        }
    }

    public JSONObject getUserObject(User user) {
        for (JSONObject object : WarningsCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                return object;
            }
        }
        return null;
    }

    public void removeUserObject(User user) {
        for (JSONObject object : WarningsCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                WarningsCache.remove(object);
                break;
            }
        }
    }

    public long getUserWarnCountAddOne(User user) {
        try {
            for (JSONObject object : WarningsCache) {
                if (object.get("userID").toString().equals(user.getId())) {
                    String Warnings = (String) object.get("warnings");
                    long WarnLong = Long.parseLong(Warnings);
                    WarnLong++;
                    return WarnLong;
                }
            }
        } catch (Exception ignored) {

        }
        return 1;
    }
    public long getUserWarnCountRemoveOne(User user) {
        try {
            for (JSONObject object : WarningsCache) {
                if (object.get("userID").toString().equals(user.getId())) {
                    String Warnings = (String) object.get("warnings");
                    long WarnLong = Long.parseLong(Warnings);
                    WarnLong--;
                    return WarnLong;
                }
            }
        } catch (Exception ignored) {

        }
        return 0;
    }

    public long getUserWarnCount(User user) {
        for (JSONObject object : WarningsCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                String Warnings = (String) object.get("warnings");
                long WarnLong = Long.parseLong(Warnings);
                return WarnLong;
            }
        }
        return 0;
    }

    public void decreaseUser(User user) {
        if (isAlreadyOn(user)) {
            JSONObject object = getUserObject(user);
            String Warnings = (String) object.get("warnings");
            long WarnLong = Long.parseLong(Warnings);
            WarnLong--;
            removeUserObject(user);
            addNewUser(user, WarnLong);

        }
    }

    public void addNewUser(User user) {
        JSONObject newObject = new JSONObject();
        newObject.put("userID", user.getId());
        newObject.put("warnings", 1 + "");
        WarningsCache.add(newObject);
        saveCacheToFile();
    }

    public void addNewUser(User user, long Invites) {
        JSONObject newObject = new JSONObject();
        newObject.put("userID", user.getId());
        newObject.put("warnings", Invites + "");
        WarningsCache.add(newObject);
        saveCacheToFile();
    }


    public boolean isAlreadyOn(User user) {
        for (JSONObject object : WarningsCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void wipeCache() {
        WarningsCache.clear();
        saveCacheToFile();
    }

    public ArrayList<JSONObject> getCache() {
        return WarningsCache;
    }


    public void saveCacheToFile() {
        JSONObject all = new JSONObject();
        all.put(KEY, WarningsCache);
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(all.toJSONString());
        String jsonString = gson.toJson(el);
        try {
            Files.write(Paths.get(InitilizeBot.get().getWarningsPath()), jsonString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getRaw() {
        try {
            return (JSONObject) new JSONParser().parse(new FileReader(InitilizeBot.get().getWarningsPath()));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadCacheFromFile() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(InitilizeBot.get().getWarningsPath()));
            WarningsCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}