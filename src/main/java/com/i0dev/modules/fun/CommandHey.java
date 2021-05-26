package com.i0dev.modules.fun;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandHey extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static boolean COMMAND_ENABLED;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.fun_hey.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.fun_hey.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.fun_hey.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.fun_hey.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.fun_hey.enabled");
    }

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
