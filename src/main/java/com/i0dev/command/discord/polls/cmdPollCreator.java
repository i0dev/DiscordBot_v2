package main.java.com.i0dev.command.discord.polls;

import main.java.com.i0dev.cache.PollCache;
import main.java.com.i0dev.object.Blacklist;

import main.java.com.i0dev.utility.GlobalConfig;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdPollCreator extends ListenerAdapter {

    private final String Identifier = "Poll Creator";
    private final List<String> COMMAND_ALIASES = main.java.com.i0dev.utility.getConfig.get().getStringList("commands.pollCreator.aliases");
    private final boolean REQUIRE_PERMISSIONS = main.java.com.i0dev.utility.getConfig.get().getBoolean("commands.pollCreator.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = main.java.com.i0dev.utility.getConfig.get().getBoolean("commands.pollCreator.permissionLiteMode");
    private final String MESSAGE_CONTENT = main.java.com.i0dev.utility.getConfig.get().getString("commands.pollCreator.messageContent");
    private final String MESSAGE_FORMAT = main.java.com.i0dev.utility.getConfig.get().getString("commands.pollCreator.format");
    private final boolean COMMAND_ENABLED = main.java.com.i0dev.utility.getConfig.get().getBoolean("commands.pollCreator.enabled");
    private final String alreadyCreating = main.java.com.i0dev.utility.getConfig.get().getString("commands.pollCreator.alreadyCreating");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;

        if (main.java.com.i0dev.utility.MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            if (PollCache.get().getMap().containsKey(e.getAuthor()) || PollCache.get().getResponseMap().containsKey(e.getAuthor())) {
                e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(alreadyCreating, e.getAuthor())).build()).queue();
                return;
            }
            PollCache.get().getMap().put(e.getAuthor(), 0);
            PollCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + main.java.com.i0dev.utility.getConfig.get().getLong("general.creatorTimeouts"));
            e.getAuthor().openPrivateChannel().complete().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbedNoThumbnail("Please enter the channel you would like the poll to be posted in.").build()).queue();
            e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();

        }
    }
}
