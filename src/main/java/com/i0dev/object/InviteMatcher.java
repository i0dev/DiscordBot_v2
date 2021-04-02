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

public class InviteMatcher {

    private static String KEY = "invitepairs";

    private static InviteMatcher instance = new InviteMatcher();

    public static InviteMatcher get() {
        return instance;
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
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(all.toJSONString());
        String jsonString = gson.toJson(el);
        try {
            Files.write(Paths.get(InitilizeBot.get().getApplicationsPath()), jsonString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCacheFromFile() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(InitilizeBot.get().getInviteMatcherPath()));
            InvitesCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}