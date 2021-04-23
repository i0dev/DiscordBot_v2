package com.i0dev.object.discordLinking;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Setter
@Getter
public class From_IngameCodeLinker {

    private String code;
    private Player player;

    public From_IngameCodeLinker(Player player, String code) {
        this.code = code;
        this.player = player;
    }
}
