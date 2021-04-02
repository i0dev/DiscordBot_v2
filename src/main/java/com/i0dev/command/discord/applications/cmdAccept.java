package main.java.com.i0dev.command.discord.applications;

import main.java.com.i0dev.object.Application;
import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.FormatUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdAccept extends ListenerAdapter {

    private final String Identifier = "Accept";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.accept.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.accept.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.accept.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.accept.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.accept.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.accept.enabled");
    private final String defaultResponse = getConfig.get().getString("commands.accept.defaultResponse");
    private final String dmToAcceptedDesc = getConfig.get().getString("commands.accept.dmToAcceptedDesc");
    private final String userNoApplicationSubmitted = getConfig.get().getString("commands.accept.userNoApplicationSubmitted");
    private final boolean requireAppToBeSubmittedToAccept = getConfig.get().getBoolean("commands.accept.requireAppToBeSubmittedToAccept");
    private final boolean deleteApplicationAfterAccepted = getConfig.get().getBoolean("commands.accept.deleteApplicationAfterAccepted");


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
            if (message.length <= 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            Role MentionedRole = FindFromString.get().getRole(message[2], e.getMessage());
            if (MentionedRole == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_ROLE_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }
            User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }

            if (requireAppToBeSubmittedToAccept && !Application.get().hasASubmittedApplication(MentionedUser)) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(userNoApplicationSubmitted, MentionedUser)).build()).queue();
                return;
            }

            String response = FormatUtil.ticketRemainingArgFormatter(message, 3);
            if (response.length() <= 1) {
                response = defaultResponse;
            }
            MentionedUser.openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(dmToAcceptedDesc
                            .replace("{response}", response)
                            .replace("{roleMention}", MentionedRole.getName())
                    , e.getAuthor())).build()).queue();

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT
                            .replace("{response}", response)
                    , MentionedUser)).build()).queue();
            if (deleteApplicationAfterAccepted) {
                Application.get().removeUser(MentionedUser);
            }
        }
    }
}