package com.i0dev.object.objects;

import com.i0dev.object.engines.TicketEngine;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Ticket {

    private Long channelID;
    private Long ticketOwnerID;
    private String ticketOwnerAvatarURL;
    private String ticketOwnerTag;
    private boolean adminOnlyMode;
    private Long ticketNumber;


    public Ticket() {
        this.channelID = 0L;
        this.ticketOwnerID = 0L;
        this.ticketOwnerAvatarURL = "";
        this.ticketOwnerTag = "";
        this.adminOnlyMode = false;
        this.ticketNumber = 0L;
    }

    public void addToCache() {
        TicketEngine.getInstance().add(this);
    }

}