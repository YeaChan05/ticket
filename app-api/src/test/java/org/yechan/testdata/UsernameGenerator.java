package org.yechan.testdata;

import java.util.UUID;

public class UsernameGenerator {
    public static String generateUsername() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8);
    }
}
