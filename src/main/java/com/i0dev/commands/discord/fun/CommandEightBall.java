package com.i0dev.commands.discord.fun;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CommandEightBall {

    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.fun_8ball.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.fun_8ball.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.fun_8ball.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.fun_8ball.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.fun_8ball.enabled");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "8ball")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.EIGHTBALL_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
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
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{question}", FormatUtil.remainingArgFormatter(message, 1)).replace("{answer}", Ans + ""), e.getAuthor())).build()).queue();
    }

}
