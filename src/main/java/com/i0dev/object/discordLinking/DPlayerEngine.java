package com.i0dev.object.discordLinking;

import com.google.gson.JsonObject;
import com.i0dev.InitilizeBot;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class DPlayerEngine {

    private static DPlayerEngine instance = new DPlayerEngine();

    public static DPlayerEngine getInstance() {
        return instance;
    }

    @Setter
    @Getter
    List<Object> cache = new ArrayList<>();

    public void add(DPlayer object) {
        getCache().add(object);
        save(object.getDiscordID());
    }

    public void add(User user) {
        getCache().add(getObject(user));
        save(user);
    }

    public void remove(DPlayer object) {
        getCache().remove(object);
        save(object.getDiscordID());
    }

    public void remove(User user) {
        getCache().remove(getObject(user));
        save(user);
    }

    public boolean isOnList(User user) {
        return cache.contains(getObject(user));
    }


    public DPlayer getObject(User user) {
        for (Object singleton : getCache()) {
            DPlayer object = (DPlayer) singleton;
            if (object.getDiscordID() == user.getIdLong()) {
                return object;
            }
        }

        DPlayer dPlayer = new DPlayer(user);
        Cache dCache = dPlayer.getCachedData();
        dCache.setDiscordTag(user.getAsTag());
        try {
            dCache.setDiscordAvatarURL(user.getEffectiveAvatarUrl());
        } catch (Exception e) {
        }
        dPlayer.addToCache();
        return dPlayer;
    }

    public DPlayer getObjectDontAdd(User user) {
        for (Object singleton : getCache()) {
            DPlayer object = (DPlayer) singleton;
            if (object.getDiscordID() == user.getIdLong()) {
                return object;
            }
        }
        return null;
    }


    public void save(User user) {
        FileUtil.saveFile(getObject(user), getPath() + "/" + user.getId() + ".json");
    }

    public void save(List<Object> objects) {
        for (Object object : objects) {
            FileUtil.saveFile(object, getPath() + "/" + ((DPlayer) object).getDiscordID() + ".json");
        }
    }


    public void save(Long discordID) {
        FileUtil.saveFile(getObject(InternalJDA.get().getJda().getUserById(discordID)), getPath() + "/" + discordID + ".json");
    }

    public void saveAll() {
        File directory = new File(getPath());
        for (String filename : directory.list()) {
            File dFile = new File(getPath() + "/" + filename);
            JsonObject jsonObject = FileUtil.getJsonObject(dFile.getPath());
            save(jsonObject.get("discordID").getAsLong());
        }
    }

    public String getPath() {
        return InitilizeBot.get().getDPlayerDir();
    }

    public void load() {
        File directory = new File(getPath());
        for (String filename : directory.list()) {
            File dFile = new File(getPath() + "/" + filename);
            JsonObject jsonObject = FileUtil.getJsonObject(dFile.getPath());
            DPlayer dPlayer = new DPlayer();
            JsonObject linkingObject = jsonObject.get("linkInfo").getAsJsonObject();

            dPlayer.setDiscordID(jsonObject.get("discordID").getAsLong());
            dPlayer.getLinkInfo().setMinecraftUUID(linkingObject.get("minecraftUUID").getAsString());

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
            } else {
                dPlayer.setPoints(0D);
            }
            if (jsonObject.has("lastBoostTime")) {
                dPlayer.setLastBoostTime(jsonObject.get("lastBoostTime").getAsLong());
            } else {
                dPlayer.setLastBoostTime(0L);
            }
            if (jsonObject.has("boostCount")) {
                dPlayer.setBoostCount(jsonObject.get("boostCount").getAsLong());
            } else {
                dPlayer.setBoostCount(0L);
            }
            getCache().add(dPlayer);
        }
    }

    public TimerTask taskAddToCache = new TimerTask() {
        public void run() {


        }
    };


    public TimerTask taskUpdateUsers = new TimerTask() {
        public void run() {

            for (Object o : cache) {
                DPlayer dPlayer = ((DPlayer) o);

                File dFile = new File(getPath() + "/" + dPlayer.getDiscordID() + ".json");
                JsonObject jsonObject = FileUtil.getJsonObject(dFile.getPath());

                if (dPlayer.getPoints() != jsonObject.get("points").getAsDouble()) {
                    save(dPlayer.getDiscordID());
                }


            }

        }
    };
    //special

    public void increaseWarn(User user) {
        getObject(user).setWarnCount(getObject(user).getWarnCount() + 1);
        save(user);
    }

    public void decreaseWarn(User user) {
        getObject(user).setWarnCount(getObject(user).getWarnCount() - 1);
        save(user);
    }

    public void increaseTicketsClosed(User user) {
        getObject(user).setTicketsClosed(getObject(user).getTicketsClosed() + 1);
        save(user);
    }


    public void increaseInvites(User user) {
        getObject(user).setInviteCount(getObject(user).getInviteCount() + 1);
        save(user);
    }

    public void decreaseInvites(User user) {
        getObject(user).setInviteCount(getObject(user).getInviteCount() - 1);
        save(user);
    }

    public void clearWarns() {
        for (Object singleton : getCache()) {
            DPlayer dPlayer = ((DPlayer) singleton);
            dPlayer.setWarnCount(0);
        }
        saveAll();
    }

    public void clearBlacklist() {
        for (Object singleton : getCache()) {
            DPlayer dPlayer = ((DPlayer) singleton);
            dPlayer.setBlacklisted(false);
        }
        saveAll();
    }

    public void clearTicketsClosed() {
        for (Object singleton : getCache()) {
            DPlayer dPlayer = ((DPlayer) singleton);
            dPlayer.setTicketsClosed(0);
        }
        saveAll();
    }

    public void clearInvites() {
        for (Object singleton : getCache()) {
            DPlayer dPlayer = ((DPlayer) singleton);
            dPlayer.setInviteCount(0);
        }
        saveAll();
    }

    public void updateInviter(User user, User inviter) {
        getObject(user).setInvitedByDiscordID(inviter.getIdLong());
        getObject(user).getCachedData().setInvitedByDiscordTag(inviter.getAsTag());
        getObject(user).getCachedData().setInvitedByDiscordAvatarURL(inviter.getEffectiveAvatarUrl());
        save(user);
    }


    public DPlayer getObjectFromIGN(String ign) {
        for (Object object : getCache()) {
            DPlayer dPlayer = (DPlayer) object;
            if (dPlayer.getCachedData().getMinecraftIGN().equalsIgnoreCase(ign)) {
                return dPlayer;
            }
        }
        return null;
    }

    public DPlayer getObjectFromDiscordID(Long discordID) {
        for (Object object : getCache()) {
            DPlayer dPlayer = (DPlayer) object;
            if (dPlayer.getDiscordID() == discordID) {
                return dPlayer;
            }
        }
        return null;
    }

    public void setLinked(User user, String code, String ign, String UUID) {
        getObject(user).getLinkInfo().setLinkCode(code);
        getObject(user).getLinkInfo().setMinecraftUUID(UUID);
        getObject(user).getCachedData().setMinecraftIGN(ign);
        getObject(user).getLinkInfo().setLinkedTime(System.currentTimeMillis());
        getObject(user).getLinkInfo().setLinked(true);
        save(user);
    }

    public boolean isBlacklisted(User user) {
        DPlayer dPlayer = getObject(user);
        if (dPlayer == null) {
            return false;
        } else {
            return dPlayer.isBlacklisted();
        }
    }


}