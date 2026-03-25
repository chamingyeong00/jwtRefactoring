package yuseteam.mealticketsystemwas.domain.oauthjwt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.UserDTO;
import yuseteam.mealticketsystemwas.domain.oauthjwt.entity.User;
import yuseteam.mealticketsystemwas.domain.oauthjwt.repository.UserRepository;
import java.io.IOException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;
import java.util.List;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    public JWTFilter(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // CORS preflight 요청(OPTIONS)은 JWT 검증 없이 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 우선 Authorization 헤더 확인 (클라이언트가 헤더 기반 인증을 사용하는 경우)
        String path = request.getRequestURI();
        // 공용 인증 엔드포인트는 필터에서 무시(로그인/회원가입 등)
        if ("/api/auth/signin".equals(path) || "/api/auth/signup".equals(path) || "/api/auth/initial-setup".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String headerAuth = request.getHeader("Authorization");
        boolean usingHeader = false;
        String tokenValue = null;
        if (headerAuth != null && !headerAuth.isBlank()) {
            if (headerAuth.startsWith("Bearer ")) {
                tokenValue = headerAuth.replace("Bearer ", "");
            } else {
                tokenValue = headerAuth;
            }
            usingHeader = true;
        }

        // 헤더가 없으면 쿠키에서 확인
        if (tokenValue == null || tokenValue.isBlank()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("Authorization".equals(cookie.getName())) {
                        tokenValue = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (tokenValue == null || tokenValue.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 또는 파싱 오류 처리: 헤더 사용이면 401, 쿠키 사용이면 쿠키 삭제 후 익명으로 진행
        try {
            if (jwtService.isExpired(tokenValue)) {
                if (usingHeader) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token has expired.");
                    return;
                } else {
                    clearAuthorizationCookie(response);
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        } catch (Exception e) {
            if (usingHeader) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token.");
                return;
            } else {
                clearAuthorizationCookie(response);
                filterChain.doFilter(request, response);
                return;
            }
        }

        Long userId;
        try {
            userId = jwtService.parseUserId(tokenValue);
        } catch (Exception e) {
            if (usingHeader) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token.");
                return;
            } else {
                clearAuthorizationCookie(response);
                filterChain.doFilter(request, response);
                return;
            }
        }

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            if (usingHeader) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("User not found.");
                return;
            } else {
                clearAuthorizationCookie(response);
                filterChain.doFilter(request, response);
                return;
            }
        }

        // tokenVersion 기반 무효화 검사
        Integer tokenVerFromToken = jwtService.parseTokenVersion(tokenValue);
        int tokenVerProvided = (tokenVerFromToken == null) ? 0 : tokenVerFromToken;
        int currentTokenVer = (user.getTokenVersion() == null) ? 0 : user.getTokenVersion();
        if (tokenVerProvided != currentTokenVer) {
            if (usingHeader) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has been revoked.");
                return;
            } else {
                clearAuthorizationCookie(response);
                filterChain.doFilter(request, response);
                return;
            }
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setRole(user.getRole());
        userDTO.setName(user.getName());
        userDTO.setSocialname(user.getSocialname());

        List<GrantedAuthority> authorities;
        if (user.getRole() != null) {
            String rawRole = user.getRole().name();
            String normalizedRole = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;
            authorities = List.of(new SimpleGrantedAuthority(normalizedRole));
        } else {
            authorities = Collections.emptyList();
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDTO, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 쿠키 재설정(유효기간 연장) — 쿠키 기반 인증일 때만 갱신
        if (!usingHeader) {
            Cookie cookie = new Cookie("Authorization", tokenValue);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60 * 24); // 24h
            response.addCookie(cookie);
        }

        filterChain.doFilter(request, response);
    }

    private void clearAuthorizationCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}