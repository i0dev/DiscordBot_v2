package main.java.com.i0dev.command.discord.moderation.mute;

import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.object.Blacklist;

import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class cmdGetMuted extends ListenerAdapter {

    private final String Identifier = "Get Muted";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.getMuted.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.getMuted.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.getMuted.permissionLiteMode");
    private final String MESSAGE_TITLE = getConfig.get().getString("commands.getMuted.messageTitle");
    private final String LIST_FORMAT = getConfig.get().getString("commands.getMuted.listFormat");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.getMuted.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.getMuted.enabled");
    private final String ROLE_NOT_FOUND = getConfig.get().getString("commands.mute.roleNotFoundError");


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

            Role ROLE_MUTED_ROLE = InternalJDA.get().getJda().getGuildById(getConfig.get().getLong("general.guildID")).getRoleById(getConfig.get().getLong("roles.mutedRoleID"));
            if (ROLE_MUTED_ROLE == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(ROLE_NOT_FOUND, e.getAuthor())).build()).queue();
                return;
            }
            StringBuilder description = new StringBuilder();

            for (Member user : e.getGuild().getMembers()) {
                if (user.getRoles().contains(ROLE_MUTED_ROLE)) {
                    description.append(LIST_FORMAT
                            .replace("{userTag}", user.getUser().getAsTag()));
                    description.append("\n");
                }
            }

            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_TITLE, e.getAuthor()), description.toString()).build()).queue();

        }
    }
}
