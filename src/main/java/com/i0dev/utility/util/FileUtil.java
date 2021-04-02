package main.java.com.i0dev.utility.util;

import main.java.com.i0dev.DiscordBot;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileUtil {


    public static File createFile(String path) {
        return createFile(path, "");
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
            if (!"".equals(internalResourceName)) {
                saveResource(path, internalResourceName);
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
            URL url = DiscordBot.get().getClass().getClassLoader().getResource(filename);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
