package com.i0dev.commands.discord.fun;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class CommandHey {

    public static final String Identifier = "Hey";
    private final List<String> COMMAND_ALIASES = Configuration.getStringList("commands.fun_hey.aliases");
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.fun_hey.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.fun_hey.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.fun_hey.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.fun_hey.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.fun_hey.enabled");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Hey")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.HEY_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();

    }

}
