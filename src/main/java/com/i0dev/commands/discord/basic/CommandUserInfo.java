package com.i0dev.commands.discord.basic;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandUserInfo {

    public static final String Identifier = "User Info";
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.userInfo.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.userInfo.permissionLiteMode");
    public static final String MESSAGE_TITLE = Configuration.getString("commands.userInfo.messageTitle");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.userInfo.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.userInfo.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.userInfo.enabled");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "User Info")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 2) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.USER_INFO_COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
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
                .replace("{userJoinDate}", Joined[1] + "/" + Joined[2].substring(0, 2) + "/" + Joined[0])
                .replace("{userCreatedDate}", Created[1] + "/" + Created[2].substring(0, 2) + "/" + Created[0])
                .replace("{isBot}", MentionedUser.isBot() + "")
                .replace("{onlineStatus}", MentionedMember.getOnlineStatus().getKey() + "")
                .replace("{boosting}", GlobalConfig.GENERAL_MAIN_GUILD.getBoosters().contains(MentionedMember) + "")
                .replace("{voiceState}", VoiceState)
                .replace("{roleList}", FormatUtil.FormatList(MentionedMember.getRoles()))
                .replace("{permissionList}", FormatUtil.FormatList(MentionedMember.getPermissions()));
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_TITLE, null, MentionedUser), Placeholders.convert(description, null, MentionedUser), null, MentionedUser.getEffectiveAvatarUrl()).build()).queue();


    }
}