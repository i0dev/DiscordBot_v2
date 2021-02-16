package com.i0dev.command.ticket;

import com.i0dev.entity.Blacklist;
import com.i0dev.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.FileReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class cmdCreateTicketPanel extends ListenerAdapter {

    private final String Identifier = "Send Ticket Panel";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.createTicketPanel.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.createTicketPanel.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.createTicketPanel.permissionLiteMode");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.createTicketPanel.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.createTicketPanel.enabled");


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
                if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            List<JSONObject> TicketOptions = getConfig.get().getObjectList("commands.createTicketPanel.ticketOptions");
            String ticketPanelTitle = getConfig.get().getString("commands.createTicketPanel.ticketPanelTitle");
            String ticketPanelDescription = getConfig.get().getString("commands.createTicketPanel.ticketPanelDescription");
            String ticketPanelFieldHeaderFormat = getConfig.get().getString("commands.createTicketPanel.ticketPanelFieldHeaderFormat");
            String ticketPanelFieldBaseFormat = getConfig.get().getString("commands.createTicketPanel.ticketPanelFieldBaseFormat");
            boolean pinTicketPanel = getConfig.get().getBoolean("commands.createTicketPanel.pinTicketPanel");

            EmbedBuilder Embed = new EmbedBuilder()
                    .setTitle(ticketPanelTitle)
                    .setThumbnail(conf.EMBED_THUMBNAIL)
                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setFooter(conf.EMBED_FOOTER)
                    .setDescription(ticketPanelDescription)
                    .setTimestamp(ZonedDateTime.now());

            for (JSONObject object : TicketOptions) {
                String PanelHeader = object.get("PanelHeader").toString();
                String PanelDescription = object.get("PanelDescription").toString();
                String Emoji = object.get("Emoji").toString();
                Embed.addField(ticketPanelFieldHeaderFormat.replace("{emoji}", Emoji).replace("{panelHeader}", PanelHeader),
                        ticketPanelFieldBaseFormat.replace("{panelDescription}", PanelDescription), false);
            }
            Message PanelMessage = e.getChannel().sendMessage(Embed.build()).complete();
            if (pinTicketPanel) PanelMessage.pin().queue();
            for (JSONObject object : TicketOptions) {
                String Emoji = getEmojiWithoutArrow(object.get("Emoji").toString());
                PanelMessage.addReaction(Emoji).queue();
            }
        }
    }

    private String getEmojiWithoutArrow(String emoji) {
        String EmojiWithoutArrow;
        if (emoji.length() < 3) {
            EmojiWithoutArrow = emoji;
        } else {
            EmojiWithoutArrow = emoji.substring(0, emoji.length() - 1);
        }
        return EmojiWithoutArrow;
    }
}
