package yuseteam.mealticketsystemwas.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseteam.mealticketsystemwas.domain.auth.dto.InitialSetupRequest;
import yuseteam.mealticketsystemwas.domain.auth.jwt.JwtTokenService;
import yuseteam.mealticketsystemwas.domain.auth.entity.User;
import yuseteam.mealticketsystemwas.domain.auth.repository.UserRepository;
import yuseteam.mealticketsystemwas.domain.auth.util.BearerTokenExtractor;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OnboardingService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public OnboardingService(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public Map<String, Object> initialSetup(InitialSetupRequest req, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();

        String token = BearerTokenExtractor.extract(request.getHeader("Authorization"));
        if (token == null || token.isBlank()) {
            body.put("error", "Authorization 헤더가 없습니다.");
            body.put("status", 401);
            return body;
        }

        try {
            if (jwtTokenService.isExpired(token)) {
                body.put("error", "JWT가 만료되었습니다.");
                body.put("status", 401);
                return body;
            }
        } catch (Exception e) {
            body.put("error", "유효하지 않은 토큰입니다.");
            body.put("status", 401);
            return body;
        }

        String type = jwtTokenService.parseType(token);
        if (!"signup".equals(type)) {
            body.put("error", "가입 진행용 토큰(type=signup)만 초기 설정이 가능합니다.");
            body.put("status", 403);
            return body;
        }

        Long userId = jwtTokenService.parseUserId(token);
        if (userId == null) {
            body.put("error", "유효하지 않은 토큰입니다.");
            body.put("status", 401);
            return body;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            body.put("error", "유저를 찾을 수 없습니다: " + userId);
            body.put("status", 400);
            return body;
        }

        if (user.getRole() != null && user.getPhone() != null) {
            body.put("error", "이미 초기 설정이 완료된 사용자입니다.");
            body.put("status", 400);
            body.put("role", user.getRole().name());
            body.put("phone", user.getPhone());
            return body;
        }

        if (req.getRole() == null) {
            body.put("error", "role 은 필수입니다.");
            body.put("status", 400);
            return body;
        }

        if (req.getPhone() == null || !req.getPhone().matches("\\d{11}")) {
            body.put("error", "전화번호는 숫자만 입력 가능하며, 11자리여야 합니다.");
            body.put("status", 400);
            return body;
        }

        if (userRepository.existsByPhone(req.getPhone())) {
            body.put("error", "이미 등록된 전화번호입니다.");
            body.put("status", 409);
            return body;
        }

        user.setRole(req.getRole());
        user.setPhone(req.getPhone());
        userRepository.save(user);

        int tokenVersion = user.getTokenVersion() == null ? 0 : user.getTokenVersion();
        String accessToken = jwtTokenService.issueAccessToken(user.getId(), user.getRole().name(), tokenVersion);

        body.put("message", "initial setup complete");
        body.put("id", user.getId());
        body.put("role", user.getRole().name());
        body.put("phone", user.getPhone());
        body.put("accessToken", accessToken);
        body.put("status", 200);
        return body;
    }
}
