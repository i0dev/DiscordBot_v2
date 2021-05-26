package com.i0dev.modules.creators;

import com.i0dev.modules.creators.caches.PollCache;
import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PollCreator extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static boolean COMMAND_ENABLED;
    public static String alreadyCreating;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.pollCreator.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.pollCreator.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.pollCreator.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.pollCreator.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.pollCreator.enabled");
        alreadyCreating = Configuration.getString("commands.pollCreator.alreadyCreating");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Poll Creator")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.POLL_CREATOR_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        if (PollCache.get().getMap().containsKey(e.getAuthor()) || PollCache.get().getResponseMap().containsKey(e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(alreadyCreating, e.getAuthor())).build()).queue();
            return;
        }
        PollCache.get().getMap().put(e.getAuthor(), 0);
        PollCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));
        e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed("Please enter the channel you would like the poll to be posted in.").build()).queue();
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();
    }
}
