package com.i0dev.object.objects;

import com.i0dev.Engine;
import lombok.Getter;

@Getter
public class RoleQueueObject {

    Long userID;
    Long roleID;
    Type type;

    public RoleQueueObject(Long userID, Long roleID, Type type) {
        this.userID = userID;
        this.roleID = roleID;
        this.type = type;
    }

    public void add() {
        Engine.getRoleQueueList().add(this);
    }


}