package com.i0dev.commands.discord.ticket;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.EmojiUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;

public class CommandTicketPanel {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.createTicketPanel.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.createTicketPanel.permissionLiteMode");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.createTicketPanel.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.createTicketPanel.enabled");
    public static final String messageSentBeforePanel = Configuration.getString("commands.createTicketPanel.messageSentBeforePanel");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Create Ticket Panel")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.CREATE_PANEL_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        List<JSONObject> TicketOptions = Configuration.getObjectList("commands.createTicketPanel.ticketOptions");
        String ticketPanelTitle = Configuration.getString("commands.createTicketPanel.ticketPanelTitle");
        String ticketPanelDescription = Configuration.getString("commands.createTicketPanel.ticketPanelDescription");
        String ticketPanelFieldBaseFormat = Configuration.getString("commands.createTicketPanel.ticketPanelFieldBaseFormat");
        boolean pinTicketPanel = Configuration.getBoolean("commands.createTicketPanel.pinTicketPanel");

        EmbedBuilder Embed = new EmbedBuilder()
                .setTitle(ticketPanelTitle)
                .setThumbnail(GlobalConfig.EMBED_THUMBNAIL.equals("") ? null : GlobalConfig.EMBED_THUMBNAIL)
                .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                .setFooter(GlobalConfig.EMBED_FOOTER)
                .setDescription(ticketPanelDescription)
                .setTimestamp(ZonedDateTime.now());

        for (JSONObject object : TicketOptions) {
            String PanelHeader = object.get("PanelHeader").toString();
            String PanelDescription = object.get("PanelDescription").toString();
            Embed.addField(PanelHeader,
                    ticketPanelFieldBaseFormat.replace("{panelDescription}", PanelDescription), false);
        }
        if (!"".equals(messageSentBeforePanel)) {
            e.getChannel().sendMessage(messageSentBeforePanel).queue();
        }
        Message PanelMessage = e.getChannel().sendMessage(Embed.build()).complete();
        if (pinTicketPanel) PanelMessage.pin().queue();
        for (JSONObject object : TicketOptions) {
            String Emoji = EmojiUtil.getEmojiWithoutArrow(object.get("Emoji").toString());
            PanelMessage.addReaction(Emoji).queue();
        }
    }
}