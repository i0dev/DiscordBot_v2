package com.i0dev.commands.discord.completedModules.tebex;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

public class Info {
    private static final String Identifier = "Tebex Info";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.tebex.parts.info.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.tebex.parts.info.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.tebex.parts.info.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.tebex.parts.info.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.tebex.parts.info.message.general");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Tebex Info")) {
            return;
        }
        JSONObject json = (JSONObject) ((APIUtil.getInformation())).get("account");
        String desc = MESSAGE_CONTENT
                .replace("{id}", json.get("id").toString())
                .replace("{domain}", json.get("domain").toString())
                .replace("{name}", json.get("name").toString())
                .replace("{onlineMode}", json.get("online_mode").toString())
                .replace("{gameType}", json.get("game_type").toString())
                .replace("{logEvents}", json.get("log_events").toString());
        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());
    }

}