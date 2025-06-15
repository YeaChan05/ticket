package org.yechan;

import java.util.UUID;

public class EmailGenerator {
    public static String generateEmail() {
        String uuid = UUID.randomUUID().toString();
        String localPart = uuid.replace("-", "");
        return localPart + "@test.com";
    }
}
