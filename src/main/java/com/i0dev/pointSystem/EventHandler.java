package com.i0dev.pointSystem;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.LogsFile;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class EventHandler extends ListenerAdapter {


    private static final double imageSent = Configuration.getDouble("events.pointEvents.giveAmounts.imageSent");
    private static final double videoSent = Configuration.getDouble("events.pointEvents.giveAmounts.videoSent");
    private static final double messageCharacterModifier = Configuration.getDouble("events.pointEvents.giveAmounts.messageCharacterModifier");
    private static final boolean prevBoostsEffectBoostPoints = Configuration.getBoolean("events.pointEvents.giveAmounts.prevBoostsEffectBoostPoints");
    private static final double boost = Configuration.getDouble("events.pointEvents.giveAmounts.boost");
    private static final double reaction = Configuration.getDouble("events.pointEvents.giveAmounts.reaction");
    private static final double voiceChannelSeconds = Configuration.getDouble("events.pointEvents.giveAmounts.voiceChannelSeconds");
    private static final double voiceChannelXSecondsPoints = Configuration.getDouble("events.pointEvents.giveAmounts.voiceChannelXSecondsPoints");


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
        DPlayer dpLayer = DPlayerEngine.getInstance().getObject(e.getAuthor());
        if (e.getMessage().getType() == MessageType.GUILD_MEMBER_BOOST
                || e.getMessage().getType() == MessageType.GUILD_BOOST_TIER_1
                || e.getMessage().getType() == MessageType.GUILD_BOOST_TIER_2
                || e.getMessage().getType() == MessageType.GUILD_BOOST_TIER_3) {
            if (prevBoostsEffectBoostPoints) {
                double math = boost * (1D / (dpLayer.getBoostCount() == 0 ? 1D : dpLayer.getBoostCount()));
                pointsToGive += math;
                LogsFile.logPoints(e.getAuthor().getAsTag() + " has received " + math + " points for boosting the server");

            } else {
                pointsToGive += boost;
                LogsFile.logPoints(e.getAuthor().getAsTag() + " has received " + boost + " points for boosting the server");
            }

            dpLayer.setBoostCount(dpLayer.getBoostCount() + 1);
            dpLayer.setPoints(dpLayer.getPoints() + pointsToGive);
            DPlayerEngine.getInstance().save(e.getAuthor());
        } else {
            dpLayer.setPoints(dpLayer.getPoints() + pointsToGive);
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        DPlayer dpLayer = DPlayerEngine.getInstance().getObject(e.getUser());
        dpLayer.setPoints(dpLayer.getPoints() + reaction);
    }

    private final static Map<User, Long> voiceChannelCache = new HashMap<>();

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

    public static TimerTask taskCheckVoice = new TimerTask() {
        @Override
        public void run() {
            if (voiceChannelCache.isEmpty()) return;
            List<User> toRemove = new ArrayList<>();
            voiceChannelCache.forEach((user, time) -> {
                Member member = GlobalConfig.GENERAL_MAIN_GUILD.getMember(user);
                if (member.getVoiceState().getChannel() == null || user.isBot()) {
                    toRemove.add(user);
                } else {
                    if (System.currentTimeMillis() >= time + (voiceChannelSeconds * 1000)) {
                        DPlayer dpLayer = DPlayerEngine.getInstance().getObject(user);
                        dpLayer.setPoints(dpLayer.getPoints() + voiceChannelXSecondsPoints);
                        LogsFile.logPoints(user.getAsTag() + " has received " + voiceChannelXSecondsPoints + " points for sitting in a call for " + voiceChannelSeconds + " seconds");

                        voiceChannelCache.put(user, System.currentTimeMillis());
                    }
                }
            });
            for (User user : toRemove) {
                voiceChannelCache.remove(user);
            }
        }
    };
}
