package com.i0dev.object.discordLinking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.User;

@Getter
@Setter
@ToString
public class DPlayer {

    private Cache cachedData = new Cache();
    private LinkInfo linkInfo = new LinkInfo();
    private long discordID = 0;
    private long lastUpdatedMillis;
    private long invitedByDiscordID = 0;
    private long ticketsClosed = 0;
    private long inviteCount = 0;
    private long warnCount = 0;
    private boolean blacklisted = false;
    private double points = 0.0;
    private long lastBoostTime = 0;
    private long boostCount = 0;
    private long mapPoints = 0;
    private long lastRewardsClaim = 0;
    private boolean claimedReclaim = false;
    private long boostCredits = 0;
    private long rewardsClaimed = 0;


    public DPlayer(User user) {
        this.discordID = user.getIdLong();
        this.lastUpdatedMillis = System.currentTimeMillis();
        this.cachedData = new Cache(user);
    }

    public DPlayer(String UUID, String IGN) {
        this.lastUpdatedMillis = System.currentTimeMillis();
        this.cachedData = new Cache(IGN);
        this.linkInfo = new LinkInfo(UUID);

    }

    public DPlayer() {
        this.lastUpdatedMillis = System.currentTimeMillis();
    }

    public DPlayer add() {
        DPlayerEngine.getCache().add(this);
        this.save();
        return this;
    }

    public DPlayer save() {
        DPlayerEngine.save(this);
        return this;
    }

    public DPlayer remove() {
        DPlayerEngine.getCache().remove(this);
        this.save();
        return this;
    }
}
