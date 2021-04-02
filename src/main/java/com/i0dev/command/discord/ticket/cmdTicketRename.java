package main.java.com.i0dev.command.discord.ticket;

import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.object.Ticket;

import main.java.com.i0dev.utility.util.FormatUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.List;

public class cmdTicketRename extends ListenerAdapter {

    private final String Identifier = "Ticket Rename";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.ticketRename.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketRename.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.ticketRename.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.ticketRename.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.ticketRename.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.ticketRename.enabled");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (!Ticket.get().isTicket(e.getChannel())) return;

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
            if (message.length == 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            JSONObject ticketObject = Ticket.get().getTicket(e.getChannel());
            String newTicketName = FormatUtil.remainingArgFormatter(message, 1).replace(" ", "-") + "-" + ticketObject.get("ticketNumber");
            e.getChannel().getManager().setName(newTicketName).queue();

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{newName}", newTicketName), e.getAuthor())).build()).queue();

        }
    }
}
