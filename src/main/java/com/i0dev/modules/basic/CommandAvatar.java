package com.i0dev.modules.basic;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandAvatar extends DiscordCommand {


    private static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    private static String MESSAGE_TITLE;
    private static String MESSAGE_FORMAT;
    private static boolean COMMAND_ENABLED;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.avatar.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.avatar.permissionLiteMode");
        MESSAGE_TITLE = Configuration.getString("commands.avatar.messageTitle");
        MESSAGE_FORMAT = Configuration.getString("commands.avatar.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.avatar.enabled");
    }

    public static void run(GuildMessageReceivedEvent e) {
        //Perform a global check
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Avatar")) {
            return;
        }
        //Splits the message up into a String[]
        String[] message = e.getMessage().getContentRaw().split(" ");
        //checks if the message only contains 1 argument
        if (message.length == 1) {
            //runs the avatar for the sender
            e.getChannel().sendMessage(EmbedFactory.createImageEmbed(Placeholders.convert(MESSAGE_TITLE, null, e.getAuthor()), e.getAuthor().getEffectiveAvatarUrl()).build()).queue();
            return;
        }
        //checks if the message contains 2 arguments
        if (message.length == 2) {
            User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            //null check for the player
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            }
            //runs the avatar for other players
            else {
                e.getChannel().sendMessage(EmbedFactory.createImageEmbed(Placeholders.convert(MESSAGE_TITLE, null, MentionedUser), MentionedUser.getEffectiveAvatarUrl()).build()).queue();
            }
            return;
        }
        //if nothing matches criteria, send a format message
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.AVATAR_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();

    }

}
