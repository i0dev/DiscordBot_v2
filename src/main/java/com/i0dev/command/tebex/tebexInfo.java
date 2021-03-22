package main.java.com.i0dev.command.tebex;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class tebexInfo extends ListenerAdapter {

    private final String Identifier = "Tebex Information";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.tebexInfo.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexInfo.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexInfo.permissionLiteMode");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.tebexInfo.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.tebexInfo.enabled");

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
            if (message.length != 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            JSONObject json = null;
            try {
                json = ApiUtils.getInformation();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            json = (JSONObject) json.get("account");
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(conf.EMBED_TITLE)
                    .addField("Tebex information",

                            "ID: `" + json.get("id") + "`"
                                    + "\n" + "Domain: `" + json.get("domain") + "`"
                                    + "\n" + "Name: `" + json.get("name") + "`"
                                    + "\n" + "Online Mode: `" + json.get("online_mode") + "`"
                                    + "\n" + "Game Type: `" + json.get("game_type") + "`"
                                    + "\n" + "Log Events: `" + json.get("log_events") + "`"
                            , false)

                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setTimestamp(ZonedDateTime.now())
                    .setThumbnail(conf.EMBED_THUMBNAIL)
                    .setFooter(conf.EMBED_FOOTER);


            e.getChannel().sendMessage(embed.build()).queue();
        }
    }
}