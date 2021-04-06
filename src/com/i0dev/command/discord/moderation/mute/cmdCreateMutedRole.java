package com.i0dev.command.discord.moderation.mute;

import com.i0dev.object.Blacklist;
import com.i0dev.utility.*;
import com.i0dev.utility.util.PermissionUtil;


import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class cmdCreateMutedRole extends ListenerAdapter {

    private final String Identifier = "Created Muted Role";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.createMutedRole.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.createMutedRole.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.createMutedRole.permissionLiteMode");
    private final String MESSAGE_CONTENT = getConfig.get().getString("commands.createMutedRole.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.createMutedRole.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.createMutedRole.enabled");


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

            Role role = e.getGuild().createRole().setName("Muted").setColor(java.awt.Color.darkGray).complete();
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{ID}", role.getId()), e.getAuthor())).build()).queue();
            for (TextChannel channel : e.getGuild().getTextChannels()) {
                channel.putPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE).queueAfter(5, TimeUnit.SECONDS);
            }
            for (VoiceChannel channel : e.getGuild().getVoiceChannels()) {
                channel.putPermissionOverride(role).setDeny(Permission.VOICE_SPEAK).queueAfter(5, TimeUnit.SECONDS);
            }
        }
    }
}
