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

public class Application {

    private static String KEY = "applications";

    private static Application instance = new Application();



    public static Application get() {
        return instance;
    }

    ArrayList<JSONObject> ApplicationCache = new ArrayList<>();

    public void addUser(User user, ArrayList<String> questions, ArrayList<String> answers) {
        JSONObject object = new JSONObject();
        object.put("userID", user.getId());
        object.put("userTag", user.getAsTag());
        object.put("questions", questions);
        object.put("answers", answers);
        object.put("timeSubmitted", System.currentTimeMillis());
        ApplicationCache.add(object);
        saveApplications();
    }

    public JSONObject getApplicationObject(User applicant) {
        if (ApplicationCache.isEmpty()) return null;

        for (JSONObject object : ApplicationCache) {
            if (object.get("userID").equals(applicant.getId())) {
                return object;
            }
        }
        return null;
    }

    public void removeUser(User user) {
        for (JSONObject object : ApplicationCache) {
            if (object.get("userID").equals(user.getId())) {
                ApplicationCache.remove(object);
                saveApplications();
                break;
            }
        }
    }

    public boolean hasASubmittedApplication(User user) {
        if (ApplicationCache.isEmpty()) return false;

        for (JSONObject object : ApplicationCache) {
            if (object.get("userID").equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void wipeCache() {
        ApplicationCache.clear();
        saveApplications();
    }

    public ArrayList<JSONObject> getBlacklisted() {
        return ApplicationCache;
    }

    public void saveApplications() {
        JSONObject all = new JSONObject();
        all.put(KEY, ApplicationCache);
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

    public void loadApplications() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(InitilizeBot.get().getApplicationsPath()));
            ApplicationCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}