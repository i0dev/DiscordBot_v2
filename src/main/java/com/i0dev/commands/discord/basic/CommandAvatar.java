package com.i0dev.commands.discord.basic;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandAvatar {


    private static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.avatar.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.avatar.permissionLiteMode");
    private static final String MESSAGE_TITLE = Configuration.getString("commands.avatar.messageTitle");
    private static final String MESSAGE_FORMAT = Configuration.getString("commands.avatar.format");
    private static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.avatar.enabled");


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
