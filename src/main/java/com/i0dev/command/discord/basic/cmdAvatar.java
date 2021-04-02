package main.java.com.i0dev.command.discord.basic;

import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdAvatar extends ListenerAdapter {


    private final String Identifier = "Avatar";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.avatar.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.avatar.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.avatar.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.avatar.messageTitle");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.avatar.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.avatar.enabled");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        //make's sure bots cant trigger this command on accident
        if (e.getAuthor().isBot()) return;
        //Return if the guild does not match the Main Guild
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;

        //Check if the message sent is one of the ones in the avatar command aliases list
        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {

            //check if the user is blacklisted or not
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            //check if the delete is enabled and delete the message.
            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            //Checks weather ths command is enabled or not
            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            //Checks weather the player has correct permissions to runt he command.
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            //Splits the message up into a String[]
            String[] message = e.getMessage().getContentRaw().split(" ");
            //checks if the message only contains 1 argument
            if (message.length == 1) {
                //runs the avatar for the sender
                e.getChannel().sendMessage(EmbedFactory.get().imageEmbed(Placeholders.convert(MESSAGE_TITLE.replace("{user}", e.getAuthor().getAsTag()), e.getAuthor()), e.getAuthor().getEffectiveAvatarUrl()).build()).queue();
                return;
            }
            //checks if the message contains 2 arguments
            if (message.length == 2) {
                User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
                //null check for the player
                if (MentionedUser == null) {
                    e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                }
                //runs the avatar for other players
                else {
                    e.getChannel().sendMessage(EmbedFactory.get().imageEmbed(Placeholders.convert(MESSAGE_TITLE, MentionedUser), MentionedUser.getEffectiveAvatarUrl()).build()).queue();
                }
                return;
            }
            //if nothing matches criteria, send a format message
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();

        }
    }
}
