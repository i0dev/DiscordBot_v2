package com.i0dev.object.objects;

import com.i0dev.object.engines.ScreenshareEngine;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Screenshare {

    private String ign;
    private String reason;
    private Long punisherID;
    private String punisherTag;


    public Screenshare() {
        this.ign = "";
        this.reason = "";
        this.punisherID = 0L;
        this.punisherTag = "";

    }

    public void addToCache() {
        ScreenshareEngine.getInstance().add(this);
    }

}