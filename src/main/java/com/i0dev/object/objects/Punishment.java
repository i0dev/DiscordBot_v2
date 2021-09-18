package com.i0dev.object.objects;

import com.i0dev.utility.util.APIUtil;
import litebans.api.Entry;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Punishment {

    String type;
    String punishedUUID;
    String punishedIGN;
    String durationString;
    long durationEndTime;
    String reason;
    String staffUUID;
    String staffIGN;
    boolean silent;

    String proof;
    long proofSubmittedTime;
    long proofSubmittedById;
    boolean proofSubmitted;

    long punishID;
    long punishTime;

    public Punishment(Entry e) {
        this.type = e.getType();
        this.punishedUUID = e.getUuid();
        this.punishedIGN = APIUtil.getIGNFromUUID(e.getUuid());
        this.durationString = e.getDurationString();
        this.durationEndTime = e.getDateEnd();
        this.reason = e.getReason();
        this.staffUUID = e.getExecutorUUID();
        this.staffIGN = e.getExecutorName();
        this.silent = e.isSilent();

        this.proof = "";
        this.proofSubmitted = false;
        this.proofSubmittedById = 0;
        this.proofSubmittedTime = 0;

        this.punishID = e.getId();
        this.punishTime = System.currentTimeMillis();

    }

    public void addToCache() {

    }
}
