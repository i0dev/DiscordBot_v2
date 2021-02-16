package main.java.com.i0dev.util;

import main.java.com.i0dev.resources.Resources;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class getConfig {

    JSONObject json;

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
        try {
            Files.write(Paths.get(getFile(FILEPATH).getPath()), json.toJSONString().getBytes());
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

    public void putIfAbsent() {

        for (Object s : json.keySet()) {
            JSONObject object = (JSONObject) s;

        }

    }

}
