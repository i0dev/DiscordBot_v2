package main.java.com.i0dev.command.applications;

import main.java.com.i0dev.cache.ApplicationCache;
import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdApply extends ListenerAdapter {

    private final String Identifier = "Apply";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.apply.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.apply.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.apply.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.apply.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.apply.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.apply.enabled");
    private final String alreadyCreating = getConfig.get().getString("commands.apply.alreadyCreating");

    private final List<String> initialQuestions = getConfig.get().getStringList("commands.apply.Questions");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(conf.GENERAL_MAIN_GUILD)) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (conf.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }


            if (ApplicationCache.get().getMap().containsKey(e.getAuthor()) || ApplicationCache.get().getResponseMap().containsKey(e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(alreadyCreating, e.getAuthor())).build()).queue();
                return;
            }
            ApplicationCache.get().getMap().put(e.getAuthor(), 0);
            ApplicationCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + getConfig.get().getLong("general.creatorTimeouts"));
            e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail(initialQuestions.get(0)).build()).queue();
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();

        }
    }
}
