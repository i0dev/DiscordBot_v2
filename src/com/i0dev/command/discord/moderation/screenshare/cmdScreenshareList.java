package com.i0dev.command.discord.moderation.screenshare;

import com.i0dev.object.Blacklist;
import com.i0dev.object.Screenshare;
import com.i0dev.utility.*;
import com.i0dev.utility.util.PermissionUtil;


import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.List;

public class cmdScreenshareList extends ListenerAdapter {

    private final String Identifier = "Screenshare List";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.screenshare_list.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.screenshare_list.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.screenshare_list.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.screenshare_list.messageTitle");
    private final String LIST_FORMAT = getConfig.get().getString("commands.screenshare_list.listFormat");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.screenshare_list.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.screenshare_list.enabled");


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

            StringBuilder description = new StringBuilder();

            for (JSONObject screenshare : Screenshare.get().getScreenshareList()) {
                description.append(LIST_FORMAT
                        .replace("{IGN}", screenshare.get("userID").toString())
                        .replace("{punisherTag}", screenshare.get("punisherTag").toString())
                        .replace("{reason}", screenshare.get("reason").toString()));
                description.append("\n");

            }

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TITLE, e.getAuthor()), description.toString()).build()).queue();

        }
    }
}
