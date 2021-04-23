package com.i0dev.object.objects;

import com.i0dev.object.engines.SuggestionEngine;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Suggestion {

    private Long userID;
    private String suggestion;
    private String userTag;
    private Long messageID;
    private Long channelID;
    private String userAvatarUrl;
    private boolean accepted;
    private boolean rejected;


    public Suggestion() {
        this.userID = 0L;
        this.suggestion = "";
        this.messageID = 0L;
        this.channelID = 0L;
        this.userTag = "";
        this.userAvatarUrl = "";
        this.accepted = false;
        this.rejected = false;
    }

    public void addToCache() {
        SuggestionEngine.getInstance().add(this);
    }

}