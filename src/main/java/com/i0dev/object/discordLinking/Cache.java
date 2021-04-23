package com.i0dev.object.discordLinking;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

@Setter
@Getter
public class Cache {

    private String minecraftIGN;

    private String discordTag;
    private String discordAvatarURL;

    private String invitedByDiscordTag;
    private String invitedByDiscordAvatarURL;

    public Cache(User user) {
        this.minecraftIGN = "";
        this.discordTag = user.getAsTag();
        this.discordAvatarURL = user.getEffectiveAvatarUrl();
        this.invitedByDiscordAvatarURL = "";
        this.invitedByDiscordTag = "";
    }

    public Cache(String ign) {
        this.minecraftIGN = ign;
        this.discordTag = "";
        this.discordAvatarURL = "";
        this.invitedByDiscordAvatarURL = "";
        this.invitedByDiscordTag = "";
    }

    @Deprecated
    public Cache() {
        this.minecraftIGN = "";
        this.discordTag = "";
        this.discordAvatarURL = "";
        this.invitedByDiscordAvatarURL = "";
        this.invitedByDiscordTag = "";
    }
}
