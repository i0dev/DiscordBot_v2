package com.i0dev.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.i0dev.InitializeBot;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Configuration {

    @Getter
    @Setter
    private static JSONObject json;

    public static JSONObject getNewJson() {
        JSONObject object = new JSONObject();
        try {
            object = (JSONObject) new JSONParser().parse(new FileReader(InitializeBot.get().getConfigPath()));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static void reloadConfig() {
        setJson(getNewJson());
        absenceCheck(getInternalConfig(), getJson());
        saveJSON();
    }

    public static void absenceCheck(JSONObject internal, JSONObject external) {
        if (json == null) return;
        if (!json.containsKey("general")) return;
        for (Object o : internal.keySet()) {
            external.putIfAbsent(o, internal.get(o));
            if (internal.get(o) instanceof JSONObject) {
                if (o.toString().equalsIgnoreCase("ranksToLink")) continue;
                absenceCheck((JSONObject) internal.get(o), ((JSONObject) external.get(o)));
            }
        }
    }

    public static void saveJSON() {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(json.toJSONString());
        String jsonString = gson.toJson(el);
        try {
            Files.write(Paths.get(InitializeBot.get().getConfigPath()), jsonString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Object getObjectFromPath(String path) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return json.get(paths[0]);
        }
        JSONObject finalProduct = new JSONObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) {
                finalProduct = (JSONObject) json.get(paths[i]);
            } else {
                finalProduct = (JSONObject) finalProduct.get(paths[i]);
            }
        }
        return finalProduct.get(paths[paths.length - 1]);
    }

    public static String getString(String path) {
        return getObjectFromPath(path).toString();
    }

    public static long getLong(String path) {
        return (long) getObjectFromPath(path);
    }

    public static List<String> getStringList(String path) {
        return (List<String>) getObjectFromPath(path);
    }

    public static List<JSONObject> getObjectList(String path) {
        return (List<JSONObject>) getObjectFromPath(path);
    }

    public static List<Long> getLongList(String path) {
        return (List<Long>) getObjectFromPath(path);
    }

    public static boolean getBoolean(String path) {
        return (boolean) getObjectFromPath(path);
    }

    public static double getDouble(String path) {
        return (double) getObjectFromPath(path);
    }

    public static JSONObject getObject(String path) {
        return (JSONObject) getObjectFromPath(path);
    }

    public static JSONObject getInternalConfig() {
        try {
            URLConnection connection = InitializeBot.get().getClass().getClassLoader().getResource("Config.json").openConnection();
            connection.setUseCaches(false);
            return (JSONObject) new JSONParser().parse(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

}
