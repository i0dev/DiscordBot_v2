package com.i0dev;

import com.i0dev.engine.minecraft.UpdateCaches;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;

import java.util.List;

public class API {

    public static API instance = new API();

    public static API get() {
        return instance;
    }

    public DPlayer getDPlayer(String ign) {
        return DPlayerEngine.getInstance().getObjectFromIGN(ign);
    }

    public DPlayer getDPlayer(Long discordID) {
        return DPlayerEngine.getInstance().getObjectFromDiscordID(discordID);
    }

    public DPlayer getDPlayer(net.dv8tion.jda.api.entities.User user) {
        return DPlayerEngine.getInstance().getObjectDontAdd(user);
    }

    public List<Object> getDPlayerList() {
        return DPlayerEngine.getInstance().getCache();
    }

    public DPlayerEngine getDPlayerEngine() {
        return DPlayerEngine.getInstance();
    }

    public void updateDPlayerFile(DPlayer dPlayer){
        UpdateCaches.update(dPlayer);
    }


}
