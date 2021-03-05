package main.java.com.i0dev.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import main.java.com.i0dev.resources.Resources;
import net.dv8tion.jda.api.JDA;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class getConfig {

    JSONObject json;

    public JSONObject getJson() {
        return json;
    }

    String FILEPATH = "DiscordBot/Config.json";

    public String getFilePath() {
        return FILEPATH;
    }

    public void reloadConfig() {
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(FILEPATH));
            System.out.println("Reloaded Configuration");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public File getFile(String name) {
        File file = new File(name);
        try {
            if (!file.exists()) saveResource(name);
            return file;
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static final getConfig configuration = new getConfig();

    public static getConfig get() {
        return configuration;
    }

    public void saveJSON() {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(json.toJSONString());
        String jsonString = gson.toJson(el);
        try {
            Files.write(Paths.get(getFile(FILEPATH).getPath()), jsonString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getString(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (String) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return finalProduct.get(paths[paths.length - 1]).toString();
    }

    public int getInt(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (Integer) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return (Integer) finalProduct.get(paths[paths.length - 1]);
    }

    public Long getLong(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (Long) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return (Long) finalProduct.get(paths[paths.length - 1]);
    }

    public List<String> getStringList(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (List<String>) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }

        return (List<String>) finalProduct.get(paths[paths.length - 1]);
    }

    public List<JSONObject> getObjectList(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (List<JSONObject>) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }

        return (List<JSONObject>) finalProduct.get(paths[paths.length - 1]);
    }

    public List<Integer> getIntList(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (List<Integer>) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return (List<Integer>) finalProduct.get(paths[paths.length - 1]);
    }


    public List<Long> getLongList(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (List<Long>) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return (ArrayList<Long>) finalProduct.get(paths[paths.length - 1]);
    }

    public boolean getBoolean(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (Boolean) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return (Boolean) finalProduct.get(paths[paths.length - 1]);
    }

    public double getDouble(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (Double) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return (Double) finalProduct.get(paths[paths.length - 1]);
    }

    public float getFloat(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (Float) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return (Float) finalProduct.get(paths[paths.length - 1]);
    }

    public JSONObject getObject(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return (JSONObject) json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return finalProduct;
    }

    public void saveResource(String resourcePath) {

        String absoluteFile = resourcePath.split("../")[resourcePath.split("../").length - 1];
        InputStream in = getResource(absoluteFile);

        File outFile = new File(resourcePath);
        try {
            OutputStream out = new FileOutputStream(outFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public InputStream getResource(String filename) {
        try {
            URL url = Resources.class.getResource(filename);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public JSONObject getInternalConfig() throws IOException, ParseException {
        URLConnection connection = Resources.class.getResource("Config.json").openConnection();
        connection.setUseCaches(false);
        return (JSONObject) new JSONParser().parse(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
    }

    public JSONObject getExternalConfig() {
        return getJson();
    }


    public void putDefaultsIfAbsent() throws IOException, ParseException {
        JSONObject externalConfig = getExternalConfig();
        JSONObject internalConfig = getInternalConfig();

        JSONObject internalCommandsConfig = (JSONObject) internalConfig.get("commands");
        JSONObject externalCommandsConfig = (JSONObject) externalConfig.get("commands");
        for (Object parentObj : internalCommandsConfig.keySet()) {
            if (!externalCommandsConfig.containsKey(parentObj)) {
                externalCommandsConfig.put(parentObj, internalCommandsConfig.get(parentObj));
                continue;
            }

            JSONObject externalParentJSONObject = (JSONObject) externalCommandsConfig.get(parentObj);
            JSONObject internalParentJSONObject = (JSONObject) internalCommandsConfig.get(parentObj);

            for (Object childObj : internalParentJSONObject.keySet()) {
                if (!externalParentJSONObject.containsKey(childObj)) {
                    externalParentJSONObject.put(childObj, internalParentJSONObject.get(childObj));
                }
            }
        }

        JSONObject internalGeneralConfig = (JSONObject) internalConfig.get("general");
        JSONObject externalGeneralConfig = (JSONObject) externalConfig.get("general");
        for (Object parentObj : internalGeneralConfig.keySet()) {
            if (!externalGeneralConfig.containsKey(parentObj))
                externalGeneralConfig.put(parentObj, internalGeneralConfig.get(parentObj));
        }

        JSONObject internalChannelsConfig = (JSONObject) internalConfig.get("channels");
        JSONObject externalChannelsConfig = (JSONObject) externalConfig.get("channels");
        for (Object parentObj : internalChannelsConfig.keySet()) {
            if (!externalChannelsConfig.containsKey(parentObj))
                externalChannelsConfig.put(parentObj, internalChannelsConfig.get(parentObj));
        }

        JSONObject internalRolesConfig = (JSONObject) internalConfig.get("roles");
        JSONObject externalRolesConfig = (JSONObject) externalConfig.get("roles");
        for (Object parentObj : internalRolesConfig.keySet()) {
            if (!externalRolesConfig.containsKey(parentObj))
                externalRolesConfig.put(parentObj, internalRolesConfig.get(parentObj));
        }

        if (!externalConfig.containsKey("movementTracks"))
            externalConfig.put("movementTracks", internalConfig.get("movementTracks"));

        JSONObject internalMessagesConfig = (JSONObject) internalConfig.get("messages");
        JSONObject externalMessagesConfig = (JSONObject) externalConfig.get("messages");
        for (Object parentObj : internalMessagesConfig.keySet()) {
            if (!externalMessagesConfig.containsKey(parentObj))
                externalMessagesConfig.put(parentObj, internalMessagesConfig.get(parentObj));
        }

        JSONObject internalEventsConfig = (JSONObject) internalConfig.get("events");
        JSONObject externalEventsConfig = (JSONObject) externalConfig.get("events");
        for (Object parentObj : internalEventsConfig.keySet()) {
            if (!externalEventsConfig.containsKey(parentObj)) {
                externalEventsConfig.put(parentObj, internalEventsConfig.get(parentObj));
                continue;
            }

            JSONObject externalParentJSONObject = (JSONObject) externalEventsConfig.get(parentObj);
            JSONObject internalParentJSONObject = (JSONObject) internalEventsConfig.get(parentObj);

            for (Object childObj : internalParentJSONObject.keySet()) {
                if (!externalParentJSONObject.containsKey(childObj)) {
                    externalParentJSONObject.put(childObj, internalParentJSONObject.get(childObj));
                }
            }
        }
        JSONObject SuggestCmd = ((JSONObject) externalCommandsConfig.get("suggest"));
        if (SuggestCmd.get("upVoteEmoji").toString().equalsIgnoreCase("?")) {
            SuggestCmd.put("upVoteEmoji", "U+1F44D");
        }
        if (SuggestCmd.get("downVoteEmoji").toString().equalsIgnoreCase("?")) {
            SuggestCmd.put("downVoteEmoji", "U+1F44E");
        }

        JSONObject TicketPanel = ((JSONObject) externalCommandsConfig.get("createTicketPanel"));
        try {
            if (((ArrayList<JSONObject>) TicketPanel.get("ticketOptions")).get(0).get("Emoji").toString().equalsIgnoreCase("?")) {
                ((ArrayList<JSONObject>) TicketPanel.get("ticketOptions")).get(0).put("Emoji", "U+1F39F");
            }
        } catch (Exception ignored) {

        }
        try {
            if (((ArrayList<JSONObject>) TicketPanel.get("ticketOptions")).get(1).get("Emoji").toString().equalsIgnoreCase("?")) {
                ((ArrayList<JSONObject>) TicketPanel.get("ticketOptions")).get(1).put("Emoji", "U+1F514");
            }
        } catch (Exception ignored) {

        }

        JSONObject TicketCreate = ((JSONObject) externalEventsConfig.get("event_ticketCreate"));
        if (TicketCreate.get("adminOnlyEmoji").toString().equalsIgnoreCase("?")) {
            TicketCreate.put("adminOnlyEmoji", "U+1F514");
        }
        if (TicketCreate.get("closeTicketEmoji").toString().equalsIgnoreCase("?")) {
            TicketCreate.put("closeTicketEmoji", "U+1F5D1");
        }
        JSONObject GiveawayCreate = ((JSONObject) externalCommandsConfig.get("gcreate"));
        if (GiveawayCreate.get("giveawayEmoji").toString().equalsIgnoreCase("?")) {
            GiveawayCreate.put("giveawayEmoji", "U+1F389");
        }

        JSONObject Verify = ((JSONObject) externalCommandsConfig.get("createVerifyPanel"));
        if (Verify.get("verifyEmoji").toString().equalsIgnoreCase("?")) {
            Verify.put("verifyEmoji", "U+2705");
        }

        saveJSON();
    }

    public static void reload() {
        JDA jda = initJDA.get().getJda();
        jda.shutdown();
        Timer createJDATimer = new Timer();
        createJDATimer.schedule(createJDALater, 1000);

    }

    public static TimerTask createJDALater = new TimerTask() {
        public void run() {
            initJDA.get().createJDA();
            conf.initGlobalConfig();
            getConfig.get().reloadConfig();
            Timer TaskTimer = new Timer();
            TaskTimer.schedule(runStartupLater, 1000);


        }
    };
    public static TimerTask runStartupLater = new TimerTask() {
        public void run() {
            initJDA.get().registerListeners();
        }
    };
}
