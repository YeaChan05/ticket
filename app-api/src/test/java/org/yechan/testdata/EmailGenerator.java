package org.yechan.testdata;

import java.util.UUID;

public class EmailGenerator {
    public static String generateEmail() {
        String uuid = UUID.randomUUID().toString();
        String localPart = uuid.replace("-", "");
        return localPart + "@test.com";
    }

    public static String[] invalidEmailRequests() {
        return new String[]{
                "plainaddress",
                "@missinglocalpart.com",
                "missingat.com",
                "missingdomain@.com",
                "user@.com",
                "user@domain..com",
                "user@domain,com",
                "user@domain space.com",
                "user@domain#com",
                "user@domain@com",
                "user@domain..com",
        };
    }
}
