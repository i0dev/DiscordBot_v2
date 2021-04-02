package main.java.com.i0dev.command.discord.invite;


import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.object.Invites;
import main.java.com.i0dev.utility.GlobalConfig;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.text.NumberFormat;
import java.util.*;

public class cmdInviteLeaderboard extends ListenerAdapter {


    private final String Identifier = "Invite Leaderboard";
    private final List<String> COMMAND_ALIASES = main.java.com.i0dev.utility.getConfig.get().getStringList("commands.inviteLeaderboard.aliases");
    private final boolean REQUIRE_PERMISSIONS = main.java.com.i0dev.utility.getConfig.get().getBoolean("commands.inviteLeaderboard.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = main.java.com.i0dev.utility.getConfig.get().getBoolean("commands.inviteLeaderboard.permissionLiteMode");
    private final String messageTitle = main.java.com.i0dev.utility.getConfig.get().getString("commands.inviteLeaderboard.messageTitle");
    private final String leaderboardFormat = main.java.com.i0dev.utility.getConfig.get().getString("commands.inviteLeaderboard.leaderboardFormat");
    private final String MESSAGE_FORMAT = main.java.com.i0dev.utility.getConfig.get().getString("commands.inviteLeaderboard.format");
    private final boolean COMMAND_ENABLED = main.java.com.i0dev.utility.getConfig.get().getBoolean("commands.inviteLeaderboard.enabled");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;

        if (main.java.com.i0dev.utility.MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();
            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length != 1) {
                e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }

            ArrayList<JSONObject> list = Invites.get().getCache();
            list.sort(Comparator.comparing(o -> Integer.parseInt(o.get("invites").toString())));
            Collections.reverse(list);
            StringBuilder desc = new StringBuilder();
            int maxEntries = 0;
            for (JSONObject obj : list) {
                if (maxEntries >= 25) { break; }
                User user = e.getJDA().getUserById(obj.get("userID").toString());
                if (user == null) continue;
                desc.append(main.java.com.i0dev.utility.Placeholders.convert(leaderboardFormat
                        .replace("{invites}", NumberFormat.getNumberInstance().format(Integer.parseInt(obj.get("invites").toString())))
                        .replace("{place}", (list.indexOf(obj) + 1) + ""), user));
                maxEntries++;
            }
            e.getChannel().sendMessage(main.java.com.i0dev.utility.EmbedFactory.get().createSimpleEmbed(main.java.com.i0dev.utility.Placeholders.convert(messageTitle, e.getAuthor()), main.java.com.i0dev.utility.Placeholders.convert(desc.toString(), e.getAuthor())).build()).queue();
        }
    }
}