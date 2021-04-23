package com.i0dev.object.discordLinking;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

@Getter
@Setter
public class DPlayer {

    private Cache cachedData;
    private Linking linkInfo;

    private long discordID;
    private long lastUpdatedMillis;
    private long invitedByDiscordID;
    private long ticketsClosed;
    private long inviteCount;
    private long warnCount;
    private boolean blacklisted;

    //new
    private double points;
    private long lastBoostTime;
    private long boostCount;


    public DPlayer(User user) {
        this.blacklisted = false;
        this.discordID = user.getIdLong();
        this.lastUpdatedMillis = System.currentTimeMillis();
        this.ticketsClosed = 0L;
        this.inviteCount = 0L;
        this.warnCount = 0L;
        this.invitedByDiscordID = 0L;
        this.cachedData = new Cache(user);
        this.linkInfo = new Linking();

        this.points = 0D;
        this.lastBoostTime = 0L;
        this.boostCount = 0L;
    }

    public DPlayer(String UUID, String IGN) {
        this.blacklisted = false;
        this.discordID = 0L;
        this.lastUpdatedMillis = System.currentTimeMillis();
        this.ticketsClosed = 0L;
        this.inviteCount = 0L;
        this.warnCount = 0L;
        this.invitedByDiscordID = 0L;
        this.cachedData = new Cache(IGN);
        this.linkInfo = new Linking(UUID);

        this.points = 0D;
        this.lastBoostTime = 0L;
        this.boostCount = 0L;
    }

    @Deprecated
    public DPlayer() {
        this.discordID = 0L;
        this.blacklisted = false;
        this.lastUpdatedMillis = System.currentTimeMillis();
        this.ticketsClosed = 0L;
        this.inviteCount = 0L;
        this.warnCount = 0L;
        this.invitedByDiscordID = 0L;
        this.cachedData = new Cache();
        this.linkInfo = new Linking();

        this.points = 0D;
        this.lastBoostTime = 0L;
        this.boostCount = 0L;
    }

    public DPlayer addToCache() {
        DPlayerEngine.getInstance().add(this);
        return this;
    }

    public DPlayer save() {
        DPlayerEngine.getInstance().save(this.getDiscordID());
        return this;
    }


}
