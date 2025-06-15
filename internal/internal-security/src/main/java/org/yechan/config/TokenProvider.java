package org.yechan.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.yechan.dto.TokenHolder;

@Component
public class TokenProvider {
    private final Key key;
    private final long accessExpiration;

    public TokenProvider(SecurityConfigurationProperties properties) {
        this.accessExpiration = properties.jwt().accessExpiration();
        byte[] keyBytes = Base64.getDecoder().decode(properties.jwt().secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenHolder createAccessToken(String subject, Map<String, ?> claims) {
        var date = new Date();
        long now = date.getTime();
        Date validity = new Date(now + this.accessExpiration * 1000);

        var accessToken = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(date)
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
        return new TokenHolder(accessToken);
    }

}
