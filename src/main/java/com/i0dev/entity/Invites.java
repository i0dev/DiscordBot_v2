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

public class Invites {

    private static String FILEPATH = "DiscordBot/storage/Invites.json";
    private static String KEY = "invites";
    private static File FILE = new File(FILEPATH);

    private static Invites instance = new Invites();

    public static Invites get() {
        return instance;
    }

    public String getFilePath() {
        return FILEPATH;
    }

    ArrayList<JSONObject> InvitesCache = new ArrayList<>();

    public void increaseUser(User user) {
        if (isAlreadyOn(user)) {
            JSONObject object = getUserObject(user);
            String Invites = (String) object.get("invites");
            long InvitesLong = Long.parseLong(Invites);
            InvitesLong++;
            removeUserObject(user);
            addNewUser(user, InvitesLong);

        } else {
            addNewUser(user);
        }
    }

    public JSONObject getUserObject(User user) {
        for (JSONObject object : InvitesCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                return object;
            }
        }
        return null;
    }

    public void removeUserObject(User user) {
        for (JSONObject object : InvitesCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                InvitesCache.remove(object);
                break;
            }
        }
    }

    public long getUserInviteCountAdd1(User user) {
        try {
            for (JSONObject object : InvitesCache) {
                if (object.get("userID").toString().equals(user.getId())) {
                    String Invites = (String) object.get("invites");
                    long InvitesLong = Long.parseLong(Invites);
                    InvitesLong++;
                    return InvitesLong;
                }
            }
        } catch (Exception ignored) {

        }
        return 1;
    }

    public long getUserInviteCount(User user) {
        for (JSONObject object : InvitesCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                String Invites = (String) object.get("invites");
                long InvitesLong = Long.parseLong(Invites);
                return InvitesLong;
            }
        }
        return 0;
    }

    public void decreaseUser(User user) {
        if (isAlreadyOn(user)) {
            JSONObject object = getUserObject(user);
            String Invites = (String) object.get("invites");
            long InvitesLong = Long.parseLong(Invites);
            InvitesLong--;
            removeUserObject(user);
            addNewUser(user, InvitesLong);

        }
    }

    public void addNewUser(User user) {
        JSONObject newObject = new JSONObject();
        newObject.put("userID", user.getId());
        newObject.put("invites", 1 + "");
        InvitesCache.add(newObject);
        saveCacheToFile();
    }

    public void addNewUser(User user, long Invites) {
        JSONObject newObject = new JSONObject();
        newObject.put("userID", user.getId());
        newObject.put("invites", Invites + "");
        InvitesCache.add(newObject);
        saveCacheToFile();
    }


    public boolean isAlreadyOn(User user) {
        for (JSONObject object : InvitesCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void wipeCache() {
        InvitesCache.clear();
        saveCacheToFile();
    }

    public ArrayList<JSONObject> getCache() {
        return InvitesCache;
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

    public JSONObject getRaw() {
        try {
            return (JSONObject) new JSONParser().parse(new FileReader(FILE));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
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