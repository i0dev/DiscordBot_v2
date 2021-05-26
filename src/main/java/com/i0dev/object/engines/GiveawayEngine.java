package com.i0dev.object.engines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.i0dev.InitializeBot;
import com.i0dev.object.objects.Giveaway;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GiveawayEngine {

    private static GiveawayEngine instance = new GiveawayEngine();

    public static GiveawayEngine getInstance() {
        return instance;
    }

    @Setter
    @Getter
    private List<Object> cache = new ArrayList<>();

    public void add(Giveaway object) {
        getCache().add(object);
        save();
    }

    public void remove(Giveaway object) {
        getCache().remove(object);
        save();
    }

    public void remove(Message message) {
        getCache().remove(getObject(message));
        save();
    }

    public void setEnded(Giveaway giveaway) {
        remove(giveaway);
        giveaway.setEnded(true);
        add(giveaway);
    }

    public void clear() {
        getCache().clear();
        save();
    }

    public boolean isOnList(Message message) {
        return cache.contains(getObject(message));
    }

    public Giveaway getObject(Message message) {
        for (Object singleton : getCache()) {
            Giveaway object = (Giveaway) singleton;
            if (object.getMessageID().equals(message.getIdLong())) {
                return object;
            }
        }
        return null;
    }

    public Giveaway getObject(String messageID) {
        try {
            Long.parseLong(messageID);
        } catch (Exception ignored) {
            return null;
        }
        for (Object singleton : getCache()) {
            Giveaway object = (Giveaway) singleton;
            if (object.getMessageID().equals(Long.parseLong(messageID))) {
                return object;
            }
        }
        return null;
    }

    public void save() {
        FileUtil.saveFile(cache, getPath());
    }

    public String getPath() {
        return InitializeBot.get().getGiveawaysPath();
    }

    public void load(JsonArray array) {
        for (JsonElement element : array) {
            JsonObject jsonObject = element.getAsJsonObject();

            Giveaway giveaway = new Giveaway();
            giveaway.setChannelID(jsonObject.get("channelID").getAsLong());
            giveaway.setMessageID(jsonObject.get("messageID").getAsLong());
            giveaway.setHostID(jsonObject.get("hostID").getAsLong());
            giveaway.setPrize(jsonObject.get("prize").getAsString());
            giveaway.setEndTime(jsonObject.get("endTime").getAsLong());
            giveaway.setWinnerAmount(jsonObject.get("winnerAmount").getAsLong());
            giveaway.setEnded(jsonObject.get("ended").getAsBoolean());

            getCache().add(giveaway);

            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(9000);
                } catch (InterruptedException ignored) {
                }
                for (Object o : cache) {
                    Giveaway giveaway1 = ((Giveaway) o);
                    try {
                        InternalJDA.getJda().getTextChannelById(giveaway1.getChannelID()).retrieveMessageById(giveaway1.getMessageID()).complete();
                    } catch (Exception ignored) {
                        remove(giveaway1);
                    }
                }
                save();
            });
        }
    }
}
