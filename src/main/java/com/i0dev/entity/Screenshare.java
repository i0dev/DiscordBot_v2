package main.java.com.i0dev.entity;

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

public class Screenshare {

    private static String FILEPATH = "DiscordBot/storage/Screenshare.json";
    private static String KEY = "screenshare";
    private static File FILE = new File(FILEPATH);

    private static Screenshare instance = new Screenshare();

    public static Screenshare get() {
        return instance;
    }
    public String getFilePath(){
        return FILEPATH;
    }
    ArrayList<JSONObject> ScreenshareCache = new ArrayList<>();

    public void addUser(User user, String reason, User punisher) {
        JSONObject object = new JSONObject();
        object.put("userID", user.getId());
        object.put("reason", reason);
        object.put("punisherID", punisher.getId());
        object.put("punisherTag", punisher.getAsTag());
        ScreenshareCache.add(object);
        saveScreenshare();

    }

    public void addUser(User user, User punisher) {
        JSONObject object = new JSONObject();
        object.put("userID", user.getId());
        object.put("reason", "No Reason");
        object.put("punisherID", punisher.getId());
        object.put("punisherTag", punisher.getAsTag());

        ScreenshareCache.add(object);
        saveScreenshare();
    }

    public JSONObject getScreenshareObject(User user) {
        if (ScreenshareCache.isEmpty()) return null;

        for (JSONObject object : ScreenshareCache) {
            if (object.get("userID").equals(user.getId())) {
                return object;
            }
        }
        return null;
    }

    public void removeUser(User user) {
        for (JSONObject object : ScreenshareCache) {
            if (object.get("userID").equals(user.getId())) {
                ScreenshareCache.remove(object);
                saveScreenshare();
                break;

            }
        }
    }

    public boolean isOnSSList(User user) {
        if (ScreenshareCache.isEmpty()) return false;

        for (JSONObject object : ScreenshareCache) {
            if (object.get("userID").equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void wipeCache() {
        ScreenshareCache.clear();
        saveScreenshare();
    }

    public ArrayList<JSONObject> getScreenshareList() {
        return ScreenshareCache;
    }

    public void saveScreenshare() {
        JSONObject all = new JSONObject();
        all.put(KEY, ScreenshareCache);
        try {
            Files.write(Paths.get(getConfig.get().getFile(FILEPATH).getPath()), all.toJSONString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadScreenshare() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(FILE));
            ScreenshareCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}