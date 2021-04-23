package com.i0dev.engine.minecraft;

import com.i0dev.object.discordLinking.Cache;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.util.APIUtil;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class UpdateCaches {

    public static final long updatePlayerCacheMillis = Configuration.getLong("general.updatePlayerCacheMillis");

    public static TimerTask taskUpdateCacheData = new TimerTask() {
        public void run() {
            List<Object> toSave = new ArrayList<>();
            List<Object> cache = DPlayerEngine.getInstance().getCache();
            if (cache.isEmpty()) return;
            for (Object o : cache) {
                DPlayer dPlayer = (DPlayer) o;
                if (dPlayer.getLastUpdatedMillis() + updatePlayerCacheMillis > System.currentTimeMillis()) {
                    update(dPlayer);
                    toSave.add(o);
                }
            }
            DPlayerEngine.getInstance().save(toSave);
        }
    };

    public static void update(DPlayer dPlayer) {
        Cache playerCache = dPlayer.getCachedData();
        User user = InternalJDA.get().getJda().getUserById(dPlayer.getDiscordID());
        if (user != null) {
            playerCache.setDiscordTag(user.getAsTag());
            playerCache.setDiscordAvatarURL(user.getEffectiveAvatarUrl());
        }
        if (APIUtil.getIGNFromUUID(dPlayer.getLinkInfo().getMinecraftUUID()) != null) {
            playerCache.setMinecraftIGN(APIUtil.getIGNFromUUID(dPlayer.getLinkInfo().getMinecraftUUID()));
        }
        User inviter = InternalJDA.get().getJda().getUserById(dPlayer.getInvitedByDiscordID());
        if (inviter != null) {
            playerCache.setInvitedByDiscordTag(inviter.getAsTag());
            playerCache.setInvitedByDiscordAvatarURL(inviter.getEffectiveAvatarUrl());
        }
        dPlayer.setLastUpdatedMillis(System.currentTimeMillis());
    }

}
