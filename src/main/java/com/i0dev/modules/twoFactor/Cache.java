package com.i0dev.modules.twoFactor;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Cache {
    @Getter
    List<Player> cache = new ArrayList<>();

    @Getter
    List<TwoFactor> twoFactorCache = new ArrayList<>();

    public static Cache instance = new Cache();

    public static Cache getInstance() {
        return instance;
    }


}
