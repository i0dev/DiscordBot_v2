package main.java.com.i0dev.command.discord.tebex;

import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.APIUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.awt.*;
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
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            String playerIGN = message[1];
            if (APIUtil.getUUIDFromIGN(playerIGN).equals("")) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(ignError).build()).queue();
                return;
            }

            JSONObject json = null;
            json = APIUtil.lookupUser(APIUtil.getUUIDFromIGN(playerIGN));

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
}