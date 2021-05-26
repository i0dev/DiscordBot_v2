package com.i0dev.utility;

import com.google.gson.JsonObject;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.Cache;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.discordLinking.LinkInfo;
import com.i0dev.utility.util.FileUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PUBLIC)
public class SQLManager {

    @Getter
    static Connection connection;

    static List<String> servers;
    static String username;
    static String password;
    static String address;
    static String database;
    static String port;

    public static void init() {
        servers = Configuration.getStringList("modules.mapPoints.general.servers");
        username = Configuration.getString("database.username");
        password = Configuration.getString("database.password");
        address = Configuration.getString("database.address");
        database = Configuration.getString("database.database");
        port = Configuration.getString("database.port");
    }

    public static List<Long> getDiscordIDS() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT discordID from dplayers");
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
        createTables();
        if (!tables.contains("dPlayers")) {
            InitializeBot.getAsyncService().schedule(() -> {
                try {
                    System.out.println("Migrating data from physical storage to database form. This may take a few seconds...");
                    File directory = new File(InitializeBot.get().getDPlayerDir());
                    long iter = 0;
                    if (directory.list() == null) return;
                    for (String filename : directory.list()) {
                        File dFile = new File(InitializeBot.get().getDPlayerDir() + "/" + filename);
                        DPlayer dPlayer = DPlayerEngine.getDPlayerFromJsonObject(FileUtil.getJsonObject(dFile.getPath()));
                        SQLManager.addToDatabase(dPlayer);
                        iter++;
                    }
                    System.out.println("Completed Migrating [$$] dPlayer files to database form.".replace("$$", iter + ""));
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            }, 4, TimeUnit.SECONDS);
        }
    }

    public static void createTables() throws SQLException {
        String dPlayers = "dPlayers";
        String cachedData = "cachedData";
        String linkInfo = "linkInfo";
        String mapPointsMap = "mapPointsMap";


        String query = "CREATE TABLE IF NOT EXISTS " + dPlayers + " (" +
                "`discordID` BIGINT," +
                "`lastUpdatedMillis` BIGINT," +
                "`invitedByDiscordID` BIGINT," +
                "`ticketsClosed` BIGINT," +
                "`inviteCount` BIGINT," +
                "`warnCount` BIGINT," +
                "`blacklisted` BIT," +
                "`points` DECIMAL(18,6)," +
                "`lastBoostTime` BIGINT," +
                "`boostCount` SMALLINT" +
                ");";
        createTable(query, dPlayers);


        StringBuilder initValues = new StringBuilder();
        for (String server : servers) {
            initValues.append("`").append(server).append("` VARCHAR(50),");
        }
        initValues = new StringBuilder(initValues.substring(0, initValues.length() - 1));

        query = "CREATE TABLE IF NOT EXISTS " + mapPointsMap + " (" +
                "`discordID` BIGINT," + initValues +
                ");";
        createTable(query, mapPointsMap);


        query = "CREATE TABLE IF NOT EXISTS " + cachedData + " (" +
                "`discordID` BIGINT," +
                "`minecraftIGN` VARCHAR(16)," +
                "`discordTag` VARCHAR(50)," +
                "`discordAvatarURL` Varchar(300)," +
                "`invitedByDiscordTag` VARCHAR(50)," +
                "`invitedByDiscordAvatarURL` VARCHAR(300)" +
                ");";
        createTable(query, cachedData);


        query = "CREATE TABLE IF NOT EXISTS " + linkInfo + " (" +
                "`discordID` BIGINT," +
                "`linkCode` VARCHAR(15)," +
                "`linkedTime` BIGINT," +
                "`minecraftUUID` Varchar(300)," +
                "`linked` BIT" +
                ");";
        createTable(query, linkInfo);
    }

    public static void createTable(String query, String table) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query);
        if (table.equalsIgnoreCase("mapPointsMap")) {
            for (String server : servers) {
                if (!getColumns("mapPointsMap").contains(server)) {
                    PreparedStatement preparedStatement = connection.prepareStatement("ALTER TABLE " + table + " ADD COLUMN " + server + " VARCHAR(50) DEFAULT 0;");
                    preparedStatement.execute();
                    System.out.println("Added column " + server + " to map points map");
                }
            }
        }
    }

    public static DPlayer getDPlayer(long discordID) throws SQLException {
        if (discordID == 0) return null;
        PreparedStatement preparedStatement = connection.prepareStatement("select * from dplayers where discordid=?;");
        preparedStatement.setLong(1, discordID);
        ResultSet set = preparedStatement.executeQuery();
        DPlayer dPlayer = new DPlayer();
        dPlayer.setDiscordID(discordID);
        while (set.next()) {
            dPlayer.setLastUpdatedMillis(set.getLong(2));
            dPlayer.setInvitedByDiscordID(set.getLong(3));
            dPlayer.setTicketsClosed(set.getLong(4));
            dPlayer.setInviteCount(set.getLong(5));
            dPlayer.setWarnCount(set.getLong(6));
            dPlayer.setBlacklisted(set.getBoolean(7));
            dPlayer.setPoints(set.getLong(8));
            dPlayer.setLastBoostTime(set.getLong(9));
            dPlayer.setBoostCount(set.getLong(10));
        }
        preparedStatement = connection.prepareStatement("select * from cacheddata where discordid=?;");
        preparedStatement.setLong(1, discordID);
        set = preparedStatement.executeQuery();
        if (set == null || !set.next()) {
            dPlayer.setCachedData(new Cache());
        } else {
            Cache cache = new Cache();
            cache.setMinecraftIGN(set.getString(2));
            cache.setDiscordTag(set.getString(3));
            cache.setDiscordAvatarURL(set.getString(4));
            cache.setInvitedByDiscordTag(set.getString(5));
            cache.setInvitedByDiscordAvatarURL(set.getString(6));
            dPlayer.setCachedData(cache);
        }
        preparedStatement = connection.prepareStatement("select * from linkinfo where discordid=?;");
        preparedStatement.setLong(1, discordID);
        set = preparedStatement.executeQuery();
        if (set == null || !set.next()) {
            dPlayer.setLinkInfo(new LinkInfo());
        } else {
            LinkInfo linkInfo = new LinkInfo();
            linkInfo.setLinkCode(set.getString(2));
            linkInfo.setLinkedTime(set.getLong(3));
            linkInfo.setMinecraftUUID(set.getString(4));
            linkInfo.setLinked(set.getBoolean(5));
            dPlayer.setLinkInfo(linkInfo);
        }
        preparedStatement = connection.prepareStatement("select * from mappointsmap where discordid=?;");
        preparedStatement.setLong(1, discordID);
        set = preparedStatement.executeQuery();
        JsonObject object = new JsonObject();
        while (set.next()) {
            List<String> columns = getColumns("mappointsmap");
            for (int i = 2; i < columns.size(); i++) {
                object.addProperty(columns.get(i), set.getLong(i));
            }
        }

        dPlayer.setMapPointsMap(object);


        return dPlayer;
    }

    public static List<String> getTables() throws SQLException {
        PreparedStatement preparedStatement = connection
                .prepareStatement("SHOW TABLES;");
        ResultSet set = preparedStatement.executeQuery();
        List<String> tables = new ArrayList<>();
        while (set.next()) {
            tables.add(set.getString(1));
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

    public static void addToDatabase(DPlayer dPlayer) throws SQLException {
        if (dPlayerExists(dPlayer.getDiscordID())) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE from dplayers WHERE discordID=?");
            preparedStatement.setLong(1, dPlayer.getDiscordID());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("DELETE from mappointsmap WHERE discordID=?");
            preparedStatement.setLong(1, dPlayer.getDiscordID());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("DELETE from cacheddata WHERE discordID=?");
            preparedStatement.setLong(1, dPlayer.getDiscordID());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("DELETE from linkinfo WHERE discordID=?");
            preparedStatement.setLong(1, dPlayer.getDiscordID());
            preparedStatement.execute();
        }

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO dplayers VALUES " + "(?,?,?,?,?,?,?,?,?,?);");
        preparedStatement.setLong(1, dPlayer.getDiscordID());
        preparedStatement.setLong(2, dPlayer.getLastUpdatedMillis());
        preparedStatement.setLong(3, dPlayer.getInvitedByDiscordID());
        preparedStatement.setLong(4, dPlayer.getTicketsClosed());
        preparedStatement.setLong(5, dPlayer.getInviteCount());
        preparedStatement.setLong(6, dPlayer.getWarnCount());
        preparedStatement.setBoolean(7, dPlayer.isBlacklisted());
        preparedStatement.setBigDecimal(8, BigDecimal.valueOf(dPlayer.getPoints()));
        preparedStatement.setLong(9, dPlayer.getLastBoostTime());
        preparedStatement.setLong(10, dPlayer.getBoostCount());
        preparedStatement.execute();

        preparedStatement = connection.prepareStatement("INSERT INTO linkInfo VALUES " + "(?,?,?,?,?);");
        preparedStatement.setBigDecimal(1, BigDecimal.valueOf(dPlayer.getDiscordID()));
        preparedStatement.setString(2, dPlayer.getLinkInfo().getLinkCode());
        preparedStatement.setLong(3, dPlayer.getLinkInfo().getLinkedTime());
        preparedStatement.setString(4, dPlayer.getLinkInfo().getMinecraftUUID());
        preparedStatement.setBoolean(5, dPlayer.getLinkInfo().isLinked());
        preparedStatement.execute();

        preparedStatement = connection.prepareStatement("INSERT INTO cachedData VALUES " + "(?,?,?,?,?,?);");
        preparedStatement.setBigDecimal(1, BigDecimal.valueOf(dPlayer.getDiscordID()));
        preparedStatement.setString(2, dPlayer.getCachedData().getMinecraftIGN());
        preparedStatement.setString(3, dPlayer.getCachedData().getDiscordTag());
        preparedStatement.setString(4, dPlayer.getCachedData().getDiscordAvatarURL());
        preparedStatement.setString(5, dPlayer.getCachedData().getInvitedByDiscordTag());
        preparedStatement.setString(6, dPlayer.getCachedData().getInvitedByDiscordAvatarURL());
        preparedStatement.execute();

        String questionmarks = "";

        for (String ignored : servers) {
            questionmarks += "?,";
        }
        questionmarks += "?";
        preparedStatement = connection.prepareStatement("INSERT INTO mapPointsMap VALUES " + "(" + questionmarks + ");");
        int one = 1;
        preparedStatement.setLong(one, dPlayer.getDiscordID());
        for (int i = 0; i < servers.size(); i++) {
            preparedStatement.setString(i + 2, dPlayer.getMapPointsMap().has(servers.get(i)) ? dPlayer.getMapPointsMap().get(servers.get(i)).getAsString() : "0");
        }
        preparedStatement.execute();
    }

    public static boolean dPlayerExists(long discordID) throws SQLException {
        String query = "SELECT * from dplayers where discordID=" + discordID + ";";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet.next();
    }

}
