package main.java.com.i0dev.command.discord.fun;

import main.java.com.i0dev.object.Blacklist;

import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdCoinFlip extends ListenerAdapter {

    private final String Identifier = "CoinFlip";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.fun_coinFlip.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.fun_coinFlip.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.fun_coinFlip.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.fun_coinFlip.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.fun_coinFlip.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.fun_coinFlip.enabled");


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;

        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            String output = CoinFlip() + "";
            if (output.equals("0")) {
                output = "Heads";
            }
            if (output.equals("1")) {
                output = "Tails";
            }

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{flip}", output + ""), e.getAuthor())).build()).queue();

        }
    }

    public int CoinFlip() {
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