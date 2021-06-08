package com.i0dev.modules.boosting;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.util.List;

public class Info extends DiscordCommand {

    public static boolean PERMISSION_STRICT;
    public static boolean PERMISSION_LITE;
    public static boolean PERMISSION_ADMIN;
    public static boolean ENABLED;
    public static String MESSAGE_TITLE;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.boosting.parts.info.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.boosting.parts.info.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.boosting.parts.info.permission.admin");
        ENABLED = Configuration.getBoolean("modules.boosting.parts.info.enabled");

        MESSAGE_TITLE = Configuration.getString("modules.boosting.parts.info.message.title");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Boosting Info")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), BoostingManager.info, e.getAuthor());
            return;
        }

        List<JSONObject> obj = Configuration.getObjectList("modules.boosting.general.rewardOptions");

        StringBuilder desc = new StringBuilder();
        desc.append("**Boosting Information**\n");
        desc.append("By nitro boosting the server you will receive rewards when you do `/boosting claim` in game or `{command}` in discord".replace("{command}", "cmg sn"));
        desc.append("\n\n**Boosting Rewards**\n");
        for (JSONObject object : obj) {
            desc.append(object.get("displayName")).append("\n");
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), EmbedFactory.createEmbed(MESSAGE_TITLE, desc + "").build());
    }
}