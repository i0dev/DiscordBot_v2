package main.java.com.i0dev.command;


import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;

public class HelpPageCache {

    public ArrayList<Message> helpMessages = new ArrayList<>();

    private static HelpPageCache instance = new HelpPageCache();

    public static HelpPageCache get() {
        return instance;
    }

    public ArrayList<Message> getList() {
        return this.helpMessages;
    }

}
