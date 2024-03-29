package com.i0dev.object.discordLinking;

import com.google.gson.JsonObject;
import com.i0dev.InitializeBot;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.SQLManager;
import com.i0dev.utility.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DPlayerEngine {

    @Setter
    @Getter
    public static List<Object> cache = new ArrayList<>();

    public static DPlayer getObject(long userID) {
        for (Object singleton : getCache()) {
            DPlayer object = (DPlayer) singleton;
            if (object.getDiscordID() == userID) {
                return object;
            }
        }
        DPlayer dPlayer = new DPlayer();
        User user = InternalJDA.getJda().getUserById(userID);
        dPlayer.setDiscordID(userID);
        if (user != null) {
            dPlayer.getCachedData().setDiscordTag(user.getAsTag());
            dPlayer.getCachedData().setDiscordAvatarURL(user.getEffectiveAvatarUrl());
        }
        dPlayer.add();
        return dPlayer;
    }

    public static void save(long... userIDs) {
        for (long userID : userIDs) {
            if (GlobalConfig.USING_DATABASE) {
                try {
                    SQLManager.save(getObject(userID), "discordID", userID);
                } catch (SQLException | NoSuchMethodException | InvocationTargetException | IllegalAccessException throwable) {
                    throwable.printStackTrace();
                }
            } else {
                FileUtil.saveFile(getObject(userID), InitializeBot.get().getDPlayerDir() + "/" + userID + ".json");
            }
        }
    }

    public static void save(DPlayer... dPlayers) {
        for (DPlayer dPlayer : dPlayers) {
            if (GlobalConfig.USING_DATABASE) {
                try {
                    SQLManager.save(dPlayer, "discordID", dPlayer.getDiscordID());
                } catch (SQLException | NoSuchMethodException | InvocationTargetException | IllegalAccessException throwable) {
                    throwable.printStackTrace();
                }
            } else {
                FileUtil.saveFile(dPlayer, InitializeBot.get().getDPlayerDir() + "/" + dPlayer.getDiscordID() + ".json");
            }
        }
    }

    public static void saveAll() {
        if (GlobalConfig.USING_DATABASE) {
            try {
                for (Long discordID : SQLManager.getDiscordIDS("discordID", DPlayer.class)) {
                    DPlayer dPlayer = SQLManager.makeDPlayerObject(discordID, "discordID", DPlayer.class);
                    if (dPlayer != null) dPlayer.add();
                }
            } catch (SQLException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            File directory = new File(InitializeBot.get().getDPlayerDir());
            for (String filename : directory.list()) {
                File dFile = new File(InitializeBot.get().getDPlayerDir() + "/" + filename);
                JsonObject jsonObject = FileUtil.getJsonObject(dFile.getPath());
                save(jsonObject.get("discordID").getAsLong());
            }
        }
    }

    public static void load() {
        if (GlobalConfig.USING_DATABASE) {
            try {
                for (Long discordID : SQLManager.getDiscordIDS("discordID", DPlayer.class)) {
                    DPlayer dPlayer = SQLManager.makeDPlayerObject(discordID, "discordID", DPlayer.class);
                    if (dPlayer != null) dPlayer.add();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            File directory = new File(InitializeBot.get().getDPlayerDir());
            for (String filename : directory.list()) {
                File dFile = new File(InitializeBot.get().getDPlayerDir() + "/" + filename);
                try {
                    BufferedReader br = new BufferedReader(new FileReader(dFile));
                    String st;
                    StringBuilder fileContents = new StringBuilder();
                    while ((st = br.readLine()) != null) {
                        fileContents.append(st.replace("�", ""));
                    }
                    DPlayer dPlayer = getDPlayerFromJsonObject(FileUtil.getJsonObject(fileContents));
                    if (dPlayer == null) continue;
                    dPlayer.add();
                    dPlayer.save();
                } catch (Exception e) {
                    System.out.println("There was an error loading the user: " + dFile.getName() + " Please manually edit their file.");
                }
            }
        }
    }

    public static DPlayer getDPlayerFromJsonObject(JsonObject jsonObject) {
        try {
            DPlayer dPlayer = new DPlayer();
            JsonObject linkingObject = jsonObject.get("linkInfo").getAsJsonObject();
            dPlayer.setDiscordID(jsonObject.get("discordID").getAsLong());
            LinkInfo linkInfo = dPlayer.getLinkInfo();
            String uuid = linkingObject.get("minecraftUUID").getAsString();
            linkInfo.setMinecraftUUID(uuid);

            dPlayer.setLastUpdatedMillis(jsonObject.get("lastUpdatedMillis").getAsLong());

            dPlayer.setInviteCount(jsonObject.get("inviteCount").getAsLong());
            dPlayer.setWarnCount(jsonObject.get("warnCount").getAsLong());
            dPlayer.setTicketsClosed(jsonObject.get("ticketsClosed").getAsLong());
            dPlayer.setBlacklisted(jsonObject.get("blacklisted").getAsBoolean());

            dPlayer.setInvitedByDiscordID(jsonObject.get("invitedByDiscordID").getAsLong());
            dPlayer.getLinkInfo().setLinkCode(linkingObject.get("linkCode").getAsString());
            dPlayer.getLinkInfo().setLinkedTime(linkingObject.get("linkedTime").getAsLong());
            dPlayer.getLinkInfo().setLinked(linkingObject.get("linked").getAsBoolean());

            JsonObject cacheObject = jsonObject.get("cachedData").getAsJsonObject();
            Cache cache = new Cache();
            cache.setDiscordTag(cacheObject.get("discordTag").getAsString());
            cache.setMinecraftIGN(cacheObject.get("minecraftIGN").getAsString());
            cache.setDiscordAvatarURL(cacheObject.get("discordAvatarURL").getAsString());
            cache.setInvitedByDiscordTag(cacheObject.get("invitedByDiscordTag").getAsString());
            cache.setInvitedByDiscordAvatarURL(cacheObject.get("invitedByDiscordAvatarURL").getAsString());
            dPlayer.setCachedData(cache);

            //After initial update:
            if (jsonObject.has("points")) {
                dPlayer.setPoints(jsonObject.get("points").getAsDouble());
            }
            if (jsonObject.has("lastBoostTime")) {
                dPlayer.setLastBoostTime(jsonObject.get("lastBoostTime").getAsLong());
            }
            if (jsonObject.has("boostCount")) {
                dPlayer.setBoostCount(jsonObject.get("boostCount").getAsLong());
            }
            if (jsonObject.has("mapPoints")) {
                dPlayer.setMapPoints(jsonObject.get("mapPoints").getAsLong());
            }
            if (jsonObject.has("lastRewardsClaim")) {
                dPlayer.setLastRewardsClaim(jsonObject.get("lastRewardsClaim").getAsLong());
            }
            if (jsonObject.has("rewardsClaimed")) {
                dPlayer.setRewardsClaimed(jsonObject.get("rewardsClaimed").getAsLong());
            }
            if (jsonObject.has("claimedReclaim")) {
                dPlayer.setClaimedReclaim(jsonObject.get("claimedReclaim").getAsBoolean());
            }
            if (jsonObject.has("boostCredits")) {
                dPlayer.setBoostCredits(jsonObject.get("boostCredits").getAsLong());
            }

            return dPlayer;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void increment(long userID, String type) {
        DPlayer dPlayer = getObject(userID);
        switch (type.toLowerCase()) {
            case "invites":
                dPlayer.setInviteCount(dPlayer.getInviteCount() + 1);
            case "ticketsclosed":
                dPlayer.setTicketsClosed(dPlayer.getTicketsClosed() + 1);
            case "warns":
                dPlayer.setWarnCount(dPlayer.getWarnCount() + 1);
        }
    }

    public static void decrease(long userID, String type) {
        DPlayer dPlayer = getObject(userID);
        switch (type.toLowerCase()) {
            case "invites":
                dPlayer.setInviteCount(dPlayer.getInviteCount() - 1);
            case "ticketsclosed":
                dPlayer.setTicketsClosed(dPlayer.getTicketsClosed() - 1);
            case "warns":
                dPlayer.setWarnCount(dPlayer.getWarnCount() - 1);
        }
    }

    public static void clear(String type) {
        for (Object singleton : getCache()) {
            DPlayer dPlayer = ((DPlayer) singleton);
            switch (type) {
                case "invites":
                    dPlayer.setInviteCount(0);
                case "blacklist":
                    dPlayer.setBlacklisted(false);
                case "warns":
                    dPlayer.setWarnCount(0);

            }
        }
    }

    public static void updateInviter(long userID, User inviter) {
        getObject(userID).setInvitedByDiscordID(inviter.getIdLong());
        getObject(userID).getCachedData().setInvitedByDiscordTag(inviter.getAsTag());
        getObject(userID).getCachedData().setInvitedByDiscordAvatarURL(inviter.getEffectiveAvatarUrl());
        save(userID);
    }

    public static DPlayer getObjectFromIGN(String ign) {
        for (Object object : getCache()) {
            DPlayer dPlayer = (DPlayer) object;
            if (dPlayer.getCachedData().getMinecraftIGN().equalsIgnoreCase(ign)) {
                return dPlayer;
            }
        }
        return null;
    }

    public static void setLinked(long userID, String code, String ign, String UUID) {
        getObject(userID).getLinkInfo().setLinkCode(code);
        getObject(userID).getLinkInfo().setMinecraftUUID(UUID);
        getObject(userID).getLinkInfo().setLinkedTime(System.currentTimeMillis());
        getObject(userID).getLinkInfo().setLinked(true);
        getObject(userID).getCachedData().setMinecraftIGN(ign);
        save(userID);
    }
}