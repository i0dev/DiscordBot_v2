package com.i0dev.commands.discord.tempApplications;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.ApplicationEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class CommandAccept {

    public static final String Identifier = "Accept";
    private final List<String> COMMAND_ALIASES = Configuration.getStringList("commands.accept.aliases");
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.accept.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.accept.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.accept.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.accept.format");
    public static final boolean ENABLED = Configuration.getBoolean("commands.accept.enabled");
    public static final String defaultResponse = Configuration.getString("commands.accept.defaultResponse");
    public static final String dmToAcceptedDesc = Configuration.getString("commands.accept.dmToAcceptedDesc");
    public static final String userNoApplicationSubmitted = Configuration.getString("commands.accept.userNoApplicationSubmitted");
    public static final boolean requireAppToBeSubmittedToAccept = Configuration.getBoolean("commands.accept.requireAppToBeSubmittedToAccept");
    public static final boolean deleteApplicationAfterAccepted = Configuration.getBoolean("commands.accept.deleteApplicationAfterAccepted");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Accept")) {
            return;
        }

        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length <= 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.ACCEPT_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }

        Role MentionedRole = FindFromString.get().getRole(message[2], e.getMessage());
        if (MentionedRole == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_ROLE_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }

        if (requireAppToBeSubmittedToAccept && !ApplicationEngine.getInstance().isOnList(MentionedUser)) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(userNoApplicationSubmitted, MentionedUser)).build()).queue();
            return;
        }

        String response = FormatUtil.ticketRemainingArgFormatter(message, 3);
        if (response.length() <= 1) {
            response = defaultResponse;
        }
        MentionedUser.openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(dmToAcceptedDesc
                        .replace("{response}", response)
                        .replace("{roleMention}", MentionedRole.getName())
                , MentionedUser, e.getAuthor())).build()).queue();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT
                        .replace("{response}", response)
                , MentionedUser, e.getAuthor())).build()).queue();
        if (deleteApplicationAfterAccepted) {
            ApplicationEngine.getInstance().remove(MentionedUser);

        }
    }
}