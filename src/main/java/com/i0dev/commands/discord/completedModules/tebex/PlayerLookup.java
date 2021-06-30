package com.i0dev.commands.discord.completedModules.tebex;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class PlayerLookup {

    private static final String Identifier = "Tebex Lookup";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.tebex.parts.playerLookup.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.tebex.parts.playerLookup.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.tebex.parts.playerLookup.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.tebex.parts.playerLookup.enabled");

    private static final String MESSAGE_CANT_FIND = Configuration.getString("modules.tebex.parts.playerLookup.message.cantFind");
    //public static final String MESSAGE_CONTENT = getConfig.getString("modules.tebex.parts.playerLookup.message.general");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Tebex Lookup")) {
            return;
        }

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), TebexManager.lookup, e.getAuthor());
            return;
        }
        String playerIGN = message[2];
        if (APIUtil.getUUIDFromIGN(playerIGN).equals("")) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CANT_FIND, e.getAuthor());
            return;
        }

        JSONObject json = APIUtil.lookupUser(APIUtil.getUUIDFromIGN(playerIGN).toString());

        ArrayList<JSONObject> payments = (ArrayList<JSONObject>) json.get("payments");
        StringBuilder paymentSection = new StringBuilder();
        for (JSONObject payment : payments) {
            paymentSection.append("ID: `" + payment.get("txn_id") + "` - $" + payment.get("price") + " " + payment.get("currency"));
            paymentSection.append("\n");
        }
        double total = 0;
        for (JSONObject obj : payments) {
            if (!obj.get("currency").toString().equals("USD")) continue;
            total += Double.parseDouble(obj.get("price").toString());
        }
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(GlobalConfig.EMBED_TITLE.equals("") ? null : GlobalConfig.EMBED_TITLE)
                .addField("Minecraft Information", "IGN: ``" + ((JSONObject) json.get("player")).get("username") + "``\nUUID: ``" + APIUtil.getUUIDFromIGN(playerIGN) + "`` ", true)
                .addField("Store Information", "Bans: `" + json.get("banCount") + "`\nChargeback Rate: ``" + json.get("chargebackRate") + "%``\nTotal Spent: `$" + total + " USD`", true)
                .addField("Payment History", paymentSection.toString(), false)
                .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                .setTimestamp(ZonedDateTime.now())
                .setThumbnail("https://crafatar.com/avatars/" + APIUtil.getUUIDFromIGN(playerIGN))
                .setFooter(GlobalConfig.EMBED_FOOTER);
        e.getChannel().sendMessage(embed.build()).queue();
    }

}