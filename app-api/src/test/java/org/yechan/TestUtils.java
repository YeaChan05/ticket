package org.yechan;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;

public class TestUtils {
    public static String decodeBase64Url(String base64UrlString) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(base64UrlString);
        return new String(decodedBytes, UTF_8);
    }
}
