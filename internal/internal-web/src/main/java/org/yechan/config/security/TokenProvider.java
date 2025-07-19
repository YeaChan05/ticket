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

    public boolean isValidToken(String token) {
        try {
            var jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            var algorithm = jws.getHeader().getAlgorithm();

            if (!SignatureAlgorithm.HS256.getValue().equals(algorithm)) {
                throw new GlobalException("Unsupported signature algorithm", GlobalErrorCode.UNSUPPORTED_SIGNATURE_ALGORITHM);
            }
            return true;
        } catch (ExpiredJwtException e) {
            throw new GlobalException("Token expired", GlobalErrorCode.TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new GlobalException("Invalid signature", GlobalErrorCode.INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new GlobalException("Malformed token", GlobalErrorCode.MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new GlobalException("Unsupported token", GlobalErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new GlobalException("Token is empty or null", GlobalErrorCode.EMPTY_TOKEN);
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
                .map(String::trim)
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(claims.get("email"), null, authorities);
    }
}
