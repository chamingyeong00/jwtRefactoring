package yuseteam.mealticketsystemwas.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseteam.mealticketsystemwas.domain.auth.jwt.JwtTokenService;
import yuseteam.mealticketsystemwas.domain.auth.entity.User;
import yuseteam.mealticketsystemwas.domain.auth.repository.UserRepository;

@Service
public class LogoutService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public LogoutService(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public void logoutByAccessToken(String token) {
        if (jwtTokenService.isExpired(token)) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }
        Long userId = jwtTokenService.parseUserId(token);
        if (userId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        int current = user.getTokenVersion() == null ? 0 : user.getTokenVersion();
        user.setTokenVersion(current + 1);
        userRepository.save(user);
    }
}
