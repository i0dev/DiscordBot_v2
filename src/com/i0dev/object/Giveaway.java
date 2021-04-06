package com.i0dev.object;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.i0dev.InitilizeBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Giveaway {

    private static String KEY = "giveaways";


    private static Giveaway instance = new Giveaway();

    public static Giveaway get() {
        return instance;
    }

    ArrayList<JSONObject> GiveawayCache = new ArrayList<>();

    public void createGiveaway(User user, String prize, TextChannel channel, Message message, long endTime, int winnerAmount) {
        JSONObject object = new JSONObject();
        object.put("channelID", channel.getId());
        object.put("messageID", message.getId());
        object.put("hostID", user.getId());
        object.put("prize", prize);
        object.put("endTime", endTime);
        object.put("winnerAmount", winnerAmount);
        object.put("ended", false);
        GiveawayCache.add(object);
        saveGiveaways();
    }

    public void createGiveaway(JSONObject giveaway, boolean ended) {
        giveaway.put("ended", ended);
        GiveawayCache.add(giveaway);
        saveGiveaways();
    }

    public JSONObject getGiveaway(String ID) {
        if (GiveawayCache.isEmpty()) return null;

        for (JSONObject object : GiveawayCache) {
            if (object.get("messageID").equals(ID)) {
                return object;
            }
        }
        return null;
    }

    public void endGiveaway(String ID) {
        if (GiveawayCache.isEmpty()) return;
        JSONObject giveaway = getGiveaway(ID);
        deleteGiveaway(ID);
        createGiveaway(giveaway, true);

    }

    public void deleteGiveaway(String ID) {
        for (JSONObject object : GiveawayCache) {
            if (object.get("messageID").equals(ID)) {
                GiveawayCache.remove(object);
                saveGiveaways();
                break;

            }
        }
    }

    public void wipeCache() {
        GiveawayCache.clear();
        saveGiveaways();
    }

    public ArrayList<JSONObject> getCache() {
        return GiveawayCache;
    }

    public void saveGiveaways() {
        JSONObject all = new JSONObject();
        all.put(KEY, GiveawayCache);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(all.toJSONString());
        String jsonString = gson.toJson(el);

        try {
            Files.write(Paths.get(InitilizeBot.get().getGiveawaysPath()), jsonString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadGiveaways() {
        JSONObject json = null;

        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(InitilizeBot.get().getGiveawaysPath()));
            GiveawayCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}