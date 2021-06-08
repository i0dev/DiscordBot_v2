package com.i0dev.modules.boosting;

import com.i0dev.InitializeBot;
import com.i0dev.modules.points.EventHandler;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class BoostHandler extends ListenerAdapter {

    @Setter
    @Getter
    public static int boostCountCache;

    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent e) {
        int timesBoosted = e.getGuild().getBoostCount() - boostCountCache;
        long channelID = Configuration.getLong("channels.boostingMessageChannelID");
        String title = Configuration.getString("modules.boosting.message.title");
        String message = Configuration.getString("modules.boosting.message.content")
                .replace("{times}", timesBoosted + "")
                .replace("{s}", timesBoosted > 1 ? "s" : "");
        boolean enabled = Configuration.getBoolean("modules.boosting.general.boostMessageEnabled");
        if (!enabled) return;
        EventHandler.performBoost(boostCountCache, e.getUser());
        MessageUtil.sendMessage(channelID, EmbedFactory.createEmbed(Placeholders.convert(title, e.getUser()), Placeholders.convert(message, e.getUser())).build());
        DPlayer dPlayer = DPlayerEngine.getObject(e.getUser().getIdLong());
        dPlayer.setBoostCount(dPlayer.getBoostCount() + timesBoosted);
        dPlayer.setBoostCredits(dPlayer.getBoostCredits() + timesBoosted);
        dPlayer.save();
        setBoostCountCache(e.getGuild().getBoostCount());
    }


    public void onGuildUpdateBoostCount(GuildUpdateBoostCountEvent e) {
        InitializeBot.getAsyncService().schedule(() -> setBoostCountCache(e.getGuild().getBoostCount()), 5, TimeUnit.SECONDS);
    }


}
