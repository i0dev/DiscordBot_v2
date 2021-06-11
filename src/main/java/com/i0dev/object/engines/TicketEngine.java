package com.i0dev.object.engines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.i0dev.InitializeBot;
import com.i0dev.object.objects.Ticket;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.util.FileUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TicketEngine {

    private static TicketEngine instance = new TicketEngine();

    public static TicketEngine getInstance() {
        return instance;
    }

    @Setter
    @Getter
    private List<Object> cache = new ArrayList<>();

    public void add(Ticket object) {
        getCache().add(object);
        save();
    }

    public void remove(Ticket object) {
        getCache().remove(object);
        save();
    }

    public void remove(TextChannel channel) {
        getCache().remove(getObject(channel.getIdLong()));
        save();
    }

    public void setAdminOnly(Ticket ticket) {
        remove(ticket);
        ticket.setAdminOnlyMode(true);
        add(ticket);
    }

    public void clear() {
        getCache().clear();
        save();
    }

    public boolean isOnList(long channelID) {
        return cache.contains(getObject(channelID));
    }


    public Ticket getObject(long channelID) {
        for (Object singleton : getCache()) {
            Ticket object = (Ticket) singleton;
            if (object.getChannelID().equals(channelID)) {
                return object;
            }
        }
        return null;
    }

    public void save() {
        FileUtil.saveFile(cache, getPath());
    }

    public String getPath() {
        return InitializeBot.get().getTicketsPath();
    }

    public void load(JsonArray array) {
        for (JsonElement element : array) {
            JsonObject jsonObject = element.getAsJsonObject();
            Ticket ticket = new Ticket();
            ticket.setChannelID(jsonObject.get("channelID").getAsLong());
            ticket.setTicketOwnerID(jsonObject.get("ticketOwnerID").getAsLong());
            ticket.setTicketOwnerAvatarURL(jsonObject.get("ticketOwnerAvatarURL").getAsString());
            ticket.setTicketOwnerTag(jsonObject.get("ticketOwnerTag").getAsString());
            ticket.setAdminOnlyMode(jsonObject.get("adminOnlyMode").getAsBoolean());
            ticket.setTicketNumber(jsonObject.get("ticketNumber").getAsLong());
            getCache().add(ticket);
        }

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(9000);
            } catch (InterruptedException ignored) {
            }
            for (Object o : cache) {
                Ticket ticket = ((Ticket) o);
                if (InternalJDA.getJda().getTextChannelById(ticket.getChannelID()) == null) {
                    remove(ticket);
                }
            }
            save();
        });

    }
}
