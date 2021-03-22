package main.java.com.i0dev.command.tebex;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class tebexTransactionLookup extends ListenerAdapter {

    private final String Identifier = "Tebex Transaction Lookup";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.tebexTransactionLookup.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexTransactionLookup.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexTransactionLookup.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.tebexTransactionLookup.messageTitle");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.tebexTransactionLookup.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.tebexTransactionLookup.enabled");

    private final String ignError = getConfig.get().getString("commands.tebexTransactionLookup.transError");

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
            String transID = message[1];

            JSONObject json = null;
            try {
                json = ApiUtils.lookupTransaction(transID);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if (json == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(ignError).build()).queue();
                return;
            }
            ArrayList<JSONObject> packages = (ArrayList<JSONObject>) json.get("packages");

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(MESSAGE_TITLE.replace("{transID}", transID))
                    .addField("Purchase Info", "Amount: `" +
                                    ((JSONObject) json.get("currency")).get("symbol") + "" + json.get("amount").toString() + " " +
                                    ((JSONObject) json.get("currency")).get("iso_4217") +
                                    "`\nStatus: `" + json.get("status").toString() + "`\nPlayer IGN: `" + ((JSONObject) json.get("player")).get("name") + "`"
                                    + "\nPlayer UUID: `" + ApiUtils.convertUUID(((JSONObject) json.get("player")).get("uuid").toString()) + "`",
                            true)
                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setTimestamp(Prettify.getZonedDateTimeFromString(json.get("date").toString()))
                    .setThumbnail("https://crafatar.com/avatars/" + ApiUtils.getUUIDFromIGN(((JSONObject) json.get("player")).get("name").toString()))
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
}