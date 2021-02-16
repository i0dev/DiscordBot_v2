package main.java.com.i0dev.command.fun;

import main.java.com.i0dev.entity.Blacklist;

import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

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

            String Question = "";
            for (int i = 1; i < message.length; i++) {
                Question = Question + " " + message[i] + " ";
            }
            int Response = (int) (Math.random() * 5);


            if (Response < 0) {
                Response = Response * -1;
            }
            String Ans = "N/A";
            switch (Response) {
                case 0:
                    Ans = "Most Likely";
                    break;
                case 1:
                    Ans = "Yes";
                    break;
                case 2:
                    Ans = "Possibly";
                    break;
                case 3:
                    Ans = "No";
                    break;
                case 4:
                    Ans = "Defiantly Not";
                    break;
                case 5:
                    Ans = "NEVER";
                    break;
            }

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{question}", Question).replace("{answer}", Ans + ""), e.getAuthor())).build()).queue();

        }
    }
}
