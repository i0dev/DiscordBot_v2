package main.java.com.i0dev.command.discord.moderation.mute;

import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.object.Blacklist;

import main.java.com.i0dev.utility.util.FormatUtil;
import main.java.com.i0dev.utility.util.MessageUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdMute extends ListenerAdapter {

    private final String Identifier = "Mute User";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.mute.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.mute.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.mute.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.mute.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.mute.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.mute.enabled");
    private final boolean LOGS_ENABLED = getConfig.get().getBoolean("commands.mute.log");
    private final String LOGS_MESSAGE = getConfig.get().getString("commands.mute.logMessage");
    private final String ROLE_NOT_FOUND = getConfig.get().getString("commands.mute.roleNotFoundError");
    private final String USER_ALREADY_MUTED = getConfig.get().getString("commands.mute.userAlreadyMuted");


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
            if (message.length == 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }
            Member MentionedMember = GlobalConfig.GENERAL_MAIN_GUILD.getMember(MentionedUser);

            String reason = FormatUtil.remainingArgFormatter(message, 2);
            Role ROLE_MUTED_ROLE = InternalJDA.get().getJda().getGuildById(getConfig.get().getLong("general.guildID")).getRoleById(getConfig.get().getLong("roles.mutedRoleID"));
            if (ROLE_MUTED_ROLE == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(ROLE_NOT_FOUND, e.getAuthor())).build()).queue();
                return;
            }

            if (MentionedMember.getRoles().contains(ROLE_MUTED_ROLE)) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(USER_ALREADY_MUTED, MentionedUser)).build()).queue();
                return;
            }
            e.getGuild().addRoleToMember(MentionedMember, ROLE_MUTED_ROLE).queue();

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{reason}", reason), MentionedUser)).build()).queue();

            if (LOGS_ENABLED) {
                MessageUtil.sendMessage( GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL,EmbedFactory.get().createSimpleEmbed(LOGS_MESSAGE
                        .replace("{userTag}", MentionedUser.getAsTag())
                        .replace("{punisherTag}", e.getAuthor().getAsTag())
                        .replace("{reason}", reason))
                        .build());
            }
        }
    }
}
