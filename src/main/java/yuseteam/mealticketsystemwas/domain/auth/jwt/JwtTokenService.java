package yuseteam.mealticketsystemwas.domain.auth.jwt;

import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtTokenService(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String issueAccessToken(Long userId, String roleName, Integer tokenVersion) {
        return jwtTokenUtil.createJwt(
                userId,
                roleName,
                AuthTokenType.ACCESS.name().toLowerCase(),
                24L * 60 * 60 * 1000,
                tokenVersion
        );
    }

    public String issueRefreshToken(Long userId, String roleName, Integer tokenVersion) {
        return jwtTokenUtil.createJwt(
                userId,
                roleName,
                AuthTokenType.REFRESH.name().toLowerCase(),
                14L * 24 * 60 * 60 * 1000,
                tokenVersion
        );
    }

    public String issueSignupToken(Long userId, Integer tokenVersion) {
        return jwtTokenUtil.createJwt(
                userId,
                null,
                AuthTokenType.SIGNUP.name().toLowerCase(),
                15L * 60 * 1000,
                tokenVersion
        );
    }

    public boolean isExpired(String token) {
        return jwtTokenUtil.isExpired(token);
    }

    public Long parseUserId(String token) {
        return jwtTokenUtil.parseUserId(token);
    }

    public String parseType(String token) {
        return jwtTokenUtil.parseType(token);
    }

    public Integer parseTokenVersion(String token) {
        return jwtTokenUtil.parseTokenVersion(token);
    }
}
