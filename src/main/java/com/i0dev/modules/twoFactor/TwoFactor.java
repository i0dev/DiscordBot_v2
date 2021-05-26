package com.i0dev.modules.twoFactor;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TwoFactor {

    private String code;
    private net.dv8tion.jda.api.entities.User user;
    private org.bukkit.entity.Player player;


    public TwoFactor() {
        this.code = "";
        this.user = null;
        this.player = null;
    }

    public TwoFactor addToCache() {
        Cache.getInstance().getTwoFactorCache().add(this);
        return this;
    }


}
