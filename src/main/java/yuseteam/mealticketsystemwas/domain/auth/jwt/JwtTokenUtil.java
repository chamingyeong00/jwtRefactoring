package yuseteam.mealticketsystemwas.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static yuseteam.mealticketsystemwas.domain.auth.jwt.JwtClaims.*;

@Component
public class JwtTokenUtil {

    private final SecretKey secretKey;

    public JwtTokenUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public boolean isExpired(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp != null && exp.before(new Date());
    }

    /**
     * payload 예시
     * {
     *   "sub": "123",
     *   "role": "USER",
     *   "type": "access",
     *   "tokenVersion": 0,
     *   "iat": 1710000000,
     *   "exp": 1710003600
     * }
     */
    public String createJwt(Long userId, String role, String type, long expiredMs, Integer tokenVersion) {
        long nowMs = System.currentTimeMillis();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TYPE, type)
                .claim(CLAIM_TOKEN_VERSION, tokenVersion == null ? 0 : tokenVersion)
                .issuedAt(new Date(nowMs))
                .expiration(new Date(nowMs + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Long parseUserId(String token) {
        String sub = parseClaims(token).getSubject();
        if (sub == null || sub.isBlank()) return null;
        try {
            return Long.parseLong(sub);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String parseType(String token) {
        return parseClaims(token).get(CLAIM_TYPE, String.class);
    }

    public Integer parseTokenVersion(String token) {
        Object verObj = parseClaims(token).get(CLAIM_TOKEN_VERSION);
        if (verObj == null) return null;
        if (verObj instanceof Integer) return (Integer) verObj;
        if (verObj instanceof Number) return ((Number) verObj).intValue();
        try {
            return Integer.parseInt(verObj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
