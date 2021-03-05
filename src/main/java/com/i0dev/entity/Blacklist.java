package main.java.com.i0dev.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import main.java.com.i0dev.util.getConfig;
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

public class Blacklist {

    private static String FILEPATH = "DiscordBot/storage/Blacklisted.json";
    private static String KEY = "blacklisted";
    private static File FILE = new File(FILEPATH);

    private static Blacklist instance = new Blacklist();

    public static Blacklist get() {
        return instance;
    }
    public String getFilePath(){
        return FILEPATH;
    }
    ArrayList<JSONObject> BlacklistedUsersCache = new ArrayList<>();

    public void addUser(User user, String reason, User punisher) {
        JSONObject object = new JSONObject();
        object.put("userID", user.getId());
        object.put("userTag", user.getAsTag());
        object.put("reason", reason);
        object.put("punisherID", punisher.getId());
        object.put("punisherTag", punisher.getAsTag());
        BlacklistedUsersCache.add(object);
        saveBlacklist();
    }

    public JSONObject getBlacklistedObject(User user) {
        if (BlacklistedUsersCache.isEmpty()) return null;

        for (JSONObject object : BlacklistedUsersCache) {
            if (object.get("userID").equals(user.getId())) {
                return object;
            }
        }
        return null;
    }

    public void removeUser(User user) {
        for (JSONObject object : BlacklistedUsersCache) {
            if (object.get("userID").equals(user.getId())) {
                BlacklistedUsersCache.remove(object);
                saveBlacklist();
                break;
            }
        }
    }

    public boolean isBlacklisted(User user) {
        if (BlacklistedUsersCache.isEmpty()) return false;

        for (JSONObject object : BlacklistedUsersCache) {
            if (object.get("userID").equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void wipeCache() {
        BlacklistedUsersCache.clear();
        saveBlacklist();
    }

    public ArrayList<JSONObject> getBlacklisted() {
        return BlacklistedUsersCache;
    }

    public void saveBlacklist() {
        JSONObject all = new JSONObject();
        all.put(KEY, BlacklistedUsersCache);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(all.toJSONString());
        String jsonString = gson.toJson(el);

        try {
            Files.write(Paths.get(getConfig.get().getFile(FILEPATH).getPath()), jsonString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBlacklist() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(FILE));
            BlacklistedUsersCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}