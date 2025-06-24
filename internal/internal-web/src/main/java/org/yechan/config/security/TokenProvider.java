package org.yechan.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.yechan.dto.TokenHolder;
import org.yechan.error.GlobalErrorCode;
import org.yechan.error.GlobalException;

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
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(date)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .setId(UUID.randomUUID().toString())
                .compact();
        return new TokenHolder(accessToken);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new GlobalException("JWT token has expired", GlobalErrorCode.INVALID_REQUEST);
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT claims string is empty", e);
        }
    }

    public Authentication getAuthentication(String token) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        var roleKey = ClaimKey.ROLE.getKey();
        var authorities = Arrays.stream(claims.get(roleKey).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }
}
