package com.i0dev.entity;

import com.i0dev.util.conf;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
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

public class Giveaway {

    private static String FILEPATH = "DiscordBot/storage/Giveaways.json";
    private static String KEY = "giveaways";
    private static File FILE = new File(FILEPATH);

    public String getFilePath() {
        return FILEPATH;
    }

    private static Giveaway instance = new Giveaway();

    public static Giveaway get() {
        return instance;
    }

    ArrayList<JSONObject> GiveawayCache = new ArrayList<>();

    public void createGiveaway(User user, String prize, TextChannel channel, Message message, long endTime, int winnerAmount) {
        JSONObject object = new JSONObject();
        object.put("channelID", channel.getId());
        object.put("messageID", message.getId());
        object.put("guildID", conf.GENERAL_MAIN_GUILD.getId());
        object.put("prize", prize);
        object.put("startTime", System.currentTimeMillis());
        object.put("endTime", endTime);
        object.put("winnerAmount", winnerAmount);
        GiveawayCache.add(object);
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

    public ArrayList<JSONObject> getBlacklisted() {
        return GiveawayCache;
    }

    public void saveGiveaways() {
        JSONObject all = new JSONObject();
        all.put(KEY, GiveawayCache);
        try {
            Files.write(Paths.get(com.i0dev.util.getConfig.get().getFile(FILEPATH).getPath()), all.toJSONString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadGiveaways() {
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(FILE));
            GiveawayCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}