package org.yechan.testdata;

import java.util.concurrent.ThreadLocalRandom;

public class PhoneNumberGenerator {
    public static String generatePhone() {
        int middle = ThreadLocalRandom.current().nextInt(0, 10000);
        int last = ThreadLocalRandom.current().nextInt(0, 10000);
        return String.format("010-%04d-%04d", middle, last);
    }
}
