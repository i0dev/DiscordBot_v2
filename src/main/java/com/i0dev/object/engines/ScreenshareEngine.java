package com.i0dev.object.engines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.i0dev.InitializeBot;
import com.i0dev.object.objects.Screenshare;
import com.i0dev.utility.util.FileUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ScreenshareEngine {

    private static ScreenshareEngine instance = new ScreenshareEngine();

    public static ScreenshareEngine getInstance() {
        return instance;
    }

    @Setter
    @Getter
    private List<Object> cache = new ArrayList<>();

    public void add(Screenshare object) {
        getCache().add(object);
        save();
    }

    public void remove(Screenshare object) {
        getCache().remove(object);
        save();
    }

    public void remove(String ign) {
        getCache().remove(getObject(ign));
        save();
    }
    public void clear() {
        getCache().clear();
        save();
    }

    public boolean isOnList(String ign) {
        return cache.contains(getObject(ign));
    }

    public Screenshare getObject(String ign) {
        for (Object singleton : getCache()) {
            Screenshare object = (Screenshare) singleton;
            if (object.getIgn().equals(ign)) {
                return object;
            }
        }
        return null;
    }

    public void save() {
        FileUtil.saveFile(cache, getPath());
    }

    public String getPath() {
        return InitializeBot.get().getScreensharePath();
    }

    public void load(JsonArray array) {
        for (JsonElement element : array) {
            JsonObject jsonObject = element.getAsJsonObject();

            Screenshare screenshare = new Screenshare();
            screenshare.setIgn(jsonObject.get("ign").getAsString());
            screenshare.setPunisherID(jsonObject.get("punisherID").getAsLong());
            screenshare.setReason(jsonObject.get("reason").getAsString());
            screenshare.setPunisherTag(jsonObject.get("punisherTag").getAsString());

            getCache().add(screenshare);
        }




    }
}
