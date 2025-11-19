package com.auth_service.util;

import de.huxhorn.sulky.ulid.ULID;
import org.springframework.stereotype.Component;

@Component
public class ULIDGenerator {

    private static final ULID ulid = new ULID();

    // Generate new ULID
    public static String generate() {
        return ulid.nextULID();
    }

}
