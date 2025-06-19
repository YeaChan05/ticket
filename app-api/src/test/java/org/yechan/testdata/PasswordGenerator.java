package org.yechan.testdata;

public class PasswordGenerator {
    public static String generatePassword() {
        return "Password123!";
    }

    static String[] invalidPasswordRequests() {
        return new String[]{
                "short",
                "1234567",
                "abcdefgh",
                "ABCDEFGH",
                "12345678",
                "!@#$%^&*",
                "abc123!@#"
        };
    }
}
