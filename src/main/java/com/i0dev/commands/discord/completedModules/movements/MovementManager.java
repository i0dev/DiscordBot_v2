package com.i0dev.commands.discord.completedModules.movements;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MovementManager {

    public static final String NICKNAME_FORMAT = Configuration.getString("modules.movement.nicknameFormat");
    public static final String MESSAGE_NOT_STAFF = Configuration.getString("modules.movement.message.notStaff");

    public static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("**:arrow_up: Movement Commands :arrow_down:**").append("\n");

        builder.append(assign).append("\n");
        builder.append(promote).append("\n");
        builder.append(demote).append("\n");
        builder.append(resign).append("\n");
        builder.append(clear).append("\n");
        return builder.toString();
    }

    public static String assign = "`{prefix}movement Assign <user> <role>` *Assigns a user to that movement track.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String promote = "`{prefix}movement Promote <user>` *Promotes the user by one movement track.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String demote = "`{prefix}movement Demote <user>` *Demotes the user by one movement track.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String resign = "`{prefix}movement Resign <user>` *Resigns that user from the staff team.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);
    public static String clear = "`{prefix}movement Clear <user>` *Fully demotes and removes that user from the staff team.*".replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX);


    public static void run(GuildMessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 1) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
        } else {
            switch (message[1].toLowerCase()) {
                case "assign":
                    Assign.run(e);
                    break;
                case "promote":
                    Promote.run(e);
                    break;
                case "demote":
                    Demote.run(e);
                    break;
                case "resign":
                    Resign.run(e);
                    break;
                case "clear":
                    StaffClear.run(e);
                    break;
                default:

                    MessageUtil.sendMessage(e.getChannel().getIdLong(), usage(), e.getAuthor());
                    break;
            }
        }
    }
}

