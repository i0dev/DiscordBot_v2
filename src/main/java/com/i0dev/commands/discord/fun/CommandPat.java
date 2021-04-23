package com.i0dev.commands.discord.fun;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class  CommandPat {

    public static final String Identifier = "Pat";
    private final List<String> COMMAND_ALIASES = Configuration.getStringList("commands.fun_pat.aliases");
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.fun_pat.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.fun_pat.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.fun_pat.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.fun_pat.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.fun_pat.enabled");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Pat")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.PAT_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
            return;
        }
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT,MentionedUser, e.getAuthor())).build()).queue();
    }

}
