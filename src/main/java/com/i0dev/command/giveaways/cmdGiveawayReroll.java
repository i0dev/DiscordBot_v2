package main.java.com.i0dev.command.giveaways;

import main.java.com.i0dev.engine.TaskCheckActiveGiveaways;
import main.java.com.i0dev.entity.Blacklist;

import main.java.com.i0dev.entity.Giveaway;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.List;

public class cmdGiveawayReroll extends ListenerAdapter {

    private final String Identifier = "ReRoll giveaway";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.giveawayReroll.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.giveawayReroll.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.giveawayReroll.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.giveawayReroll.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.giveawayReroll.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.giveawayReroll.enabled");
    private final String error = getConfig.get().getString("commands.giveawayReroll.error");
    private final String errorRunning = getConfig.get().getString("commands.giveawayReroll.errorRunning");

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
            if (message.length != 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            String giveawayID = message[1];
            JSONObject giveaway = Giveaway.get().getGiveaway(giveawayID);
            if (giveaway == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(error, e.getAuthor())).build()).queue();
                return;
            }
            if (!((boolean) giveaway.get("ended"))) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(errorRunning, e.getAuthor())).build()).queue();
                return;
            }
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();

            TaskCheckActiveGiveaways.get().endGiveawayFull(giveaway, true, true, true, e.getAuthor());

        }
    }
}
