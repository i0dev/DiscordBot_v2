package com.i0dev.modules.applications;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.modules.applications.cache.ApplicationCache;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class CommandApply extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static boolean COMMAND_ENABLED;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;

    public static String alreadyCreating;
    private static List<String> initialQuestions;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.apply.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.apply.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.apply.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.apply.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.apply.enabled");
        alreadyCreating = Configuration.getString("commands.apply.alreadyCreating");
        initialQuestions = Configuration.getStringList("commands.apply.Questions");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Apply")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.APPLY_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }


        if (ApplicationCache.get().getMap().containsKey(e.getAuthor()) || ApplicationCache.get().getResponseMap().containsKey(e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(alreadyCreating, e.getAuthor())).build()).queue();
            return;
        }
        ApplicationCache.get().getMap().put(e.getAuthor(), 0);
        ApplicationCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));
        e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(initialQuestions.get(0)).build()).queue();
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();

    }
}
