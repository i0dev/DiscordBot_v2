package com.i0dev.commands.discord.completedModules.linking;

import com.i0dev.object.discordLinking.*;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandLink implements CommandExecutor {

    private static final List<String> INGAME_MESSAGE_CODE_NOT_VALID = Configuration.getStringList("modules.link.parts.code.message.ingameCodeNotValid");
    private static final List<String> INGAME_CODE_MESSAGE = Configuration.getStringList("modules.link.parts.generate.message.ingameCodeMessage");
    private static final List<String> MESSAGE_INGAME_CONTENT = Configuration.getStringList("modules.link.parts.code.message.ingameGeneral");


    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.link.parts.code.message.logMessage");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.link.parts.code.option.log");

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("generate")) {

            DPlayer dPlayer = DPlayerEngine.getInstance().getObjectFromIGN(commandSender.getName());
            if (dPlayer != null && dPlayer.getLinkInfo().isLinked()) {
                List<String> formattedMsg = new ArrayList<>();
                for (String message : LinkManager.INGAME_MESSAGE_ALREADY_LINKED) {
                    formattedMsg.add(Placeholders.convert(message.replace("{ign}", commandSender.getName()), InternalJDA.get().getJda().getUserById(dPlayer.getDiscordID())));
                }
                MessageUtil.sendMessageIngame(((Player) commandSender), formattedMsg);
                return false;
            }

            String code = FormatUtil.GenerateRandomString();


            List<String> formattedMsg = new ArrayList<>();
            for (String message : INGAME_CODE_MESSAGE) {
                formattedMsg.add(Placeholders.convert(message.replace("{code}", code).replace("{ign}", commandSender.getName())));
            }
            MessageUtil.sendMessageIngame(((Player) commandSender), formattedMsg);


            From_IngameCodeLinker codeLinker = new From_IngameCodeLinker((Player) commandSender, code);
            CodeCache.getInstance().getFrom_Ingame_cache().add(codeLinker);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("code")) {


            DPlayer dPlayer = DPlayerEngine.getInstance().getObjectFromIGN(commandSender.getName());
            if (dPlayer != null && dPlayer.getLinkInfo().isLinked()) {
                List<String> formattedMsg = new ArrayList<>();
                for (String message : LinkManager.INGAME_MESSAGE_ALREADY_LINKED) {
                    formattedMsg.add(Placeholders.convert(message.replace("{ign}", commandSender.getName()), InternalJDA.get().getJda().getUserById(dPlayer.getDiscordID())));
                }
                MessageUtil.sendMessageIngame(((Player) commandSender), formattedMsg);
                return false;
            }


            String code = args[1];
            From_DiscordCodeLinker codeLinker = CodeCache.getInstance().getObjectDiscord(code);
            if (codeLinker == null) {

                List<String> formattedMsg = new ArrayList<>();
                for (String message : INGAME_MESSAGE_CODE_NOT_VALID) {
                    formattedMsg.add(Placeholders.convert(message.replace("{code}", code).replace("{ign}", commandSender.getName())));
                }
                MessageUtil.sendMessageIngame(((Player) commandSender), formattedMsg);
                return false;
            }

            DPlayerEngine.getInstance().setLinked(codeLinker.getUser(), code, commandSender.getName(), ((Player) commandSender).getUniqueId().toString());

            List<String> formattedMsg = new ArrayList<>();
            for (String message : MESSAGE_INGAME_CONTENT) {
                formattedMsg.add(Placeholders.convert(message
                        .replace("{ign}", commandSender.getName()), codeLinker.getUser()));
            }
            MessageUtil.sendMessageIngame(((Player) commandSender), formattedMsg);

            if (OPTION_LOG) {
                String logMsg = MESSAGE_LOG_MESSAGE
                        .replace("{code}", code)
                        .replace("{ign}", commandSender.getName())
                        .replace("{uuid}", ((Player) commandSender).getUniqueId().toString());
                MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, logMsg, codeLinker.getUser());
            }

            return true;
        }

        //format
        commandSender.sendMessage(FormatUtil.c("&9&l/link command:"));
        commandSender.sendMessage(FormatUtil.c("&c/link generate &f- &7Generates a code to link your account to discord."));
        commandSender.sendMessage(FormatUtil.c("&c/link code <code> &f- &7Links your account with that code."));
        commandSender.sendMessage(FormatUtil.c("&f"));


        return true;
    }
}
