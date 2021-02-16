package main.java.com.i0dev.command.movements;

import main.java.com.i0dev.util.*;
import main.java.com.i0dev.entity.Blacklist;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.List;

public class cmdPromote extends ListenerAdapter {

    private final String Identifier = "Promote User";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.promote.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.promote.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.promote.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.promote.messageContent");
    private final String movementMessage = getConfig.get().getString("commands.promote.movementMessage");
    private final String userMaxPosition = getConfig.get().getString("commands.promote.userMaxPosition");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.promote.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.promote.enabled");
    private final String staffMovementsChannelID = getConfig.get().getString("channels.staffMovementsChannel");
    private final String nicknameFormat = getConfig.get().getString("commands.promote.nicknameFormat");


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
            if (message.length != 2) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", conf.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            User MentionedUser = FindFromString.get().getUser(message[1], e.getMessage());
            if (MentionedUser == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[1]), e.getAuthor())).build()).queue();
                return;
            }
            Member MentionedMember = e.getGuild().getMember(MentionedUser);

            if (!Movements.isAlreadyStaff(MentionedMember)) {
                JSONObject firstRoleObject = Movements.getTracks().get(0);
                Movements.giveNewRoles(MentionedMember, (Long) Movements.getTracks().get(0).get("mainRole"));
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(movementMessage.replace("{position}", firstRoleObject.get("displayName").toString()).replace("{senderTag}", e.getAuthor().getAsTag()), MentionedUser)).build()).queue();
                e.getGuild().getTextChannelById(staffMovementsChannelID).sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{position}", firstRoleObject.get("displayName").toString()).replace("{senderTag}", e.getAuthor().getAsTag()), MentionedUser)).build()).queue();
                return;
            }
            if (Movements.isHighestStaff(MentionedMember)) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(userMaxPosition, MentionedUser)).build()).queue();
                return;
            }

            Role currentParentRole = Movements.getParentStaff(MentionedMember);
            Role nextRole = Movements.getNextRole(currentParentRole);
            JSONObject nextRoleObject = Movements.getNextRoleObject(currentParentRole);

            Movements.removeOldRoles(MentionedMember, Long.valueOf(currentParentRole.getId()));
            Movements.giveNewRoles(MentionedMember, Long.valueOf(nextRole.getId()));
            try{
            MentionedMember.modifyNickname(nicknameFormat.replace("{userName}", MentionedUser.getName()).replace("{displayName}", nextRoleObject.get("displayName").toString())).queue();
        }catch (Exception ignored){

        }
            e.getGuild().getTextChannelById(staffMovementsChannelID).sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(movementMessage.replace("{position}", nextRoleObject.get("displayName").toString()).replace("{senderTag}", e.getAuthor().getAsTag()), MentionedUser)).build()).queue();
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{position}", nextRoleObject.get("displayName").toString()).replace("{senderTag}", e.getAuthor().getAsTag()), MentionedUser)).build()).queue();


        }
    }
}
