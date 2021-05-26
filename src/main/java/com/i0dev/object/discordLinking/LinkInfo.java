package com.i0dev.object.discordLinking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkInfo {

    private String linkCode;
    private long linkedTime;
    private String minecraftUUID;
    private boolean linked;

    public LinkInfo() {
        this.linkCode = "";
        this.linkedTime = 0;
        this.minecraftUUID = "";
        this.linked = false;
    }

    public LinkInfo(String UUID) {
        this.linkCode = "";
        this.linkedTime = 0;
        this.minecraftUUID = UUID;
        this.linked = false;
    }


}
