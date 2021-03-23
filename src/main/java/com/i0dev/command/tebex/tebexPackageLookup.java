package main.java.com.i0dev.command.tebex;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

public class tebexPackageLookup extends ListenerAdapter {

    private final String Identifier = "Tebex Package Lookup";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.tebexPackageLookup.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexPackageLookup.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexPackageLookup.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.tebexPackageLookup.messageTitle");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.tebexPackageLookup.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.tebexPackageLookup.enabled");

    private final String ignError = getConfig.get().getString("commands.tebexPackageLookup.pkgError");

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
            String pkgID = message[1];

            JSONObject json = null;
            json = ApiUtils.lookupPackage(pkgID);
            if (json == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(ignError).build()).queue();
                return;
            }
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(MESSAGE_TITLE.replace("{pkgID}", pkgID))

                    .addField("Package Information",

                            "ID: `" + json.get("id") + "`"
                                    + "\n" + "Name: `" + json.get("name") + "`"
                                    + "\n" + "Price: `$" + json.get("price") + "`"
                                    + "\n" + "Type: `" + json.get("type") + "`"
                                    + "\n" + "Disabled: `" + json.get("disabled") + "`"
                                    + "\n" + "Category ID: `" + ((JSONObject) json.get("category")).get("id") + "`"
                                    + "\n" + "Category Name: `" + ((JSONObject) json.get("category")).get("name") + "`"
                            , false)

                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setTimestamp(ZonedDateTime.now())
                    .setThumbnail(conf.EMBED_THUMBNAIL)
                    .setFooter(conf.EMBED_FOOTER);


            e.getChannel().sendMessage(embed.build()).queue();
        }
    }
}