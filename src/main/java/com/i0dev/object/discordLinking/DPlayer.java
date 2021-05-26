package com.i0dev.object.discordLinking;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

@Getter
@Setter
public class DPlayer {

    private Cache cachedData;
    private LinkInfo linkInfo;

    private long discordID;
    private long lastUpdatedMillis;
    private long invitedByDiscordID;
    private long ticketsClosed;
    private long inviteCount;
    private long warnCount;
    private boolean blacklisted;

    private double points;
    private long lastBoostTime;
    private long boostCount;

    private JsonObject mapPointsMap;


    public DPlayer(User user) {
        this.blacklisted = false;
        this.discordID = user.getIdLong();
        this.lastUpdatedMillis = System.currentTimeMillis();
        this.ticketsClosed = 0L;
        this.inviteCount = 0L;
        this.warnCount = 0L;
        this.invitedByDiscordID = 0L;
        this.cachedData = new Cache(user);
        this.linkInfo = new LinkInfo();

        this.points = 0D;
        this.lastBoostTime = 0L;
        this.boostCount = 0L;
        mapPointsMap = new JsonObject();


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
        this.linkInfo = new LinkInfo(UUID);

        this.points = 0D;
        this.lastBoostTime = 0L;
        this.boostCount = 0L;
        mapPointsMap = new JsonObject();

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
        this.linkInfo = new LinkInfo();

        this.points = 0D;
        this.lastBoostTime = 0L;
        this.boostCount = 0L;
        mapPointsMap = new JsonObject();

    }

    public DPlayer add() {
        DPlayerEngine.getCache().add(this);
        this.save();
        return this;
    }

    public DPlayer save() {
        DPlayerEngine.save(this.getDiscordID());
        return this;
    }

    public DPlayer remove() {
        DPlayerEngine.getCache().remove(this);
        this.save();
        return this;
    }

    @Override
    public String toString() {
        return "DPlayer{" +
                "cachedData=" + cachedData + "\n" +
                ", linkInfo=" + linkInfo + "\n" +
                ", discordID=" + discordID + "\n" +
                ", lastUpdatedMillis=" + lastUpdatedMillis + "\n" +
                ", invitedByDiscordID=" + invitedByDiscordID + "\n" +
                ", ticketsClosed=" + ticketsClosed + "\n" +
                ", inviteCount=" + inviteCount + "\n" +
                ", warnCount=" + warnCount + "\n" +
                ", blacklisted=" + blacklisted + "\n" +
                ", points=" + points + "\n" +
                ", lastBoostTime=" + lastBoostTime + "\n" +
                ", boostCount=" + boostCount + "\n" +
                ", mapPointsMap=" + mapPointsMap +
                '}';
    }
}
