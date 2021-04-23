package com.i0dev.commands.discord.completedModules.tebex.packageTebex;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

public class Get {

    private static final String Identifier = "Tebex Package Get";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.tebex.parts.package.parts.get.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.tebex.parts.package.parts.get.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.tebex.parts.package.parts.get.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.tebex.parts.package.parts.get.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.tebex.parts.package.parts.get.message.general");
    private static final String MESSAGE_CANT_FIND = Configuration.getString("modules.tebex.parts.package.parts.get.message.cantFind");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Tebex Package Get")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 3) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PackageMod.get, e.getAuthor());
            return;
        }
        JSONObject json = APIUtil.lookupPackage(message[3]);
        if (json == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CANT_FIND, e.getAuthor());
            return;
        }
        JSONObject category = (JSONObject) json.get("category");
        String desc = MESSAGE_CONTENT
                .replace("{id}", json.get("id").toString())
                .replace("{name}", json.get("name").toString())
                .replace("{price}", json.get("price").toString())
                .replace("{type}", json.get("type").toString())
                .replace("{disabled}", json.get("disabled").toString())
                .replace("{categoryID}", category.get("id").toString())
                .replace("{categoryName}", category.get("name").toString());
        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());
    }
}