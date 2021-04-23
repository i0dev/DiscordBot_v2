package com.i0dev.object.objects;

import com.i0dev.object.engines.GiveawayEngine;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Giveaway {

    private Long channelID;
    private Long messageID;
    private Long hostID;
    private String prize;
    private Long endTime;
    private Long winnerAmount;
    private boolean ended;

    public Giveaway() {
        this.channelID = 0L;
        this.messageID = 0L;
        this.hostID = 0L;
        this.prize = "";
        this.endTime = 0L;
        this.winnerAmount = 0L;
        this.ended = false;
    }

    public void addToCache() {
        GiveawayEngine.getInstance().add(this);
    }

}