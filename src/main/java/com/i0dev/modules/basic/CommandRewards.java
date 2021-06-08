package com.i0dev.modules.basic;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.TimeUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandRewards extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static String messageWait;
    public static boolean COMMAND_ENABLED;
    public static long POINTS_TO_GIVE;
    public static long rewardsClaimCooldownMillis;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.rewards.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.rewards.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.rewards.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.rewards.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.rewards.enabled");
        POINTS_TO_GIVE = Configuration.getLong("commands.rewards.pointsToGive");
        rewardsClaimCooldownMillis = Configuration.getLong("commands.rewards.rewardsClaimCooldownMillis");
        messageWait = Configuration.getString("commands.rewards.messageWait");

    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Rewards")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.REWARDS_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        DPlayer dPlayer = DPlayerEngine.getObject(e.getAuthor().getIdLong());

        if (!dPlayer.getLinkInfo().isLinked()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GlobalConfig.NOT_LINKED, e.getAuthor());
            return;
        }

        if (dPlayer.getLastRewardsClaim() != 0 && System.currentTimeMillis() < dPlayer.getLastRewardsClaim() + rewardsClaimCooldownMillis) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), messageWait
                            .replace("{timeLeft}", TimeUtil.formatTime((dPlayer.getLastRewardsClaim() + rewardsClaimCooldownMillis) - System.currentTimeMillis()))
                    , e.getAuthor());
            return;
        }

        dPlayer.setLastRewardsClaim(System.currentTimeMillis());


        String description = MESSAGE_CONTENT
                .replace("{points}", POINTS_TO_GIVE + "");

        dPlayer.setPoints(dPlayer.getPoints() + POINTS_TO_GIVE);
        dPlayer.setRewardsClaimed(dPlayer.getRewardsClaimed() + 1);
        dPlayer.save();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(description, e.getAuthor())).build()).queue();

    }
}