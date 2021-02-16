package main.java.com.i0dev.command.basic;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdMemberCount extends ListenerAdapter {

    private final String Identifier = "Member Count";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.memberCount.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.memberCount.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.memberCount.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.memberCount.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.memberCount.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.memberCount.enabled");


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

            String description = MESSAGE_CONTENT
                    .replace("{BotCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getUser().isBot()).count())
                    .replace("{memberCount}", "" + e.getGuild().getMemberCount())
                    .replace("{HumanCount}", "" + e.getGuild().getMembers().stream().filter(member -> !member.getUser().isBot()).count())
                    .replace("{onlineCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE)).count())
                    .replace("{DNDCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)).count())
                    .replace("{idleCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.IDLE)).count())
                    .replace("{offlineCount}", "" + e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count());


            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(description, e.getAuthor())).build()).queue();

        }
    }
}