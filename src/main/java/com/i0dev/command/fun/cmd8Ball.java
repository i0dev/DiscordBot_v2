package main.java.com.i0dev.command.fun;

import main.java.com.i0dev.entity.Blacklist;

import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class cmd8Ball extends ListenerAdapter {

    private final String Identifier = "8Ball";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.fun_8ball.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.fun_8ball.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.fun_8ball.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.fun_8ball.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.fun_8ball.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.fun_8ball.enabled");


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
            if (message.length == 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            int choice = Math.abs(ThreadLocalRandom.current().nextInt(20)) + 1;
            String Ans = "N/A";
            switch (choice) {
                case 1:
                    Ans = "Ask again later.";
                    break;
                case 2:
                    Ans = "Better not tell you now.";
                    break;
                case 3:
                    Ans = "Cannot predict now.";
                    break;
                case 4:
                    Ans = " Concentrate and ask again";
                    break;
                case 5:
                    Ans = "Don't count on it.";
                    break;
                case 6:
                    Ans = "It is certain.";
                    break;
                case 7:
                    Ans = "It is decidedly so.";
                    break;
                case 8:
                    Ans = "Most likely.";
                    break;
                case 9:
                    Ans = "My reply is no.";
                    break;
                case 10:
                    Ans = "My sources say no.";
                    break;
                case 11:
                    Ans = "Outlook not so good.";
                    break;
                case 12:
                    Ans = "Outlook good.";
                    break;
                case 13:
                    Ans = "Reply hazy, try again.";
                    break;
                case 14:
                    Ans = "Signs point to yes.";
                    break;
                case 15:
                    Ans = "Very doubtful.";
                    break;
                case 16:
                    Ans = "Without a doubt.";
                    break;
                case 17:
                    Ans = "Yes.";
                    break;
                case 18:
                    Ans = "Yes - definitely.";
                    break;
                case 19:
                    Ans = "You may rely on it.";
                    break;
                case 20:
                    Ans = "As I see it, yes.";
                    break;
            }
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{question}", Prettify.remainingArgFormatter(message, 1)).replace("{answer}", Ans + ""), e.getAuthor())).build()).queue();
        }
    }
}
