package yuseteam.mealticketsystemwas.domain.auth.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import yuseteam.mealticketsystemwas.domain.auth.jwt.JwtTokenService;
import yuseteam.mealticketsystemwas.domain.auth.entity.User;
import yuseteam.mealticketsystemwas.domain.auth.repository.UserRepository;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(JwtTokenService jwtTokenService, UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        Object principal = authentication.getPrincipal();
        Long userId;

        if (principal instanceof OAuthPrincipal oauthPrincipal) {
            userId = oauthPrincipal.getUserId();
        } else {
            try {
                userId = (Long) principal.getClass().getMethod("getId").invoke(principal);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("소셜 로그인 사용자 정보가 올바르지 않습니다.");
                return;
            }
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("사용자를 찾을 수 없습니다.");
            return;
        }

        int tokenVersion = user.getTokenVersion() == null ? 0 : user.getTokenVersion();
        boolean needInitialSetup = user.getRole() == null || user.getPhone() == null || user.getPhone().isBlank();

        if (needInitialSetup) {
            String signupToken = jwtTokenService.issueSignupToken(user.getId(), tokenVersion);
            response.setHeader("Authorization", "Bearer " + signupToken);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"type\":\"signup\",\"token\":\"" + signupToken + "\"}");
            return;
        }

        String roleName = user.getRole().name();
        String accessToken = jwtTokenService.issueAccessToken(user.getId(), roleName, tokenVersion);
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"type\":\"access\",\"token\":\"" + accessToken + "\"}");
    }
}
