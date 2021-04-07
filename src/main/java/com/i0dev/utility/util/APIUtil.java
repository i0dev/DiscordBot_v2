package com.i0dev.utility.util;

import com.i0dev.utility.GlobalConfig;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class APIUtil {

    private static JSONObject getGeneralRequest(String method, String url, String param, String HeaderKey, String HeaderValue) {
        try {
            StringBuilder result = new StringBuilder();
            HttpURLConnection conn = (HttpURLConnection) new URL(url + param).openConnection();
            conn.setRequestMethod(method);
            if (!HeaderKey.equals("") && !HeaderValue.equals("")) {
                conn.setRequestProperty(HeaderKey, HeaderValue);
            }
            if (conn.getResponseCode() == 403) {
                return new JSONObject();
            }
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (MalformedURLException | ParseException ignored) {
            return new JSONObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private static JSONObject getGeneralRequest(String method, String url, String param) {
        return getGeneralRequest(method, url, param, "", "");
    }

    private static JSONObject getTebexRequest(String method, String param) {
        return getGeneralRequest(method, "https://plugin.tebex.io/", param, "X-Tebex-Secret", GlobalConfig.TEBEX_SECRET);
    }

    public static JSONObject lookupTransaction(String transID) {
        return getTebexRequest("GET", "payments/" + transID);
    }

    public static JSONObject lookupPackage(String packageID) {
        return getTebexRequest("GET", "package/" + packageID);
    }

    public static JSONObject MinecraftServerLookup(String ipAddress) throws IOException {
        return getGeneralRequest("GET", "https://api.mcsrvstat.us/2/", ipAddress);
    }

    public static JSONObject getInformation() {
        return getTebexRequest("GET", "information");
    }

    private static JSONObject convertToJSON(HttpURLConnection connection) {
        try {
            StringBuilder result = new StringBuilder();
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private static JSONObject convertToJSON(InputStream stream) {
        try {
            StringBuilder result = new StringBuilder();
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONObject createGiftcard(String amt, String note) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://plugin.tebex.io/gift-cards").openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("X-Tebex-Secret", GlobalConfig.TEBEX_SECRET);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            String o;
            if (note.equals("")) {
                o = "{\"amount\": \"" + amt + "\"}";
            } else {
                o = "{\"amount\": " + amt + ",\"note\":\"" + note + "\"}";
            }
            conn.getOutputStream().write(o.getBytes());
            if (conn.getResponseCode() == 400) {
                JSONObject ob = new JSONObject();
                ob.put("error", "2");
                return ob;
            }
            System.out.println(convertToJSON(conn).toJSONString());
            return convertToJSON(conn);

        } catch (MalformedURLException | ProtocolException ignored) {
            ignored.printStackTrace();
            return new JSONObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONObject lookupUser(String UUID) {
        return getTebexRequest("GET", "user/" + UUID);
    }

    public static String getUUIDFromIGN(String ign) {
        return convertUUID(getGeneralRequest("GET", "https://api.mojang.com/users/profiles/minecraft/", ign).get("id").toString());
    }

    public static String getIGNFromUUID(String uuid) {
        return getGeneralRequest("GET", "https://sessionserver.mojang.com/session/minecraft/profile/", uuid).get("name").toString();
    }

    public static void refreshAPICache(String uuid) {
        getGeneralRequest("GET", "https://crafatar.com/renders/head/", uuid);
    }

    public static String convertUUID(String s) {
        return s.substring(0, 7) + "-" + s.substring(7, 11) + "-" + s.substring(11, 15) + "-" + s.substring(15);
    }
}
