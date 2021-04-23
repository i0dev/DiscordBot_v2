package com.i0dev.commands.discord.completedModules.tebex.payment;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class Get {

    private static final String Identifier = "Tebex Payment Get";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.tebex.parts.payment.parts.get.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.tebex.parts.payment.parts.get.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.tebex.parts.payment.parts.get.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.tebex.parts.payment.parts.get.enabled");

    //private static final String MESSAGE_CONTENT = getConfig.getString("modules.tebex.parts.package.parts.get.message.general");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Tebex Payment Get")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 3) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PaymentMod.get, e.getAuthor());
            return;
        }
        JSONObject json = APIUtil.lookupTransaction(message[3]);
        if (json == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), PaymentMod.MESSAGE_CANT_FIND, e.getAuthor());
            return;
        }
        ArrayList<JSONObject> packages = (ArrayList<JSONObject>) json.get("packages");

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(GlobalConfig.EMBED_TITLE.equals("") ? null : GlobalConfig.EMBED_TITLE)
                .addField("Purchase Info", "Amount: `" +
                                ((JSONObject) json.get("currency")).get("symbol") + "" + json.get("amount").toString() + " " +
                                ((JSONObject) json.get("currency")).get("iso_4217") +
                                "`\nStatus: `" + json.get("status").toString() + "`\nPlayer IGN: `" + ((JSONObject) json.get("player")).get("name") + "`"
                                + "\nPlayer UUID: `" + APIUtil.convertUUID(((JSONObject) json.get("player")).get("uuid").toString()) + "`",
                        true)
                .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                .setTimestamp(FormatUtil.getZonedDateTimeFromString(json.get("date").toString()))
                .setThumbnail("https://crafatar.com/avatars/" + APIUtil.getUUIDFromIGN(((JSONObject) json.get("player")).get("name").toString()))
                .setFooter("Transaction Date ");

        StringBuilder packagesFormat = new StringBuilder();
        for (JSONObject pkg : packages) {
            packagesFormat.append("ID: `" + pkg.get("id") + "` - ");
            packagesFormat.append("Name: `" + pkg.get("name") + "`\n");
        }
        embed.addField("Packages", packagesFormat.toString(), false);


        e.getChannel().sendMessage(embed.build()).queue();

    }
}