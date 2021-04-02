package main.java.com.i0dev.command.discord.minecraftAPI;

import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.APIUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class cmdMCServerInfo extends ListenerAdapter {

    private final String Identifier = "Minecraft Server Information";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.MCServerInfo.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.MCServerInfo.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.MCServerInfo.permissionLiteMode");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.MCServerInfo.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.MCServerInfo.enabled");
    private final String messageTitle = getConfig.get().getString("commands.MCServerInfo.messageTitle");
    private final String cantFindServerError = getConfig.get().getString("commands.MCServerInfo.cantFindServerError");

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
            String ip = message[1];
            JSONObject json = null;
            try {
                json = APIUtil.MinecraftServerLookup(ip);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if (json == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(cantFindServerError.replace("{ip}", ip)).build()).queue();
                return;
            }
            EmbedBuilder embed;
            if (((boolean) json.get("online"))) {
                StringBuilder motd = new StringBuilder();
                JSONObject players = ((JSONObject) json.get("players"));
                ((ArrayList<String>) ((JSONObject) json.get("motd")).get("clean")).forEach(s -> motd.append(s + "\n"));
                embed = new EmbedBuilder()
                        .setTitle(messageTitle.replace("{ip}", ip))
                        .addField("Server information",
                                "Numerical IP: `" + json.get("ip") + "`"
                                        + "\n" + "Port: `" + json.get("port") + "`"
                                        + "\n" + "Online: `" + json.get("online") + "`"
                                        + "\n" + "Online Players: `" + players.get("online") + "/" + players.get("max") + "`"
                                        + "\n" + "Minecraft Version(s): `" + json.get("version") + "`"
                                , false)
                        .addField("MOTD",
                                motd.toString()
                                , false)

                        .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                        .setTimestamp(ZonedDateTime.now())
                        .setThumbnail("https://api.mcsrvstat.us/icon/" + ip)
                        .setFooter(GlobalConfig.EMBED_FOOTER);
            } else {
                embed = new EmbedBuilder()
                        .setTitle(messageTitle.replace("{ip}", ip))
                        .addField("Server information",
                                "Online: `" + json.get("online") + "`"
                                        + "\n" + "Port: `" + json.get("port") + "`"
                                        + "\n" + "Numerical IP: `" + json.get("ip") + "`"
                                        + "\n" + "\n*This minecraft server is either offline or had an error retrieving data. Please try an IP address that is online!*"
                                , false)
                        .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                        .setTimestamp(ZonedDateTime.now())
                        .setFooter(GlobalConfig.EMBED_FOOTER);
            }
            e.getChannel().sendMessage(embed.build()).queue();
        }
    }
}