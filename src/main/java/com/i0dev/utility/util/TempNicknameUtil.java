package com.i0dev.utility.util;

import com.i0dev.utility.GlobalConfig;
import net.dv8tion.jda.api.entities.User;

public class TempNicknameUtil {

    public static void modifyNickname(User user, String nickname) {
        try {
            GlobalConfig.GENERAL_MAIN_GUILD.getMember(user).modifyNickname(nickname).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
