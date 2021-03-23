package main.java.com.i0dev.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ApiUtils {

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
        return getGeneralRequest(method, "https://plugin.tebex.io/", param, "X-Tebex-Secret", conf.TEBEX_SECRET);
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

    public static JSONObject createGiftcard(String amt, String note) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://plugin.tebex.io/gift-cards").openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-Tebex-Secret", conf.TEBEX_SECRET);
            conn.setRequestProperty("amount", amt);
            if (!note.equals("")) {
                conn.setRequestProperty("note", note);
            }
            if (conn.getResponseCode() == 403) {
                return new JSONObject();
            }
            return convertToJSON(conn);

        } catch (MalformedURLException | ProtocolException ignored) {
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

    public static String convertUUID(String s) {
        return s.substring(0, 7) + "-" + s.substring(7, 11) + "-" + s.substring(11, 15) + "-" + s.substring(15);
    }
}
