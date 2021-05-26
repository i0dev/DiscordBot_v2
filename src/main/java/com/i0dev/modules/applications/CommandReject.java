package com.i0dev.modules.applications;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.ApplicationEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandReject extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static boolean COMMAND_ENABLED;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;

    public static String defaultResponse;
    public static String dmToRejectedDesc;
    public static String userNoApplicationSubmitted;
    public static boolean requireAppToBeSubmittedToReject;
    public static boolean deleteApplicationAfterRejected;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.reject.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.reject.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.reject.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.reject.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.reject.enabled");
        defaultResponse = Configuration.getString("commands.reject.defaultResponse");
        dmToRejectedDesc = Configuration.getString("commands.reject.dmToRejectedDesc");
        userNoApplicationSubmitted = Configuration.getString("commands.reject.userNoApplicationSubmitted");
        requireAppToBeSubmittedToReject = Configuration.getBoolean("commands.reject.requireAppToBeSubmittedToReject");
        deleteApplicationAfterRejected = Configuration.getBoolean("commands.reject.deleteApplicationAfterRejected");
    }

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