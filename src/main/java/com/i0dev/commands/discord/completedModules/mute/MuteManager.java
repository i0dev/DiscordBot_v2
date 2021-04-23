package com.i0dev.commands.discord.completedModules.mute;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MuteManager {

    public static final String MESSAGE_ROLE_NOT_FOUND = Configuration.getString("modules.mute.message.roleNotFound");

    public static final Role ROLE_MUTED_ROLE = GlobalConfig.GENERAL_MAIN_GUILD.getRoleById(Configuration.getLong("roles.mutedRoleID"));

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:mute: Mute Commands :mute:**").append("\n");

        builder.append(add).append("\n");
        builder.append(remove).append("\n");
        builder.append(create).append("\n");
        builder.append(list).append("\n");
        builder.append(clear).append("\n");
        return builder.toString();
    }

    public static String add = "`{prefix}mute Add <user>` *Mutes a user from speaking in any channel.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String remove = "`{prefix}mute Remove <user>` *Lifts the mute from a muted user.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String create = "`{prefix}mute CreateRole` *Creates the a Muted role.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String list = "`{prefix}mute List` *Retrieves the list of muted members.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String clear = "`{prefix}mute Clear` *Clears the list of muted members*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[1].toLowerCase()) {
                case "add":
                    Add.run(e);
                    break;
                case "remove":
                    Remove.run(e);
                    break;
                case "list":
                    Retrieve.run(e);
                    break;
                case "clear":
                    Clear.run(e);
                    break;
                case "create":
                    CreateRole.run(e);
                    break;
                default:
                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}