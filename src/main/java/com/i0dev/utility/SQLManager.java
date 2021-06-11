package com.i0dev.utility;

import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.objects.FieldPair;
import com.i0dev.utility.util.FileUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PUBLIC)
public class SQLManager {

    @Getter
    static Connection connection;
    @Getter
    public static List<FieldPair> cachedNames = new ArrayList<>();

    static String username;
    static String password;
    static String address;
    static String database;
    static String port;

    public static void init() {
        username = Configuration.getString("database.username");
        password = Configuration.getString("database.password");
        address = Configuration.getString("database.address");
        database = Configuration.getString("database.database");
        port = Configuration.getString("database.port");
    }

    public static List<Long> getDiscordIDS(String key, Class<?> clazz) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT " + key + " from " + clazz.getSimpleName());
        ResultSet set = statement.executeQuery();
        List<Long> list = new ArrayList<>();
        while (set.next()) {
            list.add(set.getLong(1));
        }
        return list;
    }

    public static void connect() throws SQLException {
        String url = "jdbc:mysql://" + address + ":" + port + "/" + database;
        connection = DriverManager.getConnection(url, username, password);
        System.out.println("Connected to the [$$] database successfully!".replace("$$", database));
    }

    public static void migrateData() throws SQLException {
        List<String> tables = getTables();
        Statement statement = SQLManager.getConnection().createStatement();
        statement.execute(getTableCreateQuery(DPlayer.class));
        if (!tables.contains(DPlayer.class.getSimpleName().toLowerCase())) {
            Executors.newScheduledThreadPool(30).schedule(() -> {
                try {
                    System.out.println("Migrating data from physical storage to database form. This may take a few seconds...");
                    File directory = new File(InitializeBot.get().getDPlayerDir());
                    long iter = 0;
                    if (directory.list() == null) return;
                    for (String filename : directory.list()) {
                        File dFile = new File(InitializeBot.get().getDPlayerDir() + "/" + filename);
                        DPlayer dPlayer = DPlayerEngine.getDPlayerFromJsonObject(FileUtil.getJsonObject(dFile.getPath()));
                        save(dPlayer, "discordID", dPlayer.getDiscordID());
                        iter++;
                    }
                    System.out.println("Completed Migrating [$$] dPlayer files to database form.".replace("$$", iter + ""));
                } catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException throwable) {
                    throwable.printStackTrace();
                }
            }, 4, TimeUnit.SECONDS);
        }
    }

    public static DPlayer makeDPlayerObject(long id, String key, Class<? extends DPlayer> clazz) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ResultSet result = connection.createStatement().executeQuery("select * from " + clazz.getSimpleName() + " where " + key + "=" + id + ";");
        int iter = 0;
        result.next();
        DPlayer dPlayer = new DPlayer();
        for (Field field : clazz.getDeclaredFields()) {
            iter++;
            String type = field.getType().getTypeName();
            switch (type) {
                case "long":
                    dPlayer.getClass().getMethod(getSetMethodName(field), long.class).invoke(dPlayer, result.getLong(iter));
                    break;
                case "java.lang.String":
                    dPlayer.getClass().getMethod(getSetMethodName(field), String.class).invoke(dPlayer, result.getString(iter));
                    break;
                case "boolean":
                    dPlayer.getClass().getMethod(getSetMethodName(field), boolean.class).invoke(dPlayer, result.getBoolean(iter));
                    break;
                case "double":
                    dPlayer.getClass().getMethod(getSetMethodName(field), double.class).invoke(dPlayer, result.getDouble(iter));
                    break;
                default:
                    Object object = getObject(field, result, (iter - 1));
                    iter += field.getType().getDeclaredFields().length - 1;
                    dPlayer.getClass().getMethod(getSetMethodName(field), object.getClass()).invoke(dPlayer, object);
            }
        }
        result.close();
        return dPlayer;
    }

    static Object getObject(Field field, ResultSet result, int iter) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Object object = field.getType().newInstance();
        for (Field declaredField : field.getType().getDeclaredFields()) {
            iter++;
            switch (declaredField.getType().getName()) {
                case "long":
                    object.getClass().getMethod(getSetMethodName(declaredField), declaredField.getType()).invoke(object, result.getLong(iter));
                    break;
                case "java.lang.String":
                    object.getClass().getMethod(getSetMethodName(declaredField), declaredField.getType()).invoke(object, result.getString(iter));
                    break;
                case "boolean":
                    object.getClass().getMethod(getSetMethodName(declaredField), declaredField.getType()).invoke(object, result.getBoolean(iter));
                    break;
                case "double":
                    object.getClass().getMethod(getSetMethodName(declaredField), declaredField.getType()).invoke(object, result.getDouble(iter));
                    break;
                default:
                    Object innerObject = getObject(declaredField, result, iter);
                    object.getClass().getMethod(getSetMethodName(declaredField), innerObject.getClass()).invoke(object, innerObject);
            }
        }
        return object;
    }

    public static String getSetMethodName(Field field) {
        return "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }

    public static String getGetMethodName(Field field) {
        return "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }

    public static String getIsMethodName(Field field) {
        return "is" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }

    public static List<String> getTables() throws SQLException {
        PreparedStatement preparedStatement = connection
                .prepareStatement("SHOW TABLES;");
        ResultSet set = preparedStatement.executeQuery();
        List<String> tables = new ArrayList<>();
        while (set.next()) {
            tables.add(set.getString(1).toLowerCase());
        }
        return tables;
    }

    public static List<String> getColumns(String table) throws SQLException {
        PreparedStatement preparedStatement = connection
                .prepareStatement("SHOW COLUMNS FROM " + table + ";");
        ResultSet set = preparedStatement.executeQuery();
        List<String> columns = new ArrayList<>();
        while (set.next()) {
            columns.add(set.getString(1));
        }
        return columns;
    }

    public static void save(Object object, String key, long id) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            Class<?> clazz = object.getClass();
            connection.createStatement().execute("DELETE FROM " + clazz.getSimpleName() + " WHERE " + key + "=" + id + ";");
            StringBuilder toQ = new StringBuilder();
            for (Field field : clazz.getDeclaredFields()) {
                String type = field.getType().getTypeName();
                switch (type) {
                    case "long":
                    case "double":
                        toQ.append(clazz.getMethod(getGetMethodName(field)).invoke(object)).append(",");
                        break;
                    case "java.lang.String":
                        toQ.append("'").append(clazz.getMethod(getGetMethodName(field)).invoke(object)).append("',");
                        break;
                    case "boolean":
                        toQ.append(((boolean) clazz.getMethod(getIsMethodName(field)).invoke(object)) ? 1 : 0).append(",");
                        break;
                    default:
                        for (Field declaredField : field.getType().getDeclaredFields()) {
                            Class<?> inClass = declaredField.getType();
                            String inType = inClass.getTypeName();
                            Object obj = object.getClass().getMethod(getGetMethodName(field)).invoke(object);
                            switch (inType) {
                                case "long":
                                    toQ.append(obj.getClass().getMethod(getGetMethodName(declaredField)).invoke(obj)).append(",");
                                    break;
                                case "double":
                                    DecimalFormat decimalFormat = new DecimalFormat("#.#####");
                                    toQ.append(decimalFormat.format(obj.getClass().getMethod(getGetMethodName(declaredField)).invoke(obj))).append(",");
                                    break;
                                case "java.lang.String":
                                    toQ.append("'").append(obj.getClass().getMethod(getGetMethodName(declaredField)).invoke(obj)).append("',");
                                    break;
                                case "boolean":
                                    toQ.append(((boolean) obj.getClass().getMethod(getIsMethodName(declaredField)).invoke(obj)) ? 1 : 0).append(",");
                                    break;
                            }
                        }
                }
            }
            connection.createStatement().execute("INSERT INTO " + clazz.getSimpleName() + " VALUES(" + toQ.substring(0, toQ.length() - 1) + ");");
        } catch (Exception eee) {
            eee.printStackTrace();
        }
    }

    public static void absenceCheck(Class<?> clazz) throws SQLException {
        List<String> columns = SQLManager.getColumns(clazz.getSimpleName());
        for (FieldPair name : cachedNames) {
            if (!columns.contains(name.getName())) {
                String line = name.getQuery();
                String query = "ALTER TABLE " + clazz.getSimpleName() + " " +
                        "ADD COLUMN " + line.substring(0, line.length() - 1) + ";";
                Statement statement = SQLManager.getConnection().createStatement();
                statement.execute(query);
            }
        }
        cachedNames = new ArrayList<>();
    }

    public static String getTableCreateQuery(Class<?> clazz) {
        List<String> list = new ArrayList<>();
        for (Field declaredField : DPlayer.class.getDeclaredFields()) {
            list.addAll(getColumnLines(declaredField));
        }
        int lastIndex = list.size() - 1;
        String lastItem = list.get(lastIndex);
        list.remove(lastIndex);
        list.add(lastItem.substring(0, lastItem.length() - 1));

        StringBuilder toQ = new StringBuilder();
        for (String s : list) {
            toQ.append(s);
        }
        return "CREATE TABLE IF NOT EXISTS " + clazz.getSimpleName() + " (" + toQ + ");";
    }

    public static List<String> getColumnLines(Class<?> clazz) {
        List<String> ret = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            String type = declaredField.getType().getTypeName();
            String name = declaredField.getName();
            switch (type) {
                case "long":
                    ret.add("`" + clazz.getSimpleName() + "_" + name + "` BIGINT NOT NULL DEFAULT 0,");
                    cachedNames.add(new FieldPair(clazz.getSimpleName() + "_" + name, "`" + clazz.getSimpleName() + "_" + name + "` BIGINT NOT NULL DEFAULT 0,"));
                    break;
                case "double":
                    ret.add("`" + clazz.getSimpleName() + "_" + name + "` DOUBLE(16,10) NOT NULL DEFAULT 0,");
                    cachedNames.add(new FieldPair(clazz.getSimpleName() + "_" + name, "`" + clazz.getSimpleName() + "_" + name + "` DOUBLE(16,10) NOT NULL DEFAULT 0,"));
                    break;
                case "java.lang.String":
                    ret.add("`" + clazz.getSimpleName() + "_" + name + "` VARCHAR(300) NOT NULL DEFAULT '',");
                    cachedNames.add(new FieldPair(clazz.getSimpleName() + "_" + name, "`" + clazz.getSimpleName() + "_" + name + "` VARCHAR(300) NOT NULL DEFAULT '',"));
                    break;
                case "boolean":
                    ret.add("`" + clazz.getSimpleName() + "_" + name + "` BIT NOT NULL DEFAULT 0,");
                    cachedNames.add(new FieldPair(clazz.getSimpleName() + "_" + name, "`" + clazz.getSimpleName() + "_" + name + "` BIT NOT NULL DEFAULT 0,"));
                    break;
            }
        }
        return ret;
    }

    public static List<String> getColumnLines(Field field) {
        String type = field.getType().getTypeName();
        String name = field.getName();
        List<String> ret = new ArrayList<>();
        switch (type) {
            case "long":
                ret.add("`" + name + "` BIGINT NOT NULL DEFAULT 0,");
                cachedNames.add(new FieldPair(name, "`" + name + "` BIGINT NOT NULL DEFAULT 0,"));
                break;
            case "double":
                ret.add("`" + name + "` DOUBLE(16,10) NOT NULL DEFAULT 0,");
                cachedNames.add(new FieldPair(name, "`" + name + "` DOUBLE(16,10) NOT NULL DEFAULT 0,"));
                break;
            case "java.lang.String":
                ret.add("`" + name + "` VARCHAR(300) NOT NULL DEFAULT '',");
                cachedNames.add(new FieldPair(name, "`" + name + "` VARCHAR(300) NOT NULL DEFAULT '',"));
                break;
            case "boolean":
                ret.add("`" + name + "` BIT NOT NULL DEFAULT 0,");
                cachedNames.add(new FieldPair(name, "`" + name + "` BIT NOT NULL DEFAULT 0,"));
                break;
            default:
                ret.addAll(getColumnLines(field.getType()));
        }
        return ret;
    }

}