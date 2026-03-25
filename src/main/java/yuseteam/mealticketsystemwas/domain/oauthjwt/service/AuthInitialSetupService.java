package yuseteam.mealticketsystemwas.domain.oauthjwt.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.InitialSetupReqDTO;
import yuseteam.mealticketsystemwas.domain.oauthjwt.entity.User;
import yuseteam.mealticketsystemwas.domain.oauthjwt.jwt.JWTUtil;
import yuseteam.mealticketsystemwas.domain.oauthjwt.repository.UserRepository;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthInitialSetupService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public AuthInitialSetupService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Map<String, Object> initialSetup(InitialSetupReqDTO req,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        Map<String, Object> body = new LinkedHashMap<>();

        String token = getCookieValue(request, "Authorization");
        if (token == null || token.isBlank()) {
            body.put("error", "Authorization 쿠키가 없습니다.");
            body.put("status", 401);
            return body;
        }
        if (Boolean.TRUE.equals(jwtUtil.isExpired(token))) {
            body.put("error", "JWT가 만료되었습니다.");
            body.put("status", 401);
            return body;
        }

        Long userId = jwtUtil.parseUserId(token);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            body.put("error", "유저를 찾을 수 없습니다: " + userId);
            body.put("status", 400);
            return body;
        }

        // 이미 초기 설정이 끝난 사용자 방지
        if (user.getRole() != null || user.getPhone() != null) {
            body.put("error", "이미 초기 설정이 완료된 사용자입니다.");
            body.put("status", 400);
            body.put("role", user.getRole() == null ? null : user.getRole().name());
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
        boolean phoneExists = userRepository.existsByPhone(req.getPhone());
        if (phoneExists) {
            body.put("error", "이미 등록된 전화번호입니다.");
            body.put("status", 409);
            return body;
        }

        user.setRole(req.getRole());
        user.setPhone(req.getPhone());
        userRepository.save(user);

        long expiryMs = 1000L * 60 * 60 * 24;
        String newToken = jwtUtil.createJwt(user.getId(), user.getRole().name(), expiryMs);

        Cookie cookie = new Cookie("Authorization", newToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) (expiryMs / 1000));
        response.addCookie(cookie);

        body.put("message", "initial setup complete");
        body.put("id", user.getId());
        body.put("role", user.getRole().name());
        body.put("phone", user.getPhone());
        body.put("token", newToken);
        body.put("status", 200);
        return body;
    }

    private static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}