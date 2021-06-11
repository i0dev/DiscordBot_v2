package com.i0dev.utility.util;

import com.i0dev.utility.GlobalConfig;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class TempNicknameUtil {

    public static void modifyNickname(User user, String nickname) {
        if (GlobalConfig.GENERAL_MAIN_GUILD.getMember(user).getEffectiveName().equals(nickname)) return;
        System.out.println("Sent a request to change " + user.getAsTag() + "'s nickname");
        GlobalConfig.GENERAL_MAIN_GUILD.getMember(user).modifyNickname(nickname).queue(null, throwable -> {
            if (!(throwable instanceof HierarchyException)) {
                throwable.printStackTrace();
            }
        });
    }
}
