package main.java.com.i0dev.command.tebex;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class tebexPlayerLookup extends ListenerAdapter {

    private final String Identifier = "Tebex Lookup";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.tebexLookup.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexLookup.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexLookup.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.tebexLookup.messageTitle");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.tebexLookup.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.tebexLookup.enabled");

    private final String ignError = getConfig.get().getString("commands.tebexLookup.ignError");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            String playerIGN = message[1];
            if (TebexAPI.getUUIDFromIGN(playerIGN).equals("")) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(ignError).build()).queue();
                return;
            }

            JSONObject json = null;
            try {
                json = TebexAPI.lookupUser(TebexAPI.getUUIDFromIGN(playerIGN));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

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
                    .setTitle(MESSAGE_TITLE.replace("{ign}", playerIGN))
                    .addField("Minecraft Information", "IGN: ``" + ((JSONObject) json.get("player")).get("username") + "``\nUUID: ``" + TebexAPI.getUUIDFromIGN(playerIGN) + "`` ", true)
                    .addField("Store Information", "Bans: `" + json.get("banCount") + "`\nChargeback Rate: ``" + json.get("chargebackRate") + "%``\nTotal Spent: `$" + total + " USD`", true)
                    .addField("Payment History", paymentSection.toString(), false)
                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setTimestamp(ZonedDateTime.now())
                    .setThumbnail("https://crafatar.com/avatars/" + TebexAPI.getUUIDFromIGN(playerIGN))
                    .setFooter(conf.EMBED_FOOTER);
            e.getChannel().sendMessage(embed.build()).queue();
        }
    }
}