package main.java.com.i0dev.command.basic;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdSuggest extends ListenerAdapter {

    private final String Identifier = "Suggest";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.suggest.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.suggest.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.suggest.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.suggest.messageTitle");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.suggest.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.suggest.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.suggest.enabled");
    private final String upVoteEmoji = getConfig.get().getString("commands.suggest.upVoteEmoji");
    private final String downVoteEmoji = getConfig.get().getString("commands.suggest.downVoteEmoji");
    private final String suggestionChannelID = getConfig.get().getString("channels.suggestionChannelID");
    private final String sucsessMessage = getConfig.get().getString("commands.suggest.SuccessMessage");

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
            if (message.length != 3) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(sucsessMessage
                            .replace("{gamemode}", message[1])
                            .replace("{suggestion}", Prettify.remainingArgFormatter(message, 2))
                    , e.getAuthor())).build()).queue();
            e.getGuild().getTextChannelById(suggestionChannelID).sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TITLE, e.getAuthor()), Placeholders.convert(MESSAGE_CONTENT
                    .replace("{gamemode}", message[1])
                    .replace("{suggestion}", Prettify.remainingArgFormatter(message, 2)), e.getAuthor())).build()).queue(message1 -> {
                message1.addReaction(EmojiUtil.getEmojiWithoutArrow(upVoteEmoji)).queue();
                message1.addReaction(EmojiUtil.getEmojiWithoutArrow(downVoteEmoji)).queue();
            });
        }
    }

}