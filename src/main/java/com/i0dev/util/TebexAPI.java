package main.java.com.i0dev.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TebexAPI {

    public static JSONObject lookupTransaction(String transID) throws IOException {
        try {
            StringBuilder result = new StringBuilder();
            HttpURLConnection conn = (HttpURLConnection) new URL("https://plugin.tebex.io/payments/" + transID).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Tebex-Secret", conf.TEBEX_SECRET);
            if (conn.getResponseCode() == 403) {
                System.out.println("Tebex Secret is invalid!");
                return null;
            }
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (MalformedURLException | ParseException ignored) {
        }
        return null;
    }

    public static JSONObject lookupPackage(String packageID) throws IOException {
        try {
            StringBuilder result = new StringBuilder();
            HttpURLConnection conn = (HttpURLConnection) new URL("https://plugin.tebex.io/package/" + packageID).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Tebex-Secret", conf.TEBEX_SECRET);
            if (conn.getResponseCode() == 403) {
                System.out.println("Tebex Secret is invalid!");
                return null;
            }
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (MalformedURLException | ParseException ignored) {
        }
        return null;
    }

    public static JSONObject getInformation() throws IOException {
        try {
            StringBuilder result = new StringBuilder();
            HttpURLConnection conn = (HttpURLConnection) new URL("https://plugin.tebex.io/information").openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Tebex-Secret", conf.TEBEX_SECRET);
            if (conn.getResponseCode() == 403) {
                System.out.println("Tebex Secret is invalid!");
                return null;
            }
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (MalformedURLException | ParseException ignored) {
        }
        return null;
    }

    public static JSONObject lookupUser(String UUID) throws IOException {
        try {
            StringBuilder result = new StringBuilder();
            HttpURLConnection conn = (HttpURLConnection) new URL("https://plugin.tebex.io/user/" + UUID).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Tebex-Secret", conf.TEBEX_SECRET);
            if (conn.getResponseCode() == 403) {
                System.out.println("Tebex Secret is invalid!");
                return null;
            }
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (MalformedURLException | ParseException e) {
        }
        return null;
    }

    public static String getUUIDFromIGN(String ign) {
        try {
            StringBuilder result = new StringBuilder();
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + ign).openConnection();
            conn.setRequestMethod("GET");
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            JSONObject json = (JSONObject) new JSONParser().parse(result.toString());
            String UUID = json.get("id").toString();
            return convertUUID(UUID);


        } catch (ParseException | IOException e) {
        }
        return "";
    }

    public static String convertUUID(String s) {
        return s.substring(0, 7) + "-" + s.substring(7, 11) + "-" + s.substring(11, 15) + "-" + s.substring(15);
    }
}
