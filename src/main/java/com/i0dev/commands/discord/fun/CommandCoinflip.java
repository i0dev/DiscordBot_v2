package com.i0dev.commands.discord.fun;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandCoinflip {

    public static final String Identifier = "CoinFlip";
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.fun_coinFlip.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.fun_coinFlip.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.fun_coinFlip.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.fun_coinFlip.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.fun_coinFlip.enabled");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "CoinFlip")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.COINFLIP_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        String output = CoinFlip() + "";
        if (output.equals("0")) {
            output = "Heads";
        }
        if (output.equals("1")) {
            output = "Tails";
        }

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{flip}", output + ""), e.getAuthor())).build()).queue();

    }


    public static int CoinFlip() {
        int Coin = (int) (Math.random() * 2);
        if (Coin < 0) {
            Coin = Coin * -1;
        }
        if (Coin == 0) {
            CoinFlip();
        }
        return Coin;

    }
}
