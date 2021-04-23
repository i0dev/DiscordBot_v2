package com.i0dev.commands.discord.minecraft;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.APIUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class CommandMcServerInfo {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.MCServerInfo.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.MCServerInfo.permissionLiteMode");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.MCServerInfo.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.MCServerInfo.enabled");
    public static final String messageTitle = Configuration.getString("commands.MCServerInfo.messageTitle");
    public static final String cantFindServerError = Configuration.getString("commands.MCServerInfo.cantFindServerError");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Minecraft Server Information")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.MC_SERVER_INFO_ALIASES.get(0)), e.getAuthor())).build()).queue();
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
            e.getChannel().sendMessage(EmbedFactory.createEmbed(cantFindServerError.replace("{ip}", ip)).build()).queue();
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