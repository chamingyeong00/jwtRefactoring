package yuseteam.mealticketsystemwas.domain.oauthjwt.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(Long userId, String role, Long expiredMs, Integer tokenVersion) {
        String jti = UUID.randomUUID().toString();

        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .claim("jti", jti)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs));

        if (tokenVersion != null) {
            builder.claim("ver", tokenVersion);
        }

        return builder.signWith(secretKey)
                .compact();
    }

    public String createJwt(Long userId, String role, Long expiredMs) {
        return createJwt(userId, role, expiredMs, null);
    }

    public Long parseUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("userId", Number.class).longValue();
    }

    public Integer parseTokenVersion(String token) {
        Object verObj = Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("ver");
        if (verObj == null) return null;
        if (verObj instanceof Integer) return (Integer) verObj;
        if (verObj instanceof Number) return ((Number) verObj).intValue();
        try {
            return Integer.parseInt(verObj.toString());
        } catch (Exception e) {
            return null;
        }
    }

}
