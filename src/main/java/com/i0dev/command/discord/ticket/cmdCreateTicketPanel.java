package com.i0dev.command.discord.ticket;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;

import com.i0dev.utility.util.EmojiUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.List;

public class cmdCreateTicketPanel extends ListenerAdapter {

    private final String Identifier = "Send Ticket Panel";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.createTicketPanel.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.createTicketPanel.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.createTicketPanel.permissionLiteMode");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.createTicketPanel.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.createTicketPanel.enabled");
    private final String messageSentBeforePanel = getConfig.get().getString("commands.createTicketPanel.messageSentBeforePanel");

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
                if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            List<JSONObject> TicketOptions = getConfig.get().getObjectList("commands.createTicketPanel.ticketOptions");
            String ticketPanelTitle = getConfig.get().getString("commands.createTicketPanel.ticketPanelTitle");
            String ticketPanelDescription = getConfig.get().getString("commands.createTicketPanel.ticketPanelDescription");
            String ticketPanelFieldBaseFormat = getConfig.get().getString("commands.createTicketPanel.ticketPanelFieldBaseFormat");
            boolean pinTicketPanel = getConfig.get().getBoolean("commands.createTicketPanel.pinTicketPanel");

            EmbedBuilder Embed = new EmbedBuilder()
                    .setTitle(ticketPanelTitle)
                    .setThumbnail(GlobalConfig.EMBED_THUMBNAIL)
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
}
