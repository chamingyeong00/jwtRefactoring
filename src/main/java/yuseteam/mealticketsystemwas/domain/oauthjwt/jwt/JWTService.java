package yuseteam.mealticketsystemwas.domain.oauthjwt.jwt;

import org.springframework.stereotype.Service;

@Service
public class JWTService {

    private final JWTUtil jwtUtil;

    public JWTService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String createToken(Long userId, String roleName, Integer tokenVersion) {
        return jwtUtil.createJwt(userId, roleName, 24L * 60 * 60 * 1000, tokenVersion); // 24h
    }

    public Boolean isExpired(String token) {
        return jwtUtil.isExpired(token);
    }

    public Long parseUserId(String token) { return jwtUtil.parseUserId(token); }

    public Integer parseTokenVersion(String token) { return jwtUtil.parseTokenVersion(token); }

}