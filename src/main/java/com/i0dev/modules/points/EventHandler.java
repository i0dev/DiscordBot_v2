package com.i0dev.modules.points;

import com.i0dev.Engine;
import com.i0dev.InitializeBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.object.objects.LogObject;
import com.i0dev.utility.util.FormatUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EventHandler extends ListenerAdapter {

    private static final double imageSent = Configuration.getDouble("events.pointEvents.giveAmounts.imageSent");
    private static final double videoSent = Configuration.getDouble("events.pointEvents.giveAmounts.videoSent");
    private static final double messageCharacterModifier = Configuration.getDouble("events.pointEvents.giveAmounts.messageCharacterModifier");
    private static final boolean prevBoostsEffectBoostPoints = Configuration.getBoolean("events.pointEvents.giveAmounts.prevBoostsEffectBoostPoints");
    private static final double boost = Configuration.getDouble("events.pointEvents.giveAmounts.boost");
    private static final double reaction = Configuration.getDouble("events.pointEvents.giveAmounts.reaction");


    @Setter
    @Getter
    private static int boostCount;
    @Setter
    @Getter
    private static long lastUpdateBoost;

    @Override
    public void onGuildUpdateBoostCount(GuildUpdateBoostCountEvent e) {
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (System.currentTimeMillis() < lastUpdateBoost + 5000) {
            setBoostCount(e.getGuild().getBoostCount());
            setLastUpdateBoost(System.currentTimeMillis());
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;


        double pointsToGive = messageCharacterModifier * e.getMessage().getContentRaw().length();
        for (Message.Attachment attachment : e.getMessage().getAttachments()) {
            if (attachment.isImage()) {
                pointsToGive += imageSent;

            } else if (attachment.isVideo()) {
                pointsToGive += videoSent;
            }
        }
        DPlayer dpLayer = DPlayerEngine.getObject(e.getAuthor().getIdLong());
        if (e.getMessage().getType() == MessageType.GUILD_MEMBER_BOOST
                || e.getMessage().getType() == MessageType.GUILD_BOOST_TIER_1
                || e.getMessage().getType() == MessageType.GUILD_BOOST_TIER_2
                || e.getMessage().getType() == MessageType.GUILD_BOOST_TIER_3) {

            int boosts = e.getGuild().getBoostCount() - boostCount;
            if (prevBoostsEffectBoostPoints) {

                int boostMath = 0;
                for (int i = 0; i < boosts; i++) {
                    dpLayer.setBoostCount(dpLayer.getBoostCount() + 1);
                    long numerator = 2;
                    long denominator = dpLayer.getBoostCount();
                    if (dpLayer.getBoostCount() == 0 || dpLayer.getBoostCount() == 1) {
                        numerator = 1;
                        denominator = 1;
                    }
                    double math = boost * numerator / denominator;
                    boostMath += math;
                }
                DPlayerEngine.save(e.getAuthor().getIdLong());

                pointsToGive += boostMath;

                String message1 = ("[{tag}] has received [{points}] for nitro boosting the server [{count}] time(s)."
                        .replace("{tag}", e.getAuthor().getAsTag())
                        .replace("{points}", boostMath + "")
                        .replace("{count}", boosts + ""));
                message1 = "" + FormatUtil.formatDate(System.currentTimeMillis()) + ": " + message1;
                Engine.getToLog().add(new LogObject(message1, new File(InitializeBot.get().getPointLogPath())));
            } else {
                dpLayer.setBoostCount(dpLayer.getBoostCount() + boosts);

                for (int i = 0; i < boosts; i++) {
                    pointsToGive += boost;
                }
                String message1 = ("[{tag}] has received [{points}] for nitro boosting the server [{count}] time(s)."
                        .replace("{tag}", e.getAuthor().getAsTag())
                        .replace("{points}", boost + "")
                        .replace("{count}", boosts + ""));
                message1 = "" + FormatUtil.formatDate(System.currentTimeMillis()) + ": " + message1;
                Engine.getToLog().add(new LogObject(message1, new File(InitializeBot.get().getPointLogPath())));
            }

            setBoostCount(e.getGuild().getBoostCount());
            setLastUpdateBoost(System.currentTimeMillis());
            dpLayer.setPoints(dpLayer.getPoints() + pointsToGive);
            DPlayerEngine.save(e.getAuthor().getIdLong());
        } else {
            dpLayer.setPoints(dpLayer.getPoints() + pointsToGive);
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        DPlayer dpLayer = DPlayerEngine.getObject(e.getUser().getIdLong());
        dpLayer.setPoints(dpLayer.getPoints() + reaction);
    }

    @Getter
    public final static Map<User, Long> voiceChannelCache = new HashMap<>();

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
        if (e.getEntity().getUser().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        voiceChannelCache.put(e.getEntity().getUser(), System.currentTimeMillis());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
        if (e.getEntity().getUser().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        voiceChannelCache.put(e.getEntity().getUser(), System.currentTimeMillis());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
        if (e.getEntity().getUser().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        voiceChannelCache.remove(e.getEntity().getUser());
    }
}
