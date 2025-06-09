package org.yechan.api.fixture;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.yechan.dto.request.UserRegisterRequest;

public class UserFixture {
    private static final String uuid = UUID.randomUUID().toString();


    public static String generateEmail() {
        String localPart = uuid.replace("-", "");
        return localPart + "@test.com";
    }

    public static String generateUsername() {
        return uuid.substring(0, 8);
    }

    public static String generatePhone() {
        int middle = ThreadLocalRandom.current().nextInt(1000, 10000);
        int last = ThreadLocalRandom.current().nextInt(1000, 10000);
        return String.format("010-%d-%d", middle, last);
    }

    public static UserRegisterRequest generateUserRegisterRequest() {
        var username = generateUsername();

        var email = generateEmail();

        var phone = generatePhone();

        return new UserRegisterRequest(
                username,
                email,
                "Password123!",// 중복되어도 상관 없음
                phone
        );
    }
}
