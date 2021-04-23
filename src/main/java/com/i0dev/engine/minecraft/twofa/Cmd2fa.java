package com.i0dev.engine.minecraft.twofa;

import com.i0dev.utility.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmd2fa implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!Cache.getInstance().getCache().contains(((Player) commandSender))) return false;

        if (args.length != 1 || !TwoFactorAuthentication.isCodeValid(args[0])) {
            MessageUtil.sendMessageIngame(((Player) commandSender), TwoFactorAuthentication.INGAME_INVALID_CODE.replace("{code}", args.length != 1 ? "No Code Provided" : args[0]));
            return false;
        }

        TwoFactor twoFactor = TwoFactorAuthentication.getObject(((Player) commandSender));
        if (twoFactor.getCode().equalsIgnoreCase(args[0])) {

            Cache.getInstance().getTwoFactorCache().remove(twoFactor);
            Cache.getInstance().getCache().remove(((Player) commandSender));
            MessageUtil.sendMessageIngame(((Player) commandSender), TwoFactorAuthentication.INGAME_SUCSESS_MESSAGE);

            return true;
        } else {
            MessageUtil.sendMessageIngame(((Player) commandSender), TwoFactorAuthentication.INGAME_INVALID_CODE.replace("{code}", args[0]));

        }
        MessageUtil.sendMessageIngame(((Player) commandSender), "&c/2fa <code>");

        return false;
    }
}
