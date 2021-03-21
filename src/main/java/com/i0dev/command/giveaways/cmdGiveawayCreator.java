package main.java.com.i0dev.command.giveaways;

import main.java.com.i0dev.cache.GiveawayCache;
import main.java.com.i0dev.util.*;
import main.java.com.i0dev.entity.Blacklist;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdGiveawayCreator extends ListenerAdapter {

    private final String Identifier = "Giveaway Creator";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.gcreate.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.gcreate.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.gcreate.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.gcreate.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.gcreate.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.gcreate.enabled");
    private final String alreadyCreating = getConfig.get().getString("commands.gcreate.alreadyCreating");


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
            if (GiveawayCache.get().getMap().containsKey(e.getAuthor()) || GiveawayCache.get().getResponseMap().containsKey(e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(alreadyCreating, e.getAuthor())).build()).queue();
                return;
            }
            GiveawayCache.get().getMap().put(e.getAuthor(), 0);
            GiveawayCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + getConfig.get().getLong("general.creatorTimeouts"));
            e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Please enter the channel you would like the giveaway to be posted in.").build()).queue();
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();

        }
    }
}
