package main.java.com.i0dev.object;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import main.java.com.i0dev.InitilizeBot;
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

public class Ticket {

    private static String KEY = "tickets";


    private static Ticket instance = new Ticket();

    public static Ticket get() {
        return instance;
    }

    ArrayList<JSONObject> OpenTicketCache = new ArrayList<>();

    public void createTicket(TextChannel channel, User ticketOwner, boolean adminOnlyMode, int ticketNumber) {
        JSONObject object = new JSONObject();
        object.put("channelID", channel.getId());
        object.put("ticketOwnerID", ticketOwner.getId());
        object.put("ticketOwnerAvatarURL", ticketOwner.getEffectiveAvatarUrl());
        object.put("ticketOwnerTag", ticketOwner.getAsTag());
        object.put("adminOnlyMode", adminOnlyMode);
        object.put("ticketNumber", ticketNumber);
        OpenTicketCache.add(object);
        saveTickets();
    }

    public JSONObject getTicket(String ID) {
        if (OpenTicketCache.isEmpty()) return null;

        for (JSONObject object : OpenTicketCache) {
            if (object.get("channelID").equals(ID)) {
                return object;
            }
        }
        return null;
    }

    public JSONObject getTicket(TextChannel ID) {
        if (OpenTicketCache.isEmpty()) return null;

        for (JSONObject object : OpenTicketCache) {
            if (object.get("channelID").equals(ID.getId())) {
                return object;
            }
        }
        return null;
    }

    public boolean isTicket(TextChannel channel) {
        if (OpenTicketCache.isEmpty()) return false;

        for (JSONObject object : OpenTicketCache) {
            if (object.get("channelID").equals(channel.getId())) {
                return true;
            }
        }
        return false;
    }

    public void ticketAdminOnly(TextChannel channel, boolean adminOnly) {
        JSONObject ticket = getTicket(channel);
        deleteTicket(ticket.get("channelID").toString());
        ticket.put("adminOnlyMode", adminOnly);
        OpenTicketCache.add(ticket);
        saveTickets();
    }


    public JSONObject isTicket(String ID) {
        if (OpenTicketCache.isEmpty()) return null;

        for (JSONObject object : OpenTicketCache) {
            if (object.get("channelID").equals(ID)) {
                return object;
            }
        }
        return null;
    }

    public void deleteTicket(String ID) {
        for (JSONObject object : OpenTicketCache) {
            if (object.get("channelID").equals(ID)) {
                OpenTicketCache.remove(object);
                saveTickets();
                break;
            }
        }
    }

    public void wipeCache() {
        OpenTicketCache.clear();
        saveTickets();
    }

    public ArrayList<JSONObject> getTickets() {
        return OpenTicketCache;
    }

    public void saveTickets() {
        JSONObject all = new JSONObject();
        all.put(KEY, OpenTicketCache);
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(all.toJSONString());
        String jsonString = gson.toJson(el);
        try {
            Files.write(Paths.get(InitilizeBot.get().getTicketsPath()), jsonString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTickets() {
        JSONObject json;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(InitilizeBot.get().getTicketsPath()));
            OpenTicketCache = (ArrayList<JSONObject>) json.get(KEY);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}