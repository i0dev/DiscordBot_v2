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

public class TicketTop {

    private static String KEY = "ticketTop";

    private static TicketTop instance = new TicketTop();

    public static TicketTop get() {
        return instance;
    }


    ArrayList<JSONObject> TicketTopCache = new ArrayList<>();

    public void increaseUser(User user) {
        if (isAlreadyOn(user)) {
            JSONObject object = getUserObject(user);
            String Invites = (String) object.get("count");
            long InvitesLong = Long.parseLong(Invites);
            InvitesLong++;
            removeUserObject(user);
            addNewUser(user, InvitesLong);

        } else {
            addNewUser(user);
        }
    }

    public JSONObject getUserObject(User user) {
        for (JSONObject object : TicketTopCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                return object;
            }
        }
        return null;
    }

    public void removeUserObject(User user) {
        for (JSONObject object : TicketTopCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                TicketTopCache.remove(object);
                break;
            }
        }
    }


    public long getUserInviteCount(User user) {
        for (JSONObject object : TicketTopCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                String Invites = (String) object.get("count");
                long InvitesLong = Long.parseLong(Invites);
                return InvitesLong;
            }
        }
        return 0;
    }


    public void addNewUser(User user) {
        JSONObject newObject = new JSONObject();
        newObject.put("userID", user.getId());
        newObject.put("count", 1 + "");
        TicketTopCache.add(newObject);
        saveCacheToFile();
    }

    public void addNewUser(User user, long Invites) {
        JSONObject newObject = new JSONObject();
        newObject.put("userID", user.getId());
        newObject.put("count", Invites + "");
        TicketTopCache.add(newObject);
        saveCacheToFile();
    }


    public boolean isAlreadyOn(User user) {
        for (JSONObject object : TicketTopCache) {
            if (object.get("userID").toString().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void wipeCache() {
        TicketTopCache.clear();
        saveCacheToFile();
    }

    public ArrayList<JSONObject> getCache() {
        return TicketTopCache;
    }


    public void saveCacheToFile() {
        JSONObject all = new JSONObject();
        all.put(KEY, TicketTopCache);
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(all.toJSONString());
        String jsonString = gson.toJson(el);
        try {
            Files.write(Paths.get(InitilizeBot.get().getTicketTopPath()), jsonString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCacheFromFile() {
        JSONObject json;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(InitilizeBot.get().getTicketTopPath()));
            TicketTopCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}