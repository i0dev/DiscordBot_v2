package com.i0dev.object.discordLinking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LinkInfo {

    private String linkCode = "";
    private long linkedTime = 0;
    private String minecraftUUID = "";
    private boolean linked = false;

    public LinkInfo(String UUID) {
        this.minecraftUUID = UUID;
    }

}
