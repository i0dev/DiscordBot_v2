package com.i0dev.object.engines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.i0dev.InitilizeBot;
import com.i0dev.object.objects.Application;
import com.i0dev.utility.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class ApplicationEngine {

    private static ApplicationEngine instance = new ApplicationEngine();

    public static ApplicationEngine getInstance() {
        return instance;
    }

    @Setter
    @Getter
    private List<Object> cache = new ArrayList<>();

    public void add(Application object) {
        getCache().add(object);
        save();
    }

    public void remove(Application object) {
        getCache().remove(object);
        save();
    }

    public void remove(User user) {
        getCache().remove(getObject(user));
        save();
    }
    public void clear() {
        getCache().clear();
        save();
    }

    public boolean isOnList(User user) {
        return cache.contains(getObject(user));
    }

    public Application getObject(User user) {
        for (Object singleton : getCache()) {
            Application object = (Application) singleton;
            if (object.getUserID().equals(user.getIdLong())) {
                return object;
            }
        }
        return null;
    }

    public void save() {
        FileUtil.saveFile(cache, getPath());
    }

    public String getPath() {
        return InitilizeBot.get().getApplicationsPath();
    }

    public void load(JsonArray array) {
        for (JsonElement element : array) {
            JsonObject jsonObject = element.getAsJsonObject();

            Application application = new Application();
            application.setQuestions(FileUtil.ArrayToStringList(jsonObject.get("questions").getAsJsonArray()));
            application.setAnswers(FileUtil.ArrayToStringList(jsonObject.get("answers").getAsJsonArray()));
            application.setUserID(jsonObject.get("userID").getAsLong());
            application.setUserTag(jsonObject.get("userTag").getAsString());
            application.setTimeSubmitted(jsonObject.get("timeSubmitted").getAsLong());

            getCache().add(application);
        }
    }
}
