package yuseteam.mealticketsystemwas.domain.oauthjwt.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.CustomOAuth2UserDTO;
import yuseteam.mealticketsystemwas.domain.oauthjwt.jwt.JWTService;
import yuseteam.mealticketsystemwas.domain.oauthjwt.repository.UserRepository;
import yuseteam.mealticketsystemwas.domain.oauthjwt.entity.User;

import java.io.IOException;
import java.util.Objects;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    public CustomSuccessHandler(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2UserDTO principal = (CustomOAuth2UserDTO) authentication.getPrincipal();

        Long userId = principal.getId();
        String roleName = principal.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .orElse(null);

        User user = userRepository.findById(userId).orElse(null);
        Integer tokenVersion = (user == null || user.getTokenVersion() == null) ? 0 : user.getTokenVersion();

        String token = jwtService.createToken(userId, roleName, tokenVersion);
        ResponseCookie responseCookie= ResponseCookie.from("Authorization", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", responseCookie.toString());

        if (user != null && user.getPhone() != null) {
            String redirectUrl = "https://www.yummypass.r-e.kr/ticket-purchase";
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            return;
        }

        String redirectUrl = "https://www.yummypass.r-e.kr/social-signup-phone";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}