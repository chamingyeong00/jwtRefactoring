package yuseteam.mealticketsystemwas.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuseteam.mealticketsystemwas.domain.auth.service.AuthService;
import yuseteam.mealticketsystemwas.domain.auth.service.LogoutService;
import yuseteam.mealticketsystemwas.domain.auth.dto.SignInRequest;
import yuseteam.mealticketsystemwas.domain.auth.dto.SignInResponse;
import yuseteam.mealticketsystemwas.domain.auth.dto.SignUpRequest;
import yuseteam.mealticketsystemwas.domain.auth.dto.RefreshAccessRequest;
import yuseteam.mealticketsystemwas.domain.auth.util.BearerTokenExtractor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LogoutService logoutService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequest req) {
        try {
            authService.signup(req);
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "로그인", description = "아이디/비밀번호로 로그인 후 access/refresh JWT를 반환")
    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> login(@RequestBody SignInRequest req, HttpServletResponse response) {
        try {
            SignInResponse res = authService.signIn(req);
            response.setHeader("Authorization", "Bearer " + res.getAccessToken());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SignInResponse(null, null, e.getMessage(), null));
        }
    }

    @Operation(summary = "access 토큰 재발급", description = "refresh 토큰으로 access/refresh를 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<SignInResponse> refresh(@RequestBody RefreshAccessRequest req, HttpServletResponse response) {
        try {
            SignInResponse res = authService.refreshAccessToken(req == null ? null : req.getRefreshToken());
            response.setHeader("Authorization", "Bearer " + res.getAccessToken());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SignInResponse(null, null, e.getMessage(), null));
        }
    }

    @Operation(summary = "로그아웃", description = "Authorization 헤더의 access JWT를 무효화(tokenVersion 증가)")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String token = BearerTokenExtractor.extract(authorizationHeader);
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }

        try {
            logoutService.logoutByAccessToken(token);
            return ResponseEntity.ok("로그아웃 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }
    }
}
