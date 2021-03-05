package main.java.com.i0dev.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import main.java.com.i0dev.util.getConfig;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ReactionRoles {

    private static String FILEPATH = "DiscordBot/storage/ReactionRoles.json";
    private static String KEY = "reactionroles";
    private static File FILE = new File(FILEPATH);

    public String getFilePath() {
        return FILEPATH;
    }

    private static ReactionRoles instance = new ReactionRoles();

    public static ReactionRoles get() {
        return instance;
    }

    ArrayList<JSONObject> ReactionRoleCache = new ArrayList<>();

    public void createReactionRole(TextChannel channel, Message message, ArrayList<JSONObject> options) {
        JSONObject object = new JSONObject();
        object.put("channelID", channel.getId());
        object.put("messageID", message.getId());
        object.put("options", options);
        ReactionRoleCache.add(object);
        saveObject();
    }

    public JSONObject getReactionRole(String ID) {
        if (ReactionRoleCache.isEmpty()) return null;

        for (JSONObject object : ReactionRoleCache) {
            if (object.get("messageID").equals(ID)) {
                return object;
            }
        }
        return null;
    }

    public void deleteReactionRole(String ID) {
        for (JSONObject object : ReactionRoleCache) {
            if (object.get("messageID").equals(ID)) {
                ReactionRoleCache.remove(object);
                saveObject();
                break;

            }
        }
    }

    public void wipeCache() {
        ReactionRoleCache.clear();
        saveObject();
    }

    public ArrayList<JSONObject> getCache() {
        return ReactionRoleCache;
    }

    public void saveObject() {
        JSONObject all = new JSONObject();
        all.put(KEY, ReactionRoleCache);
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

    public void loadObject() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(FILE));
            ReactionRoleCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}