package yuseteam.mealticketsystemwas.domain.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import yuseteam.mealticketsystemwas.domain.auth.entity.User;
import yuseteam.mealticketsystemwas.domain.auth.repository.UserRepository;
import yuseteam.mealticketsystemwas.domain.auth.util.BearerTokenExtractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = BearerTokenExtractor.extract(request.getHeader("Authorization"));
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtTokenService.isExpired(token)) {
                writeUnauthorized(response, "TOKEN_EXPIRED", "토큰이 만료되었습니다.");
                return;
            }
        } catch (Exception e) {
            writeUnauthorized(response, "TOKEN_INVALID", "유효하지 않은 토큰입니다.");
            return;
        }

        String type = jwtTokenService.parseType(token);
        if (AuthTokenType.SIGNUP.name().toLowerCase().equals(type)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"code\":\"SIGNUP_TOKEN_FORBIDDEN\",\"message\":\"회원가입 토큰으로는 이 자원에 접근할 수 없습니다.\"}");
            return;
        }
        if (AuthTokenType.REFRESH.name().toLowerCase().equals(type)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"code\":\"REFRESH_TOKEN_FORBIDDEN\",\"message\":\"refresh 토큰으로는 이 자원에 접근할 수 없습니다.\"}");
            return;
        }

        Long userId = jwtTokenService.parseUserId(token);
        if (userId == null) {
            writeUnauthorized(response, "TOKEN_INVALID", "유효하지 않은 토큰입니다.");
            return;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            writeUnauthorized(response, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다.");
            return;
        }

        Integer tokenVerFromToken = jwtTokenService.parseTokenVersion(token);
        int tokenVerProvided = (tokenVerFromToken == null) ? 0 : tokenVerFromToken;
        int currentTokenVer = (user.getTokenVersion() == null) ? 0 : user.getTokenVersion();
        if (tokenVerProvided != currentTokenVer) {
            writeUnauthorized(response, "TOKEN_REVOKED", "토큰이 무효화되었습니다. (로그아웃)");
            return;
        }

        List<GrantedAuthority> authorities;
        if (user.getRole() != null) {
            String rawRole = user.getRole().name();
            String normalizedRole = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;
            authorities = List.of(new SimpleGrantedAuthority(normalizedRole));
        } else {
            authorities = Collections.emptyList();
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user.getId(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }
}
