package main.java.com.i0dev.command.applications;

import main.java.com.i0dev.entity.Application;
import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdReject extends ListenerAdapter {

    private final String Identifier = "Reject";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.reject.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.reject.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.reject.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.reject.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.reject.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.reject.enabled");
    private final String defaultResponse = getConfig.get().getString("commands.reject.defaultResponse");
    private final String dmToRejectedDesc = getConfig.get().getString("commands.reject.dmToRejectedDesc");
    private final String userNoApplicationSubmitted = getConfig.get().getString("commands.reject.userNoApplicationSubmitted");
    private final boolean requireAppToBeSubmittedToReject = getConfig.get().getBoolean("commands.reject.requireAppToBeSubmittedToReject");
    private final boolean deleteApplicationAfterRejected = getConfig.get().getBoolean("commands.reject.deleteApplicationAfterRejected");


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
            if (message.length <= 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }

            if (requireAppToBeSubmittedToReject && !Application.get().hasASubmittedApplication(MentionedUser)) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(userNoApplicationSubmitted, MentionedUser)).build()).queue();
                return;
            }

            String response = Prettify.ticketRemainingArgFormatter(message, 2);
            if (response.length() <= 1) {
                response = defaultResponse;
            }
            MentionedUser.openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(dmToRejectedDesc
                            .replace("{response}", response)
                    , e.getAuthor())).build()).queue();

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT
                            .replace("{response}", response)
                    , MentionedUser)).build()).queue();
            if (deleteApplicationAfterRejected) {
                Application.get().removeUser(MentionedUser);
            }
        }
    }
}