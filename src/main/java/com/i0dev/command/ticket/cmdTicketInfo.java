package com.i0dev.command.ticket;

import com.i0dev.entity.Blacklist;
import com.i0dev.entity.Ticket;
import com.i0dev.util.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.List;

public class cmdTicketInfo extends ListenerAdapter {

    private final String Identifier = "Ticket Info";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.ticketInfo.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketInfo.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketInfo.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.ticketInfo.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.ticketInfo.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.ticketInfo.enabled");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;
        if (!Ticket.get().isTicket(e.getChannel())) return;

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

            JSONObject ticketObject = Ticket.get().getTicket(e.getChannel());

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT
                    .replace("{channelName}", e.getChannel().getName())
                    .replace("{ownersTag}", ticketObject.get("ticketOwnerTag").toString())
                    .replace("{channelID}", ticketObject.get("channelID").toString())
                    .replace("{adminOnly}", ticketObject.get("adminOnlyMode") + ""), e.getAuthor())).build()).queue();

        }
    }
}
