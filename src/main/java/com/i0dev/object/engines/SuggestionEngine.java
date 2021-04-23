package com.i0dev.object.engines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.i0dev.InitilizeBot;
import com.i0dev.object.objects.Suggestion;
import com.i0dev.utility.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class SuggestionEngine {

    private static SuggestionEngine instance = new SuggestionEngine();

    public static SuggestionEngine getInstance() {
        return instance;
    }

    @Setter
    @Getter
    private List<Object> cache = new ArrayList<>();

    public void add(Suggestion object) {
        getCache().add(object);
        save();
    }

    public void remove(Suggestion object) {
        getCache().remove(object);
        save();
    }

    public void remove(Message message) {
        getCache().remove(getObject(message));
        save();
    }

    public void clear() {
        getCache().clear();
        save();
    }

    public void setAccepted(Suggestion suggestion) {
        remove(suggestion);
        suggestion.setAccepted(true);
        add(suggestion);
    }

    public void setRejected(Suggestion suggestion) {
        remove(suggestion);
        suggestion.setRejected(true);
        add(suggestion);
    }

    public boolean isOnList(Message message) {
        return cache.contains(getObject(message));
    }

    public Suggestion getObject(Message message) {
        for (Object singleton : getCache()) {
            Suggestion object = (Suggestion) singleton;
            if (object.getMessageID().equals(message.getIdLong())) {
                return object;
            }
        }
        return null;
    }

    public void save() {
        FileUtil.saveFile(cache, getPath());
    }

    public String getPath() {
        return InitilizeBot.get().getSuggestionPath();
    }

    public void load(JsonArray array) {
        for (JsonElement element : array) {
            JsonObject jsonObject = element.getAsJsonObject();

            Suggestion suggestion = new Suggestion();
            suggestion.setSuggestion(jsonObject.get("suggestion").getAsString());
            suggestion.setChannelID(jsonObject.get("channelID").getAsLong());
            suggestion.setUserID(jsonObject.get("userID").getAsLong());
            suggestion.setMessageID(jsonObject.get("messageID").getAsLong());
            suggestion.setUserTag(jsonObject.get("userTag").getAsString());
            suggestion.setUserAvatarUrl(jsonObject.get("userAvatarUrl").getAsString());
            suggestion.setAccepted(jsonObject.get("accepted").getAsBoolean());
            suggestion.setRejected(jsonObject.get("rejected").getAsBoolean());

            getCache().add(suggestion);
        }
    }
}
