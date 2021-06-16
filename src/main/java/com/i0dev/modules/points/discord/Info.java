package com.i0dev.modules.points.discord;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

public class Info extends DiscordCommand {

    public static boolean PERMISSION_STRICT;
    public static boolean PERMISSION_LITE;
    public static boolean PERMISSION_ADMIN;
    public static boolean ENABLED;
    public static String MESSAGE_TITLE;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.points.parts.info.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.points.parts.info.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.points.parts.info.permission.admin");
        ENABLED = Configuration.getBoolean("modules.points.parts.info.enabled");
        MESSAGE_TITLE = Configuration.getString("modules.points.parts.info.message.title");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Points Info")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PointsManager.info, e.getAuthor());
            return;
        }

        JSONObject obj = Configuration.getObject("events.pointEvents.giveAmounts");

        StringBuilder desc = new StringBuilder();
        desc.append("**How to obtain points**\n");
        desc.append("**Sending a message:** `{points}` points for each character.\n".replace("{points}", obj.get("messageCharacterModifier") + ""));
        desc.append("**Sending an image:** `{points}` points.\n".replace("{points}", obj.get("imageSent") + ""));
        desc.append("**Sending a video:** `{points}` points.\n".replace("{points}", obj.get("videoSent") + ""));
        desc.append("**Reacting to a message:** `{points}` points.\n".replace("{points}", obj.get("reaction") + ""));
        desc.append("**Being in a voice channel:** `{points}` points for every `{time}` seconds.\n".replace("{time}", obj.get("voiceChannelSeconds") + "").replace("{points}", obj.get("voiceChannelXSecondsPoints") + ""));
        desc.append("**Inviting a user to the discord:** `{points}` points.\n".replace("{points}", obj.get("inviteUser") + ""));
        desc.append("**Invited user leaving the discord:** `-{points}` points.\n".replace("{points}", obj.get("inviteUser") + ""));
        desc.append("\n\n**Boosting**\n");
        if (((boolean) obj.get("prevBoostsEffectBoostPoints"))) {
            desc.append("For every time you boost, you will get less points added for every future boost.");
            desc.append("The starting amount is `{points}` points, and every boost after the first will be less than that.".replace("{points}", obj.get("boost") + ""));
        } else {
            desc.append("The boost amount is `{points}` points.".replace("{points}", obj.get("boost") + ""));
        }
        desc.append("\n\n**Rewards**\n");
        desc.append("If you have your account linked you will be able to use the command `{cmd}` to claim `{points}` points.".replace("{points}", Configuration.getLong("commands.rewards.pointsToGive") + "").replace("{cmd}", DiscordCommandManager.REWARDS_ALIASES.get(0)));
        desc.append("\n\n**Shop**\n");
        desc.append("To spend your points, you can spend them in the shop in game, with the command `/points shop`, or in discord with the command `.points shop`");

        MessageUtil.sendMessage(e.getChannel().getIdLong(), EmbedFactory.createEmbed(MESSAGE_TITLE, desc + "").build());


    }
}