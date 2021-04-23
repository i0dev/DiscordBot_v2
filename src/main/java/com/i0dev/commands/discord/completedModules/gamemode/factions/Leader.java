package com.i0dev.commands.discord.completedModules.gamemode.factions;

import com.i0dev.engine.discord.RoleQueue;
import com.i0dev.engine.discord.Type;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Leader {
    private static final String Identifier = "Gamemode Faction Leader";
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.gamemode.parts.factions.parts.leader.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.gamemode.parts.factions.parts.leader.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.gamemode.parts.factions.parts.leader.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.gamemode.parts.factions.parts.leader.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.gamemode.parts.factions.parts.leader.message.general");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Gamemode Factions Leader")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 3) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), FactionsModule.leaderMod, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[3], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[3]), e.getAuthor())).build()).queue();
            return;
        }

        RoleQueue.addToQueue(MentionedUser.getIdLong(), FactionsModule.FACTION_LEADER_ROLE, Type.ADD_ROLE);

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{senderTag}", e.getAuthor().getAsTag()), MentionedUser,e.getAuthor())).build()).queue();
    }
}