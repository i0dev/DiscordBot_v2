package com.i0dev.utility;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.util.FormatUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PapiPlaceholders extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "discordbot";
    }

    @Override
    public String getAuthor() {
        return "i01";
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null) return "";
        DPlayer dPlayer = DPlayerEngine.getObjectFromIGN(p.getName());

        if (params.equals("points")) {
            return dPlayer == null ? "0" : dPlayer.getPoints() + "";
        }
        if (params.equals("pointsRounded")) {
            return dPlayer == null ? "0" : FormatUtil.formatValueSuffix(Math.round(dPlayer.getPoints())) + "";
        }

        if (params.startsWith("mapPoints_")) {
            return dPlayer == null ? "0" : dPlayer.getMapPointsMap().get(params.substring("mapPoints_".length())).getAsString();
        }
        return super.onPlaceholderRequest(p, params);
    }
}
