package com.i0dev;

import com.i0dev.commands.discord.completedModules.giveaway.cache.GiveawayCache;
import com.i0dev.modules.creators.caches.PollCache;
import com.i0dev.modules.giveaway.giveawayHandler;
import com.i0dev.modules.linking.RoleRefreshHandler;
import com.i0dev.modules.other.FactionsTopHandler;
import com.i0dev.modules.points.EventHandler;
import com.i0dev.object.discordLinking.Cache;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.GiveawayEngine;
import com.i0dev.object.objects.Giveaway;
import com.i0dev.object.objects.LogObject;
import com.i0dev.object.objects.RoleQueueObject;
import com.i0dev.object.objects.Type;
import com.i0dev.utility.*;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.TimeUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PUBLIC)
public class Engine {

    static void run() {
        ScheduledExecutorService executorService = InitializeBot.getAsyncService();
        executorService.scheduleAtFixedRate(taskExecuteMemberCountUpdate, 1, 2, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(taskExecuteGiveawayCreator, 1, 30, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskExecutePollCreator, 1, 30, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskUpdateDPlayerCache, 1, 24, TimeUnit.HOURS);
        executorService.scheduleAtFixedRate(taskPointsCheckVoiceChannels, 1, 2, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskExecuteGiveaways, 1, 10, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskAppendToFile, 1, 10, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskUpdateActivity, 1, 30, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskVerifyAuthentication, 15, 15 * 60, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskUpdateGiveawayTimes, 15, 30, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskAutoUpdateConfig, 60, 60, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskSendFTOP, 45, 45, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskFlushDPLayers, 1, 30, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(taskGiveContinuousRoles, 1, 60, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(taskExecuteRoleQueue, 1, 2, TimeUnit.SECONDS);
    }

    @Getter
    private static final ArrayList<RoleQueueObject> roleQueueList = new ArrayList<>();

    static Runnable taskExecuteRoleQueue = () -> {
        try {
            if (roleQueueList.isEmpty()) return;
            Guild guild = GlobalConfig.GENERAL_MAIN_GUILD;
            RoleQueueObject queueObject = roleQueueList.get(0);
            roleQueueList.remove(0);

            User user = guild.getJDA().getUserById(queueObject.getUserID());
            Role role = guild.getRoleById(queueObject.getRoleID());

            if (user == null || role == null) return;

            Member member = guild.getMemberById(user.getId());

            if (queueObject.getType().equals(Type.ADD_ROLE)) {
                if (member.getRoles().contains(role)) return;
                guild.addRoleToMember(user.getId(), role).queue();
                System.out.println("[LOG] Applied the role {" + role.getName() + "} to the user: {" + member.getEffectiveName() + "}");
            } else if (queueObject.getType().equals(Type.REMOVE_ROLE)) {
                if (!member.getRoles().contains(role)) return;
                guild.removeRoleFromMember(user.getId(), role).queue();
                System.out.println("[LOG] Removed the role {" + role.getName() + "} to the user: {" + member.getEffectiveName() + "}");
            }

        } catch (Exception ignored) {

        }
    };

    static Runnable taskExecuteGiveaways = () -> {
        if (GiveawayEngine.getInstance().getCache().isEmpty()) return;
        for (Object singleton : GiveawayEngine.getInstance().getCache()) {
            Giveaway giveaway = (Giveaway) singleton;
            if (giveaway.isEnded()) continue;
            giveawayHandler.endGiveawayFull(giveaway, false, false, false, null);
        }
    };

    static Runnable taskExecuteGiveawayCreator = () -> {
        if (GiveawayCache.get().getTimeoutMap().isEmpty()) return;
        GiveawayCache.get().getTimeoutMap().forEach((user, timout) -> {
            if (System.currentTimeMillis() > (long) timout) {
                try {
                    ((User) user).openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed("Your giveaway creator has timed out due to inactivity.").build()).queue();
                } catch (Exception ignored) {
                }
                GiveawayCache.get().removeUser((User) user);
            }
        });
    };

    static Runnable taskExecutePollCreator = () -> {
        if (PollCache.get().getTimeoutMap().isEmpty()) return;
        PollCache.get().getTimeoutMap().forEach((user, timout) -> {
            if (System.currentTimeMillis() > (long) timout) {
                try {
                    ((User) user).openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed("Your poll creator has timed out due to inactivity.").build()).queue();
                } catch (Exception ignored) {
                }
                PollCache.get().removeUser((User) user);
            }
        });
    };

    static Runnable taskExecuteMemberCountUpdate = () -> {
        if (!Configuration.getBoolean("events.memberCounter.memberCountEnabled")) return;
        VoiceChannel channel = GlobalConfig.GENERAL_MAIN_GUILD.getVoiceChannelById(Configuration.getLong("events.memberCounter.memberCountChannelID"));
        if (channel == null) System.out.println("The member counting channel is invalid!");
        channel.getManager().setName(Placeholders.convert(Configuration.getString("events.memberCounter.channelNameFormat"))).queue();
    };

    static Runnable taskUpdateDPlayerCache = () -> {
        List<Long> toSave = new ArrayList<>();
        List<Object> cache = DPlayerEngine.getCache();
        if (cache.isEmpty()) return;
        for (Object o : cache) {
            DPlayer dPlayer = (DPlayer) o;
            long updatePlayerCacheMillis = Configuration.getLong("general.updatePlayerCacheMillis");
            if (dPlayer.getLastUpdatedMillis() + updatePlayerCacheMillis > System.currentTimeMillis()) {
                Cache playerCache = dPlayer.getCachedData();
                User user = InternalJDA.getJda().getUserById(dPlayer.getDiscordID());
                if (user != null) {
                    playerCache.setDiscordTag(user.getAsTag());
                    playerCache.setDiscordAvatarURL(user.getEffectiveAvatarUrl());
                }
                if (dPlayer.getLinkInfo().isLinked() && APIUtil.getIGNFromUUID(dPlayer.getLinkInfo().getMinecraftUUID()) != null) {
                    playerCache.setMinecraftIGN(APIUtil.getIGNFromUUID(dPlayer.getLinkInfo().getMinecraftUUID()));
                }
                User inviter = InternalJDA.getJda().getUserById(dPlayer.getInvitedByDiscordID());
                if (inviter != null) {
                    playerCache.setInvitedByDiscordTag(inviter.getAsTag());
                    playerCache.setInvitedByDiscordAvatarURL(inviter.getEffectiveAvatarUrl());
                }
                dPlayer.setLastUpdatedMillis(System.currentTimeMillis());
                toSave.add(((DPlayer) o).getDiscordID());
            }
        }
        long[] toSaveArray = new long[toSave.size()];
        for (int i = 0; i < toSave.size(); i++) {
            toSaveArray[i] = toSave.get(i);
        }
        DPlayerEngine.save(toSaveArray);
    };

    static Runnable taskFlushDPLayers = () -> {
        System.out.println("[DEBUG] Started auto-saving DPlayers into storage.");
        for (Object o : DPlayerEngine.getCache()) {
            try {
                DPlayer dPlayer = ((DPlayer) o);
                if (dPlayer.getLinkInfo().isLinked()) {
                    RoleRefreshHandler.RefreshUserRank(dPlayer);
                    //  System.out.println("DEBUG: read user " + dPlayer.getCachedData().getMinecraftIGN());
                    Thread.sleep(100);
                    if (!FormatUtil.isUUID(dPlayer.getLinkInfo().getMinecraftUUID())) {
                        UUID newUUID = APIUtil.getUUIDFromIGN(dPlayer.getCachedData().getMinecraftIGN());
                        if (newUUID != null) {
                            dPlayer.getLinkInfo().setMinecraftUUID(newUUID.toString());
                        }
                    }
                }
                dPlayer.save();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    };

    static Runnable taskPointsCheckVoiceChannels = () -> {
        Map<User, Long> voiceChannelCache = EventHandler.getVoiceChannelCache();
        if (voiceChannelCache.isEmpty()) return;
        List<User> toRemove = new ArrayList<>();
        voiceChannelCache.forEach((user, time) -> {
            Member member = GlobalConfig.GENERAL_MAIN_GUILD.getMember(user);
            if (member == null || member.getVoiceState() == null || member.getVoiceState().getChannel() == null || user.isBot()) {
                toRemove.add(user);
            } else {
                double voiceChannelSeconds = Configuration.getDouble("events.pointEvents.giveAmounts.voiceChannelSeconds");
                double voiceChannelXSecondsPoints = Configuration.getDouble("events.pointEvents.giveAmounts.voiceChannelXSecondsPoints");
                if (System.currentTimeMillis() >= time + (voiceChannelSeconds * 1000)) {
                    DPlayer dpLayer = DPlayerEngine.getObject(user.getIdLong());
                    dpLayer.setPoints(dpLayer.getPoints() + voiceChannelXSecondsPoints);
                    voiceChannelCache.put(user, System.currentTimeMillis());
                }
            }
        });
        for (User user : toRemove) {
            voiceChannelCache.remove(user);
        }
    };

    @Getter
    static final List<LogObject> toLog = new LinkedList<>();

    public static Runnable taskAppendToFile = () -> {
        if (toLog.isEmpty()) return;
        List<LogObject> cache = new LinkedList<>(toLog);
        for (LogObject object : cache) {
            try {
                FileWriter fileWriter = new FileWriter(object.getFile(), true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);

                printWriter.println(object.getContent());

                bufferedWriter.close();
                printWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        toLog.removeAll(cache);
    };

    static Runnable taskUpdateActivity = () -> {
        String activity = Placeholders.convert(GlobalConfig.DISCORD_ACTIVITY);
        switch (GlobalConfig.DISCORD_ACTIVITY_TYPE.toLowerCase()) {
            case "watching":
                InternalJDA.getJda().getPresence().setActivity(Activity.watching(activity));
                break;
            case "listening":
                InternalJDA.getJda().getPresence().setActivity(Activity.listening(activity));
                break;
            case "playing":
                InternalJDA.getJda().getPresence().setActivity(Activity.playing(activity));
                break;
        }
    };

    static Runnable taskVerifyAuthentication = () -> {
        if (InternalJDA.getJda() == null || InternalJDA.getJda().getGuildById("773035795023790131") == null) {
            System.out.println("Failed to verify with authentication servers.");
            if (InternalJDA.getJda() != null) InternalJDA.getJda().shutdown();
            InitializeBot.getAsyncService().shutdown();
        }
    };

    static Runnable taskGiveContinuousRoles = () -> {
        System.out.println("[DEBUG] Started giving missing roles to users.");
        List<Long> roles = Configuration.getLongList("events.event_welcome.rolesToContinuouslyGive");
        for (User user : InternalJDA.getJda().getUsers()) {
            for (Long roleID : roles) {
                Role role = InternalJDA.getJda().getRoleById(roleID);
                if (role == null) continue;
                Member member = GlobalConfig.GENERAL_MAIN_GUILD.getMember(user);
                if (member == null || member.getRoles().contains(role)) continue;
                new RoleQueueObject(user.getIdLong(), roleID, Type.ADD_ROLE).add();
            }
        }
    };

    static Runnable taskUpdateGiveawayTimes = () -> {
        for (Object o : GiveawayEngine.getInstance().getCache()) {
            Giveaway giveaway = ((Giveaway) o);
            if (giveaway.isEnded()) continue;
            TextChannel giveawayChannel = InternalJDA.getJda().getTextChannelById(giveaway.getChannelID());
            if (giveawayChannel == null) continue;
            Message message = giveawayChannel.retrieveMessageById(giveaway.getMessageID()).complete();
            if (message == null) continue;
            MessageEmbed messageEmbed = message.getEmbeds().get(0);
            StringBuilder desc = new StringBuilder();
            String giveawayEmojiText = Configuration.getString("modules.giveaway.giveawayEmojiText");
            String createdGiveawayContent = Configuration.getString("modules.giveaway.message.createdGiveawayDesc");

            User host = InternalJDA.getJda().getUserById(giveaway.getHostID());
            desc.append(Placeholders.convert(createdGiveawayContent
                    .replace("{emoji}", giveawayEmojiText)
                    .replace("{winnerCount}", giveaway.getWinnerAmount() + "")
                    .replace("{timeLeft}", TimeUtil.formatTime(giveaway.getEndTime() - System.currentTimeMillis()))
                    .replace("{prize}", giveaway.getPrize()), host));

            EmbedBuilder embed = new EmbedBuilder()
                    .setTimestamp(messageEmbed.getTimestamp())
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                    .setThumbnail(GlobalConfig.EMBED_THUMBNAIL.equals("") ? null : GlobalConfig.EMBED_THUMBNAIL)
                    .setDescription(desc.toString())
                    .setTitle(messageEmbed.getTitle())
                    .setFooter(messageEmbed.getFooter().getText());

            message.editMessage(embed.build()).queue();

        }
    };

    static Runnable taskAutoUpdateConfig = () -> {
        if (!Configuration.getJson().equals(Configuration.getNewJson())) {
            Configuration.reloadConfig();
            InitializeBot.initializeCommands();
        }
    };

    static long lastSentFTopTime = 0;

    static Runnable taskSendFTOP = () -> {
        if (!Configuration.getBoolean("events.autoFtop.enabled")) return;
        List<String> minutes = Configuration.getStringList("general.autoFtopOnMinute");
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.of("America/New_York"));
        if (minutes.contains(time.getMinute() + "") && System.currentTimeMillis() > lastSentFTopTime + (120 * 1000)) {
            MessageEmbed embed = FactionsTopHandler.getFTOPEmbed();
            MessageUtil.sendMessage(GlobalConfig.F_TOP_LOGS_CHANNEL_ID, embed);
            com.massivecraft.factions.task.TaskFactionTopCalculate.get().run();
            lastSentFTopTime = System.currentTimeMillis();
        }
    };
}