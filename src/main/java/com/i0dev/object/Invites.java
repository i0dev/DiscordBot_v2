package main.java.com.i0dev.object;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import main.java.com.i0dev.InitilizeBot;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Invites {

    private static String KEY = "invites";

    private static Invites instance = new Invites();

    public static Invites get() {
        return instance;
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
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(all.toJSONString());
        String jsonString = gson.toJson(el);
        try {
            Files.write(Paths.get(InitilizeBot.get().getInvitesPath()), jsonString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getRaw() {
        try {
            return (JSONObject) new JSONParser().parse(new FileReader(InitilizeBot.get().getInvitesPath()));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadCacheFromFile() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(InitilizeBot.get().getInvitesPath()));
            InvitesCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}