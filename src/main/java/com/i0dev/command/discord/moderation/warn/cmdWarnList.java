package main.java.com.i0dev.command.discord.moderation.warn;

import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.object.Warning;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class cmdWarnList extends ListenerAdapter {

    private final String Identifier = "Warns List";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.warns_list.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.warns_list.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.warns_list.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.warns_list.messageTitle");
    private final String LIST_FORMAT = getConfig.get().getString("commands.warns_list.listFormat");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.warns_list.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.warns_list.enabled");


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


            ArrayList<JSONObject> list = Warning.get().getCache();
            list.sort(Comparator.comparing(o -> Integer.parseInt(o.get("warnings").toString())));
            Collections.reverse(list);
            StringBuilder desc = new StringBuilder();
            for (JSONObject obj : list) {
                User user = e.getJDA().getUserById(obj.get("userID").toString());
                if (user == null) continue;
                desc.append(main.java.com.i0dev.utility.Placeholders.convert(LIST_FORMAT
                        .replace("{warnings}", NumberFormat.getNumberInstance().format(Integer.parseInt(obj.get("warnings").toString())))
                        .replace("{place}", (list.indexOf(obj) + 1) + ""), user));
            }
            e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(MESSAGE_TITLE, e.getAuthor()), main.java.com.i0dev.utility.Placeholders.convert(desc.toString(), e.getAuthor())).build()).queue();






        }
    }
}