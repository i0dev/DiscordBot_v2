package com.i0dev.object.engines;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionHandler {

    private boolean strict;
    private boolean lite;
    private boolean admin;

    public PermissionHandler(boolean lite, boolean strict, boolean admin) {
        this.strict = strict;
        this.admin = admin;
        this.lite = lite;
    }
}
