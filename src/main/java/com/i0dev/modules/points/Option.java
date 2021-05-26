package com.i0dev.modules.points;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Option {
    private long price = 0;
    private String discordDisplayName = "";
    private String ingameDisplayName = "";
    private ArrayList<String> commandsToRun;
    private String discordDescription = "";
    private ArrayList<String> ingameDescription;
    private String itemMaterial;
    private long itemAmount = 0;
    private long itemData = 0;
    private boolean glow = false;

    public Option() {
        this.commandsToRun = new ArrayList<>();
        this.ingameDescription = new ArrayList<>();
    }
}