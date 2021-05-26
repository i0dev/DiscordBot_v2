package com.i0dev.modules.basic.cache;

import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;

public class HelpCmdCache {

    public ArrayList<Message> helpMessages = new ArrayList<>();

    private static HelpCmdCache instance = new HelpCmdCache();

    public static HelpCmdCache get() {
        return instance;
    }

    public ArrayList<Message> getList() {
        return this.helpMessages;
    }

}
