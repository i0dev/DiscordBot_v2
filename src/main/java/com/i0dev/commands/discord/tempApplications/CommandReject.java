package com.i0dev.commands.discord.tempApplications;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.ApplicationEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandReject {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.reject.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.reject.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.reject.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.reject.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.reject.enabled");
    public static final String defaultResponse = Configuration.getString("commands.reject.defaultResponse");
    public static final String dmToRejectedDesc = Configuration.getString("commands.reject.dmToRejectedDesc");
    public static final String userNoApplicationSubmitted = Configuration.getString("commands.reject.userNoApplicationSubmitted");
    public static final boolean requireAppToBeSubmittedToReject = Configuration.getBoolean("commands.reject.requireAppToBeSubmittedToReject");
    public static final boolean deleteApplicationAfterRejected = Configuration.getBoolean("commands.reject.deleteApplicationAfterRejected");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Reject")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length <= 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.REJECT_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }

        if (requireAppToBeSubmittedToReject && !ApplicationEngine.getInstance().isOnList(MentionedUser)) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(userNoApplicationSubmitted, MentionedUser)).build()).queue();
            return;
        }

        String response = FormatUtil.ticketRemainingArgFormatter(message, 2);
        if (response.length() <= 1) {
            response = defaultResponse;
        }
        MentionedUser.openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(dmToRejectedDesc
                        .replace("{response}", response)
                , e.getAuthor())).build()).queue();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT
                        .replace("{response}", response)
                , MentionedUser)).build()).queue();
        if (deleteApplicationAfterRejected) {
            ApplicationEngine.getInstance().remove(MentionedUser);
        }
    }

}