package com.i0dev.command.discord.fun;

import com.i0dev.object.Blacklist;

import com.i0dev.utility.*;

import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdDice extends ListenerAdapter {

    private final String Identifier = "Dice";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.fun_dice.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.fun_dice.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.fun_dice.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.fun_dice.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.fun_dice.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.fun_dice.enabled");


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
            int Coin = (int) (Math.random() * 6);
            if (Coin < 0) {
                Coin = Coin * -1;
            }
            if (Coin == 0) {
                Coin = 6;
            }

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{roll}", Coin + ""), e.getAuthor())).build()).queue();

        }
    }
}
