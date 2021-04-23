package com.i0dev.commands.discord.fun;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class CommandDice {

    public static final String Identifier = "Dice";
    private final List<String> COMMAND_ALIASES = Configuration.getStringList("commands.fun_dice.aliases");
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.fun_dice.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.fun_dice.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.fun_dice.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.fun_dice.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.fun_dice.enabled");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Dice")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.DICE_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        int Coin = (int) (Math.random() * 6);
        if (Coin < 0) {
            Coin = Coin * -1;
        }
        if (Coin == 0) {
            Coin = 6;
        }

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{roll}", Coin + ""), e.getAuthor())).build()).queue();

    }
}

