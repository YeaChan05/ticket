package org.yechan.api.fixture;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.yechan.dto.request.UserRegisterRequest;

public class UserFixture {
    public static UserRegisterRequest generateUserRegisterRequest() {
        String uuid = UUID.randomUUID().toString();
        var username = generateUsername(uuid);

        var email = generateEmail(uuid);

        var phone = generatePhone();

        return new UserRegisterRequest(
                username,
                email,
                "Password123!",// 중복되어도 상관 없음
                phone
        );
    }

    private static String generateEmail(final String uuid) {
        String localPart = uuid.replace("-", "");
        return localPart + "@test.com";
    }

    private static String generateUsername(final String uuid) {
        return uuid.substring(0, 8);
    }

    private static String generatePhone() {
        int middle = ThreadLocalRandom.current().nextInt(1000, 10000);
        int last = ThreadLocalRandom.current().nextInt(1000, 10000);
        return String.format("010-%d-%d", middle, last);
    }
}
