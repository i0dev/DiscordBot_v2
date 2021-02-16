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
import java.util.HashMap;

public class InviteMatcher {

    private static String FILEPATH = "DiscordBot/storage/InviteMatcher.json";
    private static String KEY = "invitepairs";
    private static File FILE = new File(FILEPATH);

    private static InviteMatcher instance = new InviteMatcher();

    public static InviteMatcher get() {
        return instance;
    }

    public String getFilePath() {
        return FILEPATH;
    }

    ArrayList<JSONObject> InvitesCache = new ArrayList<>();

    public JSONObject getNewJoinObject(User newJoin) {
        for (JSONObject object : InvitesCache) {
            if (object.get("newJoin").toString().equals(newJoin.getId())) {
                return object;
            }
        }
        return null;
    }

    public void removeNewJoin(User newJoin) {
        for (JSONObject object : InvitesCache) {
            if (object.get("newJoin").toString().equals(newJoin.getId())) {
                InvitesCache.remove(object);
                saveCacheToFile();
                break;
            }
        }
    }
    public void wipeCache() {
        InvitesCache.clear();
        saveCacheToFile();
    }

    public void addNewUser(User invitedBy, User newJoin) {
        JSONObject newObject = new JSONObject();
        newObject.put("newJoin", newJoin.getId());
        newObject.put("invitedBy", invitedBy.getId());
        InvitesCache.add(newObject);
        saveCacheToFile();
    }

    public boolean isAlreadyOn(User invitedBy) {
        for (JSONObject object : InvitesCache) {
            if (object.get("invitedBy").toString().equals(invitedBy.getId())) {
                return true;
            }
        }
        return false;
    }


    public void saveCacheToFile() {
        JSONObject all = new JSONObject();
        all.put(KEY, InvitesCache);
        try {
            Files.write(Paths.get(getConfig.get().getFile(FILEPATH).getPath()), all.toJSONString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCacheFromFile() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(FILE));
            InvitesCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}