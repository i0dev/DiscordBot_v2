package com.i0dev.commands.discord.completedModules.gamemode.skyblock;

import com.i0dev.object.objects.Type;
import com.i0dev.object.objects.RoleQueueObject;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Leader {
    private static final String Identifier = "Gamemode Island Leader";
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.gamemode.parts.skyblock.parts.leader.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.gamemode.parts.skyblock.parts.leader.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.gamemode.parts.skyblock.parts.leader.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.gamemode.parts.skyblock.parts.leader.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.gamemode.parts.skyblock.parts.leader.message.general");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Gamemode Skyblock Leader")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 3) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), SkyBlockModule.leaderMod, e.getAuthor());
            return;
        }
        User MentionedUser = FindFromString.get().getUser(message[3], e.getMessage());
        if (MentionedUser == null) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_USER_NOT_FOUND.replace("{arg}", message[3]), e.getAuthor())).build()).queue();
            return;

        }

        new RoleQueueObject(MentionedUser.getIdLong(), SkyBlockModule.ISLAND_LEADER_ROLE, Type.ADD_ROLE).add();

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT.replace("{senderTag}", e.getAuthor().getAsTag()), MentionedUser, e.getAuthor())).build()).queue();
    }
}
