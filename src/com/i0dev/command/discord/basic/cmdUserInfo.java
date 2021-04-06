package com.i0dev.command.discord.basic;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;

import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdUserInfo extends ListenerAdapter {

    private final String Identifier = "User Info";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.userInfo.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.userInfo.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.userInfo.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.userInfo.messageTitle");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.userInfo.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.userInfo.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.userInfo.enabled");


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
            if (message.length != 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }
            Member MentionedMember = GlobalConfig.GENERAL_MAIN_GUILD.getMember(MentionedUser);


            String VoiceState = "Not Connected";
            if (MentionedMember.getVoiceState() != null) {
                VoiceState = "Normal";
                if (MentionedMember.getVoiceState().isGuildDeafened()) VoiceState = "Server Deafened";
                else if (MentionedMember.getVoiceState().isGuildMuted()) VoiceState = "Server Muted";
                else if (MentionedMember.getVoiceState().isSelfDeafened()) VoiceState = "Deafened";
                else if (MentionedMember.getVoiceState().isSelfMuted()) VoiceState = "Muted";
            }

            String[] Joined = MentionedMember.getTimeJoined().toString().split("-");
            String[] Created = MentionedUser.getTimeCreated().toString().split("-");
            String description = MESSAGE_CONTENT.replace("{userID}", MentionedUser.getId())
                    .replace("{userName}", MentionedUser.getName())
                    .replace("{userTag}", MentionedUser.getAsTag())
                    .replace("{userJoinDate}", Joined[1] + "/" + Joined[2].substring(0, 2) + "/" + Joined[0])
                    .replace("{userCreatedDate}", Created[1] + "/" + Created[2].substring(0, 2) + "/" + Created[0])
                    .replace("{isBot}", MentionedUser.isBot() + "")
                    .replace("{userMention}", MentionedUser.getAsMention())
                    .replace("{onlineStatus}", MentionedMember.getOnlineStatus().getKey() + "")
                    .replace("{boosting}", GlobalConfig.GENERAL_MAIN_GUILD.getBoosters().contains(MentionedMember) + "")
                    .replace("{voiceState}", VoiceState)
                    .replace("{roleList}", FormatUtil.FormatList(MentionedMember.getRoles()))
                    .replace("{permissionList}", FormatUtil.FormatList(MentionedMember.getPermissions()));
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TITLE, e.getAuthor()), Placeholders.convert(description, e.getAuthor()), MentionedUser.getEffectiveAvatarUrl()).build()).queue();

        }
    }
}