package com.i0dev.utility.util;

import com.google.gson.*;
import com.i0dev.InitializeBot;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileUtil {


    public static File createFile(String path) {
        return createFile(path, null);
    }

    public static File createDirectory(String path) {
        File folder = new File(path);
        folder.mkdirs();
        return folder;
    }


    public static File createFile(String path, String internalResourceName) {
        try {
            File file = new File(path);
            if (file.exists()) return file;
            String[] folders = path.split("../");
            String finalFileName = folders[folders.length - 1];
            String directoriesToCreate = path.substring(0, path.length() - finalFileName.length());
            new File(directoriesToCreate).mkdirs();
            file.createNewFile();
            if (!"".equals(internalResourceName) && internalResourceName != null) {
                saveResource(path, internalResourceName);
            } else if (internalResourceName != null) {
                Files.write(Paths.get(file.getPath()), "[]".getBytes());
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void saveResource(String resourcePath, String internalFileName) {
        InputStream in = getInternalStream(internalFileName);
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

    public static InputStream getInternalStream(String filename) {
        try {
            URL url = InitializeBot.get().getClass().getClassLoader().getResource(filename);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static String toJSON(List<Object> objects) {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new JsonParser().parse(new Gson().fromJson(new Gson().toJson(objects), JsonArray.class).toString()));
    }

    public static String toJSON(Object object) {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new JsonParser().parse(new Gson().fromJson(new Gson().toJson(object), JsonObject.class).toString()));
    }

    public static List<String> ArrayToStringList(JsonArray array) {
        String[] arrName = new Gson().fromJson(array, String[].class);
        return Arrays.asList(arrName);
    }

    public static void saveFile(List<Object> objects, String path) {
        String content;
        if (objects.isEmpty()) {
            content = "[]";
        } else {
            content = toJSON(objects);
        }
        insertContents(path, content);
    }

    public static void saveFile(Object object, String path) {
        String content;
        content = toJSON(object);
        insertContents(path, content);
    }

    public static void insertContents(String path, String content) {
        try {
            Files.write(Paths.get(path), content.getBytes());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static JsonArray getJsonArray(String path) {
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(reader, JsonArray.class);
    }

    public static JsonObject getJsonObject(String path) {
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(reader, JsonObject.class);
    }

    public static JsonObject getJsonObject(StringBuilder contents) {
        return new Gson().fromJson(contents.toString(), JsonObject.class);
    }
}
