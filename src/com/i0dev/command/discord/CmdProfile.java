package com.i0dev.command.discord;

import com.i0dev.object.Blacklist;
import com.i0dev.object.InviteMatcher;
import com.i0dev.object.Invites;
import com.i0dev.utility.*;

import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class CmdProfile extends ListenerAdapter {

    private final String Identifier = "Profile";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.profile.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.profile.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.profile.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.profile.messageTitle");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.profile.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.profile.enabled");


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
            if (message.length > 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            User MentionedUser = null;
            if (message.length == 2) {
                MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
                if (MentionedUser == null) {
                    e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                    return;
                }
            } else if (message.length == 1) {
                MentionedUser = e.getAuthor();
            }


            StringBuilder desc = new StringBuilder();
            desc.append("**User Information:** ").append("`{userTag} ({userID})`".replace("{userID}", MentionedUser.getId()).replace("{userTag}", MentionedUser.getAsTag())).append("\n");
            desc.append("**Total Tickets Closed:** ").append("`COMING SOON`").append("\n");
            desc.append("**Invited By:** ").append("`" + (InviteMatcher.get().getNewJoinObject(MentionedUser) == null ? "No Log" : InviteMatcher.get().getNewJoinObject(MentionedUser)) + "`").append("\n");
            desc.append("**Total Invites:** ").append("`" + Invites.get().getUserInviteCount(MentionedUser) + "`").append("\n");
            desc.append("**Linked Status:** ").append("`COMING SOON`").append("\n");
            desc.append("**Linked IGN:** ").append("`COMING SOON`").append("\n");

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TITLE, e.getAuthor()), Placeholders.convert(desc.toString(), e.getAuthor()), MentionedUser.getEffectiveAvatarUrl()).build()).queue();

        }
    }
}